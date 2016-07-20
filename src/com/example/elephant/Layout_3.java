package com.example.elephant;

import com.example.elephant.im.IM_Chat_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Layout_3 extends Activity implements OnClickListener{

	TextView tv_im_main;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_3);
		
		init();
		
	}
	
	
	//³õÊ¼»¯¿Ø¼þ
	private void init() {
		tv_im_main=(TextView) findViewById(R.id.tv_im_main);
		tv_im_main.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.tv_im_main:
			startActivity(new Intent(Layout_3.this,IM_Chat_activity.class));
			break;

		default:
			break;
		}
		
	}
}
