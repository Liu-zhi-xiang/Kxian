package com.gjmetal.app.adapter.information;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.flash.FinanceDate;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：搜索
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-2 9:41
 */
public class DateAdapter extends SimpleRecAdapter<FinanceDate.DayEventsBean, DateAdapter.ViewHolder> {
    private Context mContext;

    public DateAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_finance_date;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FinanceDate.DayEventsBean bean = data.get(position);
        holder.tvFinanceTitle.setText(ValueUtil.isStrNotEmpty(bean.getEventName()) ? bean.getEventName() : "");
        String time = DateUtil.getStringDateByString(bean.getTime(), 5);
        if (ValueUtil.isStrNotEmpty(time) && time.equals("12:02")) {
            holder.tvFinanceTime.setText("待定");
        } else {
            holder.tvFinanceTime.setText(ValueUtil.isStrNotEmpty(bean.getTime()) ? DateUtil.getStringDateByString(bean.getTime(), 5) : "");
        }
        holder.tvTodayValue.setText(ValueUtil.isStrNotEmpty(bean.getDayValue()) ? bean.getDayValue() : "");
        holder.tvPreValue.setText(ValueUtil.isStrNotEmpty(bean.getForecastValue()) ? bean.getForecastValue() : "");
        holder.tvBeforeValue.setText(ValueUtil.isStrNotEmpty(bean.getBeforeValue())?bean.getBeforeValue():"");
        int star = bean.getImportant();
        switch (star) {
            case 1:
                holder.ivStar1.setBackgroundResource(R.mipmap.ic_star);
                holder.ivStar2.setBackgroundResource(R.mipmap.ic_unstar);
                holder.ivStar3.setBackgroundResource(R.mipmap.ic_unstar);
                break;
            case 2:
                holder.ivStar1.setBackgroundResource(R.mipmap.ic_star);
                holder.ivStar2.setBackgroundResource(R.mipmap.ic_star);
                holder.ivStar3.setBackgroundResource(R.mipmap.ic_unstar);
                break;
            case 3:
                holder.ivStar1.setBackgroundResource(R.mipmap.ic_star);
                holder.ivStar2.setBackgroundResource(R.mipmap.ic_star);
                holder.ivStar3.setBackgroundResource(R.mipmap.ic_star);
                break;
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivStar1)
        ImageView ivStar1;
        @BindView(R.id.ivStar2)
        ImageView ivStar2;
        @BindView(R.id.ivStar3)
        ImageView ivStar3;
        @BindView(R.id.llScore)
        LinearLayout llScore;
        @BindView(R.id.tvFinanceTime)
        TextView tvFinanceTime;
        @BindView(R.id.tvFinanceTitle)
        TextView tvFinanceTitle;
        @BindView(R.id.tvTodayValue)
        TextView tvTodayValue;
        @BindView(R.id.tvPreValue)
        TextView tvPreValue;
        @BindView(R.id.tvBeforeValue)
        TextView tvBeforeValue;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
}

