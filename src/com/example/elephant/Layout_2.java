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
	static List<Map<String,String>> list;//从服务器获取的的数据
	static Map<String, String> map;
	static boolean idFirstIn=true;//是否第一次进入该页面
	DataFromWeb dataFromWeb;
	static Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==0x001) {
				listView.setAdapter(adapter);			
			}
			if (msg.what==0x002) {
				adapter.notifyDataSetChanged();	
				//设置上一个item位置不变
				listView.getRefreshableView().setSelectionFromTop(5*upRefreshCount-1, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);

			}
			if (msg.what==0x1111) {
				adapter.notifyDataSetChanged();
			}
			
			
		};
	};
	static int upRefreshCount=0;//上拉加载数据次数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_2);

		init();// 控件初始化

		/*
		 * 设置PullToRefresh刷新模式 BOTH:上拉刷新和下拉刷新都支持 DISABLED：禁用上拉下拉刷新
		 * PULL_FROM_START:仅支持下拉刷新（默认） PULL_FROM_END：仅支持上拉刷新
		 * MANUAL_REFRESH_ONLY：只允许手动触发
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
					// 设置刷新标签
					listView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
					// 设置下拉标签
					listView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
					// 设置释放标签
					listView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
					// 设置上一次刷新的提示标签
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"最后更新时间:" + str);
					// 加载数据操作
					upRefreshCount=0;
					dataFromWeb.flushDataFromWeb();
				}
				if (refreshView.isFooterShown()) {
					String str = DateUtils.formatDateTime(Layout_2.this,
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);
					// 设置刷新标签
					listView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
					// 设置下拉标签
					listView.getLoadingLayoutProxy().setPullLabel("加载更多");
					// 设置释放标签
					listView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
					// 设置上一次刷新的提示标签
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"最后更新时间:" + str);
					// 加载数据操作
					upRefreshCount=upRefreshCount+1;
					dataFromWeb.refreshDataAtTheEnd();
					
				}
				
				
			}

		});
		//item被点击
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				map=list.get(position-1);//增加了下拉刷新，脚标增加了1，这里减1
				Intent intent=new Intent(Layout_2.this,LiaoTieCommentActivity.class);//打开详情评论界面

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
		//只建立一个对象，节省内存空间
		adapter = new Layout2MianListAdapter(this,handler);	
		dataFromWeb=new DataFromWeb(Layout_2.this);
		dataFromWeb.flushDataFromWeb();

	}	

	
	/**
	 * MianUIAdapter调用删除listview item
	 * 包括服务器数据
	 * @param bigPicPathString 
	 * @param thumbPicPathString 
	 */
	public static void deleteListViewItem(final Context context,int position, String thumbPicPathString, String bigPicPathString) {
		Map map=list.get(position);
		LiaoTie liaoTie=new LiaoTie();
		liaoTie.setObjectId(map.get("parentobjectId").toString());//本身item的objectId
		liaoTie.delete(context, new DeleteListener() {			
			@Override
			public void onSuccess() {	
				Toast.makeText(context, "删除成功",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "删除失败", 1).show();
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
		
		//删除文件
		String newUrlString=bigPicPathString+","+thumbPicPathString;		
		String[] urls =newUrlString.split(",");
		
		BmobFile.deleteBatch(context, urls, new DeleteBatchListener() {

		    @Override
		    public void done(String[] failUrls, BmobException e) {
		        if(e==null){
//		        	Toast.makeText(context,"全完",1).show();;
		        }else{
		            if(failUrls!=null){
//		                Toast.makeText(context,"删除失败个数："+failUrls.length+","+e.toString(),1).show();;
		            }else{
//		            	Toast.makeText(context,"全部文件删除失败："+e.getErrorCode()+","+e.toString(),1).show();
		            }
		        }
		    }

			
		});
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		upRefreshCount=0;//不知为什么，点击后退不清零，必须强制清零
		super.onBackPressed();
	}
	
	
}

/**
 * 从服务器获取数据类
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
	 * 从服务器获取数据
	 */
	public void flushDataFromWeb() {
		BmobQuery query = new BmobQuery("LiaoTie");// 查询内容数据
		query.setLimit(5);//每次刷新10条数据
		query.order("-createdAt");//按照时间反序排序，最新的排最前面
		query.findObjects(context, new FindCallback() {

			@Override
			public void onFailure(int arg0, String arg1) {
				Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
				Toast.makeText(context, "网络不好，请检查", 1).show();

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
								
							
								Layout_2.list.add(Layout_2.map);//数据放在首位
							
							}
								Message message=new Message();
								message.what=0x001;
								Layout_2.handler.sendMessage(message);							
								Layout_2.adapter.setListData(Layout_2.list);//适配器获得数据
								Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
						}else {
//							Toast.makeText(context, "发布成功", 1).show();
						}
						

						

					} catch (JSONException e) {
						e.printStackTrace();
						Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，

					}
				
				}

			}
		});
	}
	
	/**
	 * 刷新加载数据
	 */
	public void refreshDataAtTheEnd(){
		
		BmobQuery query = new BmobQuery("LiaoTie");// 查询内容数据
		query.setLimit(5);//每次刷新10条数据		
		query.order("-createdAt");//按照时间反序排序，最新的排最前面
		query.setSkip(5*(Layout_2.upRefreshCount));
		query.findObjects(context, new FindCallback() {

			@Override
			public void onFailure(int arg0, String arg1) {
				Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
				Toast.makeText(context, "网络不好，请检查", 1).show();

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
							Layout_2.list.add(map);//新建的map，不能用Layout2中的map,会发生数据错乱
							
						}
							Message message=new Message();
							message.what=0x002;
							Layout_2.handler.sendMessage(message);
							
							Layout_2.adapter.setListData(Layout_2.list);//适配器获得数据
							Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，

					} catch (JSONException e) {
						e.printStackTrace();
						Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
					}
				
				}else {
					Toast.makeText(context, "已经到底了", 1).show();
					Layout_2.listView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
				}

			}
		});
	}
}
