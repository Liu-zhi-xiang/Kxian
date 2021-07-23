package com.gjmetal.app.ui.alphametal.subtraction;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MonthTapeGridViewAdapterTwo;
import com.gjmetal.app.adapter.alphametal.MonthTapeNextGridViewAdapterTwo;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.manager.TimerManager;
import com.gjmetal.app.model.alphametal.MonthTape;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.model.market.Tape;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyGridView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 *  Description: 产业测算 盘口
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/11/5  13:49
 *
 */

public class IndustryTapeActivity extends BaseActivity {
    @BindView(R.id.gvTop)
    MyGridView gvTop;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.vTop)
    View vTop;
    @BindView(R.id.vBottom)
    View vBottom;
    @BindView(R.id.gvBottom)
    MyGridView gvBottom;
    @BindView(R.id.vNextTop)
    View vNextTop;
    @BindView(R.id.vNextBottom)
    View vNextBottom;
    private String mMenuCode;
    private Specific bean;
    private MonthTapeGridViewAdapterTwo tapeGridViewAdapter;
    private MonthTapeNextGridViewAdapterTwo monthTapeNextGridViewAdapter;
    private List<Tape> topList;
    private List<Tape> bottomList;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_measure_tape);
        KnifeKit.bind(this);
    }

    @Override
    protected void fillData() {
        bean = (Specific) getIntent().getSerializableExtra(Constant.MODEL);
        mMenuCode =getIntent().getStringExtra(Constant.INFO);
        if (ValueUtil.isEmpty(bean)) {
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, ValueUtil.isStrNotEmpty(bean.getName()) ? bean.getName() + "盘口" : "");
        topList = new ArrayList<>();
        bottomList = new ArrayList<>();
        vTop.setVisibility(View.VISIBLE);
        vBottom.setVisibility(View.VISIBLE);
        tapeGridViewAdapter = new MonthTapeGridViewAdapterTwo(context, topList);
        monthTapeNextGridViewAdapter = new MonthTapeNextGridViewAdapterTwo(context, bottomList, bean,mMenuCode);
        gvTop.setAdapter(tapeGridViewAdapter);
        gvBottom.setAdapter(monthTapeNextGridViewAdapter);
        getPositionQuotation(true, bean.getContract());
    }

    private void getPositionQuotation(final boolean firstLoad, String contract) {
        if (firstLoad) {
            DialogUtil.waitDialog(context);
        }
        Api.getAlphaMetalService().getMonthQuotation(contract)
                .compose(XApi.<BaseModel<MonthTape>>getApiTransformer())
                .compose(XApi.<BaseModel<MonthTape>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<MonthTape>>() {
                    @Override
                    public void onNext(BaseModel<MonthTape> listBaseModel) {
                        if (firstLoad) {
                            DialogUtil.dismissDialog();
                        }
                        startTimer();
                        if (vEmpty == null||gvTop==null||gvBottom==null) {
                            return;
                        }
                        if (ValueUtil.isEmpty(listBaseModel.getData()) && ValueUtil.isListEmpty(listBaseModel.getData().getBottom()) && ValueUtil.isListEmpty(listBaseModel.getData().getTop())) {
                            vBottom.setVisibility(View.GONE);
                            vTop.setVisibility(View.GONE);
                            vNextBottom.setVisibility(View.GONE);
                            vNextTop.setVisibility(View.GONE);
                            gvTop.setVisibility(View.GONE);
                            gvBottom.setVisibility(View.GONE);
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.BLUE);
                            return;
                        }
                        if(ValueUtil.isListNotEmpty(listBaseModel.getData().getBottom())&& ValueUtil.isListEmpty(listBaseModel.getData().getTop())){//上面的空，下面的不空
                            vBottom.setVisibility(View.GONE);
                            vTop.setVisibility(View.GONE);
                            vNextBottom.setVisibility(View.VISIBLE);
                            vNextTop.setVisibility(View.VISIBLE);
                            gvTop.setVisibility(View.GONE);
                            gvBottom.setVisibility(View.VISIBLE);
                            vEmpty.setVisibility(View.GONE);
                        }else if(ValueUtil.isListEmpty(listBaseModel.getData().getBottom())&& ValueUtil.isListNotEmpty(listBaseModel.getData().getTop())){//上面的不为空，下面的为空
                            vBottom.setVisibility(View.VISIBLE);
                            vTop.setVisibility(View.VISIBLE);
                            vNextBottom.setVisibility(View.GONE);
                            vNextTop.setVisibility(View.GONE);
                            gvTop.setVisibility(View.VISIBLE);
                            gvBottom.setVisibility(View.GONE);
                            vEmpty.setVisibility(View.GONE);
                        }
                        updateUI(listBaseModel.getData().getTop(), listBaseModel.getData().getBottom());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (firstLoad) {
                            DialogUtil.dismissDialog();
                        }
                        TimerManager.getInstance().closeTimer();
                        if (gvTop == null) {
                            return;
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getPositionQuotation(true, bean.getContract());
                            }
                        }, gvTop, vBottom, vTop, vNextTop, gvBottom, vNextBottom);

                    }
                });
    }

    private void updateUI(List<Tape> mTopList, List<Tape> mBottomList) {
        if (ValueUtil.isListNotEmpty(topList)) {
            topList.clear();
        }
        if (ValueUtil.isListNotEmpty(mTopList)) {
            topList.addAll(mTopList);
            if (ValueUtil.isListNotEmpty(topList)) {
                if (topList.size() % 2 != 0) {
                    topList.add(new Tape("", "",true));
                }
            }
        }
        if (ValueUtil.isListNotEmpty(bottomList)) {
            bottomList.clear();
        }
        if (ValueUtil.isListNotEmpty(mBottomList)) {
            bottomList.addAll(mBottomList);
            if (ValueUtil.isListNotEmpty(bottomList)) {
                if (bottomList.size() % 2 != 0) {
                    bottomList.add(new Tape("", "",true));
                }
            }
        }
        for (Tape bean : topList) {
            if (ValueUtil.isStrNotEmpty(bean.getKey()) && bean.getKey().equals("涨跌")) {
                String value = null;
                if (ValueUtil.isNotEmpty(bean.getValue())) {
                    value = bean.getValue().substring(0, bean.getValue().indexOf("/"));
                }
                tapeGridViewAdapter.upOrDown(GjUtil.lastUpOrDown(context, value));
                break;
            }
        }
        String downUpLeft = null;
        String downUpRight = null;
        for (int i = 0; i < bottomList.size(); i++) {
            Tape bean = bottomList.get(i);
            if (ValueUtil.isStrNotEmpty(bean.getKey()) && bean.getKey().equals("涨跌")) {
                if (i == 12) {
                    if (ValueUtil.isStrNotEmpty(bean.getValue())) {
                        downUpLeft = bean.getValue().substring(0, bean.getValue().indexOf("/"));
                    }
                } else if (i == 13) {
                    if (ValueUtil.isStrNotEmpty(bean.getValue())) {
                        downUpRight = bean.getValue().substring(0, bean.getValue().indexOf("/"));
                    }
                }
            }
        }
        monthTapeNextGridViewAdapter.upOrDown(GjUtil.lastUpOrDown(context, downUpLeft), GjUtil.lastUpOrDown(context, downUpRight));
        tapeGridViewAdapter.notifyDataSetChanged();
        monthTapeNextGridViewAdapter.notifyDataSetChanged();
    }
    private void startTimer(){
        TimerManager.getInstance().startTimer(1, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                getPositionQuotation(false, bean.getContract());
            }
        });
    }

    public static void launch(Activity context, Specific bean,String mMenuCode) {
        GjUtil.closeMarketTimer();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, bean);
        bundle.putString(Constant.INFO, mMenuCode);
        Router.newIntent(context)
                .to(IndustryTapeActivity.class)
                .data(bundle)
                .launch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerManager.getInstance().closeTimer();
    }
}
