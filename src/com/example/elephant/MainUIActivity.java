package com.example.elephant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.adpter.MainUIItemAdapter;
import com.example.elephant.db.BookComment;
import com.example.elephant.db.MainContent;
import com.example.elephant.utils.DownLoadUtil;
import com.example.elephant.utils.MyBroadcastReceiver;
import com.example.elephant.utils.XMLParseForString;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainUIActivity extends Activity {

	String TAG="MainUIActivity";
	static final int GET_INFO_SUCCESS=10;
	static final int SERVER_ERROR=11;
	static final int SERVER_URL_ERROR=12;
	static final int PROTOCOL_ERROR=13;
	static final int IO_ERROR=14;
	static final int XML_PARSE_ERROR=15;
	static final int DOWNLOAD_SUCCESS=16;
	static final int DOWNLOAD_ERROR=17;
	
	public static ProgressDialog flushPd;//主界面数据刷新
	public static PullToRefreshListView mainListView;//主页面ListView
	SharedPreferences sp;
	XMLParseForString xmlResult;
	ProgressDialog pd;
	File file;//下载的文件
//	static ImageView addImg;//添加动态
	ImageView infoIcon;//点击个人头像查看信息
	ImageView openMore;//打开更多
	public static MainUIItemAdapter adapter;
	static List<Map<String,String>> list;
	MyBroadcastReceiver myBroadcastReceiver;
	Bundle itembBundle;//传给评论页面
	Intent itemIntent;//打开评论页面
	String[] item_values;
	String bookauthor,usercontent,username,school,bookname,bookpublisher,bookisbn,viewcountStr,objectId,createdAt,significance,commentcount;
	int viewcount=0;
	Map<String, String> map;
    static int upRefreshCount=0;
	DataFromWebMainUI dataFromWebMainUI;
	
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case GET_INFO_SUCCESS:
				showDownDialog();
				break;
			case SERVER_URL_ERROR:
				Toast.makeText(MainUIActivity.this,"服务器URL错误",1).show();
				break;
			case IO_ERROR:
//				Toast.makeText(MainUIActivity.this,"IO错误",1).show();
				break;
			case XML_PARSE_ERROR:
				Toast.makeText(MainUIActivity.this,"xml解析出错",1).show();
				break;
			case DOWNLOAD_SUCCESS:
				Toast.makeText(MainUIActivity.this,"下载完成",1).show();
				//自动安装
				autoInstallApk(file);
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(MainUIActivity.this,"下载出错",1).show();
				break;
			case 0x1111://更新头像
				adapter.notifyDataSetChanged();
			default:
				break;
			}
		
		}

		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_main_ui);
		
		mainListView=(PullToRefreshListView) findViewById(R.id.lv_main_ui_activity);
		
		itembBundle=new Bundle();
		itemIntent=new Intent(MainUIActivity.this,MainComment.class);
		
		dataFromWebMainUI=new DataFromWebMainUI(this);//刷新数据类
		
		//第一个版本
		sp=getSharedPreferences("config", MODE_PRIVATE);
		Editor editor=sp.edit();
		if (!sp.getString("version", "").equals("2.0")) {
			//升级
		}else {
			editor.putString("version", "2.0");
			editor.commit();
		}
		
		
		/**
		 * Bmob后端服务初始化
		 */
		Bmob.initialize(this, "c0fc8badde1ed95a0f425c1191d27bd1");
		
		
		//开启检查升级
		new Thread(new CheckedForUpdate()).start();
		
		//点击头像显示个人信息
		infoIcon=(ImageView) findViewById(R.id.item_img_lv_item_ui);
		//打开更多
		openMore = (ImageView) findViewById(R.id.main_item_more);
		//下拉刷新控件初始化
		refreshInit();
		
		/**
		 * 主界面view加载
		 */
		list=new ArrayList<>();
		dataFromWebMainUI.flushDataFromWeb();

		adapter=new MainUIItemAdapter(this,handler);		
		
		mainListView.setOnItemClickListener(new OnItemClickListener() {//获取单个Item的内容，传给评论页面

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				itembBundle=new Bundle();
				map =list.get(arg2-1);//获取单个Item的内容，传给评论页面。

				username=map.get("username");//不为空值
				bookname=map.get("bookname");//不为空值
				usercontent=map.get("usercontent");//不为空值
				viewcountStr=map.get("viewcount");//为0
				viewcount=Integer.parseInt(viewcountStr)+1;//每点击一次增加一浏览量
				bookauthor=map.get("bookauthor");
				bookpublisher=map.get("bookpublisher");
				bookisbn=map.get("bookisbn");
				objectId=map.get("objectId");
				createdAt=map.get("createdAt");
				significance=map.get("significance");
				commentcount=map.get("commentcount");
				school=map.get("school");
			
					
				itembBundle.putString("bookauthor", bookauthor);
				itembBundle.putString("usercontent", usercontent);
				itembBundle.putString("username", username);
				itembBundle.putString("bookname", bookname);
				itembBundle.putString("bookpublisher", bookpublisher);
				itembBundle.putString("bookisbn", bookisbn);
				itembBundle.putString("viewcount", viewcount+"");
				itembBundle.putString("objectId", objectId);
				itembBundle.putString("createdAt", createdAt);
				itembBundle.putString("significance", significance);
				itembBundle.putString("commentcount", commentcount);
				itembBundle.putString("school", school);
				
				itemIntent.putExtra("itemdata", itembBundle);
				startActivity(itemIntent);
				itembBundle.clear();
				
				//浏览数量
				map.put("viewcount",viewcount+"");
				list.add(0,map);
				adapter.notifyDataSetChanged();
				list.remove(0);
		
			}
		});
		
		
				
		
		
	}
	/**
	 * 刷新效果实现
	 */
	private void refreshInit() {
		/**
		 * android原生
		 */
//		refresh_layout = (SwipeRefreshLayout) this.findViewById(R.id.refresh_layout);
//		refresh_layout.setColorScheme(R.color.lightpink, R.color.lightgreen, R.color.lightcoral, R.color.lightsteelblue);//设置跑动的颜色值
//		refresh_layout.setOnRefreshListener(new OnRefreshListener() {	//设置下拉的监听	
//			@Override
//			public void onRefresh() {				
//				flushDataThread.getData();
//				list.clear();
//				list=flushDataThread.getList();								
//			}
//		});

		
		/**
		 * pullToRefresh
		 */
		mainListView.setMode(Mode.BOTH);

		mainListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {

						if (refreshView.isHeaderShown()) {
							String str = DateUtils.formatDateTime(
									MainUIActivity.this,
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							// 设置刷新标签
							mainListView.getLoadingLayoutProxy()
									.setRefreshingLabel("正在刷新");
							// 设置下拉标签
							mainListView.getLoadingLayoutProxy().setPullLabel(
									"下拉刷新");
							// 设置释放标签
							mainListView.getLoadingLayoutProxy()
									.setReleaseLabel("释放开始刷新");
							// 设置上一次刷新的提示标签
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("最后更新时间:" + str);
							// 加载数据操作
							 upRefreshCount=0;
							 dataFromWebMainUI.flushDataFromWeb();
							
						}
						if (refreshView.isFooterShown()) {
							String str = DateUtils.formatDateTime(
									MainUIActivity.this,
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							// 设置刷新标签
							mainListView.getLoadingLayoutProxy()
									.setRefreshingLabel("正在加载");
							// 设置下拉标签
							mainListView.getLoadingLayoutProxy().setPullLabel(
									"加载更多");
							// 设置释放标签
							mainListView.getLoadingLayoutProxy()
									.setReleaseLabel("释放开始刷新");
							// 设置上一次刷新的提示标签
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("最后更新时间:" + str);
							// 加载数据操作
							 upRefreshCount=upRefreshCount+1;
							 dataFromWebMainUI.refreshDataAtTheEnd();

						}

					}
				});
		
	}

	/**
	 * 显示下载进度条
	 */
	private void showDownDialog() {
		String description=xmlResult.getDescription();
		final String apkurl=xmlResult.getApkurl();
		
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("升级提示");
		builder.setMessage(description);
		builder.setCancelable(false);
		builder.setPositiveButton("马上升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainUIActivity.this,"开始下载",1).show();
				
				pd=new ProgressDialog(MainUIActivity.this);
				pd.setMessage("正在下载...");
				pd.setCancelable(false);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				
				new Thread(){
					public void run() {
						////下载文件工具类，获取下载文件的文件名
						String filename=DownLoadUtil.getFilename(apkurl);
						//在sd卡建立一个文件
						file=new File(Environment.getExternalStorageDirectory(),filename);
						//得到下载后的apk文件
						file=DownLoadUtil.getFile(apkurl,file.getAbsolutePath(),pd);
						
						if (file!=null) {
							Message message=Message.obtain();
							message.what=DOWNLOAD_SUCCESS;
//							message.obj=file;
							handler.sendMessage(message);
						}else {
							Message message=Message.obtain();
							message.what=DOWNLOAD_ERROR;
							handler.sendMessage(message);
						}
						pd.dismiss();		
						
					};
				}.start();
				
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();		
		
	}
	
	/**
	 * 下载好自动安装
	 */
	private void autoInstallApk(File file) {
		Intent intent=new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	
	/**
	 * 检查升级
	 */
	private class CheckedForUpdate implements Runnable{

		Message message=Message.obtain();
		@Override
		public void run() {
			
			//获取资源文件下的字符串http://10.50.3.40:80/mobilesafe.apk
			String updateUrl=getResources().getString(R.string.update_url);
			try {
				URL url=new URL(updateUrl);
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(5000);
				
				InputStream is=conn.getInputStream();				
				xmlResult=new XMLParseForString(is);//返回信息
				String oldVersion=sp.getString("version", "");
				String newVersion=xmlResult.getVersion();
				
				//判断版本是否一致
				if (!oldVersion.equals(newVersion)) {
					//版本不同，升级
					Log.d(TAG, "版本不同，升级");
					message.what=GET_INFO_SUCCESS;
					handler.sendMessage(message);
										
				}else {
					Log.d(TAG, "相同版本，无需升级");
				}	
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				message.what=SERVER_URL_ERROR;
				handler.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
				message.what=IO_ERROR;
				handler.sendMessage(message);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				message.what=XML_PARSE_ERROR;
				handler.sendMessage(message);
			} 
		}
		
	}
	

	/**
	 * 返回数据给广播接收器
	 * @return
	 */
	public static MainUIItemAdapter getAdapterData() {
		
		return adapter;
	}

	/**
	 * MianUIAdapter调用删除listview item
	 * 包括服务器数据
	 */
	public static void deleteListViewItem(final Context context,int position) {
		Map map=list.get(position);
		MainContent mainContent=new MainContent();
		mainContent.setObjectId(map.get("objectId").toString());
		mainContent.delete(context, new DeleteListener() {			
			@Override
			public void onSuccess() {	
				Toast.makeText(context, "删除成功",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "删除失败", 1).show();
			}
		});
		
		BmobQuery query=new BmobQuery("BookComment");
		query.addWhereEqualTo("parentobjectId", map.get("objectId").toString());
		query.findObjects(context, new FindCallback() {		
			@Override
			public void onFailure(int arg0, String arg1) {				
			}		
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				BookComment bookComment=new BookComment();
				if (arg0.length()>0) {
					for (int i = 0; i < arg0.length(); i++) {
						try {
							JSONObject iJsonObject=arg0.getJSONObject(i);
							bookComment.setObjectId(iJsonObject.get("objectId").toString());
							bookComment.delete(context, new DeleteListener() {						
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
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
	
}


/**
 * 从服务器获取数据类
 * @author Administrator
 *
 */
class DataFromWebMainUI{
	
	Context context;
	Map<String,String> map;
	
	public DataFromWebMainUI(Context context) {
		this.context=context;
	}
	
	/**
	 * 从服务器获取数据
	 */
	public void flushDataFromWeb() {
		BmobQuery query=new BmobQuery("MainContent");//查询内容数据		
		query.setLimit(5);
		query.order("-createdAt");//按照时间反序排序，最新的排最前面
		query.findObjects(context, new FindCallback() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				MainUIActivity.mainListView.onRefreshComplete();
				Toast.makeText(context, "网络不好，请检查", 1).show();

			}
			
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				
				if (arg0.length() > 0) {

					try {
						MainUIActivity.list.clear();

						for (int i = 0; i < arg0.length(); i++) {

							JSONObject jsonItem=arg0.getJSONObject(i);
							
							String username=jsonItem.getString("username");
							String usercontent=jsonItem.getString("usercontent");
							String objectId=jsonItem.getString("objectId");
							String createdAt=jsonItem.getString("createdAt");
							String commentcount=jsonItem.getString("commentcount");
							String viewcount=jsonItem.getString("viewcount");//获取到浏览量为字符串
							String school=jsonItem.getString("school");
							String significance,bookname,bookisbn,bookauthor,bookpublisher;
							
							
							if (!jsonItem.getString("significance").equals(".#")) {
								significance=jsonItem.getString("significance");
							}else {
								significance="";
							}
							
							if (!jsonItem.getString("bookname").equals(".#")) {
								bookname=jsonItem.getString("bookname");
							}else {
								bookname="";
							}
							
							if (!jsonItem.getString("bookisbn").equals(".#")) {
								bookisbn=jsonItem.getString("bookisbn");
							} else {
							    bookisbn="";
							}
							
							if (!jsonItem.getString("bookauthor").equals(".#")) {
								bookauthor=jsonItem.getString("bookauthor");
							}else {
								bookauthor="";
							}
							
							if (!jsonItem.getString("bookpublisher").equals(".#")) {
								bookpublisher=jsonItem.getString("bookpublisher");
							}else {
								bookpublisher="";
							}

							//装载进Map集合
							map=new HashMap();
							map.put("username", username);
							map.put("usercontent",usercontent);
							map.put("significance", significance);
							map.put("bookname", bookname);
							map.put("bookisbn", bookisbn);
							map.put("bookauthor", bookauthor);
							map.put("bookpublisher", bookpublisher);
							map.put("objectId", objectId);
							map.put("viewcount", viewcount);
							map.put("createdAt", createdAt);
							map.put("commentcount", commentcount);
							map.put("school", school);
							
							MainUIActivity.list.add(map);//数据放在首位

						}

						MainUIActivity.adapter.setListData(MainUIActivity.list);// 适配器获得数据
						MainUIActivity.mainListView.setAdapter(MainUIActivity.adapter);
						MainUIActivity.mainListView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，

					} catch (JSONException e) {
						e.printStackTrace();
						MainUIActivity.mainListView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，

					}

				}
				
			}
		});
	}
	
	/**
	 * 刷新加载数据
	 */
	public void refreshDataAtTheEnd(){
		
		BmobQuery query=new BmobQuery("MainContent");//查询内容数据		
		query.setLimit(5);
		query.setSkip(5*MainUIActivity.upRefreshCount);
		query.order("-createdAt");//按照时间反序排序，最新的排最前面
		query.findObjects(context, new FindCallback() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				MainUIActivity.mainListView.onRefreshComplete();
				Toast.makeText(context, "网络不好，请检查", 1).show();

			}
			
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				
				if (arg0.length() > 0) {

					try {
						for (int i = 0; i < arg0.length(); i++) {

							JSONObject jsonItem=arg0.getJSONObject(i);
							
							String username=jsonItem.getString("username");
							String usercontent=jsonItem.getString("usercontent");
							String objectId=jsonItem.getString("objectId");
							String createdAt=jsonItem.getString("createdAt");
							String commentcount=jsonItem.getString("commentcount");
							String viewcount=jsonItem.getString("viewcount");//获取到浏览量为字符串
							String school=jsonItem.getString("school");

							String significance,bookname,bookisbn,bookauthor,bookpublisher;
							
							
							if (!jsonItem.getString("significance").equals(".#")) {
								significance=jsonItem.getString("significance");
							}else {
								significance="";
							}
							
							if (!jsonItem.getString("bookname").equals(".#")) {
								bookname=jsonItem.getString("bookname");
							}else {
								bookname="";
							}
							
							if (!jsonItem.getString("bookisbn").equals(".#")) {
								bookisbn=jsonItem.getString("bookisbn");
							} else {
							    bookisbn="";
							}
							
							if (!jsonItem.getString("bookauthor").equals(".#")) {
								bookauthor=jsonItem.getString("bookauthor");
							}else {
								bookauthor="";
							}
							
							if (!jsonItem.getString("bookpublisher").equals(".#")) {
								bookpublisher=jsonItem.getString("bookpublisher");
							}else {
								bookpublisher="";
							}

							//装载进Map集合
							map=new HashMap();
							map.put("username", username);
							map.put("usercontent",usercontent);
							map.put("significance", significance);
							map.put("bookname", bookname);
							map.put("bookisbn", bookisbn);
							map.put("bookauthor", bookauthor);
							map.put("bookpublisher", bookpublisher);
							map.put("objectId", objectId);
							map.put("viewcount", viewcount);
							map.put("createdAt", createdAt);
							map.put("commentcount", commentcount);
							map.put("school", school);
							
							MainUIActivity.list.add(map);//数据放在首位

						}

						MainUIActivity.adapter.setListData(MainUIActivity.list);// 适配器获得数据
						MainUIActivity.mainListView.setAdapter(MainUIActivity.adapter);
						MainUIActivity.mainListView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
						MainUIActivity.adapter.notifyDataSetChanged();	
						//设置上一个item位置不变
						MainUIActivity.mainListView.getRefreshableView().setSelectionFromTop(5*MainUIActivity.upRefreshCount-1, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);

					} catch (JSONException e) {
						e.printStackTrace();
						MainUIActivity.mainListView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，

					}

				}else {
					Toast.makeText(context, "已经到底了", 1).show();
					MainUIActivity.mainListView.onRefreshComplete();// 数据加载到适配器完成后，刷新完成，
				}
				
			}
		});
	}
}
