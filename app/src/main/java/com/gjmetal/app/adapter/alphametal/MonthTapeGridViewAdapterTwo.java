package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.Tape;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;

import java.util.List;

/**
 * Description：跨月基差盘口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-14 14:50
 */

public class MonthTapeGridViewAdapterTwo extends BaseAdapter {
    private Context mContext;
    private List<Tape> dataList;
    private int upColor;

    public MonthTapeGridViewAdapterTwo(Context context, List<Tape> dataList) {
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
            convertView = ViewUtil.buildView(R.layout.item_market_tape);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvValue = convertView.findViewById(R.id.tvValue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Tape bean = dataList.get(position);
        String name = bean.getKey();
        if(bean.isAdd()){
            holder.tvName.setText("");
            holder.tvValue.setText("");
        }else {
            holder.tvName.setText(ValueUtil.isStrNotEmpty(name) ? name : "- -");
            holder.tvValue.setText(ValueUtil.isStrNotEmpty(bean.getValue()) ? bean.getValue() : "- -");
        }
        // 字段的数据根据最新价 红涨绿跌 白平, (涨停)为红色 (涨跌)为绿色
        // (卖量、买量)字段为白色
        if (ValueUtil.isStrNotEmpty(bean.getValue()) && bean.getValue().equals("- -")||bean.getValue().equals("-")) {
            holder.tvValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFFFFFF));
        } else {
            if (ValueUtil.isStrNotEmpty(name) && name.equals("卖量") || name.equals("买量")) {
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFFFFFF));
            } else {
                holder.tvValue.setTextColor(upColor);
            }
        }
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
        AutofitTextView tvName;
        AutofitTextView tvValue;
    }
}

