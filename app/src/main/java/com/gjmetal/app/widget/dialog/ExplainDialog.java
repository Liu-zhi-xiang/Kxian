package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.star.kchart.utils.DensityUtil;
import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.ExplainDialogAdapter;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.Explain;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：
 * Author: chenshanshan
 * Email: 1175558532@qq.com
 * Date: 2018-10-15  10:32
 */
public class ExplainDialog extends Dialog {
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.rvChoosemh)
    RecyclerView rvChoosemh;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private ExplainDialogAdapter explainDialogAdapter;
    private Context context;

    private String contract;
    private String type;

    private List<Explain> mExplainLists;

    public ExplainDialog(@NonNull Context context) {
        super(context);
    }

    public ExplainDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_explain, null);
        setContentView(view);
        KnifeKit.bind(this, view);
        setRvChoosemh();
    }

    //初始化配置
    @SuppressWarnings("unchecked")
    public void setRvChoosemh() {
        mExplainLists = new ArrayList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChoosemh.setLayoutManager(linearLayoutManager);
        explainDialogAdapter = new ExplainDialogAdapter(getContext());
        rvChoosemh.setAdapter(explainDialogAdapter);
    }

    /**
     * 进口测算 说明
     *
     * @param contract 合约名
     */
    private void initMeasureChartExplain(final String contract) {
        Api.getAlphaMetalService().getMeasureInstruction(contract)
                .compose(XApi.<BaseModel<List<Explain>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Explain>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Explain>>>() {
                    @Override
                    public void onNext(BaseModel<List<Explain>> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            List<Explain> explainList = listBaseModel.getData();
                            if (ValueUtil.isListNotEmpty(explainList)) {
                                rvChoosemh.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                explainDialogAdapter.setData(explainList);
                            } else {
                                showAgainLoad(null);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showAgainLoad(error);
                    }
                });
    }

    /**
     * @param contract//利率的说明
     */
    private void initExchangeChartExplain(String contract) {
        Api.getMarketService().getRateInstruction(contract)
                .compose(XApi.<BaseModel<List<Explain>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Explain>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Explain>>>() {
                    @Override
                    public void onNext(BaseModel<List<Explain>> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            List<Explain> explainList = listBaseModel.getData();
                            if (ValueUtil.isListNotEmpty(explainList)) {
                                rvChoosemh.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                explainDialogAdapter.setData(explainList);
                            } else {
                                showAgainLoad(null);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showAgainLoad(error);
                    }
                });
    }

    /**
     * @param contract
     */
    private void initMarketChartExplain(String contract) {
        Api.getMarketService().getInstruction(contract)
                .compose(XApi.<BaseModel<List<Explain>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Explain>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Explain>>>() {
                    @Override
                    public void onNext(BaseModel<List<Explain>> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            List<Explain> explainList = listBaseModel.getData();
                            if (ValueUtil.isListNotEmpty(explainList)) {
                                rvChoosemh.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                explainDialogAdapter.setData(explainList);
                            } else {
                                showAgainLoad(null);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showAgainLoad(error);
                    }
                });
    }

    /**
     * @param contract
     */
    private void monthChartActivity(String contract) {
        Api.getAlphaMetalService().getInstruction(contract)
                .compose(XApi.<BaseModel<List<Explain>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Explain>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Explain>>>() {
                    @Override
                    public void onNext(BaseModel<List<Explain>> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            List<Explain> explainList = listBaseModel.getData();
                            if (ValueUtil.isListNotEmpty(explainList)) {
                                rvChoosemh.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                explainDialogAdapter.setData(explainList);
                            } else {
                                showAgainLoad(null);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showAgainLoad(error);
                    }
                });
    }

    private void showAgainLoad(NetError error) {
        GjUtil.showEmptyHint(context, Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (rvChoosemh != null) {
                    setContract(contract, type);
                }
            }
        }, rvChoosemh);

    }

    public void setContract(String contract, String type) {
        this.contract = contract;
        this.type = type;
        if (type.equals("MeasureChartActivity")) {
            initMeasureChartExplain(contract); //进口测算 说明
        } else if (type.equals("ExchangeChartActivity")) {
            initExchangeChartExplain(contract);
        } else if (type.equals("MarketChartActivity")) {
            initMarketChartExplain(contract);
        } else if (type.equals("SubtractionChartActivity")) {
            monthChartActivity(contract);
        }

    }

    @Override
    public void onDetachedFromWindow() {
        if (isShowing())
            dismiss();
        super.onDetachedFromWindow();
    }

    @Override
    public void show() {
        super.show();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point point=new Point();
        d.getSize(point);
        p.width = (point.x - DensityUtil.dp2px(30)); //设置dialog的宽度为当前手机屏幕的宽度
        p.height =point.y - DensityUtil.dp2px(120);
        getWindow().setAttributes(p);
    }

    @OnClick(R.id.ivClose)
    public void onViewClicked() {
        dismiss();
    }
}
