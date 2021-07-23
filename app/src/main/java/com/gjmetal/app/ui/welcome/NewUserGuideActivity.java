package com.gjmetal.app.ui.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Description：App 引导页
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-25  14:18
 */
@NoTouchView
public class NewUserGuideActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ivStartApp)
    ImageView ivStartApp;
    @BindView(R.id.ivStar1)
    ImageView ivStar1;
    @BindView(R.id.ivStar2)
    ImageView ivStar2;
    @BindView(R.id.ivStar3)
    ImageView ivStar3;
    @BindView(R.id.ivStar4)
    ImageView ivStar4;
    @BindView(R.id.ivStar5)
    ImageView ivStar5;
    private List<View> ViewList;
    private int position = 0;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_new_user_guide);
    }

    public static void launch(Activity context) {
        Router.newIntent(context)
                .to(NewUserGuideActivity.class)
                .data(new Bundle())
                .launch();
    }

    @Override
    protected void fillData() {
        ViewList = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        ViewList.add(inflater.inflate(R.layout.activity_guide_1, null));
        ViewList.add(inflater.inflate(R.layout.activity_guide_2, null));
        ViewList.add(inflater.inflate(R.layout.activity_guide_3, null));
        ViewList.add(inflater.inflate(R.layout.activity_guide_4, null));
        ViewList.add(inflater.inflate(R.layout.activity_guide_5, null));

        StepPagerAdapter adpter = new StepPagerAdapter();
        viewPager.setAdapter(adpter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                position = arg0;
                initPageView();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        initPageView();
    }

    @OnClick(R.id.ivStar1)
    public void ivStar1() {
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.ivStar2)
    public void ivStar2() {
        viewPager.setCurrentItem(1);
    }

    @OnClick(R.id.ivStar3)
    public void ivStar3() {
        viewPager.setCurrentItem(2);
    }

    @OnClick(R.id.ivStar4)
    public void ivStar4() {
        viewPager.setCurrentItem(3);
    }

    @OnClick(R.id.ivStar5)
    public void ivStar5() {
        viewPager.setCurrentItem(4);
    }

    @OnClick(R.id.ivStartApp)
    public void ivStartApp() {
        jumpToMain();
    }


    private void initPageView() {
        ivStar5.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                ivStar1.setImageResource(R.mipmap.ic_dian_res);
                ivStar2.setImageResource(R.mipmap.ic_dian_nor);
                ivStar3.setImageResource(R.mipmap.ic_dian_nor);
                ivStar4.setImageResource(R.mipmap.ic_dian_nor);
                ivStar5.setImageResource(R.mipmap.ic_dian_nor);
                break;
            case 1:
                ivStar1.setImageResource(R.mipmap.ic_dian_nor);
                ivStar2.setImageResource(R.mipmap.ic_dian_res);
                ivStar3.setImageResource(R.mipmap.ic_dian_nor);
                ivStar4.setImageResource(R.mipmap.ic_dian_nor);
                ivStar5.setImageResource(R.mipmap.ic_dian_nor);
                break;
            case 2:
                ivStar1.setImageResource(R.mipmap.ic_dian_nor);
                ivStar2.setImageResource(R.mipmap.ic_dian_nor);
                ivStar3.setImageResource(R.mipmap.ic_dian_res);
                ivStar4.setImageResource(R.mipmap.ic_dian_nor);
                ivStar5.setImageResource(R.mipmap.ic_dian_nor);
                break;
            case 3:
                ivStar1.setImageResource(R.mipmap.ic_dian_nor);
                ivStar2.setImageResource(R.mipmap.ic_dian_nor);
                ivStar3.setImageResource(R.mipmap.ic_dian_nor);
                ivStar4.setImageResource(R.mipmap.ic_dian_res);
                ivStar5.setImageResource(R.mipmap.ic_dian_nor);
                break;
            case 4:
                ivStar1.setImageResource(R.mipmap.ic_dian_nor);
                ivStar2.setImageResource(R.mipmap.ic_dian_nor);
                ivStar3.setImageResource(R.mipmap.ic_dian_nor);
                ivStar4.setImageResource(R.mipmap.ic_dian_nor);
                ivStar5.setImageResource(R.mipmap.ic_dian_res);
                break;
            default:
                break;
        }
    }


    private void jumpToMain() {
        SharedUtil.put(Constant.HAS_SHOW_GUIDE, "true");
        WelcomeActivity.launch(context);
        finish();
    }

    public class StepPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return ViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(ViewList.get(position));
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = ViewList.get(position);
            if (view.getParent() != null) {
                container.removeView(view);
            }
            container.addView(view);
            return ViewList.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

    }

}
