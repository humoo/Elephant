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

public class MyGridViewAdapter extends BaseAdapter {

	private static DisplayImageOptions options;
	private static ImageLoader imageLoader;
	private Context context;
	private HashMap<Integer, Model> datas;

	public MyGridViewAdapter(Context context, HashMap<Integer, Model> datas) {
		this.context = context;
		this.datas = datas;
		int size = datas.size();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		/*
		 * options = new DisplayImageOptions.Builder()
		 * .showStubImage(R.drawable.ic_stub)
		 * .showImageForEmptyUri(R.drawable.ic_empty)
		 * .showImageOnFail(R.drawable.ic_error) .cacheOnDisc(true)
		 * .bitmapConfig(Bitmap.Config.RGB_565)
		 * .cacheInMemory(true).cacheOnDisc(true).build();
		 */

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
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.gridview_item_layout, parent, false);
			holder = new ViewHolder();
			holder.chBox = (CheckBox) view.findViewById(R.id.cBox);
			holder.imaView = (ImageView) view.findViewById(R.id.image);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.chBox.setChecked(datas.get(position).isChecked);
		imageLoader.displayImage(datas.get(position).url, holder.imaView,
				options);

		return view;
	}

	private static class ViewHolder {
		ImageView imaView;
		CheckBox chBox;
	}
}
