package com.example.elephant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.example.elephant.db.MainContent;

public class AddNewTextActivity extends Activity{

	ImageView back,ok,iv_add_pic;
	EditText editTextName,editTextISBN,editTextAuthor,editTextPunisher,editText;
	SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_activity);
		
		back=(ImageView) findViewById(R.id.iv_back_title_back);
		ok=(ImageView) findViewById(R.id.iv_back_title_ok);
		iv_add_pic=(ImageView) findViewById(R.id.iv_add_pic);
		editTextName=(EditText) findViewById(R.id.et_addnew_textName);
		editTextISBN=(EditText) findViewById(R.id.et_addnew_textISBN);
		editTextAuthor=(EditText) findViewById(R.id.et_addnew_textAuthor);
		editTextPunisher=(EditText) findViewById(R.id.et_addnew_textPublisher);
		editText=(EditText) findViewById(R.id.et_addnew_text);
		
		sp=getSharedPreferences("config",MODE_PRIVATE);
		
		back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ok.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (editTextName.getText().toString().trim().equals("")) {
					Toast.makeText(AddNewTextActivity.this, "书名不能为空", 1).show();			
				}else if (editText.getText().toString().trim().equals("")) {
					Toast.makeText(AddNewTextActivity.this, "请留言介绍几句吧~", 1).show();			

				}else if (sp.getBoolean("islogin", false)) {//已登录
					sendMyNewText();				
					finish();
				}else {
					Toast.makeText(AddNewTextActivity.this, "你还没登录呢", 1).show();			
				}				
			}			
		});
		
		
		/**
		 * 添加图片
		 */
		iv_add_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, 0x0001);
				
			}
		});
	}
	/**
	 * 提交新发布内容
	 */
	private void sendMyNewText() {

		final String bookname,school;
		final String bookisbn;
		final String bookauthor, bookpublisher, significance;
		final String viewcount=""+0;//用户流量初始化为0浏览量
		bookname=editTextName.getText().toString().trim();
		bookisbn=editTextISBN.getText().toString().trim();
		bookauthor=editTextAuthor.getText().toString();
		bookpublisher=editTextPunisher.getText().toString();
		significance=sp.getString("significance",".#");//默认签名为.#
		MainContent mainContent=new MainContent();
		
		mainContent.setUsercontent(editText.getText().toString());//设置内容
		mainContent.setUsername(sp.getString("accountname", "sp出错"));//设置用户名		
		mainContent.setSignificance(significance);//设置签名	
		mainContent.setBookname(bookname);//设置书名
		mainContent.setViewcount(viewcount+"");//用户流量
		mainContent.setCommentcount(viewcount+"");
		mainContent.setSchool(sp.getString("school", ""));
		
		if (bookisbn.equals("")) {
			mainContent.setBookisbn(bookisbn+".#");//设置ISBN
		}else {
			mainContent.setBookisbn(bookisbn);//设置ISBN
		}		
		if (bookauthor.equals("")) {
			mainContent.setBookauthor(bookauthor+".#");//设置作者
		}else {
			mainContent.setBookauthor(bookauthor);//设置作者
		}
		if (bookpublisher.equals("")) {
			mainContent.setBookpublisher(bookpublisher+".#");//设置出版社		
		}else {
			mainContent.setBookpublisher(bookpublisher);//设置出版社		
		}
		
		
		mainContent.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				Toast.makeText(AddNewTextActivity.this, "发布成功", 1).show();	
				
				//刷新数据
				new DataFromWebMainUI(AddNewTextActivity.this).flushDataFromWeb();


	                        
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(AddNewTextActivity.this, "发布失败", 1).show();				
			}
		});
	
		
		
		
	}
}
