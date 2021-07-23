package com.gjmetal.app.adapter.market;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.gjmetal.app.R;
import com.gjmetal.app.model.market.Explain;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：
 * Author: chenshanshan
 * Email: 1175558532@qq.com
 * Date: 2018-10-12  14:22
 */
public class ExplainDialogAdapter extends SimpleRecAdapter<Explain, ExplainDialogAdapter.MyViewHolder> {

    public ExplainDialogAdapter(Context context) {
        super(context);
    }

    @Override
    public MyViewHolder newViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_explain;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvName.setText(data.get(position).getKey());
        holder.tvValue.setText(data.get(position).getValue());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvValue)
        TextView tvValue;

        public MyViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
}
