package com.example.elephant.im;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.example.elephant.R;

public class IM_Chat_activity extends FragmentActivity{

	private ImageView[] mTabs;
	private FriendsFragment friendsFragment;
	private ChatFragment chatFragment;
	private Fragment[] fragments;
	ImageView iv_back_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.im_chat_activity);
		
		initView();
		initTab();
		// 开启广播接收器
	
		
		
		
		//点击退出
		iv_back_title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//点击切换页面
		mTabs[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getSupportFragmentManager().beginTransaction().show(fragments[0]).hide(fragments[1]).commit();
				
			}
		});
		mTabs[1].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getSupportFragmentManager().beginTransaction().show(fragments[1]).hide(fragments[0]).commit();

			}
		});
	}
	
	private void initView(){
		mTabs = new ImageView[2];
		mTabs[0] = (ImageView) findViewById(R.id.im_id_chat);
		mTabs[1] = (ImageView) findViewById(R.id.im_id_friends);
		iv_back_title=(ImageView) findViewById(R.id.iv_back_title);
		//把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}
	
	private void initTab(){
		friendsFragment = new FriendsFragment();
		chatFragment = new ChatFragment();
		fragments = new Fragment[] {chatFragment, friendsFragment};
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatFragment).
			add(R.id.fragment_container, friendsFragment).hide(friendsFragment).show(chatFragment).commit();
		

	}

	
	
	
	
}
