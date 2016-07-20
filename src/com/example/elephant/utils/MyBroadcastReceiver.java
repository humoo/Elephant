package com.example.elephant.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.elephant.MainUIActivity;
import com.example.elephant.adpter.MainUIItemAdapter;

public class MyBroadcastReceiver extends BroadcastReceiver {
	
	MainUIItemAdapter mAdapter;
	Context mContext;

	public MyBroadcastReceiver() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		
		mAdapter=MainUIActivity.getAdapterData();
//		Toast.makeText(context, "π„≤• ’µΩ",1).show();
		MainUIActivity.flushPd.dismiss();
		mAdapter.notifyDataSetChanged();
		MainUIActivity.mainListView.onRefreshComplete();
//		MainUIActivity.refresh_layout.setRefreshing(false);
		
	}

}
