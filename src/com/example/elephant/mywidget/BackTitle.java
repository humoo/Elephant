package com.example.elephant.mywidget;

import java.util.zip.Inflater;

import com.example.elephant.R;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class BackTitle extends View{

	Inflater inflater;
	public BackTitle(Context context,AttributeSet attrs) {
		super(context,attrs);
		View view=inflate(context, R.layout.back_title, null);
//		 View myView = inflater.inflate(R.layout.back_title);
         
		ImageView imageView=(ImageView) view.findViewById(R.id.iv_back_title);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				System.out.println("back");
			}
		});

		
		
		
	}

	
}
