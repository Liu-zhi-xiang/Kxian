package com.gjmetal.app.ui.alphametal;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.ui.alphametal.calculator.CounterFragment;
import com.gjmetal.app.ui.alphametal.industry.IndustryFragment;
import com.gjmetal.app.ui.alphametal.lme.LMEChildNoScrollFragment;
import com.gjmetal.app.ui.alphametal.measure.MeasureFragment;
import com.gjmetal.app.ui.alphametal.subtraction.SubtractionFragment;
import com.gjmetal.app.ui.my.MyInformationActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewPagerUtil;
import com.gjmetal.app.widget.ControlScrollViewPager;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Description：交易助手
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-17 20:04
 */

public class AlphaMetalFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    Titlebar titleBar;
    @BindView(R.id.marketMagicIndicator)
    MagicIndicator marketMagicIndicator;
    @BindView(R.id.rgLMEView)
    RadioGroup rgLMEView;
    @BindView(R.id.rbMeasure)
    RadioButton rbMeasure;
    @BindView(R.id.rbLem)
    RadioButton rbLme;
    @BindView(R.id.rbComputer)
    RadioButton rbComputer;
    @BindView(R.id.vpHelper)
    ControlScrollViewPager vpHelper;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    public static String AT_PRESENTMENU_CODE = "";
    private List<String> mDataList;
    private List<Future> futureList;
    @Override
    protected int setRootView() {
        return R.layout.fragment_helper;
    }

    public void showNewMsgView(boolean hasNewMsg) {
        if (titleBar != null) {
            titleBar.setRedMsgView(hasNewMsg);
        }
        mHasNewMsg = hasNewMsg;
    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        titleBar.initStyle(Titlebar.TitleSyle.HOME_MENU, "");//2.4.2版本隐藏搜索
        if (titleBar != null) {
            titleBar.setRedMsgView(mHasNewMsg);
        }
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.launch(getActivity());
            }
        });
        getHelperExChanges();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        if (ValueUtil.isNotEmpty(socketEvent) && isAdded()) {
            SocketManager.socketHint(getActivity(), socketEvent.getSocketStatus(), titleBar.getTvSocketHint());
        }
    }

    /**
     * 获取码表
     */
    public  void getHelperExChanges() {
        if (vpHelper == null) {
            return;
        }
        vpHelper.setVisibility(View.VISIBLE);
        //内存获取
        futureList=  SharedUtil.ListDataSave.getDataList(Constant.ALPHAMETAL_MENU_STATE, Constant.ALPHAMETAL_CONFIG,Future.class);
        if (futureList!=null&&futureList.size() > 0){
            mDataList = new ArrayList<>();
            upDateTitleTable();
        }else {
            //网络获取
            Api.getMarketService().getMarketConfig("trade-arbity")
                    .compose(XApi.<BaseModel<List<Future>>>getApiTransformer())
                    .compose(XApi.<BaseModel<List<Future>>>getScheduler())
                    .subscribe(new ApiSubscriber<BaseModel<List<Future>>>() {
                        @Override
                        public void onNext(BaseModel<List<Future>> listBaseModel) {
                            if (vpHelper == null) {
                                return;
                            }
                            vpHelper.setVisibility(View.VISIBLE);
                            mDataList = new ArrayList<>();
                            if (listBaseModel != null && ValueUtil.isListNotEmpty(listBaseModel.getData())) {//数据存储到本地
                                futureList = listBaseModel.getData();
                                if (futureList != null && futureList.size() > 0) {
                                    upDateTitleTable();
                                    SharedUtil.ListDataSave.setDataList(Constant.ALPHAMETAL_MENU_STATE, futureList, Constant.ALPHAMETAL_CONFIG);
                                }
                            }
                        }
                        @Override
                        protected void onFail(NetError error) {
                            DialogUtil.dismissDialog();
                            if (error == null) {
                                return;
                            }
                            failAgainLoad(error);
                        }
                    });
        }
    }

    private void failAgainLoad(NetError error) {
        if (vEmpty == null) {
            return;
        }
        if (ValueUtil.isListEmpty(mDataList)) {
            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    getHelperExChanges();
                }
            }, vpHelper, marketMagicIndicator);
        } else {
            marketMagicIndicator.setVisibility(View.VISIBLE);
            vpHelper.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
        }
    }

    /**
     * 更新标题栏
     *
     * @param mDataList
     */
    private List<Fragment> mFragmentsTwo;

    private void upDateTitleTable(){
        marketMagicIndicator.setVisibility(View.VISIBLE);
        SubtractionFragment monthFragment; //  Subtraction  3 套利测算
        MeasureFragment measureFragment; // MEASURE  0 进口测算||  EXPORTPROFIT 0  出口测算||
        IndustryFragment industryFragment; //  4 产业测算
        LMEChildNoScrollFragment lmeFragment; //LME  1 升水铁
        CounterFragment counterFragment;// Options  2  股权计算器
        final List<Fragment> mFragments = new ArrayList<>();
        mFragmentsTwo=mFragments;
        marketMagicIndicator.setVisibility(View.VISIBLE);
        for (int i = 0; i < futureList.size(); i++) {
            mDataList.add(futureList.get(i).getName());
            switch (futureList.get(i).getType()) {
                case "3-4":
                case "3-1":
                    measureFragment = new MeasureFragment(futureList.get(i));
                    mFragments.add(measureFragment);
                    break;
                case "6":
                    lmeFragment = new LMEChildNoScrollFragment(futureList.get(i));
                    mFragments.add(lmeFragment);
                    break;
                case "7":
                    counterFragment = new CounterFragment(i,futureList.get(i));
                    mFragments.add(counterFragment);
                    break;
                case "3-5":
                    monthFragment = new SubtractionFragment(i,futureList.get(i));
                    mFragments.add(monthFragment);
                    break;
                case "3-6":
                    industryFragment = new IndustryFragment(i,futureList.get(i));
                    mFragments.add(industryFragment);
                    break;
                default:
                    break;
            }

        }
        AT_PRESENTMENU_CODE = futureList.get(0).getType();
        initViewPager(mDataList, mFragments);
    }


    private void initViewPager(List<String> mDataList, List<Fragment> mFragments) {

        vpHelper.setOffscreenPageLimit(mDataList.size());
        vpHelper.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        });
        vpHelper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SharedUtil.putInt(Constant.ALPHA_METAL_ITEM, position);
                if(ValueUtil.isListNotEmpty(futureList)){
                    AT_PRESENTMENU_CODE = futureList.get(position).getType();
                    AppAnalytics.getInstance().AlphametalOnEvent(getContext(),futureList.get(position).getType(),null, AppAnalytics.AlphametalChartEvent.ACCESS);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        ViewPagerUtil.initLmeTabView(getActivity(), marketMagicIndicator, mDataList, vpHelper);
        DialogUtil.dismissDialog();
    }

    public static boolean options = false, LME = false, Subtraction = false, IndustryMeasure = false, EXPORTPROFIT = false, MEASURE = false;
    public static void getAlphaMetal(final Context context, String code, final String function) {
        ReadPermissionsManager.readPermission(code
                , Constant.POWER_SOURCE
                , Constant.Alphametal.RESOURCE_MODULE
                , context
                , null
                , function, false, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
//                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
//                    BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.ACCESS.getValue()));
//                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {  // 不在最前端显示 相当于调用了onPause();
            if (mFragmentsTwo != null && mFragmentsTwo.size() > SharedUtil.getInt(Constant.ALPHA_METAL_ITEM)) {
                mFragmentsTwo.get(SharedUtil.getInt(Constant.ALPHA_METAL_ITEM)).setUserVisibleHint(false);
            }

        } else { // 在最前端显示 相当于调用了onResume();
            if (mFragmentsTwo != null && mFragmentsTwo.size() > SharedUtil.getInt(Constant.ALPHA_METAL_ITEM)) {
                mFragmentsTwo.get(SharedUtil.getInt(Constant.ALPHA_METAL_ITEM)).setUserVisibleHint(true);
            }
            if (futureList==null||futureList.size()==0){
                getHelperExChanges();
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }

}
