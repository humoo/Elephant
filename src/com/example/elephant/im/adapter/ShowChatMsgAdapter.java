package com.example.elephant.im.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.volley.toolbox.ImageLoader;

import com.example.elephant.R;
import com.example.elephant.db.MyOpenHelper;
import com.example.elephant.utils.ImageHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowChatMsgAdapter extends BaseAdapter{

	Context context;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	MyOpenHelper dbHelper;
	static SQLiteDatabase db;
	static String belongemail;
	public static List<Map<String, String>> list=new ArrayList<Map<String,String>>();
	static Map<String,String> map;
	LayoutInflater inflater;
	
	File localFileLeft,localFileRight;
	Bitmap leftBitmap,rightBitmap;
	/**
	 * 
	 * @param context
	 * @param belongemail 发送信息方的邮箱
	 */
	public ShowChatMsgAdapter(Context context, String belongemail) {
		this.context=context;
		this.belongemail=belongemail;
		inflater=LayoutInflater.from(context);
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(context);
		/**
		 * 从数据库读取用户消息列表
		 */
		dbHelper=new MyOpenHelper(context, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
		db =dbHelper.getWritableDatabase();
		
		setData();
		
		localFileRight=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", "") +"_accountImg.jpg");
		localFileLeft=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ belongemail +"_accountImg.jpg");
		if (localFileRight.exists()) {
			rightBitmap=BitmapFactory.decodeFile(localFileRight.getAbsolutePath());
		}else {
			rightBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_head);
		}
		
		
		leftBitmap= BitmapFactory.decodeFile(localFileLeft.getAbsolutePath());
		
		leftBitmap=ImageHelper.toRoundBitmap(leftBitmap);
		rightBitmap=ImageHelper.toRoundBitmap(rightBitmap);
				
	}
	
	public static void setData() {
		
		//降序，_id从大到小
		Cursor cursor=db.rawQuery("select * from chat where belongemail = ? ", new String[]{ belongemail });
		if (cursor.moveToFirst()) {
			
			do {
				map=new HashMap<String, String>();
				map.put("msgtime", cursor.getString(cursor.getColumnIndex("msgtime")));
				map.put("msgcontent", cursor.getString(cursor.getColumnIndex("msgcontent")));
				map.put("status", cursor.getInt(cursor.getColumnIndex("status"))+"");//Int----->String
				map.put("belongemail", cursor.getString(cursor.getColumnIndex("belongemail")));
				list.add(map);
			} while (cursor.moveToNext());
			
		}
		
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		
		View view;
		map=list.get(position);
		
		TextView contact_message,contact_time;
		ImageView iv;
		
		if (map.get("status").equals("0")) {
			view=inflater.inflate(R.layout.im_chat_left_item,null);
			
			iv=(ImageView) view.findViewById(R.id.iv);
			contact_message=(TextView) view.findViewById(R.id.tv);
			
			if (localFileLeft.exists()) {
				iv.setImageBitmap(leftBitmap);
			}
			

		}else {
			view=inflater.inflate(R.layout.im_chat_right_item,null);
			iv=(ImageView) view.findViewById(R.id.iv);
			contact_message=(TextView) view.findViewById(R.id.tv);
			iv.setImageBitmap(rightBitmap);

		}
		
		
		contact_time=(TextView) view.findViewById(R.id.time);
		if (position==0) {
			contact_time.setVisibility(View.VISIBLE);
			
//			 SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
			 Date date = new Date(Long.parseLong(map.get("msgtime")));
			 
			contact_time.setText(formatter.format(date));
		}
		if (position>0) {
			if (Long.parseLong(map.get("msgtime"))-Long.parseLong(list.get(position-1).get("msgtime"))>1000*60*3) {
				contact_time.setVisibility(View.VISIBLE);
				
				 SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
				 Date date = new Date(Long.parseLong(map.get("msgtime")));
				 
				contact_time.setText(formatter.format(date));
			}
		}
		
		contact_message.setText(map.get("msgcontent"));
		
		return view;
		
		
	}

}
