package com.gjmetal.app.adapter.my;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.my.MessageBean;
import com.gjmetal.app.util.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.droidlover.xrecyclerview.RecyclerAdapter;

/**
 * 消息适配器
 * Created by huangb on 2018/4/9.
 */

public class MyMessageAdapter extends RecyclerAdapter<MessageBean.ItemListBean, MyMessageAdapter.ViewHodler> {
    public MyMessageAdapter(Context context) {
        super(context);
    }

    private int mCont = -1;

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHodler(LayoutInflater.from(context).inflate(R.layout.my_item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHodler holder, final int position) {
        final MessageBean.ItemListBean itemListBean = data.get(position);
        holder.text_content.setText(itemListBean.getContent());
        holder.tv_time.setText(TimeUtils.milliseconds2String(Long.parseLong(itemListBean.getCreateAt())));
        holder.image_delete.setVisibility(View.GONE);
        if (itemListBean.getStatus() == 0) {
            holder.image_dot.setVisibility(View.VISIBLE);
        } else {
            holder.image_dot.setVisibility(View.GONE);
//            holder.image_delete.setVisibility(View.VISIBLE);
        }
        if (position == mCont) {
            holder.image_delete.setVisibility(View.VISIBLE);
        }
        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCont = -1;
                if (mOnDeleteMessageLstener != null) {
                    mOnDeleteMessageLstener.OnDelete(data.get(position), position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (itemListBean.getStatus() == 0) {
                    data.get(position).setStatus(1);
                    if (mOnDeleteMessageLstener != null) {
                        mOnDeleteMessageLstener.redMsg(data.get(position), position);
                    }
                }
                if (mCont == position) {
                    mCont = -1;
                    notifyDataSetChanged();
                    return false;
                }
                mCont = position;
                notifyDataSetChanged();
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListBean.getStatus() == 0) {
                    data.get(position).setStatus(1);
                    notifyDataSetChanged();
                    if (mOnDeleteMessageLstener != null) {
                        mOnDeleteMessageLstener.redMsg(data.get(position), position);
                    }
                }
            }
        });

    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.text_content)
        TextView text_content;
        @BindView(R.id.image_dot)
        ImageView image_dot;
        @BindView(R.id.image_delete)
        ImageView image_delete;
        @BindView(R.id.tv_time)
        TextView tv_time;

        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnDeleteMessageLstener {
        void OnDelete(MessageBean.ItemListBean messageBean, int position);

        void redMsg(MessageBean.ItemListBean messageBean, int position);
    }

    private OnDeleteMessageLstener mOnDeleteMessageLstener;

    public void addDeleteMessageListener(OnDeleteMessageLstener onDeleteMessageLstener) {
        if (onDeleteMessageLstener != null) {
            this.mOnDeleteMessageLstener = onDeleteMessageLstener;
        }
    }

}
