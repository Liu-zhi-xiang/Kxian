package com.gjmetal.app.ui.spot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.spot.expand.SpotExpandAdapter;
import com.gjmetal.app.adapter.spot.expand.XBaseExpandAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotItems;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.MyViewPager;
import com.gjmetal.app.widget.explist.DockingExpandableListView;
import com.gjmetal.app.widget.explist.IDockingHeaderUpdateListener;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by hgh on 2018/3/30.
 * 现货子视图
 */

@SuppressLint("ValidFragment")
public class SpotChildFragment extends BaseFragment {
    @BindView(R.id.expSpotQuotationChild)
    DockingExpandableListView expSpotQuotationChild;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    public static String SPOT_PRICE="现货报价";
    public static String SPOT_STOCK="库存";
    public static String SPOT_ANALYSIS="持仓分析";
    public static String SPOT_NEWS="相关资讯";
    private String type;
    private String value;
    private List<Spot.PListBean> spotsQuotation = new ArrayList<>();
    private List<Spot.PListBean> spotsStockList = new ArrayList<>();
    private List<Spot.PListBean> spotsNews = new ArrayList<>();
    private List<Spot.PListBean> spotPositionAnalysisList = new ArrayList<>();
    private int page = 1;
    private XBaseExpandAdapter dockingExpandableListViewAdapter;
    private Spot mSpot;
    private int index = 0;
    private MyViewPager vPagerview;//对viewpager进行处理
    private int loadNoData = 0;
    private int loadSuccess = 0;
    private int loadFail = 0;
    private int loadTotal = 0;//加载总次数
    private int needLoad = 0;//需加载几个接口
    private SpotItems spotItems;

    @Override
    protected int setRootView() {
        return R.layout.fragment_spot_child;
    }

    public SpotChildFragment(MyViewPager vPagerview, int index, SpotItems spotItems) {
        this.vPagerview = vPagerview;
        this.index = index;
        this.type = spotItems.getCfgKey();
        this.value = spotItems.getCfgVal();
        this.spotItems = spotItems;
    }

    public SpotChildFragment() {
    }

