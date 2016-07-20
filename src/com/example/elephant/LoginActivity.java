package com.example.elephant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.a.a.thing;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;

import com.example.elephant.db.UserName;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	EditText account, password;
	TextView register, login;
	UserName user;
	String name = null;
	String id = null;
	String provinceId;
	Map<String, String> map;
	List<Map<String, String>> dataList = new ArrayList<>();
	AlertDialog.Builder builder;

	String collegeName, mainjobName, colleageCode;// 学校名称，专业名称,学校代号
	
	SharedPreferences sp;//保存学校院系信息
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		account = (EditText) findViewById(R.id.et_login_account);
		password = (EditText) findViewById(R.id.et_login_password);
		register = (TextView) findViewById(R.id.tv_btn_register);
		login = (TextView) findViewById(R.id.tv_btn_login);

		sp=getSharedPreferences("config",MODE_PRIVATE);//保存学校院系信息
		editor=sp.edit();

		
		user = new UserName();

		// 点击注册
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 打开注册的activity
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivityForResult(intent, 1);

			}
		});

		// 点击登陆
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showLogin();

			}
		});

		// 注册添加学校信息
		builder = new AlertDialog.Builder(LoginActivity.this);
	}

	/**
	 * 登录
	 */
	public void showLogin() {

		final String name = account.getText().toString();
		final String pwd = password.getText().toString();

		if (name.equals("")) {
			Toast.makeText(LoginActivity.this, "你还没输入名称", 1).show();
			return;
		} else if(name.length()<2){
			Toast.makeText(LoginActivity.this, "帐户名应不小于两个字符", 1).show();
			return;
		}else if (pwd.equals("")) {
			Toast.makeText(LoginActivity.this, "你还没输入密码", 1).show();
			return;

		} else {
			BmobQuery query = new BmobQuery("UserName");

			// 仅返回name字段,一行数据
			if (name.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
				query.addWhereEqualTo("email", name);
			}else {
				query.addWhereEqualTo("username", name);
			}

			query.findObjects(this, new FindCallback() {
				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(LoginActivity.this, "登录时发生了未知错误", 1).show();
				}

				@Override
				public void onSuccess(JSONArray arg0) {
					// 解析返回的JSON数据
					// 线程
					try {
						if (arg0.length() == 0) {
							Toast.makeText(LoginActivity.this, "账号还没有注册", 1)
									.show();
						} else {					
							JSONObject jsonItem = arg0.getJSONObject(0);//返回一行注册信息

							if ((jsonItem.getString("username").equals(name)||jsonItem.getString("email").equals(name))
									&& jsonItem.getString("password").equals(
											pwd)) {

								String significance = jsonItem
										.getString("significance");

								////保存学校院系信息
								editor.putString("accountname", jsonItem.getString("username"));//账号昵称
								editor.putString("password", jsonItem.getString("password"));//密码
								editor.putString("significance", significance);//个性签名
								editor.putString("school", jsonItem.getString("school"));//学校
								editor.putString("mainjob", jsonItem.getString("mainjob"));//院系
								editor.putString("email", jsonItem.getString("email"));//邮箱
								editor.putString("year", jsonItem.getString("year"));//入学年份
								editor.putString("subject", jsonItem.getString("subject"));//专业
								editor.putString("gender", jsonItem.getString("gender"));//性别
								editor.putString("userObjectId", jsonItem.getString("objectId"));//objectId
								editor.commit();
								
								Toast.makeText(LoginActivity.this, "登录成功", 1)
										.show();
								Intent intent = new Intent();
								intent.putExtra("accountname", jsonItem.getString("username"));
								intent.putExtra("significance", significance);// 签名

								setResult(1, intent);
								
								
							
								finish();
								return;

							} else {
								Toast.makeText(LoginActivity.this, "账号或密码错误", 1)
										.show();
							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			// 自动填充账号密码
			account.setText(data.getStringExtra("account"));
			password.setText(data.getStringExtra("password"));
		}

	}
}
