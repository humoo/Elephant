package com.example.elephant;

import net.sf.json.util.NewBeanInstanceStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.elephant.db.AccountImg;
import com.example.elephant.db.BookComment;
import com.example.elephant.db.LiaoTie;
import com.example.elephant.db.LiaoTieComment;
import com.example.elephant.db.MainContent;
import com.example.elephant.db.UploadGridPics;
import com.example.elephant.db.UserName;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.UpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeNickNameActivity extends Activity {

	TextView tv_change_activity_title,tv_change_activity_text;//改变布局标题，改变底层文字
	ImageView iv_back_title,iv_ok_title;//后退，提交
	EditText ed_new_nickName;//修改新的昵称
	
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
		ed_new_nickName=(EditText) findViewById(R.id.ed_new_significance);

		tv_change_activity_title.setText("修改昵称");
		ed_new_nickName.setText(sp.getString("accountname", "sp出错"));
		tv_change_activity_text.setText("请输入新的昵称");
		
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
				
				
				if (ed_new_nickName.getText().toString().split(" ").length!=1) {
					Toast.makeText(ChangeNickNameActivity.this, "昵称不允许有空格", 1).show();
				}else if (ed_new_nickName.getText().toString().equals("")) {
					Toast.makeText(ChangeNickNameActivity.this, "你还没输入", 1).show();
				}else {
					
					//修改UserName表中昵称部分
					UserName userName=new UserName();
					userName.setUsername(ed_new_nickName.getText().toString());
					System.out.println(sp.getString("userObjectId", "")+"objectId");
					userName.update(ChangeNickNameActivity.this, sp.getString("userObjectId", ""), new UpdateListener() {	
						
						@Override
						public void onSuccess() {
														
						}
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(ChangeNickNameActivity.this, "修改失败", 1).show();

						}
					});
					
					//修改MainContent表中昵称部分
					final MainContent mainContent=new MainContent();
					BmobQuery queryContent=new BmobQuery("MainContent");
					queryContent.addWhereEqualTo("username", sp.getString("accountname", ""));
					queryContent.findObjects(getApplicationContext(), new FindCallback() {					
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(getApplicationContext(), "查找MainContent错误", 1).show();
						}					
						@Override
						public void onSuccess(JSONArray arg0) {
							JSONObject jsonObject;
							String objectId;
							if (arg0.length()!=0) {
								for (int i = 0; i < arg0.length(); i++) {
									try {
										jsonObject=arg0.getJSONObject(i);
										objectId=jsonObject.getString("objectId");
										
										mainContent.setUsername(ed_new_nickName.getText().toString());
										mainContent.update(getApplicationContext(), objectId, new UpdateListener() {
											@Override
											public void onSuccess() {		
											}
											@Override
											public void onFailure(int arg0, String arg1) {
												Toast.makeText(getApplicationContext(), "插入MainContent错误", 1).show();
											}
										});										
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
								}
							}
							
						}
					});
					
					//修改BookComment表中昵称部分
					final BookComment bookComment=new BookComment();
					BmobQuery queryComment=new BmobQuery("BookComment");
					queryComment.addWhereEqualTo("username", sp.getString("accountname", ""));
					queryComment.findObjects(getApplicationContext(), new FindCallback() {					
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(getApplicationContext(), "查找BookComment错误", 1).show();
						}					
						@Override
						public void onSuccess(JSONArray arg0) {
							JSONObject jsonObject;
							String objectId;
							if (arg0.length()!=0) {
								for (int i = 0; i < arg0.length(); i++) {
									try {
										jsonObject=arg0.getJSONObject(i);
										objectId=jsonObject.getString("objectId");
										
										bookComment.setUsername(ed_new_nickName.getText().toString());
										bookComment.update(ChangeNickNameActivity.this, objectId, new UpdateListener() {
											@Override
											public void onSuccess() {													

												/**
												 * bmob多线程有可能造成数据更新不完整
												 */
												Editor editor=sp.edit();
												editor.putString("accountname", ed_new_nickName.getText().toString());
												editor.commit();
												
												Intent intent=new Intent();
												intent.putExtra("accountname", ed_new_nickName.getText().toString());//最后数据没用上，传递作用
												setResult(2, intent);
												finish();
											}
											@Override
											public void onFailure(int arg0, String arg1) {
												Toast.makeText(getApplicationContext(), "插入MainComment错误", 1).show();
											}
										});										
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
								}
							}else {
								/**
								 * bmob多线程有可能造成数据更新不完整
								 */
								Editor editor=sp.edit();
								editor.putString("accountname", ed_new_nickName.getText().toString());
								editor.commit();
								
								Intent intent=new Intent();
								intent.putExtra("accountname", ed_new_nickName.getText().toString());//最后数据没用上，传递作用
								setResult(2, intent);
								finish();
							}
							
						}
					});
											
					
					//修改AccountImg表中昵称部分 
					final AccountImg accountImg=new AccountImg();
					BmobQuery queryaccountImg=new BmobQuery("AccountImg");
					queryaccountImg.addWhereEqualTo("name", sp.getString("accountname", ""));
					queryaccountImg.findObjects(getApplicationContext(), new FindCallback() {					
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(getApplicationContext(), "查找AccountImg错误", 1).show();
						}					
						@Override
						public void onSuccess(JSONArray arg0) {
							JSONObject jsonObject;
							String objectId;
							if (arg0.length()!=0) {
								
									try {
										jsonObject=arg0.getJSONObject(0);
										objectId=jsonObject.getString("objectId");
										
										accountImg.setName(ed_new_nickName.getText().toString());
										accountImg.update(getApplicationContext(), objectId, new UpdateListener() {
											@Override
											public void onSuccess() {		
											}
											@Override
											public void onFailure(int arg0, String arg1) {
												Toast.makeText(getApplicationContext(), "插入AccountImg错误", 1).show();
											}
										});										
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
								
							}
							
						}
					});
					
					//修改LiaoTie表中昵称部分 
					final LiaoTie liaoTie =new LiaoTie();
					BmobQuery queryliaoTie=new BmobQuery("LiaoTie");
					queryliaoTie.addWhereEqualTo("username", sp.getString("accountname", ""));
					queryliaoTie.findObjects(getApplicationContext(), new FindCallback() {					
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(getApplicationContext(), "查找liaoTie错误", 1).show();
						}					
						@Override
						public void onSuccess(JSONArray arg0) {
							JSONObject jsonObject;
							String objectId;
							if (arg0.length()!=0) {
								for (int i = 0; i < arg0.length(); i++) {
									try {
										jsonObject=arg0.getJSONObject(i);
										objectId=jsonObject.getString("objectId");
										
										liaoTie.setUsername(ed_new_nickName.getText().toString());
										liaoTie.update(getApplicationContext(), objectId, new UpdateListener() {
											@Override
											public void onSuccess() {		
											}
											@Override
											public void onFailure(int arg0, String arg1) {
												Toast.makeText(getApplicationContext(), "插入liaoTie错误", 1).show();
											}
										});										
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}	
								
							}
							
						}
					});
					
					//修改LiaoTieComment表中昵称部分 
					final LiaoTieComment liaoTieComment=new LiaoTieComment();
					BmobQuery queryliaoTieComment=new BmobQuery("LiaoTieComment");
					queryliaoTieComment.addWhereEqualTo("username", sp.getString("accountname", ""));
					queryliaoTieComment.findObjects(getApplicationContext(), new FindCallback() {					
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(getApplicationContext(), "查找LiaoTieComment错误", 1).show();
						}					
						@Override
						public void onSuccess(JSONArray arg0) {
							JSONObject jsonObject;
							String objectId;
							if (arg0.length()!=0) {
								for (int i = 0; i < arg0.length(); i++) {
									try {
										jsonObject=arg0.getJSONObject(i);
										objectId=jsonObject.getString("objectId");
										
										liaoTieComment.setUsername(ed_new_nickName.getText().toString());
										liaoTieComment.update(ChangeNickNameActivity.this, objectId, new UpdateListener() {
											@Override
											public void onSuccess() {													

											
											}
											@Override
											public void onFailure(int arg0, String arg1) {
												Toast.makeText(getApplicationContext(), "插入LiaoTieComment错误", 1).show();
											}
										});										
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
								}
							}
							
						}
					});
					
					//修改UploadGridPics表中昵称部分 
					final UploadGridPics uploadGridPics =new UploadGridPics();
					BmobQuery queryuploadGridPics=new BmobQuery("UploadGridPics");
					queryuploadGridPics.addWhereEqualTo("username", sp.getString("accountname", ""));
					queryuploadGridPics.findObjects(getApplicationContext(), new FindCallback() {					
						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(getApplicationContext(), "查找uploadGridPics错误", 1).show();
						}					
						@Override
						public void onSuccess(JSONArray arg0) {
							JSONObject jsonObject;
							String objectId;
							if (arg0.length()!=0) {
								for (int i = 0; i < arg0.length(); i++) {
									try {
										jsonObject=arg0.getJSONObject(i);
										objectId=jsonObject.getString("objectId");
										
										uploadGridPics.setUsername(ed_new_nickName.getText().toString());
										uploadGridPics.update(getApplicationContext(), objectId, new UpdateListener() {
											@Override
											public void onSuccess() {		
											}
											@Override
											public void onFailure(int arg0, String arg1) {
												Toast.makeText(getApplicationContext(), "插入uploadGridPics错误", 1).show();
											}
										});										
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}	
								
							}
							
						}
					});
					
					
				}
				
				
			}
		});
		
	}
}
