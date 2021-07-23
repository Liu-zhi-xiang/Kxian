package com.gjmetal.app.adapter.market;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;

import java.util.List;

/**
 * Description：行情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-29 15:56
 */
public class SpinnerGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Future> futureList;

    public SpinnerGridViewAdapter(Context context, List<Future> futureList) {
        this.futureList = futureList;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return futureList.size();
    }

    @Override
    public Object getItem(int position) {
        return futureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = ViewUtil.buildView(R.layout.item_market_spinner_child);
            holder = new ViewHolder();
            holder.ivSlected = convertView.findViewById(R.id.ivSlected);
            holder.tvItemContactName = convertView.findViewById(R.id.tvItemContactName);
            holder.tvContactType = convertView.findViewById(R.id.tvContactType);
            holder.rlContact = convertView.findViewById(R.id.rlContact);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Future bean = futureList.get(position);
        holder.tvItemContactName.setText(ValueUtil.isStrNotEmpty(bean.getName()) ? bean.getName() : "- -");
        holder.tvContactType.setText(ValueUtil.isStrNotEmpty(bean.getDescribe()) ? bean.getDescribe() : "- -");
        if (bean.isSelected()) {
            holder.tvItemContactName.setTextColor(ContextCompat.getColor(mContext,R.color.cD4975C));
            holder.tvContactType.setTextColor(ContextCompat.getColor(mContext,R.color.cD4975C));
            holder.rlContact.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_spinner_contact_res));
            holder.ivSlected.setVisibility(View.VISIBLE);
        } else {
            holder.rlContact.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_spinner_contact_nor));
            holder.tvItemContactName.setTextColor(ContextCompat.getColor(mContext,R.color.cFFFFFF));
            holder.tvContactType.setTextColor(ContextCompat.getColor(mContext,R.color.c9EB2CD));
            holder.ivSlected.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void onRefersh(int position) {
        for (int i = 0; i < futureList.size(); i++) {
            if (position == i) {
                futureList.get(i).setSelected(true);
            } else {
                futureList.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        AutofitTextView tvItemContactName;
        AutofitTextView tvContactType;
        RelativeLayout rlContact;
        ImageView ivSlected;
    }
}

