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
	LayoutInflater inflater;//���������
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
		 * �Ż�baseadapter
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
		myWidget.booknameTextView.setText("���� : "+map.get("bookname"));
//		myWidget.booknameTextView.setTextColor(Color.RED);//�������ú�ɫ
		myWidget.bookISBNTextView.setText("ISBN : "+map.get("bookisbn"));
		myWidget.bookAuthortTextView.setText("���� : "+map.get("bookauthor"));
		myWidget.bookPublisherTextView.setText("������ : "+map.get("bookpublisher"));		
		/**
		 * ����ͷ��
		 */

		loadAccountImg(map,position);
		
		//���õ���¼�
		myWidget.imageView.setTag(position);//���ñ�־λ�������֪�����ĸ�view
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
		
		//����˵�
		myWidget.main_item_more.setTag(position);
		myWidget.main_item_more.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final int position =Integer.parseInt(v.getTag().toString());
				map=list.get(position);
				AlertDialog.Builder builder=new AlertDialog.Builder(context);

				if (map.get("username").equals(openSharePrefrenceHelper.getSP_CONFIG().getString("accountname", ""))) {

					builder.setItems(new String[]{"��������","����ISBN","ɾ��"}, new DialogInterface.OnClickListener() {						
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							if (which==0) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookname")+""); 
								Toast.makeText(context, "�����Ѹ���",Toast.LENGTH_SHORT).show();
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookisbn")+""); 
								Toast.makeText(context, "ISBN�Ѹ���",Toast.LENGTH_SHORT).show();
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
										MainUIActivity.deleteListViewItem(context,position);	
									}
								});
								alertDialog.setNegativeButton("ȡ��", null);
								alertDialog.show();
							}
							
						}				
					});					
				}else {
					builder.setItems(new String[]{"��������","����ISBN","�ղ�","�ٱ�"}, new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which==0) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookname")+""); 
								Toast.makeText(context, "�����Ѹ���",Toast.LENGTH_SHORT).show();
							}
							if (which==1) {
								ClipboardManager copy = (ClipboardManager)   
										context.getSystemService(Context.CLIPBOARD_SERVICE);  
						                 copy.setText(map.get("bookisbn")+""); 
								Toast.makeText(context, "ISBN�Ѹ���",Toast.LENGTH_SHORT).show();
							}
							if (which==2) {
								Toast.makeText(context, "�ղسɹ�",Toast.LENGTH_SHORT).show();
							}
							if (which==3) {
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
	
	private void loadAccountImg(final Map<String, String> map, final int position) {
		//��������ͼƬ�ļ�ʱ
		
		File localFile=new File(Environment.getExternalStorageDirectory()
				+ "/Elephant/accountImg/"+ map.get("username") +"_accountImg.jpg");
		
		new BitmapFactory();
		//��������Ѵ���ͷ���򲻴��������ͼƬ,�ӱ���·����ȡ
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
							JSONObject item=(JSONObject) iJsonObject.get("file");//����json���ݷ�����filename��url�ֶ�			
							/**
							 * ���û����ƺ�ͷ���ַ����,�������߳�����
							 */
							downloadAccountPicThread=new DownloadAccountPicThread(context,
									map.get("username")+"_accountImg.jpg",
									item.get("url").toString(),handler);
							Log.e("logd","ͼƬ��ַ..."+map.get("username")+"_accountImg.jpg"+"---"+item.get("url").toString());
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

	class MyWidget{
		ImageView imageView;//ͷ��
		ImageView main_item_more;//����˵�
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
