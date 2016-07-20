package com.example.elephant.image;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;

import com.example.elephant.R;

public class GridViewActivity extends Activity implements OnItemClickListener {

	private GridView gridView;
	private MyGridViewAdapter adapter;
	private HashMap<Integer, Model> mediaImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview_activity_layout);

		init();
	}

	private void init() {
		gridView = (GridView) findViewById(R.id.gridview);
		mediaImage = ImageUtil.getMediaImage(this);
		adapter = new MyGridViewAdapter(this, mediaImage);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CheckBox cbBox = (CheckBox) view.findViewById(R.id.cBox);
		mediaImage.put(position,
				new Model(mediaImage.get(position).url, !cbBox.isChecked()));
		adapter.notifyDataSetChanged();
	}
}
