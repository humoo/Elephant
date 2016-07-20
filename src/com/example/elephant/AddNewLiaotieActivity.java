package com.example.elephant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.example.elephant.adpter.GridViewAdapter;
import com.example.elephant.db.LiaoTie;
import com.example.elephant.image2.ChoosePhotosActivity;
import com.example.elephant.utils.FileUtils;
import com.example.elephant.utils.ImageHelper;
import com.example.elephant.utils.OpenSharePrefrenceHelper;

public class AddNewLiaotieActivity extends Activity {
	int INTENT_FROM_AddNewLiaoTieActivity=1;
	private GridView gridView1;// ������ʾ����ͼ
	private Bitmap bmp;// ������ʱͼƬ
	private final int IMAGE_OPEN = 1; // ��ͼƬ���
	public static ArrayList<HashMap<String, Object>> imageItem; // ��ѡͼƬ�б�
	private String pathImage;                //ѡ��ͼƬ·��  
	SimpleAdapter simpleAdapter;
	OpenSharePrefrenceHelper openSharePrefrenceHelper;
	public static String[] savePicFilesPath=new String[9];//������ѡȡ��ͼƬ·��
	public static Map<String, String> myMap=new HashMap<>();//������ѡȡ��ͼƬ·��
	public static boolean isSelectedPic=false;//��־λ���Ƿ�ѡȡͼƬ�ص���ǰactivity
	public static int picCount=0;
	static List list;
	EditText content;
	static GridViewAdapter adapter;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if (msg.what==0x2000) {
				adapter.notifyDataSetChanged();
			}
		};
	};

	// �ṩ������activity������ǰactivity
	public static AddNewLiaotieActivity finishAddNewLiaoTieActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_liaotie_activity);

		finishAddNewLiaoTieActivity=this;
		
		openSharePrefrenceHelper = new OpenSharePrefrenceHelper(this);

		list=new ArrayList<>();
		
		gridView1 = (GridView) findViewById(R.id.gridView1);
		ImageView back = (ImageView) findViewById(R.id.iv_back_title_back);
		ImageView ok = (ImageView) findViewById(R.id.iv_back_title_ok);
		content= (EditText) findViewById(R.id.ed_liaotie_content);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (list.size()>0||!content.getText().toString().trim().equals("")) {
					exitAlert();//�˳�����
				}else {
					finish();
				}
			}

			
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����������
				if (content.getText().toString().trim().equals("")) {
					Toast.makeText(AddNewLiaotieActivity.this, "�㻹û��������", 1)
							.show();
				} else {
					publishNewToWeb(content.getText().toString().trim());
				}

			}
		});

		/*
		 * ����Ĭ��ͼƬ���ͼƬ�Ӻ� ͨ��������ʵ�� SimpleAdapter����imageItemΪ����Դ
		 * R.layout.griditem_addpicΪ����
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
		imageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		map.put("imagePath", "");
		imageItem.add(map);
//		simpleAdapter = new SimpleAdapter(this, imageItem,
//				R.layout.griditem_addpic, new String[] { "itemImage" },
//				new int[] { R.id.imageView1 });
//		simpleAdapter.setViewBinder(new ViewBinder() {
//			@Override
//			public boolean setViewValue(View view, Object data,
//					String textRepresentation) {
//				if (view instanceof ImageView && data instanceof Bitmap) {
//					ImageView i = (ImageView) view;
//					i.setLayoutParams(new RelativeLayout.LayoutParams(
//							RelativeLayout.LayoutParams.MATCH_PARENT,
//							RelativeLayout.LayoutParams.MATCH_PARENT));
//					i.setImageBitmap((Bitmap) data);
//					return true;
//				}
//				return false;
//			}
//		});
		
		
		
		/**
		 * ����������
		 */
		
		adapter=new GridViewAdapter(this, handler, imageItem);
		gridView1.setAdapter(adapter);
		
			
		
		
		
		
