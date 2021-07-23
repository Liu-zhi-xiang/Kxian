package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：盈亏比价
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-29 15:56
 */
public class HelperAdapter extends SimpleRecAdapter<Specific, HelperAdapter.ViewHolder> {

    private Context mContext;
    private String type;
    private String  mMenuCode;
    public HelperAdapter(Context context, String type,String  mMenuCode) {
        super(context);
        this.mContext = context;
        this.type = type;
        this.mMenuCode = mMenuCode;
    }
    private OnItemClickListener onItemClickListener;

    public HelperAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_helper_view;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Specific bean = data.get(position);
        GjUtil.setNoDataHide(mContext, holder.tvFutureRemark, bean.getRemark());
        GjUtil.setNoDataShow(mContext, holder.tvFutureName, bean.getZhName());
        GjUtil.setNoDataShow(mContext, holder.tvParity, bean.getParity());//Lme3M
        GjUtil.setNoDataShow(mContext, holder.tvProfit, bean.getProfit());//进口盈亏
        GjUtil.showTextStyle(context, holder.tvUpOrDown, bean.getProfitUpdown(), bean.getProfitUpdown(), holder.tvUpOrDown, holder.tvProfit);
        holder.tvParity.setTextColor(getColor(R.color.cD8DDE3)); //比值
        if (bean.getProfitState() != null && bean.getProfitState() == 1) {
            holder.tvProfit.setTextColor(getColor(R.color.cF8E71C));// 盈亏
        }

        if (position % 2 == 0) {
            holder.llItem.setBackgroundResource(R.drawable.shape_item_market_nor_selector);
        } else {
            holder.llItem.setBackgroundResource(R.drawable.shape_item_market_res_selector);
        }

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.onClick(v,position,bean);
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFutureName)
        AutofitTextView tvFutureName;
        @BindView(R.id.tvFutureRemark)
        AutofitTextView tvFutureRemark;
        @BindView(R.id.tvParity)
        AutofitTextView tvParity;
        @BindView(R.id.tvProfit)
        AutofitTextView tvProfit;
        @BindView(R.id.tvUpOrDown)
        AutofitTextView tvUpOrDown;
        @BindView(R.id.llItem)
        LinearLayout llItem;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
    public interface OnItemClickListener {
        void onClick(View view, int postion, Specific monthModel);

        void onLongClick(View view, int postion, Specific monthModel);
    }

}

