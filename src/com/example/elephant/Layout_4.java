package com.example.elephant;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.utils.DownloadAccountPicThread;
import com.example.elephant.utils.ImageHelper;

public class Layout_4 extends Activity implements OnClickListener{

	TextView TV_logiName,TV_significance;
	TextView tv_open_account_info;
	static ImageView infoImg;
	ImageView setting;
	static SharedPreferences sp;
	SharedPreferences.Editor editor;
	
	// 点击放大图片
	Bitmap bp = null;
	float scaleWidth;
	float scaleHeight;
	ImageView showBigImg;
	int h;
	boolean num = false;
	LinearLayout ll;
	
	public Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==0x1111) {
				File file=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
				if (file.exists()) {
					Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
					infoImg.setImageBitmap(ImageHelper.toRoundBitmap(bm));
				}
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout4_activity);

		TV_logiName=(TextView) findViewById(R.id.tv_info_name);
		TV_significance=(TextView) findViewById(R.id.tv_info_significance);
		tv_open_account_info=(TextView) findViewById(R.id.tv_open_account_info);
		infoImg=(ImageView) findViewById(R.id.iv_info_img);
		setting=(ImageView) findViewById(R.id.iv_main_acti_setting);
		showBigImg=(ImageView) findViewById(R.id.iv_show_big_img);
		ll=(LinearLayout) findViewById(R.id.ll_show_big_img);

		
		tv_open_account_info.setOnClickListener(this);
		
		sp=getSharedPreferences("config", MODE_PRIVATE);
		editor=sp.edit();
		
		if (isLogin()) {
//			accInfo.setEnabled(true);
			tv_open_account_info.setEnabled(true);
			TV_logiName.setText(sp.getString("accountname", "sp出错"));
			if (sp.getString("significance","").equals(".#")) {
				TV_significance.setText("");
			}else {
				TV_significance.setText(sp.getString("significance",""));
			}			
			
			//当本地有图片文件时
			File file=new File(Environment.getExternalStorageDirectory()
					+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
			if (file.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
				infoImg.setImageBitmap(ImageHelper.toRoundBitmap(bm));
			}else {//从网上下载图片
				downloadFromWeb();
//				Toast.makeText(Layout_4.this, "登陆",1).show();
			}
			
		}else {
//			accInfo.setEnabled(false);
			tv_open_account_info.setEnabled(false);
			TV_logiName.setText("请登录");
			TV_significance.setText("点击头像登录");
			infoImg.setImageResource(R.drawable.circle_head);
		}
		
		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入个人信息页
				
				Intent intent=new Intent(Layout_4.this,SettingActivity.class);
				startActivityForResult(intent, 2);
				
			}
		});
		
		infoImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isLogin()) {
					Intent intent=new Intent(Layout_4.this,LoginActivity.class);
					startActivityForResult(intent, 1);
		
				}else {
					//点击放大图片					
					ll.setVisibility(View.VISIBLE);
					//当本地有图片文件时
					File file=new File(Environment.getExternalStorageDirectory()
							+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
					if (file.exists()) {
						Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
						showBigImg.setImageBitmap(bm);
					}else {
				
						/**
						 * 从网上加载图片
						 */
//						downloadFromWeb();
						
					}
//					Display display=getWindowManager().getDefaultDisplay();
//					
//			         int width=bp.getWidth();  
//			         int height=bp.getHeight();  
//			         int w=display.getWidth();  
//			         int h=display.getHeight();  
					 showBigImg.setVisibility(View.VISIBLE);					 
//			         showBigImg.setImageBitmap(bp); 
					 num = true;

				}
				
			}

			
		});
		
		showBigImg.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				 ll.setVisibility(View.INVISIBLE);
			}
		});		
	}
	
	/**
	 * 从服务器下载图片
	 */
	private void downloadFromWeb() {
		BmobQuery query=new BmobQuery("AccountImg");
		query.addWhereEndsWith("name", sp.getString("accountname", ""));
		query.findObjects(Layout_4.this, new FindCallback() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				Log.d("logd","加载图片失败");
			}

			@Override
			public void onSuccess(JSONArray arg0) {

				try {
					/**
					 * 获取到服务器已有头像的用户列表和头像地址
					 */
					if (arg0.length()!=0) {

						JSONObject iJsonObject=arg0.getJSONObject(0);
						JSONObject item=(JSONObject) iJsonObject.get("file");
				
						/**
						 * 将用户名称和头像地址储存,并开启线程下载
						 */
						
						//子线程下载图片
						DownloadAccountPicThread downloadAccountPicThread=new DownloadAccountPicThread(Layout_4.this,
								item.get("filename").toString(),
								item.get("url").toString(),handler);
						
						downloadAccountPicThread.start();
						
					}
	
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("logd","错误");

				}
				
			}
		});
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			if (num == true) {
//				Matrix matrix = new Matrix();
//				matrix.postScale(scaleWidth, scaleHeight);
//
//				Bitmap newBitmap = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
//						bp.getHeight(), matrix, true);
				showBigImg.setVisibility(View.INVISIBLE);
				ll.setVisibility(View.INVISIBLE);

//				infoImg.setImageBitmap(newBitmap);
				num = false;
			} else {
//				Matrix matrix = new Matrix();
//				matrix.postScale(1.0f, 1.0f);
//				Bitmap newBitmap = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
//						bp.getHeight(), matrix, true);
//				infoImg.setImageBitmap(newBitmap);
//				showBigImg.setVisibility(View.VISIBLE);
//				ll.setVisibility(View.VISIBLE);


			}
			break;
		}

		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data==null) {
			
		}else {
			
			if (requestCode == 1) {

				TV_logiName.setText(data.getStringExtra("accountname"));// 获取账户名字
				if (data.getStringExtra("significance").equals(".#")) {
					TV_significance.setText("");// 获取签名

				} else {
					TV_significance.setText(data.getStringExtra("significance"));// 获取签名
				}

				//当本地有图片文件时
				File file=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
				if (file.exists()) {
					Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
					infoImg.setImageBitmap(ImageHelper.toRoundBitmap(bm));
				}
				
				tv_open_account_info.setEnabled(true);

				// 存放是否已登录状态
				editor.putBoolean("islogin", true);
				editor.putString("accountname",
						data.getStringExtra("accountname"));
				editor.putString("significance",
						data.getStringExtra("significance"));
				editor.commit();
			}
			
			if (requestCode==2) {
				tv_open_account_info.setEnabled(false);//没登陆不能打开个人信息
				TV_logiName.setText("请登录");
				TV_significance.setText("点击头像登录");
				infoImg.setImageResource(R.drawable.circle_head);
			}
			
			if (resultCode==3) {
				TV_significance.setText(data.getStringExtra("significance"));
				TV_logiName.setText(sp.getString("accountname", "sp出错"));
				//当本地有图片文件时
				File file=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
				if (file.exists()) {
					Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
					infoImg.setImageBitmap(ImageHelper.toRoundBitmap(bm));
				}
			}
			
		}
		
		
		
	}
	
	/**
	 * 判断是否已登陆
	 */
	public boolean isLogin(){
		
		return sp.getBoolean("islogin", false);
		
	}


	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.tv_open_account_info:
			Intent intent=new Intent(Layout_4.this,AccountInfoActivity.class);
			startActivityForResult(intent,3);
			break;

		default:
			break;
		}
		
	}
}
