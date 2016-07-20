package com.example.elephant.im;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.elephant.R;
import com.example.elephant.im.adapter.ChatListViewAdapter;

public class ChatFragment extends Fragment {

	public static ListView lv_chat;
	public static ChatListViewAdapter adapter;
	public static Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==0x1111) {
				adapter.notifyDataSetChanged();
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.im_chat_fragment, container, false);
		
		lv_chat=(ListView) view.findViewById(R.id.lv_chat);
		
		return view;
	}
	
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		ChatListViewAdapter.list.clear();
		ChatListViewAdapter.setListData();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		adapter=new ChatListViewAdapter(getActivity(),handler);
		lv_chat.setAdapter(adapter);
		
	}
	
}
