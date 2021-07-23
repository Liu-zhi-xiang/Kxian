package com.gjmetal.app.adapter.market;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.market.chart.ExchangeChartActivity;
import com.gjmetal.app.ui.market.chart.MarketChartActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TextUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.base.SimpleRecAdapter;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：搜索
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-2 9:41
 */
public class MarketSearchAdapter extends SimpleRecAdapter<RoomItem, MarketSearchAdapter.ViewHolder> {
    private Context mContext;
    private String mKey;
    private BaseCallBack mBaseCallBack;

    public MarketSearchAdapter(Context context, String key, BaseCallBack baseCallBack) {
        super(context);
        this.mContext = context;
        this.mKey = key;
        this.mBaseCallBack = baseCallBack;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_market_search;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RoomItem item = data.get(position);
        if (ValueUtil.isStrNotEmpty(item.getName())) {
            holder.tvMarketName.setText(Html.fromHtml(TextUtil.matcherSearchTitle(item.getName(), mKey)));
        } else {
            holder.tvMarketName.setText("");
        }
        if(ValueUtil.isStrNotEmpty(item.getType())&&item.getType().equals(Constant.MenuType.FIVE.getValue())){
            holder.ivAddChange.setVisibility(View.GONE);
        }else {
            holder.ivAddChange.setVisibility(View.VISIBLE);
        }
        holder.ivAddChange.setBackgroundResource(item.isCheck() ? R.mipmap.iv_chart_cancel_plus : R.mipmap.iv_chart_add_plus);
        holder.ivAddChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getInstance().isLoginIng()) {
                    if (item.isCheck()) {
                        List<Integer> longList = new ArrayList<>();
                        longList.add(item.getId());
                        delFavoritesCode(longList, item);
                    } else {
                        addFileFavoritesCode(item.getType(), item.getContract(), item);
                    }
                } else {
                    LoginActivity.launch((Activity) context);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValueUtil.isStrEmpty(item.getContract())||ValueUtil.isStrEmpty(item.getType())) {
                    return;
                }
                if (item.getType().equals(Constant.MenuType.FIVE.getValue())) {//利率
                    ExchangeChartActivity.launch((Activity) context, item);
                } else {
                    MarketChartActivity.launch((Activity) context, item);//原生K线
                }
            }
        });
    }

    public void setSearchKey(String key) {
        this.mKey = key;
        notifyDataSetChanged();
    }


    /**
     * 添加自选
     *
     * @param typeId
     * @param codeId
     */
    public void addFileFavoritesCode(String typeId, String codeId, final RoomItem item) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().addFileFavoritesCode(typeId, codeId)
                .compose(XApi.<BaseModel<RoomItem>>getApiTransformer())
                .compose(XApi.<BaseModel<RoomItem>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<RoomItem>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onNext(BaseModel<RoomItem> listBaseModel) {
                        DialogUtil.dismissDialog();
                        item.setCheck(!item.isCheck());
                        mBaseCallBack.back(item);
                        notifyDataSetChanged();
                        GjUtil.onRefreshMarket();
                        ToastUtil.showToast("添加成功");
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                    }
                });
    }

    /**
     * 删除自选
     *
     * @param list
     */
    public void delFavoritesCode(List<Integer> list, final RoomItem item) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().delFavoritesCode(list)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        ToastUtil.showToast("取消成功");
                        DialogUtil.dismissDialog();
                        item.setCheck(!item.isCheck());
                        mBaseCallBack.back(item);
                        notifyDataSetChanged();
                        GjUtil.onRefreshMarket();

                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                    }
                });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMarketName)
        TextView tvMarketName;
        @BindView(R.id.ivAddChange)
        ImageView ivAddChange;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
}

