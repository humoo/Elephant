package com.example.elephant.adpter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.InfoIconActivity;
import com.example.elephant.Layout_2;
import com.example.elephant.MainUIActivity;
import com.example.elephant.R;
import com.example.elephant.ShowPhotoActivity;
import com.example.elephant.adpter.MainUIItemAdapter.MyWidget;
import com.example.elephant.db.UploadGridPics;
import com.example.elephant.utils.DownLoadUtil;
import com.example.elephant.utils.DownloadAccountPicThread;
import com.example.elephant.utils.FileUtils;
import com.example.elephant.utils.ImageHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter.ViewBinder;

public class Layout2MianListAdapter extends BaseAdapter{

	Context context;
	LayoutInflater inflater;
	List<Map<String,String>> list;
	Map<String,String> map;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	DownloadAccountPicThread downloadAccountPicThread;
	Handler handler;
	String parentobjectId="";
	UploadGridPics uploadGridPics;
	BmobQuery query;
	List imageItem = new ArrayList<HashMap<String, Object>>();//放gridview子item数据
	String[] imageUrls;
	File hasFile;
	String userName="";//防止信息错误
	String[] bigPicPathStrings,thumbPicPathStrings;
	int picCount;//图片数量

	public Layout2MianListAdapter(Context context, Handler handler) {
		this.context=context;
		this.handler=handler;
		inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		openSharePrefrenceHelper=new OpenSharePrefrenceHelper(context);
		File fileDir=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/");
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
		uploadGridPics=new UploadGridPics();
		query=new BmobQuery("UploadGridPics");
	}
	
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView==null) {
			convertView=inflater.inflate(R.layout.layout2_item, null);
			myWidget=new MyWidget();
			
			myWidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
			myWidget.main_item_more=(ImageView) convertView.findViewById(R.id.main_item_more);
			myWidget.gridView1=(GridView) convertView.findViewById(R.id.gridView1);//显示图片
			
			//数据
			myWidget.nameTextView=(TextView) convertView.findViewById(R.id.item_name_tv_mian_ui);
			myWidget.sigTextView=(TextView) convertView.findViewById(R.id.item_signature_tv_mian_ui);
			myWidget.zanCountTextView=(TextView) convertView.findViewById(R.id.tv_zan_count);
			myWidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv_mian_ui);		
			myWidget.contentTextView=(TextView) convertView.findViewById(R.id.item_content_tv);
			myWidget.createAtTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
			myWidget.commentCountTextView=(TextView) convertView.findViewById(R.id.tv_view_comment_count);
						
			convertView.setTag(myWidget);
		}else {
			myWidget=(MyWidget) convertView.getTag();
		}
		
		map=list.get(position);
		
		myWidget.nameTextView.setText(map.get("username"));
		myWidget.sigTextView.setText(map.get("significance"));
		myWidget.zanCountTextView.setText(map.get("zanCount"));
		myWidget.contentTextView.setText(map.get("usercontent"));
		myWidget.createAtTextView.setText(map.get("createdAt"));
		myWidget.commentCountTextView.setText(map.get("commentCount"));

		//从网上获取UploadGridPics表中的数据
		
		
		//myWidget.gridView1.setTag(position);//设置标志位，才知道是哪个gridView1
		
		picCount=Integer.parseInt(map.get("pics"));//大于0，说明有图片数据
		
		//Log.d("logd", map.get("username")+"...."+map.get("pics"));
		
		thumbPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
		bigPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
		
		if (picCount>0) {
			myWidget.gridView1.setVisibility(View.VISIBLE);
			//当以缓存后，就不再执行以下步骤
			
			final String path=FileUtils.SDPATH_THUMB_FROM_WEB+map.get("username")+"/aaa";//地址以后修改
			hasFile=new File(path);
			hasFile.mkdirs();
			
//			thumbPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
//			bigPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
			thumbPicPathStrings=map.get("thumbPicPathString").split(",");
			bigPicPathStrings=map.get("bigPicPathString").split(",");
			
			//if (hasFile.exists()&&hasFile.listFiles().length>0) {
//				
//				
			//	Log.d("logd", "本地有图");
//				
//				
			//}else {
				
				imageUrls=null;
				imageUrls=new String[Integer.parseInt(map.get("pics"))];//初始化
				final ImageAdapter adapter=new ImageAdapter();			
				myWidget.gridView1.setAdapter(adapter);
				//myWidget.gridView1.setTag(position, adapter);
				
				query.addWhereEqualTo("parentObjectId",map.get("parentobjectId"));
				
				userName=map.get("username");
				
//				query.findObjects(context, new FindCallback() {
//					@Override
//					public void onFailure(int arg0, String arg1) {
//						Log.e("", "无图");
//					}
//					@Override
//					public void onSuccess(final JSONArray arg0) {
//						
//						
//						Log.d("logd","imageUrls.length:"+imageUrls.length);//错误
//						Log.d("logd", "----当前用户-----"+userName+",图片张数："+arg0.length());
//						//当有图片数据
//						if (arg0.length()>0) {
//							JSONObject jsonObject=null;
//							
//							imageUrls=new String[arg0.length()];
//							
//							for (int i = 0; i < arg0.length(); i++) {
//								
//								try {
//									jsonObject=arg0.getJSONObject(i);
//									//Log.e("","imageUrls["+i+"]" );
//									
//									imageUrls[i]=jsonObject.getString("thumbPic");//缩略图路径
//									
//									
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//								
//							}
//							
//							//获取路径完毕下载缩略图到本地
//							//注：与imageLoader功能重复，会导致图片被下载两次
//							
//							//File fileDir=new File(path);
//							
//							hasFile.mkdirs();
//							Log.d("logd", "来下载"+map.get("username")+"，长度："+imageUrls.length);
//							ImageHelper.downGridPicsFromWeb(imageUrls,hasFile);
//							
////							for (int i = 0; i < imageUrls.length; i++) {
////
////
////								Log.e("logd",imageUrls[i]);
////								Log.e("logd",path+imageUrls[i].substring(60));
////								
////								Log.e("logd","下载"+path+imageUrls[i]);
////							
////								
////							}
//							
//							
////							Bitmap bmp = BitmapFactory.decodeResource(
////									context.getResources(), R.drawable.banner);
////							List imageItem = new ArrayList<HashMap<String, Object>>();
////							for (int i = 0; i < imageUrls.length; i++) {
////								HashMap<String, Object> hashmap = new HashMap<String, Object>();
////								hashmap.put("itemImage", bmp);
////								imageItem.add(hashmap);
////							}
////							SimpleAdapter simpleAdapter = new SimpleAdapter(context,
////									imageItem, R.layout.griditem_addpic,
////									new String[] { "itemImage" },
////									new int[] { R.id.imageView1 });
////							simpleAdapter.setViewBinder(new ViewBinder() {
////								@Override
////								public boolean setViewValue(View view, Object data,
////										String textRepresentation) {
////									if (view instanceof ImageView
////											&& data instanceof Bitmap) {
////										ImageView i = (ImageView) view;
////										i.setLayoutParams(new RelativeLayout.LayoutParams(
////												RelativeLayout.LayoutParams.MATCH_PARENT,
////												RelativeLayout.LayoutParams.MATCH_PARENT));
////										i.setScaleType(ScaleType.CENTER_CROP);
////										//i.setImageBitmap((Bitmap) data);
////										for (int j = 0; j < imageUrls.length; j++) {
////											ImageLoader.getInstance().displayImage(imageUrls[j],i);
////										}
////										
////										return true;
////									}
////									return false;
////								}
////							});
//							myWidget.gridView1.setFocusable(false);
//							myWidget.gridView1.setPressed(false);
////							// myWidget.gridView1.setEnabled(false);
////							myWidget.gridView1.setAdapter(simpleAdapter);
//						//	myWidget.gridView1.setTag(position);
//							
//							//if (myWidget.gridView1.getTag().equals(position)) {
//							
//							//myWidget.gridView1.setVisibility(View.VISIBLE);
//								adapter.notifyDataSetChanged();
////								notifyDataSetChanged();
//								//picCount=0;
//						//	}
//							
//						myWidget.gridView1
//								.setOnItemClickListener(new OnItemClickListener() {
//
//									@Override
//									public void onItemClick(
//											AdapterView<?> parent, View view,
//											int position, long id) {
//
//										Intent intent = new Intent(context,
//												ShowPhotoActivity.class);
//										intent.putExtra("intentFrom", 2);
//										intent.putExtra("position", position);
//										intent.putExtra("count", arg0.length());
//										context.startActivity(intent);
//
//									}
//								});
//							
//							
//						}
//						
//					}
//				});
			//}
			
				myWidget.gridView1.setTag(position);//设置标志位
				
				
				myWidget.gridView1.setFocusable(false);
				myWidget.gridView1.setPressed(false);
				adapter.notifyDataSetChanged();
				
				myWidget.gridView1
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(
							AdapterView<?> parent, View view,
							int position, long id) {
						
						final int myPostion =Integer.parseInt(parent.getTag().toString());
						map=list.get(myPostion);
						
						Intent intent = new Intent(context,
								ShowPhotoActivity.class);
						
					//	Toast.makeText(context, "当前："+ Integer.parseInt(map.get("pics")), 1).show();
						intent.putExtra("intentFrom", 2);
						intent.putExtra("position", position);
						intent.putExtra("thumbPicdata", map.get("thumbPicPathString"));
						intent.putExtra("bigPicdata", map.get("bigPicPathString"));
						intent.putExtra("count", Integer.parseInt(map.get("pics")));
					//	Log.e("", "map.get(pics):"+Integer.parseInt(map.get("pics")));
						context.startActivity(intent);

					}
				});
			//--------------显示图片数据模拟-----

		}else {
				
//			imageUrls=null;
//			imageUrls=new String[0];//初始化
//			final ImageAdapter adapter=new ImageAdapter();			
//			myWidget.gridView1.setAdapter(adapter);
			myWidget.gridView1.setVisibility(View.GONE);
			
		}
		
		
		//本校不显示
