package com.example.elephant.image;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;



import com.example.elephant.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyListAdapter extends BaseAdapter {

	private Context context;
	private HashMap<Integer, Model> datas;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public MyListAdapter(Context context, HashMap<Integer, Model> datas){
		this.context = context;
		this.datas = datas;
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)//加载中显示的图片
		.showImageForEmptyUri(R.drawable.ic_empty)//空uri显示的图片
		.showImageOnFail(R.drawable.ic_error)//下载错误显示的图片
		.cacheInMemory(true)//缓存
		.cacheOnDisk(true) // 加载图片时会在磁盘中加载缓存  
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if(convertView == null){
			view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent , false);
			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.image);
			holder.chBox = (CheckBox) view.findViewById(R.id.checkBox);
			view.setTag(holder);
		} else{
			holder = (ViewHolder) view.getTag();
		}
		holder.chBox.setChecked(datas.get(position).isChecked);
		imageLoader.displayImage(datas.get(position).url, holder.image, options);
		return view;
	}
	
	private static class ViewHolder {
		ImageView image;
		CheckBox chBox;
	}
}
