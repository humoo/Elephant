package com.example.elephant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elephant.utils.FileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShowPhotoActivity extends Activity{
	
	int INTENT_FROM_AddNewLiaoTieActivity=1;
	int INTENT_FROM_Layout_2=2;
	
	private ArrayList<View> listViews = null;
	private ViewPager pager;
	MyPageAdapter adapter1;
	MypagerForLayout2 mypagerForLayout2;
	private ArrayList<HashMap<String, Object>> imageItem; // ��ѡͼƬ�б�
	Button delPic,bt_down_pic;
	Bitmap bmp=null;
	RelativeLayout photo_relativeLayout,save_relativeLayout;
	String[] thumbPicdata,bigPicdata;
	TextView text_pic;
	int count;//����layout2������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo_activity);
		photo_relativeLayout=(RelativeLayout) findViewById(R.id.photo_relativeLayout);
		save_relativeLayout=(RelativeLayout) findViewById(R.id.save_relativeLayout);
		pager=(ViewPager) findViewById(R.id.show_photo_viewpager);
		delPic=(Button) findViewById(R.id.bt_del_pic);
		bt_down_pic=(Button) findViewById(R.id.bt_down_pic);
		
		imageItem=new ArrayList<HashMap<String, Object>>();
		
		
		if (getIntent().getIntExtra("intentFrom", 2)==INTENT_FROM_AddNewLiaoTieActivity) {
			//imageItem.clear();
			photo_relativeLayout.setVisibility(View.VISIBLE);
			save_relativeLayout.setVisibility(View.GONE);
			imageItem=AddNewLiaotieActivity.imageItem;
			for (int i = 1; i < AddNewLiaotieActivity.imageItem.size(); i++) {
				try {
					bmp=BitmapFactory.decodeStream(new FileInputStream(new File(AddNewLiaotieActivity.imageItem.get(i).get("imagePath").toString())));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				initListViews(bmp);
			}
			
			final int position=getIntent().getIntExtra("position", 0)+1;//ȡ���ϸ�ҳ������λ��
			
			adapter1 = new MyPageAdapter(listViews);// ����adapter
			pager.setAdapter(adapter1);// ����������

			pager.setCurrentItem(position-1);
			
			delPic.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					
					
					
					AddNewLiaotieActivity.picCount=AddNewLiaotieActivity.picCount-1;
					
//					if (listViews.size()>2) {
						
					if (listViews.size()==1) {//��viewpagerֻ��һ��ҳ��ʱ������˳���ǰҳ��
						FileUtils.delFile(AddNewLiaotieActivity.imageItem.get(pager.getCurrentItem()+1).get("picName").toString());
						AddNewLiaotieActivity.list.clear();
						AddNewLiaotieActivity.imageItem.remove(0);
			            AddNewLiaotieActivity.adapter.notifyDataSetChanged();
						ShowPhotoActivity.this.finish();
						return;
						
					}else {
						 //ɾ���ļ�
		                FileUtils.delFile(AddNewLiaotieActivity.imageItem.get(pager.getCurrentItem()+1).get("picName").toString());
		                AddNewLiaotieActivity.imageItem.remove(pager.getCurrentItem()+1);  
		                AddNewLiaotieActivity.list.remove(1);
		                AddNewLiaotieActivity.adapter.notifyDataSetChanged();
		                
		                
		                
		                int currentPosition=pager.getCurrentItem();
		                listViews.clear();
		                for (int i = 1; i < AddNewLiaotieActivity.imageItem.size(); i++) {
		                	try {
								bmp=BitmapFactory.decodeStream(new FileInputStream(new File(AddNewLiaotieActivity.imageItem.get(i).get("imagePath").toString())));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
		    				initListViews(bmp);
		    			}
		                
		                adapter1 = new MyPageAdapter(listViews);// ����adapter
		                pager.setAdapter(adapter1);// ����������
		                if (currentPosition==listViews.size()-1) {		                	
			                pager.setCurrentItem(currentPosition-1);
						}else {
			                pager.setCurrentItem(currentPosition);

						}
					}
					
	

	                
				}
			});
			
		}
		
		
		
		
		
		if (getIntent().getIntExtra("intentFrom", 2)==INTENT_FROM_Layout_2) {
			photo_relativeLayout.setVisibility(View.GONE);
			save_relativeLayout.setVisibility(View.VISIBLE);
			text_pic=(TextView) findViewById(R.id.text_pic);
			int position=getIntent().getIntExtra("position", 0);
			count=getIntent().getIntExtra("count", 0);
			thumbPicdata=getIntent().getStringExtra("thumbPicdata").split(",");
			bigPicdata=getIntent().getStringExtra("bigPicdata").split(",");
//			for (int i = 0; i < count; i++) {
//				Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.banner);
//				initListViews(bitmap);
//				
//			}
//			
//			adapter = new MyPageAdapter(listViews);// ����adapter
//			pager.setAdapter(adapter);// ����������
//			adapter.changData(listViews);
//			pager.setCurrentItem(position);
			
			mypagerForLayout2 = new MypagerForLayout2();
			pager.setAdapter(mypagerForLayout2);
						
			for (int i = 0; i < thumbPicdata.length; i++) {
				if (listViews == null)
					listViews = new ArrayList<View>();
				ImageView im = new ImageView(this);
				im.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
				//im.setScaleType(ScaleType.CENTER_INSIDE);
				listViews.add(im);
				
				ImageLoader.getInstance().displayImage(bigPicdata[i], im);
						
			}
			//����adapter�е�����
			mypagerForLayout2.changData(listViews);
			text_pic.setText((position+1)+"/"+count);
			pager.setCurrentItem(position);
			
			bt_down_pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(ShowPhotoActivity.this, "���浽....", 1).show();

				}
			});
			
			
		}
		
		
	}
	
	
	
	/**
	 * ��ʼ��ͼƬ����
	 * @param bm
	 */
	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		ImageView img = new ImageView(this);// ����textView����
		//img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		
		img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		listViews.add(img);// ���view
		
	}
	
	@Override
	public void onBackPressed() {

		if (getIntent().getIntExtra("intentFrom", 2)==INTENT_FROM_AddNewLiaoTieActivity) {
			if (!bmp.isRecycled()) {
				bmp.recycle();
			}
			
			listViews.clear();
		}
		
		super.onBackPressed();
	}
	
	
	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// ҳ��

		public MyPageAdapter(ArrayList<View> listViews) {// ���캯��
															// ��ʼ��viewpager��ʱ�����һ��ҳ��
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void changData(ArrayList<View> view) {
			this.listViews = view;
			notifyDataSetChanged();
		}
		
		// ��ȡҪ�����Ŀؼ��������������������Ի����Ĺ����Ϊ������ô�����Ӧ����չʾ�Ĺ��ͼƬ��ImageView����
		@Override
		public int getCount() {
			if (listViews==null) {
				return 0;
			}
			return listViews.size();
		}

		// ���ж���ʾ���Ƿ���ͬһ��ͼƬ���������ǽ�����������ȽϷ��ؼ���
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	
		
		// PagerAdapterֻ��������Ҫ��ʾ��ͼƬ�����������ͼƬ�����˻���ķ�Χ���ͻ���������������ͼƬ����
		@Override
		public void destroyItem(ViewGroup view, int position, Object object) {
//			Log.d("logd", "position:"+position+",size:"+size);
//			Toast.makeText(ShowPhotoActivity.this,"position:"+position+",size:"+size,1).show();
			if (position==0&&size==1) {
				Toast.makeText(ShowPhotoActivity.this, "����",1).show();
				view.removeView(listViews.get(position));
				return;
			}
			if (position==size-1) {
				view.removeView(listViews.get(position-1));
				return;
			}
			view.removeView(listViews.get(position));
		}

		// ��Ҫ��ʾ��ͼƬ���Խ��л����ʱ�򣬻�����������������ʾͼƬ�ĳ�ʼ�������ǽ�Ҫ��ʾ��ImageView���뵽ViewGroup�У�Ȼ����Ϊ����ֵ���ؼ���
//		@Override
//		public Object instantiateItem(ViewGroup view, int position) {
//			view.addView(listViews.get(position));
//			return listViews.get(position);
//		}
		
//		public void setListViews(ArrayList<View> listViews) {// �Լ�д��һ�����������������
//			this.listViews = listViews;
//			size = listViews == null ? 0 : listViews.size();
//		}
//
//		public int getCount() {// ��������
//			return size;
//		}
//
//		public int getItemPosition(Object object) {
//			return POSITION_NONE;
//		}
//
//		public void destroyItem(View arg0, int arg1, Object arg2) {// ����view����
//			Log.d("logd", "arg1:"+arg1+",size:"+size);
//			
//			
//			
//			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
//		}
//
//		public void finishUpdate(View arg0) {
//		}
//
		public Object instantiateItem(View arg0, int arg1) {// ����view����
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return arg0 == arg1;
//		}
//
	}
	
	
	
	class MypagerForLayout2 extends PagerAdapter {
		ArrayList<View> view = null;

		public MypagerForLayout2() {
			view = new ArrayList<>();
		}

		public void changData(ArrayList<View> view) {
			this.view = view;
			notifyDataSetChanged();
		}

		// ��ȡ��ͼ����
		@Override
		public int getCount() {
			return view.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(view.get(position));
			
			
			return view.get(position);
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			text_pic.setText((position+1)+"/"+count);
						
			super.setPrimaryItem(container, position, object);
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(view.get(position));
		}

	}
		
		
}
