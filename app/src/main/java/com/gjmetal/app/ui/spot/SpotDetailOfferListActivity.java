package com.gjmetal.app.ui.spot;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotPriceTitle;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.UpdateViewPagerUntil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.FragmentPagerAdapter;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：现货详情走势价格列表
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-26 11:16
 */
public class SpotDetailOfferListActivity extends BaseActivity {
    @BindView(R.id.favMagicIndicator)
    MagicIndicator favMagicIndicator;
    @BindView(R.id.vpSpotDetail)
    ViewPager vpSpotDetail;
    private Spot.PListBean mLiatData;
    private ArrayList<Fragment> mFragments;
    private List<String> mTitleName = new ArrayList<>();
    private List<SpotPriceTitle> spotPriceTitleList = new ArrayList<>();
    private String dayKey = "DAY";//日报价

    @Override
    protected void initView() {
        setContentView(R.layout.activity_spot_more_detail);
        KnifeKit.bind(this);
    }

    @Override
    protected void fillData() {
        mLiatData = (Spot.PListBean) getIntent().getExtras().getSerializable(Constant.MODEL);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, mLiatData.getName() == null ? "" : mLiatData.getName());
        mFragments = new ArrayList<>();
        getFutures(mLiatData.getCode(), mLiatData.getLcfgId());
    }

    public static void launch(Activity context, Spot.PListBean listData) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, listData);
            Router.newIntent(context)
                    .to(SpotDetailOfferListActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    private void initDatas() {
        for (int i = 0; i < spotPriceTitleList.size(); i++) {
            String indexKey = null;
            if (ValueUtil.isStrNotEmpty(spotPriceTitleList.get(i).getItemKey())) {
                indexKey = spotPriceTitleList.get(i).getItemKey();
            }
            if (ValueUtil.isStrNotEmpty(indexKey) && indexKey.equals(dayKey)) {
                SpotDetailToDayFragment spotDetailToDayFragment = new SpotDetailToDayFragment(i, spotPriceTitleList.get(i), mLiatData);
                mFragments.add(spotDetailToDayFragment);
            } else {
                SpotDetailWeekFragment spotDetailWeekFragment = new SpotDetailWeekFragment(i, spotPriceTitleList.get(i), mLiatData);
                mFragments.add(spotDetailWeekFragment);
            }
            mTitleName.add(spotPriceTitleList.get(i).getItemName());
        }
        vpSpotDetail.setOffscreenPageLimit(mFragments.size());

        vpSpotDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (ValueUtil.isListNotEmpty(spotPriceTitleList)) {
                    AppAnalytics.getInstance().onEvent(context, "spot_" + spotPriceTitleList.get(position).getItemKey() + "_click", "现货-各报价类型的点击量");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 更新标题栏
        vpSpotDetail.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        UpdateViewPagerUntil.initView(context, vpSpotDetail, favMagicIndicator, mTitleName, R.color.c2A2D4F,
                R.color.c9EB2CD, R.color.cFFFFFF, R.color.cD4975C, 16, new BaseCallBack() {
                    @Override
                    public void back(Object obj) {

                    }
                });
    }

    private void getFutures(String code, String lcfgId) {
        DialogUtil.waitDialog(context);
        Api.getSpotService().querySpotChartTitle(code, lcfgId)
                .compose(XApi.<BaseModel<List<SpotPriceTitle>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<SpotPriceTitle>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<SpotPriceTitle>>>() {
                    @Override
                    public void onNext(BaseModel<List<SpotPriceTitle>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            spotPriceTitleList.addAll(listBaseModel.getData());
                            initDatas();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();

                    }
                });
    }

}
