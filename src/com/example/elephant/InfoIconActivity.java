package com.example.elephant;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.db.UserName;
import com.example.elephant.im.AddNewFriendActivity;
import com.example.elephant.im.ChatMainActivity;
import com.example.elephant.utils.ImageHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoIconActivity extends Activity {

	String username,significance;
	TextView tv_userName,tv_significance,tv_add_new_friend,tv_send_msg;
	ImageView iv_info_img,iv_back;
	TextView tv_show_school,tv_show_year,tv_show_subject,tv_show_gender;
	
	String webSchool,webmainJob,webYear,webSubject,webGender;
	
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==0x001) {			
				tv_show_school.setText(webSchool+"-"+webmainJob);
				tv_show_year.setText(webYear);
				tv_show_subject.setText(webSubject);
				tv_show_gender.setText(webGender);
			
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_icon_activity);
		
		init();
		
		queryFromWeb();
		
		//设置头像
		File picFile=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+username+"_accountImg.jpg");
		if (picFile.exists()) {
			iv_info_img.setImageBitmap(ImageHelper.toRoundBitmap(BitmapFactory.decodeFile(picFile.getAbsolutePath())));
		}
		
		//后退
		iv_back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {finish();}
		});
		//发送消息
		tv_send_msg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(InfoIconActivity.this,ChatMainActivity.class);
				intent.putExtra("username", username);//用户名
				startActivity(intent);	
				finish();
			}
		});
		//添加好友请求
		tv_add_new_friend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(InfoIconActivity.this,AddNewFriendActivity.class);
				intent.putExtra("username", getIntent().getStringExtra("username"));//用户名
				startActivity(intent);
			}
		});
	}

	private void init() {
		
		Intent intent=getIntent();
		username=intent.getStringExtra("username");//用户名
		significance=intent.getStringExtra("significance");//签名
		
		tv_userName=(TextView) findViewById(R.id.tv_info_name);
		tv_significance=(TextView) findViewById(R.id.tv_info_significance);
		tv_add_new_friend=(TextView) findViewById(R.id.tv_add_new_friend);
		tv_send_msg=(TextView) findViewById(R.id.tv_send_msg);
		iv_info_img=(ImageView) findViewById(R.id.iv_info_img);
		iv_back=(ImageView) findViewById(R.id.iv_back_title);
		
		tv_userName.setText(username);
		tv_significance.setText(significance);
		
		//详细信息初始化
		tv_show_school=(TextView) findViewById(R.id.tv_show_school);
		tv_show_year=(TextView) findViewById(R.id.tv_show_year);
		tv_show_subject=(TextView) findViewById(R.id.tv_show_subject);
		tv_show_gender=(TextView) findViewById(R.id.tv_show_gender);
		
	}

	/**
	 * 从网上获取学校院系入学年份专业性别等信息
	 */
	private void queryFromWeb() {
		
		BmobQuery query=new BmobQuery("UserName");
		query.addWhereEqualTo("username", username);
		query.findObjects(InfoIconActivity.this, new FindCallback() {		
			@Override
			public void onFailure(int arg0, String arg1) {				
			}		
			@Override
			public void onSuccess(JSONArray arg0) {
				
				if (arg0.length()>0) {
					try {
						JSONObject iJsonObject=arg0.getJSONObject(0);
						webSchool=iJsonObject.getString("school");
						webmainJob=iJsonObject.getString("mainjob");
						webYear=iJsonObject.getString("year");
						webSubject=iJsonObject.getString("subject");
						webGender=iJsonObject.getString("gender");
						
						Message message=new Message();
						message.what=0x001;
						handler.sendMessage(message);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					
					
				}
				
			}
		});
	}
	
	
}
