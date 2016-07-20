package com.example.elephant.image;

import java.util.HashMap;

import com.example.elephant.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;



public class ListViewActivity extends Activity {

	private ListView listView;
	private MyListAdapter mAdapter;
	private HashMap<Integer, Model> httpImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_activity_layout);

		init();
	}

	private void init() {
		/*listDatas = new HashMap<Integer, Boolean>();
		for (int i = 0; i < 100; i++) {
			listDatas.put(i, false); // checkbox默认都是未�?�中的（false�?
		}*/
		
		listView = (ListView) findViewById(R.id.listView);

		httpImage = ImageUtil.getHttpImage(this);
		mAdapter = new MyListAdapter(this, httpImage);
		listView.setAdapter(mAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox cBox = (CheckBox) view.findViewById(R.id.checkBox);
				Toast.makeText(ListViewActivity.this, position + "",
						Toast.LENGTH_SHORT).show();
				httpImage.put(position, new Model(httpImage.get(position).url, !cBox.isChecked()));
				mAdapter.notifyDataSetChanged();
			}
		});
	}

}
