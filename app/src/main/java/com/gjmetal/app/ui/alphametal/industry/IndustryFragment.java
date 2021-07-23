package com.gjmetal.app.ui.alphametal.industry;

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
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.alphametal.subtraction.SubtractionChildFragment;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Description:产业测算（复用套利测算子界面）
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/6/27  17:40
 */
public class IndustryFragment extends DelayerFragment {
    @BindView(R.id.vPermission)
    ApplyReadView applyReadView;
    @BindView(R.id.favMagicIndicator)
    MagicIndicator favMagicIndicator;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.vpMeasure)
    ViewPager vpMeasure;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.rlLogoImage)
    View rlLogoImage;
    private ArrayList<String> mNameLists;
    private ArrayList<Fragment> mFragments;
    private MyHelperAdapter mMyHelperAdapter;
    public static final String MENUCODE = "IndustryMeasure";
    private int index;
    private int chilPosition = 0;
    private boolean initeViewsBool = false;
    private Future futureBran;
    private String function=Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE;
    public IndustryFragment() {

    }

    @SuppressLint("ValidFragment")
    public IndustryFragment(int index, Future futureBran) {
        this.index = index;
        this.futureBran = futureBran;

    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_measure;
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

    private void viewShow() {
        initDatas();
        if (rl != null && vpMeasure != null) {
            rl.setVisibility(View.VISIBLE);
            vpMeasure.setVisibility(View.VISIBLE);
        }
        if (vEmpty != null) {
            vEmpty.setVisibility(View.GONE);
        }
        if (applyReadView != null)
            applyReadView.setVisibility(View.GONE);
        AlphaMetalFragment.IndustryMeasure = true;
    }

    private void initDatas() {
        if (initeViewsBool) {
            return;
        }
        mNameLists = new ArrayList<>();
        mFragments = new ArrayList<>();

        if (futureBran!=null&&futureBran.getSubItem()!=null&&futureBran.getSubItem().size()>0){
            for (int x=0;x<futureBran.getSubItem().size();x++){
                SubtractionChildFragment monthChildFragment = new SubtractionChildFragment(x,futureBran.getSubItem().get(x));
                mFragments.add(monthChildFragment);
                mNameLists.add(futureBran.getSubItem().get(x).getName());
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
        vpMeasure.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chilPosition = position;
                if (ValueUtil.isListNotEmpty(futureBran.getSubItem())) {
                    AppAnalytics.getInstance().onEvent(getContext(), "alpha_industrial_" + futureBran.getSubItem().get(position).getRoomCode() + "_acess", "alpha-产业测算-访问量");
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
                });
        initeViewsBool = true;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisibleToUser) {
        super.onFragmentVisibleChange(isVisibleToUser);
        if (isVisibleToUser && MainActivity.alphaPermission) {
            AlphaMetalFragment.IndustryMeasure = false;
            AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE);
        }
        if ( mFragments != null && mFragments.size() > chilPosition) {
            if (isVisibleToUser) {
                mFragments.get(chilPosition).setUserVisibleHint(true);
            }else {
                mFragments.get(chilPosition).setUserVisibleHint(false);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ApplyEvent(ApplyEvent applyEvent) {
        ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
            @Override
            public void onSubscibeDialogCancel() {
                if (applyReadView != null)
                    applyReadView.showPassDueApply(getActivity(),applyReadView, R.color.cD4975C, R.color.cffffff, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "2");
                        }
                    }, rl, vpMeasure, vEmpty);
            }

            @Override
            public void onSubscibeDialogShow() {
                if (applyReadView != null)
                    applyReadView.showApply(getActivity(),R.color.cD4975C, R.color.cffffff, applyReadView, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "1");
                        }
                    }, rl, vpMeasure, vEmpty);
            }

            @Override
            public void onSubscibeYesShow() {
                viewShow();
            }

            @Override
            public void onSubscibeError(NetError error) {
                showAgainLoad(error);
            }

            @Override
            public void onUnknown() {
                if (MainActivity.alphaPermission) {
                    AlphaMetalFragment.IndustryMeasure = false;
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().AlphametalPageStart(futureBran.getType());//产业-停留时间
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().AlphametalPageEnd(futureBran.getType());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }

    private void showAgainLoad(NetError error) {
        if (vpMeasure == null || rl == null || vEmpty == null || applyReadView == null) {
            return;
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                    AlphaMetalFragment.IndustryMeasure = false;
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE);
            }
        }, rl, vpMeasure, applyReadView);

    }
}