//		if (!map.get("school").equals(openSharePrefrenceHelper.getSP_CONFIG().getString("school", ""))) {
			myWidget.schoolTextView.setText("["+map.get("school")+"]");
//		}else {
//			myWidget.schoolTextView.setText("");
//		}
		
		/**
		 * 加载头像
		 */
		loadAccountImg(map,position);
		
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

		// 多项菜单
		myWidget.main_item_more.setTag(position);
		myWidget.main_item_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int position = Integer.parseInt(v.getTag().toString());
				map = list.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				if (map.get("username").equals(
						openSharePrefrenceHelper.getSP_CONFIG().getString(
								"accountname", ""))) {

					builder.setItems(new String[] { "复制内容", "删除" },
							new DialogInterface.OnClickListener() {
								@SuppressWarnings("deprecation")
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										ClipboardManager copy = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										copy.setText(map.get("usercontent") + "");
										Toast.makeText(context, "已复制",
												Toast.LENGTH_SHORT).show();
									}
									if (which == 1) {
										AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												context);
										alertDialog.setTitle("提示");
										alertDialog.setMessage("确定要删除吗？");
										alertDialog
												.setPositiveButton(
														"确定",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(DialogInterface dialog,int which) {
																/**
																 * 不能在adapter中使用删除removeViewAt();
																 */
																Layout_2.deleteListViewItem(context,
																		position,
																		map.get("thumbPicPathString"),
																		map.get("bigPicPathString"));
															}
														});
										alertDialog.setNegativeButton("取消",	null);
										alertDialog.show();
									}

								}
							});
				} else {
					builder.setItems(new String[] { "复制内容", "收藏",
							"举报" }, new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								ClipboardManager copy = (ClipboardManager) context
										.getSystemService(Context.CLIPBOARD_SERVICE);
								copy.setText(map.get("usercontent") + "");
								Toast.makeText(context, "已复制",Toast.LENGTH_SHORT).show();
							}
							if (which == 1) {
								Toast.makeText(context, "收藏成功",Toast.LENGTH_SHORT).show();
							}
							if (which == 2) {
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
	 * 下载头像
	 * @param map
	 * @param position
	 */
	private void loadAccountImg(final Map<String, String> map, final int position) {
		//当本地有图片文件时
		
		File localFile=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
		
		new BitmapFactory();
		//如果是当前用户，则不从网络加载图片,从本地路径读取
		if (localFile.exists()) {			
			myWidget.imageView.setImageBitmap(
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
			myWidget.imageView.setImageResource(R.drawable.circle_head);		
		}		
	}
	
	
	/**
	 * 优化adapter
	 * @author Administrator
	 *
	 */
	class MyWidget{
		ImageView imageView;//头像
		ImageView main_item_more;//多项菜单
		TextView nameTextView;//name
		TextView sigTextView;//significance	
		TextView zanCountTextView;//zanCount
		TextView schoolTextView;//school
		TextView contentTextView;//content
		TextView createAtTextView;//createAt
		TextView commentCountTextView;//commentCount
		
		GridView gridView1;
		ImageView showImg;//头像

		/**
		 * 缺zanUsers。pics
		 */
	}
	
	
	class ImageAdapter extends BaseAdapter {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				//LayoutInflater layoutInflator = LayoutInflater.from(context);
				view = inflater.inflate(R.layout.griditem_addpic, null);
				holder = new ViewHolder();
				holder.image = (ImageView) view.findViewById(R.id.imageView1);
				holder.image.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.MATCH_PARENT));
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			if (thumbPicPathStrings.length>0) {
//				holder.image.setTag(thumbPicPathStrings[position]);
				ImageLoader.getInstance().displayImage(thumbPicPathStrings[position],
						holder.image);
				Log.e("", "thumbPicPathStrings["+position+"]位置："+thumbPicPathStrings[position]);
			}else {
				holder.image.setLayoutParams(new RelativeLayout.LayoutParams(
						0,0));
			}
			
			
			return view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageUrls.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return imageUrls[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
		
		
	}
	
	private static class ViewHolder {
		ImageView image;
	}



}
