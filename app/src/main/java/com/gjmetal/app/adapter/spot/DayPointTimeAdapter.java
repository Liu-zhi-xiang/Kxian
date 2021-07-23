package com.gjmetal.app.adapter.spot;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.SpotOfferChart;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;

import java.util.List;

/**
 * Description：日报价时间
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-5-18 16:37
 */

public class DayPointTimeAdapter extends BaseAdapter {
    private Context mContext;
    private List<SpotOfferChart> dataList;

    public DayPointTimeAdapter(Context context, List<SpotOfferChart> dataList) {
        this.dataList = dataList;
        this.mContext = context;
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = ViewUtil.buildView(R.layout.adapter_spotofferchart);
            holder = new ViewHolder();
            holder.tvChooseDate = convertView.findViewById(R.id.tvChooseDate);
            holder.ivChooseTime = convertView.findViewById(R.id.ivChooseTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SpotOfferChart bean = dataList.get(position);
        holder.tvChooseDate.setText(ValueUtil.isStrNotEmpty(bean.getDate()) ? bean.getDate() : "");
        if (bean.isChooseDate()) {
            holder.ivChooseTime.setImageResource(R.mipmap.spotchoosetimeyes);
        } else {
            holder.ivChooseTime.setImageResource(R.mipmap.spotchoosetimeno);
        }
        return convertView;
    }

    public static class ViewHolder {
        AutofitTextView tvChooseDate;
        ImageView ivChooseTime;
    }
}

