package com.gjmetal.app.ui.market.change;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.allChange.AllChangeItemAdapter;
import com.gjmetal.app.adapter.market.allChange.AllTabAdapter;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.allChange.AddFutureParameter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.star.event.BusProvider;

import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description：全部
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-30 16:42
 */

public class AllFutureFragment extends BaseFragment {
    @BindView(R.id.rvTab)
    RecyclerView rvTab;
    @BindView(R.id.vLine)
    View vLine;
    @BindView(R.id.rvData)
    RecyclerView rvData;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.btnAddMyChange)
    Button btnAddMyChange;
    private AllTabAdapter tabAdapter;
    private AllChangeItemAdapter allChangeItemAdapter;
    private List<Future> tabItemList = new ArrayList<>();
    private List<Future> futureItemList = new ArrayList<>();
    private List<Future> firstList = new ArrayList<>();
    private List<Future> hasChooseList = new ArrayList<>();
    private boolean loadOver = false;
    private List<AddFutureParameter> parameterList = new ArrayList<>();
    @Override
    protected int setRootView() {
        return R.layout.fragment_change_all;
    }

    @SuppressLint("ValidFragment")
    public AllFutureFragment() {
    }

    protected void initView() {
        BusProvider.getBus().register(this);
        //水平Tab
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTab.setLayoutManager(mLayoutManager);
        tabAdapter = new AllTabAdapter(getContext(), new AllTabAdapter.ChooseTabTtemListener() {
            @Override
            public void setItemData(Future futureItem, int position) {
                if (ValueUtil.isEmpty(futureItem) || tabAdapter == null) {
                    return;
                }
                for (Future bean : tabAdapter.getDataSource()) {
                    if (bean == futureItem) {
                        bean.setSelected(true);
                    } else {
                        bean.setSelected(false);
                    }
                }
                tabAdapter.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        showFirstData();
                        break;
                    case 1:
                        showSecondData(futureItem);
                        break;
                    case 2:
                        if (loadOver) {
                            return;
                        }
                        showThreeData(futureItem);
                        break;
                }
            }
        });
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin() && isAdded() || baseEvent.isRefreshAllChoose()) {
            if (ValueUtil.isListNotEmpty(hasChooseList)) {
                hasChooseList.clear();
            }
            loadData();
        } else if (baseEvent.isClearHasChange()) {
            if (ValueUtil.isListNotEmpty(hasChooseList)) {
                hasChooseList.clear();
            }
            SharedUtil.clearData(Constant.CHANGE_DATA);
            loadData();
        }

    }

    @OnClick({R.id.btnAddMyChange})
    public void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnAddMyChange:
                AppAnalytics.getInstance().onEvent(getActivity(), "market_btn_addfavor");
                if (ValueUtil.isListNotEmpty(hasChooseList)) {
                    for (Future futureItem : hasChooseList) {
                        if (futureItem.isCheck()) {
                            AddFutureParameter parameter = new AddFutureParameter();
                            parameter.setCodeId(futureItem.getContract());
                            parameter.setTypeId(futureItem.getType());
                            parameterList.add(parameter);
                        }
                    }
                }else {
                    ToastUtil.showToast("请选择");
                    return;
                }
                if (ValueUtil.isListEmpty(parameterList)) {
                    ToastUtil.showToast("请选择");
                    return;
                }
                addSubscribeFutures(parameterList);
                hasChooseList.clear();
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && rvData != null && ValueUtil.isListEmpty(tabItemList)) {
            loadData();
        }

    }

    public void loadData() {
        showFirstData();
        getAllMenus();
    }

    /**
     * 初始化
     */
    private void showFirstData() {
        if (vEmpty != null) {
            vEmpty.setVisibility(View.GONE);
        }
        loadOver = false;
        if (ValueUtil.isListNotEmpty(tabItemList)) {
            tabItemList.clear();
            Future defaultFuture = new Future();
            defaultFuture.setSelected(true);
            defaultFuture.setName("全部");
            tabItemList.add(defaultFuture);
            tabAdapter.setData(tabItemList);
            tabAdapter.notifyDataSetChanged();
        }

        //列表
        allChangeItemAdapter = new AllChangeItemAdapter(getContext(), new AllChangeItemAdapter.CallBackOnItemClik() {
            @Override
            public void onClick(Future futureItem) {
                futureItem.setSelected(true);
                futureItem.setEnd(true);
                if (ValueUtil.isListEmpty(futureItem.getSubItem())) {
                    showThreeData(futureItem);
                } else {
                    showSecondData(futureItem);
                }

            }

            @Override
            public void onCheck(Future futureItem) {
                if (ValueUtil.isNotEmpty(futureItem)) {
                    if (futureItem.isCheck()) {
                        if (!hasChooseList.contains(futureItem)) {
                            hasChooseList.add(futureItem);
                        }
                    } else {
                        hasChooseList.remove(futureItem);
                    }
                }

            }
        });
        if (rvData == null) {
            return;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvData.setLayoutManager(layoutManager);
        if (ValueUtil.isListNotEmpty(firstList)) {
            for (Future bean : firstList) {
                bean.setEnd(false);
            }
        }
        allChangeItemAdapter.showCheck(false);
        allChangeItemAdapter.setData(firstList);
        allChangeItemAdapter.notifyDataSetChanged();
        rvData.setAdapter(allChangeItemAdapter);
    }


    /**
     * 二级
     *
     * @param futureItem
     */
    private void showSecondData(Future futureItem) {
        if (vEmpty != null) {
            vEmpty.setVisibility(View.GONE);
        }
        loadOver = false;
        if (ValueUtil.isListNotEmpty(tabItemList)) {
            tabItemList.clear();
            Future defaultFuture = new Future();
            defaultFuture.setSelected(false);
            defaultFuture.setName("全部");
            tabItemList.add(defaultFuture);
            tabItemList.add(futureItem);
            tabAdapter.setData(tabItemList);
            tabAdapter.notifyDataSetChanged();
        }
        List<Future> itemList = new ArrayList<>();
        if (ValueUtil.isListNotEmpty(futureItem.getSubItem())) {//三级不为空
            for (Future.SubItem bean : futureItem.getSubItem()) {
                Future item = new Future();
                item.setName(bean.getName());
                item.setId(bean.getId());
                item.setType(bean.getType());
                itemList.add(item);
            }
        }
        allChangeItemAdapter.showCheck(false);
        allChangeItemAdapter.setData(itemList);
        allChangeItemAdapter.notifyDataSetChanged();
    }

    /**
     * 三级
     *
     * @param futureItem
     */
    private void showThreeData(Future futureItem) {
        if (ValueUtil.isListNotEmpty(tabItemList)) {
            for (Future bean : tabItemList) {
                bean.setSelected(false);
            }
            tabItemList.add(futureItem);
            tabAdapter.setData(tabItemList);
            tabAdapter.notifyDataSetChanged();
        }
        loadOver = true;
        if (vEmpty == null || ValueUtil.isListEmpty(firstList)) {
            return;
        }
        List<Future> roomItemList = new ArrayList<>();
        for (Future roomItem : firstList) {
            if (ValueUtil.isListNotEmpty(roomItem.getSubItem())) {//二级不为空
                for (Future.SubItem subItem : roomItem.getSubItem()) {
                    if (subItem.getId() == futureItem.getId()) {
                        if (ValueUtil.isListNotEmpty(subItem.getRoomItem())) {
                            for (RoomItem item : subItem.getRoomItem()) {
                                Future bean = new Future();
                                bean.setId(item.getId());
                                bean.setContract(item.getContract());
                                bean.setName(item.getName());
                                bean.setType(item.getType());
                                roomItemList.add(bean);
                            }
                        }
                        break;
                    }
                }
            } else {//二级为空
                if (ValueUtil.isListNotEmpty(futureItem.getRoomItem())) {
                    if (roomItem.getId() == futureItem.getId()) {
                        for (RoomItem item : futureItem.getRoomItem()) {
                            Future bean = new Future();
                            bean.setId(item.getId());
                            bean.setContract(item.getContract());
                            bean.setName(item.getName());
                            bean.setType(item.getType());
                            roomItemList.add(bean);
                        }
                        break;
                    }
                }

            }
        }

        String strJson = SharedUtil.get(Constant.CHANGE_DATA, Constant.HAS_CHNAGE_LIST);
        List<RoomItem> hasList = GsonUtil.fromJson(strJson, new TypeToken<List<RoomItem>>() {
        }.getType());//获取已选中的

        List<Future> hasFutureList = new ArrayList<>();
        if (ValueUtil.isListNotEmpty(hasList)) {
            for (RoomItem item : hasList) {
                Future future = new Future();
                future.setId(item.getId());
                future.setName(item.getName());
                future.setContract(item.getContract());
                future.setType(item.getType());
                hasFutureList.add(future);
            }
        }

        if (ValueUtil.isListNotEmpty(hasChooseList)) {
            hasFutureList.addAll(hasChooseList);
        }
        if (ValueUtil.isListNotEmpty(roomItemList)) {
            vEmpty.setVisibility(View.GONE);
            for (Future bean : roomItemList) {
                bean.setEnd(true);
                if (ValueUtil.isListNotEmpty(hasFutureList)) {//判断自选是否选中
                    for (Future hasBean : hasFutureList) {
                        if (ValueUtil.isStrNotEmpty(hasBean.getContract()) && hasBean.getContract().equals(bean.getContract())) {
                            bean.setCheck(true);
                        }
                    }
                }
            }
            allChangeItemAdapter.showCheck(true);
            allChangeItemAdapter.setData(roomItemList);
        } else {
            allChangeItemAdapter.clearData();
            allChangeItemAdapter.notifyDataSetChanged();
            vEmpty.setVisibility(View.VISIBLE);
            vEmpty.setNoData(Constant.BgColor.BLUE);
        }
    }

    /**
     * 从码表中获取全部菜单
     */
    private void getAllMenus() {
        try {
            List<Future> configList = SharedUtil.ListDataSave.getDataList(Constant.GMETAL_DB, Constant.MARKET_CONFIG, Future.class);
            if (ValueUtil.isListEmpty(configList)) {
                return;
            }
            if (vEmpty == null || vLine == null) {
                return;
            }
            vLine.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
            Future future = new Future();
            future.setSelected(true);
            future.setName("全部");
            if (ValueUtil.isListNotEmpty(tabItemList)) {
                tabItemList.clear();
            }
            tabItemList.add(future);
            rvTab.setAdapter(tabAdapter);
            tabAdapter.setData(tabItemList);

            if (ValueUtil.isListNotEmpty(firstList)) {
                firstList.clear();
            }
            if (ValueUtil.isListNotEmpty(futureItemList)) {
                futureItemList.clear();
            }
            //移除自选、场外期权、利率
            for (Future bean : configList) {
                if (!bean.getType().equals(Constant.MenuType.FOUR.getValue()) && bean.getId() != -1 && !bean.getType().equals(Constant.MenuType.FIVE.getValue())) {
                    firstList.add(bean);
                    futureItemList.add(bean);
                }
            }
            allChangeItemAdapter.setData(futureItemList);
            rvData.setAdapter(allChangeItemAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }


    /**
     * 添加到自选
     *
     * @param hasChooseList
     */
    private void addSubscribeFutures(List<AddFutureParameter> hasChooseList) {
        DialogUtil.waitDialog(getActivity());
        Api.getMarketService().batchAddFileFavoritesCode(hasChooseList)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        //通知自选刷新
                        BaseEvent baseEvent = new BaseEvent();
                        baseEvent.setRefreshMyChoose(true);
                        baseEvent.setRefreshMarketMain(true);
                        BusProvider.getBus().post(baseEvent);
                        if(ValueUtil.isListNotEmpty(hasChooseList)){
                            hasChooseList.clear();
                        }
                        if(ValueUtil.isListNotEmpty(parameterList)){
                            parameterList.clear();
                        }
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(listBaseModel.getMessage());

                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }


}
