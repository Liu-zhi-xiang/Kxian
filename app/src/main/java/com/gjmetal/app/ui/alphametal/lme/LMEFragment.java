package com.gjmetal.app.ui.alphametal.lme;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MyHelperAdapter;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.alphametal.MetalSubject;
import com.gjmetal.app.util.UpdateViewPagerUntil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.event.BusProvider;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description LME升贴水
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-16 9:52
 */

public class LMEFragment extends BaseFragment {
    @BindView(R.id.favMagicIndicator)
    MagicIndicator favMagicIndicator;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.vpLME)
    ViewPager vpLME;
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

    private List<MetalSubject> mMetalSubjectList;
    private String mMenuCode;
    private MyHelperAdapter mMyHelperAdapter;
    private List<String> mNameLists;
    private List<Fragment> mFragments;
    private UpdateViewPagerUntil mUpdateViewPagerUntil;
    private int mTimers;

    public LMEFragment() {
    }
    @SuppressLint("ValidFragment")
    public LMEFragment(String type, int menusType, String menuCode, List<MetalSubject> metalSubjectList, int timers) {
        this.mMetalSubjectList = metalSubjectList;
        this.mMenuCode = menuCode;
        this.mTimers = timers;
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_lme;
    }

    @Override
    protected void initView() {
        mNameLists = new ArrayList<>();
        mFragments = new ArrayList<>();

        if (ValueUtil.isListEmpty(mMetalSubjectList)) {
            rl.setVisibility(View.GONE);
            favMagicIndicator.setVisibility(View.GONE);
            return;
        }
        rl.setVisibility(View.VISIBLE);
        favMagicIndicator.setVisibility(View.VISIBLE);
        if (ValueUtil.isListEmpty(mMetalSubjectList)) {
            return;
        }
        for (int i = 0; i < mMetalSubjectList.size(); i++) {
            LMEChildFragment lmeChildFragment = new LMEChildFragment(i, mMenuCode, mMetalSubjectList.get(i),
                    mMetalSubjectList, mTimers);
            mFragments.add(lmeChildFragment);
            mNameLists.add(mMetalSubjectList.get(i).getMetalName());
        }
        initTabLayout();
    }


    private void initTabLayout() {
        vpLME.setOffscreenPageLimit(mFragments.size());
        // 更新标题栏
        mMyHelperAdapter = new MyHelperAdapter(getContext(), getChildFragmentManager(), mFragments);

        vpLME.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseEvent baseEvent = new BaseEvent();
                baseEvent.setClickPostion(true);
                baseEvent.setLmePostion(position);
                BusProvider.getBus().post(baseEvent);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpLME.setAdapter(mMyHelperAdapter);
        mUpdateViewPagerUntil = new UpdateViewPagerUntil();
        UpdateViewPagerUntil.initView(getActivity(), vpLME,
                favMagicIndicator, mNameLists, R.color.c2A2D4F,
                R.color.c9EB2CD, R.color.cFFFFFF, R.color.cD4975C, 16, new BaseCallBack() {
                    @Override
                    public void back(Object obj) {

                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("alpha_LME_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageEnd("alpha_LME_time");
    }
}
