package com.example.elephant.adpter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.a.a.madness;
import com.example.elephant.InfoIconActivity;
import com.example.elephant.LiaoTieCommentActivity;
import com.example.elephant.MainComment;
import com.example.elephant.R;
import com.example.elephant.adpter.Layout2MianListAdapter.MyWidget;
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

public class LiaoTieCommentAdapter extends BaseAdapter{

	Context context;
	List<Map<String, String>> list;
	LayoutInflater inflater;
	Map<String,String> map;
	DownloadAccountPicThread downloadAccountPicThread;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	Handler handler;
	public LiaoTieCommentAdapter(Context context, Handler handler) {
		this.context=context;
		this.handler=handler;
		inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		map=new HashMap<>();
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(context);
		
		
		/**
		 * 相片路径，其他adapter已建立，此处省去
		 */
		
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

	MyWidget mywidget;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView==null) {
			mywidget=new MyWidget();

			Log.d("logd", position+"--"+list.get(position).get("username"));
			if (position==0) {		//从listview传过来的数据布局		
				convertView=inflater.inflate(R.layout.liaotie_item_show_head, null);

				mywidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
				mywidget.nameTextView=(TextView) convertView.findViewById(R.id.tv_comment_username);
				mywidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv);
				mywidget.sigTextView=(TextView) convertView.findViewById(R.id.tv_comment_significance);
				mywidget.createAtTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
				mywidget.contentTextView=(TextView) convertView.findViewById(R.id.item_content_tv);
				//赞
				mywidget.zanCountTextView=(TextView) convertView.findViewById(R.id.tv_zan_count);
			}else {//评论的布局

				convertView=inflater.inflate(R.layout.liaotie_comment_item, null);
				mywidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
				mywidget.nameTextView=(TextView) convertView.findViewById(R.id.tv_comment_username);
				mywidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv);
				mywidget.sigTextView=(TextView) convertView.findViewById(R.id.tv_comment_significance);
				mywidget.createAtTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
				mywidget.contentTextView=(TextView) convertView.findViewById(R.id.item_content_tv);
				//楼层
				mywidget.floorsTextView=(TextView) convertView.findViewById(R.id.tv_comment_floors);
			}
			convertView.setTag(mywidget);

			
		}else {
			mywidget=(MyWidget) convertView.getTag();
		}
		
		map=list.get(position);
		mywidget.nameTextView.setText(map.get("username"));
		mywidget.schoolTextView.setText("["+map.get("school")+"]");
		mywidget.contentTextView.setText(map.get("usercontent"));
		mywidget.sigTextView.setText(map.get("significance"));
		mywidget.createAtTextView.setText(map.get("createdAt"));
		if (position==0) {
			mywidget.zanCountTextView.setText(map.get("zanCount"));
		}else {
			mywidget.floorsTextView.setText(map.get("floorNum")+"楼");
		}
		
		/**
		 * 加载头像
		 */
		loadAccountImg(map,position);

		// 设置点击事件
		mywidget.imageView.setTag(position);// 设置标志位，点击才知道是哪个view
		mywidget.imageView.setOnClickListener(new OnClickListener() {
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
		mywidget.contentTextView.setTag(position);// 设置标志位，点击才知道是哪个view
		mywidget.contentTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int position = Integer.parseInt(v.getTag().toString());
				map = list.get(position);

				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				if (map.get("username").equals(
						openSharePrefrenceHelper.getSP_CONFIG().getString(
								"accountname", ""))) {

					builder.setItems(new String[] { "回复", "复制", "删除", },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										/**
										 * 回复----------------------------------
										 * --------------------------
										 */
									}
									if (which == 1) {
										ClipboardManager copy = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										copy.setText(map.get(map.get("comment")));
										Toast.makeText(context, "已复制",
												Toast.LENGTH_SHORT).show();
									}
									if (which == 2) {
										AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												context);
										alertDialog.setTitle("提示");
										alertDialog.setMessage("确定要删除吗？");
										alertDialog
												.setPositiveButton(
														"确定",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																/**
																 * 不能在adapter中使用删除removeViewAt
																 * ();
																 */
																// MainUIActivity.mainListView.removeViewAt(position);//删除主界面上的view
																LiaoTieCommentActivity
																		.deleteListViewItem(
																				context,
																				position);
															}
														});
										alertDialog.setNegativeButton("取消",
												null);
										alertDialog.show();
									}

								}

							});
				} else {
					builder.setItems(new String[] { "回复", "复制", "举报" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										/**
										 * 回复----------------------------------
										 * --------------------------
										 */
									}
									if (which == 1) {
										ClipboardManager copy = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										copy.setText(map.get("comment") + "");
										Toast.makeText(context, "已复制",
												Toast.LENGTH_SHORT).show();
									}

									if (which == 2) {
										Toast.makeText(context, "已举报",
												Toast.LENGTH_SHORT).show();
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
	 */
	private void loadAccountImg(Map<String, String> map2, int position) {
		
		//当本地有图片文件时
		
				File localFile=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
				
				new BitmapFactory();
				//如果是当前用户，则不从网络加载图片,从本地路径读取
				if (localFile.exists()) {			
					mywidget.imageView.setImageBitmap(
							ImageHelper.toRoundBitmap(
									BitmapFactory
									.decodeFile(localFile.getAbsolutePath())));		
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
					mywidget.imageView.setImageResource(R.drawable.circle_head);		
				}
	}

	/**
	 * 解决convertView多布局复用加载错误
	 */
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {  //定义不同位置的convertView类型
			return 0;
		} else {
			return 1;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		
		return 2;//使用布局文件数量
	}
	
	class MyWidget{
		
		ImageView imageView;//头像
		
		TextView nameTextView;//name
		TextView sigTextView;//significance	
		TextView zanCountTextView;//zanCount
		TextView schoolTextView;//school
		TextView contentTextView;//content
		TextView createAtTextView;//createAt
		TextView commentCountTextView;//commentCount
		TextView floorsTextView;//floors
		
		
		/**
		 * 缺zanUsers。pics
		 */
	}
}
