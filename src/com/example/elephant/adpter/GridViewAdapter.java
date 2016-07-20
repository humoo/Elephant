package com.example.elephant.adpter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.elephant.AddNewLiaotieActivity;
import com.example.elephant.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GridViewAdapter extends BaseAdapter{
	
	Handler handler;
	
	LayoutInflater inflater;
	Context context;
	List<HashMap<String, Object>> list;
	public GridViewAdapter(Context context,Handler handler,List<HashMap<String, Object>> imageItem) {
		this.handler=handler;
		this.context=context;
		inflater=LayoutInflater.from(context);
		this.list=imageItem;//gridview上的图片列表
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		final int coord = position;
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.item_gridview_adapter,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if (position == list.size()-1) {//可移动的添加号 list.size()-1=0
			
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_unfocused));
			
			if (position == 9) {
				holder.image.setVisibility(View.GONE);				
			}
		} else {
	
			Map<String, Object> map = list.get(position+1);
			holder.image.setImageBitmap((Bitmap) map.get("itembitmapImage"));
			holder.image.setVisibility(View.VISIBLE);
			
		}
	
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		
	}
	
	
}
