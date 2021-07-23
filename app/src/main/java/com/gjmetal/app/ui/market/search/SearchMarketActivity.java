package com.gjmetal.app.ui.market.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.MarketSearchAdapter;
import com.gjmetal.app.adapter.market.MenuChooseAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.MarketSearchEvent;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.search.MenuChoose;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：搜索
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-30 16:21
 */
public class SearchMarketActivity extends BaseActivity {
    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.rvMenu)
    RecyclerView rvMenu;

    private MarketSearchAdapter marketSearchAdapter;
    private MenuChooseAdapter menuChooseAdapter;
    private BaseEvent baseEvent;
    private String searchKey;
    private List<MenuChoose> lastGroupList = new ArrayList<>();
    private List<RoomItem> allContractList = new ArrayList<>();//存所有合约

    @Override
    protected void initView() {
        initTitleSyle(Titlebar.TitleSyle.MARKET_SEARCH, "");
        setContentView(R.layout.activity_market_search);
        KnifeKit.bind(this);
        baseEvent = (BaseEvent) getIntent().getSerializableExtra(Constant.MODEL);
        SocketManager.getInstance().leaveAllRoom();
        titleBar.getEtSearch().setHint(R.string.txt_market_search_hint);
        titleBar.getEtSearch().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                searchKey = s.toString().trim();
                vEmpty.setVisibility(View.GONE);
                rvMenu.setVisibility(View.GONE);
                if (ValueUtil.isStrNotEmpty(searchKey)) {
                    marketSearch(searchKey);
                    titleBar.getCancelSearch().setText("");
                } else {
                    titleBar.getCancelSearch().setVisibility(View.VISIBLE);
                    titleBar.getCancelSearch().setText(R.string.txt_cancel);
                    if (marketSearchAdapter != null) {
                        marketSearchAdapter.clearData();
                        marketSearchAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
        titleBar.getCancelSearch().setVisibility(View.INVISIBLE);
        titleBar.getEtSearch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvMenu.setVisibility(View.GONE);
                titleBar.getEtSearch().setCursorVisible(true);
                titleBar.getCancelSearch().setVisibility(View.VISIBLE);
            }
        });
        titleBar.getCancelSearch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleBar.getCancelSearch().getText().toString().equals("")) {
                    searchAction();
                } else if (titleBar.getCancelSearch().getText().toString().equals(getString(R.string.txt_cancel))) {
                    titleBar.getEtSearch().setCursorVisible(false);
                    titleBar.getEtSearch().setText("");
                    titleBar.getCancelSearch().setVisibility(View.INVISIBLE);
                    rvSearch.setVisibility(View.GONE);
                    vEmpty.setVisibility(View.GONE);
                    rvMenu.setVisibility(View.VISIBLE);
                    ViewUtil.hideInputMethodManager(titleBar.getCancelSearch());
                }

            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        marketSearchAdapter = new MarketSearchAdapter(context, searchKey, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                getFutures(true);
            }
        });
        rvSearch.setLayoutManager(mLayoutManager);
        rvSearch.setAdapter(marketSearchAdapter);
    }

    @Override
    protected void fillData() {
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ValueUtil.isEmpty(baseEvent)) {
            rvMenu.setVisibility(View.GONE);
            return;
        }
        rvMenu.setVisibility(View.VISIBLE);
        if (ValueUtil.isListNotEmpty(baseEvent.getFutureList())) {
            try {
                List<MenuChoose> groupList = new ArrayList<>();
                List<String> groupNameList = new ArrayList<>();
                for (int i = 0; i < baseEvent.getFutureList().size(); i++) {
                    //将所有合约名存放到一起
                    if (ValueUtil.isListNotEmpty(baseEvent.getFutureList().get(i).getSubItem())) {
                        for (int j = 0; j < baseEvent.getFutureList().get(i).getSubItem().size(); j++) {
                            Future.SubItem bean = baseEvent.getFutureList().get(i).getSubItem().get(j);
                            if (ValueUtil.isNotEmpty(bean) && ValueUtil.isListNotEmpty(bean.getRoomItem())) {
                                for (RoomItem item : bean.getRoomItem()) {
                                    allContractList.add(item);
                                }
                            }
                        }
                    }

                    Future future = baseEvent.getFutureList().get(i);
                    if (ValueUtil.isNotEmpty(future) && ValueUtil.isStrNotEmpty(future.getGroupType())) {
                        MenuChoose menuChoose = new MenuChoose(future.getGroupType(), null);
                        if (!groupNameList.contains(future.getGroupType())) {
                            groupNameList.add(future.getGroupType());
                            groupList.add(menuChoose);
                            XLog.d("groupName", baseEvent.getFutureList().get(i).getGroupType());
                        }
                    }
                }
                //组装数据结构
                if (ValueUtil.isListNotEmpty(groupList)) {
                    for (int j = 0; j < groupList.size(); j++) {
                        MenuChoose menuChoose = groupList.get(j);
                        List<Future> childFutureList = new ArrayList<>();
                        for (Future bean : baseEvent.getFutureList()) {
                            if (menuChoose.getGroupType().equals(bean.getGroupType())) {
                                childFutureList.add(bean);
                            }
                        }
                        lastGroupList.add(j, new MenuChoose(menuChoose.getGroupType(), childFutureList));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            menuChooseAdapter = new MenuChooseAdapter(context, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    if (ValueUtil.isEmpty(obj) || ValueUtil.isListEmpty(baseEvent.getFutureList())) {
                        return;
                    }
                    Future item = (Future) obj;
                    int index = 0;
                    for (int k = 0; k < baseEvent.getFutureList().size(); k++) {
                        if (baseEvent.getFutureList().get(k).getId() == item.getId()) {
                            index = k;
                            break;
                        }
                    }
                    SharedUtil.putInt(Constant.MAIN_PAGE_SELECTED, 0);
                    BusProvider.getBus().post(new MarketSearchEvent(index));
                    finish();
                }
            });
            rvMenu.setLayoutManager(new LinearLayoutManager(context));
            rvMenu.setAdapter(menuChooseAdapter);
            menuChooseAdapter.setData(lastGroupList);
            getFutures(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (ValueUtil.isNotEmpty(baseEvent) && baseEvent.isLogin()) {
            getFutures(true);
        }
    }

    private void searchAction() {
        if (ValueUtil.isStrEmpty(searchKey)) {
            ToastUtil.showToast("请输入搜索关键字");
            return;
        }
        ViewUtil.hideInputMethodManager(titleBar.getEtSearch());
        marketSearch(searchKey);
    }


    /**
     * 搜索
     *
     * @param searchName
     */
    private void marketSearch(String searchName) {
        if (ValueUtil.isStrNotEmpty(searchName)) {
            List<RoomItem> searchList = new ArrayList<>();
            String upCaseKey = searchName.toUpperCase();//大写
            String lowerCaseKey = searchName.toLowerCase();//小写
            if (ValueUtil.isListNotEmpty(allContractList)) {
                for (RoomItem bean : allContractList) {
                    if (ValueUtil.isStrNotEmpty(bean.getName())) {
                        if (bean.getName().contains(searchName) || bean.getName().contains(upCaseKey) || bean.getName().contains(lowerCaseKey)) {
                            searchList.add(bean);
                        }
                    }
                    if (ValueUtil.isStrNotEmpty(bean.getContract())) {
                        if (bean.getContract().contains(searchName) || bean.getContract().contains(lowerCaseKey) || bean.getContract().contains(upCaseKey)) {
                            searchList.add(bean);
                        }
                    }
                }
            }

            updateUI(searchList);
        }
    }

    private void updateUI(List<RoomItem> searchList) {
        String strJson = SharedUtil.get(Constant.CHANGE_DATA, Constant.HAS_CHNAGE_LIST);
        List<RoomItem> hasChangeList = GsonUtil.fromJson(strJson, new TypeToken<List<RoomItem>>() {
        }.getType());//获取已选中的
        if (ValueUtil.isListNotEmpty(searchList)) {
            for (RoomItem bean : searchList) {
                if (ValueUtil.isListNotEmpty(hasChangeList)) {//判断自选是否选中
                    for (RoomItem hasBean : hasChangeList) {
                        if (ValueUtil.isStrNotEmpty(hasBean.getContract()) && hasBean.getContract().equals(bean.getContract())) {
                            bean.setCheck(true);
                            bean.setId(hasBean.getId());
                        }
                    }
                }
            }
        }
        marketSearchAdapter.setData(searchList);
        marketSearchAdapter.setSearchKey(searchKey);
        marketSearchAdapter.notifyDataSetChanged();
        rvMenu.setVisibility(View.GONE);
        if (ValueUtil.isListEmpty(searchList)) {
            rvSearch.setVisibility(View.GONE);
            vEmpty.setVisibility(View.VISIBLE);
            vEmpty.setNoData(Constant.BgColor.BLUE, R.string.search_no_data, R.mipmap.ic_common_search_nothing_light);
        } else {
            rvSearch.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
        }
    }


    /**
     * 自选数据
     *
     * @param isCallBack
     */
    private void getFutures(final boolean isCallBack) {
        if (!User.getInstance().isLoginIng()) {
            return;
        }
        Api.getMarketService().getFutures("future-quote")
                .compose(XApi.<BaseModel<List<RoomItem>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<RoomItem>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<RoomItem>>>() {
                    @Override
                    public void onNext(BaseModel<List<RoomItem>> listBaseModel) {
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            String strJson = GsonUtil.toJson(listBaseModel.getData());
                            SharedUtil.put(Constant.CHANGE_DATA, Constant.HAS_CHNAGE_LIST, strJson);//保存选中数据
                        } else {
                            SharedUtil.clearData(Constant.CHANGE_DATA);
                        }
                        if (isCallBack) {
                            if (marketSearchAdapter != null && ValueUtil.isListNotEmpty(marketSearchAdapter.getDataSource())) {
                                if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                                    for (RoomItem bean : marketSearchAdapter.getDataSource()) {
                                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {//判断自选是否选中
                                            for (RoomItem hasBean : listBaseModel.getData()) {
                                                if (ValueUtil.isStrNotEmpty(hasBean.getContract()) && hasBean.getContract().equals(bean.getContract())) {
                                                    bean.setCheck(true);
                                                    bean.setId(hasBean.getId());
                                                }
                                            }
                                        }
                                    }
                                    marketSearchAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }


    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Router.newIntent(context)
                    .to(SearchMarketActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }

    public static void launch(Activity context, BaseEvent baseEvent) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, baseEvent);
            Router.newIntent(context)
                    .to(SearchMarketActivity.class)
                    .data(bundle)
                    .launch();
        }
    }
}
