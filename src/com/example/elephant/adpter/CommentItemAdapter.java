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
			
			myWidget.booknameTextView.setText("���� : " +map.get("bookname"));		
			myWidget.bookAuthortTextView.setText("���� : " +map.get("bookauthor"));		
			myWidget.bookISBNTextView.setText("ISBN : " +map.get("bookisbn"));		
			myWidget.bookPublisherTextView.setText("������ : "+map.get("bookpublisher"));		


		}else {
			myWidget.commentTextView.setText(map.get("usercontent"));
			myWidget.commentFloorTextView.setText(map.get("floorNum")+"¥");
		}
		
		/**
		 * ����ͷ��
		 */
		loadAccountImg(map);

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

		//��������/�������ݱ����,��������˵�
		myWidget.commentTextView.setTag(position);// ���ñ�־λ�������֪�����ĸ�view
		myWidget.commentTextView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final int position = Integer.parseInt(v.getTag().toString());
				map = list.get(position);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(context);

				if (map.get("username").equals(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""))) {

					builder.setItems(new String[]{"�ظ�","����","ɾ��",}, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							if (which==0) {
								/**
								 * �ظ�------------------------------------------------------------
								 */
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get(map.get("comment")));
								Toast.makeText(context, "�Ѹ���",Toast.LENGTH_SHORT).show();
							}
							if (which==2) {
								AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
								alertDialog.setTitle("��ʾ");
								alertDialog.setMessage("ȷ��Ҫɾ����");
								alertDialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {				
									@Override
									public void onClick(DialogInterface dialog, int which) {
										/**
										 * ������adapter��ʹ��ɾ��removeViewAt();
										 */
//										MainUIActivity.mainListView.removeViewAt(position);//ɾ���������ϵ�view
										MainComment.deleteListViewItem(context,position);	
									}
								});
								alertDialog.setNegativeButton("ȡ��", null);
								alertDialog.show();
							}
						
						}
								
					});					
				}else {
					builder.setItems(new String[]{"�ظ�","����","�ٱ�"}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which==0) {
								/**
								 * �ظ�------------------------------------------------------------
								 */
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("comment")+"");
								Toast.makeText(context, "�Ѹ���",Toast.LENGTH_SHORT).show();
							}
							
							if (which==2) {
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
	 */
	private void loadAccountImg(final Map<String, String> map) {
		//��������ͼƬ�ļ�ʱ
		
				File localFile=new File(Environment.getExternalStorageDirectory()
						+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
				
				new BitmapFactory();//Ӧ�ÿ���ȥ��
				
				/**
				 * Ӧ�������ݿⴢ��
				 * //��������ͼƬ�ļ�ʱ
				 */
				if (localFile.exists()) {			
					myWidget.imageView.setImageBitmap(
							ImageHelper.toRoundBitmap(
									BitmapFactory
									.decodeFile(localFile.getAbsolutePath())));		
//					Log.d("logd","��ǰ����ͼƬ�û�..."+map.get("username"));
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
									Log.d("logd","���ͼƬ���û�..."+map.get("username"));
									
									
									//���߳�����ͼƬ
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
	 * ���convertView�಼�ָ��ü��ش���
	 */
	@Override
	public int getItemViewType(int position) {
		if (position == 0) { // ���岻ͬλ�õ�convertView����
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {

		return 2;// ʹ�ò����ļ�����
	}
	
	class MyWidget{
		
		ImageView imageView;
		TextView nameTextView;
		TextView sigTextView;
		TextView commentTextView;
		TextView posttimeTextView;
		TextView commentFloorTextView;
		TextView schoolTextView;
		
		
		//ͷ
		TextView booknameTextView;
		TextView bookAuthortTextView ;
		TextView bookISBNTextView;
		TextView bookPublisherTextView;
	}

}
