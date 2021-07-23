package com.gjmetal.app.ui.alphametal.subtraction;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MyHelperAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.alphametal.subtraction.add.SubtractionAddActivity;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.my.ApplyForReadWebActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.MyDrawLogo;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.UpdateViewPagerUntil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.EmptyView;
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
import butterknife.OnClick;

/**
 * Description: 套利测算
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/23  15:17
 */

public class SubtractionFragment extends DelayerFragment {
    @BindView(R.id.ivMonthAddEdit)
    ImageView ivMonthAddEdit;
    @BindView(R.id.favMagicIndicator)
    MagicIndicator favMagicIndicator;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.vpMeasure)
    ViewPager vpMeasure;
    @BindView(R.id.rlLogoImage)
    View rlLogoImage;
    @BindView(R.id.vPermission)
    ApplyReadView applyReadView;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;

    private ArrayList<String> mNameLists;
    private ArrayList<Fragment> mFragments;
    private MyHelperAdapter mMyHelperAdapter;

    private int chilPosition = 0;
    private boolean initeViewsBool = false;
    private Future futureBran;
    private String function;
    public SubtractionFragment() {

    }

    @SuppressLint("ValidFragment")
    public SubtractionFragment(int index, Future futureBran) {
        this.indexs = index;
        this.futureBran = futureBran;
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_month;
    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        function=Constant.ApplyReadFunction.ZH_APP_AM_SUBTRACTION;
        rlLogoImage.setBackground(new MyDrawLogo(getContext(), -30));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (!isAdded()) {
            return;
        }
        if (baseEvent.isBackAddMonth() && vpMeasure != null) {
            vpMeasure.setCurrentItem(0);
            baseEvent.setBackAddMonth(false);
            BusProvider.getBus().post(baseEvent);
        }
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
                    }, rl, vpMeasure, favMagicIndicator, vEmpty);
            }

            @Override
            public void onSubscibeDialogShow() {
                if (applyReadView != null)
                    applyReadView.showApply(getActivity(), R.color.cD4975C, R.color.cffffff, applyReadView, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "1");
                        }
                    }, rl, vpMeasure, favMagicIndicator, vEmpty);
            }

            @Override
            public void onSubscibeYesShow() {
                showVIEW();
            }

            @Override
            public void onSubscibeError(NetError error) {
                showAgainLoad(error);
            }

            @Override
            public void onUnknown() {
                if (MainActivity.alphaPermission) {
                    AlphaMetalFragment.Subtraction = false;
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_KQJC_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SUBTRACTION);
                }
            }
        });
    }



    private void showVIEW() {
        initDatas();
        if (rl != null && vpMeasure != null && favMagicIndicator != null) {
            if (vpMeasure.getVisibility()==View.GONE)
            rl.setVisibility(View.VISIBLE);
            vpMeasure.setVisibility(View.VISIBLE);
        }
        if (vEmpty != null) {
            vEmpty.setVisibility(View.GONE);
        }
        if (applyReadView != null)
            applyReadView.setVisibility(View.GONE);
        AlphaMetalFragment.Subtraction = true;
    }

    private void initDatas() {
        if (initeViewsBool) {
            return;
        }
        mNameLists = new ArrayList<>();
        mFragments = new ArrayList<>();
        if (futureBran != null && futureBran.getSubItem() != null && futureBran.getSubItem().size() > 0) {
            for (int x = 0; x < futureBran.getSubItem().size(); x++) {
                mNameLists.add(futureBran.getSubItem().get(x).getName());
                SubtractionChildFragment monthChildFragment = new SubtractionChildFragment(x, futureBran.getSubItem().get(x));
                mFragments.add(monthChildFragment);
            }
        }
        intitTabLayout();
    }

    private void intitTabLayout() {
        if (vpMeasure == null || mFragments == null) {
            initeViewsBool = false;
            return;
        }
        vpMeasure.setOffscreenPageLimit(mFragments.size());
        // 更新标题栏
        mMyHelperAdapter = new MyHelperAdapter(getContext(), getChildFragmentManager(), mFragments);
        vpMeasure.setAdapter(mMyHelperAdapter);
        if (ValueUtil.isListNotEmpty(futureBran.getSubItem())) {
            AppAnalytics.getInstance().onEvent(getContext(), "alpha_arbitrage_" + futureBran.getSubItem().get(0).getType() + "_acess", "alpha-套利测算-各品种-访问量");
        }
        if (mFragments.size() > 1) {
            vpMeasure.setCurrentItem(1);
            chilPosition=1;
        }
        vpMeasure.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chilPosition = position;
                if (ValueUtil.isListNotEmpty(futureBran.getSubItem())) {
                    AppAnalytics.getInstance().onEvent(getContext(), "alpha_arbitrage_" + futureBran.getSubItem().get(position).getRoomCode() + "_acess", "alpha-套利测算-各品种-访问量");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        UpdateViewPagerUntil.initView(getActivity(), vpMeasure,
                favMagicIndicator, mNameLists, R.color.c2A2D4F,
                R.color.c9EB2CD, R.color.cFFFFFF, R.color.cD4975C, 16, new BaseCallBack() {
                    @Override
                    public void back(Object obj) {

                    }
                }).onPageSelected(chilPosition);
        initeViewsBool = true;

    }

    @OnClick(R.id.ivMonthAddEdit)
    public void onViewClicked() {
        if (User.getInstance().isLoginIng()) {
            toAddMonth();
        } else {
            LoginActivity.launch((Activity) getContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().AlphametalPageStart(futureBran.getType());//停留时间
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().AlphametalPageEnd(futureBran.getType());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);//解绑EventBus
    }

    private void toAddMonth() {
        DialogUtil.loadDialog(getContext());
        Api.getMyService().readCheckPowerTwo("/rest/basis/addCrossMetal,/rest/basis/addCrossMonthSubtractionContract")
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<CrossMetalModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<CrossMetalModel>> stringBaseModel) {
                        DialogUtil.dismissDialog();
                        if (stringBaseModel.code.equals(Constant.ResultCode.SUCCESS.getValue()) && stringBaseModel.data.get(0).getPermission()) {
                            //有权限;
                            SubtractionAddActivity.launch(getActivity(), stringBaseModel.data.get(0).getPermission(), stringBaseModel.data.get(1).getPermission());
                        } else {
                            ToastUtil.showToast(stringBaseModel.message);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (error.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())) {
                            ToastUtil.showToast(getResources().getString(R.string.not_vip_cheack));
                        } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                        } else {
                            ToastUtil.showToast(error.getMessage());
                        }
                    }
                });
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisibleToUser) {
        super.onFragmentVisibleChange(isVisibleToUser);
        if (isVisibleToUser && MainActivity.alphaPermission) {
            AlphaMetalFragment.Subtraction = false;
            AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_KQJC_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SUBTRACTION);
        }
        if (mFragments != null && mFragments.size() > chilPosition) {
            if (isVisibleToUser) {
                mFragments.get(chilPosition).setUserVisibleHint(true);
            } else {
                mFragments.get(chilPosition).setUserVisibleHint(false);
            }
        }
    }

    private void showAgainLoad(NetError error) {
        if (vpMeasure == null || rl == null || vEmpty == null || applyReadView == null) {
            return;
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                AlphaMetalFragment.Subtraction = false;
                AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_KQJC_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SUBTRACTION);
            }
        }, rl, vpMeasure, applyReadView);
    }
}
