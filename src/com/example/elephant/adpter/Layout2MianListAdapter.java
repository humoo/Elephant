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
	List imageItem = new ArrayList<HashMap<String, Object>>();//��gridview��item����
	String[] imageUrls;
	File hasFile;
	String userName="";//��ֹ��Ϣ����
	String[] bigPicPathStrings,thumbPicPathStrings;
	int picCount;//ͼƬ����

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
			myWidget.gridView1=(GridView) convertView.findViewById(R.id.gridView1);//��ʾͼƬ
			
			//����
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

		//�����ϻ�ȡUploadGridPics���е�����
		
		
		//myWidget.gridView1.setTag(position);//���ñ�־λ����֪�����ĸ�gridView1
		
		picCount=Integer.parseInt(map.get("pics"));//����0��˵����ͼƬ����
		
		//Log.d("logd", map.get("username")+"...."+map.get("pics"));
		
		thumbPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
		bigPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
		
		if (picCount>0) {
			myWidget.gridView1.setVisibility(View.VISIBLE);
			//���Ի���󣬾Ͳ���ִ�����²���
			
			final String path=FileUtils.SDPATH_THUMB_FROM_WEB+map.get("username")+"/aaa";//��ַ�Ժ��޸�
			hasFile=new File(path);
			hasFile.mkdirs();
			
//			thumbPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
//			bigPicPathStrings=new String[Integer.parseInt(map.get("pics"))];
			thumbPicPathStrings=map.get("thumbPicPathString").split(",");
			bigPicPathStrings=map.get("bigPicPathString").split(",");
			
			//if (hasFile.exists()&&hasFile.listFiles().length>0) {
//				
//				
			//	Log.d("logd", "������ͼ");
//				
//				
			//}else {
				
				imageUrls=null;
				imageUrls=new String[Integer.parseInt(map.get("pics"))];//��ʼ��
				final ImageAdapter adapter=new ImageAdapter();			
				myWidget.gridView1.setAdapter(adapter);
				//myWidget.gridView1.setTag(position, adapter);
				
				query.addWhereEqualTo("parentObjectId",map.get("parentobjectId"));
				
				userName=map.get("username");
				
//				query.findObjects(context, new FindCallback() {
//					@Override
//					public void onFailure(int arg0, String arg1) {
//						Log.e("", "��ͼ");
//					}
//					@Override
//					public void onSuccess(final JSONArray arg0) {
//						
//						
//						Log.d("logd","imageUrls.length:"+imageUrls.length);//����
//						Log.d("logd", "----��ǰ�û�-----"+userName+",ͼƬ������"+arg0.length());
//						//����ͼƬ����
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
//									imageUrls[i]=jsonObject.getString("thumbPic");//����ͼ·��
//									
//									
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//								
//							}
//							
//							//��ȡ·�������������ͼ������
//							//ע����imageLoader�����ظ����ᵼ��ͼƬ����������
//							
//							//File fileDir=new File(path);
//							
//							hasFile.mkdirs();
//							Log.d("logd", "������"+map.get("username")+"�����ȣ�"+imageUrls.length);
//							ImageHelper.downGridPicsFromWeb(imageUrls,hasFile);
//							
////							for (int i = 0; i < imageUrls.length; i++) {
////
////
////								Log.e("logd",imageUrls[i]);
////								Log.e("logd",path+imageUrls[i].substring(60));
////								
////								Log.e("logd","����"+path+imageUrls[i]);
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
			
				myWidget.gridView1.setTag(position);//���ñ�־λ
				
				
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
						
					//	Toast.makeText(context, "��ǰ��"+ Integer.parseInt(map.get("pics")), 1).show();
						intent.putExtra("intentFrom", 2);
						intent.putExtra("position", position);
						intent.putExtra("thumbPicdata", map.get("thumbPicPathString"));
						intent.putExtra("bigPicdata", map.get("bigPicPathString"));
						intent.putExtra("count", Integer.parseInt(map.get("pics")));
					//	Log.e("", "map.get(pics):"+Integer.parseInt(map.get("pics")));
						context.startActivity(intent);

					}
				});
			//--------------��ʾͼƬ����ģ��-----

		}else {
				
//			imageUrls=null;
//			imageUrls=new String[0];//��ʼ��
//			final ImageAdapter adapter=new ImageAdapter();			
//			myWidget.gridView1.setAdapter(adapter);
			myWidget.gridView1.setVisibility(View.GONE);
			
		}
		
		
		//��У����ʾ
