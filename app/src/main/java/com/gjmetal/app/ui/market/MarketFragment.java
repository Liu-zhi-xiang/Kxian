package com.gjmetal.app.ui.market;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.event.MarketSearchEvent;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.AppVersionManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.ui.market.search.SearchMarketActivity;
import com.gjmetal.app.ui.my.MyInformationActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.FragmentPagerAdapter;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.GuideHintDialog;
import com.gjmetal.app.widget.dialog.PrivacyPolicyDialog;
import com.gjmetal.app.widget.dialog.VersionDialog;
import com.gjmetal.app.widget.looper.LoopViewPager;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.NetError;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：行情主界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:15
 */

public class MarketFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    Titlebar titleBar;
    @BindView(R.id.ivUpMenu)
    ImageView ivUpMenu;
    @BindView(R.id.tvTabTitle)
    TextView tvTabTitle;
    @BindView(R.id.ivNextMenu)
    ImageView ivNextMenu;
    @BindView(R.id.vpFuture)
    LoopViewPager vpFuture;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private Handler handler = new Handler();
    private List<Future> titleList;
    private List<Fragment> mFragments = new ArrayList<>();
    private int pageIndex = 0;
    private String pageName;//页面名称
    @Override
    protected int setRootView() {
        return R.layout.fragment_market;
    }

    public void showNewMsgView(boolean hasNewMsg) {
        if (titleBar != null) {
            titleBar.setRedMsgView(hasNewMsg);
        }
        mHasNewMsg = hasNewMsg;
    }

    protected void initView() {
        BusProvider.getBus().register(this);//注册EventBus
        titleBar.initStyle(Titlebar.TitleSyle.HOME_MARKET, "");
        if (titleBar != null) {
            titleBar.setRedMsgView(mHasNewMsg);
        }
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.launch(getActivity());
            }
        });
        //搜索
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseEvent baseEvent = new BaseEvent();
                if (ValueUtil.isListNotEmpty(titleList)) {
                    for (int i = 0; i < titleList.size(); i++) {
                        if (i == pageIndex) {
                            titleList.get(i).setSelected(true);
                        } else {
                            titleList.get(i).setSelected(false);
                        }
                    }
                    baseEvent.setFutureList(titleList);
                }
                SearchMarketActivity.launch(getActivity(), baseEvent);
            }
        });
        getExChanges();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        if (ValueUtil.isNotEmpty(socketEvent) && isAdded()) {
            SocketManager.socketHint(getActivity(), socketEvent.getSocketStatus(), titleBar.getTvSocketHint());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MarketSearchEvent(MarketSearchEvent marketSearchEvent) {
        try {
            if (ValueUtil.isNotEmpty(marketSearchEvent) && isAdded()) {
                if (vpFuture == null) {
                    return;
                }
                pageIndex = marketSearchEvent.index;
                SharedUtil.putInt(Constant.MARKET_PAGE_INDEX_1, pageIndex);
                vpFuture.setCurrentItem(pageIndex, false);
                tvTabTitle.setText(titleList.get(pageIndex).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.ivNextMenu, R.id.ivUpMenu})
    public void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.ivNextMenu:
                clickTitleTab(true);
                break;
            case R.id.ivUpMenu:
                clickTitleTab(false);
                break;
        }
    }

    private void clickTitleTab(boolean isNext) {
        try {
            if (ValueUtil.isListEmpty(titleList)) {
                ToastUtil.showToast("未获取到数据");
                return;
            }
            int current = vpFuture.getCurrentItem();
            if (isNext) {
                if (current == titleList.size() - 1) {
                    current = 0;
                } else {
                    current = current + 1;
                }
            } else {
                if (current == 0) {
                    current = titleList.size() - 1;
                } else {
                    current = current - 1;
                }
            }
            SharedUtil.putInt(Constant.MARKET_PAGE_INDEX_1, current);
            vpFuture.setCurrentItem(current, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取标题栏
     */
    private void getExChanges() {
        try {
            titleList = new ArrayList<>();
            titleList.addAll(SharedUtil.ListDataSave.getMarketDataList(Constant.GMETAL_DB, Constant.MARKET_CONFIG));
            vpFuture.setVisibility(View.VISIBLE);
            ivNextMenu.setVisibility(View.VISIBLE);
            ivUpMenu.setVisibility(View.VISIBLE);
            if (ValueUtil.isListNotEmpty(titleList)) {
                upDateTitleTable(titleList);
            } else {
                failAgainLoad(null);
            }

            String hasHint = SharedUtil.get(Constant.GUIDE, Constant.GuideType.MARKET_ITEM.getValue() + "");
            if (ValueUtil.isStrEmpty(hasHint)) {
                showHint();
            }else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateApp();
                    }
                }, 2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHint() {
        //隐私政策
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new PrivacyPolicyDialog(getContext(), new DialogCallBack() {
                @Override
                public void onSure() {

                }
                @Override
                public void onCancel() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //版本更新
                            updateApp();
                        }
                    }, 1000);
                    //引导说明
                    new GuideHintDialog(getActivity(), Constant.GuideType.MARKET_ITEM.getValue()).show();
                }
            }).onSaveInstanceState();
        }
    }

    private void updateApp() {
        //检测版本更新
        AppVersionManager.updateVersion(getActivity(), false, null, new VersionDialog.DialogCallBack() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {

            }
            @Override
            public void onLoadFinish() {
            }
        });
    }

    private void failAgainLoad(NetError error) {
        if (vEmpty == null) {
            return;
        }
        if (ValueUtil.isListEmpty(titleList)) {
            vpFuture.setVisibility(View.GONE);
            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    vEmpty.setVisibility(View.GONE);
                    getExChanges();
                }
            });
        } else {
            vpFuture.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
        }
    }


    /**
     * 更新标题栏
     *
     * @param titleList
     */
    private void upDateTitleTable(final List<Future> titleList) {
        if (ValueUtil.isListEmpty(titleList)) {
            return;
        }
        for (int i = 0; i < titleList.size(); i++) {
            if (ValueUtil.isStrNotEmpty(titleList.get(i).getType()) && titleList.get(i).getType().equals(Constant.MenuType.FOUR.getValue())) {//4 场外期权
                MarketOtcOptionsFragment marketOtcOptionsFragment = new MarketOtcOptionsFragment(i, 1, titleList.get(i));
                mFragments.add(marketOtcOptionsFragment);
            } else {
                MarketChildFragment marketChildFragment = new MarketChildFragment(i, 0, titleList.get(i), titleList.get(i).getSubItem());
                mFragments.add(marketChildFragment);
            }
        }
        if (ValueUtil.isListNotEmpty(titleList)) {
            pageName = String.valueOf(titleList.get(0).getId());
        }
        SharedUtil.put(Constant.MARKET_PAGE_SELECTED, "0");
        SharedUtil.putInt(Constant.MARKET_PAGE_INDEX_1, 0);

        vpFuture.setOffscreenPageLimit(mFragments.size() - 1);
        vpFuture.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int var1) {
                return mFragments.get(var1);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }
        });
        vpFuture.setCurrentItem(0, false);
        if (ValueUtil.isListEmpty(titleList)) {
            return;
        }
        tvTabTitle.setText(titleList.get(pageIndex).getName());
        AppAnalytics.getInstance().onEvent(getActivity(), "market_" + titleList.get(0).getId() + "_acess", "行情-各交易所-访问量");
        vpFuture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pageIndex = position;
                AppAnalytics.getInstance().onEvent(getActivity(), "market_" + titleList.get(pageIndex).getId() + "_acess", "行情-各交易所-访问量");
                if (ValueUtil.isListNotEmpty(titleList)) {
                    pageName = String.valueOf(titleList.get(pageIndex).getId());
                    if (ValueUtil.isStrNotEmpty(titleList.get(position).getType())) {//场外期权、利率
                        if (titleList.get(position).getType().equals(Constant.MenuType.FOUR.getValue())) {
                            SocketManager.getInstance().leaveAllRoom();
                        } else if (titleList.get(position).getType().equals(Constant.MenuType.FIVE.getValue())) {
                            SocketManager.getInstance().leaveAllRoom();
                        }
                    }
                }
                SharedUtil.putInt(Constant.MARKET_PAGE_INDEX_1, position);
                tvTabTitle.setText(titleList.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && ValueUtil.isListNotEmpty(mFragments)) {
            for (int i = 0; i < mFragments.size(); i++) {
                mFragments.get(i).setUserVisibleHint(false);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            GjUtil.closeMarketTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ValueUtil.isStrNotEmpty(pageName)) {
            AppAnalytics.getInstance().onPageStart("market_" + pageName + "_time");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ValueUtil.isStrNotEmpty(pageName)) {
            AppAnalytics.getInstance().onPageEnd("market_" + pageName + "_time");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }
}
