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
 * ÿ���Ự�û�������һ���������ݿ�
 * ����chat,friends,recent������
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
	//������,������ʾlistview
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
	 * @param fromCustomUserID �Է��û�����
	 * @param email ��������
	 * @param text ������Ϣ
	 * @param serverActionTime ʱ��
	 * @param isreaded �Ƿ������촰�� 0����
	 * @param status ����0������1
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
		
		chatValues.put("bothemail", fromCustomUserID+"&"+email);//�Է�����+�Լ�������
		chatValues.put("belongemail", fromCustomUserID);//������Ϣ��һ��
		chatValues.put("told", email);//������Ϣ��һ��
		chatValues.put("isreaded", 0);
		chatValues.put("status", status);
		chatValues.put("msgtype", 1);
		chatValues.put("username", fromCustomUserID);//�Է��ǳƣ�����
		chatValues.put("msgcontent", text);
		chatValues.put("msgtime", time);		 
		db.insert("chat", null, chatValues);
		
		
		recentValues.put("fromeamil", fromCustomUserID);//�Է�����
		recentValues.put("username",fromCustomUserID );//�Է��ǳ�
		recentValues.put("lastmsg",text );
		recentValues.put("msgtype", 1);
		recentValues.put("unread", 1);
		recentValues.put("msgtime", time);
//		cursor=db.query("recent", new String[]{ "fromeamil"}, "where fromeamil=? ", new String[]{fromCustomUserID}, null, null, null);
		cursor=db.rawQuery("select * from recent where fromeamil = ? ", new String[]{fromCustomUserID});
		if (cursor.moveToFirst()) {//���������
			recentValues.put("unread", cursor.getInt(cursor.getColumnIndex("unread"))+1);
			db.update("recent", recentValues, "fromeamil=?", new String[]{ fromCustomUserID });
			Log.e("", "δ��:"+cursor.getInt(cursor.getColumnIndex("unread"))+"");
		}else {
			db.insert("recent", null, recentValues);
			Log.e("", "cursor.moveToFirst():"+cursor.moveToFirst());
		}
		
		
	}
	

}
