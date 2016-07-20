package com.example.elephant.adpter;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.InfoIconActivity;
import com.example.elephant.MainUIActivity;
import com.example.elephant.R;
import com.example.elephant.utils.DownloadAccountPicThread;
import com.example.elephant.utils.ImageHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;

public class MainUIItemAdapter extends BaseAdapter{

	Context context;
	LayoutInflater inflater;//布局填充器
	List<Map<String,String>> list;
	Map<String,String> map;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	DownloadAccountPicThread downloadAccountPicThread;	
	Handler handler;
	
	public MainUIItemAdapter(Context context, Handler handler) {
		this.context=context;
		this.handler=handler;
		inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(context);		
		
		File fileDir=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/");
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

	}

	
	public void setListData(List<Map<String, String>> mlist) {
		
		list=mlist;

	}
	@Override
	public int getCount() {	
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	MyWidget myWidget;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		/**
		 * 优化baseadapter
		 */
		if (convertView==null) {
			
			convertView=inflater.inflate(R.layout.lv_item_main_ui,null);  
			myWidget=new MyWidget();
			
			myWidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
			myWidget.main_item_more=(ImageView) convertView.findViewById(R.id.main_item_more);
			myWidget.nameTextView=(TextView) convertView.findViewById(R.id.item_name_tv_mian_ui);
			myWidget.sigTextView=(TextView) convertView.findViewById(R.id.item_signature_tv_mian_ui);
			myWidget.viewCountTextView=(TextView) convertView.findViewById(R.id.tv_view_count);
			myWidget.contentTextView=(TextView) convertView.findViewById(R.id.item_content_tv_main_ui4);
			myWidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv_mian_ui);
			
			myWidget.booknameTextView=(TextView) convertView.findViewById(R.id.item_content_tv_main_uiBookName);
			myWidget.bookAuthortTextView=(TextView) convertView.findViewById(R.id.item_content_tv_main_uiBookAuthor);
			myWidget.bookISBNTextView=(TextView) convertView.findViewById(R.id.item_content_tv_main_uiBookISBN);
			myWidget.bookPublisherTextView=(TextView) convertView.findViewById(R.id.item_content_tv_main_uiBookPublisher);
			myWidget.posttimeTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
			myWidget.commentcountTextView=(TextView) convertView.findViewById(R.id.tv_view_comment_count);			
			convertView.setTag(myWidget);

		}else {
			myWidget=(MyWidget) convertView.getTag();
		}
		
		map=list.get(position);
	
		myWidget.nameTextView.setText(map.get("username"));
		myWidget.sigTextView.setText(map.get("significance"));
		myWidget.schoolTextView.setText("["+map.get("school")+"]");
		myWidget.viewCountTextView.setText(map.get("viewcount"));
		myWidget.contentTextView.setText(map.get("usercontent"));
		myWidget.posttimeTextView.setText(map.get("createdAt"));
		myWidget.commentcountTextView.setText(map.get("commentcount"));
		myWidget.booknameTextView.setText("书名 : "+map.get("bookname"));
//		myWidget.booknameTextView.setTextColor(Color.RED);//书名设置红色
		myWidget.bookISBNTextView.setText("ISBN : "+map.get("bookisbn"));
		myWidget.bookAuthortTextView.setText("作者 : "+map.get("bookauthor"));
		myWidget.bookPublisherTextView.setText("出版社 : "+map.get("bookpublisher"));		
		/**
		 * 加载头像
		 */

		loadAccountImg(map,position);
		
