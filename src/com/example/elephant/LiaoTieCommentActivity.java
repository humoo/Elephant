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
	static List<Map<String, String>> list;//评论列表
	Map<String, String> map;
	ImageView back;
	TextView send;
	EditText et_mycomment_txt;
	Intent intent;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	String floorNum="";//评论楼层
	String myFloorNum="";
	int upRefreshCount=0;//上拉刷新次数
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
		 * 设置滑动上下拉加载
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
					flushCommentList();
				}
				if (refreshView.isFooterShown()) {
					String str = DateUtils.formatDateTime(LiaoTieCommentActivity.this,
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
					flushCommentListAtTheEnd();
					
				}
			}

			
		});
		
		/**
		 * 获得上个activity传过来的数据
		 */
		getDataFromPreActivity();
		/**
		 * 刷新评论列表
		 */
		flushCommentList();
		//按下返回
		back.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//发送评论
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("logd","点击发送");
				sendComment();
				
			}

			
		});
	}

	
	/**
	 * 刷新评论列表
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

				Toast.makeText(LiaoTieCommentActivity.this, "获取评论列表失败", 1).show();				
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
						list.add(map);//最新的消息在最shang面			

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
	 * 上拉加载
	 */
	private void flushCommentListAtTheEnd() {		
		BmobQuery query=new BmobQuery("LiaoTieComment");
		query.addWhereEqualTo("parentobjectId", intent.getStringExtra("parentobjectId"));
		query.setLimit(5);
		query.order("-createdAt");
		query.setSkip(5*upRefreshCount);//跳过已刷新的条数
		query.findObjects(LiaoTieCommentActivity.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();

				Toast.makeText(LiaoTieCommentActivity.this, "获取评论列表失败", 1).show();				
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
							list.add(map);//最新的消息在最shang面			
						}
					}else {						
						Toast.makeText(LiaoTieCommentActivity.this, "已经到底了", 1).show();					
					}           
					listView.onRefreshComplete();
					adapter.notifyDataSetChanged();
					//设置上一个item位置不变
					listView.getRefreshableView().setSelectionFromTop(5*upRefreshCount-1, ComponentCallbacks2.TRIM_MEMORY_BACKGROUND);

				} catch (JSONException e) {
					e.printStackTrace();
					listView.onRefreshComplete();

				}
				
			}
		});
	}

	/**
	 * 获得上个activity传过来的数据并设置adapter
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
	 * 发送评论
	 */
	private void sendComment() {
		

		final String myComment=et_mycomment_txt.getText().toString();
		if (!myComment.trim().equals("")) {
			final LiaoTieComment liaoTieComment=new LiaoTieComment();
			liaoTieComment.setComment(myComment);
			liaoTieComment.setSchool(openSharePrefrenceHelper.getSP_CONFIG().getString("school", ""));
			et_mycomment_txt.setText("");
			liaoTieComment.setUsername(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""));
			liaoTieComment.setParentobjectId(intent.getStringExtra("parentobjectId"));//设置父类objectId
			liaoTieComment.setSignificance(openSharePrefrenceHelper.getSP_CONFIG().getString("significance",""));//设置当前评论者用户签名
			
			//查询该主题已存在评论数			
			BmobQuery query1=new BmobQuery("LiaoTieComment");
			query1.addWhereEqualTo("parentobjectId",liaoTieComment.getParentobjectId());
			query1.setLimit(1000);//返回最多1000条评论
			query1.findObjects(LiaoTieCommentActivity.this, new FindCallback() {				
				@Override
				public void onFailure(int arg0, String arg1) {						
				}
				
				@Override
				public void onSuccess(JSONArray arg0) {
					//插入评论计数
					//if (arg0.length()>0) {
						LiaoTie liaoTie=new LiaoTie();
						floorNum=arg0.length()+1+"";//
						
						//查询评论中楼层最高的数
						try {
							org.json.JSONObject iJsonObject=arg0.getJSONObject(arg0.length()-1);						
							myFloorNum=iJsonObject.getString("floorNum");
							myFloorNum=(Integer.parseInt(myFloorNum)+1)+"";
							
						} catch (JSONException e) {
							e.printStackTrace();
							myFloorNum=1+"";//异常说明当前为第一条纪录
						}
						liaoTie.setCommentCount(floorNum+"");
						liaoTie.update(LiaoTieCommentActivity.this,liaoTieComment.getParentobjectId() , new UpdateListener() {	
							@Override
							public void onSuccess() {
								Log.d("MainComment", "评论计数添加成功");
								
								//计数成功，追加评论

								liaoTieComment.setFloorNum(myFloorNum);
								liaoTieComment.save(LiaoTieCommentActivity.this,new SaveListener() {				
									@Override
									public void onSuccess() {
										Toast.makeText(LiaoTieCommentActivity.this,"评论成功",1).show();											
										//发送消息提醒数据改变										
										Map<String,String> map = new HashMap();
										map.put("usercontent", myComment);
										map.put("username",openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""));
										map.put("significance",openSharePrefrenceHelper.getSP_CONFIG().getString("significance",""));
										map.put("floorNum", myFloorNum);
										map.put("school", openSharePrefrenceHelper.getSP_CONFIG().getString("school", ""));
										list.add(1,map);//最新的消息在最shang面
										//刷新数据
//										adapter.notifyDataSetChanged();
										//评论成功应从网上刷新数据
										flushCommentList();
										
									}				
									@Override
									public void onFailure(int arg0, String arg1) {
										Toast.makeText(LiaoTieCommentActivity.this,"评论失败",1).show();								
									}
								});	
							}					
							@Override
							public void onFailure(int arg0, String arg1) {	
								Log.d("logd", "评论计数添加失败");
							}
						});
					}					
			});
						
		}else {
			Toast.makeText(LiaoTieCommentActivity.this,"你还没输入内容", 1).show();
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
	 * 删除数据
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
				Toast.makeText(context, "删除成功",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "删除失败", 1).show();
			}
		});
		
		/**
		 * 更新评论计数，这里不更新了
		 */
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
}
