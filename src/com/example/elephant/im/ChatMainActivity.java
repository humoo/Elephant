package com.example.elephant.im;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;

import com.example.elephant.InfoIconActivity;
import com.example.elephant.R;
import com.example.elephant.TabMainActivity;
import com.example.elephant.db.MyOpenHelper;
import com.example.elephant.im.adapter.ShowChatMsgAdapter;
import com.example.elephant.utils.OpenSharePrefrenceHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatMainActivity extends Activity{
	
	EditText ed_send_text;
	TextView tv_send_btn,tv_account_name;
	ImageView iv_back_title,iv_account;
	MyOpenHelper dbHelper;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	SQLiteDatabase db;
	public static ListView lv_show_chat_msg;
	public static ShowChatMsgAdapter adapter;
	public static boolean isShow=false;//给TabMainActivity调用
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat_main_act);
		
		init();
		
		isShow=true;
		
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(this);
		dbHelper=new MyOpenHelper(ChatMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
		db =dbHelper.getWritableDatabase();
		
		
		
		//适配器
		adapter=new ShowChatMsgAdapter(this,getIntent().getStringExtra("username"));//username应为邮箱
		lv_show_chat_msg.setAdapter(adapter);
		lv_show_chat_msg.setSelection(ShowChatMsgAdapter.list.size()-1);
	}

	private void init() {
		
		tv_send_btn=(TextView) findViewById(R.id.tv_send_btn);
		tv_account_name=(TextView) findViewById(R.id.tv_account_name);//标题栏用户名称
		ed_send_text=(EditText) findViewById(R.id.ed_send_text);
		iv_back_title=(ImageView) findViewById(R.id.iv_back_title);
		iv_account=(ImageView) findViewById(R.id.iv_account);//联系人详细信息
		lv_show_chat_msg=(ListView) findViewById(R.id.lv_show_chat_msg);
		
		//设置帐户名
	    tv_account_name.setText(getIntent().getStringExtra("username"));//应为昵称，此处为邮箱
		
		//点击退出
		iv_back_title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitSetting();
				finish();
			}
		});
		
		//点击查看联系人详细信息
		iv_account.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatMainActivity.this, InfoIconActivity.class);
				intent.putExtra("username", getIntent().getStringExtra("username"));
//				intent.putExtra("significance", "待定");
				startActivity(intent);				
			}
		});
		
		//点击发送消息
		tv_send_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendText();				
			}
		});
	}

	
	/**
	 * 发送消息，并保存到本地数据库
	 */
	@SuppressWarnings("static-access")
	protected void sendText() {
		
		if (!ed_send_text.getText().toString().trim().equals("")) {
			
			String time=System.currentTimeMillis()+"";
			Long longTime=Long.parseLong(time.substring(0, 10));
			
			//第二个参数getIntent().getStringExtra("username")待修改
			dbHelper.saveChatMsg(db,getIntent().getStringExtra("username"),
					openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
					ed_send_text.getText().toString().trim(),
					longTime
					,1,1);
			/*
			 * 重新加载数据
			 */
			ShowChatMsgAdapter.list.clear();
			ShowChatMsgAdapter.setData();
			adapter.notifyDataSetChanged();
			lv_show_chat_msg.setSelection(ShowChatMsgAdapter.list.size()-1);
			
			IMMyself.sendText(ed_send_text.getText().toString(), getIntent().getStringExtra("username"), 0, new OnActionListener() {
				@Override
				public void onSuccess() {
					
				}
				
				@Override
				public void onFailure(String arg0) {
					Toast.makeText(ChatMainActivity.this, "发送失败"+arg0, 1).show();
				}
			});
			
			ed_send_text.setText("");
			
		}
		
	}
	
	
	@Override
	public void onBackPressed() {
		exitSetting();
		super.onBackPressed();
	}

	/**
	 * 退出时设置
	 */
	private void exitSetting() {
		ShowChatMsgAdapter.list.clear();
		//点击进入聊天窗口，将小红点消除
		ContentValues recentValues=new ContentValues();
		recentValues.put("unread", 0);	
		//fromeamil应等于getIntent().getStringExtra("username")
		db.update("recent", recentValues, "fromeamil=?", new String[]{ getIntent().getStringExtra("username") });
		isShow=false;
	}

}
