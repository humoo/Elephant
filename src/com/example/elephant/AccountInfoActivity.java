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
	ImageView iv_show_img;//��ʾͷ��
	TextView tv_change_img;//�޸�ͷ��
	TextView tv_change_significance,tv_significance_info;//�޸�ǩ����ǩ����ʾ
	TextView tv_accountinfo_change_account_name,tv_accountName;//�޸��ǳƣ���ʾ�ǳ�
	TextView tv_change_subject,tv_show_subject;//�޸�רҵ����ʾרҵ
	TextView tv_change_gender,tv_show_gender;//�޸��Ա���ʾ�Ա�
	TextView tv_change_year,tv_show_year;//�޸���ѧ��ݣ���ʾ��ѧ���
	TextView tv_change_school;//ѧУѡ��
	public static TextView tv_show_school;//ѧУ��ʾ
	TextView tv_change_password;//�����޸�
	boolean isNull=false;//�ж���������������Ƿ�Ϊ��
	private static String picFileFullName;//��Ƭ��������

	SharedPreferences sp;
	public static Editor editor;
	String significance;
	public static String objectId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);
		
		init();
		
		
		//��������ͼƬ�ļ�ʱ
		File file=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ sp.getString("accountname", "") +"_accountImg.jpg");
		if (file.exists()) {//��û����ͷ��
			Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
			iv_show_img.setImageBitmap(ImageHelper.toRoundBitmap(bm));
//		}else if(){//����û�о�ʹ��Ĭ��
			/**
			 * ����������ͼƬ
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
		tv_accountName.setText(sp.getString("accountname", "sp����"));
		tv_accountinfo_change_account_name.setOnClickListener(this);//�����ǳ�
		
		tv_change_subject=(TextView) findViewById(R.id.tv_change_subject);//�޸�רҵ
		tv_show_subject=(TextView) findViewById(R.id.tv_show_subject);
		tv_show_subject.setText(sp.getString("subject", ""));
		tv_change_subject.setOnClickListener(this);
		
		tv_change_gender=(TextView) findViewById(R.id.tv_change_gender);//�޸��Ա�
		tv_show_gender=(TextView) findViewById(R.id.tv_show_gender);
		tv_show_gender.setText(sp.getString("gender", "��"));
		tv_change_gender.setOnClickListener(this);
		
		tv_change_year=(TextView) findViewById(R.id.tv_change_year);//�޸��Ա�
		tv_show_year=(TextView) findViewById(R.id.tv_show_year);
		tv_show_year.setText(sp.getString("year", "2013��"));
		tv_change_year.setOnClickListener(this);
		
		tv_change_school=(TextView) findViewById(R.id.tv_change_school);//�޸�רҵ
		tv_show_school=(TextView) findViewById(R.id.tv_show_school);
		tv_show_school.setText(sp.getString("school", "")+"-"+sp.getString("mainjob", ""));
		tv_change_school.setOnClickListener(this);
		
		tv_change_password=(TextView) findViewById(R.id.tv_change_password);//�޸�����
		tv_change_password.setOnClickListener(this);
		
		tv_change_img=(TextView) findViewById(R.id.tv_change_img);//�޸�ͷ��
		tv_change_img.setOnClickListener(this);
		iv_show_img=(ImageView) findViewById(R.id.iv_show_img);//��ʾͷ��
		
		

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back_title:
			ResultForLayout3();
			finish();
			break;
		case R.id.tv_change_significance://����޸ĸ���ǩ��
			Intent intent=new Intent(AccountInfoActivity.this,ChangeSignificanceActivity.class);
			startActivityForResult(intent, 0x1000);
			break;
		case R.id.tv_accountinfo_change_account_name://����޸��ǳ�
			Intent intent2=new Intent(AccountInfoActivity.this,ChangeNickNameActivity.class);
			startActivityForResult(intent2, 0x1001);
			break;
		case R.id.tv_change_subject://����޸�רҵ
			Intent intent3=new Intent(AccountInfoActivity.this,ChangeSubjectActivity.class);
			startActivityForResult(intent3, 0x1002);
			break;
		case R.id.tv_change_gender://����޸��Ա�
			ChangeSex();
			break;
		case R.id.tv_change_year://����޸���ѧ���
			ChangeYear();
			break;
		case R.id.tv_change_school://����޸�ѧУרҵ
			ChangeSchool();
			break;
		case R.id.tv_change_password://����޸�����
			Intent intent4=new Intent(AccountInfoActivity.this,ChangePasswordActivity.class);
			startActivityForResult(intent4, 0x1003);
			break;
		case R.id.tv_change_img://����޸�ͷ��
			changeMyImg();
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * �޸ĸ���ͷ��
	 */
	private void changeMyImg() {
		AlertDialog.Builder imgDialog=new AlertDialog.Builder(AccountInfoActivity.this);
		String[] items=new String[]{"�������","�����"};
		
		imgDialog.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which==0) {
					Intent albumIntent = new Intent(Intent.ACTION_PICK);//��ϵͳ�����
					albumIntent.setType("image/*");
					startActivityForResult(albumIntent, 0x1004);
				}	
				if (which == 1) {//�����
					Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
					File file = new File(Environment.getExternalStorageDirectory()
							+ "/Elephant/accountImg/accountImg.jpg");
					if (file.exists()) {
						file.delete();
					}
					//ָ��uri�洢��Ƭ
					getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
					isNull = true;
					startActivityForResult(getImageByCamera, 0x1004);
				}
				
			}
		
		});
		
		imgDialog.show();		
	}

	/**
	 * �������ݸ�Layout3,�޸ĸ���ǩ��
	 */
	public void ResultForLayout3() {		
		Intent intent=new Intent();
		intent.putExtra("significance", sp.getString("significance",""));
		intent.putExtra("accountname", sp.getString("accountname", "sp����"));
		intent.putExtra("accountImgUri",sp.getString("accountImgUri", ""));
		setResult(3, intent);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data==null) {
			if (isNull) {//������ֵΪ�գ��ұ�־λΪ�棬˵���Ѿ����ղ�����
				/**
				 * ���ĺõ���Ƭ���вü�
				 */
				File fileImg = new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/accountImg.jpg");					
				Uri uriFromImg=Uri.fromFile(fileImg);
				//�ü�ͼƬ
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(uriFromImg, "image/*");
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);// aspectX�ǿ�ߵı������������õ��������Σ������Ϊ1:1��
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 400); // outputX outputY �ǲü�ͼƬ���
				intent.putExtra("outputY", 400); //��֪��ô������̫��<640
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
				tv_accountName.setText(sp.getString("accountname", "sp����"));
			}
			if (requestCode==0x1002&&resultCode==3) {
				tv_show_subject.setText(sp.getString("subject", "sp����"));
			}
			if (requestCode==0x1004) {//�����ѡȡͼƬ���ֻ�����
				getClipPhotoByPickPicture(data.getData(),data.getExtras());
			}	
			if (requestCode == 0x1006) {
				//���ղü��õ�ͼƬ��Ϣ�����浽�����ļ���
				Bitmap bmap = data.getParcelableExtra("data");
				if (bmap!=null) {
					saveClipPhoto(bmap);
				}
				
			}

		}
			
	}
	

	/**
	 * ����ü����ͼƬ
	 * @param bmap
	 */
	private void saveClipPhoto(Bitmap bmap) {
		
		iv_show_img.setImageBitmap(ImageHelper.toRoundBitmap(bmap));
		

		// ͼ�񱣴浽�ļ���
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
			bmap.compress(Bitmap.CompressFormat.JPEG, 20, foutput); // ѹ��ͼƬ
			
			editor.putString("accountImgUri", fileImg.getAbsolutePath().toString());//�����ļ���ַ
			editor.commit();
			
			/*
			 * ���浽Bmob������
			 */
			ImageHelper.saveAccountImgToBmob(AccountInfoActivity.this,
					sp.getString("accountname", "sp����"), 
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
	 * ȡ�÷��ص���Ƭ��Ϣ
	 * @param uri �����������ݵ�uri
	 * @param bundle �����������ݵ�bundle
	 */
	private void getClipPhotoByPickPicture(Uri uri, Bundle bundle) {

		if (uri == null) {
			/**
			 * ����⡣��������
			 */
		
		} else {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// aspectX�ǿ�ߵı������������õ��������Σ������Ϊ1:1��
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 400); // outputX outputY �ǲü�ͼƬ���
			intent.putExtra("outputY", 400); //��֪��ô������̫��<640
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
	 * �޸���ѧ���
	 */
	private void ChangeYear() {
		AlertDialog.Builder builder=new AlertDialog.Builder(AccountInfoActivity.this);
		builder.setTitle("��ѡ��");
		final String[] year=new String[]{"2016��","2015��","2014��","2013��","2012��","2011��","2010��","�þ���ǰ"};
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
							tv_show_year.setText(sp.getString("year", "2013��"));
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
	 * �޸��Ա�
	 */
	private void ChangeSex() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("��ѡ��");
		final String[] gen=new String[]{"��","Ů"};
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
							tv_show_gender.setText(sp.getString("gender", "��"));
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
	 * �޸�ѧУԺϵ
	 */
	private void ChangeSchool() {
		AlertDialog.Builder showAlert=new AlertDialog.Builder(AccountInfoActivity.this);
		showAlert.setTitle("��ʾ");
		showAlert.setMessage("ÿ����ֻ�����λ����޸�ѧУ��Ժϵ,ȷ���޸���");
		showAlert.setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
		});
		showAlert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new ChangeSchoolAndMainjob(AccountInfoActivity.this);		
			}
		});
		showAlert.show();
	}
	
}
