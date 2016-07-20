package com.example.elephant.adpter;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.InfoIconActivity;
import com.example.elephant.MainComment;
import com.example.elephant.MainUIActivity;
import com.example.elephant.R;
import com.example.elephant.utils.DownloadAccountPicThread;
import com.example.elephant.utils.ImageHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentItemAdapter extends BaseAdapter{

	LayoutInflater inflater;
	Context context;
	List<Map<String, String>> list;
	Map<String, String> map;
	DownloadAccountPicThread downloadAccountPicThread;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	Handler handler;
	
	public CommentItemAdapter(Context context, Handler handler) {
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
	
	/**
	 * 设置list数据
	 * @param mlist
	 */
	public void setListData(List<Map<String, String>> mlist) {
		this.list=mlist;
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

		
		if (convertView==null) {
			myWidget=new MyWidget();
			
			if (position==0) {
				convertView=inflater.inflate(R.layout.comment_item_show_head, null);
				myWidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
				myWidget.nameTextView=(TextView) convertView.findViewById(R.id.item_name_tv_mian_ui);
				myWidget.sigTextView=(TextView) convertView.findViewById(R.id.item_signature_tv_mian_ui);
				myWidget.commentTextView=(TextView) convertView.findViewById(R.id.item_content_tv_main_ui4);
				myWidget.posttimeTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
				myWidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv_mian_ui);
				
				myWidget.booknameTextView = (TextView)  convertView.findViewById(R.id.item_content_tv_main_uiBookName);
				myWidget.bookAuthortTextView = (TextView)  convertView.findViewById(R.id.item_content_tv_main_uiBookAuthor);
				myWidget.bookISBNTextView = (TextView)  convertView.findViewById(R.id.item_content_tv_main_uiBookISBN);
				myWidget.bookPublisherTextView = (TextView)  convertView.findViewById(R.id.item_content_tv_main_uiBookPublisher);
				
			}else {
				convertView=inflater.inflate(R.layout.comment_item_ui, null);
				Log.d("logd", position+"");
				myWidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
				myWidget.nameTextView=(TextView) convertView.findViewById(R.id.tv_comment_username);
				myWidget.sigTextView=(TextView) convertView.findViewById(R.id.tv_comment_significance);
				myWidget.commentTextView=(TextView) convertView.findViewById(R.id.tv_comment_content);
				myWidget.posttimeTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
				myWidget.commentFloorTextView=(TextView) convertView.findViewById(R.id.tv_comment_floors);
				myWidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv_mian_ui);

			}

			convertView.setTag(myWidget);
			
		}else {
			myWidget=(MyWidget) convertView.getTag();
		}
		
		map=list.get(position);
		myWidget.nameTextView.setText(map.get("username"));
		myWidget.sigTextView.setText(map.get("significance"));
		myWidget.posttimeTextView.setText(map.get("createdAt"));		
		myWidget.schoolTextView.setText("["+map.get("school")+"]");		
		
		if (position==0) {
			myWidget.commentTextView.setText(map.get("usercontent"));
			
			myWidget.booknameTextView.setText("书名 : " +map.get("bookname"));		
			myWidget.bookAuthortTextView.setText("作者 : " +map.get("bookauthor"));		
			myWidget.bookISBNTextView.setText("ISBN : " +map.get("bookisbn"));		
			myWidget.bookPublisherTextView.setText("出版社 : "+map.get("bookpublisher"));		


		}else {
			myWidget.commentTextView.setText(map.get("usercontent"));
			myWidget.commentFloorTextView.setText(map.get("floorNum")+"楼");
		}
		
		/**
		 * 加载头像
		 */
		loadAccountImg(map);

		// 设置点击事件
		myWidget.imageView.setTag(position);// 设置标志位，点击才知道是哪个view
		myWidget.imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = Integer.parseInt(v.getTag().toString());
				map = list.get(position);
				Intent intent = new Intent(context, InfoIconActivity.class);
				intent.putExtra("username", map.get("username"));
				intent.putExtra("significance", map.get("significance"));
				context.startActivity(intent);
			}
		});

		//当评论区/评论内容被点击,弹出多项菜单
		myWidget.commentTextView.setTag(position);// 设置标志位，点击才知道是哪个view
		myWidget.commentTextView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final int position = Integer.parseInt(v.getTag().toString());
				map = list.get(position);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(context);

				if (map.get("username").equals(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""))) {

					builder.setItems(new String[]{"回复","复制","删除",}, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							if (which==0) {
								/**
								 * 回复------------------------------------------------------------
								 */
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get(map.get("comment")));
								Toast.makeText(context, "已复制",Toast.LENGTH_SHORT).show();
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
										MainComment.deleteListViewItem(context,position);	
									}
								});
								alertDialog.setNegativeButton("取消", null);
								alertDialog.show();
							}
						
						}
								
					});					
				}else {
					builder.setItems(new String[]{"回复","复制","举报"}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which==0) {
								/**
								 * 回复------------------------------------------------------------
								 */
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("comment")+"");
								Toast.makeText(context, "已复制",Toast.LENGTH_SHORT).show();
							}
							
							if (which==2) {
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
	
	/**
	 * 加载头像
	 * @param map
	 */
	private void loadAccountImg(final Map<String, String> map) {
		//当本地有图片文件时
		
				File localFile=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
				
				new BitmapFactory();//应该可以去掉
				
				/**
				 * 应该用数据库储存
				 * //当本地有图片文件时
				 */
				if (localFile.exists()) {			
					myWidget.imageView.setImageBitmap(
							ImageHelper.toRoundBitmap(
									BitmapFactory
									.decodeFile(localFile.getAbsolutePath())));		
//					Log.d("logd","当前已有图片用户..."+map.get("username"));
				}else {
				
					BmobQuery query=new BmobQuery("AccountImg");
					query.addWhereEndsWith("name", map.get("username"));
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
									JSONObject item=(JSONObject) iJsonObject.get("file");
							
									/**
									 * 将用户名称和头像地址储存,并开启线程下载
									 */
									Log.d("logd","存过图片的用户..."+map.get("username"));
									
									
									//子线程下载图片
									downloadAccountPicThread=new DownloadAccountPicThread(context,
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
					
					//强制设置默认头像
					myWidget.imageView.setImageResource(R.drawable.circle_head);		

				}
		
	}

	
	/**
	 * 解决convertView多布局复用加载错误
	 */
	@Override
	public int getItemViewType(int position) {
		if (position == 0) { // 定义不同位置的convertView类型
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {

		return 2;// 使用布局文件数量
	}
	
	class MyWidget{
		
		ImageView imageView;
		TextView nameTextView;
		TextView sigTextView;
		TextView commentTextView;
		TextView posttimeTextView;
		TextView commentFloorTextView;
		TextView schoolTextView;
		
		
		//头
		TextView booknameTextView;
		TextView bookAuthortTextView ;
		TextView bookISBNTextView;
		TextView bookPublisherTextView;
	}

}
