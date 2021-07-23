package com.gjmetal.app.ui.alphametal.lme;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.alphametal.C3TModel;
import com.gjmetal.app.model.alphametal.LmeModel;
import com.gjmetal.app.model.alphametal.MetalSubject;
import com.gjmetal.app.model.market.kline.Lem;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.star.kchart.comInterface.ILem;
import com.star.kchart.lemview.ChildView;
import com.star.kchart.lemview.GroupView;
import com.star.kchart.lemview.MainView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description：LME子界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-17 20:04
 */
public class LMEChildFragment extends BaseFragment {
    @BindView(R.id.rbDayComparison)
    RadioButton rbDayComparison;
    @BindView(R.id.rbWeekComparison)
    RadioButton rbWeekComparison;
    @BindView(R.id.rbTwoWeekComparison)
    RadioButton rbTwoWeekComparison;
    @BindView(R.id.rgTab)
    RadioGroup rgTab;
    @BindView(R.id.tvLmeTitle)
    TextView tvLmeTitle;
    @BindView(R.id.tvSwapsDetails)
    TextView tvSwapsDetails;
    @BindView(R.id.tvLmeTime)
    TextView tvLmeTime;
    @BindView(R.id.tvCash)
    AutofitTextView tvCash;
    @BindView(R.id.tv3M)
    AutofitTextView tv3M;
    @BindView(R.id.tvTom)
    AutofitTextView tvTom;
    @BindView(R.id.lme_view)
    GroupView lmeView;
    @BindView(R.id.lmeEmpty)
    EmptyView lmeEmpty;
    @BindView(R.id.tvCash3m)
    TextView tvCash3m;
    @BindView(R.id.tvCashValue)
    TextView tvCashValue;
    @BindView(R.id.tvCashSmall)
    TextView tvCashSmall;
    @BindView(R.id.ivCashBigUp)
    ImageView ivCashBigUp;
    @BindView(R.id.ivCashBigDown)
    ImageView ivCashBigDown;
    @BindView(R.id.tvCashName)
    TextView tvCashName;
    @BindView(R.id.txtNewPrice)
    TextView txtNewPrice;
    @BindView(R.id.tvNewPriceValueLeft)
    TextView tvNewPriceValueLeft;
    @BindView(R.id.tvNewPriceValueRight)
    TextView tvNewPriceValueRight;
    @BindView(R.id.ivNewPriceSmallUp)
    ImageView ivNewPriceSmallUp;
    @BindView(R.id.ivNewPriceSmallDown)
    ImageView ivNewPriceSmallDown;
    @BindView(R.id.llNewPriceRight)
    LinearLayout llNewPriceRight;
    @BindView(R.id.vNewOne)
    View vNewOne;
    @BindView(R.id.txtYesterdayClosePrice)
    TextView txtYesterdayClosePrice;
    @BindView(R.id.tvClosePriceValue)
    TextView tvClosePriceValue;
    @BindView(R.id.txtLmeBuy)
    TextView txtLmeBuy;
    @BindView(R.id.tvLmeBuyValueLeft)
    TextView tvLmeBuyValueLeft;
    @BindView(R.id.tvLmeBuyValueRight)
    TextView tvLmeBuyValueRight;
    @BindView(R.id.vLmeTwo)
    View vLmeTwo;
    @BindView(R.id.txtLmeSell)
    TextView txtLmeSell;
    @BindView(R.id.tvLmeSellValueLeft)
    TextView tvLmeSellValueLeft;
    @BindView(R.id.tvLmeSellValueRight)
    TextView tvLmeSellValueRight;

    List<Lem> mLemModels;
    private MetalSubject mMetalSubject;
    private String metalCode;
    private String metalName;
    private String companyDay = "0";
    private boolean isFlishDialog = true;
    private CountDownTimer mCountMinuteDownTimer = null;

    @Override
    protected int setRootView() {
        return R.layout.fragment_lme_child;
    }

