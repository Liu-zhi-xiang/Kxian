package com.gjmetal.app.adapter.spot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.SpotOfferChart;
import com.gjmetal.app.model.spot.SpotStock;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import java.util.List;

import butterknife.BindView;

public class SpotStockAdapter extends SimpleRecAdapter<SpotStock, SpotStockAdapter.MyViewHolder> {

    private SpotOfferChartAdapter.OnItemClicker onItemClicker = null;

    public SpotStockAdapter(Context context) {
        super(context);
    }


    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_spotstock;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(data.get(position).getStockName());
        holder.tvValue.setText(data.get(position).getValue());
        holder.tvUpDown.setText(data.get(position).getUpdown());
        holder.llItem.setBackgroundResource(position % 2 == 0 ? R.color.c1E3A65 : R.color.c25345B);
        if (data.get(position).getUpdown() != null) {
            GjUtil.setUporDownColor(context, holder.tvUpDown, data.get(position).getUpdown());
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llItem)
        LinearLayout llItem;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvValue)
        TextView tvValue;
        @BindView(R.id.tvUpDown)
        TextView tvUpDown;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    public void setOnItemClicker(SpotOfferChartAdapter.OnItemClicker onItemClicker) {
        this.onItemClicker = onItemClicker;
    }

    public interface OnItemClicker {
        void onItemClicker(List<SpotOfferChart> list, int position);
    }
}
