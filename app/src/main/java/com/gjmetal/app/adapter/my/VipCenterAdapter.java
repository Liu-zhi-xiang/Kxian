package com.gjmetal.app.adapter.my;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.my.VipService;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：会员中心列表
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-24 11:32
 */

public class VipCenterAdapter extends SimpleRecAdapter<VipService, VipCenterAdapter.ViewHolder> {
    private Context mContext;

    public VipCenterAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_vip_center;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final VipService bean = data.get(position);
        holder.tvTop.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

        holder.tvServiceName.setText(ValueUtil.isStrNotEmpty(bean.getName()) ? bean.getName() : "- -");
        //会员使用中
        if (bean.getStatus().equals(Constant.VipStatus.INUSE.getValue())) {
            holder.tvServiceHasTime.setVisibility(View.VISIBLE);
            holder.tvServiceTime.setVisibility(View.VISIBLE);
            holder.tvServiceNoTime.setVisibility(View.GONE);
            holder.tvCallService.setVisibility(View.GONE);
            holder.tvServiceHasTime.setText("剩" + bean.getRemainDays() + "天");
            holder.tvServiceTime.setText(bean.getExpireDate() + " 到期");
        } else if (bean.getStatus().equals(Constant.VipStatus.EXPIRED.getValue())) {
            holder.tvServiceHasTime.setVisibility(View.GONE);
            holder.tvServiceTime.setVisibility(View.GONE);
            holder.tvServiceNoTime.setVisibility(View.VISIBLE);
            holder.tvCallService.setVisibility(View.VISIBLE);
            holder.tvServiceNoTime.setText("已过期");
            holder.tvCallService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ValueUtil.isStrEmpty(bean.getPhone())){
                        return;
                    }
                    new HintDialog(context, "确认拨打客服电话吗？", new DialogCallBack() {
                        @Override
                        public void onSure() {
                            PhoneUtil.makePhone(context, bean.getPhone());
                        }

                        @Override
                        public void onCancel() {

                        }
                    }).show();
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTop)
        TextView tvTop;
        @BindView(R.id.tvServiceName)
        TextView tvServiceName;
        @BindView(R.id.tvServiceHasTime)
        TextView tvServiceHasTime;
        @BindView(R.id.tvServiceTime)
        TextView tvServiceTime;
        @BindView(R.id.tvServiceNoTime)
        TextView tvServiceNoTime;
        @BindView(R.id.tvCallService)
        TextView tvCallService;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
}

