package com.gjmetal.app.adapter.spot;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.util.DateUtil;
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

public class SpotDetailWeekAdapter extends SimpleRecAdapter<ChooseData, SpotDetailWeekAdapter.MyViewHolder> {
    private Context mContext;
    public SpotDetailWeekAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_spot_detail_week;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ChooseData bean = data.get(position);
        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(getColor(R.color.c1E3A65));
        } else {
            holder.llItem.setBackgroundColor(getColor(R.color.c25345B));
        }
        holder.tvMonthTime.setText(DateUtil.getStringDateByLong(bean.getBeginDate(),2)+"~"+DateUtil.getStringDateByLong(bean.getEndDate(),2));
        holder.tvAvgPrice.setText(ValueUtil.isStrNotEmpty(bean.getMiddle())?bean.getMiddle():"");
        holder.tvMonthUnit.setText(ValueUtil.isStrNotEmpty(bean.getUnit())?bean.getUnit():"");
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMonthTime)
        AutofitTextView tvMonthTime;
        @BindView(R.id.tvAvgPrice)
        AutofitTextView tvAvgPrice;
        @BindView(R.id.tvMonthUnit)
        AutofitTextView tvMonthUnit;
        @BindView(R.id.llItem)
        LinearLayout llItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }


}






