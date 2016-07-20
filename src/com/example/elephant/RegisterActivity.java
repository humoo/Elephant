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
	SharedPreferences sp;//����ѧУԺϵ��Ϣ
	Editor editor;
	String collegeName, mainjobName, colleageCode;// ѧУ���ƣ�רҵ����,ѧУ����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		
		user = new UserName();
		sp=getSharedPreferences("config",MODE_PRIVATE);
		editor=sp.edit();

		// ע�����ѧУ��Ϣ
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
	 * ע��
	 */
	public void showRegister() {

		String select=selectEditText.getText().toString();
		String email=emailEditText.getText().toString().trim();
		final String name =account.getText().toString().trim();
		final String pwd = password.getText().toString().trim();
		

		if (select.equals("")) {
			Toast.makeText(RegisterActivity.this, "��ѡ��ѧУ", 1).show();
		}else if (email.equals("")||!email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {		
			Toast.makeText(RegisterActivity.this, "��������ȷ�������ַ", 1).show();
		}else if (name.split(" ").length!=1||name.equals("")) {
			Toast.makeText(RegisterActivity.this, "������������", 1).show();
		} else if (pwd.equals("")) {
			Toast.makeText(RegisterActivity.this, "�㻹û��������", 1).show();
		} else {

			// ���������δ���ڸ�����
			user.setUsername(name);
			user.setPassword(pwd);
			user.setSignificance("");
			user.setSchool(collegeName);
			user.setMainjob(mainjobName);
			user.setEmail(email);
			user.setYear("2015��");//Ĭ����ѧ��ݣ�д��
			user.setGender("��");//Ĭ���Ա�д��
			user.setSubject(" ");
			
			user.save(this, new SaveListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(RegisterActivity.this, "ע��ɹ����������½", 1).show();
					Intent intent=new Intent();
					intent.putExtra("account", name);
					intent.putExtra("password", pwd);
					setResult(10, intent);
					finish();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(RegisterActivity.this, "������ǳ��ѱ�ע��", 1).show();
				}
			});
		}
	}
	
	
	/**
	 * ��ѡ��ѧУ�����
	 * 
	 * @param v
	 */
	public void chooseProvince(View v) {

		builder.setTitle("��ѡ��ʡ��");
		// ��ѯprovince��
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

										// ���ʡ��ID
										provinceId = map.get("provinceId")
												.toString();

										// ��ѯcollege��

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
	 * ��ѯcollege��
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

										// ���ѧУ����
										collegeName = map.get("colleageName")
												.toString();
										colleageCode = map.get("colleageCode")
												.toString();

										// ��ѯschool��
										chooseSchool(colleageCode);

									}
								}
								dataList.clear();

							}

						});
				builder.setTitle("��ѡ��ѧУ");
				builder.show();

			}
		});
	}

	/**
	 * ��ѯschool��Ժϵ
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

										// ���ѧУ����
										mainjobName = map.get("colleageName")
												.toString();

										System.out.println(collegeName+"....."+mainjobName);
										EditText et_login_select=(EditText) findViewById(R.id.et_login_select);
										et_login_select.setText(collegeName+"-"+mainjobName);
										schoolAndMainjob=collegeName+"-"+mainjobName;
										
										editor.putString("collegeName", collegeName);//ѧУ����
										editor.putString("mainjobName", mainjobName);//Ժϵ����
										editor.commit();
										
									}
								}
								dataList.clear();

							}

						});
				builder.setTitle("��ѡ��ѧУ");
				builder.show();

			}
		});

	}
}
