package com.example.elephant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindCallback;

import com.example.elephant.adpter.MainUIItemAdapter;
import com.example.elephant.db.BookComment;
import com.example.elephant.db.MainContent;
import com.example.elephant.utils.DownLoadUtil;
import com.example.elephant.utils.MyBroadcastReceiver;
import com.example.elephant.utils.XMLParseForString;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainUIActivity extends Activity {

	String TAG="MainUIActivity";
	static final int GET_INFO_SUCCESS=10;
	static final int SERVER_ERROR=11;
	static final int SERVER_URL_ERROR=12;
	static final int PROTOCOL_ERROR=13;
	static final int IO_ERROR=14;
	static final int XML_PARSE_ERROR=15;
	static final int DOWNLOAD_SUCCESS=16;
	static final int DOWNLOAD_ERROR=17;
	
	public static ProgressDialog flushPd;//����������ˢ��
	public static PullToRefreshListView mainListView;//��ҳ��ListView
	SharedPreferences sp;
	XMLParseForString xmlResult;
	ProgressDialog pd;
	File file;//���ص��ļ�
//	static ImageView addImg;//��Ӷ�̬
	ImageView infoIcon;//�������ͷ��鿴��Ϣ
	ImageView openMore;//�򿪸���
	public static MainUIItemAdapter adapter;
	static List<Map<String,String>> list;
	MyBroadcastReceiver myBroadcastReceiver;
	Bundle itembBundle;//��������ҳ��
	Intent itemIntent;//������ҳ��
	String[] item_values;
	String bookauthor,usercontent,username,school,bookname,bookpublisher,bookisbn,viewcountStr,objectId,createdAt,significance,commentcount;
	int viewcount=0;
	Map<String, String> map;
    static int upRefreshCount=0;
	DataFromWebMainUI dataFromWebMainUI;
	
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case GET_INFO_SUCCESS:
				showDownDialog();
				break;
			case SERVER_URL_ERROR:
				Toast.makeText(MainUIActivity.this,"������URL����",1).show();
				break;
			case IO_ERROR:
//				Toast.makeText(MainUIActivity.this,"IO����",1).show();
				break;
			case XML_PARSE_ERROR:
				Toast.makeText(MainUIActivity.this,"xml��������",1).show();
				break;
			case DOWNLOAD_SUCCESS:
				Toast.makeText(MainUIActivity.this,"�������",1).show();
				//�Զ���װ
				autoInstallApk(file);
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(MainUIActivity.this,"���س���",1).show();
				break;
			case 0x1111://����ͷ��
				adapter.notifyDataSetChanged();
			default:
				break;
			}
		
		}

		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_main_ui);
		
		mainListView=(PullToRefreshListView) findViewById(R.id.lv_main_ui_activity);
		
		itembBundle=new Bundle();
		itemIntent=new Intent(MainUIActivity.this,MainComment.class);
		
		dataFromWebMainUI=new DataFromWebMainUI(this);//ˢ��������
		
		//��һ���汾
		sp=getSharedPreferences("config", MODE_PRIVATE);
		Editor editor=sp.edit();
		if (!sp.getString("version", "").equals("2.0")) {
			//����
		}else {
			editor.putString("version", "2.0");
			editor.commit();
		}
		
		
		/**
		 * Bmob��˷����ʼ��
		 */
		Bmob.initialize(this, "c0fc8badde1ed95a0f425c1191d27bd1");
		
		
		//�����������
		new Thread(new CheckedForUpdate()).start();
		
		//���ͷ����ʾ������Ϣ
		infoIcon=(ImageView) findViewById(R.id.item_img_lv_item_ui);
		//�򿪸���
		openMore = (ImageView) findViewById(R.id.main_item_more);
		//����ˢ�¿ؼ���ʼ��
		refreshInit();
		
		/**
		 * ������view����
		 */
		list=new ArrayList<>();
		dataFromWebMainUI.flushDataFromWeb();

		adapter=new MainUIItemAdapter(this,handler);		
		
		mainListView.setOnItemClickListener(new OnItemClickListener() {//��ȡ����Item�����ݣ���������ҳ��

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				itembBundle=new Bundle();
				map =list.get(arg2-1);//��ȡ����Item�����ݣ���������ҳ�档

				username=map.get("username");//��Ϊ��ֵ
				bookname=map.get("bookname");//��Ϊ��ֵ
				usercontent=map.get("usercontent");//��Ϊ��ֵ
				viewcountStr=map.get("viewcount");//Ϊ0
				viewcount=Integer.parseInt(viewcountStr)+1;//ÿ���һ������һ�����
				bookauthor=map.get("bookauthor");
				bookpublisher=map.get("bookpublisher");
				bookisbn=map.get("bookisbn");
				objectId=map.get("objectId");
				createdAt=map.get("createdAt");
				significance=map.get("significance");
				commentcount=map.get("commentcount");
				school=map.get("school");
			
					
				itembBundle.putString("bookauthor", bookauthor);
				itembBundle.putString("usercontent", usercontent);
				itembBundle.putString("username", username);
				itembBundle.putString("bookname", bookname);
				itembBundle.putString("bookpublisher", bookpublisher);
				itembBundle.putString("bookisbn", bookisbn);
				itembBundle.putString("viewcount", viewcount+"");
				itembBundle.putString("objectId", objectId);
				itembBundle.putString("createdAt", createdAt);
				itembBundle.putString("significance", significance);
				itembBundle.putString("commentcount", commentcount);
				itembBundle.putString("school", school);
				
				itemIntent.putExtra("itemdata", itembBundle);
				startActivity(itemIntent);
				itembBundle.clear();
				
				//�������
				map.put("viewcount",viewcount+"");
				list.add(0,map);
				adapter.notifyDataSetChanged();
				list.remove(0);
		
			}
		});
		
		
				
		
		
	}
	/**
	 * ˢ��Ч��ʵ��
	 */
	private void refreshInit() {
		/**
		 * androidԭ��
		 */
//		refresh_layout = (SwipeRefreshLayout) this.findViewById(R.id.refresh_layout);
//		refresh_layout.setColorScheme(R.color.lightpink, R.color.lightgreen, R.color.lightcoral, R.color.lightsteelblue);//�����ܶ�����ɫֵ
//		refresh_layout.setOnRefreshListener(new OnRefreshListener() {	//���������ļ���	
//			@Override
//			public void onRefresh() {				
//				flushDataThread.getData();
//				list.clear();
//				list=flushDataThread.getList();								
//			}
//		});

		
		/**
		 * pullToRefresh
		 */
		mainListView.setMode(Mode.BOTH);

		mainListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {

						if (refreshView.isHeaderShown()) {
							String str = DateUtils.formatDateTime(
									MainUIActivity.this,
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							// ����ˢ�±�ǩ
							mainListView.getLoadingLayoutProxy()
									.setRefreshingLabel("����ˢ��");
							// ����������ǩ
							mainListView.getLoadingLayoutProxy().setPullLabel(
									"����ˢ��");
							// �����ͷű�ǩ
							mainListView.getLoadingLayoutProxy()
									.setReleaseLabel("�ͷſ�ʼˢ��");
							// ������һ��ˢ�µ���ʾ��ǩ
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("������ʱ��:" + str);
							// �������ݲ���
							 upRefreshCount=0;
							 dataFromWebMainUI.flushDataFromWeb();
							
						}
						if (refreshView.isFooterShown()) {
							String str = DateUtils.formatDateTime(
									MainUIActivity.this,
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							// ����ˢ�±�ǩ
							mainListView.getLoadingLayoutProxy()
									.setRefreshingLabel("���ڼ���");
							// ����������ǩ
							mainListView.getLoadingLayoutProxy().setPullLabel(
									"���ظ���");
							// �����ͷű�ǩ
							mainListView.getLoadingLayoutProxy()
									.setReleaseLabel("�ͷſ�ʼˢ��");
							// ������һ��ˢ�µ���ʾ��ǩ
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel("������ʱ��:" + str);
							// �������ݲ���
							 upRefreshCount=upRefreshCount+1;
							 dataFromWebMainUI.refreshDataAtTheEnd();

						}

					}
				});
		
	}

	/**
	 * ��ʾ���ؽ�����
	 */
	private void showDownDialog() {
		String description=xmlResult.getDescription();
		final String apkurl=xmlResult.getApkurl();
		
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("������ʾ");
		builder.setMessage(description);
		builder.setCancelable(false);
		builder.setPositiveButton("��������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainUIActivity.this,"��ʼ����",1).show();
				
				pd=new ProgressDialog(MainUIActivity.this);
				pd.setMessage("��������...");
				pd.setCancelable(false);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				
				new Thread(){
					public void run() {
						////�����ļ������࣬��ȡ�����ļ����ļ���
						String filename=DownLoadUtil.getFilename(apkurl);
						//��sd������һ���ļ�
						file=new File(Environment.getExternalStorageDirectory(),filename);
						//�õ����غ��apk�ļ�
						file=DownLoadUtil.getFile(apkurl,file.getAbsolutePath(),pd);
						
						if (file!=null) {
							Message message=Message.obtain();
							message.what=DOWNLOAD_SUCCESS;
//							message.obj=file;
							handler.sendMessage(message);
						}else {
							Message message=Message.obtain();
							message.what=DOWNLOAD_ERROR;
							handler.sendMessage(message);
						}
						pd.dismiss();		
						
					};
				}.start();
				
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();		
		
	}
	
	/**
	 * ���غ��Զ���װ
	 */
	private void autoInstallApk(File file) {
		Intent intent=new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	
	/**
	 * �������
	 */
	private class CheckedForUpdate implements Runnable{

		Message message=Message.obtain();
		@Override
		public void run() {
			
			//��ȡ��Դ�ļ��µ��ַ���http://10.50.3.40:80/mobilesafe.apk
			String updateUrl=getResources().getString(R.string.update_url);
			try {
				URL url=new URL(updateUrl);
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(5000);
				
				InputStream is=conn.getInputStream();				
				xmlResult=new XMLParseForString(is);//������Ϣ
				String oldVersion=sp.getString("version", "");
				String newVersion=xmlResult.getVersion();
				
				//�жϰ汾�Ƿ�һ��
				if (!oldVersion.equals(newVersion)) {
					//�汾��ͬ������
					Log.d(TAG, "�汾��ͬ������");
					message.what=GET_INFO_SUCCESS;
					handler.sendMessage(message);
										
				}else {
					Log.d(TAG, "��ͬ�汾����������");
				}	
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				message.what=SERVER_URL_ERROR;
				handler.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
				message.what=IO_ERROR;
				handler.sendMessage(message);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				message.what=XML_PARSE_ERROR;
				handler.sendMessage(message);
			} 
		}
		
	}
	

	/**
	 * �������ݸ��㲥������
	 * @return
	 */
	public static MainUIItemAdapter getAdapterData() {
		
		return adapter;
	}

	/**
	 * MianUIAdapter����ɾ��listview item
	 * ��������������
	 */
	public static void deleteListViewItem(final Context context,int position) {
		Map map=list.get(position);
		MainContent mainContent=new MainContent();
		mainContent.setObjectId(map.get("objectId").toString());
		mainContent.delete(context, new DeleteListener() {			
			@Override
			public void onSuccess() {	
				Toast.makeText(context, "ɾ���ɹ�",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(context, "ɾ��ʧ��", 1).show();
			}
		});
		
		BmobQuery query=new BmobQuery("BookComment");
		query.addWhereEqualTo("parentobjectId", map.get("objectId").toString());
		query.findObjects(context, new FindCallback() {		
			@Override
			public void onFailure(int arg0, String arg1) {				
			}		
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				BookComment bookComment=new BookComment();
				if (arg0.length()>0) {
					for (int i = 0; i < arg0.length(); i++) {
						try {
							JSONObject iJsonObject=arg0.getJSONObject(i);
							bookComment.setObjectId(iJsonObject.get("objectId").toString());
							bookComment.delete(context, new DeleteListener() {						
								@Override
								public void onSuccess() {									
								}
								@Override
								public void onFailure(int arg0, String arg1) {									
								}
							});
							
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
				
			}
		});
		
		list.remove(position);
		adapter.notifyDataSetChanged();
	}
	
}


/**
 * �ӷ�������ȡ������
 * @author Administrator
 *
 */
class DataFromWebMainUI{
	
	Context context;
	Map<String,String> map;
	
	public DataFromWebMainUI(Context context) {
		this.context=context;
	}
	
	/**
	 * �ӷ�������ȡ����
	 */
	public void flushDataFromWeb() {
		BmobQuery query=new BmobQuery("MainContent");//��ѯ��������		
		query.setLimit(5);
		query.order("-createdAt");//����ʱ�䷴���������µ�����ǰ��
		query.findObjects(context, new FindCallback() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				MainUIActivity.mainListView.onRefreshComplete();
				Toast.makeText(context, "���粻�ã�����", 1).show();

			}
			
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				
				if (arg0.length() > 0) {

					try {
						MainUIActivity.list.clear();

						for (int i = 0; i < arg0.length(); i++) {

							JSONObject jsonItem=arg0.getJSONObject(i);
							
							String username=jsonItem.getString("username");
							String usercontent=jsonItem.getString("usercontent");
							String objectId=jsonItem.getString("objectId");
							String createdAt=jsonItem.getString("createdAt");
							String commentcount=jsonItem.getString("commentcount");
							String viewcount=jsonItem.getString("viewcount");//��ȡ�������Ϊ�ַ���
							String school=jsonItem.getString("school");
							String significance,bookname,bookisbn,bookauthor,bookpublisher;
							
							
							if (!jsonItem.getString("significance").equals(".#")) {
								significance=jsonItem.getString("significance");
							}else {
								significance="";
							}
							
							if (!jsonItem.getString("bookname").equals(".#")) {
								bookname=jsonItem.getString("bookname");
							}else {
								bookname="";
							}
							
							if (!jsonItem.getString("bookisbn").equals(".#")) {
								bookisbn=jsonItem.getString("bookisbn");
							} else {
							    bookisbn="";
							}
							
							if (!jsonItem.getString("bookauthor").equals(".#")) {
								bookauthor=jsonItem.getString("bookauthor");
							}else {
								bookauthor="";
							}
							
							if (!jsonItem.getString("bookpublisher").equals(".#")) {
								bookpublisher=jsonItem.getString("bookpublisher");
							}else {
								bookpublisher="";
							}

							//װ�ؽ�Map����
							map=new HashMap();
							map.put("username", username);
							map.put("usercontent",usercontent);
							map.put("significance", significance);
							map.put("bookname", bookname);
							map.put("bookisbn", bookisbn);
							map.put("bookauthor", bookauthor);
							map.put("bookpublisher", bookpublisher);
							map.put("objectId", objectId);
							map.put("viewcount", viewcount);
							map.put("createdAt", createdAt);
							map.put("commentcount", commentcount);
							map.put("school", school);
							
							MainUIActivity.list.add(map);//���ݷ�����λ

						}

						MainUIActivity.adapter.setListData(MainUIActivity.list);// �������������
						MainUIActivity.mainListView.setAdapter(MainUIActivity.adapter);
						MainUIActivity.mainListView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�

					} catch (JSONException e) {
						e.printStackTrace();
						MainUIActivity.mainListView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�

					}

				}
				
			}
		});
	}
	
	/**
	 * ˢ�¼�������
	 */
	public void refreshDataAtTheEnd(){
		
		BmobQuery query=new BmobQuery("MainContent");//��ѯ��������		
		query.setLimit(5);
		query.setSkip(5*MainUIActivity.upRefreshCount);
		query.order("-createdAt");//����ʱ�䷴���������µ�����ǰ��
		query.findObjects(context, new FindCallback() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				MainUIActivity.mainListView.onRefreshComplete();
				Toast.makeText(context, "���粻�ã�����", 1).show();

			}
			
			@Override
			public void onSuccess(org.json.JSONArray arg0) {
				
				if (arg0.length() > 0) {

					try {
						for (int i = 0; i < arg0.length(); i++) {

							JSONObject jsonItem=arg0.getJSONObject(i);
							
							String username=jsonItem.getString("username");
							String usercontent=jsonItem.getString("usercontent");
							String objectId=jsonItem.getString("objectId");
							String createdAt=jsonItem.getString("createdAt");
							String commentcount=jsonItem.getString("commentcount");
							String viewcount=jsonItem.getString("viewcount");//��ȡ�������Ϊ�ַ���
							String school=jsonItem.getString("school");

							String significance,bookname,bookisbn,bookauthor,bookpublisher;
							
							
							if (!jsonItem.getString("significance").equals(".#")) {
								significance=jsonItem.getString("significance");
							}else {
								significance="";
							}
							
							if (!jsonItem.getString("bookname").equals(".#")) {
								bookname=jsonItem.getString("bookname");
							}else {
								bookname="";
							}
							
							if (!jsonItem.getString("bookisbn").equals(".#")) {
								bookisbn=jsonItem.getString("bookisbn");
							} else {
							    bookisbn="";
							}
							
							if (!jsonItem.getString("bookauthor").equals(".#")) {
								bookauthor=jsonItem.getString("bookauthor");
							}else {
								bookauthor="";
							}
							
							if (!jsonItem.getString("bookpublisher").equals(".#")) {
								bookpublisher=jsonItem.getString("bookpublisher");
							}else {
								bookpublisher="";
							}

							//װ�ؽ�Map����
							map=new HashMap();
							map.put("username", username);
							map.put("usercontent",usercontent);
							map.put("significance", significance);
							map.put("bookname", bookname);
							map.put("bookisbn", bookisbn);
							map.put("bookauthor", bookauthor);
							map.put("bookpublisher", bookpublisher);
							map.put("objectId", objectId);
							map.put("viewcount", viewcount);
							map.put("createdAt", createdAt);
							map.put("commentcount", commentcount);
							map.put("school", school);
							
							MainUIActivity.list.add(map);//���ݷ�����λ

						}

						MainUIActivity.adapter.setListData(MainUIActivity.list);// �������������
						MainUIActivity.mainListView.setAdapter(MainUIActivity.adapter);
						MainUIActivity.mainListView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
						MainUIActivity.adapter.notifyDataSetChanged();	
						//������һ��itemλ�ò���
						MainUIActivity.mainListView.getRefreshableView().setSelectionFromTop(5*MainUIActivity.upRefreshCount-1, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);

					} catch (JSONException e) {
						e.printStackTrace();
						MainUIActivity.mainListView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�

					}

				}else {
					Toast.makeText(context, "�Ѿ�������", 1).show();
					MainUIActivity.mainListView.onRefreshComplete();// ���ݼ��ص���������ɺ�ˢ����ɣ�
				}
				
			}
		});
	}
}
