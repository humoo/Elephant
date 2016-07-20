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
	
	// ����Ŵ�ͼƬ
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
			TV_logiName.setText(sp.getString("accountname", "sp����"));
			if (sp.getString("significance","").equals(".#")) {
				TV_significance.setText("");
			}else {
				TV_significance.setText(sp.getString("significance",""));
			}			
			
			//��������ͼƬ�ļ�ʱ
			File file=new File(Environment.getExternalStorageDirectory()
					+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
			if (file.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
				infoImg.setImageBitmap(ImageHelper.toRoundBitmap(bm));
			}else {//����������ͼƬ
				downloadFromWeb();
//				Toast.makeText(Layout_4.this, "��½",1).show();
			}
			
		}else {
//			accInfo.setEnabled(false);
			tv_open_account_info.setEnabled(false);
			TV_logiName.setText("���¼");
			TV_significance.setText("���ͷ���¼");
			infoImg.setImageResource(R.drawable.circle_head);
		}
		
		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���������Ϣҳ
				
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
					//����Ŵ�ͼƬ					
					ll.setVisibility(View.VISIBLE);
					//��������ͼƬ�ļ�ʱ
					File file=new File(Environment.getExternalStorageDirectory()
							+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
					if (file.exists()) {
						Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
						showBigImg.setImageBitmap(bm);
					}else {
				
						/**
						 * �����ϼ���ͼƬ
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
	 * �ӷ���������ͼƬ
	 */
	private void downloadFromWeb() {
		BmobQuery query=new BmobQuery("AccountImg");
		query.addWhereEndsWith("name", sp.getString("accountname", ""));
		query.findObjects(Layout_4.this, new FindCallback() {			
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
						DownloadAccountPicThread downloadAccountPicThread=new DownloadAccountPicThread(Layout_4.this,
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

				TV_logiName.setText(data.getStringExtra("accountname"));// ��ȡ�˻�����
				if (data.getStringExtra("significance").equals(".#")) {
					TV_significance.setText("");// ��ȡǩ��

				} else {
					TV_significance.setText(data.getStringExtra("significance"));// ��ȡǩ��
				}

				//��������ͼƬ�ļ�ʱ
				File file=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
				if (file.exists()) {
					Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
					infoImg.setImageBitmap(ImageHelper.toRoundBitmap(bm));
				}
				
				tv_open_account_info.setEnabled(true);

				// ����Ƿ��ѵ�¼״̬
				editor.putBoolean("islogin", true);
				editor.putString("accountname",
						data.getStringExtra("accountname"));
				editor.putString("significance",
						data.getStringExtra("significance"));
				editor.commit();
			}
			
			if (requestCode==2) {
				tv_open_account_info.setEnabled(false);//û��½���ܴ򿪸�����Ϣ
				TV_logiName.setText("���¼");
				TV_significance.setText("���ͷ���¼");
				infoImg.setImageResource(R.drawable.circle_head);
			}
			
			if (resultCode==3) {
				TV_significance.setText(data.getStringExtra("significance"));
				TV_logiName.setText(sp.getString("accountname", "sp����"));
				//��������ͼƬ�ļ�ʱ
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
	 * �ж��Ƿ��ѵ�½
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
