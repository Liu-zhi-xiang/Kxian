package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.model.market.Tape;
import com.gjmetal.app.ui.alphametal.industry.IndustryFragment;
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

public class MonthTapeNextGridViewAdapterTwo extends BaseAdapter {
    private Context mContext;
    private List<Tape> dataList;
    private int mLeftColor, mRightColor;
    private Specific mSpecific;
    private String mMenuCode;
    public MonthTapeNextGridViewAdapterTwo(Context context, List<Tape> dataList, Specific specific) {
        this.dataList = dataList;
        this.mContext = context;
        this.mSpecific = specific;
        mLeftColor = ContextCompat.getColor(mContext,R.color.cFFFFFF);
        mRightColor = ContextCompat.getColor(mContext,R.color.cFFFFFF);
    }
    public MonthTapeNextGridViewAdapterTwo(Context context, List<Tape> dataList, Specific specific,String mMenuCode) {
        this.dataList = dataList;
        this.mContext = context;
        this.mSpecific = specific;
        this.mMenuCode = mMenuCode;
        mLeftColor = ContextCompat.getColor(mContext,R.color.cFFFFFF);
        mRightColor = ContextCompat.getColor(mContext,R.color.cFFFFFF);
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

        holder.tvValue.setText(ValueUtil.isStrNotEmpty(bean.getValue()) ? bean.getValue() : "- -");
        // 字段的数据根据最新价 红涨绿跌 白平, (涨停)为红色 (涨跌)为绿色
        // (卖量、买量)字段为白色

        if(bean.isAdd()){//占位符
            holder.tvValue.setVisibility(View.VISIBLE);
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvValue.setText("");
            holder.tvName.setText("");
        }else {

            if (!TextUtils.isEmpty(mMenuCode)&&mMenuCode.equals(IndustryFragment.MENUCODE)){
                setItem(position, holder, bean, name);
            }else {
                if (position == 0 || position == 1) {
                    holder.tvValue.setVisibility(View.GONE);
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext,R.color.cD4975C));
                    if (ValueUtil.isStrNotEmpty(name)) {
                        holder.tvName.setText(name);
                    } else {
                        holder.tvName.setText("- -");
                    }
                } else {
                    setItem(position, holder, bean, name);
                }
            }
        }

        return convertView;
    }
    //设置对应项
    private void setItem(int position, ViewHolder holder, Tape bean, String name) {
        holder.tvValue.setVisibility(View.VISIBLE);
        holder.tvName.setText(ValueUtil.isStrNotEmpty(name) ? name : "- -");
        if (ValueUtil.isStrNotEmpty(bean.getValue()) && bean.getValue().equals("- -") || bean.getValue().equals("-")) {
            holder.tvValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFFFFFF));
        } else {
            if (ValueUtil.isStrNotEmpty(name) && name.equals("卖量") || name.equals("买量")) {
                holder.tvValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFFFFFF));
            } else {
                if(position%2==0){
                    holder.tvValue.setTextColor(mLeftColor);
                }else {
                    holder.tvValue.setTextColor(mRightColor);
                }
            }
        }
    }

    /**
     * 是涨、跌颜色
     *
     * @param
     */
    public void upOrDown(int leftColor, int rightColor) {
        mLeftColor = leftColor;
        mRightColor = rightColor;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        AutofitTextView tvName;
        AutofitTextView tvValue;
    }
}

