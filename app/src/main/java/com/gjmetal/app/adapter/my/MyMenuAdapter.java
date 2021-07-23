package com.gjmetal.app.adapter.my;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.my.MyMenuItem;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;

import java.util.List;

/**
 * Description：个人中心菜单
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-21 14:59
 */

public class MyMenuAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyMenuItem> dataList;

    public MyMenuAdapter(Context context, List<MyMenuItem> dataList) {
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
            convertView = ViewUtil.buildView(R.layout.item_mymenu);
            holder = new ViewHolder();
            holder.tvMenuName = convertView.findViewById(R.id.tvMenuName);
            holder.tvDesc = convertView.findViewById(R.id.tvDesc);
            holder.tvNum = convertView.findViewById(R.id.tvNum);
            holder.ivMenu = convertView.findViewById(R.id.ivMenu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyMenuItem bean = dataList.get(position);
        holder.tvMenuName.setText(ValueUtil.isStrNotEmpty(bean.getMenuName()) ? bean.getMenuName() : "");
        holder.tvDesc.setText(ValueUtil.isStrNotEmpty(bean.getDesc()) ? bean.getDesc() : "");
        holder.ivMenu.setBackgroundResource(bean.getRes());
        if (bean.getMsgMum() > 0) {
            if (bean.getMsgMum() > 99) {
                holder.tvNum.setText(99 + "+");
            } else {
                holder.tvNum.setText(String.valueOf(bean.getMsgMum()));
            }
            holder.tvNum.setVisibility(View.VISIBLE);
        } else {
            holder.tvNum.setVisibility(View.GONE);
        }
        return convertView;
    }


    /**
     * 设置显示消息数
     *
     * @param num
     */
    public void setMsgNum(int num) {
        if (ValueUtil.isListEmpty(dataList)) {
            return;
        }
        for (MyMenuItem bean : dataList) {
            if (bean.isMsg()) {
                bean.setMsgMum(num);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        AutofitTextView tvMenuName;
        AutofitTextView tvDesc;
        TextView tvNum;
        ImageView ivMenu;
    }
}

