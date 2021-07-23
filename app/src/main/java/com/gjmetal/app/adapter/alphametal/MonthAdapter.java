package com.gjmetal.app.adapter.alphametal;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Description 跨月基差 Adapter
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-13 10:16
 */


public class MonthAdapter extends SimpleRecAdapter<Specific, MonthAdapter.MyViewHolder> {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private int mType = 0; //0 : 涨跌; 1 : 涨幅


    public MonthAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_moth_layout;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Specific specific = data.get(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.shape_item_market_nor_selector);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.shape_item_market_res_selector);
        }
        GjUtil.setNoDataHide(mContext, holder.tvMonthNameRemark, specific.getRemark());
        if (ValueUtil.isStrNotEmpty(specific.getName())) {
            holder.tvMonthNameValue.setText(specific.getName());
        } else {
            holder.tvMonthNameValue.setText("- -");
        }

        try {
            if (mType == 0) {
                String str = StrUntils.deletePerCent(specific.getUpdown());
                if (ValueUtil.isStrNotEmpty(specific.getUpdown()) && !specific.getUpdown().equals("-")
                        && !specific.getUpdown().equals("- -") && !specific.getUpdown().equals("--")) {
                    if (specific.getUpdown().substring(0, 1).equals("-")) {
                        holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.c35CB6B));
                        if (specific.isState()) {//数据发生变化
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                        } else {
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.c35CB6B));
                        }

                    } else if (Double.valueOf(str) == 0) {
                        holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                        if (specific.isState()) {//数据发生变化
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                        } else {
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                        }
                    } else {
                        holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));
                        if (specific.isState()) {//数据发生变化
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                        } else {
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));
                        }
                    }
                    holder.tvMonthUpOrDownValue.setText(specific.getUpdown());

                } else {
                    holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                    if (specific.isState()) {//数据发生变化
                        holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                    } else {
                        holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                    }
                    holder.tvMonthUpOrDownValue.setText("- -");

                }
            } else if (mType == 1) {
                String str = StrUntils.deletePerCent(specific.getUpdown());
                if (ValueUtil.isStrNotEmpty(specific.getPercent()) && !specific.getPercent().equals("-")
                        && !specific.getUpdown().equals("- -") && !specific.getUpdown().equals("--")) {
                    if (specific.getUpdown().substring(0, 1).equals("-")) {
                        holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.c35CB6B));
                        if (specific.isState()) {//数据发生变化
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                        } else {
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.c35CB6B));
                        }
                    } else if (Double.valueOf(str) == 0) {
                        holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                        if (specific.isState()) {//数据发生变化
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                        } else {
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                        }
                    } else {
                        holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));
                        if (specific.isState()) {//数据发生变化
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                        } else {
                            holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cFF5252));
                        }
                    }
                    holder.tvMonthUpOrDownValue.setText(specific.getPercent());

                } else {
                    holder.tvMonthUpOrDownValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                    if (specific.isState()) {//数据发生变化
                        holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cF8E71C));
                    } else {
                        holder.tvMonthBestNewValue.setTextColor(ContextCompat.getColor(mContext,R.color.cE7EDF5));
                    }
                    holder.tvMonthUpOrDownValue.setText("- -");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (ValueUtil.isStrNotEmpty(specific.getLast()) && !specific.getLast().equals("-")) {
            holder.tvMonthBestNewValue.setText(specific.getLast());
        } else {
            holder.tvMonthBestNewValue.setText("- -");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValueUtil.isEmpty(specific)) {
                    ToastUtil.showToast("未获取到数据");
                    return;
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(v, position, specific);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ValueUtil.isNotEmpty(specific)) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onLongClick(v, position, specific);
                    }
                }
                return true; //决定是否在长按后再加一个短按动作
            }
        });


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMonthNameValue)
        AutofitTextView tvMonthNameValue;
        @BindView(R.id.tvMonthNameRemark)
        AutofitTextView tvMonthNameRemark;
        @BindView(R.id.tvMonthBestNewValue)
        TextView tvMonthBestNewValue;
        @BindView(R.id.tvMonthUpOrDownValue)
        TextView tvMonthUpOrDownValue;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    public void setClickType(int type) {
        this.mType = type;
        notifyDataSetChanged();
    }

    public void spanItemTop(Specific specific) {
        data.add(0, specific);
        notifyDataSetChanged();
    }

    public void updateDatas(ArrayList<Specific> specifics) {
        if (ValueUtil.isListNotEmpty(specifics)) {
            data.clear();
            data.addAll(specifics);
            notifyDataSetChanged();
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        if (listener != null) {
            this.mOnItemClickListener = listener;
        }
    }


    public interface OnItemClickListener {
        void onClick(View view, int postion, Specific monthModel);

        void onLongClick(View view, int postion, Specific monthModel);
    }


}






