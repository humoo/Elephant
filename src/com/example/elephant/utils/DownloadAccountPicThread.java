package com.example.elephant.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.elephant.Layout_4;
import com.example.elephant.MainUIActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class DownloadAccountPicThread extends Thread {

	File file;
	// �ļ�����Ŀ¼
	File fileDir = new File(Environment.getExternalStorageDirectory()
			+ "/Elephant/accountImg/");

	Context context;
	String filename;
	String fileUrl;
	Handler handler;
	public DownloadAccountPicThread(Context context, String filename,
			String fileUrl, Handler handler) {
		this.context = context;
		this.filename = filename;
		this.fileUrl = fileUrl;
		this.handler=handler;
	}

	@Override
	public void run() {
		downPicFromUrl();
	}

	public void downPicFromUrl() {
		FileOutputStream fos = null;
		InputStream in = null;

		Log.d("logd", "�����߳�..." + filename );

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		// �����ļ�
		file = new File(fileDir, filename+"_cache");

		if (file.exists()) {
			file.delete();
		}

		try {

			fos = new FileOutputStream(file);

			URL url = new URL(fileUrl);

			in = url.openStream();

			int len = -1;
			byte[] b = new byte[1024];
			while ((len = in.read(b)) != -1) {
				fos.write(b, 0, len);
			}

			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.d("logd", "ͷ��url����");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("logd", "ͷ��io����");

		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			file.renameTo(new File(fileDir, filename));//����������adapter���ر���ͼƬ�����
			Message msg=new Message();
			msg.what=0x1111;
			handler.sendMessage(msg);
		}

		
	}
	
	/**
	 * ����bitmap
	 * @return
	 */
	public Bitmap getBitmap() {
		if (file.length()!=0) {
			return ImageHelper.toRoundBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));		
		}

		return null;
	}

}
