package com.gjmetal.app.ui.market.change;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：自选管理主界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-30 11:31
 */
public class AddMarketTagActivity extends BaseActivity {
    @BindView(R.id.vpChange)
    ViewPager vpChange;
    private List<Fragment> mFragments;
    private AllFutureFragment allFutureFragment;
    private HasChangeFragment hasChangeFragment;
    private boolean hasData;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_market_tag);
        KnifeKit.bind(this);

        hasData = getIntent().getBooleanExtra(Constant.MODEL, false);
        initTitleSyle(Titlebar.TitleSyle.RADIO_GROUP_ADD, "", hasData ? getString(R.string.txt_clear) : "");


        allFutureFragment = new AllFutureFragment();
        hasChangeFragment = new HasChangeFragment(new BaseCallBack() {
            @Override
            public void back(Object obj) {
                selectAll();
                titleBar.getRadioGroup().check(R.id.rbSpot);
            }

        });
        mFragments = new ArrayList<>();
        mFragments.add(hasChangeFragment);
        mFragments.add(allFutureFragment);
        vpChange.setOffscreenPageLimit(mFragments.size());
        titleBar.setCheckedChangeListener(( group,  checkedId) ->{
            switch (checkedId) {
                case R.id.rbFuture://已选
                    selectHas();
                    break;
                case R.id.rbSpot://全部
                    selectAll();
                    break;
            }
    });

        titleBar.getTvRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAllChooseDialog();
            }
        });
        SocketManager.getInstance().leaveAllRoom();
    }

    private void selectHas() {
        initTitleSyle(Titlebar.TitleSyle.RADIO_GROUP_ADD, "", hasData || hasChangeFragment.getShowClear() ? getString(R.string.txt_clear) : "");
        vpChange.setCurrentItem(0);
        titleBar.getRadioGroup().check(R.id.rbFuture);
    }

    private void selectAll() {
        initTitleSyle(Titlebar.TitleSyle.RADIO_GROUP_ADD_SEARCH, "");
        vpChange.setCurrentItem(1);
        titleBar.getRadioGroup().check(R.id.rbSpot);
    }

    private void showClearAllChooseDialog() {
        new HintDialog(context, getString(R.string.sure_clear_all_choose), new DialogCallBack() {
            @Override
            public void onSure() {
                hasChangeFragment.clearAllChoose();
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    @Override
    protected void fillData() {
        vpChange.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        });
        //判断行情是否有自选，没有自动跳转到全部选择
        if (hasData) {
            selectHas();
        } else {
            selectAll();
        }
        vpChange.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        titleBar.getRadioGroup().check(R.id.rbFuture);
                        selectHas();
                        break;
                    case 1:
                        titleBar.getRadioGroup().check(R.id.rbSpot);
                        selectAll();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static void launch(Activity activity, boolean hasData) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constant.MODEL, hasData);
            Router.newIntent(activity)
                    .to(AddMarketTagActivity.class)
                    .data(bundle)
                    .launch();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }
}
