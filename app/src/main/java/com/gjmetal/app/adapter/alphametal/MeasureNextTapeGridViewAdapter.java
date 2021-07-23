package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gjmetal.app.R;
import com.gjmetal.app.model.socket.TapeSocket;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;

import java.util.List;

/**
 * Description：进口测算盘口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-14 14:50
 */

public class MeasureNextTapeGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<TapeSocket.BlocksBean.ItemsBean> dataList;
    private int upColor;

    public MeasureNextTapeGridViewAdapter(Context context, List<TapeSocket.BlocksBean.ItemsBean> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        upColor = ContextCompat.getColor(mContext,R.color.cFFFFFF);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if (convertView == null) {
            convertView = ViewUtil.buildView(R.layout.item_measure_tape_next);
            holder = new ViewHolder();
            holder.tvMeasureName = convertView.findViewById(R.id.tvMeasureName);
            holder.tvMeasureValue = convertView.findViewById(R.id.tvMeasureValue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TapeSocket.BlocksBean.ItemsBean bean = dataList.get(position);
        String name = bean.getName();
        String value = bean.getValue();
        if (bean.isAdd()) {//空占位符
            holder.tvMeasureName.setText("");
            holder.tvMeasureValue.setText("");
        } else {
            holder.tvMeasureName.setText(ValueUtil.isStrNotEmpty(name) ? name : "- -");
            holder.tvMeasureValue.setText(ValueUtil.isStrNotEmpty(value) ? value : "- -");
        }
        holder.tvMeasureValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFFFFFF));

        return convertView;
    }

    /**
     * 是涨、跌颜色
     *
     * @param
     */
    public void upOrDown(int color) {
        upColor = color;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        AutofitTextView tvMeasureName;
        AutofitTextView tvMeasureValue;
    }


}

