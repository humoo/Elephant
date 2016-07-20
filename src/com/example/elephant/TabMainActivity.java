package com.example.elephant;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMMyself.OnReceiveTextListener;
import imsdk.data.IMSDK;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elephant.db.MyOpenHelper;
import com.example.elephant.im.ChatFragment;
import com.example.elephant.im.ChatMainActivity;
import com.example.elephant.im.adapter.ChatListViewAdapter;
import com.example.elephant.im.adapter.ShowChatMsgAdapter;
import com.example.elephant.im.bean.ShowChatMsgListView;
import com.example.elephant.utils.IsOnTopTaskHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressLint("ResourceAsColor")
public class TabMainActivity extends TabActivity implements OnClickListener {

	ImageView addNew;
	int clickId = 0;
	TextView one, two, three, four;
	TabHost tabHost;
	Drawable drawableOne, drawableOne2, drawableTwo, drawableTwo2,
			drawableThree, drawableThree2, drawableFour, drawableFour2;
	PopupWindow pop;// 向上弹出发布
	OpenSharePrefrenceHelper openSharePrefrenceHelper;

	NotificationManager manager;
	Notification notify = new Notification();  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);

		// //沉浸式状态栏
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// Window window = getWindow();
		//
		// // Translucent status bar
		// window.setFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// // Translucent navigation bar
		// window.setFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// }

		// 获取该activity里的TabHost组件
		// TabHost tabHost=getTabHost();

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());
		// 获取导航按钮控件
		TabWidget tabWidget = tabHost.getTabWidget();
		addNew = (ImageView) findViewById(R.id.add_new);

		// 创建Tab分页面
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("书市")
				.setContent(new Intent(this, MainUIActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("聊贴")
				.setContent(new Intent(this, Layout_2.class)));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("浏览")
				.setContent(new Intent(this, Layout_3.class)));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("账号")
				.setContent(new Intent(this, Layout_4.class)));

		one = (TextView) findViewById(R.id.tv_tab_one);
		two = (TextView) findViewById(R.id.tv_tab_two);
		three = (TextView) findViewById(R.id.tv_tab_three);
		four = (TextView) findViewById(R.id.tv_tab_four);

		drawableOne = getResources().getDrawable(R.drawable.shushi);
		drawableOne2 = getResources().getDrawable(R.drawable.shushi2);
		drawableTwo = getResources().getDrawable(R.drawable.guanshui);
		drawableTwo2 = getResources().getDrawable(R.drawable.guanshui2);
		drawableThree = getResources().getDrawable(R.drawable.liulan);
		drawableThree2 = getResources().getDrawable(R.drawable.liulan2);
		drawableFour = getResources().getDrawable(R.drawable.accountpng);
		drawableFour2 = getResources().getDrawable(R.drawable.accountpng2);
		// 初始化导航栏图标
		drawableOne.setBounds(0, 0, drawableOne.getMinimumWidth(),
				drawableOne.getMinimumHeight());
		drawableOne2.setBounds(0, 0, drawableOne2.getMinimumWidth(),
				drawableOne2.getMinimumHeight());
		drawableTwo.setBounds(0, 0, drawableTwo.getMinimumWidth(),
				drawableTwo.getMinimumHeight());
		drawableTwo2.setBounds(0, 0, drawableTwo2.getMinimumWidth(),
				drawableTwo2.getMinimumHeight());
		drawableThree.setBounds(0, 0, drawableThree.getMinimumWidth(),
				drawableThree.getMinimumHeight());
		drawableThree2.setBounds(0, 0, drawableThree2.getMinimumWidth(),
				drawableThree2.getMinimumHeight());
		drawableFour.setBounds(0, 0, drawableFour.getMinimumWidth(),
				drawableFour.getMinimumHeight());
		drawableFour2.setBounds(0, 0, drawableFour2.getMinimumWidth(),
				drawableFour2.getMinimumHeight());

		/**
		 * 分布页面设置
		 */
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);

		/**
		 * 向上弹出发布新消息对话框
		 */
		final View view = getLayoutInflater()
				.inflate(R.layout.pop_dialog, null);
		ImageView add_new_shutie = (ImageView) view
				.findViewById(R.id.add_new_shutie);
		ImageView add_new_liaotie = (ImageView) view
				.findViewById(R.id.add_new_liaotie);
		TextView cancelBtn = (TextView) view.findViewById(R.id.cancel_btn);

		add_new_shutie.setOnClickListener(this);
		add_new_liaotie.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);// 点击back退出pop
		pop.setAnimationStyle(R.style.add_new_style);
		pop.setBackgroundDrawable(new ColorDrawable(0x33ff0000));// 设置背景透明，点击back退出pop
		addNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (pop.isShowing()) {
					pop.dismiss();
				} else {
					pop.showAtLocation(view, Gravity.BOTTOM, 0, -560);//在父控件下方出来
					// pop.showAsDropDown(view);
				}

			}
		});

		// 设置颜色
		// for (int i = 0; i< tabWidget.getChildCount(); i++) {
		// TextView tv=((TextView)
		// tabWidget.getChildAt(i).findViewById(android.R.id.title));
		// tv.setTextColor(Color.rgb(95, 158,160));
		// tv.setTextSize(15);
		//
		// }
		
		
		/**
		 *  初始化IMSDK
		 */
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(this);
		// 在Application类onCreate()方法中，设置applicationContext和appKey
		IMSDK.init(getApplicationContext(), "ac92c957619f334f9263d7e3");  
		// 设置本用户的用户名
		IMMyself.setCustomUserID(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""));	 
		// 设置本用户密码
		IMMyself.setPassword(openSharePrefrenceHelper.getSP_CONFIG().getString("password", ""));
		// 一键登录
		IMMyself.login(false, 5, null);
		// 发送文本消息
		IMMyself.sendText("Hello!", "lyc@imsdk.im", 5, null);
		//注册登陆初始化
		loginINIT();
		//接收信息
		receiveMsg();
		
		
		
		
		/**
		 * ImageLoader初始化
		 */
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.gridview_bg)
				.showImageOnFail(R.drawable.gridview_bg)
				.showImageOnLoading(R.drawable.gridview_bg)
				.cacheInMemory(true)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		
		
		/**
		 * 通知初始化
		 */
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
	}

	
	// Notification标示ID  
    private static final int ID = 1;  
    private MyOpenHelper dbHelper;
    private SQLiteDatabase db ;
	/**
	 * 接收信息  From IM
	 */
	private void receiveMsg() {
		// 设置监听器
		IMMyself.setOnReceiveTextListener(new OnReceiveTextListener() {
			// 监听来自其他用户的文本讯息
			@SuppressWarnings({ "deprecation", "static-access" })
			@Override
			public void onReceiveText(String text, String fromCustomUserID,
					long serverActionTime) {

				/**
				 * 保存到数据库
				 */				
				//如果ChatMainActivity在栈顶的话
				if (IsOnTopTaskHelper.getTopActivityName(TabMainActivity.this).equals("ChatMainActivity")) {

					/**
					 * 保存到数据库并更新UI
					 */
					//保存到数据库,fromCustomUserID应为邮箱
					dbHelper=new MyOpenHelper(TabMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
					db =dbHelper.getWritableDatabase();
					dbHelper.saveChatMsg(db,fromCustomUserID,
							openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
							text,
							serverActionTime
							,1,0);
					if (ChatMainActivity.isShow) {						
						/*
						 * 重新加载数据
						 */
						ShowChatMsgAdapter.list.clear();
						ShowChatMsgAdapter.setData();
						ChatMainActivity.adapter.notifyDataSetChanged();
						ChatMainActivity.lv_show_chat_msg.setSelection(ShowChatMsgAdapter.list.size()-1);
					}
				}else if (IsOnTopTaskHelper.getTopActivityName(TabMainActivity.this).equals("IM_Chat_activity")) {

					/**
					 * 发出通知并保存到数据库并刷新ui小红点
					 */
					notify.icon = R.drawable.circle_head;
					notify.tickerText = text; // 显示在状态栏中的文字
					notify.when = System.currentTimeMillis(); // 设置来通知时的时间
					// 单击通知后会跳转到NotificationResult类
					Intent intent = new Intent(TabMainActivity.this,
							ChatFragment.class);
					// 获取PendingIntent,点击时发送该Intent
					PendingIntent pIntent = PendingIntent.getActivity(
							TabMainActivity.this, 0, intent, 0);
					notify.flags |= Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
					notify.defaults = Notification.DEFAULT_ALL; // 把所有的属性设置成默认
					// 设置通知的标题和内容
					notify.setLatestEventInfo(TabMainActivity.this, fromCustomUserID, text,
							pIntent);
					// 发出通知
					manager.notify(ID, notify);
					
					//保存到数据库,fromCustomUserID应为邮箱
					dbHelper=new MyOpenHelper(TabMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
					db =dbHelper.getWritableDatabase();
					dbHelper.saveChatMsg(db,fromCustomUserID,
							openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
							text,
							serverActionTime
							,0,0);
					
					ChatListViewAdapter.list.clear();
					ChatListViewAdapter.setListData();//重新获取数据
					ChatFragment.adapter.notifyDataSetChanged();
					
					
				}else {//都不在栈顶

					/**
					 * 发出通知并保存到数据库
					 */
					notify.icon = R.drawable.circle_head;
					notify.tickerText = text; // 显示在状态栏中的文字
					notify.when = System.currentTimeMillis(); // 设置来通知时的时间
					// 单击通知后会跳转到NotificationResult类
					Intent intent = new Intent(TabMainActivity.this,
							ChatFragment.class);
					// 获取PendingIntent,点击时发送该Intent
					PendingIntent pIntent = PendingIntent.getActivity(
							TabMainActivity.this, 0, intent, 0);
					notify.flags |= Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
					notify.defaults = Notification.DEFAULT_ALL; // 把所有的属性设置成默认
					// 设置通知的标题和内容
					notify.setLatestEventInfo(TabMainActivity.this, fromCustomUserID, text,
							pIntent);
					// 发出通知
					manager.notify(ID, notify);
					
					//保存到数据库,fromCustomUserID应为邮箱
					dbHelper=new MyOpenHelper(TabMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
					db =dbHelper.getWritableDatabase();
					dbHelper.saveChatMsg(db,fromCustomUserID,
							openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
							text,
							serverActionTime
							,0,0);
				}
//				
			}

			// 监听系统消息
			@Override
			public void onReceiveSystemText(String text, long serverActionTime) {

				Toast.makeText(TabMainActivity.this,"onReceiveSystemText\n"+ text, Toast.LENGTH_SHORT).show();
				
			}
		});

	}

	/**
	 * //出册登陆初始化
	 */
	private void loginINIT() {
		//注册
		IMMyself.register(5, new OnActionListener() {// // 设置超时时长为5秒
					@Override
					public void onSuccess() {
						Log.d("logd", "IM-注册成功");
					}

					@Override
					public void onFailure(String arg0) {

						if (arg0.equals("CustomUserID Already Exist")) {
							// 设置超时时长为5秒
							IMMyself.login(false, 5, new OnActionListener() {
							    @Override
							    public void onSuccess() {
							    	Log.d("logd", "IM-登陆成功");
							    }
							 
							    @Override
							    public void onFailure(String error) {
							        if (error.equals("Timeout")) {
							            error = "登录超时";
							        } else if (error.equals("Wrong Password")) {
							            error = "密码错误";
							        }
							 
							        Log.d("logd", "登录错误："+error.toString());
							    }
							});
						}else {
							 Log.d("logd", "onFailure注册失败："+arg0.toString());
						}
						
					}
				});
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_tab_one:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab1");
				one.setTextColor(Color.rgb(95, 158, 160));
				one.setCompoundDrawables(null, drawableOne2, null, null);// 按下的效果
				two.setTextColor(Color.rgb(124, 124, 124));
				two.setCompoundDrawables(null, drawableTwo, null, null);// 按下的效果
				three.setTextColor(Color.rgb(124, 124, 124));
				three.setCompoundDrawables(null, drawableThree, null, null);// 按下的效果
				four.setTextColor(Color.rgb(124, 124, 124));
				four.setCompoundDrawables(null, drawableFour, null, null);// 按下的效果
				clickId = v.getId();
			}
			break;

		case R.id.tv_tab_two:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab2");
				two.setTextColor(Color.rgb(95, 158, 160));
				two.setCompoundDrawables(null, drawableTwo2, null, null);// 按下的效果
				one.setTextColor(Color.rgb(124, 124, 124));
				one.setCompoundDrawables(null, drawableOne, null, null);// 按下的效果
				three.setTextColor(Color.rgb(124, 124, 124));
				three.setCompoundDrawables(null, drawableThree, null, null);// 按下的效果
				four.setTextColor(Color.rgb(124, 124, 124));
				four.setCompoundDrawables(null, drawableFour, null, null);// 按下的效果
				clickId = v.getId();
			}
			break;
		case R.id.tv_tab_three:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab3");
				three.setTextColor(Color.rgb(95, 158, 160));
				three.setCompoundDrawables(null, drawableThree2, null, null);// 按下的效果
				one.setTextColor(Color.rgb(124, 124, 124));
				one.setCompoundDrawables(null, drawableOne, null, null);// 按下的效果
				two.setTextColor(Color.rgb(124, 124, 124));
				two.setCompoundDrawables(null, drawableTwo, null, null);// 按下的效果
				four.setTextColor(Color.rgb(124, 124, 124));
				four.setCompoundDrawables(null, drawableFour, null, null);// 按下的效果
				clickId = v.getId();
			}
			break;
		case R.id.tv_tab_four:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab4");
				four.setTextColor(Color.rgb(95, 158, 160));
				four.setCompoundDrawables(null, drawableFour2, null, null);// 按下的效果
				one.setTextColor(Color.rgb(124, 124, 124));
				one.setCompoundDrawables(null, drawableOne, null, null);// 按下的效果
				two.setTextColor(Color.rgb(124, 124, 124));
				two.setCompoundDrawables(null, drawableTwo, null, null);// 按下的效果
				three.setTextColor(Color.rgb(124, 124, 124));
				three.setCompoundDrawables(null, drawableThree, null, null);// 按下的效果
				clickId = v.getId();
			}
			break;

		case R.id.add_new_shutie://发布新书贴
			Intent intent =new Intent(TabMainActivity.this,AddNewTextActivity.class);
			startActivity(intent);
			pop.dismiss();

			break;
		case R.id.add_new_liaotie://发布新聊贴
			Intent intent2 =new Intent(TabMainActivity.this,AddNewLiaotieActivity.class);
			startActivity(intent2);
			pop.dismiss();
			break;
		case R.id.cancel_btn:
			pop.dismiss();
			break;

		default:
			break;
		}

	}
	
	

}
