package com.gjmetal.app.adapter.market.allChange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;
import cn.droidlover.xrecyclerview.RecyclerAdapter;

/**
 * Description：横向tab
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-16 15:40
 */

public class AllTabAdapter extends RecyclerAdapter<Future, AllTabAdapter.BaseViewHodler> {
    private ChooseTabTtemListener mChooseTabTtemListener = null;

    public AllTabAdapter(Context context, ChooseTabTtemListener mChooseTabTtemListener) {
        super(context);
        this.mChooseTabTtemListener = mChooseTabTtemListener;
    }

    @Override
    public AllTabAdapter.BaseViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllTabAdapter.BaseViewHodler(LayoutInflater.from(context).inflate(R.layout.item_all_change_next, parent, false));
    }

    @Override
    public void onBindViewHolder(AllTabAdapter.BaseViewHodler holder, final int position) {
        Future futureItem=data.get(position);
        holder.tvTab.setText(futureItem.getName());
        if (futureItem.isSelected()) {
            holder.viewSelected.setVisibility(View.VISIBLE);
            holder.tvTab.setTextColor(getColor(R.color.cFFFFFF));
            if(futureItem.isEnd()){
                holder.ivTabRight.setVisibility(View.GONE);
            }else {
                holder.ivTabRight.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tvTab.setTextColor(getColor(R.color.c9EB2CD));
            holder.viewSelected.setVisibility(View.INVISIBLE);
            if(position==data.size()-1){
                holder.ivTabRight.setVisibility(View.GONE);
            }else {
                holder.ivTabRight.setVisibility(View.VISIBLE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChooseTabTtemListener.setItemData(data.get(position), position);
            }
        });
    }



    public static class BaseViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTab)
        TextView tvTab;
        @BindView(R.id.viewSelected)
        View viewSelected;
        @BindView(R.id.ivTabRight)
        ImageView ivTabRight;

        public BaseViewHodler(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    public interface ChooseTabTtemListener {
        void setItemData(Future futureItem, int position);
    }
}




























