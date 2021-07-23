package com.gjmetal.app.widget.popuWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.star.kit.KnifeKit;


/**
 * Description：行情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-1 15:42
 */

public class MarketPopWindow extends PopupWindow {
    private TextView tvMarketTop;
    private TextView tvMarketDelete;
    private TextView tvMarketEdit;
    private View mView1;
    private View mView2;
    private Context mContext;
    private View mView;
    private OnClickListener mOnClickListener;

    private boolean isShowEdit = false;

    public MarketPopWindow(Context context, View view, boolean showEdit, OnClickListener listener) {
        super(context);
        this.mContext = context;
        this.mView = view;
        this.isShowEdit = showEdit;
        this.mOnClickListener = listener;
        initView();
    }

    public MarketPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public MarketPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        // 用于PopupWindow的View
        View v = LayoutInflater.from(mContext).inflate(R.layout.popwindow_market, null, false);
        setContentView(v);
        tvMarketTop = v.findViewById(R.id.tvMarketTop);
        tvMarketDelete = v.findViewById(R.id.tvMarketDelete);
        tvMarketEdit = v.findViewById(R.id.tvMarketEdit);
        mView1 = v.findViewById(R.id.editView);
        mView2 = v.findViewById(R.id.topView);
        KnifeKit.bind(v);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true); //能否获得焦点
        // 设置PopupWindow的背景
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        setTouchable(true);
        int[] location = new int[2];
        mView.getLocationOnScreen(location);
        int y = location[1];
        showAtLocation(mView, Gravity.NO_GRAVITY, (mView.getWidth() / 2), y - dip2px(mContext, 25));

        if (isShowEdit){
            tvMarketEdit.setVisibility(View.VISIBLE);
            mView1.setVisibility(View.VISIBLE);
        } else {
            tvMarketEdit.setVisibility(View.GONE);
            mView1.setVisibility(View.GONE);
        }

        tvMarketTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onTop();
                dismiss();
            }
        });

        tvMarketEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onEdit();
            }
        });

        tvMarketDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onDelete();
                dismiss();
            }
        });
    }

    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnClickListener {
        void onTop();

        void onDelete();

        void onEdit();
    }

}
























