/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gjmetal.app.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.helper.ItemTouchHelperAdapter;
import com.gjmetal.app.widget.helper.ItemTouchHelperViewHolder;
import com.gjmetal.app.widget.helper.OnStartDragListener;
import com.gjmetal.star.log.XLog;

import java.util.Collections;
import java.util.List;

/**
 * Description：已选
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-29 11:45
 */

public class MyChooseAdapter extends RecyclerView.Adapter<MyChooseAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
    private List<RoomItem> results;
    private ClickCallBack mCallBack;
    private OnStartDragListener mDragStartListener;
    private Context mContext;
    private long fromId;
    private int toSort;

    public MyChooseAdapter(Context context, OnStartDragListener dragStartListener, List<RoomItem> results, ClickCallBack callBack) {
        this.mDragStartListener = dragStartListener;
        this.results = results;
        this.mCallBack = callBack;
        this.mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_all_child, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final RoomItem exChange = results.get(position);
        if (position % 2 == 0) {
            holder.rlChoose.setBackgroundResource(R.drawable.shape_item_market_nor);
        } else {
            holder.rlChoose.setBackgroundResource(R.drawable.shape_item_market_res);
        }
        holder.tvMarketName.setText(ValueUtil.isStrNotEmpty(exChange.getName()) ? exChange.getName() : "");
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onDelete(position);
            }
        });
        holder.ivMarketToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onToTop(position);
            }
        });
        holder.ivMarketScoll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked()== MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return true;
            }
        });

    }

    @Override
    public void onItemDismiss(int position) {
        results.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        try {
            if (toPosition != results.size() && fromPosition != results.size() && fromPosition < results.size() && toPosition < results.size()) {
                this.fromId = results.get(fromPosition).getId();
                this.toSort = results.get(toPosition).getSort();
                Collections.swap(results, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public TextView tvMarketName;
        public ImageView ivDelete, ivMarketToTop, ivMarketScoll;
        public RelativeLayout rlChoose;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvMarketName = itemView.findViewById(R.id.tvMarketName);
            ivMarketToTop = itemView.findViewById(R.id.ivMarketToTop);
            ivMarketScoll = itemView.findViewById(R.id.ivMarketScoll);
            rlChoose = itemView.findViewById(R.id.rlChoose);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundResource(R.drawable.shape_move_selected_res);
        }

        @Override
        public void onItemClear() {
            XLog.d("onItemClear-----", "fromid=" + fromId + "/" + toSort);
            itemView.setBackgroundResource(R.drawable.shape_move_selected_res);
            notifyDataSetChanged();
            if (fromId != 0 && toSort != 0) {
                mCallBack.onMove(fromId, toSort);
            }
        }
    }

    public interface ClickCallBack {
        void onDelete(int index);

        void onToTop(int position);

        void onMove(long id, int toSort);
    }
}
