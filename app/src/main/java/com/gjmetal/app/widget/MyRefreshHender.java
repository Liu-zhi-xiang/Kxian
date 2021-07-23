package com.gjmetal.app.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.star.kit.KnifeKit;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import butterknife.BindView;

public class MyRefreshHender extends LinearLayout implements RefreshHeader {
    @BindView(R.id.ivHander)
    ImageView ivHander;
    @BindView(R.id.tvHander)
    TextView tvHander;
    @BindView(R.id.llHander)
    RelativeLayout llHander;

    private Context mContext;
    private AnimationDrawable imgAnimaton;

    public MyRefreshHender(Context context, int color) {
        super(context);
        setupView(context);
        setBackgroudColor(color);
    }

    public MyRefreshHender(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    public MyRefreshHender(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

    private void setupView(Context context) {
        this.mContext = context;

        inflate(context, R.layout.hander_view, this);
        KnifeKit.bind(this);

        imgAnimaton= (AnimationDrawable) ivHander.getDrawable();

    }

    private void setBackgroudColor(int color){
        llHander.setBackgroundColor(color);
    }

    @NonNull
    @Override
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {

    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {

    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        imgAnimaton.start();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        tvHander.setText(mContext.getResources().getString(R.string.stop_hander_load));
        return 20;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                tvHander.setText(mContext.getResources().getString(R.string.start_hander_load));
                break;

            case Refreshing:
                tvHander.setText(mContext.getResources().getString(R.string.stop_hander_load));
                imgAnimaton.start();
                break;
            case ReleaseToRefresh:
                imgAnimaton.stop();
                break;
        }
    }
}


















