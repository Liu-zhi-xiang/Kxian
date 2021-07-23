package com.gjmetal.app.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;


import com.gjmetal.app.base.BaseCallBack;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.List;

/**
 * Description：动态绑定指示器
 * Author: puyantao
 * Email: 1067899750@qq.com
 * Date: 2018-8-9 11:56
 */
public class UpdateViewPagerUntil {
    public UpdateViewPagerUntil() {

    }

    public static CommonNavigator initView(final Context mContext, final ViewPager vp, MagicIndicator magicIndicator,
                                           final List<String> mTitleList, int bgColor, final int normalColor,
                                           final int selectedColor, final int linColor,
                                           final int textSize,final BaseCallBack callBack) {

        CommonNavigatorAdapter mCommonNavigatorAdapter;
        magicIndicator.setBackgroundColor(ContextCompat.getColor(mContext, bgColor));
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(mCommonNavigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mTitleList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, normalColor));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, selectedColor));
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                simplePagerTitleView.setPadding(DensityUtil.dp2px(13), 0, DensityUtil.dp2px(13), 0);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onClick(View v) {
                        vp.setCurrentItem(index);
                        callBack.back(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setRoundRadius(5);
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, linColor));
                linePagerIndicator.setLineHeight(DensityUtil.dp2px(3));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, vp);
        return commonNavigator;
    }

    public static void initView(final Context mContext, MagicIndicator magicIndicator, final List<String> mTitleList, int bgColor,
                                final int normalColor, final int selectedColor, final int linColor, final int textSize, final BaseCallBack callBack) {
        magicIndicator.setBackgroundColor(ContextCompat.getColor(mContext, bgColor));
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mTitleList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, normalColor));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, selectedColor));
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                simplePagerTitleView.setPadding(DensityUtil.dp2px(20), 0, DensityUtil.dp2px(20), 0);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onClick(View v) {
                        callBack.back(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setRoundRadius(5);
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, linColor));
                linePagerIndicator.setLineHeight(DensityUtil.dp2px(3));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
    }

}



















