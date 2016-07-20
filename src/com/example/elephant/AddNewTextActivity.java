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
					Toast.makeText(AddNewTextActivity.this, "��������Ϊ��", 1).show();			
				}else if (editText.getText().toString().trim().equals("")) {
					Toast.makeText(AddNewTextActivity.this, "�����Խ��ܼ����~", 1).show();			

				}else if (sp.getBoolean("islogin", false)) {//�ѵ�¼
					sendMyNewText();				
					finish();
				}else {
					Toast.makeText(AddNewTextActivity.this, "�㻹û��¼��", 1).show();			
				}				
			}			
		});
		
		
		/**
		 * ���ͼƬ
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
	 * �ύ�·�������
	 */
	private void sendMyNewText() {

		final String bookname,school;
		final String bookisbn;
		final String bookauthor, bookpublisher, significance;
		final String viewcount=""+0;//�û�������ʼ��Ϊ0�����
		bookname=editTextName.getText().toString().trim();
		bookisbn=editTextISBN.getText().toString().trim();
		bookauthor=editTextAuthor.getText().toString();
		bookpublisher=editTextPunisher.getText().toString();
		significance=sp.getString("significance",".#");//Ĭ��ǩ��Ϊ.#
		MainContent mainContent=new MainContent();
		
		mainContent.setUsercontent(editText.getText().toString());//��������
		mainContent.setUsername(sp.getString("accountname", "sp����"));//�����û���		
		mainContent.setSignificance(significance);//����ǩ��	
		mainContent.setBookname(bookname);//��������
		mainContent.setViewcount(viewcount+"");//�û�����
		mainContent.setCommentcount(viewcount+"");
		mainContent.setSchool(sp.getString("school", ""));
		
		if (bookisbn.equals("")) {
			mainContent.setBookisbn(bookisbn+".#");//����ISBN
		}else {
			mainContent.setBookisbn(bookisbn);//����ISBN
		}		
		if (bookauthor.equals("")) {
			mainContent.setBookauthor(bookauthor+".#");//��������
		}else {
			mainContent.setBookauthor(bookauthor);//��������
		}
		if (bookpublisher.equals("")) {
			mainContent.setBookpublisher(bookpublisher+".#");//���ó�����		
		}else {
			mainContent.setBookpublisher(bookpublisher);//���ó�����		
		}
		
		
		mainContent.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				Toast.makeText(AddNewTextActivity.this, "�����ɹ�", 1).show();	
				
				//ˢ������
				new DataFromWebMainUI(AddNewTextActivity.this).flushDataFromWeb();


	                        
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(AddNewTextActivity.this, "����ʧ��", 1).show();				
			}
		});
	
		
		
		
	}
}
