package com.gjmetal.app.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import java.util.List;

import butterknife.BindView;

/**
 * Description：右行情场外期权
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-30 10:20
 */

public class OtcOptionsLeftAdapter extends SimpleRecAdapter<RoomItem, OtcOptionsLeftAdapter.ViewHolder> {
    private Context mContext;
    private List<String> dateList;
    private BaseCallBack baseCallBack;
    private String optionType;

    public OtcOptionsLeftAdapter(Context context, List<String> dateList, String optionType,BaseCallBack callBack) {
        super(context);
        this.mContext = context;
        this.dateList = dateList;
        this.optionType = optionType;
        this.baseCallBack=callBack;
    }

    public OtcOptionsLeftAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_market_left_otcoption;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.shape_item_market_res_otc);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.shape_item_market_nor_otc);
        }
        final RoomItem bean = data.get(position);
        if (ValueUtil.isListNotEmpty(data)) {
            bean.setResult(data);
        }
        if (ValueUtil.isListNotEmpty(dateList)) {
            bean.setDateList(dateList);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                baseCallBack.back(bean);
            }
        });
        GjUtil.switchOtcColor(mContext, bean.getComparedToLastDayPrice(), holder.tvPrice);//红涨绿跌
        if (ValueUtil.isStrNotEmpty(bean.getContractId())) {
            holder.tvObject.setVisibility(View.VISIBLE);
        } else {
            holder.tvObject.setVisibility(View.GONE);
        }
        holder.tvObject.setText(ValueUtil.isStrNotEmpty(bean.getContractId()) ? bean.getContractId() : "");
        holder.tvOtcGoods.setText(ValueUtil.isStrNotEmpty(bean.getName()) ? bean.getName() : "- -");
        holder.tvPrice.setText(ValueUtil.isStrNotEmpty(bean.getStrike()) ? bean.getStrike() : "- -");
        if (bean.getStrikeState() != null && bean.getStrikeState() == 1) {//数据发生变化
            holder.tvPrice.setTextColor(getColor(R.color.cF8E71C));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvOtcGoods)
        AutofitTextView tvOtcGoods;
        @BindView(R.id.tvObject)
        AutofitTextView tvObject;
        @BindView(R.id.tvPrice)
        AutofitTextView tvPrice;

        public ViewHolder(View v) {
            super(v);
            KnifeKit.bind(this, v);
        }
    }

}













