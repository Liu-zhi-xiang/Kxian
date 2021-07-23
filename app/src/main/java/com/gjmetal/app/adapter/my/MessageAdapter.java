package com.gjmetal.app.adapter.my;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.my.MessageBean;
import com.gjmetal.app.util.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.droidlover.xrecyclerview.RecyclerAdapter;

/**
 * 消息适配器
 * Created by huangb on 2018/4/9.
 */

public class MessageAdapter extends RecyclerAdapter<MessageBean.ItemListBean, MessageAdapter.ViewHodler> {
    String currentdate;

    public MessageAdapter(Context context) {
        super(context);
        currentdate = DateUtil.getStringDateByLong(System.currentTimeMillis(), 4);
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHodler(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHodler holder, final int position) {
        final MessageBean.ItemListBean itemListBean = data.get(position);
        if (itemListBean.getType() == 1) {
            holder.tvName.setText("系统消息");
        } else {
            holder.tvName.setText("智能预警");
        }
        holder.textContent.setText(itemListBean.getContent());
        String date = DateUtil.getStringDateByLong(Long.parseLong(itemListBean.getCreateAt()), 4);
        String time = null;
        if (date.equals(currentdate)) {
            time = DateUtil.getStringDateByLong(Long.parseLong(itemListBean.getCreateAt()), 8);
        } else {
            time = DateUtil.getStringDateByLong(Long.parseLong(itemListBean.getCreateAt()), 9);
        }
        if (time != null) {
            holder.tvTime.setText(time);
        }

        if (itemListBean.getStatus() == 0) {
            holder.textContent.setTextColor(ContextCompat.getColor(context,R.color.c2A2D4F));
        } else {
            holder.textContent.setTextColor(ContextCompat.getColor(context,R.color.c6A798E));
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnDeleteMessageLstener != null) {
                    mOnDeleteMessageLstener.OnDelete(holder.itemView, data, position);
                }

                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDeleteMessageLstener != null) {
                    mOnDeleteMessageLstener.redMsg(data, position);
                }
            }
        });

    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.text_content)
        TextView textContent;
        @BindView(R.id.llItem)
        LinearLayout llItem;

        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnDeleteMessageLstener {
        void OnDelete(View view, List<MessageBean.ItemListBean> itemList, int position);

        void redMsg(List<MessageBean.ItemListBean> itemList, int position);
    }

    private OnDeleteMessageLstener mOnDeleteMessageLstener;

    public void addDeleteMessageListener(OnDeleteMessageLstener onDeleteMessageLstener) {
        if (onDeleteMessageLstener != null) {
            this.mOnDeleteMessageLstener = onDeleteMessageLstener;
        }
    }

}
