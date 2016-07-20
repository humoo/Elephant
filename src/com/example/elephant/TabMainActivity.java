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
	PopupWindow pop;// ���ϵ�������
	OpenSharePrefrenceHelper openSharePrefrenceHelper;

	NotificationManager manager;
	Notification notify = new Notification();  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);

		// //����ʽ״̬��
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

		// ��ȡ��activity���TabHost���
		// TabHost tabHost=getTabHost();

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());
		// ��ȡ������ť�ؼ�
		TabWidget tabWidget = tabHost.getTabWidget();
		addNew = (ImageView) findViewById(R.id.add_new);

		// ����Tab��ҳ��
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("����")
				.setContent(new Intent(this, MainUIActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("����")
				.setContent(new Intent(this, Layout_2.class)));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("���")
				.setContent(new Intent(this, Layout_3.class)));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("�˺�")
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
		// ��ʼ��������ͼ��
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
		 * �ֲ�ҳ������
		 */
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);

		/**
		 * ���ϵ�����������Ϣ�Ի���
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
		pop.setFocusable(true);// ���back�˳�pop
		pop.setAnimationStyle(R.style.add_new_style);
		pop.setBackgroundDrawable(new ColorDrawable(0x33ff0000));// ���ñ���͸�������back�˳�pop
		addNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (pop.isShowing()) {
					pop.dismiss();
				} else {
					pop.showAtLocation(view, Gravity.BOTTOM, 0, -560);//�ڸ��ؼ��·�����
					// pop.showAsDropDown(view);
				}

			}
		});

		// ������ɫ
		// for (int i = 0; i< tabWidget.getChildCount(); i++) {
		// TextView tv=((TextView)
		// tabWidget.getChildAt(i).findViewById(android.R.id.title));
		// tv.setTextColor(Color.rgb(95, 158,160));
		// tv.setTextSize(15);
		//
		// }
		
		
		/**
		 *  ��ʼ��IMSDK
		 */
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(this);
		// ��Application��onCreate()�����У�����applicationContext��appKey
		IMSDK.init(getApplicationContext(), "ac92c957619f334f9263d7e3");  
		// ���ñ��û����û���
		IMMyself.setCustomUserID(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""));	 
		// ���ñ��û�����
		IMMyself.setPassword(openSharePrefrenceHelper.getSP_CONFIG().getString("password", ""));
		// һ����¼
		IMMyself.login(false, 5, null);
		// �����ı���Ϣ
		IMMyself.sendText("Hello!", "lyc@imsdk.im", 5, null);
		//ע���½��ʼ��
		loginINIT();
		//������Ϣ
		receiveMsg();
		
		
		
		
		/**
		 * ImageLoader��ʼ��
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
		 * ֪ͨ��ʼ��
		 */
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
	}

	
	// Notification��ʾID  
    private static final int ID = 1;  
    private MyOpenHelper dbHelper;
    private SQLiteDatabase db ;
	/**
	 * ������Ϣ  From IM
	 */
	private void receiveMsg() {
		// ���ü�����
		IMMyself.setOnReceiveTextListener(new OnReceiveTextListener() {
			// �������������û����ı�ѶϢ
			@SuppressWarnings({ "deprecation", "static-access" })
			@Override
			public void onReceiveText(String text, String fromCustomUserID,
					long serverActionTime) {

				/**
				 * ���浽���ݿ�
				 */				
				//���ChatMainActivity��ջ���Ļ�
				if (IsOnTopTaskHelper.getTopActivityName(TabMainActivity.this).equals("ChatMainActivity")) {

					/**
					 * ���浽���ݿⲢ����UI
					 */
					//���浽���ݿ�,fromCustomUserIDӦΪ����
					dbHelper=new MyOpenHelper(TabMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
					db =dbHelper.getWritableDatabase();
					dbHelper.saveChatMsg(db,fromCustomUserID,
							openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
							text,
							serverActionTime
							,1,0);
					if (ChatMainActivity.isShow) {						
						/*
						 * ���¼�������
						 */
						ShowChatMsgAdapter.list.clear();
						ShowChatMsgAdapter.setData();
						ChatMainActivity.adapter.notifyDataSetChanged();
						ChatMainActivity.lv_show_chat_msg.setSelection(ShowChatMsgAdapter.list.size()-1);
					}
				}else if (IsOnTopTaskHelper.getTopActivityName(TabMainActivity.this).equals("IM_Chat_activity")) {

					/**
					 * ����֪ͨ�����浽���ݿⲢˢ��uiС���
					 */
					notify.icon = R.drawable.circle_head;
					notify.tickerText = text; // ��ʾ��״̬���е�����
					notify.when = System.currentTimeMillis(); // ������֪ͨʱ��ʱ��
					// ����֪ͨ�����ת��NotificationResult��
					Intent intent = new Intent(TabMainActivity.this,
							ChatFragment.class);
					// ��ȡPendingIntent,���ʱ���͸�Intent
					PendingIntent pIntent = PendingIntent.getActivity(
							TabMainActivity.this, 0, intent, 0);
					notify.flags |= Notification.FLAG_AUTO_CANCEL; // ��������ť����֪ͨ����Զ���ʧ
					notify.defaults = Notification.DEFAULT_ALL; // �����е��������ó�Ĭ��
					// ����֪ͨ�ı��������
					notify.setLatestEventInfo(TabMainActivity.this, fromCustomUserID, text,
							pIntent);
					// ����֪ͨ
					manager.notify(ID, notify);
					
					//���浽���ݿ�,fromCustomUserIDӦΪ����
					dbHelper=new MyOpenHelper(TabMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
					db =dbHelper.getWritableDatabase();
					dbHelper.saveChatMsg(db,fromCustomUserID,
							openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
							text,
							serverActionTime
							,0,0);
					
					ChatListViewAdapter.list.clear();
					ChatListViewAdapter.setListData();//���»�ȡ����
					ChatFragment.adapter.notifyDataSetChanged();
					
					
				}else {//������ջ��

					/**
					 * ����֪ͨ�����浽���ݿ�
					 */
					notify.icon = R.drawable.circle_head;
					notify.tickerText = text; // ��ʾ��״̬���е�����
					notify.when = System.currentTimeMillis(); // ������֪ͨʱ��ʱ��
					// ����֪ͨ�����ת��NotificationResult��
					Intent intent = new Intent(TabMainActivity.this,
							ChatFragment.class);
					// ��ȡPendingIntent,���ʱ���͸�Intent
					PendingIntent pIntent = PendingIntent.getActivity(
							TabMainActivity.this, 0, intent, 0);
					notify.flags |= Notification.FLAG_AUTO_CANCEL; // ��������ť����֪ͨ����Զ���ʧ
					notify.defaults = Notification.DEFAULT_ALL; // �����е��������ó�Ĭ��
					// ����֪ͨ�ı��������
					notify.setLatestEventInfo(TabMainActivity.this, fromCustomUserID, text,
							pIntent);
					// ����֪ͨ
					manager.notify(ID, notify);
					
					//���浽���ݿ�,fromCustomUserIDӦΪ����
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

			// ����ϵͳ��Ϣ
			@Override
			public void onReceiveSystemText(String text, long serverActionTime) {

				Toast.makeText(TabMainActivity.this,"onReceiveSystemText\n"+ text, Toast.LENGTH_SHORT).show();
				
			}
		});

	}

	/**
	 * //�����½��ʼ��
	 */
	private void loginINIT() {
		//ע��
		IMMyself.register(5, new OnActionListener() {// // ���ó�ʱʱ��Ϊ5��
					@Override
					public void onSuccess() {
						Log.d("logd", "IM-ע��ɹ�");
					}

					@Override
					public void onFailure(String arg0) {

						if (arg0.equals("CustomUserID Already Exist")) {
							// ���ó�ʱʱ��Ϊ5��
							IMMyself.login(false, 5, new OnActionListener() {
							    @Override
							    public void onSuccess() {
							    	Log.d("logd", "IM-��½�ɹ�");
							    }
							 
							    @Override
							    public void onFailure(String error) {
							        if (error.equals("Timeout")) {
							            error = "��¼��ʱ";
							        } else if (error.equals("Wrong Password")) {
							            error = "�������";
							        }
							 
							        Log.d("logd", "��¼����"+error.toString());
							    }
							});
						}else {
							 Log.d("logd", "onFailureע��ʧ�ܣ�"+arg0.toString());
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
				one.setCompoundDrawables(null, drawableOne2, null, null);// ���µ�Ч��
				two.setTextColor(Color.rgb(124, 124, 124));
				two.setCompoundDrawables(null, drawableTwo, null, null);// ���µ�Ч��
				three.setTextColor(Color.rgb(124, 124, 124));
				three.setCompoundDrawables(null, drawableThree, null, null);// ���µ�Ч��
				four.setTextColor(Color.rgb(124, 124, 124));
				four.setCompoundDrawables(null, drawableFour, null, null);// ���µ�Ч��
				clickId = v.getId();
			}
			break;

		case R.id.tv_tab_two:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab2");
				two.setTextColor(Color.rgb(95, 158, 160));
				two.setCompoundDrawables(null, drawableTwo2, null, null);// ���µ�Ч��
				one.setTextColor(Color.rgb(124, 124, 124));
				one.setCompoundDrawables(null, drawableOne, null, null);// ���µ�Ч��
				three.setTextColor(Color.rgb(124, 124, 124));
				three.setCompoundDrawables(null, drawableThree, null, null);// ���µ�Ч��
				four.setTextColor(Color.rgb(124, 124, 124));
				four.setCompoundDrawables(null, drawableFour, null, null);// ���µ�Ч��
				clickId = v.getId();
			}
			break;
		case R.id.tv_tab_three:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab3");
				three.setTextColor(Color.rgb(95, 158, 160));
				three.setCompoundDrawables(null, drawableThree2, null, null);// ���µ�Ч��
				one.setTextColor(Color.rgb(124, 124, 124));
				one.setCompoundDrawables(null, drawableOne, null, null);// ���µ�Ч��
				two.setTextColor(Color.rgb(124, 124, 124));
				two.setCompoundDrawables(null, drawableTwo, null, null);// ���µ�Ч��
				four.setTextColor(Color.rgb(124, 124, 124));
				four.setCompoundDrawables(null, drawableFour, null, null);// ���µ�Ч��
				clickId = v.getId();
			}
			break;
		case R.id.tv_tab_four:
			if (clickId == v.getId()) {

			} else {
				tabHost.setCurrentTabByTag("tab4");
				four.setTextColor(Color.rgb(95, 158, 160));
				four.setCompoundDrawables(null, drawableFour2, null, null);// ���µ�Ч��
				one.setTextColor(Color.rgb(124, 124, 124));
				one.setCompoundDrawables(null, drawableOne, null, null);// ���µ�Ч��
				two.setTextColor(Color.rgb(124, 124, 124));
				two.setCompoundDrawables(null, drawableTwo, null, null);// ���µ�Ч��
				three.setTextColor(Color.rgb(124, 124, 124));
				three.setCompoundDrawables(null, drawableThree, null, null);// ���µ�Ч��
				clickId = v.getId();
			}
			break;

		case R.id.add_new_shutie://����������
			Intent intent =new Intent(TabMainActivity.this,AddNewTextActivity.class);
			startActivity(intent);
			pop.dismiss();

			break;
		case R.id.add_new_liaotie://����������
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
