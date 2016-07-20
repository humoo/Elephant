package com.example.elephant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.elephant.adpter.CommentItemAdapter;
import com.example.elephant.db.BookComment;
import com.example.elephant.db.MainContent;
import com.example.elephant.utils.FlushBookCommentThread;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainComment extends Activity implements OnClickListener {

	ImageView back,openInfo;
	EditText ed_myCommentTxt;
	TextView tv_sendTxt;
	int viewCount;
	String objectId;
	SharedPreferences sp;
	MainContent mainContent;
	static List<Map<String, String>> list;
	Map<String, String> map;
	Intent intent;

	public static PullToRefreshListView listView;
	static CommentItemAdapter adapter;
	FlushBookCommentThread thread;
	LinearLayout ll_comment_head;
	Bundle bundle;
	String floorNum="";//����¥��
	String myFloorNum="";
	int upRefreshCount=0;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==0x1111) {
				adapter.notifyDataSetChanged();
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_info);

		init();// ��ʼ��
		listView.setRefreshing(true);
		getDataFromPreActivity();
		flushCommentList();

		//��̬����ˢ�����۳�ʼ��
		refreshInit();
		

		back = (ImageView) findViewById(R.id.iv_back_title_back);
		openInfo=(ImageView) findViewById(R.id.item_img_lv_item_ui);
		back.setOnClickListener(this);
		tv_sendTxt.setOnClickListener(this);
		
	}
	
	/**
	 * ����һ��activity��ȡ����
	 */
	private void getDataFromPreActivity() {
		
		

		viewCount = Integer.parseInt(bundle.getString("viewcount"));// �û������
		objectId = bundle.getString("objectId");// ��ȡobjectId

		
		map=new HashMap<>();
		map.put("username", bundle.getString("username"));
		map.put("significance", bundle.getString("significance"));
		map.put("usercontent", bundle.getString("usercontent"));
		map.put("createdAt", bundle.getString("createdAt"));
		map.put("school", bundle.getString("school"));
		map.put("bookname", bundle.getString("bookname"));
		map.put("bookauthor", bundle.getString("bookauthor"));
		map.put("bookisbn", bundle.getString("bookisbn"));
		map.put("bookpublisher", bundle.getString("bookpublisher"));

		list.add(0,map);
		adapter.setListData(list);
		listView.setAdapter(adapter);

		mainContent = new MainContent();
		mainContent.setViewcount(viewCount + "");


	}

	/**
	 * ��ʼ���ؼ���Ϊ�ؼ���ֵ
	 */
	private void init() {

		list=new ArrayList<>();

		sp=getSharedPreferences("config",MODE_PRIVATE);
		
		listView=(PullToRefreshListView) findViewById(R.id.lv_liaotie_comment);
		adapter = new CommentItemAdapter(this,handler);

		ll_comment_head=(LinearLayout) findViewById(R.id.ll_comment_head);//��ȡͷ��linearlayout

		ed_myCommentTxt = (EditText) findViewById(R.id.et_mycomment_txt);// ���ۿ�����
		tv_sendTxt = (TextView) findViewById(R.id.tv_send_comment_txt);// ��������
		
		// ���ؼ��������
		intent = getIntent();
		bundle = intent.getExtras().getBundle("itemdata");
		
	}

	/**
	 * ��������
	 */
	private void sendTxt() {
		//�ж��Ƿ��ѵ�½
		if (sp.getBoolean("islogin", false)) {
			if (!ed_myCommentTxt.getText().toString().trim().equals("")) {
				final BookComment bookComment=new BookComment();
				
				//ȡ�ñ༭���������
				final String myComment=ed_myCommentTxt.getText().toString();
				bookComment.setComment(myComment);//������������
				ed_myCommentTxt.setText("");
				bookComment.setParentobjectId(objectId);//���ø���objectId
				bookComment.setUsername(sp.getString("accountname",""));//���õ�ǰ�����û���
				bookComment.setSignificance(sp.getString("significance",""));//���õ�ǰ�������û�ǩ��
				bookComment.setSchool(sp.getString("school",""));
				
				
				//��ѯ�������Ѵ���������			
				BmobQuery query1=new BmobQuery("BookComment");
				query1.addWhereEqualTo("parentobjectId",bookComment.getParentobjectId());
				query1.setLimit(1000);//�������1000������
				query1.findObjects(MainComment.this, new FindCallback() {				
					@Override
					public void onFailure(int arg0, String arg1) {						
					}
					
					@Override
					public void onSuccess(JSONArray arg0) {
						//�������ۼ���
						MainContent mainContent=new MainContent();
						floorNum=arg0.length()+1+"";//
						
						//��ѯ������¥����ߵ���
						try {
							org.json.JSONObject iJsonObject=arg0.getJSONObject(arg0.length()-1);						
							myFloorNum=iJsonObject.getString("floorNum");
							myFloorNum=(Integer.parseInt(myFloorNum)+1)+"";
							
						} catch (JSONException e) {
							e.printStackTrace();
							myFloorNum=1+"";//�쳣˵����ǰΪ��һ����¼
						}
						mainContent.setCommentcount(floorNum+"");
						mainContent.update(MainComment.this,bookComment.getParentobjectId() , new UpdateListener() {	
							@Override
							public void onSuccess() {
								Log.d("MainComment", "���ۼ�����ӳɹ�");
								
								//�����ɹ���׷������
	
								bookComment.setFloorNum(myFloorNum);
								bookComment.save(MainComment.this,new SaveListener() {				
									@Override
									public void onSuccess() {
										
										Toast.makeText(MainComment.this,"���۳ɹ�",1).show();	
//										ed_myCommentTxt.setText("");
										
										//������Ϣ�������ݸı�
										
										flushCommentList();

									}				
									@Override
									public void onFailure(int arg0, String arg1) {
										Toast.makeText(MainComment.this,"����ʧ��",1).show();								
									}
								});	
								
							}					
							@Override
							public void onFailure(int arg0, String arg1) {	
								Log.d("MainComment", "���ۼ������ʧ��");

							}
						});
						
					}
				});
			
				
			}
					
			
		}else {
			Toast.makeText(MainComment.this, "���¼���ٲ���", 1).show();
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back_title_back:			
			mainContent.update(this,objectId,new UpdateListener() {				
				@Override
				public void onSuccess() {}		
				@Override
				public void onFailure(int arg0, String arg1){}
			});
			
			finish();
			break;

		case R.id.tv_send_comment_txt:
			sendTxt();
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		mainContent.update(this,objectId,new UpdateListener() {				
			@Override
			public void onSuccess() {}		
			@Override
			public void onFailure(int arg0, String arg1){}
		});
		super.onBackPressed();
	}


	/**
	 * ���ͷ��򿪵ĸ�����ϸҳ��
	 */
	public void openMoreInfo(View v) {
		Intent intent =new Intent(MainComment.this,InfoIconActivity.class);
		intent.putExtra("significance",bundle.getString("significance"));
		intent.putExtra("username",bundle.getString("username"));
		startActivity(intent);	
	}
	
	/**
	 * ɾ�����ۣ�����������
	 * @param context
	 * @param position
	 */
	public static void deleteListViewItem(final Context context, int position) {
		Map map=list.get(position);
		BookComment bookComment=new BookComment();
		bookComment.setObjectId(map.get("objectId").toString());
		bookComment.delete(context, new DeleteListener() {		
			@Override
			public void onSuccess() {
				Toast.makeText(context, "ɾ���ɹ�", 1).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				
			}
		});
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	
	}
	
	/**
	 * ����ˢ�����۳�ʼ��
	 */
	public void refreshInit() {

		listView.setMode(Mode.BOTH);

		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {

						if (refreshView.isHeaderShown()) {
							String str = DateUtils.formatDateTime(
									MainComment.this,
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							// ����ˢ�±�ǩ
							listView.getLoadingLayoutProxy()
									.setRefreshingLabel("����ˢ��");
							// ����������ǩ
							listView.getLoadingLayoutProxy().setPullLabel(
									"����ˢ��");
							// �����ͷű�ǩ
							listView.getLoadingLayoutProxy()
									.setReleaseLabel("�ͷſ�ʼˢ��");
							// ������һ��ˢ�µ���ʾ��ǩ
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("������ʱ��:" + str);
							// �������ݲ���
							 upRefreshCount=0;
							 flushCommentList();
							
						}
						if (refreshView.isFooterShown()) {
							String str = DateUtils.formatDateTime(
									MainComment.this,
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							// ����ˢ�±�ǩ
							listView.getLoadingLayoutProxy()
									.setRefreshingLabel("���ڼ���");
							// ����������ǩ
							listView.getLoadingLayoutProxy().setPullLabel(
									"���ظ���");
							// �����ͷű�ǩ
							listView.getLoadingLayoutProxy()
									.setReleaseLabel("�ͷſ�ʼˢ��");
							// ������һ��ˢ�µ���ʾ��ǩ
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("������ʱ��:" + str);
							// �������ݲ���
							 upRefreshCount=upRefreshCount+1;
							 flushCommentListAtTheEnd();

						}

					}
				});
		
		
	
	}
	
	
	/**
	 * ˢ�������б�
	 */
	private void flushCommentList() {
		upRefreshCount=0;
		
		Log.d("logd", "����flush");

		BmobQuery query=new BmobQuery("BookComment");
		query.addWhereEqualTo("parentobjectId", objectId);
		query.setLimit(5);
		query.order("-createdAt");
		query.findObjects(MainComment.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();

				Toast.makeText(MainComment.this, "��ȡ�����б�ʧ��", 1).show();				
			}
			
			@Override
			public void onSuccess(JSONArray arg0) {
				try {
					list.clear();
					getDataFromPreActivity();

					Log.d("logd", "����onSuccess");

					for (int j = 0; j < arg0.length(); j++) {

						JSONObject itemJsonObject = arg0.getJSONObject(j);
						String comment=itemJsonObject.getString("comment");
						String username=itemJsonObject.getString("username");
						String significance=itemJsonObject.getString("significance");
						String createdAt=itemJsonObject.getString("createdAt");
						String floorNum=itemJsonObject.getString("floorNum");
						String objectId=itemJsonObject.getString("objectId");
						String school=itemJsonObject.getString("school");
				
						map=new HashMap<String, String>();
						map.put("usercontent", comment);
						map.put("username", username);
						map.put("significance", significance);
						map.put("createdAt", createdAt);
						map.put("floorNum", floorNum);
						map.put("objectId", objectId);
						map.put("school", school);
						list.add(map);//���µ���Ϣ����shang��	

					}
									
		           
					listView.onRefreshComplete();
					adapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("logd", "�������");
					listView.onRefreshComplete();

				}
				
			}
		});
	
		
	}
	
	/**
	 * ��������
	 */
	private void flushCommentListAtTheEnd() {		
		BmobQuery query=new BmobQuery("BookComment");
		query.addWhereEqualTo("parentobjectId", objectId );
		query.setLimit(5);
		query.order("-createdAt");
		query.setSkip(5*upRefreshCount);//������ˢ�µ�����
		query.findObjects(MainComment.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();
				Toast.makeText(MainComment.this, "��ȡ�����б�ʧ��", 1).show();				
			}
			
			@Override
			public void onSuccess(JSONArray arg0) {
				try {

					if (arg0.length()>0) {
						for (int j = 0; j < arg0.length(); j++) {

							org.json.JSONObject itemJsonObject = arg0.getJSONObject(j);
							String comment=itemJsonObject.getString("comment");
							String username=itemJsonObject.getString("username");
							String significance=itemJsonObject.getString("significance");
							String createdAt=itemJsonObject.getString("createdAt");
							String floorNum=itemJsonObject.getString("floorNum");
							String objectId=itemJsonObject.getString("objectId");
							String school=itemJsonObject.getString("school");
					
							map=new HashMap<String, String>();
							map.put("usercontent", comment);
							map.put("username", username);
							map.put("significance", significance);
							map.put("createdAt", createdAt);
							map.put("floorNum", floorNum);
							map.put("objectId", objectId);
							map.put("school", school);
							list.add(map);//���µ���Ϣ����shang��			
						}
					}else {						
						Toast.makeText(MainComment.this, "�Ѿ�������", 1).show();					
					}           
					listView.onRefreshComplete();
					adapter.notifyDataSetChanged();
					//������һ��itemλ�ò���
					listView.getRefreshableView().setSelectionFromTop(5*upRefreshCount-1, ComponentCallbacks2.TRIM_MEMORY_BACKGROUND);

				} catch (JSONException e) {
					e.printStackTrace();
					listView.onRefreshComplete();

				}
				
			}
		});
	}
	
}
