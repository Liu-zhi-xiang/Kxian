package com.gjmetal.app.adapter.spot.expand;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.BaseWebViewActivity;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotItems;
import com.gjmetal.app.model.spot.SpotStock;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.information.InformationWebViewActivity;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.spot.SpotChildFragment;
import com.gjmetal.app.ui.spot.SpotDetailOfferListActivity;
import com.gjmetal.app.ui.spot.SpotStockActivity;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyViewPager;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.app.widget.kline.SpotPositionAnalysisView;
import com.gjmetal.app.widget.kline.SpotPriceView;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Description：现货
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-27 17:34
 */

public class SpotExpandAdapter implements ExpandAdapterInterface {
    private Context mContext;
    private HashMap<Integer, String> mGroups = new HashMap<>();
    private SparseArray<List<Spot.PListBean>> mGroupData = new SparseArray<>();
    private int mCurrentGroup = -1;
    private Spot mSpot;
    private MyViewPager vPagerview;
    private OnClickSpotItem onClickMoreItem = null;

    public void setOnClickSpotItem(OnClickSpotItem onClickMoreItem) {
        this.onClickMoreItem = onClickMoreItem;
    }

    public interface OnClickSpotItem {
        void setOnClickSpotItemData(View view, List<Spot.PListBean> children, int childPosition);

        void setOnPositionAnalysisClick(List<Spot.PListBean> children, int childPosition);

        void onRefresh();
    }

    public SpotExpandAdapter(Context context, Spot spot, MyViewPager vPagerview) {
        this.mSpot = spot;
        this.mContext = context;
        this.vPagerview = vPagerview;
    }

