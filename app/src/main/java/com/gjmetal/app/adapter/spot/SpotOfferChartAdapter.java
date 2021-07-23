package com.gjmetal.app.adapter.spot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.SpotOfferChart;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import java.util.List;

import butterknife.BindView;

public class SpotOfferChartAdapter extends SimpleRecAdapter<SpotOfferChart, SpotOfferChartAdapter.MyViewHolder> {

    private OnItemClicker onItemClicker = null;

    public SpotOfferChartAdapter(Context context) {
        super(context);
    }

    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_spotofferchart;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (data.get(position).isChooseDate()) {
            holder.ivChooseTime.setImageResource(R.mipmap.spotchoosetimeyes);
        } else {
            holder.ivChooseTime.setImageResource(R.mipmap.spotchoosetimeno);
        }
        holder.tvChooseDate.setText(data.get(position).getDate());
        holder.llChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClicker != null) {
                    onItemClicker.onItemClicker(data, position);
                }
            }
        });
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivChooseTime)
        ImageView ivChooseTime;
        @BindView(R.id.tvChooseDate)
        TextView tvChooseDate;
        @BindView(R.id.llChooseTime)
        LinearLayout llChooseTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    public void setOnItemClicker(OnItemClicker onItemClicker) {
        this.onItemClicker = onItemClicker;
    }

    public interface OnItemClicker {
        void onItemClicker(List<SpotOfferChart> list, int position);
    }
}
