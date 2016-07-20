package com.example.elephant.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;
import com.example.elephant.AddNewLiaotieActivity;
import com.example.elephant.db.AccountImg;
import com.example.elephant.db.LiaoTie;
import com.example.elephant.db.UploadGridPics;

/**
 * 将图片转换为圆形图片
 * 
 * @author zhxumao
 *
 */
// 处理类
public class ImageHelper {
	static OpenSharePrefrenceHelper openSharePrefrenceHelper;
	 static int k=0;
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap==null) {
			
		}else {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float roundPx;
			float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
			if (width <= height) {
				roundPx = width / 2;
				top = 0;
				bottom = width;
				left = 0;
				right = width;
				height = width;
				dst_left = 0;
				dst_top = 0;
				dst_right = width;
				dst_bottom = width;
			} else {
				roundPx = height / 2;
				float clip = (width - height) / 2;
				left = clip;
				right = width - clip;
				top = 0;
				bottom = height;
				width = height;
				dst_left = 0;
				dst_top = 0;
				dst_right = height;
				dst_bottom = height;
			}

			Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect src = new Rect((int) left, (int) top, (int) right,
					(int) bottom);
			final Rect dst = new Rect((int) dst_left, (int) dst_top,
					(int) dst_right, (int) dst_bottom);
			final RectF rectF = new RectF(dst);

			paint.setAntiAlias(true);

			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, src, dst, paint);
			return output;
		}
		return null;
		
	}

	// 下载图片转为bitmap
