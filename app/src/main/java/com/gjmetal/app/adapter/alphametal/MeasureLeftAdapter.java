package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.LeftViewModel;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：测算横屏
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-20 17:29
 */

public class MeasureLeftAdapter extends SimpleRecAdapter<LeftViewModel, MeasureLeftAdapter.ViewHolder> {
    private Context mContext;
    private String type;


    public MeasureLeftAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_measure_left;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final LeftViewModel bean = data.get(position);
        holder.tvMeasureLeftName.setText(ValueUtil.isStrNotEmpty(bean.getItemName()) ? bean.getItemName() : "");
        holder.tvMeasureLeftValue.setText(ValueUtil.isStrNotEmpty(bean.getValue()) ? bean.getValue() : "");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMeasureLeftName)
        TextView tvMeasureLeftName;
        @BindView(R.id.tvMeasureLeftValue)
        TextView tvMeasureLeftValue;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

}

