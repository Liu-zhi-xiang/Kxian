package com.gjmetal.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description：标题搜索
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-30 11:00
 */

public class TitleSearchView extends LinearLayout {
    @BindView(R.id.etSearch)
    ClearEditText etSearch;
    @BindView(R.id.tvSearchCancel)
    TextView tvSearchCancel;
    @BindView(R.id.vMarginLeft)
    View vMarginLeft;
    @BindView(R.id.rlTitleSearch)
    RelativeLayout rlTitleSearch;

    public TitleSearchView(Context context) {
        super(context);
    }

    public TitleSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_title_search, this);
        ButterKnife.bind(this);
    }

    public ClearEditText getEtSearch() {
        return etSearch;
    }
    public RelativeLayout getRlTitleSearch() {
        return rlTitleSearch;
    }
    public TextView getCancelSearch() {
        return tvSearchCancel;
    }

    public void showMarginLeft(boolean show){
        if(vMarginLeft!=null){
            vMarginLeft.setVisibility(show?View.VISIBLE:View.GONE);
        }

    }

}