//		if (!map.get("school").equals(openSharePrefrenceHelper.getSP_CONFIG().getString("school", ""))) {
			myWidget.schoolTextView.setText("["+map.get("school")+"]");
//		}else {
//			myWidget.schoolTextView.setText("");
//		}
		
		/**
		 * ����ͷ��
		 */
		loadAccountImg(map,position);
		
		// ���õ���¼�
		myWidget.imageView.setTag(position);// ���ñ�־λ�������֪�����ĸ�view
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

		// ����˵�
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

					builder.setItems(new String[] { "��������", "ɾ��" },
							new DialogInterface.OnClickListener() {
								@SuppressWarnings("deprecation")
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										ClipboardManager copy = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										copy.setText(map.get("usercontent") + "");
										Toast.makeText(context, "�Ѹ���",
												Toast.LENGTH_SHORT).show();
									}
									if (which == 1) {
										AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												context);
										alertDialog.setTitle("��ʾ");
										alertDialog.setMessage("ȷ��Ҫɾ����");
										alertDialog
												.setPositiveButton(
														"ȷ��",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(DialogInterface dialog,int which) {
																/**
																 * ������adapter��ʹ��ɾ��removeViewAt();
																 */
																Layout_2.deleteListViewItem(context,
																		position,
																		map.get("thumbPicPathString"),
																		map.get("bigPicPathString"));
															}
														});
										alertDialog.setNegativeButton("ȡ��",	null);
										alertDialog.show();
									}

								}
							});
				} else {
					builder.setItems(new String[] { "��������", "�ղ�",
							"�ٱ�" }, new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								ClipboardManager copy = (ClipboardManager) context
										.getSystemService(Context.CLIPBOARD_SERVICE);
								copy.setText(map.get("usercontent") + "");
								Toast.makeText(context, "�Ѹ���",Toast.LENGTH_SHORT).show();
							}
							if (which == 1) {
								Toast.makeText(context, "�ղسɹ�",Toast.LENGTH_SHORT).show();
							}
							if (which == 2) {
								Toast.makeText(context, "�Ѿٱ�",Toast.LENGTH_SHORT).show();
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
	 * ����ͷ��
	 * @param map
	 * @param position
	 */
	private void loadAccountImg(final Map<String, String> map, final int position) {
		//��������ͼƬ�ļ�ʱ
		
		File localFile=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
		
		new BitmapFactory();
		//����ǵ�ǰ�û����򲻴��������ͼƬ,�ӱ���·����ȡ
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
					Log.d("logd","����ͼƬʧ��");
				}
	
				@Override
				public void onSuccess(JSONArray arg0) {

					try {
						/**
						 * ��ȡ������������ͷ����û��б��ͷ���ַ
						 */
						if (arg0.length()!=0) {

							JSONObject iJsonObject=arg0.getJSONObject(0);
							JSONObject item=(JSONObject) iJsonObject.get("file");			
							/**
							 * ���û����ƺ�ͷ���ַ����,�������߳�����
							 */
							downloadAccountPicThread=new DownloadAccountPicThread(context,
									item.get("filename").toString(),
									item.get("url").toString(),handler);
							
							downloadAccountPicThread.start();			
						}		
					} catch (JSONException e) {
						e.printStackTrace();
						Log.d("logd","����");
					}
					
				}
			});		
			//ǿ������Ĭ��ͷ��
			myWidget.imageView.setImageResource(R.drawable.circle_head);		
		}		
	}
	
	
	/**
	 * �Ż�adapter
	 * @author Administrator
	 *
	 */
	class MyWidget{
		ImageView imageView;//ͷ��
		ImageView main_item_more;//����˵�
		TextView nameTextView;//name
		TextView sigTextView;//significance	
		TextView zanCountTextView;//zanCount
		TextView schoolTextView;//school
		TextView contentTextView;//content
		TextView createAtTextView;//createAt
		TextView commentCountTextView;//commentCount
		
		GridView gridView1;
		ImageView showImg;//ͷ��

		/**
		 * ȱzanUsers��pics
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
				Log.e("", "thumbPicPathStrings["+position+"]λ�ã�"+thumbPicPathStrings[position]);
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
