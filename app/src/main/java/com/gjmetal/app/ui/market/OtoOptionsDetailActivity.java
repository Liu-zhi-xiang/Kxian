package com.gjmetal.app.ui.market;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.OtcOptionState;
import com.gjmetal.app.ui.market.chart.MarketChartActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.BaseWebView;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.app.widget.dialog.SmartChooseDateDialog;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：期权详情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-30 15:02
 */

public class OtoOptionsDetailActivity extends BaseActivity {
    RoomItem futureItem;
    @BindView(R.id.tvContactName)
    TextView tvContactName;
    @BindView(R.id.tvContactDate)
    TextView tvContactDate;
    @BindView(R.id.wvOtc)
    BaseWebView wvOtc;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;

    private OtcOptionState otcOptionState;
    private String optionName;
    private String selectDate;
    private List<String> dateList = new ArrayList<>();
    private List<String> contractIdList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<OtcOptionState> otcOptionStateList = new ArrayList<>();
    private SmartChooseDateDialog smartChooseDateDialog;
    private String defaultDate;//默认日期

    @Override
    protected void initView() {
        setContentView(R.layout.activity_otooption_detail);
        KnifeKit.bind(this);

        futureItem = (RoomItem) getIntent().getSerializableExtra(Constant.MODEL);
        if (ValueUtil.isEmpty(futureItem)) {
            initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, "");
            return;
        }
        wvOtc.setBackGround(ContextCompat.getColor(context,R.color.c2A2D4F));
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, futureItem.getName());

        if (ValueUtil.isStrNotEmpty(futureItem.getOptionCode())) {
            optionName = futureItem.getOptionCode();
        }
        if (ValueUtil.isStrNotEmpty(futureItem.getDefaultDate())) {
            defaultDate = futureItem.getDefaultDate();
        }
        if(ValueUtil.isStrNotEmpty(futureItem.getSelectedDate())){
            selectDate=futureItem.getSelectedDate();
        }
        if (ValueUtil.isListNotEmpty(futureItem.getResult())) {
            for (RoomItem bean : futureItem.getResult()) {
                contractIdList.add(bean.getContractId());
            }
            getOtcOptionsList(contractIdList);
        }
    }

    @Override
    protected void fillData() {
        titleBar.getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketChartActivity.launch(context, futureItem);
            }
        });
    }


    /**
     * 获取期权数据
     *
     * @param contractIdList
     */
    private void getOtcOptionsList(List<String> contractIdList) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().getIsExistContract(contractIdList)
                .compose(XApi.<BaseModel<List<OtcOptionState>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<OtcOptionState>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<OtcOptionState>>>() {
                    @Override
                    public void onNext(BaseModel<List<OtcOptionState>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isEmpty(listBaseModel.getData())) {
                            return;
                        }
                        vEmpty.setVisibility(View.GONE);
                        wvOtc.setVisibility(View.VISIBLE);
                        tvContactDate.setText(selectDate);
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            otcOptionStateList.addAll(listBaseModel.getData());
                            for (OtcOptionState bean : otcOptionStateList) {
                                nameList.add(bean.getContractName());
                                if (ValueUtil.isStrNotEmpty(futureItem.getContractId()) && futureItem.getContractId().equals(bean.getContractId())) {
                                    setFutureItemValue(bean);
                                    getTquotationDates(futureItem.getId(), bean.getContractId(), futureItem.getOptionCode());
                                }
                            }
                            if(ValueUtil.isListNotEmpty(nameList)&&nameList.size()==1){
                                GjUtil.setRightDrawable(context, tvContactName, null);
                            }else {
                                GjUtil.setRightDrawable(context, tvContactName, R.mipmap.ic_chart_down);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getOtcOptionsList(contractIdList);
                            }
                        }, wvOtc);
                    }
                });
    }

    /**
     * 获取时间
     */
    private void getTquotationDates(int menuId, String contractId, String optionType) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().getTquotationDates(menuId, contractId, optionType)
                .compose(XApi.<BaseModel<List<String>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<String>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<String>>>() {
                    @Override
                    public void onNext(BaseModel<List<String>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isEmpty(listBaseModel.getData())) {
                            return;
                        }
                        if(ValueUtil.isListNotEmpty(dateList)){
                            dateList.clear();
                        }
                        dateList.addAll(listBaseModel.getData());
                        if(ValueUtil.isListNotEmpty(dateList)&&dateList.size()==1){
                            GjUtil.setRightDrawable(context, tvContactDate, null);
                        }else {
                            GjUtil.setRightDrawable(context, tvContactDate, R.mipmap.ic_chart_down);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    /**
     * 给Future 赋值
     *
     * @param bean
     */
    private void setFutureItemValue(OtcOptionState bean) {
        otcOptionState = bean;
        showRightIcon(bean);
        tvContactName.setText(bean.getContractName());
        loadWebView(bean, optionName, selectDate, futureItem.getId());
        futureItem.setContractId(ValueUtil.isStrNotEmpty(bean.getContractId()) ? bean.getContractId() : "");
        futureItem.setType(ValueUtil.isStrNotEmpty(bean.getBizType()) ? bean.getBizType() : "");
        futureItem.setIndicatorType(ValueUtil.isStrNotEmpty(bean.getIndicatorType()) ? bean.getIndicatorType() : "");
        futureItem.setName(ValueUtil.isStrNotEmpty(bean.getContractName()) ? bean.getContractName() : "");
        futureItem.setContract(ValueUtil.isStrNotEmpty(bean.getAlias()) ? bean.getAlias() : "");
    }
    private void loadWebView(OtcOptionState bean, String optionName, String expireDate, int menuId) {
        if (ValueUtil.isEmpty(bean)) {
            return;
        }
        String url = Constant.ReqUrl.getOtoOptionsHtmlUrl(bean.getContractId(), optionName, expireDate, menuId);
        XLog.d("期权详情Url:", url);
        wvOtc.loadUrl(url, new BaseWebView.WebviewCallBack() {
            @Override
            public void onPageFinished(WebView view, String url) {

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

            }

            @Override
            public void onLoadResource(WebView view, String ur) {

            }

            @Override
            public void onReceivedError() {

            }

            @Override
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {

            }

            @Override
            public void onHideCustomView() {

            }
        });

    }

    /**
     * 是否显示右边的分时图标记
     *
     * @param optionState
     */
    private void showRightIcon(OtcOptionState optionState) {
        if (ValueUtil.isEmpty(optionState)) {
            return;
        }
        if (optionState.isFlag()) {
            initTitleSyle(Titlebar.TitleSyle.RIGHT_IMAGE, optionState.getContractName());
            titleBar.getRightImage().setBackgroundResource(R.drawable.ic_navbar_chart_selector);
        } else {
            initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, optionState.getContractName());
        }
    }

    public static void launch(Activity context, RoomItem futureItem) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, futureItem);
        Router.newIntent(context)
                .to(OtoOptionsDetailActivity.class)
                .data(bundle)
                .launch();
    }

    @OnClick({R.id.tvContactName, R.id.tvContactDate})
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tvContactDate:
                if (ValueUtil.isListEmpty(dateList)) {
                    return;
                }
                GjUtil.setRightDrawable(context, tvContactDate, R.mipmap.ic_chart_up);
                smartChooseDateDialog = new SmartChooseDateDialog(context, dateList.get(0), dateList.get(dateList.size() - 1), tvContactDate.getText().toString(), new SmartChooseDateDialog.OnMyDialogListener() {
                    @Override
                    public void onback(String year, String month, String day) {
                        String value = year + "/" + month + "/" + day;
                        if (ValueUtil.isListEmpty(dateList)) {
                            return;
                        }
                        boolean has = false;
                        for (String s : dateList) {
                            if (ValueUtil.isStrNotEmpty(s) && s.contains("-") || s.contains("/")) {
                                s = s.replace("-", "/");
                                if (s.equals(value)) {
                                    has = true;
                                    break;
                                }
                            }
                        }
                        if (has) {
                            if (ValueUtil.isStrNotEmpty(value) && ValueUtil.isListNotEmpty(dateList)) {
                                tvContactDate.setText(value);
                                selectDate = value;
                                GjUtil.setRightDrawable(context, tvContactDate, R.mipmap.ic_chart_down);
                                loadWebView(otcOptionState, optionName, selectDate, futureItem.getId());
                            }
                            smartChooseDateDialog.dismiss();
                        } else {
                            ToastUtil.showToast("该日期不可选");
                        }
                    }
                });
                smartChooseDateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        GjUtil.setRightDrawable(context, tvContactDate, R.mipmap.ic_chart_down);
                    }
                });
                smartChooseDateDialog.show();
                break;
            case R.id.tvContactName:
                if (ValueUtil.isListEmpty(nameList)) {
                    return;
                }
                GjUtil.setRightDrawable(context, tvContactName, R.mipmap.ic_chart_up);
                GjUtil.showSingleDialog(context, nameList, tvContactName.getText().toString(), new SingleChooseDialog.OnDialogClickListener() {
                    @Override
                    public void dialogClick(Dialog dialog, View v, String value, int position) {
                        if (ValueUtil.isStrNotEmpty(value) && ValueUtil.isListNotEmpty(nameList)) {
                            tvContactName.setText(value);
                            GjUtil.setRightDrawable(context, tvContactName, R.mipmap.ic_chart_down);
                            otcOptionState = otcOptionStateList.get(position);
                            selectDate = defaultDate;
                            tvContactDate.setText(selectDate);//切换合约时，用默认日期请求
                            setFutureItemValue(otcOptionState);
                            getTquotationDates(futureItem.getId(), otcOptionState.getContractId(), futureItem.getOptionCode());
                        }

                    }

                    @Override
                    public void onDismiss() {
                        GjUtil.setRightDrawable(context, tvContactName, R.mipmap.ic_chart_down);
                    }
                });
                break;
        }
    }

}
