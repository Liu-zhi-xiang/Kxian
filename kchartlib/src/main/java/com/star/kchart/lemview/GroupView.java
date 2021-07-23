package com.star.kchart.lemview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.star.kchart.R;
import com.star.kchart.comInterface.ILem;

import java.util.Collection;


public class GroupView extends BaseGroupView{
    private Context mContext;
    private MainView mMainView;//不带柱状图
    private ChildView mChildView;
    private MainSpreadView mainSpreadView;//带柱状图
    private ChildSpreadView childSpreadView;
    private int type;
    public GroupView(Context context) {
        this(context,null);
    }

    public GroupView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public GroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.GroupView);
        type=array.getInt(R.styleable.GroupView_viewtype,1);
        array.recycle();
        initView(context);
    }


    private void initView(Context context){
        this.mContext = context;
        if (type==1) {
            mMainView = new MainView(getContext());
            setMainDraw(mMainView);
            addView(mMainView, 0);
            mChildView = new ChildView(getContext());
            setChildDraw(mChildView);
            addView(mChildView, 1);
        }else {
            mainSpreadView = new MainSpreadView(getContext());
            setMainDraw(mainSpreadView);
            addView(mainSpreadView, 0);
            childSpreadView = new ChildSpreadView(getContext());
            setChildDraw(childSpreadView);
            addView(childSpreadView, 1);
        }

    }

    public void initData(Collection<? extends ILem> datas) {
        if (type==1) {
            mMainView.initData(datas, mChildView);
        }else {
            mainSpreadView.initData(datas, childSpreadView);
        }
    }



}











