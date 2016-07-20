package com.example.elephant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class OpenSharePrefrenceHelper {

	public static SharedPreferences sp_config;
	public static Editor editor_config;
	
	public static SharedPreferences sp_accountImg;
	public static Editor editor_sp_accountImg;
	
	public OpenSharePrefrenceHelper(Context context) {

		sp_config=context.getSharedPreferences("config", context.MODE_PRIVATE);
		editor_config=sp_config.edit();
		
		/**
		 * 储存用户名和头像
		 */
		sp_accountImg=context.getSharedPreferences("account", context.MODE_PRIVATE);
		editor_sp_accountImg=sp_accountImg.edit();
	
	}
	
	public SharedPreferences getSP_CONFIG() {
		return sp_config;
	}
	
	public Editor getEditor_CONFIG() {
		return editor_config;
	}
	
	public SharedPreferences getSP_ACCOUNT() {
		return sp_accountImg;
	}
	
	public Editor getEditor_ACCOUNT() {
		return editor_sp_accountImg;
	}
	
	
	
}