    @SuppressLint("ValidFragment")
    public LMEChildFragment(int index, String type, MetalSubject metalSubject, List<MetalSubject> mMetalSubjectList, int timers) {
        this.mMetalSubject = metalSubject;
        if (ValueUtil.isNotEmpty(metalSubject)) {
            metalCode = metalSubject.getMetalCode();
            metalName = mMetalSubject.getMetalName();
        }
    }

    public LMEChildFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    public void initView() {
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbDayComparison:
                        companyDay = "0";
                        isFlishDialog = true;
                        setTextAddDrawView("日变动量", "LME月间差价(对前一日变化)");

                        break;
                    case R.id.rbWeekComparison:
                        companyDay = "1";
                        isFlishDialog = true;
                        setTextAddDrawView("一周变动量", "LME月间差价(对前一周变化)");

                        break;
                    case R.id.rbTwoWeekComparison:
                        companyDay = "2";
                        isFlishDialog = true;
                        setTextAddDrawView("两周变动量", "LME月间差价(对前两周变化)");

                        break;
                    default:
                        break;

                }
            }
        });

        initC3TData(metalCode);
        setTextAddDrawView("变动量", "LME基差");

        ((ChildView) lmeView.getChildDraw()).setOnClickPointListener(new ChildView.ClickLmePointListener() {

            @Override
            public void onClickPointListener(int postion, ILem iLem) {
                if (ValueUtil.isEmpty(iLem) || postion == -1) {
                    setNoDataType();
                    return;
                }
                tvCash3m.setText(iLem.getDate());
                if (iLem.getVolume() > 0) {
                    tvCashValue.setTextColor(ContextCompat.getColor(getContext(),R.color.cFF5252));
                    tvCashSmall.setTextColor(ContextCompat.getColor(getContext(),R.color.cFF5252));
                    ivCashBigUp.setVisibility(View.VISIBLE);
                    ivCashBigDown.setVisibility(View.GONE);
                } else if (iLem.getVolume() == 0) {
                    tvCashValue.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                    tvCashSmall.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                    ivCashBigUp.setVisibility(View.GONE);
                    ivCashBigDown.setVisibility(View.GONE);
                } else {
                    tvCashValue.setTextColor(ContextCompat.getColor(getContext(),R.color.c35CB6B));
                    tvCashSmall.setTextColor(ContextCompat.getColor(getContext(),R.color.c35CB6B));
                    ivCashBigUp.setVisibility(View.GONE);
                    ivCashBigDown.setVisibility(View.VISIBLE);
                }
                tvCashValue.setText(iLem.getCurve() + "");
                tvCashSmall.setText(iLem.getVolume() + "");


                tvCashName.setText(iLem.getOrAlias());
                if (ValueUtil.isStrNotEmpty(iLem.getOrPriceDiff()+"") && !(iLem.getOrPriceDiff()+"").equals("-")) {
                    if (Double.valueOf(iLem.getOrPriceDiff()) > 0) {
                        tvNewPriceValueLeft.setTextColor(ContextCompat.getColor(getContext(),R.color.cFF5252));
                        tvNewPriceValueRight.setTextColor(ContextCompat.getColor(getContext(),R.color.cFF5252));
                        ivNewPriceSmallUp.setVisibility(View.VISIBLE);
                        ivNewPriceSmallDown.setVisibility(View.GONE);
                    } else if (Double.valueOf(iLem.getOrPriceDiff()) == 0) {
                        tvNewPriceValueLeft.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                        tvNewPriceValueRight.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                        ivNewPriceSmallUp.setVisibility(View.GONE);
                        ivNewPriceSmallDown.setVisibility(View.GONE);
                    } else {
                        tvNewPriceValueLeft.setTextColor(ContextCompat.getColor(getContext(),R.color.c35CB6B));
                        tvNewPriceValueRight.setTextColor(ContextCompat.getColor(getContext(),R.color.c35CB6B));
                        ivNewPriceSmallUp.setVisibility(View.GONE);
                        ivNewPriceSmallDown.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvNewPriceValueLeft.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                    tvNewPriceValueRight.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                    ivNewPriceSmallUp.setVisibility(View.GONE);
                    ivNewPriceSmallDown.setVisibility(View.GONE);
                }

                tvNewPriceValueRight.setText((ValueUtil.isNotEmpty(iLem.getOrPriceDiff()+"") && !(iLem.getOrPriceDiff()+"").equals("-")) ? (iLem.getOrPriceDiff()+"") : "- -");

                String bidStr = ValueUtil.isStrEmpty(iLem.getBidTime()) ? "--:--" : iLem.getBidTime();
                txtLmeBuy.setText("报买(" + bidStr + ")");
                if (ValueUtil.isEmpty(iLem.getBid())) {
                    tvLmeBuyValueLeft.setText("- -");
                } else {
                    tvLmeBuyValueLeft.setText(iLem.getBid());
                }
                if (ValueUtil.isEmpty(iLem.getBidSize())) {
                    tvLmeBuyValueRight.setText("- -");
                } else {
                    tvLmeBuyValueRight.setText(iLem.getBidSize() + "");
                }
                String askStr = ValueUtil.isStrEmpty(iLem.getAskTime()) ? "--:--" : iLem.getAskTime();
                txtLmeSell.setText("报卖(" + askStr + ")");
                if (ValueUtil.isStrEmpty(iLem.getAsk())) {
                    tvLmeSellValueLeft.setText("- -");
                } else {
                    tvLmeSellValueLeft.setText(iLem.getAsk());
                }
                if (ValueUtil.isStrEmpty(iLem.getAskSize())) {
                    tvLmeSellValueRight.setText("- -");
                } else {
                    tvLmeSellValueRight.setText(iLem.getAskSize());
                }
            }
        });

    }
    private void setTextAddDrawView(String str1, String str2) {
        ((MainView) lmeView.getMainDraw()).setDateStr(str1);
        ((MainView) lmeView.getMainDraw()).setSelectedIndex(0);
        ((ChildView) lmeView.getChildDraw()).setSelectedIndex(0);
        initLmeView(metalCode, companyDay);
        tvLmeTitle.setText(metalName + str2);
    }

    //C3T 数据接口
    private void initC3TData(String name) {
        Api.getAlphaMetalService().getC3T(name)
                .compose(XApi.<BaseModel<List<C3TModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<C3TModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<C3TModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<C3TModel>> listBaseModel) {
                        List<C3TModel> c3TModels = listBaseModel.getData();
                        if (ValueUtil.isListEmpty(c3TModels)) {
                            tvCash.setText("- -");
                            tv3M.setText("- -");
                            tvTom.setText("- -");
                            return;
                        }
                        if (c3TModels.get(0).getTradeTime() != null) {
                            tvLmeTime.setText("当前交易日:" + DateUtil.getStringDateByLong(c3TModels.get(0).getTradeTime(), 2));
                        }
                        for (int i = 0; i < c3TModels.size(); i++) {
                            if (i == 0) {
                                if (ValueUtil.isEmpty(c3TModels.get(i))) {
                                    tvCash.setText("- -");
                                } else {
                                    tvCash.setText(c3TModels.get(i).getName() + ":" + c3TModels.get(i).getValue());
                                }

                            } else if (i == 1) {
                                if (ValueUtil.isEmpty(c3TModels.get(i))) {
                                    tv3M.setText("- -");
                                } else {
                                    tv3M.setText(c3TModels.get(i).getName() + ":" + c3TModels.get(i).getValue());
                                }

                            } else if (i == 2) {
                                if (ValueUtil.isEmpty(c3TModels.get(i))) {
                                    tvTom.setText("- -");
                                } else {
                                    tvTom.setText(c3TModels.get(i).getName() + ":" + c3TModels.get(i).getValue());
                                }

                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        tvCash.setText("- -");
                        tv3M.setText("- -");
                        tvTom.setText("- -");
                    }
                });

    }


    //LME 图数据接口
    private void initLmeView(String name, String type) {
        mLemModels = new ArrayList<>();
        if (isFlishDialog) {
            DialogUtil.waitDialog(getActivity());
        }
        Api.getAlphaMetalService().getRtLMEVoList(name, type)
                .compose(XApi.<BaseModel<List<LmeModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<LmeModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<LmeModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<LmeModel>> listBaseModel) {
                        lmeEmpty.setVisibility(View.GONE);
                        lmeView.setVisibility(View.VISIBLE);

                        if (isFlishDialog) {
                            DialogUtil.dismissDialog();
                            isFlishDialog = false;
                        }
                        mLemModels.clear();
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            lmeView.initData(mLemModels);
                            return;
                        }
                        List<LmeModel> lmeModels = listBaseModel.getData();
                        for (int i = 0; i < lmeModels.size(); i++) {
                            Lem lem = new Lem();
                            LmeModel lmeModel = lmeModels.get(i);
                            if (ValueUtil.isEmpty(lmeModel)) {
                                continue;
                            }
                            lem.alias = lmeModel.getAlias();
                            lem.last = lmeModel.getLast();
                            lem.bid = lmeModel.getBid();
                            lem.bidSize = lmeModel.getBidSize();
                            lem.bidTime = lmeModel.getBidTime();
                            lem.ask = lmeModel.getAsk();
                            lem.askSize = lmeModel.getAskSize();
                            lem.askTime = lmeModel.getAskTime();
                            lem.preClose = lmeModel.getPreClose();
                            lem.priceDiff = lmeModel.getPriceDiff();

                            lem.absLast = lmeModel.getAbsLast();
                            lem.absAlias = lmeModel.getAbsAlias();
                            lem.absPriceDiff = lmeModel.getAbsPriceDiff();
                            lem.absPreClose = lmeModel.getAbsPreClose();

                            mLemModels.add(lem);

                        }
                        if (ValueUtil.isListEmpty(mLemModels)) {
                            lmeEmpty.setVisibility(View.VISIBLE);
                            lmeView.setVisibility(View.GONE);

                        } else {
                            lmeView.initData(mLemModels);

                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        setNoDataType();
                        DialogUtil.dismissDialog();
                        isFlishDialog = true;
                        mLemModels.clear();
                        showAgainLoad(error);
                        lmeView.initData(mLemModels);
                    }
                });
    }

    private void showAgainLoad(NetError error) {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        if (lmeEmpty == null) {
            return;
        }
        if (ValueUtil.isListEmpty(mLemModels)) {
            GjUtil.showEmptyHint(getActivity(),Constant.BgColor.BLUE, error, lmeEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    initLmeView(metalCode, companyDay);
                }
            }, lmeView);
        } else {
            lmeEmpty.setVisibility(View.GONE);
            lmeView.setVisibility(View.VISIBLE);
        }
    }

    //加载失败或无数据时展示
    private void setNoDataType() {
        tvCashValue.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
        tvCashSmall.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
        tvNewPriceValueLeft.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
        tvNewPriceValueRight.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));

        tvCash3m.setText("- -");
        tvCashValue.setText("- -");
        tvCashSmall.setText("- -");
        tvCashName.setText("- -");

        tvClosePriceValue.setText("- -");
        txtLmeBuy.setText("报买(--:--)");
        txtLmeSell.setText("报卖(--:--)");
        tvLmeBuyValueLeft.setText("- -");
        tvLmeBuyValueRight.setText("- -");
        tvLmeSellValueLeft.setText("- -");
        tvLmeSellValueRight.setText("- -");
        tvNewPriceValueLeft.setText("- -");
        tvNewPriceValueRight.setText("- -");

        ivNewPriceSmallUp.setVisibility(View.GONE);
        ivNewPriceSmallDown.setVisibility(View.GONE);
        ivCashBigUp.setVisibility(View.GONE);
        ivCashBigDown.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        BusProvider.getBus().unregister(this);
        super.onDestroyView();

    }
    @OnClick(R.id.tvSwapsDetails)
    public void onViewClicked() {
        SwapDetailActivity.launch(getActivity(), metalName, metalCode);
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
    }
}
