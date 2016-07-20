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

	TextView tv_change_activity_title,tv_change_activity_text;//�ı䲼�ֱ��⣬�ı�ײ�����
	ImageView iv_back_title,iv_ok_title;//���ˣ��ύ
	EditText ed_new_significance;//�޸��µ��ǳ�
	
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

		tv_change_activity_title.setText("�޸�����");
		ed_new_significance.setText("");
		tv_change_activity_text.setText("������ע������");
		
		iv_back_title.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//�ύ���ǳ�
		iv_ok_title.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				if (!ed_new_significance.getText().toString().trim().matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
					Toast.makeText(ChangePasswordActivity.this, "�����ʽ����", 1).show();
				}else {
					Toast.makeText(ChangePasswordActivity.this, "�����ѷ���������", 1).show();
					finish();					
				}
				
			}
		});
		
	}
}
