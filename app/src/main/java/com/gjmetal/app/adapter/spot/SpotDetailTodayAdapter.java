package com.gjmetal.app.adapter.spot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：现货走势列表详情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-26 14:52
 */

public class SpotDetailTodayAdapter extends SimpleRecAdapter<ChooseData, SpotDetailTodayAdapter.MyViewHolder> {
    private Context mContext;

    public SpotDetailTodayAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_spot_detail_today;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ChooseData bean = data.get(position);
        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(getColor(R.color.c25345B));
        } else {
            holder.llItem.setBackgroundColor(getColor(R.color.c1E3A65));
        }
        GjUtil.setUporDownColor(mContext, holder.tvMonthCenterValue, bean.getValue());
        String time = DateUtil.getStringDateByLong(bean.getPublishDatetime(), 10);
        holder.tvMonthTime.setText(ValueUtil.isStrNotEmpty(time) ? time : "");
        if (bean.isPremium()) {
            holder.tvMonthPrice.setText(GjUtil.spotText(mContext, bean.getLow()) + "-" + GjUtil.spotText(mContext, bean.getHigh()));
            holder.tvMonthCenterValue.setText(GjUtil.spotText(mContext, bean.getMiddle()));
        } else {
            holder.tvMonthPrice.setText(bean.getLow() + "-" + bean.getHigh());
            holder.tvMonthCenterValue.setText(ValueUtil.isStrNotEmpty(bean.getMiddle()) ? bean.getMiddle() : "");
        }
        holder.tvMonthUpOrDown.setText(ValueUtil.isStrNotEmpty(bean.getUpdown()) ? bean.getUpdown() : "");
        GjUtil.lastUpOrDown(mContext, bean.getUpdown(), holder.tvMonthUpOrDown);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMonthTime)
        AutofitTextView tvMonthTime;
        @BindView(R.id.tvMonthPrice)
        AutofitTextView tvMonthPrice;
        @BindView(R.id.tvMonthCenterValue)
        AutofitTextView tvMonthCenterValue;
        @BindView(R.id.tvMonthUpOrDown)
        AutofitTextView tvMonthUpOrDown;
        @BindView(R.id.llItem)
        LinearLayout llItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }


}






