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
		 * ��Ƭ·��������adapter�ѽ������˴�ʡȥ
		 */
		
	}
	
	/**
	 * ����list����
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
			if (position==0) {		//��listview�����������ݲ���		
				convertView=inflater.inflate(R.layout.liaotie_item_show_head, null);

				mywidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
				mywidget.nameTextView=(TextView) convertView.findViewById(R.id.tv_comment_username);
				mywidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv);
				mywidget.sigTextView=(TextView) convertView.findViewById(R.id.tv_comment_significance);
				mywidget.createAtTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
				mywidget.contentTextView=(TextView) convertView.findViewById(R.id.item_content_tv);
				//��
				mywidget.zanCountTextView=(TextView) convertView.findViewById(R.id.tv_zan_count);
			}else {//���۵Ĳ���

				convertView=inflater.inflate(R.layout.liaotie_comment_item, null);
				mywidget.imageView=(ImageView) convertView.findViewById(R.id.item_img_lv_item_ui);
				mywidget.nameTextView=(TextView) convertView.findViewById(R.id.tv_comment_username);
				mywidget.schoolTextView=(TextView) convertView.findViewById(R.id.item_school_tv);
				mywidget.sigTextView=(TextView) convertView.findViewById(R.id.tv_comment_significance);
				mywidget.createAtTextView=(TextView) convertView.findViewById(R.id.tv_post_time);
				mywidget.contentTextView=(TextView) convertView.findViewById(R.id.item_content_tv);
				//¥��
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
			mywidget.floorsTextView.setText(map.get("floorNum")+"¥");
		}
		
		/**
		 * ����ͷ��
		 */
		loadAccountImg(map,position);

		// ���õ���¼�
		mywidget.imageView.setTag(position);// ���ñ�־λ�������֪�����ĸ�view
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
		
		//��������/�������ݱ����,��������˵�
		mywidget.contentTextView.setTag(position);// ���ñ�־λ�������֪�����ĸ�view
		mywidget.contentTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int position = Integer.parseInt(v.getTag().toString());
				map = list.get(position);

				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				if (map.get("username").equals(
						openSharePrefrenceHelper.getSP_CONFIG().getString(
								"accountname", ""))) {

					builder.setItems(new String[] { "�ظ�", "����", "ɾ��", },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										/**
										 * �ظ�----------------------------------
										 * --------------------------
										 */
									}
									if (which == 1) {
										ClipboardManager copy = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										copy.setText(map.get(map.get("comment")));
										Toast.makeText(context, "�Ѹ���",
												Toast.LENGTH_SHORT).show();
									}
									if (which == 2) {
										AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												context);
										alertDialog.setTitle("��ʾ");
										alertDialog.setMessage("ȷ��Ҫɾ����");
										alertDialog
												.setPositiveButton(
														"ȷ��",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																/**
																 * ������adapter��ʹ��ɾ��removeViewAt
																 * ();
																 */
																// MainUIActivity.mainListView.removeViewAt(position);//ɾ���������ϵ�view
																LiaoTieCommentActivity
																		.deleteListViewItem(
																				context,
																				position);
															}
														});
										alertDialog.setNegativeButton("ȡ��",
												null);
										alertDialog.show();
									}

								}

							});
				} else {
					builder.setItems(new String[] { "�ظ�", "����", "�ٱ�" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										/**
										 * �ظ�----------------------------------
										 * --------------------------
										 */
									}
									if (which == 1) {
										ClipboardManager copy = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										copy.setText(map.get("comment") + "");
										Toast.makeText(context, "�Ѹ���",
												Toast.LENGTH_SHORT).show();
									}

									if (which == 2) {
										Toast.makeText(context, "�Ѿٱ�",
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
	 * ����ͷ��
	 */
	private void loadAccountImg(Map<String, String> map2, int position) {
		
		//��������ͼƬ�ļ�ʱ
		
				File localFile=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
				
				new BitmapFactory();
				//����ǵ�ǰ�û����򲻴��������ͼƬ,�ӱ���·����ȡ
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
					mywidget.imageView.setImageResource(R.drawable.circle_head);		
				}
	}

	/**
	 * ���convertView�಼�ָ��ü��ش���
	 */
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {  //���岻ͬλ�õ�convertView����
			return 0;
		} else {
			return 1;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		
		return 2;//ʹ�ò����ļ�����
	}
	
	class MyWidget{
		
		ImageView imageView;//ͷ��
		
		TextView nameTextView;//name
		TextView sigTextView;//significance	
		TextView zanCountTextView;//zanCount
		TextView schoolTextView;//school
		TextView contentTextView;//content
		TextView createAtTextView;//createAt
		TextView commentCountTextView;//commentCount
		TextView floorsTextView;//floors
		
		
		/**
		 * ȱzanUsers��pics
		 */
	}
}
