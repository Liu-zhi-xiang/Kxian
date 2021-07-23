package com.gjmetal.app.ui.flash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.FlashTabsEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.flash.FlashMenu;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyViewPager;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description：快报主界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-8 16:38
 */

public class FlashFragment extends DelayerFragment {
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.flashMagicIndicator)
    MagicIndicator flashMagicIndicator;
    @BindView(R.id.vpFlash)
    MyViewPager vpFlash;
    @BindView(R.id.ivAddSub)
    ImageView ivAddSub;
    private int chilPosition = 0;
    private List<FlashMenu> mDataList = new ArrayList<>();
    private List<Fragment> flashFragments = new ArrayList<>();
    private boolean isFlag;//初始化一次做标记
    private MyFragmentPagerAdapter mfpa = null;
    private MyCommonNavigatorAdapter mcna = null;

    @Override
    protected int setRootView() {
        return R.layout.fragment_flash;
    }

    public FlashFragment() {

    }

    protected void initView() {
        //注册
        BusProvider.getBus().register(this);
        getItems(false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin() && isAdded() && baseEvent.getFlag() == null) {
            isFlag = true;
            getItems(false);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FlashTabsEvent(FlashTabsEvent flashTabsEvent) {
        if (flashTabsEvent.isFlash() && isAdded()) {
            isFlag = true;
            getItems(false);
        }
    }

    /**
     * 获取标题
     */
    private void getItems(final boolean againLoad) {
        Api.getFlashService().getNewsflashtagList().
                compose(XApi.<BaseModel<List<FlashMenu>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<FlashMenu>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<FlashMenu>>>() {
                    @Override
                    public void onNext(BaseModel<List<FlashMenu>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isEmpty(listBaseModel)) {
                            return;
                        }
                        mDataList.clear();
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            mDataList.addAll(listBaseModel.getData());
                        } else {
                            failAgainLoad(null);
                            return;
                        }

                        if (againLoad) {
                            BaseEvent baseEvent = new BaseEvent();
                            baseEvent.setRefreshSpot(true);
                            BusProvider.getBus().post(baseEvent);
                        }
                        upDateTitleTable();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        failAgainLoad(error);
                    }
                });


    }


    /**
     * 更新标题栏
     */
    private void upDateTitleTable() {
        if (flashFragments == null || mDataList == null || flashMagicIndicator == null) {
            return;
        }
        flashFragments.clear();
        List<String> mTitleList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            FlashChildFragment childFragment = new FlashChildFragment(i, mDataList.get(i));
            if (ValueUtil.isStrNotEmpty(mDataList.get(i).getTagName())) {
                mTitleList.add(mDataList.get(i).getTagName());
            }
            flashFragments.add(childFragment);
        }

        if (ValueUtil.isListEmpty(mTitleList)) {
            return;
        }
        if (isFlag) {
            if (mcna == null) {
                initView(getActivity(), flashMagicIndicator, mTitleList, vpFlash);
            }
            if (mfpa != null) {
                mfpa.notifyDataSetChanged();//刷新viewpaer
            }
            vpFlash.setCurrentItem(0);
            mcna.setListData(mTitleList);//刷新tab列表
            vpFlash.setOffscreenPageLimit(mDataList.size());
        } else {
            vpFlash.setOffscreenPageLimit(mDataList.size());
            mfpa = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
            vpFlash.setAdapter(mfpa);
            initView(getActivity(), flashMagicIndicator, mTitleList, vpFlash);
        }
        AppAnalytics.getInstance().onEvent(getContext(), "live_" + mDataList.get(0).getId() + "_acess");
        vpFlash.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chilPosition = position;
                AppAnalytics.getInstance().onEvent(getContext(), "live_" + mDataList.get(position).getId() + "_acess");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void failAgainLoad(NetError error) {
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                vEmpty.setVisibility(View.GONE);
                flashMagicIndicator.setVisibility(View.VISIBLE);
                ivAddSub.setVisibility(View.VISIBLE);
                vpFlash.setVisibility(View.VISIBLE);
                DialogUtil.waitDialog(getActivity());
                getItems(true);
            }
        }, flashMagicIndicator, ivAddSub, vpFlash);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("flash", "isVisibleToUser=" + isVisibleToUser);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        try {
            if (!isVisible) {
                if (flashFragments != null && flashFragments.size() > chilPosition) {
                    flashFragments.get(chilPosition).setUserVisibleHint(false);
                }
            } else {
                if (flashFragments != null && flashFragments.size() > chilPosition) {
                    flashFragments.get(chilPosition).setUserVisibleHint(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("live_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageEnd("live_time");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }

    @OnClick(R.id.ivAddSub)
    public void onViewClicked() {
        if (User.getInstance().isLoginIng()) {
            if (TimeUtils.isCanClick()) {
                Intent intent = new Intent(getContext(), FlashTabsActivity.class);
                startActivity(intent);
            }
        } else {
            LoginActivity.launch((Activity) getContext());
        }
    }

    //初始化viewpager与tab列表数据设置
    public void initView(final Context mContext, MagicIndicator magicIndicator, final List<String> mTitleList, final ViewPager viewPager) {
        mcna = new MyCommonNavigatorAdapter(mContext, mTitleList, viewPager);
        magicIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(mcna);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    //处理tab列表数据
    public class MyCommonNavigatorAdapter extends net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter {
        private Context mContext;
        private List<String> mTitleDataList;
        private ViewPager viewPager;

        public MyCommonNavigatorAdapter(Context mContext, List<String> mTitleDataList, ViewPager viewPager) {
            this.mContext = mContext;
            this.mTitleDataList = mTitleDataList;
            this.viewPager = viewPager;
        }

        public void setListData(List<String> mTitleDataList) {
            this.mTitleDataList = mTitleDataList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTitleDataList == null ? 0 : mTitleDataList.size();
        }

        @Override
        public IPagerTitleView getTitleView(Context context, final int index) {
            SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
            simplePagerTitleView.setText(mTitleDataList.get(index));
            simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext,R.color.c2A2D4F));
            simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext,R.color.cD4975C));
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
            linePagerIndicator.setColors(ContextCompat.getColor(mContext,R.color.cD4975C));
            linePagerIndicator.setLineHeight(DensityUtil.dp2px(3));
            linePagerIndicator.setLineWidth(DensityUtil.dp2px(10));
            linePagerIndicator.setYOffset(DensityUtil.dp2px(4));
            return linePagerIndicator;
        }
    }

    //处理viewpager
    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private FragmentManager fm;
        private SparseBooleanArray mList_Need_Update = new SparseBooleanArray();

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
            mList_Need_Update.clear();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position); //得到缓存的fragment

            Boolean update = mList_Need_Update.get(position);
            if (update != null && update) {
                String fragmentTag = fragment.getTag(); //得到tag，这点很重要
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment); //移除旧的fragment
                fragment = getItem(position); //换成新的fragment
                ft.add(container.getId(), fragment, fragmentTag); //添加新fragment时必须用前面获得的tag，这点很重要
                ft.attach(fragment);
                ft.commit();
                mList_Need_Update.put(position, false); //清除更新标记（只有重新启动的时候需要去创建新的fragment对象），防止正常情况下频繁创建对象
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return flashFragments.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return flashFragments.get(arg0);
        }

        /**
         * 使用这个方式，让页面不缓存，能够在清除fragment的时候对其做了删除
         *
         * @param object
         * @return
         */
        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

    }
}
