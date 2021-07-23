package com.gjmetal.app.base;

import com.gjmetal.star.kit.KnifeKit;

import butterknife.Unbinder;

/**
 * Created by huangb on 2018/4/4.
 */

public abstract class XBaseActivity extends BaseActivity {
    private Unbinder mUnbinder;

    @Override
    public void setBaseView() {
        super.setBaseView();
        setToolbarStyle();
        setContentView(setRootView());
        mUnbinder = KnifeKit.bind(this);
    }



    //设置布局
    protected abstract int setRootView();

    //设置toolbar
    protected abstract void setToolbarStyle();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

}
