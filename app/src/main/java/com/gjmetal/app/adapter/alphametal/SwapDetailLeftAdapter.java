package com.gjmetal.app.adapter.alphametal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description 调期费详情 left
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-12 10:36
 */

public class SwapDetailLeftAdapter extends SimpleRecAdapter<String, SwapDetailLeftAdapter.MyViewHolder> {

    public SwapDetailLeftAdapter(Context context) {
        super(context);
    }


    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.swap_detail_left_item;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.selector_item_btn3);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.selector_item_btn4);
        }

        if (ValueUtil.isStrNotEmpty(data.get(position))) {
            holder.tvContractName.setText(data.get(position));
        } else {
            holder.tvContractName.setText("- -");
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvContractName)
        AutofitTextView tvContractName;

        public MyViewHolder(View v) {
            super(v);
            KnifeKit.bind(this, v);

        }
    }


}
