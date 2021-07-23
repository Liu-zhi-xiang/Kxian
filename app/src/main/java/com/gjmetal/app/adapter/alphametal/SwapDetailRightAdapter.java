package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.SwapDetailRightItems;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description 调期费详情 right
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-12 10:36
 */

public class SwapDetailRightAdapter extends SimpleRecAdapter<SwapDetailRightItems, SwapDetailRightAdapter.MyViewHolder> {
    private Context mContext;

    public SwapDetailRightAdapter(Context context) {
        super(context);
        this.mContext = context;
    }


    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.swap_detail_right_item;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.selector_item_btn3);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.selector_item_btn4);
        }

        if (ValueUtil.isListNotEmpty(data)) {
            SwapDetailRightItems rightItems = data.get(position);

            if (rightItems.getUpDowms().substring(0, 1).equals("-")) {
                (holder.tvNewPriceValue).setTextColor(ContextCompat.getColor(mContext,R.color.c35CB6B));
                (holder.tvUpDownValue).setTextColor(ContextCompat.getColor(mContext,R.color.c35CB6B));

            } else if (rightItems.getUpDowms().substring(0, 1).equals("+")) {
                (holder.tvNewPriceValue).setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));
                (holder.tvUpDownValue).setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));

            } else if (Double.valueOf(rightItems.getUpDowms()) > 0) {
                (holder.tvNewPriceValue).setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));
                (holder.tvUpDownValue).setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));

            } else {
                (holder.tvNewPriceValue).setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                (holder.tvUpDownValue).setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getNewPrices())) {
                holder.tvNewPriceValue.setText(rightItems.getNewPrices());
            } else {
                holder.tvNewPriceValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getUpDowms())) {
                holder.tvUpDownValue.setText(rightItems.getUpDowms());
            } else {
                holder.tvUpDownValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getBuy())) {
                holder.tvBuyValue.setText(rightItems.getBuy());
            } else {
                holder.tvBuyValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getBuyNums())) {
                holder.tvBuyNumsValue.setText(rightItems.getBuyNums());
            } else {
                holder.tvBuyNumsValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getBuyDates())) {
                holder.tvBuyDateValue.setText(rightItems.getBuyDates());
            } else {
                holder.tvBuyDateValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getSell())) {
                holder.tvSellValue.setText(rightItems.getSell());
            } else {
                holder.tvSellValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getSellNums())) {
                holder.tvSellNumsValue.setText(rightItems.getSellNums());
            } else {
                holder.tvSellNumsValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getSellDates())) {
                holder.tvSellDateValue.setText(rightItems.getSellDates());
            } else {
                holder.tvSellDateValue.setText("- -");
            }

            if (ValueUtil.isStrNotEmpty(rightItems.getYTDPut())) {
                holder.tvYTDPutValue.setText(rightItems.getYTDPut());
            } else {
                holder.tvYTDPutValue.setText("- -");
            }

        } else {
            holder.tvNewPriceValue.setText("- -");
            holder.tvUpDownValue.setText("- -");
            holder.tvBuyValue.setText("- -");
            holder.tvBuyNumsValue.setText("- -");
            holder.tvBuyDateValue.setText("- -");
            holder.tvSellValue.setText("- -");
            holder.tvSellNumsValue.setText("- -");
            holder.tvSellDateValue.setText("- -");
            holder.tvYTDPutValue.setText("- -");

        }

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNewPriceValue)
        AutofitTextView tvNewPriceValue;
        @BindView(R.id.tvUpDownValue)
        AutofitTextView tvUpDownValue;
        @BindView(R.id.tvBuyValue)
        AutofitTextView tvBuyValue;
        @BindView(R.id.tvBuyNumsValue)
        AutofitTextView tvBuyNumsValue;
        @BindView(R.id.tvBuyDateValue)
        AutofitTextView tvBuyDateValue;
        @BindView(R.id.tvSellValue)
        AutofitTextView tvSellValue;
        @BindView(R.id.tvSellNumsValue)
        AutofitTextView tvSellNumsValue;
        @BindView(R.id.tvSellDateValue)
        AutofitTextView tvSellDateValue;
        @BindView(R.id.tvYTDPutValue)
        AutofitTextView tvYTDPutValue;

        public MyViewHolder(View v) {
            super(v);
            KnifeKit.bind(this, v);
        }
    }

}













