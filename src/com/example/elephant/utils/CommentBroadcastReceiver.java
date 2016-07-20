package com.example.elephant.utils;

import com.example.elephant.MainComment;
import com.example.elephant.MainUIActivity;
import com.example.elephant.adpter.CommentItemAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CommentBroadcastReceiver extends BroadcastReceiver {
	CommentItemAdapter mAdapter;
	Context mContext;

	public CommentBroadcastReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {

//		mAdapter=MainComment.getAdapterData();
//		Toast.makeText(context, "π„≤• ’µΩ",1).show();
		mAdapter.notifyDataSetChanged();
//		MainComment.refresh_layout.setRefreshing(false);

	}

}
