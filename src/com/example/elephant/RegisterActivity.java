package com.example.elephant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.elephant.db.UserName;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity{

	TextView register;
	EditText account, password,emailEditText,selectEditText;
	UserName user;

	
	String schoolAndMainjob=null;
	String name = null;
	String id = null;
	String provinceId;
	Map<String, String> map;
	List<Map<String, String>> dataList = new ArrayList<>();
	AlertDialog.Builder builder;
	SharedPreferences sp;//保存学校院系信息
	Editor editor;
	String collegeName, mainjobName, colleageCode;// 学校名称，专业名称,学校代号
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		
		user = new UserName();
		sp=getSharedPreferences("config",MODE_PRIVATE);
		editor=sp.edit();

		// 注册添加学校信息
		builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setCancelable(false);
		
		selectEditText=(EditText) findViewById(R.id.et_login_select);
		emailEditText=(EditText) findViewById(R.id.et_login_email);
		account = (EditText) findViewById(R.id.et_login_account);
		password = (EditText) findViewById(R.id.et_login_password);
		register = (TextView) findViewById(R.id.tv_btn_register);
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showRegister();

			}
		});
	}
	
	
	/**
	 * 注册
	 */
	public void showRegister() {

		String select=selectEditText.getText().toString();
		String email=emailEditText.getText().toString().trim();
		final String name =account.getText().toString().trim();
		final String pwd = password.getText().toString().trim();
		

		if (select.equals("")) {
			Toast.makeText(RegisterActivity.this, "请选择学校", 1).show();
		}else if (email.equals("")||!email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {		
			Toast.makeText(RegisterActivity.this, "请输入正确的邮箱地址", 1).show();
		}else if (name.split(" ").length!=1||name.equals("")) {
			Toast.makeText(RegisterActivity.this, "输入名称有误", 1).show();
		} else if (pwd.equals("")) {
			Toast.makeText(RegisterActivity.this, "你还没输入密码", 1).show();
		} else {

			// 如果服务器未存在该名称
			user.setUsername(name);
			user.setPassword(pwd);
			user.setSignificance("");
			user.setSchool(collegeName);
			user.setMainjob(mainjobName);
			user.setEmail(email);
			user.setYear("2015年");//默认入学年份，写死
			user.setGender("男");//默认性别，写死
			user.setSubject(" ");
			
			user.save(this, new SaveListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(RegisterActivity.this, "注册成功，请继续登陆", 1).show();
					Intent intent=new Intent();
					intent.putExtra("account", name);
					intent.putExtra("password", pwd);
					setResult(10, intent);
					finish();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(RegisterActivity.this, "邮箱或昵称已被注册", 1).show();
				}
			});
		}
	}
	
	
	/**
	 * 当选择学校被点击
	 * 
	 * @param v
	 */
	public void chooseProvince(View v) {

		builder.setTitle("请选择省市");
		// 查询province表
		BmobQuery queryProvince = new BmobQuery("province");
		queryProvince.findObjects(RegisterActivity.this, new FindCallback() {
			@Override
			public void onFailure(int arg0, String arg1) {
			}

			@Override
			public void onSuccess(JSONArray arg0) {

				for (int j = 0; j < arg0.length(); j++) {
					try {
						JSONObject jsonProvinceItem = arg0.getJSONObject(j);
						name = jsonProvinceItem.get("provinceName").toString();
						provinceId = jsonProvinceItem.getString("provinceId")
								.toString();

						map = new HashMap<>();
						map.put("provinceName", name);
						map.put("provinceId", provinceId);
						dataList.add(map);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				String[] province = new String[dataList.size()];
				for (int i = 0; i < dataList.size(); i++) {
					map = (Map<String, String>) dataList.get(i);
					province[i] = map.get("provinceName").toString();

				}

				final ArrayAdapter adapter = new ArrayAdapter<>(
						RegisterActivity.this,
						android.R.layout.simple_list_item_1, province);
	
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								for (int i = 0; i < dataList.size(); i++) {
									map = (Map<String, String>) dataList.get(i);
									if (adapter.getItem(which).toString()
											.equals(map.get("provinceName"))) {

										// 获得省的ID
										provinceId = map.get("provinceId")
												.toString();

										// 查询college表

										chooseCollege(provinceId);

									}
								}
								dataList.clear();

							}

						});
				builder.show();
			}
		});

	}

	/**
	 * 查询college表
	 * 
	 * @param provinceId
	 */
	private void chooseCollege(String provinceId) {

		BmobQuery queryCollege = new BmobQuery("college");

		queryCollege.addWhereEqualTo("provinceId", provinceId);

		queryCollege.findObjects(RegisterActivity.this, new FindCallback() {
			@Override
			public void onFailure(int arg0, String arg1) {
			}

			@Override
			public void onSuccess(JSONArray arg0) {
				for (int i = 0; i < arg0.length(); i++) {
					try {
						JSONObject jsonObject = arg0.getJSONObject(i);
						name = (String) jsonObject.get("colleageName");
						id = (String) jsonObject.get("colleageCode");

						map = new HashMap<>();
						map.put("colleageName", name);
						map.put("colleageCode", id);
						dataList.add(map);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				String[] colleage = new String[dataList.size()];
				for (int i = 0; i < dataList.size(); i++) {
					map = (Map<String, String>) dataList.get(i);
					colleage[i] = map.get("colleageName").toString();
				}

				final ArrayAdapter adapter2 = new ArrayAdapter<>(
						RegisterActivity.this,
						android.R.layout.simple_list_item_1, colleage);

				builder.setAdapter(adapter2,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								for (int i = 0; i < dataList.size(); i++) {
									map = (Map<String, String>) dataList.get(i);
									if (adapter2.getItem(which).toString()
											.equals(map.get("colleageName"))) {

										// 获得学校名称
										collegeName = map.get("colleageName")
												.toString();
										colleageCode = map.get("colleageCode")
												.toString();

										// 查询school表
										chooseSchool(colleageCode);

									}
								}
								dataList.clear();

							}

						});
				builder.setTitle("请选择学校");
				builder.show();

			}
		});
	}

	/**
	 * 查询school表，院系
	 * 
	 * @param colleageCode
	 */
	private void chooseSchool(String colleageCode) {

		BmobQuery querySchool = new BmobQuery("school");

		querySchool.addWhereEqualTo("colleageCode", colleageCode);

		querySchool.findObjects(RegisterActivity.this, new FindCallback() {
			@Override
			public void onFailure(int arg0, String arg1) {
			}

			@Override
			public void onSuccess(JSONArray arg0) {

				for (int i = 0; i < arg0.length(); i++) {
					try {
						JSONObject jsonObject = arg0.getJSONObject(i);
						name = (String) jsonObject.get("colleageName");
						id = (String) jsonObject.get("colleageCode");

						map = new HashMap<>();
						map.put("colleageName", name);
						map.put("colleageCode", id);
						dataList.add(map);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				String[] colleage = new String[dataList.size()];
				for (int i = 0; i < dataList.size(); i++) {
					map = (Map<String, String>) dataList.get(i);
					colleage[i] = map.get("colleageName").toString();
				}

				final ArrayAdapter adapter3 = new ArrayAdapter<>(
						RegisterActivity.this,
						android.R.layout.simple_list_item_1, colleage);

				builder.setAdapter(adapter3,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								for (int i = 0; i < dataList.size(); i++) {
									map = (Map<String, String>) dataList.get(i);
									if (adapter3.getItem(which).toString()
											.equals(map.get("colleageName"))) {

										// 获得学校名称
										mainjobName = map.get("colleageName")
												.toString();

										System.out.println(collegeName+"....."+mainjobName);
										EditText et_login_select=(EditText) findViewById(R.id.et_login_select);
										et_login_select.setText(collegeName+"-"+mainjobName);
										schoolAndMainjob=collegeName+"-"+mainjobName;
										
										editor.putString("collegeName", collegeName);//学校名字
										editor.putString("mainjobName", mainjobName);//院系名字
										editor.commit();
										
									}
								}
								dataList.clear();

							}

						});
				builder.setTitle("请选择学校");
				builder.show();

			}
		});

	}
}
