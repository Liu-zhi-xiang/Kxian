package com.gjmetal.app.adapter.my;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.my.Company;
import com.gjmetal.app.ui.my.auth.CompanySendApplyCode;
import com.gjmetal.app.util.ValueUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.droidlover.xrecyclerview.RecyclerAdapter;

/**
 * Description：企业成员
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-7  10:17
 */
public class CompanyListAdapter extends RecyclerAdapter<Company.DataListBean, CompanyListAdapter.ViewHodler> {
    public CompanyListAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHodler(LayoutInflater.from(context).inflate(R.layout.item_company_auth, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHodler holder, final int position) {
        final Company.DataListBean bean=data.get(position);
        if(ValueUtil.isStrNotEmpty(bean.getName())){
           holder.tvCompanyName.setText(bean.getName());
        }else {
            holder.tvCompanyName.setText("");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanySendApplyCode.launch((Activity) context,bean);
            }
        });
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCompanyName)
        TextView tvCompanyName;
        @BindView(R.id.ivToright)
        ImageView ivToright;

        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
