package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.LME3MSettlementPriceModel;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import java.util.List;

import butterknife.BindView;

/**
 * Description:
 *      LEM三个月结算价
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/28  10:14
 */
public class LmeDifferenceInPriceAdapter extends SimpleRecAdapter<LME3MSettlementPriceModel, LmeDifferenceInPriceAdapter.MyViewHolder> {


    public LmeDifferenceInPriceAdapter(Context context) {
        super(context);
    }

    @Override
    public void setData(List<LME3MSettlementPriceModel> data) {
        super.setData(data);
    }

    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_lme_settlement_price;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.shape_item_market_nor_selector);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.shape_item_market_res_selector);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTimeNum)
        AutofitTextView tvTimeNum;
        @BindView(R.id.tvPriceValue)
        AutofitTextView tvPriceValue;
        @BindView(R.id.tv3MDifference)
        AutofitTextView tv3MDifference;
        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
}
