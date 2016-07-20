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
	String floorNum="";//评论楼层
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

		init();// 初始化
		listView.setRefreshing(true);
		getDataFromPreActivity();
		flushCommentList();

		//动态下拉刷新评论初始化
		refreshInit();
		

		back = (ImageView) findViewById(R.id.iv_back_title_back);
		openInfo=(ImageView) findViewById(R.id.item_img_lv_item_ui);
		back.setOnClickListener(this);
		tv_sendTxt.setOnClickListener(this);
		
	}
	
	/**
	 * 从上一个activity获取数据
	 */
	private void getDataFromPreActivity() {
		
		

		viewCount = Integer.parseInt(bundle.getString("viewcount"));// 用户浏览量
		objectId = bundle.getString("objectId");// 获取objectId

		
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
	 * 初始化控件，为控件赋值
	 */
	private void init() {

		list=new ArrayList<>();

		sp=getSharedPreferences("config",MODE_PRIVATE);
		
		listView=(PullToRefreshListView) findViewById(R.id.lv_liaotie_comment);
		adapter = new CommentItemAdapter(this,handler);

		ll_comment_head=(LinearLayout) findViewById(R.id.ll_comment_head);//获取头部linearlayout

		ed_myCommentTxt = (EditText) findViewById(R.id.et_mycomment_txt);// 评论框内容
		tv_sendTxt = (TextView) findViewById(R.id.tv_send_comment_txt);// 发送评论
		
		// 给控件填充数据
		intent = getIntent();
		bundle = intent.getExtras().getBundle("itemdata");
		
	}

	/**
	 * 发送评论
	 */
	private void sendTxt() {
		//判断是否已登陆
		if (sp.getBoolean("islogin", false)) {
			if (!ed_myCommentTxt.getText().toString().trim().equals("")) {
				final BookComment bookComment=new BookComment();
				
				//取得编辑框里的数据
				final String myComment=ed_myCommentTxt.getText().toString();
				bookComment.setComment(myComment);//设置评论内容
				ed_myCommentTxt.setText("");
				bookComment.setParentobjectId(objectId);//设置父类objectId
				bookComment.setUsername(sp.getString("accountname",""));//设置当前评论用户名
				bookComment.setSignificance(sp.getString("significance",""));//设置当前评论者用户签名
				bookComment.setSchool(sp.getString("school",""));
				
				
				//查询该主题已存在评论数			
				BmobQuery query1=new BmobQuery("BookComment");
				query1.addWhereEqualTo("parentobjectId",bookComment.getParentobjectId());
				query1.setLimit(1000);//返回最多1000条评论
				query1.findObjects(MainComment.this, new FindCallback() {				
					@Override
					public void onFailure(int arg0, String arg1) {						
					}
					
					@Override
					public void onSuccess(JSONArray arg0) {
						//插入评论计数
						MainContent mainContent=new MainContent();
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
						mainContent.setCommentcount(floorNum+"");
						mainContent.update(MainComment.this,bookComment.getParentobjectId() , new UpdateListener() {	
							@Override
							public void onSuccess() {
								Log.d("MainComment", "评论计数添加成功");
								
								//计数成功，追加评论
	
								bookComment.setFloorNum(myFloorNum);
								bookComment.save(MainComment.this,new SaveListener() {				
									@Override
									public void onSuccess() {
										
										Toast.makeText(MainComment.this,"评论成功",1).show();	
//										ed_myCommentTxt.setText("");
										
										//发送消息提醒数据改变
										
										flushCommentList();

									}				
									@Override
									public void onFailure(int arg0, String arg1) {
										Toast.makeText(MainComment.this,"评论失败",1).show();								
									}
								});	
								
							}					
							@Override
							public void onFailure(int arg0, String arg1) {	
								Log.d("MainComment", "评论计数添加失败");

							}
						});
						
					}
				});
			
				
			}
					
			
		}else {
			Toast.makeText(MainComment.this, "请登录后再操作", 1).show();
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
	 * 点击头像打开的个人详细页面
	 */
	public void openMoreInfo(View v) {
		Intent intent =new Intent(MainComment.this,InfoIconActivity.class);
		intent.putExtra("significance",bundle.getString("significance"));
		intent.putExtra("username",bundle.getString("username"));
		startActivity(intent);	
	}
	
	/**
	 * 删除评论，包括服务器
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
				Toast.makeText(context, "删除成功", 1).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				
			}
		});
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	
	}
	
	/**
	 * 下拉刷新评论初始化
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
							// 设置刷新标签
							listView.getLoadingLayoutProxy()
									.setRefreshingLabel("正在刷新");
							// 设置下拉标签
							listView.getLoadingLayoutProxy().setPullLabel(
									"下拉刷新");
							// 设置释放标签
							listView.getLoadingLayoutProxy()
									.setReleaseLabel("释放开始刷新");
							// 设置上一次刷新的提示标签
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("最后更新时间:" + str);
							// 加载数据操作
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
							// 设置刷新标签
							listView.getLoadingLayoutProxy()
									.setRefreshingLabel("正在加载");
							// 设置下拉标签
							listView.getLoadingLayoutProxy().setPullLabel(
									"加载更多");
							// 设置释放标签
							listView.getLoadingLayoutProxy()
									.setReleaseLabel("释放开始刷新");
							// 设置上一次刷新的提示标签
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("最后更新时间:" + str);
							// 加载数据操作
							 upRefreshCount=upRefreshCount+1;
							 flushCommentListAtTheEnd();

						}

					}
				});
		
		
	
	}
	
	
	/**
	 * 刷新评论列表
	 */
	private void flushCommentList() {
		upRefreshCount=0;
		
		Log.d("logd", "进入flush");

		BmobQuery query=new BmobQuery("BookComment");
		query.addWhereEqualTo("parentobjectId", objectId);
		query.setLimit(5);
		query.order("-createdAt");
		query.findObjects(MainComment.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();

				Toast.makeText(MainComment.this, "获取评论列表失败", 1).show();				
			}
			
			@Override
			public void onSuccess(JSONArray arg0) {
				try {
					list.clear();
					getDataFromPreActivity();

					Log.d("logd", "进入onSuccess");

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
					Log.d("logd", "进入错误");
					listView.onRefreshComplete();

				}
				
			}
		});
	
		
	}
	
	/**
	 * 上拉加载
	 */
	private void flushCommentListAtTheEnd() {		
		BmobQuery query=new BmobQuery("BookComment");
		query.addWhereEqualTo("parentobjectId", objectId );
		query.setLimit(5);
		query.order("-createdAt");
		query.setSkip(5*upRefreshCount);//跳过已刷新的条数
		query.findObjects(MainComment.this,new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				listView.onRefreshComplete();
				Toast.makeText(MainComment.this, "获取评论列表失败", 1).show();				
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
							list.add(map);//最新的消息在最shang面			
						}
					}else {						
						Toast.makeText(MainComment.this, "已经到底了", 1).show();					
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
	
}