//	public static Bitmap getHttpBitmap(String url) {
//		URL u = null;
//		Bitmap bmp = null;
//		try {
//			u = new URL(url);
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		try {
//			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//			conn.setConnectTimeout(0);
//			conn.setDoInput(true);
//			conn.connect();
//			InputStream ins = conn.getInputStream();
//			bmp = BitmapFactory.decodeStream(ins);
//			ins.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return bmp;
//	}

	static String objectId = "";

	/**
	 * 头像
	 * 上传到Bmob后台服务器 path:图片地址
	 */
	public static void saveAccountImgToBmob(final Context context,
			final String accountName, final String path) {

		File file = new File(path);
		// 未上传前查询当前用户已存在的头像objectId
		BmobQuery query = new BmobQuery("AccountImg");
		query.addWhereEqualTo("name", accountName);
		query.findObjects(context, new FindCallback() {

			@Override
			public void onFailure(int arg0, String arg1) {
			}

			@Override
			public void onSuccess(JSONArray arg0) {

				try {
					JSONObject iJsonObject = arg0.getJSONObject(0);
					objectId = iJsonObject.getString("objectId"); // 获得当前用户旧头像的objectId

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		
		
		final BmobFile bmobFile = new BmobFile(file);
		bmobFile.uploadblock(context, new UploadFileListener() {

			@Override
			public void onSuccess() {
				Log.d("logd", "文件上传成功，返回的名称--" + bmobFile.getFileUrl(context));
				insertObject(new AccountImg(accountName, bmobFile));

			}

			@Override
			public void onProgress(Integer value) {
				// TODO Auto-generated method stub
				super.onProgress(value);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "失败！\n可能造成的原因：\n昵称使用非法字符\n网络不通畅", 3).show();
			}

			/**
			 * 保存图片信息到accountImg表
			 * 
			 * @param accountImg
			 */
			private void insertObject(final AccountImg accountImg) {
				accountImg.setName(accountImg.getName());
				accountImg.setFile(accountImg.getFile());
				accountImg.setFilePath(path);
				accountImg.save(context, new SaveListener() {

					@Override
					public void onSuccess() {

						// 上传成功并删除旧的
						accountImg.setObjectId(objectId);
						accountImg.delete(context, new DeleteListener() {
							@Override
							public void onSuccess() {
								// Toast.makeText(context, "好了", 1).show();
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// Toast.makeText(context, "不行", 1).show();

							}
						});

					}

					@Override
					public void onFailure(int arg0, String arg1) {

					}
				});

			}
		});

	}
	

	/**
	 * 保存用户帖子的相片到服务器
	 * @param 上传的图片地址：
	 * @param SDPATH = Environment.getExternalStorageDirectory()+ "/Elephant/formats/";
	 * @param SDPATH_THUMB = Environment.getExternalStorageDirectory()+ "/Elephant/formats/thumb/";
	 * @param context
	 * @param ed_content 
	 * @param picsCount 
	 */
	public static void saveGridPicsToWeb(final Context context, final String ed_content, final String picsCount) {
		
		//销毁AddNewLiaotieActivity
		AddNewLiaotieActivity activity=new AddNewLiaotieActivity();
		activity.finishAddNewLiaoTieActivity.finish();
		
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(context);
		String SDPATH = FileUtils.SDPATH;//保持一致，便于修改
		String SDPATH_THUMB = FileUtils.SDPATH_THUMB;
		
		List<String> bigPicPath=new ArrayList<>();	//大图路径
		List<String> thumbPicPath=new ArrayList<>();	
		final String[] AllPicPath;	//大图缩略图的所有路径。用于批量上传
		
		File bigPicDir =new File(SDPATH);
		File ThumbPicDir =new File(SDPATH_THUMB);
		
		File[] bigPics=bigPicDir.listFiles();
		File[] thumbPics=ThumbPicDir.listFiles();
		
		AllPicPath=new String[bigPics.length+thumbPics.length];

		int j=0;
		//得到路径集合
		for (int i = 0; i < bigPics.length; i++) {
			Log.e("", "AllPicPath长度："+AllPicPath.length);
			if (thumbPics[i].isFile()) {
				bigPicPath.add(bigPics[i].getAbsolutePath());
				thumbPicPath.add(thumbPics[i].getAbsolutePath());
				
				AllPicPath[j++]=thumbPics[i].getAbsolutePath();
				AllPicPath[j++]=bigPics[i].getAbsolutePath();
				
			}			
		}
		
		
		//批量上传服务器
		BmobFile.uploadBatch(context, AllPicPath, new UploadBatchListener() {
			
			@Override
			public void onSuccess(List<BmobFile> arg0, List<String> urls) {
				if(urls.size()==AllPicPath.length){//如果数量相等，则代表文件全部上传完成
		         
					//完成后删除文件
					FileUtils.deleteDir(new File(FileUtils.SDPATH));
					FileUtils.deleteDir(new File(FileUtils.SDPATH_THUMB));
					//将数据插入LiaoTie表，将文件在服务器的url保存在云端
					String bigPicPathString="";
					String thumbPicPathString="";
					for (int i = 0; i < urls.size(); i++) {
						if (i%2==0) { //得到thumbPicPath //i=0,2,4,6,8,10,12,14,16
							if (i!= urls.size()-2) {
								thumbPicPathString=thumbPicPathString+urls.get(i)+",";
							}else {
								thumbPicPathString=thumbPicPathString+urls.get(i);
							}
							
						}
						if (i%2==1) {//得到bigPicPath //i=1,3,5,7,9,11,13,15,17
							if (i!= urls.size()-1) {
								bigPicPathString=bigPicPathString+urls.get(i)+",";
							}else {
								bigPicPathString=bigPicPathString+urls.get(i);
							}
						}
					}
					
					//上传文本数据
					final LiaoTie liaoTie = new LiaoTie();
					liaoTie.setUsername(openSharePrefrenceHelper.getSP_CONFIG().getString(
							"accountname", ""));
					liaoTie.setSignificance(openSharePrefrenceHelper.getSP_CONFIG()
							.getString("significance", ""));
					liaoTie.setUsercontent(ed_content);
					liaoTie.setCommentCount(0 + "");
					liaoTie.setZanCount(0 + "");
					liaoTie.setSchool(openSharePrefrenceHelper.getSP_CONFIG().getString(
							"school", ""));
					liaoTie.setZanUsers("");
					liaoTie.setPics(picsCount);//设置张数
					liaoTie.setThumbPicPathString(thumbPicPathString);
					liaoTie.setBigPicPathString(bigPicPathString);
					liaoTie.save(context, new SaveListener() {
						@Override
						public void onSuccess() {
							//上传图片
							Toast.makeText(context, "发布成功,刷新查看", 1).show();
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							Toast.makeText(context, "发布失败", 1).show();
						}
					});
					
		        }
				
				//Log.e("", urls.get(urls.size()-1));
				
				//插入UploadGridPics 表
//				if (urls.size()%2==0) {
//					
//					if (tableName.equals("UploadGridPics")) {
//						UploadGridPics uploadGridPics=new UploadGridPics();
//						
//						uploadGridPics.setUsername(accountName);
//						uploadGridPics.setThumbPic(urls.get(k++));							
//						uploadGridPics.setBigPic(urls.get(k++));
//						uploadGridPics.setParentObjectId(parentObjectId);
//						uploadGridPics.setPicCount(AllPicPath.length/2 +"");
//						
//						uploadGridPics.save(context, new SaveListener() {
//							
//							@Override
//							public void onSuccess() {
//								
//								//Toast.makeText(context, k+"", 1).show();
//							}
//							
//							@Override
//							public void onFailure(int arg0, String arg1) {
//								// TODO Auto-generated method stub
//								
//							}
//						});
//
//						
//					}
//										
//				}
				
			}
			
			@Override
			public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
				//1、curIndex--表示当前第几个文件正在上传
		        //2、curPercent--表示当前上传文件的进度值（百分比）
		        //3、total--表示总的上传文件数
		        //4、totalPercent--表示总的上传进度（百分比）
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * 从网上下载用户上传的图片
	 * @param imageUrls 图片的url
	 * @param fileDir 图片本地保存的路径
	 */
	public static void downGridPicsFromWeb(final String[] imageUrls, final File fileDir) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url = null;
				InputStream in=null;
				FileOutputStream fos=null;
				File file=null;
				try {
					
					for (int i = 0; i < imageUrls.length; i++) {

						url = new URL(imageUrls[i]);
						
						Log.e("",url+"-------------------------");	
						
						in=url.openStream();
						File dir=new File(fileDir.getAbsolutePath());
						if (!dir.exists()) {
							dir.mkdirs();
						}
						
						file=new File(fileDir,imageUrls[i].substring(60));
						
						
						fos=new FileOutputStream(file);
						
						int len=0;
						byte[] b=new byte[1024];
						
						while ((len=in.read(b))!=-1) {
							fos.write(b,0,len);
						}
						//Log.d("logd", "下载："+file.getAbsolutePath());

					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (in != null) {
							in.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
		}).start();
		

	}

}
