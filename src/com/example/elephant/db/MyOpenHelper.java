package com.example.elephant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

/**
 * 每个会话用户都创立一个本地数据库
 * 包含chat,friends,recent三个表
 * 
 * @author zhxumao
 *
 */
public class MyOpenHelper extends SQLiteOpenHelper {
	
	private final String CHAT_TABLE = "create table chat (_id integer primary key autoincrement,"
			+ "bothemail text,"
			+ "belongemail text,"
			+ "told text,"
			+ "isreaded integer,"
			+ "status integer,"
			+ "msgtype integer,"
			+ "username text, " 
			+ "msgcontent text," 
			+ "msgtime long)";
	private final String FRIENDS_TABLE="create table friends (belongemail text,username text,isblack integer)";
	private final String RECENT_TABLE = "create table recent (_id integer primary key autoincrement,"
			+ "fromeamil text,"
			+ "username text,"
			+ "lastmsg text,"
			+ "unread integer,"
			+ "msgtype integer,"
			+ "msgtime long)";
	//索引表,快速显示listview
	private final String SUOYIN_TABLE="create table suoyin (name text,seq integer)";

	
	Context mContext;
	/**
	 * 
	 * @param context
	 * @param dbname
	 * @param factory
	 * @param version
	 */
	public MyOpenHelper(Context context, String dbname, CursorFactory factory,
			int version) {
		super(context, dbname, factory, version);
		mContext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CHAT_TABLE);
		db.execSQL(FRIENDS_TABLE);
		db.execSQL(RECENT_TABLE);
		db.execSQL(SUOYIN_TABLE);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
		
	}
	
	static ContentValues chatValues=new ContentValues();
	static ContentValues friendsValues=new ContentValues();
	static ContentValues recentValues=new ContentValues();
	static ContentValues suoyinValues=new ContentValues();
	static Cursor cursor;
	static String time;
	static Long longTime;
	/**
	 * 
	 * @param db
	 * @param fromCustomUserID 对方用户邮箱
	 * @param email 本人邮箱
	 * @param text 聊天消息
	 * @param serverActionTime 时间
	 * @param isreaded 是否在聊天窗口 0不是
	 * @param status 接收0，发送1
	 */
	public static void saveChatMsg(SQLiteDatabase db, 
			String fromCustomUserID, 
			String email, 
			String text, 
			long serverActionTime, 
			int isreaded, 
			int status){
		
		time=serverActionTime+"000";
		longTime=Long.parseLong(time);
		//Log.e("", "longTime:"+longTime+",time:"+time);
		
		chatValues.put("bothemail", fromCustomUserID+"&"+email);//对方邮箱+自己的邮箱
		chatValues.put("belongemail", fromCustomUserID);//发送信息的一方
		chatValues.put("told", email);//接收信息的一方
		chatValues.put("isreaded", 0);
		chatValues.put("status", status);
		chatValues.put("msgtype", 1);
		chatValues.put("username", fromCustomUserID);//对方昵称，待改
		chatValues.put("msgcontent", text);
		chatValues.put("msgtime", time);		 
		db.insert("chat", null, chatValues);
		
		
		recentValues.put("fromeamil", fromCustomUserID);//对方邮箱
		recentValues.put("username",fromCustomUserID );//对方昵称
		recentValues.put("lastmsg",text );
		recentValues.put("msgtype", 1);
		recentValues.put("unread", 1);
		recentValues.put("msgtime", time);
//		cursor=db.query("recent", new String[]{ "fromeamil"}, "where fromeamil=? ", new String[]{fromCustomUserID}, null, null, null);
		cursor=db.rawQuery("select * from recent where fromeamil = ? ", new String[]{fromCustomUserID});
		if (cursor.moveToFirst()) {//如果有数据
			recentValues.put("unread", cursor.getInt(cursor.getColumnIndex("unread"))+1);
			db.update("recent", recentValues, "fromeamil=?", new String[]{ fromCustomUserID });
			Log.e("", "未读:"+cursor.getInt(cursor.getColumnIndex("unread"))+"");
		}else {
			db.insert("recent", null, recentValues);
			Log.e("", "cursor.moveToFirst():"+cursor.moveToFirst());
		}
		
		
	}
	

}
