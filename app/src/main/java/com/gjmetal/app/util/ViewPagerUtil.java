package com.gjmetal.app.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.List;

/**
 * Description：
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-7 12:50
 */
public class ViewPagerUtil {
    public static CommonNavigatorAdapter initView(final Context mContext, MagicIndicator magicIndicator, final List<String> mTitleList, final ViewPager viewPager, int bgColor, final int textNormalColor, final int selectedColor, final int selectedindexColor) {
        CommonNavigatorAdapter mCommonNavigatorAdapter;
        magicIndicator.setBackgroundColor(ContextCompat.getColor(mContext,bgColor));
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(mCommonNavigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mTitleList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext,textNormalColor));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext,selectedColor));
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                simplePagerTitleView.setPadding(35, 0, 35, 0);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setRoundRadius(5);
                linePagerIndicator.setColors(ContextCompat.getColor(mContext,selectedindexColor));
                linePagerIndicator.setLineHeight(DensityUtil.dp2px(3));
                linePagerIndicator.setPadding(0,0,0,15);
//                linePagerIndicator.setPaddingRelative(0,0,0,10);

                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        return mCommonNavigatorAdapter;
    }


    public static CommonNavigatorAdapter initLmeTabView(final Context mContext,
                                                        final MagicIndicator magicIndicator,
                                                        final List<String> mTitleList,
                                                        final ViewPager viewPager
                                                        ) {
        final View[] views = new View[mTitleList.size()];
        CommonNavigatorAdapter mCommonNavigatorAdapter;
        magicIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.c2A2D4F));
        AlphaMetalFragment.options=false;AlphaMetalFragment.LME=false;AlphaMetalFragment.Subtraction=false;
        AlphaMetalFragment.IndustryMeasure = true;AlphaMetalFragment.EXPORTPROFIT=false;AlphaMetalFragment.MEASURE=false;
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(mCommonNavigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(final Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(mContext);
                commonPagerTitleView.setContentView(R.layout.simple_pager_title_layout);

                // 初始化
                final TextView titleText = commonPagerTitleView.findViewById(R.id.title_text);
                titleText.setText(mTitleList.get(index));
//                titleText.setPadding(10, 0, 10, 0);
                if (index == 0) {
                    views[index] = titleText;
                    titleText.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_alphametal_tab_selecet));
                } else {
                    views[index] = titleText;
                    titleText.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_alphametal_tab_nor));
                }

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int i, int i1) {
                        titleText.setTextColor(ContextCompat.getColor(mContext,R.color.c25345B));
                        views[i].setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_alphametal_tab_selecet));
                    }

                    @Override
                    public void onDeselected(int i, int i1) {
                        titleText.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                        views[i].setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_alphametal_tab_nor));
                    }

                    @Override
                    public void onLeave(int i, int i1, float v, boolean b) {

                    }

                    @Override
                    public void onEnter(int i, int i1, float v, boolean b) {

                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setRoundRadius(5);
                linePagerIndicator.setColors(ContextCompat.getColor(mContext,R.color.c00000000));
                linePagerIndicator.setLineHeight(DensityUtil.dp2px(1));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        return mCommonNavigatorAdapter;

    }

}