    public void initView() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAllList();
                    }
                }, Constant.REFRESH_TIME);
            }
        });

        refreshLayout.setRefreshHeader(new MyRefreshHender(getContext(), ContextCompat.getColor(getContext(),R.color.c202239)));
        refreshLayout.setHeaderHeight(60);
        refreshLayout.setEnableLoadMore(false);
        if (index == 0 && refreshLayout != null) {
            vEmpty.setVisibility(View.GONE);
            refreshLayout.autoRefresh();
        }
        //注册
        BusProvider.getBus().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (!isAdded()) {
            return;
        }
        if (baseEvent.isRefreshSpot()) {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh();
            }
        } else if (baseEvent.isLogin()) {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh();
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CollectEvent(CollectEvent collectEvent) {
        if (isAdded() && dockingExpandableListViewAdapter != null) {
            for (int i = 0; i < collectEvent.mList.size(); i++) {
                for (int j = 0; j < spotsNews.size(); j++) {
                    if (collectEvent.mList.get(i).getNewsId() == spotsNews.get(j).getNewsId()) {
                        spotsNews.get(j).setCollect(!spotsNews.get(j).isCollect());
                    }
                }
            }
            dockingExpandableListViewAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 异步请求接口
     */
    private void loadAllList() {
        mSpot = new Spot();
        mSpot.setType(type);
        loadNoData = 0;
        loadSuccess = 0;
        loadFail = 0;
        loadTotal = 0;
        needLoad = 0;
        if (spotItems.isExistSpotPrice()) {
            needLoad++;
        }
        if (spotItems.isExistStock()) {
            needLoad++;
        }
        if (spotItems.isExistPositionAnalysis()) {
            needLoad++;
        }
        if (spotItems.isExistNews()) {
            needLoad++;
        }
        if (needLoad == 0) {
            showNoData();
            return;
        }
        if (spotItems.isExistSpotPrice()) {
            querySpotByCfgKey();
        }
        if (spotItems.isExistStock()) {
            getStock();
        }
        if (spotItems.isExistPositionAnalysis()) {
            getPositionAnalysis();
        }
        if (spotItems.isExistNews()) {
            queryNewsByColIdsList();
        }
        AppAnalytics.getInstance().onEvent(getActivity(), "spot_" + type + "_acess", "现货-各品种-点击量");
    }

    /**
     * 显示刷新列表
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (refreshLayout != null && expSpotQuotationChild != null && expSpotQuotationChild.getVisibility() == View.GONE) {
                vEmpty.setVisibility(View.GONE);
                refreshLayout.autoRefresh();
            }
        }
    }


    /**
     * 现货报价
     */
    private void querySpotByCfgKey() {
        Api.getSpotService().querySpotPrices(type)
                .compose(XApi.<BaseModel<Spot>>getApiTransformer())
                .compose(XApi.<BaseModel<Spot>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Spot>>() {
                    @Override
                    public void onNext(BaseModel<Spot> listBaseModel) {
                        mSpot = listBaseModel.getData();
                        String url = listBaseModel.getData().getDescUrl() + "/" + type;
                        XLog.d("报价说明url-----------------", url);
                        mSpot.setDescUrl(url);
                        mSpot.setType(type);
                        spotsQuotation.clear();
                        if (listBaseModel.getData() != null && ValueUtil.isListNotEmpty(listBaseModel.getData().getPriceList())) {
                            spotsQuotation.addAll(listBaseModel.getData().getPriceList());
                        } else {
                            loadNoData++;
                        }
                        onRefreshUI(true, null);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (ValueUtil.isListNotEmpty(spotsQuotation)) {
                            spotsQuotation.clear();
                        }
                        mSpot.setType(type);
                        Spot.PListBean pListBean = new Spot.PListBean();
                        pListBean.setParentError(true);
                        pListBean.setNetError(error);
                        spotsQuotation.add(pListBean);
                        onRefreshUI(false, error);
                    }
                });
    }

    /**
     * 库存
     */
    private void getStock() {
        String url = "rest/shfe/getStock/" + type;
        Flowable<BaseModel<List<Spot.PListBean>>> stock = Api.getSpotService().getStock(url);
        stock.compose(XApi.<BaseModel<List<Spot.PListBean>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Spot.PListBean>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Spot.PListBean>>>() {
                    @Override
                    public void onNext(BaseModel<List<Spot.PListBean>> listBaseModel) {
                        if (ValueUtil.isListNotEmpty(spotsStockList)) {
                            spotsStockList.clear();
                        }
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            for (int i = 0; i < listBaseModel.getData().size(); i++) {
                                listBaseModel.getData().get(i).setMetalCode(type);
                            }
                            spotsStockList.addAll(listBaseModel.getData());
                        } else {
                            loadNoData++;
                        }
                        onRefreshUI(true, null);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (ValueUtil.isListNotEmpty(spotsStockList)) {
                            spotsStockList.clear();
                        }
                        Spot.PListBean pListBean = new Spot.PListBean();
                        pListBean.setError(true);
                        pListBean.setNetError(error);
                        spotsStockList.add(pListBean);
                        onRefreshUI(false, error);
                    }

                });

    }

    /**
     * 持仓分析
     */
    private void getPositionAnalysis() {
        Api.getSpotService().getPositionAnalysis(type)
                .compose(XApi.<BaseModel<Spot.PListBean>>getApiTransformer())
                .compose(XApi.<BaseModel<Spot.PListBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Spot.PListBean>>() {
                    @Override
                    public void onNext(BaseModel<Spot.PListBean> listBaseModel) {
                        if (ValueUtil.isListNotEmpty(spotPositionAnalysisList)) {
                            spotPositionAnalysisList.clear();
                        }
                        if (ValueUtil.isNotEmpty(listBaseModel.getData())) {
                            spotPositionAnalysisList.add(listBaseModel.getData());
                        }
                        onRefreshUI(true, null);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (ValueUtil.isListNotEmpty(spotPositionAnalysisList)) {
                            spotPositionAnalysisList.clear();
                        }
                        if (ValueUtil.isStrNotEmpty(error.getType()) && error.getType().equals(Constant.ResultCode.FAILED.getValue())) {
                            Spot.PListBean pListBean = new Spot.PListBean();
                            pListBean.setError(true);
                            pListBean.setNetError(error);
                            spotPositionAnalysisList.add(pListBean);
                            onRefreshUI(false, error);
                        } else {
                            Spot.PListBean bean = new Spot.PListBean();
                            bean.setNetError(error);
                            spotPositionAnalysisList.add(bean);
                            onRefreshUI(true, null);
                        }
                    }
                });


    }

    /**
     * 相关资讯
     */
    private void queryNewsByColIdsList() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("tagName", value);
        map.put("pageSize", Constant.SPOT_PAGE_SIZE + "");
        map.put("currentPage", page + "");
        Api.getSpotService().getInformationList(map)
                .compose(XApi.<BaseModel<Spot>>getApiTransformer())
                .compose(XApi.<BaseModel<Spot>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Spot>>() {
                    @Override
                    public void onNext(BaseModel<Spot> listBaseModel) {
                        if (ValueUtil.isListNotEmpty(spotsNews)) {
                            spotsNews.clear();
                        }
                        if (listBaseModel.getData() != null && ValueUtil.isListNotEmpty(listBaseModel.getData().getList())) {
                            spotsNews.addAll(listBaseModel.getData().getList());
                        } else {
                            loadNoData++;
                        }
                        onRefreshUI(true, null);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (ValueUtil.isListNotEmpty(spotsNews)) {
                            spotsNews.clear();
                        }
                        Spot.PListBean pListBean = new Spot.PListBean();
                        pListBean.setError(true);
                        pListBean.setNetError(error);
                        spotsNews.add(pListBean);
                        onRefreshUI(false, error);
                    }

                });
    }


    /**
     * 进入持仓分析详情
     *
     * @param mSpot
     * @param mContext
     */
    public static void checkAnalysis(final Context mContext, final Spot mSpot) {
        if (ValueUtil.isEmpty(mSpot)) {
            return;
        }
        ReadPermissionsManager.readPermission("positionAnalysis-" + mSpot.getType() + "-detail"
                , Constant.POWER_SOURCE
                , Constant.Spot.RESOURCE_MODULE
                , mContext
                , null
                , Constant.ApplyReadFunction.ZH_APP_SPOT_INTEREST_DETAIL, true, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                    SpotAnalysisDetailActivity.launch((Activity) mContext, mSpot);
                }
            }
        });

    }

    public void onRefreshUI(boolean isSuccess, NetError netError) {
        if (isSuccess) {
            loadSuccess++;
        } else {
            loadFail++;
        }
        loadTotal = loadSuccess + loadFail;
        if (ValueUtil.isStrEmpty(type)) {
            if (refreshLayout != null) {
                refreshLayout.finishRefresh(false);
            }
            return;
        }
        XLog.d("loadTotalNum",loadTotal+"/loadFail="+loadFail+"/needLoad="+needLoad+"/loadNoData="+loadNoData);
        if (loadTotal == needLoad) {
            if (loadFail == needLoad) {//全部加载失败
                showAgainLoad(netError);
                return;
            }
            if (loadTotal == loadNoData) {//全部无数据
                showNoData();
                return;
            }
            upDateUI(mSpot, spotPositionAnalysisList, spotsQuotation, spotsStockList, spotsNews);
        }
    }

    /**
     * 现货报价走势图数据
     */
    private void querySpotChart(final List<Spot.PListBean> children, final int childPosition, final boolean showDialog) {
        Api.getSpotService().querySpotChart(children.get(childPosition).getLcfgId(), null, type).
                compose(XApi.<BaseModel<List<ChooseData>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<ChooseData>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<ChooseData>>>() {
                    @Override
                    public void onNext(BaseModel<List<ChooseData>> listBaseModel) {
                        if (listBaseModel.getData().size() > 0) {
                            children.get(childPosition).setNetError(null);
                            children.get(childPosition).setKlistBaseMode(listBaseModel.getData());
                            children.get(childPosition).setShowDetail(listBaseModel.getData().get(0).isShowDetail());
                        } else {
                            children.get(childPosition).setKlistBaseMode(null);
                            children.get(childPosition).setNetError(null);
                            children.get(childPosition).setIsError(true);
                            children.get(childPosition).setEmpty(true);
                        }
                        children.get(childPosition).setRequsted(true);
                        dockingExpandableListViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error == null || error.getType() == null) {
                            return;
                        }
                        if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            children.get(childPosition).setOpen(false);
                            dockingExpandableListViewAdapter.notifyDataSetChanged();
                            LoginActivity.launch(getActivity());
                        } else {
                            ReadPermissionsManager.checkCodeEvent(getActivity(), null, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_COMEX_STOCK, showDialog, true, error, new ReadPermissionsManager.CodeEventListenter() {
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
                            children.get(childPosition).setRequsted(true);
                            children.get(childPosition).setKlistBaseMode(null);
                            children.get(childPosition).setNetError(error);
                            children.get(childPosition).setParentError(false);
                            children.get(childPosition).setIsError(true);
                            dockingExpandableListViewAdapter.notifyDataSetChanged();
                        }

                    }
                });
    }

    /**
     * 更新界面
     *
     * @param mSpot
     * @param spotPositionAnalysisList
     * @param spotsStockList
     * @param spotsNews
     */
    private void upDateUI(final Spot mSpot, final List<Spot.PListBean> spotPositionAnalysisList, List<Spot.PListBean> spotsPriceList, List<Spot.PListBean> spotsStockList, List<Spot.PListBean> spotsNews) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
        }
        if (expSpotQuotationChild != null) {
            expSpotQuotationChild.setVisibility(View.VISIBLE);
        }
        if (vEmpty != null) {
            vEmpty.setVisibility(View.GONE);
        }
        if (ValueUtil.isEmpty(mSpot)) {
            return;
        }
        final SpotExpandAdapter listData = prepareData(mSpot, spotPositionAnalysisList, spotsPriceList, spotsStockList, spotsNews);
        if (expSpotQuotationChild == null) {
            return;
        }
        expSpotQuotationChild.setGroupIndicator(null);
        expSpotQuotationChild.setOverScrollMode(View.OVER_SCROLL_NEVER);
        dockingExpandableListViewAdapter = new XBaseExpandAdapter(getActivity(), expSpotQuotationChild, listData);
        expSpotQuotationChild.setAdapter(dockingExpandableListViewAdapter);
        //设置Item 不可以点击收缩
        expSpotQuotationChild.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }

        });
        for (int i = 0; i < dockingExpandableListViewAdapter.getGroupCount(); i++) {
            expSpotQuotationChild.expandGroup(i);
        }
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.group_spot_view_item, expSpotQuotationChild, false);
        expSpotQuotationChild.setDockingHeader(headerView, new IDockingHeaderUpdateListener() {

            @Override
            public void onUpdate(View convertView, int groupPosition, boolean expanded) {
                TextView tvSpotQuotation = convertView.findViewById(R.id.tvSpotQuotation);
                ImageView ivExplain = convertView.findViewById(R.id.ivExplain);
                TextView tvNameOrPrice = convertView.findViewById(R.id.tvNameOrPrice);
                TextView tvMeanPrice = convertView.findViewById(R.id.tvMeanPrice);
                TextView tvGronpUpDown = convertView.findViewById(R.id.tvGronpUpDown);
                TextView tvSpotAnalysisDetail = convertView.findViewById(R.id.tvSpotAnalysisDetail);//持仓分析
                LinearLayout llspotItemTitter = convertView.findViewById(R.id.llspotItemTitter);

                String groupName = listData.getGroupName(groupPosition);
                if (ValueUtil.isStrEmpty(groupName)) {
                    return;
                }
                if (groupName.equals(SpotChildFragment.SPOT_PRICE)) {
                    tvSpotQuotation.setText(getActivity().getResources().getString(R.string.spotquotation));
                    tvNameOrPrice.setText(getActivity().getResources().getString(R.string.nameorprice));
                    tvMeanPrice.setText(getActivity().getResources().getString(R.string.averageprice));
                    tvGronpUpDown.setText(getActivity().getResources().getString(R.string.upanddown));
                    ivExplain.setVisibility(View.VISIBLE);
                    tvSpotAnalysisDetail.setVisibility(View.GONE);
                    llspotItemTitter.setVisibility(View.VISIBLE);
                }
                if (groupName.equals(SpotChildFragment.SPOT_STOCK)) {
                    tvSpotQuotation.setText(getActivity().getResources().getString(R.string.stock));
                    tvNameOrPrice.setText(getActivity().getResources().getString(R.string.source));
                    tvMeanPrice.setText(getActivity().getResources().getString(R.string.num));
                    tvGronpUpDown.setText(getActivity().getResources().getString(R.string.increaseordecrease));
                    llspotItemTitter.setVisibility(View.VISIBLE);
                    tvSpotAnalysisDetail.setVisibility(View.GONE);
                    ivExplain.setVisibility(View.GONE);
                }
                if (groupName.equals(SpotChildFragment.SPOT_ANALYSIS)) {
                    tvSpotQuotation.setText(getActivity().getResources().getString(R.string.positionAnalysis));
                    ivExplain.setVisibility(View.GONE);
                    llspotItemTitter.setVisibility(View.GONE);
                    tvSpotAnalysisDetail.setVisibility(View.VISIBLE);
                }
                if (groupName.equals(SpotChildFragment.SPOT_NEWS)) {
                    tvSpotQuotation.setText(getActivity().getResources().getString(R.string.relevantinformation));
                    ivExplain.setVisibility(View.GONE);
                    llspotItemTitter.setVisibility(View.GONE);
                    tvSpotAnalysisDetail.setVisibility(View.GONE);
                }
                if (mSpot.getDescUrl() != null && mSpot.getDescUrl().length() > 0) {
                    ivExplain.setTag(mSpot.getDescUrl());
                }
                if (ValueUtil.isNotEmpty(mSpot)) {
                    if (ValueUtil.isListNotEmpty(spotPositionAnalysisList)) {
                        mSpot.setNetError(spotPositionAnalysisList.get(0).getNetError());
                    }
                    tvSpotAnalysisDetail.setTag(mSpot);
                }
            }
        });
    }

    /**
     * 组装数据
     *
     * @return
     */
    private SpotExpandAdapter prepareData(Spot spot, List<Spot.PListBean> spotPosAnalysisList, List<Spot.PListBean> spotPriceList, final List<Spot.PListBean> spotsStockList, List<Spot.PListBean> spotsNewsList) {
        SpotExpandAdapter listData = new SpotExpandAdapter(getActivity(), spot, vPagerview);
        listData.setOnClickSpotItem(new SpotExpandAdapter.OnClickSpotItem() {
            @Override
            public void setOnClickSpotItemData(View view, List<Spot.PListBean> children, int childPosition) {//现货报价走势图
                switch (view.getId()) {
                    case R.id.llSpotBg:
                        if (children.get(childPosition).isOpen()) {
                            children.get(childPosition).setOpen(false);
                            children.get(childPosition).setNetError(null);
                            children.get(childPosition).setIsError(false);
                            children.get(childPosition).setParentError(false);
                            children.get(childPosition).setRequsted(false);
                            if (children.get(childPosition).getKlistBaseMode() != null) {
                                children.get(childPosition).setKlistBaseMode(null);
                            }
                        } else {
                            for (int i = 0; i < children.size(); i++) {
                                children.get(i).setOpen(false);
                                children.get(i).setNetError(null);
                                children.get(i).setIsError(false);
                                children.get(i).setParentError(false);
                                children.get(i).setRequsted(false);
                            }
                            children.get(childPosition).setOpen(true);
                        }
                        dockingExpandableListViewAdapter.notifyDataSetChanged();
                        if (children.get(childPosition).isOpen()) {
                            querySpotChart(children, childPosition, false);
                        }
                        break;
                    case R.id.vEmpty:
                    case R.id.tvVipRead:
                        children.get(childPosition).setNetError(null);
                        children.get(childPosition).setIsError(false);
                        children.get(childPosition).setParentError(false);
                        children.get(childPosition).setRequsted(false);
                        dockingExpandableListViewAdapter.notifyDataSetChanged();
                        querySpotChart(children, childPosition, true);
                        break;
                }
            }

            @Override
            public void setOnPositionAnalysisClick(List<Spot.PListBean> children, int childPosition) {//持仓分析
                spotPositionAnalysisList = children;
            }

            @Override
            public void onRefresh() {
                if (refreshLayout != null) {
                    refreshLayout.autoRefresh();
                }
            }
        });
        if (spotItems.isExistSpotPrice()&&ValueUtil.isListNotEmpty(spotPriceList)) {
            listData.addGroup(SpotChildFragment.SPOT_PRICE);
            for (Spot.PListBean spb : spotPriceList) {
                listData.addChild(spb);
            }
        }
        if (spotItems.isExistStock()&&ValueUtil.isListNotEmpty(spotsStockList)) {
            listData.addGroup(SpotChildFragment.SPOT_STOCK);
            for (Spot.PListBean spb : spotsStockList) {
                listData.addChild(spb);
            }
        }
        if (spotItems.isExistPositionAnalysis()) {
            listData.addGroup(SpotChildFragment.SPOT_ANALYSIS);
            for (Spot.PListBean spb : spotPosAnalysisList) {
                listData.addChild(spb);
                break;
            }
        }
        if (spotItems.isExistNews()&&ValueUtil.isListNotEmpty(spotsNewsList)) {
            listData.addGroup(SpotChildFragment.SPOT_NEWS);
            for (Spot.PListBean spb : spotsNewsList) {
                listData.addChild(spb);
            }
        }
        XLog.d("listData", listData.getGroupName(0) + "/" + listData.getGroupCount());
        return listData;
    }

    /**
     * 加载失败提示
     *
     * @param error
     */
    private void showAgainLoad(NetError error) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh(false);
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    vEmpty.setVisibility(View.GONE);
                    refreshLayout.autoRefresh();
                }
            }
        }, expSpotQuotationChild);

    }


    /**
     * 暂无数据
     */
    private void showNoData() {
        if (vEmpty == null || refreshLayout == null) {
            return;
        }
        if (refreshLayout != null) {
            refreshLayout.finishRefresh(false);
        }
        vEmpty.setVisibility(View.VISIBLE);
        vEmpty.setNoData(Constant.BgColor.BLUE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }
}
