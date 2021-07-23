package com.gjmetal.app.adapter.spot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.SpotDetailReport;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.star.kchart.utils.DisplayUtil;

import java.math.BigDecimal;

import butterknife.BindView;

/**
 * Description：持仓分析详情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-27 10:33
 */

public class SpotAnalysisDetailAdapter extends SimpleRecAdapter<SpotDetailReport, SpotAnalysisDetailAdapter.ViewHolder> {
    private Context mContext;
    private int mScreenWidth = 0;
    private int maxValue;

    public SpotAnalysisDetailAdapter(Context context, int screenWidth) {
        super(context);
        this.mContext = context;
        this.mScreenWidth = screenWidth;

    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_spot_analysis_detail;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SpotDetailReport bean = data.get(position);
        holder.tvName.setText(ValueUtil.isStrNotEmpty(bean.getName()) ? bean.getName() : "- -");
        holder.tvProgressAdd.setText(bean.getChangeValue() > 0 ? ("+" + bean.getChangeValue()) : String.valueOf(bean.getChangeValue()));
        int value = Math.abs(bean.getValue());
        int change = Math.abs(bean.getChangeValue());

        double valuePer = new BigDecimal((float) value / maxValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double changePer = new BigDecimal((float) change / maxValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        int nowWidth = (int) (mScreenWidth * valuePer);//总量比
        int addWidth = (int) (mScreenWidth * changePer);//变化比
        RelativeLayout.LayoutParams layoutParamsNow = (RelativeLayout.LayoutParams) holder.vNowProgress.getLayoutParams();
        layoutParamsNow.width = nowWidth;
        if(layoutParamsNow.width==0){
            if (bean.getValue() < 10) {
                layoutParamsNow.width = DisplayUtil.dip2px(context,1);
            } else if (bean.getValue() > 9 && bean.getValue() < 50) {
                layoutParamsNow.width = DisplayUtil.dip2px(context,2);
            } else if (bean.getValue() > 49 && bean.getValue() < 100) {
                layoutParamsNow.width = DisplayUtil.dip2px(context,3);
            } else {
                layoutParamsNow.width = DisplayUtil.dip2px(context,4);
            }
        }
//        XLog.d("widthValue屏幕layoutParamsNow", nowWidth+"/"+addWidth+"/value="+bean.getValue()+"/changeValue="+bean.getChangeValue());
        holder.vNowProgress.setLayoutParams(layoutParamsNow);
        RelativeLayout.LayoutParams linearParamsAdd = (RelativeLayout.LayoutParams) holder.vProgressAdd.getLayoutParams();//增
        RelativeLayout.LayoutParams layoutParamsSub = (RelativeLayout.LayoutParams) holder.vProgressSub.getLayoutParams();//减
        if (Math.abs(bean.getChangeValue()) > 0 && addWidth == 0) {
            if (bean.getChangeValue() < 10) {
                linearParamsAdd.width = 1;
                layoutParamsSub.width = 1;
            } else if (bean.getChangeValue() > 9 && bean.getChangeValue() < 50) {
                linearParamsAdd.width = 2;
                layoutParamsSub.width = 2;
            } else if (bean.getChangeValue() > 49 && bean.getChangeValue() < 100) {
                linearParamsAdd.width = 3;
                layoutParamsSub.width = 3;
            } else {
                linearParamsAdd.width = 4;
                layoutParamsSub.width = 4;
            }
        } else {
            linearParamsAdd.width = addWidth;
            layoutParamsSub.width = addWidth;
        }
        XLog.d("addWidth",addWidth+"/"+bean.getValue());
        holder.tvProgressAddValue.setVisibility(View.VISIBLE);
        holder.tvProgressAddValue.setText(String.valueOf(bean.getValue()));
        if (bean.getChangeValue() == 0) {
            linearParamsAdd.width = 0;
            holder.vProgressSub.setVisibility(View.GONE);
            holder.vProgressAdd.setVisibility(View.GONE);
        } else if (bean.getChangeValue() > 0) {//涨
            holder.vProgressAdd.setLayoutParams(linearParamsAdd);
            holder.vProgressSub.setVisibility(View.GONE);
            holder.vProgressAdd.setVisibility(View.VISIBLE);
            holder.vProgressAdd.setBackgroundColor(getColor(R.color.cFF5252));
        } else {//跌
            holder.vProgressSub.setLayoutParams(layoutParamsSub);
            holder.vProgressSub.setVisibility(View.VISIBLE);
            holder.vProgressAdd.setVisibility(View.GONE);
            holder.vProgressSub.setBackgroundColor(getColor(R.color.c35CB6B));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        AutofitTextView tvName;
        @BindView(R.id.vNowProgress)
        View vNowProgress;
        @BindView(R.id.vProgressAdd)
        View vProgressAdd;
        @BindView(R.id.vProgressSub)
        View vProgressSub;
        @BindView(R.id.tvProgressAdd)
        AutofitTextView tvProgressAdd;
        @BindView(R.id.tvProgressAddValue)
        AutofitTextView tvProgressAddValue;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

}

