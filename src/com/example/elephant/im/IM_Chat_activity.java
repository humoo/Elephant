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
		// �����㲥������
	
		
		
		
		//����˳�
		iv_back_title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//����л�ҳ��
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
		//�ѵ�һ��tab��Ϊѡ��״̬
		mTabs[0].setSelected(true);
	}
	
	private void initTab(){
		friendsFragment = new FriendsFragment();
		chatFragment = new ChatFragment();
		fragments = new Fragment[] {chatFragment, friendsFragment};
		// �����ʾ��һ��fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatFragment).
			add(R.id.fragment_container, friendsFragment).hide(friendsFragment).show(chatFragment).commit();
		

	}

	
	
	
	
}
