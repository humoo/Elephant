package com.example.elephant.im;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;

import com.example.elephant.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewFriendActivity extends Activity{
	
	TextView tv_confirm_add_new_friend;
	ImageView iv_back_title;
	EditText ed_add_new_friend;//添加好友请求留言
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_add_new_friend_act);
		
		tv_confirm_add_new_friend=(TextView) findViewById(R.id.tv_confirm_add_new_friend);
		iv_back_title=(ImageView) findViewById(R.id.iv_back_title);
		ed_add_new_friend=(EditText) findViewById(R.id.ed_add_new_friend);
		
		//点击退出
		iv_back_title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {finish();}
		});
		
		//点击发送好友请求
		tv_confirm_add_new_friend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// 动态配置超时时长
				IMMyself.sendText(ed_add_new_friend.getText().toString(),getIntent().getStringExtra("username") , (ed_add_new_friend.getText().toString().length() / 400 + 1) * 5, new OnActionListener() {
				    @Override
				    public void onSuccess() {
				        Toast.makeText(AddNewFriendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				        finish();
				    }
				 
				    @Override
				    public void onFailure(String error) {
				        Toast.makeText(AddNewFriendActivity.this, "发送失败 -- " + error, Toast.LENGTH_SHORT).show();
				    }
				});
			}
		});
		
		
	}

}