		//设置点击事件
		myWidget.imageView.setTag(position);//设置标志位，点击才知道是哪个view
		myWidget.imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position =Integer.parseInt(v.getTag().toString());
				map=list.get(position);
				Intent intent =new Intent(context,InfoIconActivity.class);	
				intent.putExtra("username", map.get("username"));
				intent.putExtra("significance", map.get("significance"));
				context.startActivity(intent);				
			}
		});
		
		//多项菜单
		myWidget.main_item_more.setTag(position);
		myWidget.main_item_more.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final int position =Integer.parseInt(v.getTag().toString());
				map=list.get(position);
				AlertDialog.Builder builder=new AlertDialog.Builder(context);

				if (map.get("username").equals(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""))) {

					builder.setItems(new String[]{"复制书名","复制ISBN","删除"}, new DialogInterface.OnClickListener() {						
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							if (which==0) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookname")+""); 
								Toast.makeText(context, "书名已复制",Toast.LENGTH_SHORT).show();
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookisbn")+""); 
								Toast.makeText(context, "ISBN已复制",Toast.LENGTH_SHORT).show();
							}
							if (which==2) {
								AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
								alertDialog.setTitle("提示");
								alertDialog.setMessage("确定要删除吗？");
								alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {				
									@Override
									public void onClick(DialogInterface dialog, int which) {
										/**
										 * 不能在adapter中使用删除removeViewAt();
										 */
//										MainUIActivity.mainListView.removeViewAt(position);//删除主界面上的view
										MainUIActivity.deleteListViewItem(context,position);	
									}
								});
								alertDialog.setNegativeButton("取消", null);
								alertDialog.show();
							}
							
						}				
					});					
				}else {
					builder.setItems(new String[]{"复制书名","复制ISBN","收藏","举报"}, new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which==0) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookname")+""); 
								Toast.makeText(context, "书名已复制",Toast.LENGTH_SHORT).show();
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookisbn")+""); 
								Toast.makeText(context, "ISBN已复制",Toast.LENGTH_SHORT).show();
							}
							if (which==2) {
								Toast.makeText(context, "收藏成功",Toast.LENGTH_SHORT).show();
							}
							if (which==3) {
								Toast.makeText(context, "已举报",Toast.LENGTH_SHORT).show();
							}
												
						}
					});
				}
				builder.create().show();			
			}
		});
		return convertView;
	}
	
	private void loadAccountImg(final Map<String, String> map, final int position) {
		//当本地有图片文件时
		
		File localFile=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
		
		new BitmapFactory();
		//如果本地已存在头像，则不从网络加载图片,从本地路径读取
		if (localFile.exists()&&localFile.length()>0) {		
//			Toast.makeText(context, localFile.exists()+".."+localFile.length(), 1).show();
			myWidget.imageView.setImageBitmap(
					ImageHelper.toRoundBitmap(
							BitmapFactory
							.decodeFile(localFile.getAbsolutePath())));		
		}else {
		
			BmobQuery query=new BmobQuery("AccountImg");
			query.addWhereEqualTo("name", map.get("username"));
			query.findObjects(context, new FindCallback() {			
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
							JSONObject item=(JSONObject) iJsonObject.get("file");//解析json数据发现有filename，url字段			
							/**
							 * 将用户名称和头像地址储存,并开启线程下载
							 */
							downloadAccountPicThread=new DownloadAccountPicThread(context,
									map.get("username")+"_accountImg.jpg",
									item.get("url").toString(),handler);
							Log.e("logd","图片地址..."+map.get("username")+"_accountImg.jpg"+"---"+item.get("url").toString());
							downloadAccountPicThread.start();			
						}		
					} catch (JSONException e) {
						e.printStackTrace();
						Log.d("logd","错误");
					}
					
				}
			});		
			//强制设置默认头像
			myWidget.imageView.setImageResource(R.drawable.circle_head);		

		}		
	}

	class MyWidget{
		ImageView imageView;//头像
		ImageView main_item_more;//多项菜单
		TextView nameTextView;
		TextView sigTextView;
		TextView viewCountTextView;
		TextView schoolTextView;
		
		TextView booknameTextView;
		TextView bookISBNTextView;
		TextView bookAuthortTextView;
		TextView bookPublisherTextView;		
		TextView contentTextView;
		TextView posttimeTextView;
		TextView commentcountTextView;
	}

	
	
	

}
