package com.example.elephant.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.util.Log;

public class DownLoadUtil {

	/**
	 * 返回下载文件名
	 * 
	 * @param apkurl
	 * @return
	 */
	public static String getFilename(String apkurl) {

		String fileName = apkurl.substring(apkurl.lastIndexOf("/") + 1,
				apkurl.length());
		return fileName;
	}

	/**
	 * 下载文件，更新进度条
	 * 
	 * @param apkurl
	 * @param filePath
	 * @param pd
	 * @return
	 */
	public static File getFile(String apkurl, String filePath, ProgressDialog pd) {
		try {
			URL url = new URL(apkurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);

			File file = new File(filePath);

			// 获取到文件长度
			int pdMax = conn.getContentLength();

			pd.setMax(pdMax);

			// 打开文件输出流，将文件放到本地
			FileOutputStream fos = new FileOutputStream(file);

			InputStream is = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			int progress = 0;

			while ((len = is.read(buffer)) != -1) {

				fos.write(buffer, 0, len);
				progress = progress + len;

				pd.setProgress(progress);

				Thread.sleep(30);

			}
			fos.flush();
			fos.close();
			is.close();
			Log.d("logd", "下载apk完成");
			return file;

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("DownLoadUtil", "下载错误");
		}
		return null;
	}

}
