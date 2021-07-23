package com.gjmetal.app.adapter.information;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.droidlover.xrecyclerview.RecyclerAdapter;

/**
 * 资讯历史
 * Created by huangb on 2018/4/8.
 */

public class InformationHistoryAdapter extends RecyclerAdapter<String, InformationHistoryAdapter.ViewHodler> {

    public InformationHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public InformationHistoryAdapter.ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InformationHistoryAdapter.ViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_histroy, parent, false));
    }

    @Override
    public void onBindViewHolder(InformationHistoryAdapter.ViewHodler holder, final int position) {
        holder.linear_title.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        holder.tv_content.setText(data.get(position));
        holder.tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClicklistener != null) {
                    mOnItemClicklistener.OnTabClick(data.get(position), position);
                }
            }
        });
        holder.tv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClicklistener != null) {
                    mOnItemClicklistener.OnCleanClick();
                }
            }
        });

    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.linear_title)
        LinearLayout linear_title;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_clean)
        TextView tv_clean;

        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnHistoryClickListener {
        void OnTabClick(String s, int position);
        void OnCleanClick();
    }

    private OnHistoryClickListener mOnItemClicklistener;

    public void setOnItemClickListener(OnHistoryClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            mOnItemClicklistener = onItemClickListener;
        }
    }
}
