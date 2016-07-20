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

	String collegeName, mainjobName, colleageCode;// ѧУ���ƣ�רҵ����,ѧУ����
	
	SharedPreferences sp;//����ѧУԺϵ��Ϣ
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		account = (EditText) findViewById(R.id.et_login_account);
		password = (EditText) findViewById(R.id.et_login_password);
		register = (TextView) findViewById(R.id.tv_btn_register);
		login = (TextView) findViewById(R.id.tv_btn_login);

		sp=getSharedPreferences("config",MODE_PRIVATE);//����ѧУԺϵ��Ϣ
		editor=sp.edit();

		
		user = new UserName();

		// ���ע��
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// ��ע���activity
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivityForResult(intent, 1);

			}
		});

		// �����½
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showLogin();

			}
		});

		// ע�����ѧУ��Ϣ
		builder = new AlertDialog.Builder(LoginActivity.this);
	}

	/**
	 * ��¼
	 */
	public void showLogin() {

		final String name = account.getText().toString();
		final String pwd = password.getText().toString();

		if (name.equals("")) {
			Toast.makeText(LoginActivity.this, "�㻹û��������", 1).show();
			return;
		} else if(name.length()<2){
			Toast.makeText(LoginActivity.this, "�ʻ���Ӧ��С�������ַ�", 1).show();
			return;
		}else if (pwd.equals("")) {
			Toast.makeText(LoginActivity.this, "�㻹û��������", 1).show();
			return;

		} else {
			BmobQuery query = new BmobQuery("UserName");

			// ������name�ֶ�,һ������
			if (name.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
				query.addWhereEqualTo("email", name);
			}else {
				query.addWhereEqualTo("username", name);
			}

			query.findObjects(this, new FindCallback() {
				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(LoginActivity.this, "��¼ʱ������δ֪����", 1).show();
				}

				@Override
				public void onSuccess(JSONArray arg0) {
					// �������ص�JSON����
					// �߳�
					try {
						if (arg0.length() == 0) {
							Toast.makeText(LoginActivity.this, "�˺Ż�û��ע��", 1)
									.show();
						} else {					
							JSONObject jsonItem = arg0.getJSONObject(0);//����һ��ע����Ϣ

							if ((jsonItem.getString("username").equals(name)||jsonItem.getString("email").equals(name))
									&& jsonItem.getString("password").equals(
											pwd)) {

								String significance = jsonItem
										.getString("significance");

								////����ѧУԺϵ��Ϣ
								editor.putString("accountname", jsonItem.getString("username"));//�˺��ǳ�
								editor.putString("password", jsonItem.getString("password"));//����
								editor.putString("significance", significance);//����ǩ��
								editor.putString("school", jsonItem.getString("school"));//ѧУ
								editor.putString("mainjob", jsonItem.getString("mainjob"));//Ժϵ
								editor.putString("email", jsonItem.getString("email"));//����
								editor.putString("year", jsonItem.getString("year"));//��ѧ���
								editor.putString("subject", jsonItem.getString("subject"));//רҵ
								editor.putString("gender", jsonItem.getString("gender"));//�Ա�
								editor.putString("userObjectId", jsonItem.getString("objectId"));//objectId
								editor.commit();
								
								Toast.makeText(LoginActivity.this, "��¼�ɹ�", 1)
										.show();
								Intent intent = new Intent();
								intent.putExtra("accountname", jsonItem.getString("username"));
								intent.putExtra("significance", significance);// ǩ��

								setResult(1, intent);
								
								
							
								finish();
								return;

							} else {
								Toast.makeText(LoginActivity.this, "�˺Ż��������", 1)
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
			// �Զ�����˺�����
			account.setText(data.getStringExtra("account"));
			password.setText(data.getStringExtra("password"));
		}

	}
}
