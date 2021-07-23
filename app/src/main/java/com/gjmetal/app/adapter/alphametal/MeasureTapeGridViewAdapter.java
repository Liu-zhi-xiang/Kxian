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

public class MeasureTapeGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<TapeSocket.BlocksBean.ItemsBean> dataList;
    private float preClose = 0;
    private boolean isPreClose=true;
    public MeasureTapeGridViewAdapter(Context context, List<TapeSocket.BlocksBean.ItemsBean> dataList) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = ViewUtil.buildView(R.layout.item_market_tape);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvValue = convertView.findViewById(R.id.tvValue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TapeSocket.BlocksBean.ItemsBean bean = dataList.get(position);
        String name = bean.getName();

        if (bean.isAdd()) {
            holder.tvName.setText("");
            holder.tvValue.setText("");
        } else {
            holder.tvName.setText(ValueUtil.isStrNotEmpty(name) ? name : "- -");
            holder.tvValue.setText(ValueUtil.isStrNotEmpty(bean.getValue()) ? bean.getValue() : "- -");
        }

        // 字段的数据根据最新价 红涨绿跌 白平, (涨停)为红色 (涨跌)为绿色
        // (卖量、买量)字段为白色
        if (ValueUtil.isStrEmpty(name)) {
            holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
        } else if (name.equals("- -") || name.equals("-")) {//出口测算贸易升水固定为白色
            holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
        } else if (name.equals("涨跌")) {
            if (bean.getIsColor()==1){
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252));
            }else if (bean.getIsColor()==2){
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
            }else {
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
            }
        } else {
            String value = bean.getValue();
            if (!isPreClose||ValueUtil.isStrEmpty(value) || value.equals("- -") || value.equals("-")) {
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
            } else {
                try {
                    float  v = Float.parseFloat(bean.getValue());
                    if (preClose > v) {
                        holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
                    } else if (preClose < v) {
                        holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252));
                    } else {
                        holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
                }
            }
        }
        return convertView;
    }



    public void setPreClose(float preClose,boolean isPreClose) {
        this.preClose = preClose;
        this.isPreClose=isPreClose;
    }
    public static class ViewHolder {
        AutofitTextView tvName;
        AutofitTextView tvValue;
    }
}

