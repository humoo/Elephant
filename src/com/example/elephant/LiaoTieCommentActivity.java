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
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.elephant.adpter.LiaoTieCommentAdapter;
import com.example.elephant.db.LiaoTie;
import com.example.elephant.db.LiaoTieComment;
import com.example.elephant.utils.OpenSharePrefrenceHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LiaoTieCommentActivity extends Activity{

	
	PullToRefreshListView listView;
	static LiaoTieCommentAdapter adapter;
	static List<Map<String, String>> list;//�����б�
	Map<String, String> map;
	ImageView back;
	TextView send;
	EditText et_mycomment_txt;
	Intent intent;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	String floorNum="";//����¥��
	String myFloorNum="";
	int upRefreshCount=0;//����ˢ�´���
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==0x1111) {
				adapter.notifyDataSetChanged();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liaotie_comment_activity);
		
		init();
		
		/**
		 * ���û�������������
		 */
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isHeaderShown()) {
					String str = DateUtils.formatDateTime(LiaoTieCommentActivity.this,
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);
					// ����ˢ�±�ǩ
					listView.getLoadingLayoutProxy().setRefreshingLabel("����ˢ��");
					// ����������ǩ
					listView.getLoadingLayoutProxy().setPullLabel("����ˢ��");
					// �����ͷű�ǩ
					listView.getLoadingLayoutProxy().setReleaseLabel("�ͷſ�ʼˢ��");
					// ������һ��ˢ�µ���ʾ��ǩ
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"������ʱ��:" + str);
					// �������ݲ���
					flushCommentList();
				}
				if (refreshView.isFooterShown()) {
					String str = DateUtils.formatDateTime(LiaoTieCommentActivity.this,
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);
					// ����ˢ�±�ǩ
					listView.getLoadingLayoutProxy().setRefreshingLabel("���ڼ���");
					// ����������ǩ
					listView.getLoadingLayoutProxy().setPullLabel("���ظ���");
					// �����ͷű�ǩ
					listView.getLoadingLayoutProxy().setReleaseLabel("�ͷſ�ʼˢ��");
					// ������һ��ˢ�µ���ʾ��ǩ
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"������ʱ��:" + str);
					// �������ݲ���
					upRefreshCount=upRefreshCount+1;
					flushCommentListAtTheEnd();
					
				}
			}

			
		});
		
		/**
		 * ����ϸ�activity������������
		 */
		getDataFromPreActivity();
		/**
		 * ˢ�������б�
		 */
		flushCommentList();
		//���·���
		back.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//��������
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("logd","�������");
				sendComment();
				
			}

			
		});
	}

	
	/**
	 * ˢ�������б�
	 */
	private void flushCommentList() {
		upRefreshCount=0;

		BmobQuery query=new BmobQuery("LiaoTieComment");
		query.addWhereEqualTo("parentobjectId", intent.getStringExtra("parentobjectId"));
		query.setLimit(5);
		query.order("-createdAt");
		query.findObjects(LiaoTieCommentActivity.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();

				Toast.makeText(LiaoTieCommentActivity.this, "��ȡ�����б�ʧ��", 1).show();				
			}
			
			@Override
			public void onSuccess(JSONArray arg0) {
				try {
					list.clear();
					getDataFromPreActivity();

					
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

					listView.onRefreshComplete();

				}
				
			}
		});
	
		
	}
	
	/**
	 * ��������
	 */
	private void flushCommentListAtTheEnd() {		
		BmobQuery query=new BmobQuery("LiaoTieComment");
		query.addWhereEqualTo("parentobjectId", intent.getStringExtra("parentobjectId"));
		query.setLimit(5);
		query.order("-createdAt");
		query.setSkip(5*upRefreshCount);//������ˢ�µ�����
		query.findObjects(LiaoTieCommentActivity.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();

				Toast.makeText(LiaoTieCommentActivity.this, "��ȡ�����б�ʧ��", 1).show();				
			}
			
			@Override
			public void onSuccess(JSONArray arg0) {
				try {

					if (arg0.length()>0) {
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
					}else {						
						Toast.makeText(LiaoTieCommentActivity.this, "�Ѿ�������", 1).show();					
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

	/**
	 * ����ϸ�activity�����������ݲ�����adapter
	 */
	private void getDataFromPreActivity() {
			
		map=new HashMap<>();
		map.put("username", intent.getStringExtra("username"));
		map.put("usercontent", intent.getStringExtra("usercontent"));
		map.put("significance", intent.getStringExtra("significance"));
		map.put("zanCount", intent.getStringExtra("zanCount"));
		map.put("createdAt", intent.getStringExtra("createdAt"));
		map.put("school", intent.getStringExtra("school"));
		list.add(0,map);
		adapter.setListData(list);
		listView.setAdapter(adapter);

	}
	
	/**
	 * ��������
	 */
	private void sendComment() {
		

		final String myComment=et_mycomment_txt.getText().toString();
		if (!myComment.trim().equals("")) {
			final LiaoTieComment liaoTieComment=new LiaoTieComment();
			liaoTieComment.setComment(myComment);
			liaoTieComment.setSchool(openSharePrefrenceHelper.getSP_CONFIG().getString("school", ""));
			et_mycomment_txt.setText("");
			liaoTieComment.setUsername(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""));
			liaoTieComment.setParentobjectId(intent.getStringExtra("parentobjectId"));//���ø���objectId
			liaoTieComment.setSignificance(openSharePrefrenceHelper.getSP_CONFIG().getString("significance",""));//���õ�ǰ�������û�ǩ��
			
			//��ѯ�������Ѵ���������			
			BmobQuery query1=new BmobQuery("LiaoTieComment");
			query1.addWhereEqualTo("parentobjectId",liaoTieComment.getParentobjectId());
			query1.setLimit(1000);//�������1000������
			query1.findObjects(LiaoTieCommentActivity.this, new FindCallback() {				
				@Override
				public void onFailure(int arg0, String arg1) {						
				}
				
				@Override
				public void onSuccess(JSONArray arg0) {
					//�������ۼ���
					//if (arg0.length()>0) {
						LiaoTie liaoTie=new LiaoTie();
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
						liaoTie.setCommentCount(floorNum+"");
						liaoTie.update(LiaoTieCommentActivity.this,liaoTieComment.getParentobjectId() , new UpdateListener() {	
							@Override
							public void onSuccess() {
								Log.d("MainComment", "���ۼ�����ӳɹ�");
								
								//�����ɹ���׷������

								liaoTieComment.setFloorNum(myFloorNum);
								liaoTieComment.save(LiaoTieCommentActivity.this,new SaveListener() {				
									@Override
									public void onSuccess() {
										Toast.makeText(LiaoTieCommentActivity.this,"���۳ɹ�",1).show();											
										//������Ϣ�������ݸı�										
										Map<String,String> map = new HashMap();
										map.put("usercontent", myComment);
										map.put("username",openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""));
										map.put("significance",openSharePrefrenceHelper.getSP_CONFIG().getString("significance",""));
										map.put("floorNum", myFloorNum);
										map.put("school", openSharePrefrenceHelper.getSP_CONFIG().getString("school", ""));
										list.add(1,map);//���µ���Ϣ����shang��
										//ˢ������
//										adapter.notifyDataSetChanged();
										//���۳ɹ�Ӧ������ˢ������
										flushCommentList();
										
									}				
									@Override
									public void onFailure(int arg0, String arg1) {
										Toast.makeText(LiaoTieCommentActivity.this,"����ʧ��",1).show();								
									}
								});	
							}					
							@Override
							public void onFailure(int arg0, String arg1) {	
								Log.d("logd", "���ۼ������ʧ��");
							}
						});
					}					
			});
						
		}else {
			Toast.makeText(LiaoTieCommentActivity.this,"�㻹û��������", 1).show();
		}
		
		
		
		
	}

	private void init() {
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(this);
		intent=getIntent();
		list=new ArrayList<>();
		listView = (PullToRefreshListView) findViewById(R.id.lv_liaotie_comment);
		adapter=new LiaoTieCommentAdapter(this,handler);
		back=(ImageView) findViewById(R.id.iv_back_title);
		send=(TextView) findViewById(R.id.tv_send_comment_txt);
		et_mycomment_txt=(EditText) findViewById(R.id.et_mycomment_txt);
		
	}

	/**
	 * ɾ������
	 * @param context
	 * @param position
	 */
	public static void deleteListViewItem(final Context context, int position) {
		Map map=list.get(position);
		LiaoTieComment liaoTieComment=new LiaoTieComment();
		liaoTieComment.setObjectId(map.get("objectId").toString());
		liaoTieComment.delete(context, new DeleteListener() {			
			@Override
			public void onSuccess() {	
				Toast.makeText(context, "ɾ���ɹ�",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "ɾ��ʧ��", 1).show();
			}
		});
		
		/**
		 * �������ۼ��������ﲻ������
		 */
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
}
