package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 *  Description:盈亏比价2
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/22  20:41
 *
 */
public class HelperAdapterTwo extends SimpleRecAdapter<RoomItem, HelperAdapterTwo.ViewHolder> {

    private Context mContext;

    public HelperAdapterTwo(Context context) {
        super(context);
        this.mContext = context;

    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
    public void onBindViewHolder(ViewHolder holder,  int position) {
        final RoomItem bean = data.get(position);
        GjUtil.setNoDataHide(mContext, holder.tvFutureRemark, bean.getDescribe());
        GjUtil.setNoDataShow(mContext, holder.tvFutureName, bean.getName());
        GjUtil.setNoDataShow(mContext, holder.tvParity, bean.getParity());//Lme3M
        GjUtil.setNoDataShow(mContext, holder.tvProfit, bean.getProfit());//进口盈亏
        String profitUpdown=ValueUtil.addMark(bean.getProfitUpdown());
        GjUtil.showTextStyle(context, holder.tvUpOrDown, profitUpdown, profitUpdown, holder.tvUpOrDown, holder.tvProfit);
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
                    onItemClickListener.onClick(v,bean);
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
        void onClick(View view, RoomItem monthModel);

        void onLongClick(View view, RoomItem monthModel);
    }

}

