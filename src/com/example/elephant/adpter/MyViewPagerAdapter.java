package com.example.elephant.adpter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

public class MyViewPagerAdapter extends PagerAdapter{

	
	public MyViewPagerAdapter() {
		
	}
	
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return false;
	}

}
