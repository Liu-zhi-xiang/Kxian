package com.gjmetal.app.ui.information;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.InfoMationTabBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.my.MyInformationActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewPagerUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyViewPager;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description： 资讯主界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:15
 */
public class InformationFragment extends BaseFragment {
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.viewpager_information)
    MyViewPager mViewPager;//内容
    @BindView(R.id.leftLayout)
    LinearLayout leftLayout;
    @BindView(R.id.titleBar)
    Titlebar titleBar;
    private List<Fragment> mFragments = new ArrayList<>();
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private List<InfoMationTabBean> mTabList = new ArrayList<>();
    private List<String> mDataList;

    public InformationFragment() {

    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_information;
    }


    public void showNewMsgView(boolean hasNewMsg) {
        if (titleBar != null) {
            titleBar.setRedMsgView(hasNewMsg);
        }
        mHasNewMsg = hasNewMsg;
    }

    protected void initView() {
        titleBar.initStyle(Titlebar.TitleSyle.INFORMATION_SEARCH, "");
        titleBar.getEtSearch().setHint(R.string.txt_search_to_want);
        getTabs();
        leftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyInformationActivity.launch(getActivity());
            }
        });
        titleBar.getEtSearch().setFocusable(false);
        titleBar.getCancelSearch().setVisibility(View.GONE);
        if (titleBar != null) {
            titleBar.setRedMsgView(mHasNewMsg);
        }
        titleBar.getTitleSearchView().getEtSearch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimeUtils.isCanClick()) {
                    startActivity(new Intent(getContext(), InfomationSearchActivity.class));
                }
            }
        });
    }

    //获取资讯tab
    public void getTabs() {
        DialogUtil.waitDialog(getActivity());
        Api.getInformationService().queryCols().
                compose(XApi.<BaseModel<List<InfoMationTabBean>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<InfoMationTabBean>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<InfoMationTabBean>>>() {
                    @Override
                    public void onNext(BaseModel<List<InfoMationTabBean>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        GjUtil.checkActState(getActivity());
                        if(vEmpty==null){
                            return;
                        }
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.WHITE);
                            return;
                        }
                        vEmpty.setVisibility(View.GONE);
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            mTabList.addAll(listBaseModel.getData());
                            upDateTitleTable(mTabList);
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        GjUtil.checkActState(getActivity());
                        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getTabs();
                            }
                        });
                    }
                });
    }

    /**
     * 更新标题栏
     */
    private void upDateTitleTable(final List<InfoMationTabBean> mTabList) {
        InformationChildFragment childFragment;//资讯
        FinanceDateFragment financeDateFragment;//财经日历
        FlashWebViewFragment flashWebViewFragment;//快讯
        mDataList = new ArrayList<>();
        for (InfoMationTabBean bean : mTabList) {
            if (ValueUtil.isStrNotEmpty(bean.getColName())) {
                mDataList.add(bean.getColName());
            }
            childFragment = new InformationChildFragment(bean.getColName(), bean.getColId());
            if (bean.getColId() == 1) {//财经日历
                financeDateFragment = new FinanceDateFragment();
                mFragments.add(financeDateFragment);
            } else if (bean.getColId() == 1007) {//快讯
                flashWebViewFragment = new FlashWebViewFragment();
                mFragments.add(flashWebViewFragment);
            } else {
                mFragments.add(childFragment);
            }
        }
        mViewPager.setOffscreenPageLimit(mDataList.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        });
        if(ValueUtil.isListNotEmpty(mTabList)){
            AppAnalytics.getInstance().onEvent(getContext(),"info_"+mTabList.get(0).getColId()+"_acess","资讯-各栏目-访问量");
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(ValueUtil.isListNotEmpty(mTabList)){
                    SharedUtil.putInt(Constant.INFORMATION_METAL_ITEM, position);
                    AppAnalytics.getInstance().onEvent(getContext(),"info_"+mTabList.get(position).getColId()+"_acess","资讯-各栏目-访问量");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ViewPagerUtil.initView(getActivity(), magicIndicator, mDataList, mViewPager, R.color.cE7EDF5, R.color.c2A2D4F, R.color.cD4975C, R.color.cD4975C);
    }

    /**
     * 添加资讯tabs
     */
    @OnClick(R.id.iv_add_information)
    public void addInforMation() {
        AppAnalytics.getInstance().onEvent(getContext(),"info_customize");
        if (User.getInstance().isLoginIng()) {
            InfomationTabsActivity.launch(getActivity());
        } else {
            LoginActivity.launch((Activity) getContext());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (hidden) {
                if (mFragments != null && mFragments.size() > SharedUtil.getInt(Constant.INFORMATION_METAL_ITEM)) {
                    mFragments.get(SharedUtil.getInt(Constant.INFORMATION_METAL_ITEM)).setUserVisibleHint(false);
                }
            }else {
                if (mFragments != null && mFragments.size() > SharedUtil.getInt(Constant.INFORMATION_METAL_ITEM)) {
                    mFragments.get(SharedUtil.getInt(Constant.INFORMATION_METAL_ITEM)).setUserVisibleHint(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getInformationVIP(final Context context, String code, final String function) {
        ReadPermissionsManager.readPermission( Constant.News.RECORD_NEWS_CODE
                , Constant.POWER_RECORD
                ,Constant.News.RECORD_NEWS_MODULE
                ,""
                , context
                , null
                , function, false, true, false,code).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
//                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
//                    BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.ACCESS.getValue()));
//                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("info_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageEnd("info_time");
    }
}
