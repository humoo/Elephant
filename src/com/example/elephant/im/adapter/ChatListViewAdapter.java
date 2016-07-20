package com.example.elephant.im.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.InfoIconActivity;
import com.example.elephant.R;
import com.example.elephant.TabMainActivity;
import com.example.elephant.db.MyOpenHelper;
import com.example.elephant.im.AddNewFriendActivity;
import com.example.elephant.im.ChatMainActivity;
import com.example.elephant.utils.DownloadAccountPicThread;
import com.example.elephant.utils.ImageHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;

public class ChatListViewAdapter extends BaseAdapter{

	
	public static List<Map<String, String>> list=new ArrayList<Map<String,String>>();
	static Map<String,String> map;
	MyOpenHelper dbHelper;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	static SQLiteDatabase db;
	DownloadAccountPicThread downloadAccountPicThread;
	LayoutInflater inflater;
	Context context;
	
	Handler handler;
	
	public ChatListViewAdapter(Context context, Handler handler) {
		this.context=context;
		this.handler=handler;
		inflater=LayoutInflater.from(context);
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(context);
		/**
		 * �����ݿ��ȡ�û���Ϣ�б�
		 */
		dbHelper=new MyOpenHelper(context, openSharePrefrenceHelper.getSP_CONFIG().getString("email", ""), null, 1);
		db =dbHelper.getReadableDatabase();

		setListData();
				
	}

	
	static Cursor cursor;
	public static void setListData() {
		// ����_id�Ӵ�С
		cursor = db.query("recent", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {

			do {
				map = new HashMap<String, String>();
				map.put("accountname",
						cursor.getString(cursor.getColumnIndex("username")));
				map.put("posttime",
						cursor.getString(cursor.getColumnIndex("msgtime")));
				map.put("content",
						cursor.getString(cursor.getColumnIndex("lastmsg")));
				map.put("unread",
						cursor.getInt(cursor.getColumnIndex("unread")) + "");// Int----->String
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
		
		View view=inflater.inflate(R.layout.im_item_chat_list,null);
		final TextView contact_name=(TextView) view.findViewById(R.id.contact_name);
		TextView contact_time=(TextView) view.findViewById(R.id.contact_time);
		TextView contact_message=(TextView) view.findViewById(R.id.contact_message);
		TextView iv_circle_red=(TextView) view.findViewById(R.id.iv_circle_red);//δ��
		ImageView contact_head=(ImageView) view.findViewById(R.id.contact_head);
		
		map=list.get(position);
		contact_name.setText(map.get("accountname"));
		contact_message.setText(map.get("content"));
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
		Date date = new Date(Long.parseLong(map.get("posttime")));
		contact_time.setText(formatter.format(date));
		
		if (Integer.parseInt(map.get("unread"))>0) {
			iv_circle_red.setVisibility(View.VISIBLE);
			iv_circle_red.setText(map.get("unread"));
			if (Integer.parseInt(map.get("unread"))>99) {
				iv_circle_red.setText("������");
			}
		}else {
			iv_circle_red.setVisibility(View.INVISIBLE);
		}
		
		/**
		 * ����ͷ��
		 */
		loadAccountImg(map,contact_head);
		
		view.setTag(position);//���ñ�־λ
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int myPostion =Integer.parseInt(v.getTag().toString());
				map=list.get(myPostion);
				Intent intent=new Intent(context,ChatMainActivity.class);
				intent.putExtra("username", map.get("accountname"));//�û���
				context.startActivity(intent);				
			}
		});
		return view;
	}

	
	File localFile;
	/**
	 * ����ͷ��
	 * @param map
	 * @param contact_head 
	 */
	private void loadAccountImg(final Map<String, String> map, ImageView contact_head) {
		//��������ͼƬ�ļ�ʱ
		
				localFile=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ map.get("accountname") +"_accountImg.jpg");
				
				//����ǵ�ǰ�û����򲻴��������ͼƬ,�ӱ���·����ȡ

				/**
				 * Ӧ�������ݿⴢ��
				 * //��������ͼƬ�ļ�ʱ
				 */
				if (localFile.exists()) {			
					contact_head.setImageBitmap(
							ImageHelper.toRoundBitmap(
									BitmapFactory
									.decodeFile(localFile.getAbsolutePath())));		
				}else {
				
					BmobQuery query=new BmobQuery("AccountImg");
					query.addWhereEndsWith("name", map.get("accountname"));
					query.findObjects(context, new FindCallback() {			
						@Override
						public void onFailure(int arg0, String arg1) {
							Log.d("logd","����ͼƬʧ��");
						}
			
						@Override
						public void onSuccess(JSONArray arg0) {

							try {
								/**
								 * ��ȡ������������ͷ����û��б��ͷ���ַ
								 */
								if (arg0.length()!=0) {

									JSONObject iJsonObject=arg0.getJSONObject(0);
									JSONObject item=(JSONObject) iJsonObject.get("file");
							
									/**
									 * ���û����ƺ�ͷ���ַ����,�������߳�����
									 */
									
									//���߳�����ͼƬ
									downloadAccountPicThread=new DownloadAccountPicThread(context,
											item.get("filename").toString(),
											item.get("url").toString(),handler);
									
									downloadAccountPicThread.start();
									
								}
				
							} catch (JSONException e) {
								e.printStackTrace();
								Log.d("logd","����");

							}
							
						}
					});
					
					//ǿ������Ĭ��ͷ��
					//contact_head.setImageResource(R.drawable.circle_head);		

				}
		
	}
	
	
}
