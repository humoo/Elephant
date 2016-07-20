package com.example.elephant.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 将Bitmap临时保存到本地，压缩
 * @author Administrator
 *
 */
public class FileUtils {
	
	//保存压缩后原图
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/Elephant/formats/";
	//由原图创建的缩略图
	public static String SDPATH_THUMB = Environment.getExternalStorageDirectory()
			+ "/Elephant/thumb/";
	//文件加载时产生的缩略图保存目录
	public static String SDPATH_THUMB_FROM_WEB = Environment.getExternalStorageDirectory()
			+ "/Elephant/thumb_from_web/downSmallThumb/";

	public static void saveBitmap(Bitmap bm, String picName) {
		//Log.e("", "保存图片");
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			
			
			File f = new File(SDPATH, picName + ".JPEG"); 
			
			if (f.exists()) {
				f.delete();
			}
			
			FileOutputStream out = new FileOutputStream(f);

			if (bm.getWidth() > 1000 && bm.getHeight() > 760) { // 1300
				bm.compress(Bitmap.CompressFormat.JPEG, 40, out);
			} else if (bm.getWidth() > 760 && bm.getHeight() > 1000) {
				bm.compress(Bitmap.CompressFormat.JPEG, 40, out);
			} else if (bm.getWidth() > 600 && bm.getHeight() > 800) {// >800M
				bm.compress(Bitmap.CompressFormat.JPEG, 60, out);
			} else if (bm.getWidth() > 800 && bm.getHeight() > 600) {// >800M
				bm.compress(Bitmap.CompressFormat.JPEG, 60, out);
			} else if (bm.getWidth() > 450 && bm.getHeight() > 600) {// >500M
				bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
			} else if (bm.getWidth() > 600 && bm.getHeight() > 450) {// >500M
				bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
			} else {
				bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
			}
			
			createThumb(f,picName);
			
			out.flush();
			out.close();
			//Log.d("logd", picName+"已经保存");
			
			if (!bm.isRecycled()) {
				bm.recycle();//记得释放资源，否则会内存溢出  
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	static BitmapFactory.Options options=new BitmapFactory.Options();;
	
	/**
	 * 创建缩略图
	 * @param f 
	 * @param picName 图片名称
	 */
	private static void createThumb(File f, String picName) {
		//创建thumb文件目录
		File thumbDir=new File(SDPATH_THUMB);
		if (!thumbDir.exists()) {
			thumbDir.mkdirs();
		}
		File thumbFile = new File(SDPATH_THUMB, picName + "_thumb.JPEG"); 
		if (thumbFile.exists()) {
			thumbFile.delete();
		}
		Bitmap bitmap=null;
		try {
			FileOutputStream thumbFile_out = new FileOutputStream(thumbFile);
			
			FileInputStream in=new FileInputStream(f);
			
			options.inSampleSize = 2;// width，hight设为原来的2分一，1/4面积
			bitmap = BitmapFactory.decodeStream(in, null,
					options);
			
			bitmap=ThumbnailUtils.extractThumbnail(bitmap, 150, 150);  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, thumbFile_out);
			
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		}

	}



	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		File thumbFile = new File(SDPATH_THUMB + fileName.substring(0,fileName.length()-5)+"_thumb.JPEG");
		if(file.isFile()){
			file.delete();
        }
		//file.exists();
//		Log.e("",thumbFile.getAbsolutePath());
		if(thumbFile.isFile()){
			thumbFile.delete();
        }
		thumbFile.exists();
	}

	/**
	 * 传入文件夹名称
	 * @param dir
	 */
	public static void deleteDir(File dir) {
		//File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(file);  // 递规的方式删除文件夹
		}
		dir.delete();/// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

}