    class GroupHolder {
        ImageView ivExplain;
        TextView tvSpotQuotation, tvNameOrPrice, tvMeanPrice, tvGronpUpDown, tvSpotAnalysisDetail;//持仓分析详情
        LinearLayout llspotItemTitter;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupPosition < 0 || !mGroups.containsKey(groupPosition)) {
            return null;
        }
        GroupHolder groupHolder = null;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_spot_view_item, parent, false);
            groupHolder.tvSpotQuotation = convertView.findViewById(R.id.tvSpotQuotation);
            groupHolder.tvNameOrPrice = convertView.findViewById(R.id.tvNameOrPrice);
            groupHolder.tvMeanPrice = convertView.findViewById(R.id.tvMeanPrice);
            groupHolder.tvGronpUpDown = convertView.findViewById(R.id.tvGronpUpDown);
            groupHolder.llspotItemTitter = convertView.findViewById(R.id.llspotItemTitter);
            groupHolder.ivExplain = convertView.findViewById(R.id.ivExplain);
            groupHolder.tvSpotAnalysisDetail = convertView.findViewById(R.id.tvSpotAnalysisDetail);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (ValueUtil.isEmpty(mSpot) && ValueUtil.isStrEmpty(mSpot.getType())) {
            return null;
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_PRICE)) {
            groupHolder.ivExplain.setTag(mSpot.getDescUrl());
            groupHolder.tvSpotQuotation.setText(mContext.getResources().getString(R.string.spotquotation));
            groupHolder.tvNameOrPrice.setText(mContext.getResources().getString(R.string.nameorprice));
            groupHolder.tvMeanPrice.setText(mContext.getResources().getString(R.string.averageprice));
            groupHolder.tvGronpUpDown.setText(mContext.getResources().getString(R.string.upanddown));
            groupHolder.ivExplain.setVisibility(View.VISIBLE);
            groupHolder.llspotItemTitter.setVisibility(View.VISIBLE);
            groupHolder.tvSpotAnalysisDetail.setVisibility(View.GONE);
            clickSpotDesc(groupHolder);
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_STOCK)) {
            groupHolder.tvSpotAnalysisDetail.setVisibility(View.GONE);
            groupHolder.tvSpotQuotation.setText(mContext.getResources().getString(R.string.stock));
            groupHolder.tvNameOrPrice.setText(mContext.getResources().getString(R.string.source));
            groupHolder.tvMeanPrice.setText(mContext.getResources().getString(R.string.num));
            groupHolder.tvGronpUpDown.setText(mContext.getResources().getString(R.string.increaseordecrease));
            groupHolder.llspotItemTitter.setVisibility(View.VISIBLE);
            groupHolder.ivExplain.setVisibility(View.GONE);
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_ANALYSIS)) {
            groupHolder.tvSpotQuotation.setText(mContext.getResources().getString(R.string.positionAnalysis));
            groupHolder.ivExplain.setVisibility(View.GONE);
            groupHolder.llspotItemTitter.setVisibility(View.GONE);
            groupHolder.tvSpotAnalysisDetail.setVisibility(View.VISIBLE);
            clickSpotAnalySisDetail(groupHolder);
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_NEWS)) {
            groupHolder.tvSpotQuotation.setText(mContext.getResources().getString(R.string.relevantinformation));
            groupHolder.ivExplain.setVisibility(View.GONE);
            groupHolder.llspotItemTitter.setVisibility(View.GONE);
            groupHolder.tvSpotAnalysisDetail.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ChildHolder1 {//现货报价
        TextView tvNameSpotItem, tvTimeSpotItem, tvSpotContactName, tvSpotUnit, tvVipRead;
        LinearLayout lltSpotQuotation, linearlayout_content, llSpotBg,llExpand;
        TextView tvTitleTip, tvDate, tvPrice, tvNameTitle, tvMore;
        ImageView ivLoad;
        SpotPriceView spotPriceView;
        EmptyView vEmpty, vPEmpty;
        AutofitTextView tvHeightAndLowPriceSpotItem,tvAveragepriceSpotItem,tvUpanddownSpotItem;
    }

    class ChildHolder2 {//库存
        LinearLayout llSoptItem;
        EmptyView vEmpty;
        TextView tvFromSpotStock,tvTimeAndData;
        AutofitTextView tvSpotStockNum,tvSpotStockIncreaseOrDecrease;
    }

    class ChildHolder3 {//持仓分析
        TextView tvVipVolume;
        AutofitTextView tvShfe, tvShfeLme;
        SpotPositionAnalysisView kVolume;
        LinearLayout llAnalysisHint;
        EmptyView vEmpty;
    }

    class ChildHolder4 {//资讯
        TextView tvSpotNewsSouce, tvSpotNewsTitle, tvSpotNewsTime, tvVip;
        LinearLayout llNews;
        EmptyView vEmpty;
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final List<Spot.PListBean> children = mGroupData.get(groupPosition);
        if (children == null || childPosition < 0 || childPosition > children.size()) {
            return null;
        }
        View v = null;
        ChildHolder1 childHolder1 = null;
        ChildHolder2 childHolder2 = null;
        ChildHolder3 childHolder3 = null;
        ChildHolder4 childHolder4 = null;
        if (convertView == null) {
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_PRICE)) {
                childHolder1 = new ChildHolder1();
                v = LayoutInflater.from(mContext).inflate(R.layout.item_spotquotation_view, null);
                childHolder1.llExpand = v.findViewById(R.id.llExpand);
                childHolder1.linearlayout_content = v.findViewById(R.id.linearlayout_content);
                childHolder1.lltSpotQuotation = v.findViewById(R.id.lltSpotQuotation);
                childHolder1.tvTitleTip = v.findViewById(R.id.tvTitleTip);
                childHolder1.tvDate = v.findViewById(R.id.tvDate);
                childHolder1.tvPrice = v.findViewById(R.id.tvPrice);
                childHolder1.tvNameTitle = v.findViewById(R.id.tvNameTitle);
                childHolder1.tvMore = v.findViewById(R.id.tvMore);
                childHolder1.ivLoad = v.findViewById(R.id.ivLoad);
                childHolder1.spotPriceView = v.findViewById(R.id.spotPriceView);
                childHolder1.vEmpty = v.findViewById(R.id.vEmpty);
                childHolder1.tvUpanddownSpotItem = v.findViewById(R.id.tvUpanddownSpotItem);
                childHolder1.tvAveragepriceSpotItem = v.findViewById(R.id.tvAveragepriceSpotItem);
                childHolder1.tvNameSpotItem = v.findViewById(R.id.tvNameSpotItem);
                childHolder1.tvHeightAndLowPriceSpotItem = v.findViewById(R.id.tvHeightAndLowPriceSpotItem);
                childHolder1.tvSpotContactName = v.findViewById(R.id.tvSpotContactName);
                childHolder1.tvTimeSpotItem = v.findViewById(R.id.tvTimeSpotItem);
                childHolder1.tvSpotUnit = v.findViewById(R.id.tvSpotUnit);
                childHolder1.llSpotBg = v.findViewById(R.id.llSpotBg);
                childHolder1.vPEmpty = v.findViewById(R.id.vPEmpty);
                childHolder1.tvVipRead = v.findViewById(R.id.tvVipRead);
                v.setTag(childHolder1);
            }
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_STOCK)) {
                childHolder2 = new ChildHolder2();
                v = LayoutInflater.from(mContext).inflate(R.layout.item_spot_stock_view, null);
                childHolder2.llSoptItem = v.findViewById(R.id.llSoptItem);
                childHolder2.tvTimeAndData = v.findViewById(R.id.tvTimeAndData);
                childHolder2.tvSpotStockIncreaseOrDecrease = v.findViewById(R.id.tvSpotStockIncreaseOrDecrease);
                childHolder2.tvSpotStockNum = v.findViewById(R.id.tvSpotStockNum);
                childHolder2.tvFromSpotStock = v.findViewById(R.id.tvFromSpotStock);
                childHolder2.vEmpty = v.findViewById(R.id.vEmpty);
                v.setTag(childHolder2);
            }
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_ANALYSIS)) {
                childHolder3 = new ChildHolder3();
                v = LayoutInflater.from(mContext).inflate(R.layout.item_spot_position_analysis_view, null);
                childHolder3.tvVipVolume = v.findViewById(R.id.tvVipVolume);
                childHolder3.tvShfe = v.findViewById(R.id.tvShfe);
                childHolder3.tvShfeLme = v.findViewById(R.id.tvShfeLme);
                childHolder3.kVolume = v.findViewById(R.id.kVolume);
                childHolder3.llAnalysisHint = v.findViewById(R.id.llAnalysisHint);
                childHolder3.vEmpty = v.findViewById(R.id.vEmpty);
                v.setTag(childHolder3);
            }
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_NEWS)) {
                childHolder4 = new ChildHolder4();
                v = LayoutInflater.from(mContext).inflate(R.layout.item_spot_relevantinformation_view, null);
                childHolder4.tvSpotNewsTime = v.findViewById(R.id.tvSpotNewsTime);
                childHolder4.tvSpotNewsTitle = v.findViewById(R.id.tvSpotNewsTitle);
                childHolder4.tvSpotNewsSouce = v.findViewById(R.id.tvSpotNewsSouce);
                childHolder4.llNews = v.findViewById(R.id.llNews);
                childHolder4.tvVip = v.findViewById(R.id.tvVip);
                childHolder4.vEmpty = v.findViewById(R.id.vEmpty);
                v.setTag(childHolder4);
            }
        } else {
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_PRICE)) {
                v = convertView;
                childHolder1 = (ChildHolder1) v.getTag();
            }
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_STOCK)) {
                v = convertView;
                childHolder2 = (ChildHolder2) v.getTag();
            }
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_ANALYSIS)) {
                v = convertView;
                childHolder3 = (ChildHolder3) v.getTag();
            }
            if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_NEWS)) {
                v = convertView;
                childHolder4 = (ChildHolder4) v.getTag();
            }
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_PRICE)) {
            spotPriceEvent(childHolder1, childPosition, children);
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_STOCK)) {
            stockEvent(childHolder2, childPosition, children);
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_ANALYSIS)) {
            positionAnalysisEvent(childHolder3, childPosition, children);
        }
        if (mGroups.get(groupPosition).equals(SpotChildFragment.SPOT_NEWS)) {
            informationEvent(childHolder4, childPosition, children);
        }
        return v;

    }


    /**
     * 现货报价
     *
     * @param childHolder1
     * @param childPosition
     * @param children
     */
    private void spotPriceEvent(final ChildHolder1 childHolder1, final int childPosition, final List<Spot.PListBean> children) {
        if (children.get(0).isParentError()) {//最外层接口报错时
            childHolder1.vPEmpty.setVisibility(View.VISIBLE);
            childHolder1.llSpotBg.setVisibility(View.GONE);
            GjUtil.showEmptyHint(mContext, Constant.BgColor.BLUE, children.get(0).getNetError(), childHolder1.vPEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    onClickMoreItem.onRefresh();
                }
            });
            return;
        }
        childHolder1.vPEmpty.setVisibility(View.GONE);
        childHolder1.llSpotBg.setVisibility(View.VISIBLE);

        childHolder1.spotPriceView.setVisibility(View.GONE);
        childHolder1.spotPriceView.setLongPress(false);
        if (children.get(childPosition).isOpen()) {
            childHolder1.ivLoad.setVisibility(View.VISIBLE);
            //加载动画资源
            AnimationDrawable animationDrawable = (AnimationDrawable) childHolder1.ivLoad.getDrawable();
            animationDrawable.start();
            List<ChooseData> klistBaseMode3 = children.get(childPosition).getKlistBaseMode();
            if (children.get(childPosition).isRequsted()) {
                // 清除动画
                animationDrawable.stop();
                childHolder1.ivLoad.clearAnimation();
                childHolder1.ivLoad.setVisibility(View.GONE);
                childHolder1.spotPriceView.setRefreshData(children.get(childPosition).isOpen(), klistBaseMode3);//设置展开绘制K线
            }
            if (children.get(childPosition).isError()) {
                childHolder1.tvMore.setVisibility(View.VISIBLE);
                NetError error = children.get(childPosition).getNetError();
                childHolder1.vEmpty.setVisibility(View.VISIBLE);
                childHolder1.tvVipRead.setVisibility(View.GONE);
                childHolder1.spotPriceView.setVisibility(View.GONE);
                childHolder1.vEmpty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onClickMoreItem != null) {
                            onClickMoreItem.setOnClickSpotItemData(view, children, childPosition);
                        }
                    }
                });
                childHolder1.tvVipRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickMoreItem != null) {
                            onClickMoreItem.setOnClickSpotItemData(v, children, childPosition);
                        }
                    }
                });
                if (error == null) {
                    childHolder1.vEmpty.setNoData(Constant.BgColor.BLUE);
                } else {
                    if (children.get(childPosition).isEmpty()) {//空数据
                        childHolder1.vEmpty.setNoData(Constant.BgColor.BLUE);
                    } else {
                        ReadPermissionsManager.checkCode(mContext, error, new ReadPermissionsManager.CodeEventListenter() {
                            @Override
                            public void onNetError() {
                                childHolder1.vEmpty.setVisibility(View.VISIBLE);
                                childHolder1.vEmpty.setOnNetError(Constant.BgColor.BLUE);
                                childHolder1.tvVipRead.setVisibility(View.GONE);
                                childHolder1.spotPriceView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFail() {
                                childHolder1.vEmpty.setVisibility(View.VISIBLE);
                                childHolder1.vEmpty.setOnError(Constant.BgColor.BLUE);
                                childHolder1.tvVipRead.setVisibility(View.GONE);
                                childHolder1.spotPriceView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLogin() {
                            }

                            @Override
                            public void onShowDialog() {
                                childHolder1.vEmpty.setVisibility(View.GONE);
                                childHolder1.tvVipRead.setVisibility(View.VISIBLE);
                                childHolder1.tvMore.setVisibility(View.GONE);
                                childHolder1.spotPriceView.setVisibility(View.GONE);
                            }
                        });
                    }

                }
            } else {
                childHolder1.tvVipRead.setVisibility(View.GONE);
                childHolder1.spotPriceView.setVisibility(View.VISIBLE);
                childHolder1.tvMore.setVisibility(View.VISIBLE);
                childHolder1.vEmpty.setVisibility(View.GONE);
            }
        } else {
            childHolder1.ivLoad.clearAnimation();
            childHolder1.ivLoad.setVisibility(View.GONE);
        }
        childHolder1.lltSpotQuotation.setVisibility(children.get(childPosition).isOpen() ? View.VISIBLE : View.GONE);
        childHolder1.tvNameSpotItem.setText(children.get(childPosition).getName());
        GjUtil.setUporDownColor(mContext, childHolder1.tvUpanddownSpotItem, children.get(childPosition).getChange());
        GjUtil.setUporDownColor(mContext, childHolder1.tvHeightAndLowPriceSpotItem, children.get(childPosition).getChange());
        GjUtil.setUporDownColor(mContext, childHolder1.tvAveragepriceSpotItem, children.get(childPosition).getChange());

        childHolder1.tvTimeSpotItem.setText(children.get(childPosition).getPublishDate() + " " + children.get(childPosition).getPublishTime());
        if (ValueUtil.isStrNotEmpty(children.get(childPosition).getUnit())) {
            childHolder1.tvSpotUnit.setVisibility(View.VISIBLE);
            childHolder1.tvSpotUnit.setText(ValueUtil.isStrNotEmpty(children.get(childPosition).getUnit()) ? children.get(childPosition).getUnit() : "");
        } else {
            childHolder1.tvSpotUnit.setVisibility(View.GONE);
        }
        childHolder1.llSpotBg.setBackgroundResource(childPosition % 2 == 0 ? R.color.c1E3A65 : R.color.c25345B);
        boolean referTo = children.get(childPosition).isPremium();
        if (referTo) {
            childHolder1.tvSpotContactName.setVisibility(View.VISIBLE);
            childHolder1.tvSpotContactName.setText(children.get(childPosition).getContract());//合约名
            childHolder1.tvAveragepriceSpotItem.setText(GjUtil.spotText(mContext, children.get(childPosition).getMiddle()));
            childHolder1.tvHeightAndLowPriceSpotItem.setText(GjUtil.spotText(mContext, children.get(childPosition).getLow()) + " - " + GjUtil.spotText(mContext, children.get(childPosition).getHigh()));
        } else {
            childHolder1.tvSpotContactName.setVisibility(View.GONE);
            childHolder1.tvAveragepriceSpotItem.setText(children.get(childPosition).getMiddle());
            childHolder1.tvHeightAndLowPriceSpotItem.setText(children.get(childPosition).getLow() + " - " + children.get(childPosition).getHigh());
        }
        childHolder1.llSpotBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickMoreItem != null) {
                    AppAnalytics.getInstance().onEvent(mContext, "spot_bidding_click", "现货-报价点击量");
                    onClickMoreItem.setOnClickSpotItemData(view, children, childPosition);
                }
            }
        });
        childHolder1.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppAnalytics.getInstance().onEvent(mContext, "spot_bidding_more_click", "现货-报价更多点击量");
                Spot.PListBean listData = children.get(childPosition);
                if (ValueUtil.isStrNotEmpty(mSpot.getType())) {
                    listData.setCode(mSpot.getType());
                }
                querySpotListMore(listData);
            }
        });
        //滑动更改数据
        final ChildHolder1 finalChildHolder1 = childHolder1;
        finalChildHolder1.tvDate.setText(children.get(childPosition).getName() + "价格走势图");

        childHolder1.spotPriceView.setGetViewValue(new SpotPriceView.GetViewValue() {
            @Override
            public void setGetViewValue(ChooseData chooseData, boolean isLoogPress, boolean isLoogPressTouch) {
                if (isLoogPress) {
                    if (children.get(childPosition).isShowDetail()) {//有权限查看
                        finalChildHolder1.tvNameTitle.setVisibility(View.VISIBLE);
                        finalChildHolder1.tvPrice.setVisibility(View.VISIBLE);
                        finalChildHolder1.tvDate.setVisibility(View.VISIBLE);
                        finalChildHolder1.tvTitleTip.setVisibility(View.GONE);
                        finalChildHolder1.tvDate.setTextColor(ContextCompat.getColor(mContext,R.color.cffffff));
                        finalChildHolder1.tvDate.setText(chooseData.getDate());
                        finalChildHolder1.tvNameTitle.setText(chooseData.getValue());
                        finalChildHolder1.tvPrice.setText("价格:" + chooseData.getPrice());
                    } else {
                        finalChildHolder1.tvTitleTip.setVisibility(View.GONE);
                        finalChildHolder1.tvDate.setVisibility(View.VISIBLE);
                        finalChildHolder1.tvPrice.setVisibility(View.VISIBLE);
                        finalChildHolder1.tvPrice.setTextColor(ContextCompat.getColor(mContext,R.color.cD4975C));
                        finalChildHolder1.tvPrice.setText("价格：VIP专享内容");

                        finalChildHolder1.tvDate.setTextColor(ContextCompat.getColor(mContext,R.color.cffffff));
                        finalChildHolder1.tvDate.setText(chooseData.getDate());
                    }
                } else {
                    finalChildHolder1.tvNameTitle.setVisibility(View.GONE);
                    finalChildHolder1.tvPrice.setVisibility(View.GONE);
                    finalChildHolder1.tvDate.setVisibility(View.GONE);
                    finalChildHolder1.tvTitleTip.setVisibility(View.VISIBLE);
                    finalChildHolder1.tvTitleTip.setTextColor(ContextCompat.getColor(mContext,R.color.c6A798E));
                    finalChildHolder1.tvTitleTip.setText(children.get(childPosition).getName() + "价格走势图");
                }
                vPagerview.setSideslip(isLoogPressTouch);//处理viewpager的手势冲突
            }

            @Override
            public void onTouch(boolean isTouch) {
//                if (!children.get(childPosition).isShowDetail() && isTouch) {//没有权限查看
//                    ReadPermissionsManager.checkCodeEvent(mContext, null, Constant.ApplyReadFunction.ZH_APP_SPOT_MORE, true, true, null, new ReadPermissionsManager.CodeEventListenter() {
//                        @Override
//                        public void onNetError() {
//
//                        }
//
//                        @Override
//                        public void onFail() {
//
//                        }
//
//                        @Override
//                        public void onLogin() {
//
//                        }
//
//                        @Override
//                        public void onShowDialog() {
//
//                        }
//                    });
//                }
            }
        });
    }

    /**
     * 库存
     *
     * @param childHolder2
     * @param childPosition
     * @param children
     */
    private void stockEvent(ChildHolder2 childHolder2, final int childPosition, final List<Spot.PListBean> children) {
        if (children.get(0).isError()) {
            childHolder2.vEmpty.setVisibility(View.VISIBLE);
            childHolder2.llSoptItem.setVisibility(View.GONE);
            GjUtil.showEmptyHint(mContext, Constant.BgColor.BLUE, children.get(0).getNetError(), childHolder2.vEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    onClickMoreItem.onRefresh();
                }
            });
            return;
        }
        childHolder2.vEmpty.setVisibility(View.GONE);
        childHolder2.llSoptItem.setVisibility(View.VISIBLE);
        if (childHolder2.llSoptItem != null) {
            childHolder2.llSoptItem.setBackgroundResource(childPosition % 2 == 0 ? R.color.c1E3A65 : R.color.c25345B);
        }
        childHolder2.tvFromSpotStock.setText(ValueUtil.isStrNotEmpty(children.get(childPosition).getSource()) ? children.get(childPosition).getSource() : "");
        if (ValueUtil.isStrNotEmpty(children.get(childPosition).getUpdateTime() + "")) {
            String pushTime = DateUtil.getStringDateByLong(children.get(childPosition).getUpdateTime(), 9);
            childHolder2.tvTimeAndData.setText(pushTime);
        }
        //增减
        final String change = children.get(childPosition).getChange();
        if (ValueUtil.isStrNotEmpty(change)) {
            GjUtil.setUporDownColor(mContext, childHolder2.tvSpotStockIncreaseOrDecrease, change);
        } else {
            childHolder2.tvSpotStockIncreaseOrDecrease.setText("");
        }
        //数量
        String amount = children.get(childPosition).getAmount();
        childHolder2.tvSpotStockNum.setText(ValueUtil.isStrNotEmpty(amount) ? amount : "");
        childHolder2.llSoptItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppAnalytics.getInstance().onEvent(mContext, "spot_stock_click", "现货-库存点击量");
                if (children.get(childPosition).isDetail()) {
                    Spot.PListBean listData = children.get(childPosition);
                    querySpotByCfgKey(listData);
                }
            }
        });
    }


    /**
     * 获取现货-更多
     */
    private void querySpotListMore(final Spot.PListBean mLiatData) {
        if (ValueUtil.isEmpty(mLiatData)) {
            return;
        }
        String point = null;
        StringBuffer stringBuffer = new StringBuffer();
        if (mLiatData.getPoints().length > 0) {
            stringBuffer.setLength(0);
            for (int i = 0; i < mLiatData.getPoints().length; i++) {
                if (ValueUtil.isStrNotEmpty(mLiatData.getPoints()[i].getPointCode())) {
                    stringBuffer.append(mLiatData.getPoints()[i].getPointCode() + ",");
                }
            }
            point = stringBuffer.substring(0, stringBuffer.toString().length() - 1);
        }
        DialogUtil.waitDialog(mContext);
        Api.getSpotService().findSpotChart(mLiatData.getLcfgId(), point, mLiatData.getCode(), "").
                compose(XApi.<BaseModel<List<ChooseData>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<ChooseData>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<ChooseData>>>() {
                    @Override
                    public void onNext(BaseModel<List<ChooseData>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        SpotDetailOfferListActivity.launch((Activity) mContext, mLiatData);
                    }

                    @Override
                    protected void onFail(final NetError error) {
                        DialogUtil.dismissDialog();
                        ReadPermissionsManager.checkCodeEvent(mContext, null, Constant.ApplyReadFunction.ZH_APP_SPOT_MORE, true, true, error, new ReadPermissionsManager.CodeEventListenter() {
                            @Override
                            public void onNetError() {

                            }

                            @Override
                            public void onFail() {

                            }

                            @Override
                            public void onLogin() {

                            }

                            @Override
                            public void onShowDialog() {

                            }
                        });

                    }
                });
    }

    /**
     * 现货报价
     */
    private void querySpotByCfgKey(final Spot.PListBean mLiatData) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("metalCode", mLiatData.getMetalCode());
        params.put("source", mLiatData.getSource());
        if (mLiatData.getType() != null && mLiatData.getType().length() > 0) {
            params.put("type", mLiatData.getType());
        } else {
            params.put("type", "");
        }
        DialogUtil.waitDialog(mContext);
        Api.getSpotService().getStockDetail(params)
                .compose(XApi.<BaseModel<List<SpotStock>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<SpotStock>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<SpotStock>>>() {
                    @Override
                    public void onNext(BaseModel<List<SpotStock>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        SpotStockActivity.launch((Activity) mContext, mLiatData);
                    }

                    @Override
                    protected void onFail(final NetError error) {
                        DialogUtil.dismissDialog();
                        ReadPermissionsManager.checkCodeEvent(mContext, null, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_COMEX_STOCK, true, true, error, new ReadPermissionsManager.CodeEventListenter() {
                            @Override
                            public void onNetError() {

                            }

                            @Override
                            public void onFail() {

                            }

                            @Override
                            public void onLogin() {

                            }

                            @Override
                            public void onShowDialog() {

                            }
                        });
                    }
                });
    }

    /**
     * 持仓分析
     *
     * @param childHolder3
     */
    private void positionAnalysisEvent(final ChildHolder3 childHolder3, final int childPosition, final List<Spot.PListBean> children) {
        childHolder3.kVolume.setLongPress(false);
        Spot.PListBean mSpotPositionAnalysis = children.get(childPosition);
        if (ValueUtil.isNotEmpty(mSpotPositionAnalysis)) {
            childHolder3.tvShfe.setText(ValueUtil.isStrNotEmpty(mSpotPositionAnalysis.getShfeName()) ? mSpotPositionAnalysis.getShfeName() : "");
            childHolder3.tvShfeLme.setText(ValueUtil.isStrNotEmpty(mSpotPositionAnalysis.getLmeName()) ? mSpotPositionAnalysis.getLmeName() : "");
        }
        NetError error = children.get(childPosition).getNetError();
        if (ValueUtil.isNotEmpty(error)) {
            childHolder3.tvVipVolume.setVisibility(View.VISIBLE);
            childHolder3.kVolume.setVisibility(View.GONE);
            childHolder3.vEmpty.setVisibility(View.GONE);
            childHolder3.tvShfe.setText(mContext.getString(R.string.txt_dian_shfe));
            childHolder3.tvShfeLme.setText(mContext.getString(R.string.txt_dian_shfeLme));
        } else {
            childHolder3.tvVipVolume.setVisibility(View.GONE);
            if (ValueUtil.isNotEmpty(mSpotPositionAnalysis) && ValueUtil.isListNotEmpty(mSpotPositionAnalysis.getPositionAnalysisPoints())) {
                childHolder3.kVolume.setVisibility(View.VISIBLE);
                childHolder3.vEmpty.setVisibility(View.GONE);
                childHolder3.kVolume.setRefreshData(true, mSpotPositionAnalysis.getPositionAnalysisPoints());
                childHolder3.kVolume.setGetViewValue(new SpotPositionAnalysisView.GetViewValue() {
                    @Override
                    public void setGetViewValue(ChooseData chooseData, boolean isLoogPress, boolean isLoogPressTouch) {
                        vPagerview.setSideslip(isLoogPressTouch);//处理viewpager的手势冲突
                    }
                });
            } else {
                childHolder3.vEmpty.setVisibility(View.VISIBLE);
                childHolder3.vEmpty.setNoData(Constant.BgColor.BLUE);
                childHolder3.kVolume.setVisibility(View.GONE);
            }
        }
        childHolder3.tvVipVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetError netError = children.get(childPosition).getNetError();
                if (netError == null) {
                    if (onClickMoreItem != null) {
                        onClickMoreItem.setOnPositionAnalysisClick(children, childPosition);
                    }
                } else {
                    ReadPermissionsManager.checkCodeEvent(mContext, null, Constant.ApplyReadFunction.ZH_APP_SPOT_INTEREST, true, true, netError, new ReadPermissionsManager.CodeEventListenter() {
                        @Override
                        public void onNetError() {

                        }

                        @Override
                        public void onFail() {

                        }

                        @Override
                        public void onLogin() {
                            LoginActivity.launch((Activity) mContext);
                        }

                        @Override
                        public void onShowDialog() {

                        }
                    });
                }
            }
        });
    }

    /**
     * 资讯
     *
     * @param childHolder4
     * @param childPosition
     * @param children
     */
    public void informationEvent(ChildHolder4 childHolder4, final int childPosition, final List<Spot.PListBean> children) {
        if (children.get(0).isError() && childHolder4.vEmpty != null) {
            childHolder4.vEmpty.setVisibility(View.VISIBLE);
            childHolder4.llNews.setVisibility(View.GONE);
            GjUtil.showEmptyHint(mContext, Constant.BgColor.BLUE, children.get(0).getNetError(), childHolder4.vEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    onClickMoreItem.onRefresh();
                }
            });
            return;
        }
        if (childHolder4.vEmpty != null) {
            childHolder4.vEmpty.setVisibility(View.GONE);
        }
        childHolder4.llNews.setVisibility(View.VISIBLE);
        childHolder4.llNews.setOnClickListener(new View.OnClickListener() {//相关资讯跳转
            @Override
            public void onClick(View v) {//进相关资讯详情
                if (ValueUtil.isStrNotEmpty(children.get(childPosition).getVip()) && children.get(childPosition).getVip().equals("Y")) {
                    if (User.getInstance().isLoginIng() && children.get(childPosition).getVip().equals("Y")) {
                        ReadPermissionsManager.readPermission(Constant.News.RECORD_NEWS_CODE
                                , Constant.POWER_RECORD
                                , Constant.News.RECORD_NEWS_MODULE
                                , mContext
                                , null
                                , Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP, true, false).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                                    jumpToDetail(children.get(childPosition));
                                }
                            }
                        });
                    } else {
                        LoginActivity.launch((Activity) mContext);
                    }
                } else {
                    jumpToDetail(children.get(childPosition));
                }
            }
        });
        //来源
        childHolder4.tvSpotNewsSouce.setText(ValueUtil.isStrNotEmpty(children.get(childPosition).getProvide()) ? children.get(childPosition).getProvide() : "-");
        //Vip
        if (ValueUtil.isStrNotEmpty(children.get(childPosition).getVip())) {
            childHolder4.tvVip.setVisibility(children.get(childPosition).getVip().equals("Y") ? View.VISIBLE : View.GONE);
        } else {
            childHolder4.tvVip.setVisibility(View.GONE);
        }
        String title = children.get(childPosition).getTitle();
        if (ValueUtil.isStrNotEmpty(title)) {
            childHolder4.tvSpotNewsTitle.setText(title);
        } else {
            childHolder4.tvSpotNewsTitle.setText("");
        }
        try {
            childHolder4.tvSpotNewsTime.setText(GjUtil.diffDate(DateUtil.getStringDateByLong(children.get(childPosition).getPushTime(), 5), TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入详情
     *
     * @param bean
     */
    public void jumpToDetail(Spot.PListBean bean) {
        InformationContentBean.ListBean infobean = new InformationContentBean.ListBean();
        infobean.setDetailUrl(bean.getDetailsUrl());
        infobean.setCoverImgs(bean.getCoverImgs());
        infobean.setCollect(bean.isCollect());
        infobean.setNewsId(bean.getNewsId());
        infobean.setTitle(bean.getTitle());
        infobean.setVip(bean.getVip());
        InformationWebViewActivity.launch((Activity) mContext, infobean, new WebViewBean(bean.getTitle(), Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl()), bean.getNewsId()), Constant.IntentFrom.SPOT);
    }

    /**
     * 报价说明
     *
     * @param groupHolder
     */
    private void clickSpotDesc(GroupHolder groupHolder) {
        groupHolder.ivExplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValueUtil.isStrEmpty(mSpot.getDescUrl())) {
                    ToastUtil.showToast(mContext.getString(R.string.no_getdata));
                    return;
                }
                BaseWebViewActivity.launch((Activity) mContext, new WebViewBean(mContext.getString(R.string.txt_spot_desc), Constant.ReqUrl.getDefaultHtmlUrl(mSpot.getDescUrl())));
            }
        });
    }

    /**
     * 持仓分析详情
     */
    private void clickSpotAnalySisDetail(GroupHolder groupHolder) {
        groupHolder.tvSpotAnalysisDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotChildFragment.checkAnalysis(mContext, mSpot);
            }
        });

    }

    public int getGroupCount() {
        return mGroups.size();
    }

    public int getChildCount(int groupPosition) {
        if (mGroupData.get(groupPosition) != null) {
            return mGroupData.get(groupPosition).size();
        }
        return 0;
    }

    public List<Spot.PListBean> getGroup(int groupPosition) {
        if (mGroupData.get(groupPosition) != null) {
            return mGroupData.get(groupPosition);
        }
        return null;
    }

    public String getGroupName(int groupPosition){
        if(mGroups!=null&&mGroups.get(groupPosition)!=null){
            return mGroups.get(groupPosition);
        }
        return null;
    }

    public Spot.PListBean getChild(int groupPosition, int childPosition) {
        if (mGroupData.get(groupPosition) != null) {
            List<Spot.PListBean> group = mGroupData.get(groupPosition);
            if (childPosition >= 0 && childPosition < group.size()) {
                return group.get(childPosition);
            }
        }
        return null;

    }


    // Helper method to add group

    public SpotExpandAdapter addGroup(String group) {
        if (!mGroups.containsValue(group)) {
            mCurrentGroup++;
            mGroups.put(mCurrentGroup, group);
            mGroupData.put(mCurrentGroup, new ArrayList<Spot.PListBean>());
        }
        return this;
    }


    // Helper method to add child into one group

    public SpotExpandAdapter addChild(Spot.PListBean child) {
        if (mGroupData.get(mCurrentGroup) != null) {
            mGroupData.get(mCurrentGroup).add(child);
        }
        return this;
    }

}
