package com.gjmetal.app.adapter.market.allChange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;
import cn.droidlover.xrecyclerview.RecyclerAdapter;

/**
 * Description：选择
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-26 17:22
 */


public class AllChangeItemAdapter extends RecyclerAdapter<Future, AllChangeItemAdapter.BaseViewHodler> {
    private Context mContext;
    private CallBackOnItemClik callBackOnItemClik;
    private boolean mShowCheck;
    public AllChangeItemAdapter(Context context,CallBackOnItemClik callBackOnItemClik) {
        super(context);
        this.mContext = context;
        this.callBackOnItemClik=callBackOnItemClik;
    }

    @Override
    public BaseViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHodler(LayoutInflater.from(context)
                .inflate(R.layout.item_all_market_change, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHodler holder, final int position) {
        final Future futureItem = data.get(position);
        holder.tvName.setText(futureItem.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mShowCheck){
                    futureItem.setCheck(!futureItem.isCheck());
                    callBackOnItemClik.onCheck(futureItem);
                    notifyDataSetChanged();
                }else {
                    if(futureItem.isEnd()){
                        return;
                    }
                    callBackOnItemClik.onClick(futureItem);
                }
            }

        });
        holder.llCheck.setVisibility(mShowCheck?View.VISIBLE:View.GONE);
        if(futureItem.isCheck()){
            holder.ivCheck.setBackgroundResource(R.mipmap.ic_checkbox_res);
        }else {
            holder.ivCheck.setBackgroundResource(R.mipmap.ic_checkbox_nor);
        }
    }
    public static class BaseViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.llCheck)
        LinearLayout llCheck;
        @BindView(R.id.ivCheck)
        ImageView ivCheck;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.rlChoose)
        RelativeLayout rlChoose;

        public BaseViewHodler(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }

    }

    public void showCheck(boolean show){
        mShowCheck=show;
    }


    public interface CallBackOnItemClik {
        void onClick(Future futureItem);
        void onCheck(Future futureItem);
    }
}






















