package com.example.elephant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.push.config.Constant;
import cn.bmob.v3.listener.UpdateListener;

import com.example.elephant.audience.ChangeSchoolAndMainjob;
import com.example.elephant.db.UserName;
import com.example.elephant.utils.ImageHelper;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AccountInfoActivity extends Activity implements OnClickListener{

	ImageView back;
	ImageView iv_show_img;//显示头像
	TextView tv_change_img;//修改头像
	TextView tv_change_significance,tv_significance_info;//修改签名，签名显示
	TextView tv_accountinfo_change_account_name,tv_accountName;//修改昵称，显示昵称
	TextView tv_change_subject,tv_show_subject;//修改专业，显示专业
	TextView tv_change_gender,tv_show_gender;//修改性别，显示性别
	TextView tv_change_year,tv_show_year;//修改入学年份，显示入学年份
	TextView tv_change_school;//学校选择
	public static TextView tv_show_school;//学校显示
	TextView tv_change_password;//密码修改
	boolean isNull=false;//判断照相机返回数据是否为空
	private static String picFileFullName;//相片完整名称

	SharedPreferences sp;
	public static Editor editor;
	String significance;
	public static String objectId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);
		
		init();
		
		
		//当本地有图片文件时
		File file=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
		if (file.exists()) {//从没换过头像
			Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
			iv_show_img.setImageBitmap(ImageHelper.toRoundBitmap(bm));
//		}else if(){//网上没有就使用默认
			/**
			 * 从网上下载图片
			 */
			
			
			
		}else {
			iv_show_img.setImageResource(R.drawable.circle_head);
		}
		
	}

	private void init() {
		
		sp=getSharedPreferences("config",MODE_PRIVATE);
		editor=sp.edit();
		objectId=sp.getString("userObjectId", "");
		
		back=(ImageView) findViewById(R.id.iv_back_title);
		back.setOnClickListener(this);
		
		tv_change_significance=(TextView) findViewById(R.id.tv_change_significance);
		tv_change_significance.setOnClickListener(this);
		
		tv_significance_info=(TextView) findViewById(R.id.tv_significance_info);
		tv_significance_info.setText(sp.getString("significance", ""));
		
		tv_accountName=(TextView) findViewById(R.id.tv_accountinfo_account_name);
		tv_accountinfo_change_account_name=(TextView) findViewById(R.id.tv_accountinfo_change_account_name);
		tv_accountName.setText(sp.getString("accountname", "sp出错"));
		tv_accountinfo_change_account_name.setOnClickListener(this);//更改昵称
		
		tv_change_subject=(TextView) findViewById(R.id.tv_change_subject);//修改专业
		tv_show_subject=(TextView) findViewById(R.id.tv_show_subject);
		tv_show_subject.setText(sp.getString("subject", ""));
		tv_change_subject.setOnClickListener(this);
		
		tv_change_gender=(TextView) findViewById(R.id.tv_change_gender);//修改性别
		tv_show_gender=(TextView) findViewById(R.id.tv_show_gender);
		tv_show_gender.setText(sp.getString("gender", "男"));
		tv_change_gender.setOnClickListener(this);
		
		tv_change_year=(TextView) findViewById(R.id.tv_change_year);//修改性别
		tv_show_year=(TextView) findViewById(R.id.tv_show_year);
		tv_show_year.setText(sp.getString("year", "2013年"));
		tv_change_year.setOnClickListener(this);
		
		tv_change_school=(TextView) findViewById(R.id.tv_change_school);//修改专业
		tv_show_school=(TextView) findViewById(R.id.tv_show_school);
		tv_show_school.setText(sp.getString("school", "")+"-"+sp.getString("mainjob", ""));
		tv_change_school.setOnClickListener(this);
		
		tv_change_password=(TextView) findViewById(R.id.tv_change_password);//修改密码
		tv_change_password.setOnClickListener(this);
		
		tv_change_img=(TextView) findViewById(R.id.tv_change_img);//修改头像
		tv_change_img.setOnClickListener(this);
		iv_show_img=(ImageView) findViewById(R.id.iv_show_img);//显示头像
		
		

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back_title:
			ResultForLayout3();
			finish();
			break;
		case R.id.tv_change_significance://点击修改个人签名
			Intent intent=new Intent(AccountInfoActivity.this,ChangeSignificanceActivity.class);
			startActivityForResult(intent, 0x1000);
			break;
		case R.id.tv_accountinfo_change_account_name://点击修改昵称
			Intent intent2=new Intent(AccountInfoActivity.this,ChangeNickNameActivity.class);
			startActivityForResult(intent2, 0x1001);
			break;
		case R.id.tv_change_subject://点击修改专业
			Intent intent3=new Intent(AccountInfoActivity.this,ChangeSubjectActivity.class);
			startActivityForResult(intent3, 0x1002);
			break;
		case R.id.tv_change_gender://点击修改性别
			ChangeSex();
			break;
		case R.id.tv_change_year://点击修改入学年份
			ChangeYear();
			break;
		case R.id.tv_change_school://点击修改学校专业
			ChangeSchool();
			break;
		case R.id.tv_change_password://点击修改密码
			Intent intent4=new Intent(AccountInfoActivity.this,ChangePasswordActivity.class);
			startActivityForResult(intent4, 0x1003);
			break;
		case R.id.tv_change_img://点击修改头像
			changeMyImg();
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 修改个人头像
	 */
	private void changeMyImg() {
		AlertDialog.Builder imgDialog=new AlertDialog.Builder(AccountInfoActivity.this);
		String[] items=new String[]{"本地相册","打开相机"};
		
		imgDialog.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which==0) {
					Intent albumIntent = new Intent(Intent.ACTION_PICK);//打开系统的相册
					albumIntent.setType("image/*");
					startActivityForResult(albumIntent, 0x1004);
				}	
				if (which == 1) {//打开相机
					Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
					File file = new File(Environment.getExternalStorageDirectory()
							+ "/Elephant/accountImg/accountImg.jpg");
					if (file.exists()) {
						file.delete();
					}
					//指定uri存储相片
					getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
					isNull = true;
					startActivityForResult(getImageByCamera, 0x1004);
				}
				
			}
		
		});
		
		imgDialog.show();		
	}

	/**
	 * 返回数据给Layout3,修改个人签名
	 */
	public void ResultForLayout3() {		
		Intent intent=new Intent();
		intent.putExtra("significance", sp.getString("significance",""));
		intent.putExtra("accountname", sp.getString("accountname", "sp出错"));
		intent.putExtra("accountImgUri",sp.getString("accountImgUri", ""));
		setResult(3, intent);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data==null) {
			if (isNull) {//当返回值为空，且标志位为真，说明已经拍照并保存
				/**
				 * 对拍好的照片进行裁剪
				 */
				File fileImg = new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/accountImg.jpg");					
				Uri uriFromImg=Uri.fromFile(fileImg);
				//裁剪图片
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(uriFromImg, "image/*");
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);// aspectX是宽高的比例，这里设置的是正方形（长宽比为1:1）
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 400); // outputX outputY 是裁剪图片宽高
				intent.putExtra("outputY", 400); //不知怎么不能设太大，<640
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFromImg);
				intent.putExtra("return-data", true);
				intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
				intent.putExtra("noFaceDetection", true); // no face detection
				startActivityForResult(intent, 0x1006);
				isNull=false;
			}
		}else {
			if (requestCode==0x1000&&resultCode==1) {				
				significance=data.getStringExtra("significance");
				tv_significance_info.setText(significance);
			}
			if (requestCode==0x1001&&resultCode==2) {
				tv_accountName.setText(sp.getString("accountname", "sp出错"));
			}
			if (requestCode==0x1002&&resultCode==3) {
				tv_show_subject.setText(sp.getString("subject", "sp出错"));
			}
			if (requestCode==0x1004) {//从相册选取图片，手机拍照
				getClipPhotoByPickPicture(data.getData(),data.getExtras());
			}	
			if (requestCode == 0x1006) {
				//接收裁剪好的图片信息并保存到本地文件夹
				Bitmap bmap = data.getParcelableExtra("data");
				if (bmap!=null) {
					saveClipPhoto(bmap);
				}
				
			}

		}
			
	}
	

	/**
	 * 保存裁剪后的图片
	 * @param bmap
	 */
	private void saveClipPhoto(Bitmap bmap) {
		
		iv_show_img.setImageBitmap(ImageHelper.toRoundBitmap(bmap));
		

		// 图像保存到文件中
		FileOutputStream foutput = null;
		try {
			File fileDir = new File(
					Environment.getExternalStorageDirectory()
							+ "/Elephant/accountImg/");
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File fileImg = new File(fileDir, sp.getString("accountname", "")+"_accountImg.jpg");
			if (fileImg.exists()) {
				fileImg.delete();
			}

			foutput = new FileOutputStream(fileImg);
			bmap.compress(Bitmap.CompressFormat.JPEG, 20, foutput); // 压缩图片
			
			editor.putString("accountImgUri", fileImg.getAbsolutePath().toString());//保存文件地址
			editor.commit();
			
			/*
			 * 保存到Bmob服务器
			 */
			ImageHelper.saveAccountImgToBmob(AccountInfoActivity.this,
					sp.getString("accountname", "sp出错"), 
					fileImg.getAbsolutePath().toString());
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != foutput) {
				try {
					foutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	/**
	 * 取得返回的照片信息
	 * @param uri 传进返回数据的uri
	 * @param bundle 传进返回数据的bundle
	 */
	private void getClipPhotoByPickPicture(Uri uri, Bundle bundle) {

		if (uri == null) {
			/**
			 * 待检测。。。。。
			 */
		
		} else {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// aspectX是宽高的比例，这里设置的是正方形（长宽比为1:1）
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 400); // outputX outputY 是裁剪图片宽高
			intent.putExtra("outputY", 400); //不知怎么不能设太大，<640
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra("return-data", true);
			intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection
			startActivityForResult(intent, 0x1006);
		}

	}

	@Override
	public void onBackPressed() {
		ResultForLayout3(); 
		super.onBackPressed();
	}
	

	/**
	 * 修改入学年份
	 */
	private void ChangeYear() {
		AlertDialog.Builder builder=new AlertDialog.Builder(AccountInfoActivity.this);
		builder.setTitle("请选择");
		final String[] year=new String[]{"2016年","2015年","2014年","2013年","2012年","2011年","2010年","好久以前"};
		ArrayAdapter adapter=new ArrayAdapter<>(AccountInfoActivity.this, android.R.layout.simple_list_item_1,year);
		builder.setAdapter(adapter,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (year[which].equals(tv_show_year.getText().toString())) {						
				}else {
					UserName userName=new UserName();
					userName.setYear(year[which]);
					editor.putString("year", year[which]);
					editor.commit();
					userName.update(AccountInfoActivity.this,sp.getString("userObjectId", ""),new UpdateListener() {
						@Override
						public void onSuccess() {
							tv_show_year.setText(sp.getString("year", "2013年"));
						}				
						@Override
						public void onFailure(int arg0, String arg1) {								
						}
					});
				}
					
			}
		});
		builder.show();
		
	}

	/**
	 * 修改性别
	 */
	private void ChangeSex() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		final String[] gen=new String[]{"男","女"};
		ArrayAdapter adapter=new ArrayAdapter<>(AccountInfoActivity.this, android.R.layout.simple_list_item_1,gen);
		builder.setAdapter(adapter,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (gen[which].equals(tv_show_gender.getText().toString())) {						
				}else {
					UserName userName=new UserName();
					userName.setGender(gen[which]);
					editor.putString("gender", gen[which]);
					editor.commit();
					userName.update(AccountInfoActivity.this,sp.getString("userObjectId", ""),new UpdateListener() {
						@Override
						public void onSuccess() {
							tv_show_gender.setText(sp.getString("gender", "男"));
						}				
						@Override
						public void onFailure(int arg0, String arg1) {								
						}
					});
				}
					
			}
		});
		builder.show();
		
	}

	/**
	 * 修改学校院系
	 */
	private void ChangeSchool() {
		AlertDialog.Builder showAlert=new AlertDialog.Builder(AccountInfoActivity.this);
		showAlert.setTitle("提示");
		showAlert.setMessage("每个人只有三次机会修改学校和院系,确定修改吗");
		showAlert.setNegativeButton("取消",new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
		});
		showAlert.setPositiveButton("确定", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new ChangeSchoolAndMainjob(AccountInfoActivity.this);		
			}
		});
		showAlert.show();
	}
	
}
