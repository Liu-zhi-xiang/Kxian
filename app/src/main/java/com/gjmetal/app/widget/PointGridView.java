package com.gjmetal.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class PointGridView extends GridView {

	public PointGridView(Context context) {
		super(context);
	}

	public PointGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

}
