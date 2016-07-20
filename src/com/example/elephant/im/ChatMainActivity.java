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
	public static boolean isShow=false;//��TabMainActivity����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat_main_act);
		
		init();
		
		isShow=true;
		
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(this);
		dbHelper=new MyOpenHelper(ChatMainActivity.this, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
		db =dbHelper.getWritableDatabase();
		
		
		
		//������
		adapter=new ShowChatMsgAdapter(this,getIntent().getStringExtra("username"));//usernameӦΪ����
		lv_show_chat_msg.setAdapter(adapter);
		lv_show_chat_msg.setSelection(ShowChatMsgAdapter.list.size()-1);
	}

	private void init() {
		
		tv_send_btn=(TextView) findViewById(R.id.tv_send_btn);
		tv_account_name=(TextView) findViewById(R.id.tv_account_name);//�������û�����
		ed_send_text=(EditText) findViewById(R.id.ed_send_text);
		iv_back_title=(ImageView) findViewById(R.id.iv_back_title);
		iv_account=(ImageView) findViewById(R.id.iv_account);//��ϵ����ϸ��Ϣ
		lv_show_chat_msg=(ListView) findViewById(R.id.lv_show_chat_msg);
		
		//�����ʻ���
	    tv_account_name.setText(getIntent().getStringExtra("username"));//ӦΪ�ǳƣ��˴�Ϊ����
		
		//����˳�
		iv_back_title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitSetting();
				finish();
			}
		});
		
		//����鿴��ϵ����ϸ��Ϣ
		iv_account.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatMainActivity.this, InfoIconActivity.class);
				intent.putExtra("username", getIntent().getStringExtra("username"));
//				intent.putExtra("significance", "����");
				startActivity(intent);				
			}
		});
		
		//���������Ϣ
		tv_send_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendText();				
			}
		});
	}

	
	/**
	 * ������Ϣ�������浽�������ݿ�
	 */
	@SuppressWarnings("static-access")
	protected void sendText() {
		
		if (!ed_send_text.getText().toString().trim().equals("")) {
			
			String time=System.currentTimeMillis()+"";
			Long longTime=Long.parseLong(time.substring(0, 10));
			
			//�ڶ�������getIntent().getStringExtra("username")���޸�
			dbHelper.saveChatMsg(db,getIntent().getStringExtra("username"),
					openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""),
					ed_send_text.getText().toString().trim(),
					longTime
					,1,1);
			/*
			 * ���¼�������
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
					Toast.makeText(ChatMainActivity.this, "����ʧ��"+arg0, 1).show();
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
	 * �˳�ʱ����
	 */
	private void exitSetting() {
		ShowChatMsgAdapter.list.clear();
		//����������촰�ڣ���С�������
		ContentValues recentValues=new ContentValues();
		recentValues.put("unread", 0);	
		//fromeamilӦ����getIntent().getStringExtra("username")
		db.update("recent", recentValues, "fromeamil=?", new String[]{ getIntent().getStringExtra("username") });
		isShow=false;
	}

}
