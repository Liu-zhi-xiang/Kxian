package com.gjmetal.app.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：右行情场外期权
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-30 10:20
 */

public class OtcOptionsRightAdapter extends SimpleRecAdapter<RoomItem, OtcOptionsRightAdapter.ViewHolder> {
    private Context mContext;
    private BaseCallBack baseCallBack;
    private int mScreenWidth;

    public OtcOptionsRightAdapter(Context context, int screenWidth, BaseCallBack callBack) {
        super(context);
        this.mContext = context;
        this.baseCallBack = callBack;
        this.mScreenWidth = screenWidth;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_market_right_otcoption;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.selector_item_btn4);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.selector_item_btn3);
        }
        final RoomItem bean = data.get(position);
        if (ValueUtil.isListNotEmpty(data)) {
            bean.setResult(data);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                    baseCallBack.back(bean);
            }
        });
        GjUtil.setNoDataShow(mContext, holder.tvBuyPrice, bean.getBuy());
        GjUtil.setNoDataShow(mContext, holder.tvSellPrice, bean.getSell());
        GjUtil.setNoDataShow(mContext, holder.tvBuyPercent, bean.getBuyPer());
        GjUtil.setNoDataShow(mContext, holder.tvSellPercent, bean.getSellPer());
        GjUtil.switchOtcColor(mContext, bean.getComparedToLastDayPrice(), holder.tvBuyPrice, holder.tvSellPrice, holder.tvBuyPercent, holder.tvSellPercent);//红涨绿跌

        LinearLayout.LayoutParams layoutParamsNow = (LinearLayout.LayoutParams) holder.llPer.getLayoutParams();
        layoutParamsNow.width = mScreenWidth;
        holder.llPer.setLayoutParams(layoutParamsNow);
        //数据发生变化
        if (bean.getBuyState() != null && bean.getBuyState() == 1) {
            holder.tvBuyPrice.setTextColor(getColor(R.color.cF8E71C));
        }
        if (bean.getSellState() != null && bean.getSellState() == 1) {
            holder.tvSellPrice.setTextColor(getColor(R.color.cF8E71C));
        }
        if (bean.getBuyPerState() != null && bean.getBuyPerState() == 1) {
            holder.tvBuyPercent.setTextColor(getColor(R.color.cF8E71C));
        }
        if (bean.getSellPerState() != null && bean.getSellPerState() == 1) {
            holder.tvSellPercent.setTextColor(getColor(R.color.cF8E71C));
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBuyPrice)
        AutofitTextView tvBuyPrice;
        @BindView(R.id.tvSellPrice)
        AutofitTextView tvSellPrice;
        @BindView(R.id.tvBuyPercent)
        AutofitTextView tvBuyPercent;
        @BindView(R.id.tvSellPercent)
        AutofitTextView tvSellPercent;
        @BindView(R.id.llPer)
        LinearLayout llPer;

        public ViewHolder(View v) {
            super(v);
            KnifeKit.bind(this, v);
        }
    }

}













