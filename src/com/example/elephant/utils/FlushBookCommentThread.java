package com.example.elephant.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.elephant.MainComment;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FlushBookCommentThread extends Thread {

	Context mContext;
	List<Map<String, String>> list;
	String parentid;
	Map<String, String> map;
	Intent broadcastintent;
	
	public FlushBookCommentThread(Context context, String parentid) {

		mContext = context;
		this.parentid = parentid;
		
		list=new ArrayList<Map<String, String>>();
		broadcastintent = new Intent();  
		broadcastintent.setAction("com.example.elephant.utils.COMMENT_DAWNLOAD_SUCCESS");  

	}

	@Override
	public void run() {

		

	}

	/**
	 * 返回数据
	 */
	public void getData(){
		BmobQuery query = new BmobQuery("BookComment");

		query.addWhereEqualTo("parentobjectId", parentid);
		query.findObjects(mContext, new FindCallback() {

			@Override
			public void onFailure(int arg0, String arg1) {

				Toast.makeText(mContext, "获取评论列表失败", 1).show();
			}

			@Override
			public void onSuccess(JSONArray arg0) {
				try {
					for (int j = 0; j < arg0.length(); j++) {

						JSONObject itemJsonObject = arg0.getJSONObject(j);
						String comment=itemJsonObject.getString("comment");
						String username=itemJsonObject.getString("username");
						String significance=itemJsonObject.getString("significance");
						String createdAt=itemJsonObject.getString("createdAt");
						String floorNum=itemJsonObject.getString("floorNum");
						String objectId=itemJsonObject.getString("objectId");
						
						
						map=new HashMap<String, String>();
						map.put("comment", comment);
						map.put("username", username);
						map.put("significance", significance);
						map.put("createdAt", createdAt);
						map.put("floorNum", floorNum);
						map.put("objectId", objectId);
						list.add(0,map);//最新的消息在最shang面			

					}
		            mContext.sendBroadcast(broadcastintent);   //广播发送  

				} catch (JSONException e) {
					e.printStackTrace();
				}


			}
		});
	}
	/**
	 * 返回评论列表数据
	 * 
	 * @return
	 */
	public List<Map<String, String>> getList() {
		return list;

	}

}
