package com.gjmetal.app.ui.flash;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.ui.information.FinanceDateFragment;
import com.gjmetal.app.ui.my.MyInformationActivity;
import com.gjmetal.app.widget.ControlScrollViewPager;
import com.gjmetal.app.widget.Titlebar;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Description:日历
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/7/1  13:46
 */
public class FlashAdditionFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    Titlebar titleBar;
    @BindView(R.id.vpFlashs)
    ControlScrollViewPager vpFlashs;
    private  FlashFragment flashFragment;
    private FinanceDateFragment financeDateFragment;//财经日历
    private ArrayList<Fragment> mFragments;
    private int chilPosition=0;

    @Override
    protected int setRootView() {
        return R.layout.fragment_flash_addition;

    }
    public void showNewMsgView(boolean hasNewMsg) {
        if (titleBar != null) {
            titleBar.setRedMsgView(hasNewMsg);
        }
        mHasNewMsg = hasNewMsg;
    }
    @Override
    protected void initView()
    {
//        titleBar.initStyle(Titlebar.TitleSyle.HOME_MENU, getString(R.string.menu_main_flash));
        titleBar.initStyle(Titlebar.TitleSyle.FLASH_GROUP, getString(R.string.menu_main_flash));
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.launch(getActivity());
            }
        });
        if (titleBar != null) {
            titleBar.setRedMsgView(mHasNewMsg);
        }
        titleBar.getRgFlash().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if ( titleBar.getGrFlsahLife().isChecked()){
                    vpFlashs.setCurrentItem(0);
                }else {
                    if (mFragments!=null&&mFragments.size()>1)
                        vpFlashs.setCurrentItem(1);
                }

            }
        });
        mFragments=new ArrayList<>();
        flashFragment=new FlashFragment();
        mFragments.add(flashFragment);
        financeDateFragment=new FinanceDateFragment();
        mFragments.add(financeDateFragment);
        vpFlashs.setOffscreenPageLimit(1);
        vpFlashs.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        });
        vpFlashs.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chilPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (mFragments!=null&&mFragments.size()>chilPosition) {
                mFragments.get(chilPosition).setUserVisibleHint(false);
            }
        } else {
            if (mFragments!=null&&mFragments.size()>chilPosition) {
                mFragments.get(chilPosition).setUserVisibleHint(true);
            }
        }
    }

}
