package com.example.elephant.audience;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.UpdateListener;

import com.example.elephant.AccountInfoActivity;
import com.example.elephant.RegisterActivity;
import com.example.elephant.db.UserName;

public class ChangeSchoolAndMainjob {

	String schoolAndMainjob=null;
	String name = null;
	String id = null;
	String provinceId;
	Map<String, String> map;
	List<Map<String, String>> dataList = new ArrayList<>();
	AlertDialog.Builder builder;
	String collegeName, mainjobName, colleageCode;// ѧУ���ƣ�רҵ����,ѧУ����
	UserName user;

	Context mContext;
	public ChangeSchoolAndMainjob(Context context) {
		mContext=context;
		user = new UserName();
		builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(false);
			
		chooseProvince();
	}
		
	/**
	 * ��ѡ��ѧУ����� 
	 */
	public void chooseProvince() {

		builder.setTitle("��ѡ��ʡ��");
		// ��ѯprovince��
		BmobQuery queryProvince = new BmobQuery("province");
		queryProvince.findObjects(mContext, new FindCallback() {
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

				final ArrayAdapter adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, province);

				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								for (int i = 0; i < dataList.size(); i++) {
									map = (Map<String, String>) dataList.get(i);
									if (adapter.getItem(which).toString().equals(map.get("provinceName"))) {

										// ���ʡ��ID
										provinceId = map.get("provinceId").toString();

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

		queryCollege.findObjects(mContext, new FindCallback() {
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
						mContext,android.R.layout.simple_list_item_1, colleage);
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

		querySchool.findObjects(mContext, new FindCallback() {
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
						mContext,
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

										// ���Ժϵ����
										mainjobName = map.get("colleageName")
												.toString();
										schoolAndMainjob=collegeName+"-"+mainjobName;
										saveToWeb();
																														
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
	 * ���浽��ѡ���ѧУ���ƺ�Ժϵ���Ƶ�������
	 */
	private void saveToWeb() {
		user.setSchool(collegeName);
		user.setMainjob(mainjobName);
		
		user.update(mContext, AccountInfoActivity.objectId, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				AccountInfoActivity.tv_show_school.setText(schoolAndMainjob);//����Ժϵ����TextView
				AccountInfoActivity.editor.putString("mainjob", mainjobName);
				AccountInfoActivity.editor.putString("school",collegeName);
				AccountInfoActivity.editor.commit();				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(mContext, "�޸�ʧ��",1).show();				
			}
		});
		
	}
}
