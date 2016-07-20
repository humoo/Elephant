package com.example.elephant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener{
	
	ImageView back;
	TextView loginOut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_acti);
		
		back=(ImageView) findViewById(R.id.iv_back_title);
		loginOut=(TextView) findViewById(R.id.tv_login_out);
		
		back.setOnClickListener(this);
		loginOut.setOnClickListener(this);
		
		
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back_title://后退
			finish();
			break;
		case R.id.tv_login_out://登出
			SharedPreferences.Editor editor=getSharedPreferences("config",MODE_PRIVATE).edit();
			editor.putBoolean("islogin", false);
			editor.commit();	
			Intent intent =new Intent();
			setResult(2,intent);
			Toast.makeText(SettingActivity.this, "登出成功", 1).show();
			finish();
			break;

		default:
			break;
		}
		
	}
}
