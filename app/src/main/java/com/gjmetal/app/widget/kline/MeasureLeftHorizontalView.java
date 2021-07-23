package com.gjmetal.app.widget.kline;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MeasureLeftAdapter;
import com.gjmetal.app.model.alphametal.LeftViewModel;
import com.gjmetal.app.model.alphametal.MeassureNewLast;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description：测算k线横屏信息显示
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-10-12 15:32
 */

public class MeasureLeftHorizontalView extends LinearLayout {
    @BindView(R.id.tvLastValue)
    TextView tvLastValue;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvPercentValue)
    TextView tvPercentValue;
    @BindView(R.id.tvLMEValue)
    TextView tvLMEValue;
    @BindView(R.id.tvProfitValue)
    TextView tvProfitValue;
    @BindView(R.id.rvMeasureLeft)
    RecyclerView rvMeasureLeft;
    @BindView(R.id.txtSell)
    TextView txtSell;
    @BindView(R.id.tvSellValue)
    TextView tvSellValue;
    @BindView(R.id.tvSellCount)
    TextView tvSellCount;
    @BindView(R.id.rlSell)
    RelativeLayout rlSell;
    @BindView(R.id.txtBuy)
    TextView txtBuy;
    @BindView(R.id.tvBuyValue)
    TextView tvBuyValue;
    @BindView(R.id.tvBuyCount)
    TextView tvBuyCount;
    @BindView(R.id.rlBuy)
    RelativeLayout rlBuy;
    private MeasureLeftAdapter measureLeftAdapter;
    private List<LeftViewModel> leftViewModelList;
    private Context mContext;
    private MeasureType mMeasureType = MeasureType.LME;

    public MeasureLeftHorizontalView(Context context) {
        super(context);
    }

    public MeasureLeftHorizontalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.view_measure_left_horizontal, this);
        this.mContext = context;
        ButterKnife.bind(view);
    }

    public void fillData(MeassureNewLast datalist, MeasureType measureType) {
        if (datalist==null){
            return;
        }
        this.mMeasureType = measureType;
        switch (mMeasureType) {
            case CROSS_MONTH:
                rvMeasureLeft.setVisibility(View.GONE);
                if (ValueUtil.isNotEmpty(datalist)) {
                    tvSellValue.setText(ValueUtil.isStrEmpty(datalist.getAsk1p()) ? "- -" : datalist.getAsk1p());
                    tvSellCount.setText(ValueUtil.isStrEmpty(datalist.getAsk1v()) ? "- -" : datalist.getAsk1v());
                    tvBuyValue.setText(ValueUtil.isStrEmpty(datalist.getBid1p()) ? "- -" : datalist.getBid1p());
                    tvBuyCount.setText(ValueUtil.isStrEmpty(datalist.getBid1v()) ? "- -" : datalist.getBid1v());
                }
                break;
            case LME:
            case MEASURE_MIN:
            case IMPORT:
                rvMeasureLeft.setVisibility(View.VISIBLE);
                break;
        }
        tvLMEValue.setVisibility(View.GONE);
        tvProfitValue.setVisibility(View.GONE);
        if (ValueUtil.isEmpty(datalist)) {
            return;
        }
        tvPrice.setText(ValueUtil.isStrEmpty(datalist.getUpdown()) ? "- -" : datalist.getUpdown());
        tvPercentValue.setText(ValueUtil.isStrEmpty(datalist.getPercent()) ? "- -" : datalist.getPercent());
        GjUtil.lastUpOrDownChangeColor(mContext, datalist.getUpdown(), tvLastValue,datalist.getLast(),tvPrice, tvPercentValue);//根据涨幅变色

        if (rvMeasureLeft.getVisibility() == View.VISIBLE) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            measureLeftAdapter = new MeasureLeftAdapter(mContext);
            rvMeasureLeft.setLayoutManager(mLayoutManager);
            rvMeasureLeft.setAdapter(measureLeftAdapter);
            leftViewModelList = new ArrayList<>();
            if (datalist.getParams() != null) {
                leftViewModelList.clear();
                for (int i = 0; i < datalist.getParams().size(); i++) {
                    leftViewModelList.add(new LeftViewModel(datalist.getParams().get(i).getKey(), datalist.getParams().get(i).getValue()));
                }
            }else {
                leftViewModelList.add(new LeftViewModel("卖", datalist.getAsk1p()));
                leftViewModelList.add(new LeftViewModel("买", datalist.getBid1p()));
            }
            measureLeftAdapter.setData(leftViewModelList);
        }
    }

    public enum MeasureType {
        MEASURE_MIN,//分时
        LME,//lme
        IMPORT,//进口测算
        CROSS_MONTH//跨月基差
    }
}
