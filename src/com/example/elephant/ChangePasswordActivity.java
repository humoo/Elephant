package com.example.elephant;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {

	TextView tv_change_activity_title,tv_change_activity_text;//改变布局标题，改变底层文字
	ImageView iv_back_title,iv_ok_title;//后退，提交
	EditText ed_new_significance;//修改新的昵称
	
	SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_significance_activity);
		
		sp=getSharedPreferences("config",MODE_PRIVATE);
		
		tv_change_activity_title=(TextView) findViewById(R.id.tv_change_activity_title);
		tv_change_activity_text=(TextView) findViewById(R.id.tv_change_activity_text);		
		iv_back_title=(ImageView) findViewById(R.id.iv_back_title);
		iv_ok_title=(ImageView) findViewById(R.id.iv_ok_title);		
		ed_new_significance=(EditText) findViewById(R.id.ed_new_significance);

		tv_change_activity_title.setText("修改密码");
		ed_new_significance.setText("");
		tv_change_activity_text.setText("请输入注册邮箱");
		
		iv_back_title.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//提交新昵称
		iv_ok_title.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				if (!ed_new_significance.getText().toString().trim().matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
					Toast.makeText(ChangePasswordActivity.this, "邮箱格式错误", 1).show();
				}else {
					Toast.makeText(ChangePasswordActivity.this, "密码已发送至邮箱", 1).show();
					finish();					
				}
				
			}
		});
		
	}
}
