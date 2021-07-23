package com.gjmetal.app.adapter.market;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.market.change.AddMarketTagActivity;
import com.gjmetal.app.ui.market.chart.ExchangeChartActivity;
import com.gjmetal.app.ui.market.chart.MarketChartActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;

import butterknife.BindView;

/**
 * Description：行情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-29 15:56
 */
public class FutureAdapter extends SimpleRecAdapter<RoomItem, FutureAdapter.ViewHolder> {
    private Context mContext;
    private int mCheckUpOrDown;//默认显示涨跌
    private int mCheckVolume;//默认显示成交量
    private CallBackLongClickLister callBackLongClickLister;
    private boolean mFav;//自选
    private Future mFuture;
    private String type;

    public FutureAdapter(Context context, String type, int checkUpOrDown, int checkVolume, Future future, CallBackLongClickLister clickLister) {
        super(context);
        this.mContext = context;
        this.mFuture = future;
        this.mFav = (future.getType() != null && future.getId() == -1 && future.getType().equals("2")) ? true : false;
        this.type = type;
        this.mCheckUpOrDown = checkUpOrDown;
        this.mCheckVolume = checkVolume;
        this.callBackLongClickLister = clickLister;
    }

    public void changeItem(int checkUpOrDown, int checkVolume) {
        this.mCheckUpOrDown = checkUpOrDown;
        this.mCheckVolume = checkVolume;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_future_view;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RoomItem bean = data.get(position);
        holder.tvFutureName.setText(ValueUtil.isStrNotEmpty(bean.getName()) ? bean.getName() : "");
        GjUtil.setNoDataShow(mContext, holder.tvFutureBestNew, bean.getLast());
        GjUtil.setNoDataShow(mContext, holder.tvFutureUpOrDown, bean.getUpdown());
        if (position % 2 == 0) {
            holder.llItem.setBackgroundResource(R.drawable.shape_item_market_res_selector);
        } else {
            holder.llItem.setBackgroundResource(R.drawable.shape_item_market_nor_selector);
        }

        //自选管理
        if (mFav && position == data.size() - 1) {
            holder.tvMyExChange.setVisibility(View.VISIBLE);
            holder.tvMyExChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.getInstance().isLoginIng()) {
                        String strJson = GsonUtil.toJson(data);
                        SharedUtil.put(Constant.CHANGE_DATA, Constant.HAS_CHNAGE_LIST, strJson);//保存选中数据
                        AddMarketTagActivity.launch((Activity) mContext, true);
                    } else {
                        GjUtil.closeMarketTimer();
                        LoginActivity.launch((Activity) mContext);
                    }
                }
            });
        } else {
            holder.tvMyExChange.setVisibility(View.GONE);
        }
        if (type.equals(Constant.MenuType.FIVE.getValue())) {//利率
            holder.tvFutureVolume.setVisibility(View.GONE);
            holder.tvVolume.setVisibility(View.GONE);
            //涨跌
            GjUtil.showTextStyle(mContext, holder.tvFutureUpOrDown, bean.getUpdown(), bean.getUpdown(), holder.tvFutureBestNew, holder.tvFutureUpOrDown, holder.tvInterest);
            //涨幅
            GjUtil.showTextStyle(mContext, holder.tvInterest, bean.getPercent(), bean.getUpdown(), holder.tvFutureBestNew, holder.tvFutureUpOrDown, holder.tvInterest);
            holder.tvFutureUpOrDown.setVisibility(View.VISIBLE);
            holder.tvInterest.setVisibility(View.VISIBLE);
        } else {
            String volume=ValueUtil.formatDouble(bean.getVolume());
            String interest=ValueUtil.formatDouble(bean.getInterest());
            GjUtil.setNoDataShow(mContext, holder.tvVolume, volume);//成交量
            GjUtil.setNoDataShow(mContext, holder.tvInterest, interest);//持仓量
            holder.tvFutureUpOrDown.setVisibility(View.GONE);
            if (mCheckUpOrDown == 0) {//涨跌
                holder.tvFutureUpOrDown.setVisibility(View.GONE);
                holder.tvFutureVolume.setVisibility(View.VISIBLE);
                GjUtil.showTextStyle(mContext, holder.tvFutureVolume, bean.getUpdown(), bean.getUpdown(),holder.tvFutureBestNew, holder.tvFutureUpOrDown, holder.tvFutureVolume);
            } else {//涨幅
                holder.tvFutureUpOrDown.setVisibility(View.VISIBLE);
                holder.tvFutureVolume.setVisibility(View.GONE);
                GjUtil.showTextStyle(mContext, holder.tvFutureUpOrDown, bean.getPercent(), bean.getUpdown(),holder.tvFutureBestNew,holder.tvFutureUpOrDown, holder.tvFutureVolume);
            }
            if (mCheckVolume == 0) {//成交量
                holder.tvInterest.setVisibility(View.GONE);
                holder.tvVolume.setVisibility(View.VISIBLE);
            } else {
                holder.tvInterest.setVisibility(View.VISIBLE);
                holder.tvVolume.setVisibility(View.GONE);
            }
            if (bean.getState() != null && bean.getState() == 1) {//数据发生变化
                holder.tvFutureBestNew.setTextColor(getColor(R.color.cF8E71C));
            }
            if (ValueUtil.isStrNotEmpty(bean.getLast()) && !bean.getLast().equals("- -")) {
                if (ValueUtil.isStrNotEmpty(bean.getLoLimit()) && !bean.getLoLimit().equals("- -") && bean.getLoLimit().equals(bean.getLast())) {//跌
                    holder.tvFutureBestNew.setTextColor(getColor(R.color.cffffff));
                    holder.tvFutureBestNew.setBackgroundColor(getColor(R.color.c35CB6B));
                }else if (ValueUtil.isStrNotEmpty(bean.getUpLimit()) && !bean.getUpLimit().equals("- -") && bean.getUpLimit().equals(bean.getLast())) {//涨
                    holder.tvFutureBestNew.setTextColor(getColor(R.color.cffffff));
                    holder.tvFutureBestNew.setBackgroundColor(getColor(R.color.cFF5252));
                }else {
                    holder.tvFutureBestNew.setBackgroundColor(getColor(R.color.transparent));
                }
            }else {
                holder.tvFutureBestNew.setBackgroundColor(getColor(R.color.transparent));
            }

        }
        holder.llItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mFav) {
                    callBackLongClickLister.OnLongClick(v, position, bean);
                }
                return false;
            }
        });
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValueUtil.isStrEmpty(bean.getContract())) {
                    return;
                }
                if (type.equals(Constant.MenuType.FIVE.getValue())) {
                    ExchangeChartActivity.launch((Activity) context, bean);
                } else {
                    MarketChartActivity.launch((Activity) context, bean);
                }
                AppAnalytics.getInstance().onEvent(mContext, "market_" + mFuture.getId() + "_" + bean.getContract() + "_contracts_acess", "行情-各交易所-合约点击量");
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFutureName)
        AutofitTextView tvFutureName;
        @BindView(R.id.tvFutureBestNew)
        AutofitTextView tvFutureBestNew;
        @BindView(R.id.tvFutureUpOrDown)
        AutofitTextView tvFutureUpOrDown;
        @BindView(R.id.tvFutureVolume)
        AutofitTextView tvFutureVolume;
        @BindView(R.id.llItem)
        LinearLayout llItem;
        @BindView(R.id.tvVolume)
        AutofitTextView tvVolume;
        @BindView(R.id.tvInterest)
        AutofitTextView tvInterest;
        @BindView(R.id.tvMyExChange)
        TextView tvMyExChange;//自选管理

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    public interface CallBackLongClickLister {
        void OnLongClick(View view, int position, RoomItem futureItem);

    }
}

