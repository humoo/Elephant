package com.example.elephant;

import com.example.elephant.db.UserName;

import cn.bmob.v3.listener.UpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeSubjectActivity extends Activity {

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

		tv_change_activity_title.setText("修改专业");
		ed_new_significance.setText(sp.getString("subject", "sp出错"));
		tv_change_activity_text.setText("请输入新的专业名称");
		
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
				
				if (ed_new_significance.getText().toString().split(" ").length!=1) {
					Toast.makeText(ChangeSubjectActivity.this, "不允许有空格", 1).show();
				}else if (ed_new_significance.getText().toString().equals("")) {
					Toast.makeText(ChangeSubjectActivity.this, "你还没输入", 1).show();
				}else {
					UserName userName=new UserName();
					userName.setSubject(ed_new_significance.getText().toString());
					userName.update(ChangeSubjectActivity.this, sp.getString("userObjectId", ""), new UpdateListener() {	
						
						@Override
						public void onSuccess() {
							
							Editor editor=sp.edit();
							editor.putString("subject", ed_new_significance.getText().toString());
							editor.commit();
							
							Intent intent=new Intent();
							intent.putExtra("subject", ed_new_significance.getText().toString());//最后数据没用上，传递作用
							setResult(3, intent);
							finish();
							
						}
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(ChangeSubjectActivity.this, "修改失败", 1).show();

						}
					});
				}
				
				
			}
		});
		
	}
}
