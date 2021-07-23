package com.gjmetal.app.ui.alphametal.subtraction.add;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MyHelperAdapter;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 添加自定义跨越基差/跨品种基差
 * Description:
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/6/27  9:08
 */
public class SubtractionAddActivity extends XBaseActivity {
    @BindView(R.id.rbSubtraction)
    RadioButton rbSubtraction;
    @BindView(R.id.rbVariety)
    RadioButton rbVariety;
    @BindView(R.id.vpAddMeasure)
    ViewPager vpAddMeasure;

    private MyHelperAdapter mMyHelperAdapter;
    private ArrayList<Fragment> mFragments;
    private AddVarietyMeasureFragment addVarietyMeasureFragment;//跨品种基差
    private static String IntentSubtraction = "subtraction", IntentVariety = "variety";
    private boolean varietyBool;

    public static void launch(Activity context, boolean variety, boolean subtraction) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putBoolean(IntentSubtraction, subtraction);
            bundle.putBoolean(IntentVariety, variety);
            Router.newIntent(context)
                    .to(SubtractionAddActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    @Override
    protected int setRootView() {
        return R.layout.activity_add_measure_two;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, "新增自定义套利测算");
    }

    @Override
    protected void initView() {
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.finishSingActivity(SubtractionAddActivity.this);
            }
        });
        varietyBool = getIntent().getExtras().getBoolean(IntentVariety);
        mFragments = new ArrayList<>();

        addVarietyMeasureFragment = new AddVarietyMeasureFragment(varietyBool);
        mFragments.add(addVarietyMeasureFragment);
        vpAddMeasure.setOffscreenPageLimit(mFragments.size());
        // 更新标题栏
        mMyHelperAdapter = new MyHelperAdapter(this, getSupportFragmentManager(), mFragments);
        vpAddMeasure.setAdapter(mMyHelperAdapter);
        vpAddMeasure.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mFragments.size() <= 1) {
                    return;
                }
                if (position == 0) {
                    rbSubtraction.setChecked(true);
                    rbVariety.setChecked(false);
                    AppAnalytics.getInstance().onEvent(context, "alpha_arbitrage_0_acess", "alpha-套利测算-跨月基差测算公式访问量");
                } else {
                    rbVariety.setChecked(true);
                    rbSubtraction.setChecked(false);
                    AppAnalytics.getInstance().onEvent(context, "alpha_arbitrage_1_acess", "alpha-套利测算-跨品种测算公式访问量");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        AppAnalytics.getInstance().onEvent(context, "alpha_arbitrage_1_acess", "alpha-套利测算-跨品种测算公式访问量");

    }

    @Override
    protected void fillData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFragments != null) {
            mFragments.clear();
        }
    }
}