//		gridView1.setAdapter(simpleAdapter);
		gridView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == imageItem.size()-1) { // ���ͼƬλ��Ϊ���һ��ͼƬ

					AlertDialog.Builder builder=new AlertDialog.Builder(AddNewLiaotieActivity.this);
					builder.setItems(new String[]{"�������","�����"}, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which==0) {//�򿪱������
								if (imageItem.size() == 10) { // ��һ��ΪĬ��ͼƬ
									Toast.makeText(AddNewLiaotieActivity.this, "ͼƬ��9������",
											Toast.LENGTH_SHORT).show();
								}else {
									// ѡ��ͼƬ
									Intent intent = new Intent(Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(intent, IMAGE_OPEN);
									// ͨ��onStart()ˢ������
								}
							}else if (which==1) {//���������
								Intent intent = new Intent(AddNewLiaotieActivity.this,ChoosePhotosActivity.class);
								startActivityForResult(intent, IMAGE_OPEN);
							}
							
						}
					});
					builder.create().show();
					
				} else {
					dialog(position);
					// Toast.makeText(MainActivity.this, "�����"+(position +
					// 1)+" ��ͼƬ",
					// Toast.LENGTH_SHORT).show();
				}

			}

		});

	}
	/**
	 * �˳�����
	 */
	private void exitAlert() {
		AlertDialog.Builder builder=new AlertDialog.Builder(AddNewLiaotieActivity.this);
		builder.setTitle("��ʾ");
		builder.setMessage("��δ���棬ȷ���뿪��");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {			
				picCount=0;
				FileUtils.deleteDir(new File(FileUtils.SDPATH));
				FileUtils.deleteDir(new File(FileUtils.SDPATH_THUMB));
				finish();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
			}
		});
		builder.create().show();
	}
	
	@Override
	public void onBackPressed() {
		if (list.size()>0||!content.getText().toString().trim().equals("")) {
			exitAlert();//�˳�����
		}else {
			super.onBackPressed();
		}
		
	}
	
	/* 
     * Dialog�Ի�����ʾ�û�ɾ������ 
     * positionΪɾ��ͼƬλ�� 
     */  
    protected void dialog(final int position) {
    	
    	Intent intent=new Intent(AddNewLiaotieActivity.this,ShowPhotoActivity.class);
    	intent.putExtra("fromAddNewLiaoTieActivity", "yes");
    	intent.putExtra("intentFrom", INTENT_FROM_AddNewLiaoTieActivity);
    	intent.putExtra("position", position);
    	startActivity(intent);
    	
//    	
//        AlertDialog.Builder builder = new Builder(AddNewLiaotieActivity.this);  
//        builder.setMessage("ȷ���Ƴ������ͼƬ��");  
//        builder.setTitle("��ʾ");  
//        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
//            @Override  
//            public void onClick(DialogInterface dialog, int which) {  
//                picCount=picCount-1;
//                //ɾ���ļ�
//                FileUtils.delFile(imageItem.get(position+1).get("picName").toString());
//                imageItem.remove(position+1);  
//                Log.d("logd", "position--"+position);
//                list.remove(position);
//                adapter.notifyDataSetChanged();
//            }  
//        });  
//        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
//            @Override  
//            public void onClick(DialogInterface dialog, int which) {  
//                }  
//            });  
//        builder.create().show();  
    }  
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data==null) {
//			Toast.makeText(AddNewLiaotieActivity.this, "data==null",
//					Toast.LENGTH_SHORT).show();
		}else {
//			if (requestCode==IMAGE_OPEN) {
//				Uri uri=data.getData();
//
//				if (!TextUtils.isEmpty(uri.getAuthority())) {
//					//��ѯѡ��ͼƬ    
//	                Cursor cursor = getContentResolver().query(    
//	                        uri,    
//	                        new String[] { MediaStore.Images.Media.DATA },    
//	                        null,     
//	                        null,     
//	                        null);    
//	                //���� û�ҵ�ѡ��ͼƬ    
//	                if (null == cursor) {    
////	                	Toast.makeText(AddNewLiaotieActivity.this, "cursor==null",
////								Toast.LENGTH_SHORT).show();
//	                    return;    
//	                }    
//	                //����ƶ�����ͷ ��ȡͼƬ·��    
//	                cursor.moveToFirst();    
//	                pathImage = cursor.getString(cursor    
//	                        .getColumnIndex(MediaStore.Images.Media.DATA));   
//				}
//			}
		}
	}
	
	@Override
	protected void onStart() {
		
		if (imageItem.size()==0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", bmp);
			map.put("imagePath", "");
			imageItem.add(map);
		}
		
		if (isSelectedPic) {
			/**
			 * ȡ��myMap��ļ�ֵ��
			 */

			Set set = myMap.entrySet();
			final Iterator iterator = set.iterator();
			// picCount=picCount+myMap.size();//ͳ��ͼƬ����
			Log.d("logd", "picCount--" + picCount);

			new Thread(new Runnable() {

				
				@Override
				public void run() {
					while (iterator.hasNext()) {
						Map.Entry mapentry = (Map.Entry) iterator.next();
						Log.d("logd", mapentry.getKey() + "");

						/**
						 * ����ӵ�ͼƬ·�����뵽������
						 */
						list.add(mapentry.getKey() + "");
						/**
						 * ���ش�ͼ�Ż�����ֹOOM
						 */
						FileInputStream in = null;
						BitmapFactory.Options options = new BitmapFactory.Options();
						
						Bitmap addbmp = null;
						String newStr=null;
						int len = 0;
						byte[] b = new byte[1024 * 1024];
						try {

							in = new FileInputStream(mapentry.getKey() + "");
							options.inJustDecodeBounds = false;
							options.inPreferredConfig = Bitmap.Config.RGB_565;
							
							//len = in.read(b);
							// if (len>1024*512) {//>512k
							

							addbmp = BitmapFactory.decodeStream(in, null,
									options);
							if (addbmp.getWidth() > 2300
									&& addbmp.getHeight() > 3200) { // >800M
								options.inSampleSize = 6;// width��hight��Ϊԭ����6��һ��1/36���
								in = new FileInputStream(mapentry.getKey() + "");
								addbmp = BitmapFactory.decodeStream(in, null,
										options);
							} else if (addbmp.getWidth() > 1800
									&& addbmp.getHeight() > 2400) { // >500M
								options.inSampleSize = 4;// width��hight��Ϊԭ����4��һ��1/16���
								in = new FileInputStream(mapentry.getKey() + "");
								addbmp = BitmapFactory.decodeStream(in, null,
										options);
							} else if (addbmp.getWidth() > 1400
									&& addbmp.getHeight() > 1800) {// >300M
								options.inSampleSize = 2;// width��hight��Ϊԭ����2��һ��1/4���
								in = new FileInputStream(mapentry.getKey() + "");
								addbmp = BitmapFactory.decodeStream(in, null,
										options);
							} else {
								in = new FileInputStream(mapentry.getKey() + "");
								addbmp = BitmapFactory.decodeStream(in);
							}
							
							
							
							newStr = mapentry.getKey().toString().substring(
									mapentry.getKey().toString().lastIndexOf("/") + 1,
									mapentry.getKey().toString().lastIndexOf("."));
							FileUtils.saveBitmap(addbmp, newStr);
							
							addbmp=BitmapFactory.decodeStream(new FileInputStream(new File(FileUtils.SDPATH+newStr+".JPEG")));
							//addbmp=BitmapFactory.decodeFile(FileUtils.SDPATH+newStr+".JPEG");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								if (in!=null) {
									in.close();

								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}


						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("picName", newStr+".JPEG");//�����ַ���Ա�ɾ����Ҫ
						map.put("itembitmapImage", addbmp);
						map.put("imagePath", FileUtils.SDPATH+newStr+".JPEG");//ͼƬ�ļ���·��
						imageItem.add(map);
						
						Message message = new Message();
						message.what = 0x2000;
						handler.sendMessage(message);

					}
					if (!iterator.hasNext()) {
						myMap.clear();
						isSelectedPic = false;
					}
				}
			}).start();

		}
		super.onStart();
	}
	protected void publishNewToWeb(String ed_content) {
		
				
		if (imageItem.size()>1) {//������2�����󣬵�һ��Ϊ�Ӻ�
			ImageHelper.saveGridPicsToWeb(AddNewLiaotieActivity.this,ed_content,imageItem.size()-1+"");	
		}else {
			
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
			liaoTie.setPics(imageItem.size()-1+"");//��������
			liaoTie.setThumbPicPathString("");
			liaoTie.setBigPicPathString("");
			liaoTie.save(AddNewLiaotieActivity.this, new SaveListener() {
				@Override
				public void onSuccess() {					
					
					Toast.makeText(AddNewLiaotieActivity.this, "�����ɹ�", 1).show();

					new DataFromWeb(AddNewLiaotieActivity.this).flushDataFromWeb();

					finish();

				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(AddNewLiaotieActivity.this, "����ʧ��", 1).show();
				}
			});
//			new DataFromWeb(AddNewLiaotieActivity.this).flushDataFromWeb();//����Ҫ�õ�
		}	
		
	}
}
