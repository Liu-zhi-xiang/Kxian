package com.gjmetal.app.ui.alphametal.calculator;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MyHelperAdapter;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.my.ApplyForReadWebActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.MyDrawLogo;
import com.gjmetal.app.util.UpdateViewPagerUntil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.NetError;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *  Description: 期权计算机器
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/11/18  18:26
 *
 */

public class CounterFragment extends DelayerFragment {
    @BindView(R.id.favMagicIndicator)
    MagicIndicator favMagicIndicator;
    @BindView(R.id.vpHelper)
    ViewPager vpHelper;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.rlLogoImage)
    View rlLogoImage;
    @BindView(R.id.vPermission)
    ApplyReadView applyReadView;
    @BindView(R.id.periodEmpty)
    EmptyView periodEmpty;

    private MyHelperAdapter mMyHelperAdapter;
    private List<String> mNameLists;
    private List<Fragment> mFragments;
    private CommonNavigator mCommonNavigator;
    private UpdateViewPagerUntil mUpdateViewPagerUntil;
    private boolean initViewsBool = false;

    private String function = Constant.ApplyReadFunction.ZH_APP_AM_OPTION_CALCULATOR;
    private Future future;
    private int index;

    public CounterFragment() {
    }

    @SuppressLint("ValidFragment")
    public CounterFragment(int index, Future future) {
        this.index = index;
        this.future = future;
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_counter;
    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        rlLogoImage.setBackground(new MyDrawLogo(getContext(), -30));
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

    }

    private void initDatas() {
        if (initViewsBool) {
            return;
        }
        mNameLists = new ArrayList<>();
        mFragments = new ArrayList<>();
        if (ValueUtil.isListNotEmpty(future.getSubItem())) {
            for (int i = 0; i < future.getSubItem().size(); i++) {
                mNameLists.add(future.getSubItem().get(i).getName());
            }
            if (future.getSubItem().size() == 1) {
                CounterPeriodFragment counterPeriodFragment = new CounterPeriodFragment("0");
                mFragments.add(counterPeriodFragment);
            }
            if (future.getSubItem().size() == 2) {
                CounterLMEFragment counterLMEFragment = new CounterLMEFragment("1");
                mFragments.add(counterLMEFragment);
            }
        } else {
            mNameLists.add("上期所期权");
            CounterPeriodFragment counterPeriodFragment = new CounterPeriodFragment("0");
            mFragments.add(counterPeriodFragment);
        }
        if (vpHelper == null) {
            return;
        }
        vpHelper.setOffscreenPageLimit(1);
        // 更新标题栏
        mMyHelperAdapter = new MyHelperAdapter(getContext(), getChildFragmentManager(), mFragments);
        vpHelper.setAdapter(mMyHelperAdapter);

        mUpdateViewPagerUntil = new UpdateViewPagerUntil();
        mCommonNavigator = UpdateViewPagerUntil.initView(getActivity(), vpHelper,
                favMagicIndicator, mNameLists, R.color.c2A2D4F,
                R.color.c9EB2CD, R.color.cFFFFFF, R.color.cD4975C, 16, new BaseCallBack() {
                    @Override
                    public void back(Object obj) {

                    }
                });
        initViewsBool = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ApplyEvent(ApplyEvent applyEvent) {
        ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
            @Override
            public void onSubscibeDialogCancel() {
                if (applyReadView != null)
                    applyReadView.showPassDueApply(getActivity(), applyReadView, R.color.cD4975C, R.color.cffffff, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "2");
                        }
                    }, rl, vpHelper, view, periodEmpty);
            }

            @Override
            public void onSubscibeDialogShow() {
                if (applyReadView != null)
                    applyReadView.showApply(getActivity(), R.color.cD4975C, R.color.cffffff, applyReadView, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "1");
                        }
                    }, rl, vpHelper, view, periodEmpty);
            }

            @Override
            public void onSubscibeYesShow() {
                initDatas();
                if (rl != null && vpHelper != null) {
                    rl.setVisibility(View.VISIBLE);
                    vpHelper.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
                if (applyReadView != null) {
                    applyReadView.setVisibility(View.GONE);
                }
                if (periodEmpty != null) {
                    periodEmpty.setVisibility(View.GONE);
                }
                AlphaMetalFragment.options = true;
                AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_SQSQQ_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SHFE_MONITOR);
            }

            @Override
            public void onSubscibeError(NetError error) {
                showAgainLoad(error);
            }

            @Override
            public void onUnknown() {
                if (MainActivity.alphaPermission) {
                    AlphaMetalFragment.options = true;
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_SQSQQ_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SHFE_MONITOR);
                }
            }
        });
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (!isVisible && ValueUtil.isListNotEmpty(mFragments)) {
            for (int i = 0; i < mFragments.size(); i++) {
                mFragments.get(i).setUserVisibleHint(false);
            }
        }
        if (isVisible && mFragments != null && mFragments.size() > 0) {
            mFragments.get(0).setUserVisibleHint(true);
        }
        if (isVisible && MainActivity.alphaPermission) {
            SocketManager.getInstance().leaveAllRoom();
            AlphaMetalFragment.options = false;
            AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_QQJSQ_CODE, Constant.ApplyReadFunction.ZH_APP_AM_OPTION_CALCULATOR);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().AlphametalPageStart(future.getType());//alpha-期权计算器-停留时间
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().AlphametalPageEnd(future.getType());
    }

    private void showAgainLoad(NetError error) {
        if (periodEmpty == null || rl == null || vpHelper == null || applyReadView == null) {
            return;
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, periodEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                AlphaMetalFragment.options = false;
                AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_QQJSQ_CODE, Constant.ApplyReadFunction.ZH_APP_AM_OPTION_CALCULATOR);
            }
        }, rl, vpHelper, view, applyReadView);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }
}
