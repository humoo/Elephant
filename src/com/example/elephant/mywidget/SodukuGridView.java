package com.example.elephant.mywidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * �Զ���ġ��Ź��񡱡���������ʾ���������ͼƬ���� ��������⣺GridView��ʾ��ȫ��ֻ��ʾ��һ�е�ͼƬ���Ƚ���֣�������дGridView�����
 * 
 * @author lichao
 * @since 2014-10-16 16:41
 * 
 */
public class SodukuGridView extends GridView {

	public SodukuGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SodukuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SodukuGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}