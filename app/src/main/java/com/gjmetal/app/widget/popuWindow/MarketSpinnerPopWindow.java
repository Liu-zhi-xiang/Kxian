package com.gjmetal.app.widget.popuWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.SpinnerGridViewAdapter;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.util.ValueUtil;

import java.util.List;


/**
 * Description：行情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-1 15:42
 */

public class MarketSpinnerPopWindow extends PopupWindow {
    private Context mContext;
    private View mView;
    private List<Future> mMarketSpinnerList;
    private SpinnerGridViewAdapter spinnerGridViewAdapter;
    private OnClickSpinnerListener onClickSpinnerListener;
    private String title;
    private TextView tvSelectedTitle;
    private GridView gvItem;

    public MarketSpinnerPopWindow(Context context, View view, String title, List<Future> marketSpinnerList, OnClickSpinnerListener onClickSpinnerListener) {
        super(context);
        this.mContext = context;
        this.mView = view;
        this.onClickSpinnerListener = onClickSpinnerListener;
        this.mMarketSpinnerList = marketSpinnerList;
        this.title = title;
        initView();
    }

    public MarketSpinnerPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public MarketSpinnerPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        // 用于PopupWindow的View
        View v = LayoutInflater.from(mContext).inflate(R.layout.popwindow_market_spinner, null, false);
        setContentView(v);
        gvItem = v.findViewById(R.id.gvItem);
        tvSelectedTitle = v.findViewById(R.id.tvSelectedTitle);

        tvSelectedTitle.setText(ValueUtil.isStrNotEmpty(title) ? title : "");
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
        showAtLocation(mView, Gravity.NO_GRAVITY, (mView.getWidth() / 2), y);

        if (ValueUtil.isListEmpty(mMarketSpinnerList)) {
            return;
        }
        spinnerGridViewAdapter = new SpinnerGridViewAdapter(mContext, mMarketSpinnerList);
        gvItem.setAdapter(spinnerGridViewAdapter);
        gvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                spinnerGridViewAdapter.onRefersh(position);
//                onClickSpinnerListener.onSelected(position);
//                title = mMarketSpinnerList.get(position).getExchange();
//                tvSelectedTitle.setText(ValueUtil.isStrNotEmpty(title) ? title : "");
//                dismiss();
            }
        });
    }


    public interface OnClickSpinnerListener {
        void onSelected(int index);
    }

}
























