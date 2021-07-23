package com.gjmetal.app.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;

/**
 * Description RecycleView Decoration
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-16 14:48
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private int divider;

    public DividerItemDecoration(Context context) {
        //即你要设置的分割线的宽度 --这里设为5dp
        divider = context.getResources().getDimensionPixelSize(R.dimen.d2);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        outRect.left = divider;  //相当于 设置 left padding
//        outRect.top = divider;   //相当于 设置 top padding
//        outRect.right = divider; //相当于 设置 right padding
        //如果是第最后一个，则设置top的值。
        if (parent.getChildAdapterPosition(view) == 0){
            //这里直接硬编码为1px
            outRect.top = divider;  //相当于 设置 bottom padding
        }



    }

}
