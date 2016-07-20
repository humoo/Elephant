package com.example.elephant;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.elephant.db.BookComment;
import com.example.elephant.db.MainContent;
import com.example.elephant.db.UserName;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ChangeSignificanceActivity extends Activity{

	ImageView back,ok;
	EditText newSignificance;
	SharedPreferences sp;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_significance_activity);
		
		sp=getSharedPreferences("config", MODE_PRIVATE);
		newSignificance=(EditText) findViewById(R.id.ed_new_significance);
		newSignificance.setText(sp.getString("significance", ""));
		
		context=this;
		
		//按下提交按钮
		ok=(ImageView) findViewById(R.id.iv_ok_title);
		ok.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {			
				//查询UserName表
				BmobQuery query =new BmobQuery("UserName");
				System.out.println(sp.getString("accountname",""));
				query.addWhereEqualTo("username", sp.getString("accountname",""));				
				query.findObjects(context, new FindCallback() {			
					@Override
					public void onFailure(int arg0, String arg1) {
						Log.d("ChangeSignificanceActivity", "查询UserName表错误");
					}				
					@Override
					public void onSuccess(JSONArray arg0) {						
						try {
							JSONObject iObject=arg0.getJSONObject(0);
							String objectId=iObject.getString("objectId");				
							//更新UserName表的签名列
							UserName userName=new UserName();
							userName.setSignificance(newSignificance.getText().toString());			
							userName.update(context, objectId, new UpdateListener() {				
								@Override
								public void onSuccess() {					
									Log.d("ChangeSignificanceActivity", "更新UserName表成功");	
								}		
								@Override
								public void onFailure(int arg0, String arg1) {	
									Log.d("ChangeSignificanceActivity", "更新UserName表错误");
								}
							});						
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
				});
				
				//查询MainContent表
				BmobQuery query1 =new BmobQuery("MainContent");
				query1.addWhereEqualTo("username", sp.getString("accountname",""));				
				query1.findObjects(context, new FindCallback() {	
					@Override
					public void onFailure(int arg0, String arg1) {
						Log.d("ChangeSignificanceActivity", "查询MainContent表错误");				
					}
					
					@Override
					public void onSuccess(JSONArray arg0) {
						
						for (int i = 0; i < arg0.length(); i++) {
							JSONObject iObject;
							try {
								iObject = arg0.getJSONObject(i);
								String objectId=iObject.getString("objectId");	
								//更新UserName表的签名列
								MainContent mainContent=new MainContent();
								mainContent.setSignificance(newSignificance.getText().toString());
								mainContent.update(context, objectId, new UpdateListener() {
									@Override
									public void onSuccess() {					
										Log.d("ChangeSignificanceActivity", "更新MainContent表成功");	
									}		
									@Override
									public void onFailure(int arg0, String arg1) {	
										Log.d("ChangeSignificanceActivity", "更新MainContent表错误");
									}
								});							
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
							
						}
					}
				});
				
				//查询BookComment表
				BmobQuery query2 =new BmobQuery("BookComment");
				query2.addWhereEqualTo("username", sp.getString("accountname",""));				
				query2.findObjects(context, new FindCallback() {	
					@Override
					public void onFailure(int arg0, String arg1) {
						Log.d("ChangeSignificanceActivity", "查询BookComment表错误");				
					}
					
					@Override
					public void onSuccess(JSONArray arg0) {
						
						for (int i = 0; i < arg0.length(); i++) {
							JSONObject iObject;
							try {
								iObject = arg0.getJSONObject(i);
								String objectId=iObject.getString("objectId");	
								//更新UserName表的签名列
								BookComment bookComment=new BookComment();
								bookComment.setSignificance(newSignificance.getText().toString());
								bookComment.update(context, objectId, new UpdateListener() {
									@Override
									public void onSuccess() {					
										Log.d("ChangeSignificanceActivity", "更新BookComment表成功");	
									}		
									@Override
									public void onFailure(int arg0, String arg1) {	
										Log.d("ChangeSignificanceActivity", "更新BookComment表错误");
									}
								});							
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
							
						}
						
					}
				});
				Intent intentData=new Intent();
				intentData.putExtra("significance", newSignificance.getText().toString());
				setResult(1,intentData);
				Editor editor=sp.edit();
				editor.putString("significance", newSignificance.getText().toString());
				editor.commit();
				
				finish();
			}
		});
		//按下后退键退出
		back=(ImageView) findViewById(R.id.iv_back_title);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
}
