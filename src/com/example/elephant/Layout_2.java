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
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.adpter.Layout2MianListAdapter;
import com.example.elephant.db.LiaoTie;
import com.example.elephant.db.LiaoTieComment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Layout_2 extends Activity {

	static PullToRefreshListView listView;
	static Layout2MianListAdapter adapter;
	static List<Map<String,String>> list;//�ӷ�������ȡ�ĵ�����
	static Map<String, String> map;
	static boolean idFirstIn=true;//�Ƿ��һ�ν����ҳ��
	DataFromWeb dataFromWeb;
	static Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==0x001) {
				listView.setAdapter(adapter);			
			}
			if (msg.what==0x002) {
				adapter.notifyDataSetChanged();	
				//������һ��itemλ�ò���
				listView.getRefreshableView().setSelectionFromTop(5*upRefreshCount-1, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);

			}
			if (msg.what==0x1111) {
				adapter.notifyDataSetChanged();
			}
			
			
		};
	};
	static int upRefreshCount=0;//�����������ݴ���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_2);

		init();// �ؼ���ʼ��

		/*
		 * ����PullToRefreshˢ��ģʽ BOTH:����ˢ�º�����ˢ�¶�֧�� DISABLED��������������ˢ��
		 * PULL_FROM_START:��֧������ˢ�£�Ĭ�ϣ� PULL_FROM_END����֧������ˢ��
		 * MANUAL_REFRESH_ONLY��ֻ�����ֶ�����
		 */
		listView.setMode(Mode.BOTH);

		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (refreshView.isHeaderShown()) {
					String str = DateUtils.formatDateTime(Layout_2.this,
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
					upRefreshCount=0;
					dataFromWeb.flushDataFromWeb();
				}
				if (refreshView.isFooterShown()) {
					String str = DateUtils.formatDateTime(Layout_2.this,
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
					dataFromWeb.refreshDataAtTheEnd();
					
				}
				
				
			}

		});
		//item�����
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				map=list.get(position-1);//����������ˢ�£��ű�������1�������1
				Intent intent=new Intent(Layout_2.this,LiaoTieCommentActivity.class);//���������۽���

				intent.putExtra("username", map.get("username"));
				intent.putExtra("usercontent", map.get("usercontent"));
				intent.putExtra("significance", map.get("significance"));
				intent.putExtra("zanCount", map.get("zanCount"));
				intent.putExtra("createdAt", map.get("createdAt"));
				intent.putExtra("school", map.get("school"));
				intent.putExtra("parentobjectId", map.get("parentobjectId"));
				
				startActivity(intent);
				
			}
		});
		
	}

	private void init() {
		listView = (PullToRefreshListView) findViewById(R.id.lv_liaotie_main);
		list=new ArrayList<>();
		//ֻ����һ�����󣬽�ʡ�ڴ�ռ�
		adapter = new Layout2MianListAdapter(this,handler);	
		dataFromWeb=new DataFromWeb(Layout_2.this);
		dataFromWeb.flushDataFromWeb();

	}	

	
	/**
	 * MianUIAdapter����ɾ��listview item
	 * ��������������
	 * @param bigPicPathString 
	 * @param thumbPicPathString 
	 */
	public static void deleteListViewItem(final Context context,int position, String thumbPicPathString, String bigPicPathString) {
		Map map=list.get(position);
		LiaoTie liaoTie=new LiaoTie();
		liaoTie.setObjectId(map.get("parentobjectId").toString());//����item��objectId
		liaoTie.delete(context, new DeleteListener() {			
			@Override
			public void onSuccess() {	
				Toast.makeText(context, "ɾ���ɹ�",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "ɾ��ʧ��", 1).show();
			}
		});
		
		BmobQuery query=new BmobQuery("LiaoTieComment");
		query.addWhereEqualTo("parentobjectId", map.get("parentobjectId").toString());
		query.findObjects(context, new FindCallback() {		
			@Override
			public void onFailure(int arg0, String arg1) {				
			}		
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				LiaoTieComment liaoTieComment=new LiaoTieComment();
				if (arg0.length()>0) {
					for (int i = 0; i < arg0.length(); i++) {
						try {
							JSONObject iJsonObject=arg0.getJSONObject(i);
							liaoTieComment.setObjectId(iJsonObject.get("objectId").toString());
							liaoTieComment.delete(context, new DeleteListener() {						
								@Override
								public void onSuccess() {									
								}
								@Override
								public void onFailure(int arg0, String arg1) {									
								}
							});
							
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
				
			}
		});
		
		//ɾ���ļ�
		String newUrlString=bigPicPathString+","+thumbPicPathString;		
		String[] urls =newUrlString.split(",");
		
		BmobFile.deleteBatch(context, urls, new DeleteBatchListener() {

		    @Override
		    public void done(String[] failUrls, BmobException e) {
		        if(e==null){
//		        	Toast.makeText(context,"ȫ��",1).show();;
		        }else{
		            if(failUrls!=null){
//		                Toast.makeText(context,"ɾ��ʧ�ܸ�����"+failUrls.length+","+e.toString(),1).show();;
		            }else{
//		            	Toast.makeText(context,"ȫ���ļ�ɾ��ʧ�ܣ�"+e.getErrorCode()+","+e.toString(),1).show();
		            }
		        }
		    }

			
		});
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		upRefreshCount=0;//��֪Ϊʲô��������˲����㣬����ǿ������
		super.onBackPressed();
	}
	
	
}

/**
 * �ӷ�������ȡ������
 * @author Administrator
 *
 */
class DataFromWeb{
	
	Context context;
	Map<String,String> map;
	
	public DataFromWeb(Context context) {
		this.context=context;
		
	}
	
	/**
	 * �ӷ�������ȡ����
	 */
	public void flushDataFromWeb() {
		BmobQuery query = new BmobQuery("LiaoTie");// ��ѯ��������
		query.setLimit(5);//ÿ��ˢ��10������
		query.order("-createdAt");//����ʱ�䷴���������µ�����ǰ��
		query.findObjects(context, new FindCallback() {

			@Override
			public void onFailure(int arg0, String arg1) {
				Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
				Toast.makeText(context, "���粻�ã�����", 1).show();

			}

			@Override
			public void onSuccess(JSONArray arg0) {

				if (arg0.length() > 0) {
					
					try {
						if (Layout_2.list!=null) {
							Layout_2.list.clear();
							
							for (int i = 0; i < arg0.length(); i++) {

								JSONObject jsonItem = arg0.getJSONObject(i);
								
								String username=jsonItem.getString("username");
								String usercontent=jsonItem.getString("usercontent");
								String parentobjectId=jsonItem.getString("objectId");
								String createdAt=jsonItem.getString("createdAt");
								String commentCount=jsonItem.getString("commentCount");
								String zanCount=jsonItem.getString("zanCount");
								String significance=jsonItem.getString("significance");
								String school=jsonItem.getString("school");
								String zanUsers=jsonItem.getString("zanUsers");
								String pics=jsonItem.getString("pics");
								String thumbPicPathString=jsonItem.getString("thumbPicPathString");
								String bigPicPathString=jsonItem.getString("bigPicPathString");
							
								Layout_2.map=new HashMap();
								Layout_2.map.put("username", username);
								Layout_2.map.put("usercontent",usercontent);
								Layout_2.map.put("parentobjectId", parentobjectId);
								Layout_2.map.put("createdAt", createdAt);
								Layout_2.map.put("commentCount", commentCount);
								Layout_2.map.put("zanCount", zanCount);
								Layout_2.map.put("significance", significance);
								Layout_2.map.put("school", school);
								Layout_2.map.put("zanUsers", zanUsers);
								Layout_2.map.put("pics", pics);
								Layout_2.map.put("bigPicPathString", bigPicPathString);
								Layout_2.map.put("thumbPicPathString", thumbPicPathString);
								
							
								Layout_2.list.add(Layout_2.map);//���ݷ�����λ
							
							}
								Message message=new Message();
								message.what=0x001;
								Layout_2.handler.sendMessage(message);							
								Layout_2.adapter.setListData(Layout_2.list);//�������������
								Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
						}else {
//							Toast.makeText(context, "�����ɹ�", 1).show();
						}
						

						

					} catch (JSONException e) {
						e.printStackTrace();
						Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�

					}
				
				}

			}
		});
	}
	
	/**
	 * ˢ�¼�������
	 */
	public void refreshDataAtTheEnd(){
		
		BmobQuery query = new BmobQuery("LiaoTie");// ��ѯ��������
		query.setLimit(5);//ÿ��ˢ��10������		
		query.order("-createdAt");//����ʱ�䷴���������µ�����ǰ��
		query.setSkip(5*(Layout_2.upRefreshCount));
		query.findObjects(context, new FindCallback() {

			@Override
			public void onFailure(int arg0, String arg1) {
				Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
				Toast.makeText(context, "���粻�ã�����", 1).show();

			}

			@Override
			public void onSuccess(JSONArray arg0) {

				if (arg0.length() > 0) {
					
					try {
						for (int i = 0; i < arg0.length(); i++) {

							JSONObject jsonItem = arg0.getJSONObject(i);
							
							String username=jsonItem.getString("username");
							String usercontent=jsonItem.getString("usercontent");
							String parentobjectId=jsonItem.getString("objectId");
							String createdAt=jsonItem.getString("createdAt");
							String commentCount=jsonItem.getString("commentCount");
							String zanCount=jsonItem.getString("zanCount");
							String significance=jsonItem.getString("significance");
							String school=jsonItem.getString("school");
							String zanUsers=jsonItem.getString("zanUsers");
							String pics=jsonItem.getString("pics");
							String thumbPicPathString=jsonItem.getString("thumbPicPathString");
							String bigPicPathString=jsonItem.getString("bigPicPathString");
						
							map=new HashMap<>();
							map.put("username", username);
							map.put("usercontent",usercontent);
							map.put("parentobjectId", parentobjectId);
							map.put("createdAt", createdAt);
							map.put("commentCount", commentCount);
							map.put("zanCount", zanCount);
							map.put("significance", significance);
							map.put("school", school);
							map.put("zanUsers", zanUsers);
							map.put("pics", pics);
							map.put("thumbPicPathString", thumbPicPathString);
							map.put("bigPicPathString", bigPicPathString);
							
							Log.e("", "thumbPicPathString:"+thumbPicPathString);
							Log.e("", "username:"+username);
							Layout_2.list.add(map);//�½���map��������Layout2�е�map,�ᷢ�����ݴ���
							
						}
							Message message=new Message();
							message.what=0x002;
							Layout_2.handler.sendMessage(message);
							
							Layout_2.adapter.setListData(Layout_2.list);//�������������
							Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�

					} catch (JSONException e) {
						e.printStackTrace();
						Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
					}
				
				}else {
					Toast.makeText(context, "�Ѿ�������", 1).show();
					Layout_2.listView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
				}

			}
		});
	}
}
