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
	String collegeName, mainjobName, colleageCode;// 学校名称，专业名称,学校代号
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
	 * 当选择学校被点击 
	 */
	public void chooseProvince() {

		builder.setTitle("请选择省市");
		// 查询province表
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

										// 获得省的ID
										provinceId = map.get("provinceId").toString();

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

										// 获得院系名称
										mainjobName = map.get("colleageName")
												.toString();
										schoolAndMainjob=collegeName+"-"+mainjobName;
										saveToWeb();
																														
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
	 * 保存到新选择的学校名称和院系名称到服务器
	 */
	private void saveToWeb() {
		user.setSchool(collegeName);
		user.setMainjob(mainjobName);
		
		user.update(mContext, AccountInfoActivity.objectId, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				AccountInfoActivity.tv_show_school.setText(schoolAndMainjob);//设置院系名打TextView
				AccountInfoActivity.editor.putString("mainjob", mainjobName);
				AccountInfoActivity.editor.putString("school",collegeName);
				AccountInfoActivity.editor.commit();				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(mContext, "修改失败",1).show();				
			}
		});
		
	}
}
