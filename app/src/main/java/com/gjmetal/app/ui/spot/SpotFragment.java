package com.gjmetal.app.ui.spot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.spot.SpotItems;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.my.MyInformationActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewPagerUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyViewPager;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：现货主界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:14
 */
public class SpotFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    Titlebar titleBar;
    @BindView(R.id.spotMagicIndicator)
    MagicIndicator spotMagicIndicator;
    @BindView(R.id.vpSpot)
    MyViewPager vpSpot;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private List<SpotItems> mDataList;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected int setRootView() {
        return R.layout.fragment_spot;
    }

    public SpotFragment() {
    }

    public void showNewMsgView(boolean hasNewMsg) {
        if (titleBar != null) {
            titleBar.setRedMsgView(hasNewMsg);
        }
        mHasNewMsg = hasNewMsg;
    }

    protected void initView() {
        titleBar.initStyle(Titlebar.TitleSyle.HOME_MENU, getString(R.string.spot_goods));
        if (titleBar != null) {
            titleBar.setRedMsgView(mHasNewMsg);
        }
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.launch(getActivity());
            }
        });
        getItems(false);
    }


    /**
     * 获取标题
     */
    private void getItems(final boolean againLoad) {
        Api.getSpotService().getItems().
                compose(XApi.<BaseModel<List<SpotItems>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<SpotItems>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<SpotItems>>>() {
                    @Override
                    public void onNext(BaseModel<List<SpotItems>> listBaseModel) {
                        if (ValueUtil.isEmpty(listBaseModel)) {
                            return;
                        }
                        mDataList = new ArrayList<>();
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            mDataList.addAll(listBaseModel.getData());
                            if (againLoad) {
                                BaseEvent baseEvent = new BaseEvent();
                                baseEvent.setRefreshSpot(true);
                                BusProvider.getBus().post(baseEvent);
                            }
                            upDateTitleTable(mDataList);
                        } else {
                            failAgainLoad(null);
                        }
                        DialogUtil.dismissDialog();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        failAgainLoad(error);
                        DialogUtil.dismissDialog();
                    }
                });


    }


    /**
     * 更新标题栏
     *
     * @param mDataList
     */
    private void upDateTitleTable(final List<SpotItems> mDataList) {
        if (vpSpot == null) {
            return;
        }
        SpotChildFragment childFragment;
        List<String> mTitleList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            childFragment = new SpotChildFragment(vpSpot, i, mDataList.get(i));
            mTitleList.add(mDataList.get(i).getCfgVal());
            mFragments.add(childFragment);
        }
        vpSpot.setOffscreenPageLimit(mDataList.size());
        vpSpot.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        });
        ViewPagerUtil.initView(getActivity(), spotMagicIndicator, mTitleList, vpSpot, R.color.c2A2D4F, R.color.c5, R.color.cFFFFFF, R.color.cD4975C);

        vpSpot.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {//处理手势冲突K线
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                vpSpot.setSideslip(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("spot_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageEnd("spot_time");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }

    private void failAgainLoad(NetError error) {
        if (spotMagicIndicator == null) {
            return;
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                DialogUtil.waitDialog(getActivity());
                spotMagicIndicator.setVisibility(View.VISIBLE);
                vpSpot.setVisibility(View.VISIBLE);
                vEmpty.setVisibility(View.GONE);
                getItems(true);
            }
        }, spotMagicIndicator, vpSpot);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            for (int i = 0; i < mFragments.size(); i++) {
                mFragments.get(i).setUserVisibleHint(false);
            }
        }
    }
}
