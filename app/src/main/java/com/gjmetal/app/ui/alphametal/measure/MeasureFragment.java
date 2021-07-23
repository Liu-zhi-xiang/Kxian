package com.gjmetal.app.ui.alphametal.measure;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MyHelperAdapter;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.my.ApplyForReadWebActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.MyDrawLogo;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.UpdateViewPagerUntil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.NetError;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description:进口、出口测算
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/23  15:18
 */
@SuppressLint("ValidFragment")
public class MeasureFragment extends DelayerFragment {
    @BindView(R.id.favMagicIndicator)
    MagicIndicator favMagicIndicator;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.vpMeasure)
    ViewPager vpMeasure;
    @BindView(R.id.rlLogoImage)
    View rlLogoImage;
    @BindView(R.id.tvVipServiceone)
    TextView tvVipServiceone;
    @BindView(R.id.tvVipServicetwo)
    TextView tvVipServicetwo;
    @BindView(R.id.tvVipServicePhoneOne)
    TextView tvVipServicePhoneOne;
    @BindView(R.id.tvVipServicePhonetwo)
    TextView tvVipServicePhonetwo;
    @BindView(R.id.tvApplyForRead)
    TextView tvApplyForRead;
    @BindView(R.id.vPermission)
    ApplyReadView applyReadView;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;


    private boolean initeViewsBool = false;
    private MyHelperAdapter mMyHelperAdapter;
    private List<String> mNameLists;
    private List<Fragment> mFragments;

    private int chilPosition = 0;
    private Future futureBran;
    private String function;

    public MeasureFragment(Future future) {
        this.futureBran = future;
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_measure;
    }

    @Override
    protected void initView() {

        BusProvider.getBus().register(this);
        if (futureBran != null && futureBran.getSubItem() != null) {
            if (futureBran.getType().equals(Constant.MenuType.THREE_ONE.getValue())) {
                function = Constant.ApplyReadFunction.ZH_APP_AM_IMPORT_MEASURE;
            } else if (futureBran.getType().equals(Constant.MenuType.THREE_FOUR.getValue())) {
                function = Constant.ApplyReadFunction.ZH_APP_AM_Export_MEASURE;
            }
            if (futureBran.getSubItem() == null || futureBran.getSubItem().size() == 1) {
                rl.setVisibility(View.GONE);
            } else {
                rl.setVisibility(View.VISIBLE);
            }
        }
        mNameLists = new ArrayList<>();
        mFragments = new ArrayList<>();
        rlLogoImage.setBackground(new MyDrawLogo(getContext(), -30));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(ApplyEvent applyEvent) {
        ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
            @Override
            public void onSubscibeDialogCancel() {
                if (applyReadView != null)
                    applyReadView.showPassDueApply(getActivity(), applyReadView, R.color.cD4975C, R.color.cffffff, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "2");
                        }
                    }, rl, vpMeasure, vEmpty);
            }

            @Override
            public void onSubscibeDialogShow() {
                if (applyReadView != null)
                    applyReadView.showApply(getActivity(), R.color.cD4975C, R.color.cffffff, applyReadView, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "1");
                        }
                    }, rl, vpMeasure, vEmpty);
            }

            @Override
            public void onSubscibeYesShow() {
                showView();
            }

            @Override
            public void onSubscibeError(NetError error) {
                showAgainLoad(error);
            }

            @Override
            public void onUnknown() {
                if (MainActivity.alphaPermission) {
                    if (futureBran.getType().equals(Constant.MenuType.THREE_ONE.getValue())) {
                        AlphaMetalFragment.MEASURE = false;
                        AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_JKYK_CODE, Constant.ApplyReadFunction.ZH_APP_AM_IMPORT_MEASURE);
                    }
                    if (futureBran.getType().equals(Constant.MenuType.THREE_FOUR.getValue())) {
                        AlphaMetalFragment.EXPORTPROFIT = false;
                        AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CKCS_CODE, Constant.ApplyReadFunction.ZH_APP_AM_Export_MEASURE);
                    }
                }
            }
        });
    }

    private void showView() {
        initDatas();
        if (rl != null && vpMeasure != null) {
            if (futureBran.getType().equals(Constant.MenuType.THREE_FOUR.getValue())) {
                AlphaMetalFragment.EXPORTPROFIT = true;
                if (futureBran.getSubItem() != null && futureBran.getSubItem().size() >= 2) {
                    if (rl.getVisibility() == View.GONE)
                        rl.setVisibility(View.VISIBLE);
                } else {
                    if (rl.getVisibility() == View.VISIBLE)
                        rl.setVisibility(View.GONE);
                }
            } else if (futureBran.getType().equals(Constant.MenuType.THREE_ONE.getValue())) {
                if (rl.getVisibility() == View.GONE)
                    rl.setVisibility(View.VISIBLE);
                AlphaMetalFragment.MEASURE = true;
            }
            if (vpMeasure.getVisibility() == View.GONE)
                vpMeasure.setVisibility(View.VISIBLE);
        }
        if (vEmpty != null && vEmpty.getVisibility() == View.VISIBLE) {
            vEmpty.setVisibility(View.GONE);
        }
        if (applyReadView != null && applyReadView.getVisibility() == View.VISIBLE)
            applyReadView.setVisibility(View.GONE);
    }

    private void initDatas() {
        if (initeViewsBool) {
            return;
        }
        if (futureBran != null) {
            if (futureBran.getSubItem() != null && futureBran.getSubItem().size() > 0) {
                for (int x = 0; x < futureBran.getSubItem().size(); x++) {
                    MeasureChildFragment measureChildFragment = new MeasureChildFragment(x, futureBran.getSubItem().get(x));
                    mFragments.add(measureChildFragment);
                    mNameLists.add(futureBran.getSubItem().get(x).getName());
                }
            } else if (futureBran.getRoomItem() != null && futureBran.getRoomItem().size() > 0) {
                MeasureChildFragment measureChildFragment = new MeasureChildFragment(0, futureBran);
                mFragments.add(measureChildFragment);
            }
        }
        initTabLayout();
    }

    private void initTabLayout() {
        if (vpMeasure == null || mFragments == null) {
            initeViewsBool = false;
            return;
        }
        vpMeasure.setOffscreenPageLimit(mFragments.size());
        // 更新标题栏
        mMyHelperAdapter = new MyHelperAdapter(getContext(), getChildFragmentManager(), mFragments);
        vpMeasure.setAdapter(mMyHelperAdapter);
        if (ValueUtil.isListNotEmpty(futureBran.getSubItem())) {
            AppAnalytics.getInstance().AlphametalOnEvent(getContext(), futureBran.getType(), futureBran.getSubItem().get(0).getRoomCode(), AppAnalytics.AlphametalChartEvent.ACCESS);
        }

        UpdateViewPagerUntil.initView(getActivity(), vpMeasure,
                favMagicIndicator, mNameLists, R.color.c2A2D4F,
                R.color.c9EB2CD, R.color.cFFFFFF, R.color.cD4975C, 16, new BaseCallBack() {
                    @Override
                    public void back(Object obj) {
                        SharedUtil.putInt(Constant.MEASURE_ITEM, (Integer) obj);
                    }
                });
        vpMeasure.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chilPosition = position;
                if (ValueUtil.isListNotEmpty(futureBran.getSubItem())) {
                    AppAnalytics.getInstance().AlphametalOnEvent(getContext(), futureBran.getType(), futureBran.getSubItem().get(position).getRoomCode(), AppAnalytics.AlphametalChartEvent.ACCESS);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initeViewsBool = true;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisibleToUser) {
        super.onFragmentVisibleChange(isVisibleToUser);
        if (isVisibleToUser && MainActivity.alphaPermission) {
            if (futureBran.getType().equals(Constant.MenuType.THREE_ONE.getValue())) {
                AlphaMetalFragment.MEASURE = false;
                AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_JKYK_CODE, Constant.ApplyReadFunction.ZH_APP_AM_IMPORT_MEASURE);
            }
            if (futureBran.getType().equals(Constant.MenuType.THREE_FOUR.getValue())) {
                AlphaMetalFragment.EXPORTPROFIT = false;
                AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CKCS_CODE, Constant.ApplyReadFunction.ZH_APP_AM_Export_MEASURE);
            }
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
                if (futureBran.getType().equals(Constant.MenuType.THREE_ONE.getValue())) {
                    AlphaMetalFragment.MEASURE = false;
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_JKYK_CODE, Constant.ApplyReadFunction.ZH_APP_AM_IMPORT_MEASURE);
                }
                if (futureBran.getType().equals(Constant.MenuType.THREE_FOUR.getValue())) {
                    AlphaMetalFragment.EXPORTPROFIT = false;
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CKCS_CODE, Constant.ApplyReadFunction.ZH_APP_AM_Export_MEASURE);
                }
            }
        }, rl, vpMeasure, applyReadView);

    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().AlphametalPageStart(futureBran.getType());
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().AlphametalPageEnd(futureBran.getType());
    }
}
