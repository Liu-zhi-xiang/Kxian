package com.gjmetal.app.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.search.MenuChoose;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.PointGridView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：行情菜单选择
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-25 11:47
 */

public class MenuChooseAdapter extends SimpleRecAdapter<MenuChoose, MenuChooseAdapter.ViewHolder> {
    private Context mContext;
    private BaseCallBack baseCallBack;
    public MenuChooseAdapter(Context context, BaseCallBack baseCallBack) {
        super(context);
        this.mContext = context;
        this.baseCallBack=baseCallBack;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_market_spinner;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int groupPosition) {
        final MenuChoose bean = data.get(groupPosition);
        holder.tvSpinerTag.setText(ValueUtil.isStrNotEmpty(bean.getGroupType()) ? bean.getGroupType() : "- -");
        holder.gvItem.setVisibility(ValueUtil.isListNotEmpty(bean.getMenuList()) ? View.VISIBLE : View.GONE);

        if (ValueUtil.isListNotEmpty(bean.getMenuList())) {
            final SpinnerGridViewAdapter spinnerGridViewAdapter = new SpinnerGridViewAdapter(context, bean.getMenuList());
            holder.gvItem.setAdapter(spinnerGridViewAdapter);
            holder.gvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    spinnerGridViewAdapter.onRefersh(position);
                    for (int i = 0; i < data.size(); i++) {
                        MenuChoose menuChoose = data.get(i);
                        if (i != groupPosition) {
                            for (Future future : menuChoose.getMenuList()) {
                                future.setSelected(false);
                            }
                        }
                    }
                    notifyDataSetChanged();

                    baseCallBack.back(bean.getMenuList().get(position));
                }
            });
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSpinerTag)
        TextView tvSpinerTag;
        @BindView(R.id.gvItem)
        PointGridView gvItem;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

}

