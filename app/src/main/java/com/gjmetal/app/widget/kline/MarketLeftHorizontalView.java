package com.gjmetal.app.widget.kline;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.NewLast;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gjmetal.app.widget.kline.MarketLeftHorizontalView.MarketType.CONTACT;

/**
 * Description：k线横屏信息显示
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-10-12 15:32
 */
public class MarketLeftHorizontalView extends LinearLayout {
    @BindView(R.id.txtSell)
    TextView txtSell;
    @BindView(R.id.tvSellValue)
    TextView tvSellValue;//卖
    @BindView(R.id.tvSellCount)
    TextView tvSellCount;
    @BindView(R.id.rlSell)
    RelativeLayout rlSell;
    @BindView(R.id.txtBuy)
    TextView txtBuy;
    @BindView(R.id.tvBuyValue)
    TextView tvBuyValue;//买
    @BindView(R.id.tvBuyCount)
    TextView tvBuyCount;
    @BindView(R.id.rlBuy)
    RelativeLayout rlBuy;
    @BindView(R.id.txtInventory)
    TextView txtInventory;
    @BindView(R.id.tvInventoryValue)
    TextView tvInventoryValue;//持仓量
    @BindView(R.id.rlInventory)
    RelativeLayout rlInventory;
    @BindView(R.id.txtVolume)
    TextView txtVolume;
    @BindView(R.id.tvVolumeValue)
    TextView tvVolumeValue;//成交量
    @BindView(R.id.rlVolume)
    RelativeLayout rlVolume;
    @BindView(R.id.tvChangeInventoryValue)
    TextView tvChangeInventoryValue;
    @BindView(R.id.tvChangeVolumeValue)
    TextView tvChangeVolumeValue;
    @BindView(R.id.llMarketBottom)
    LinearLayout llMarketBottom;
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
    private MarketType marketType = CONTACT;
    private Context mContext;

    public MarketLeftHorizontalView(Context context) {
        super(context);
    }

    public MarketLeftHorizontalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_top_left_horizontal, this);
        ButterKnife.bind(this);
        mContext = context;
    }

    public void fillData(NewLast datalist, MarketType marketType) {
        this.marketType = marketType;
        switch (marketType) {
            case IRATE:
                tvLMEValue.setVisibility(View.GONE);
                tvProfitValue.setVisibility(View.GONE);
                llMarketBottom.setVisibility(View.GONE);
                break;
            case CONTACT:
                tvLMEValue.setVisibility(View.VISIBLE);
                tvProfitValue.setVisibility(View.VISIBLE);
                llMarketBottom.setVisibility(View.VISIBLE);
                break;
        }

        if (ValueUtil.isEmpty(datalist)) {
            return;
        }
        tvPrice.setText(ValueUtil.isStrEmpty(datalist.getUpdown()) ? "- -" : datalist.getUpdown());
        tvPercentValue.setText(ValueUtil.isStrEmpty(datalist.getPercent()) ? "- -" : datalist.getPercent());
        GjUtil.lastUpOrDownChangeColor(mContext, datalist.getUpdown(), tvLastValue,datalist.getLast(),tvPrice, tvPercentValue);//根据涨幅变色
        if (ValueUtil.isListNotEmpty(datalist.getMeasures())&&datalist.getMeasures().size() > 1) {
            tvLMEValue.setVisibility(View.VISIBLE);
            tvProfitValue.setVisibility(View.VISIBLE);
            tvLMEValue.setText(datalist.getMeasures().get(0).getKey() + ":" + datalist.getMeasures().get(0).getValue());
            tvProfitValue.setText(datalist.getMeasures().get(1).getKey() + ":" + datalist.getMeasures().get(1).getValue());
        }else {
            tvLMEValue.setVisibility(GONE);
            tvProfitValue.setVisibility(GONE);
        }
        tvSellValue.setText(ValueUtil.isStrEmpty(datalist.getAsk1p()) ? "- -" : datalist.getAsk1p());
        tvSellCount.setText(ValueUtil.isStrEmpty(datalist.getAsk1v()) ? "- -" : datalist.getAsk1v());

        tvBuyValue.setText(ValueUtil.isStrEmpty(datalist.getBid1p()) ? "- -" : datalist.getBid1p());
        tvBuyCount.setText(ValueUtil.isStrEmpty(datalist.getBid1v()) ? "- -" : datalist.getBid1v());

        tvInventoryValue.setText(ValueUtil.isStrEmpty(datalist.getInterest()) ? "" : datalist.getInterest());
        tvChangeInventoryValue.setText(ValueUtil.isStrEmpty(datalist.getChgInterest()) ? "" : datalist.getChgInterest());
        tvVolumeValue.setText(ValueUtil.isStrEmpty(datalist.getVolume()) ? "" : datalist.getVolume());
        tvChangeVolumeValue.setText(ValueUtil.isStrEmpty(datalist.getChgVolume()) ? "" : datalist.getChgVolume());
    }

    public enum MarketType {
        CONTACT,//普通合约
        IRATE//利率
    }
}
