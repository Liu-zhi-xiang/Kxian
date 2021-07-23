package com.gjmetal.app.adapter.market;

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
 * Description：盘口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-14 14:50
 */

public class TapeGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<TapeSocket.BlocksBean.ItemsBean> dataList;
    private int upColor;

    public TapeGridViewAdapter(Context context, List<TapeSocket.BlocksBean.ItemsBean> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        upColor = ContextCompat.getColor(mContext, R.color.cFFFFFF);
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
        holder.tvName.setText(ValueUtil.isStrNotEmpty(name) ? name : "- -");
        holder.tvValue.setText(ValueUtil.isStrNotEmpty(bean.getValue()) ? bean.getValue() : "- -");
        // (卖价、买价、最新、涨跌、开盘、最高、最低、均价、结算)字段的数据根据最新价 红涨绿跌 白平, (涨停)为红色 (涨跌)为绿色
        // (卖量、买量、成交量、持仓量、日增仓、昨结、昨收)字段为白色
        try {

            if (ValueUtil.isStrEmpty(name)) {
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
            } else if (name.equals("卖价") || name.equals("买价") || name.equals("最新") || name.equals("开盘") || name.equals("最高")
                    || name.equals("最低") || name.equals("均价") || name.equals("涨跌") ) {//卖价、买价、最新、开盘、最高、最低、均价
                if (bean.getIsColor() == 0) {
                    holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
                } else if (bean.getIsColor() == 1) {
                    holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252));
                } else if (bean.getIsColor() == 2) {
                    holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
                }
            }else if ( name.equals("涨停")){
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252));
            }else if ( name.equals("跌停")){
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
            }else {
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    public static class ViewHolder {
        AutofitTextView tvName;
        AutofitTextView tvValue;
    }
}

