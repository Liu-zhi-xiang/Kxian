package com.gjmetal.app.ui.information;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.information.InformationListAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.event.CheckInfomationTabsEvent;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.event.FontEvent;
import com.gjmetal.app.event.ReadStateEvent;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.CollectBean;
import com.gjmetal.app.model.information.InfoMationCheckTabBean;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.MyVideoPlay;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;

/**
 * 资讯列表fragment
 * Created by huangb on 2018/4/2.
 */

@SuppressLint("ValidFragment")
public class InformationChildFragment extends DelayerFragment implements InformationListAdapter.OnCollectListener {
    @BindView(R.id.rvFutureChild)
    RecyclerView rvFutureChild;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vPermission)
    ApplyReadView vPermission;
    private String type;
    private Integer colId;
    private InformationListAdapter mAdapter;
    private int page = 1;
    //是否刷新
    boolean isRefresh = true;
    private List<String> mTbas = new ArrayList<>();
    private String mCacheKey;
    private int pageSize = AppUtil.getPageSize(80);
    private String function;

    @Override
    protected int setRootView() {
        return R.layout.fragment_information_list;
    }

    public InformationChildFragment() {

    }

    public InformationChildFragment(String type, Integer colId) {
        this.type = type;
        this.colId = colId;
    }

    public void initView() {
        //注册
        BusProvider.getBus().register(this);
        indexs = colId;
        function = (colId) + "zixun";
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mCacheKey = type + colId;
        rvFutureChild.setLayoutManager(mLayoutManager);
        if (ValueUtil.isNotEmpty(type) && type.equals(getString(R.string.dingyue))) {
            mAdapter = new InformationListAdapter(getActivity(), mCacheKey);
            mAdapter.setOnCollectListener(this);
            rvFutureChild.setAdapter(mAdapter);
            if (User.getInstance().isLoginIng()) {
                InformationFragment.getInformationVIP(getContext(), String.valueOf(colId), function);
            } else {
                showAddTags(true);
            }
        } else {
            showAddTags(false);
            mAdapter = new InformationListAdapter(getActivity(), mCacheKey);
            rvFutureChild.setAdapter(mAdapter);
            mAdapter.setOnCollectListener(this);
        }
        rvFutureChild.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                if (jzVideoPlayer != null && jzVideoPlayer.currentScreen != MyVideoPlay.SCREEN_WINDOW_FULLSCREEN) {
                    JZVideoPlayer.releaseAllVideos();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRefresh = true;
                        InformationFragment.getInformationVIP(getContext(), String.valueOf(colId), function);
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                isRefresh = false;
                InformationFragment.getInformationVIP(getContext(), String.valueOf(colId), function);
            }
        });

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setRefreshHeader(new MyRefreshHender(getContext(), ContextCompat.getColor(getContext(), R.color.cffffff)));
        refreshLayout.setHeaderHeight(60);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin() && User.getInstance().isLoginIng() && isAdded()) {
            if (refreshLayout != null) {
                showAddTags(type.equals(getString(R.string.dingyue)));
                refreshLayout.autoRefresh();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ReadStateEvent(ReadStateEvent readStateEvent) {
        if (isAdded() && readStateEvent.isReadInformation) {
            for (int j = 0; j < mAdapter.getDataSource().size(); j++) {
                if (readStateEvent.getNewsId() == mAdapter.getDataSource().get(j).getNewsId()) {
                    GjUtil.setInforMationReadStasus(mCacheKey, readStateEvent.getNewsId());
                    mAdapter.getDataSource().get(j).setHasRead(true);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CollectEvent(CollectEvent collectEvent) {
        if (isAdded() && ValueUtil.isStrNotEmpty(type) && !type.equals(getString(R.string.flash))) {
            for (int i = 0; i < collectEvent.mList.size(); i++) {
                for (int j = 0; j < mAdapter.getDataSource().size(); j++) {
                    if (collectEvent.mList.get(i).getNewsId() == mAdapter.getDataSource().get(j).getNewsId()) {
                        if (collectEvent.isFromeWebView) {//从资讯详情过来的，默认设置成已读
                            mAdapter.getDataSource().get(j).setHasRead(true);
                        }
                        mAdapter.getDataSource().get(j).setCollect(!mAdapter.getDataSource().get(j).isCollect());
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CheckInfomationTabsEvent(CheckInfomationTabsEvent baseEvent) {
        //是否是订阅
        if (isAdded() && type.equals(getString(R.string.dingyue))) {
            mTbas.clear();
            refreshLayout.autoRefresh();
        } else {
            if (!isHidden() && refreshLayout != null) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FontEvent(FontEvent fontEvent) {
        if (isAdded() && ValueUtil.isStrNotEmpty(type) && !type.equals(getString(R.string.flash))) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ApplyEvent(ApplyEvent applyEvent) {
        XLog.d("InformationFragment", "ApplyEvent");
        ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
            @Override
            public void onSubscibeDialogCancel() {
                XLog.d("InformationFragment", "onSubscibeDialogCancel");
                if (!isAdded()) {
                    return;
                }
                if (refreshLayout != null && rvFutureChild != null) {
                    refreshLayout.finishRefresh();
                    rvFutureChild.setVisibility(View.GONE);
                }
                if (vEmpty != null) {
                    vEmpty.setVisibility(View.GONE);
                }
                if (vPermission != null) {
                    vPermission.setVisibility(View.VISIBLE);
                    vPermission.showInformationViP(User.getInstance().isLoginIng(), new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            LoginActivity.launch(getActivity());
                        }
                    });
                }
            }

            @Override
            public void onSubscibeDialogShow() {
                XLog.d("InformationFragment", "onSubscibeDialogShow");
                if (!isAdded()) {
                    return;
                }
                if (refreshLayout != null && rvFutureChild != null) {
                    refreshLayout.finishRefresh();
                    rvFutureChild.setVisibility(View.GONE);
                }
                if (vEmpty != null) {
                    vEmpty.setVisibility(View.GONE);
                }
                if (vPermission != null) {
                    vPermission.setVisibility(View.VISIBLE);
                    vPermission.showInformationViP(User.getInstance().isLoginIng(), new BaseCallBack() {
                        @Override
                        public void back(Object obj) {

                        }
                    });
                }
            }

            @Override
            public void onSubscibeYesShow() {
                XLog.d("InformationFragment", "onSubscibeYesShow");
                if (!isAdded()) {
                    return;
                }
                if (refreshLayout != null && rvFutureChild != null) {
                    rvFutureChild.setVisibility(View.VISIBLE);
                }
                if (vEmpty != null) {
                    vEmpty.setVisibility(View.GONE);
                }
                if (vPermission != null)
                    vPermission.setVisibility(View.GONE);
                getList();
            }

            @Override
            public void onSubscibeError(NetError error) {
                showAgainLoad(error);
            }

            @Override
            public void onUnknown() {
                if (!isAdded()) {
                    return;
                }
                if (refreshLayout != null && rvFutureChild != null) {
                    refreshLayout.finishRefresh();
                    rvFutureChild.setVisibility(View.GONE);
                }
                if (ValueUtil.isStrNotEmpty(type) && !type.equals(getString(R.string.dingyue))) {
                    if (vEmpty != null) {
                        vEmpty.setVisibility(View.GONE);
                    }
                }
                if (vPermission != null) {
                    vPermission.setVisibility(View.VISIBLE);
                    vPermission.showInformationViP(false, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            LoginActivity.launch(getActivity());
                        }
                    });
                }
            }
        });
    }


    /**
     * 添加订阅
     */
    private void showAddTags(boolean show) {
        if (rvFutureChild != null) {
            rvFutureChild.setVisibility(View.GONE);
        }
        vEmpty.showAddHint(Constant.BgColor.WHITE, R.mipmap.ic_future_add_nor, R.string.txt_add_information_tag, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getInstance().isLoginIng()) {
                    InfomationTabsActivity.launch(getActivity());
                } else {
                    LoginActivity.launch((Activity) getContext());
                }
            }
        });
        vEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //获取数据
    private void getList() {
        if (!isAdded()) {
            return;
        }
        if (isRefresh)
            page = 1;
        //订阅
        if (ValueUtil.isStrNotEmpty(type) && type.equals(getString(R.string.dingyue))) {
            if (!User.getInstance().isLoginIng()) {
                mAdapter.clearData();
                if (!isRefresh) {
                    refreshLayout.finishLoadMore();
                } else {
                    refreshLayout.finishRefresh();
                }
                mAdapter.notifyDataSetChanged();
                showAddTags(true);
                LoginActivity.launch(getActivity());
                return;
            }
            //获取订阅标签
            Api.getInformationService().queryTagAndCate().
                    compose(XApi.<BaseModel<List<InfoMationCheckTabBean>>>getApiTransformer())
                    .compose(XApi.<BaseModel<List<InfoMationCheckTabBean>>>getScheduler())
                    .subscribe(new ApiSubscriber<BaseModel<List<InfoMationCheckTabBean>>>() {
                        @Override
                        public void onNext(BaseModel<List<InfoMationCheckTabBean>> listBaseModel) {
                            GjUtil.checkActState(getActivity());
                            if (refreshLayout == null || vEmpty == null) {
                                return;
                            }
                            if (rvFutureChild != null) {
                                rvFutureChild.setVisibility(View.VISIBLE);
                            }
                            if (listBaseModel.getData() != null) {
                                mTbas.clear();
                                for (int i = 0; i < listBaseModel.getData().size(); i++) {
                                    for (int j = 0; j < listBaseModel.getData().get(i).getChildTagList().size(); j++) {
                                        if (listBaseModel.getData().get(i).getChildTagList().get(j).isSub()) {//已选标签
                                            mTbas.add(listBaseModel.getData().get(i).getChildTagList().get(j).getTagId() + "");
                                        }
                                    }
                                }
                                getDingyue();
                            } else {
                                vEmpty.setNoData(Constant.BgColor.WHITE);
                                mAdapter.clearData();
                                if (!isRefresh)
                                    refreshLayout.finishLoadMore();
                                else
                                    refreshLayout.finishRefresh();
                            }
                        }

                        @Override
                        protected void onFail(NetError error) {
                            GjUtil.checkActState(getActivity());
                            if (refreshLayout == null) {
                                return;
                            }
                            if (!isRefresh) {
                                refreshLayout.finishLoadMore();
                            } else {
                                refreshLayout.finishRefresh();
                            }
                            if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                                LoginActivity.launch(getActivity());
                                showAddTags(true);
                            } else {
                                GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                                    @Override
                                    public void back(Object obj) {
                                        refreshLayout.autoRefresh();
                                    }
                                }, rvFutureChild);
                            }


                        }
                    });
        } else {
            //获取正常资讯列表
            HashMap<String, Object> params = new HashMap<>();
            params.put("colId", String.valueOf(colId));
            params.put("pageSize", pageSize + "");
            params.put("currentPage", page + "");
            Api.getInformationService().getInformationList(params)
                    .compose(XApi.<BaseModel<InformationContentBean>>getApiTransformer())
                    .compose(XApi.<BaseModel<InformationContentBean>>getScheduler())
                    .subscribe(new ApiSubscriber<BaseModel<InformationContentBean>>() {
                        @Override
                        public void onNext(BaseModel<InformationContentBean> listBaseModel) {
                            GjUtil.checkActState(getActivity());
                            if (rvFutureChild != null) {
                                rvFutureChild.setVisibility(View.VISIBLE);
                            }
                            if (listBaseModel.getData() != null) {
                                List<InformationContentBean.ListBean> listBeans = listBaseModel.getData().getList();
                                GjUtil.getInformationReadStatus(mCacheKey, listBeans);//获取缓存阅读状态
                                if (isRefresh) {
                                    mAdapter.setData(listBeans);
                                } else {
                                    mAdapter.addData(listBeans);
                                }
                                if (vEmpty == null) {
                                    return;
                                }
                                vEmpty.setNoData(Constant.BgColor.WHITE);
                                vEmpty.setVisibility(mAdapter.getDataSource().size() == 0 ? View.VISIBLE : View.GONE);
                                if (page * pageSize < listBaseModel.getData().getTotal()) {
                                    page++;
                                    refreshLayout.setEnableLoadMore(true);
                                } else {
                                    refreshLayout.setEnableLoadMore(false);
                                }
                            } else {
                                vEmpty.setNoData(Constant.BgColor.WHITE);
                            }

                            if (!isRefresh)
                                refreshLayout.finishLoadMore();
                            else
                                refreshLayout.finishRefresh();
                        }

                        @Override
                        protected void onFail(NetError error) {
                            GjUtil.checkActState(getActivity());
                            if (vEmpty == null || refreshLayout == null) {
                                return;
                            }
                            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                                @Override
                                public void back(Object obj) {
                                    refreshLayout.autoRefresh();
                                }
                            }, rvFutureChild);
                            if (!isRefresh)
                                refreshLayout.finishLoadMore();
                            else
                                refreshLayout.finishRefresh();
                        }
                    });
        }

    }


    //获取订阅列表数据
    public void getDingyue() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", pageSize + "");
        params.put("currentPage", page + "");
        Api.getInformationService().getInformationDingYueList(params)
                .compose(XApi.<BaseModel<InformationContentBean>>getApiTransformer())
                .compose(XApi.<BaseModel<InformationContentBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<InformationContentBean>>() {
                    @Override
                    public void onNext(BaseModel<InformationContentBean> listBaseModel) {
                        GjUtil.checkActState(getActivity());
                        if (refreshLayout == null || vEmpty == null) {
                            return;
                        }
                        if (listBaseModel.getData() != null) {
                            try {//解决资讯特殊情况有重复数据的问题
                                List<InformationContentBean.ListBean> listBeans = listBaseModel.getData().getList();
                                GjUtil.getInformationReadStatus(mCacheKey, listBeans);//获取缓存阅读状态
                                if (ValueUtil.isListNotEmpty(mAdapter.getDataSource()) && ValueUtil.isListNotEmpty(listBeans)) {
                                    for (InformationContentBean.ListBean bean : mAdapter.getDataSource()) {
                                        for (InformationContentBean.ListBean listBean : listBeans) {
                                            if (bean.getNewsId() == listBean.getNewsId()) {
                                                listBeans.remove(bean);
                                            }
                                        }
                                    }
                                }

                                if (isRefresh) {
                                    mAdapter.setData(listBeans);
                                } else {
                                    mAdapter.addData(listBeans);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (page * pageSize < listBaseModel.getData().getTotal()) {
                                page++;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        } else {
                            mAdapter.clearData();
                        }
                        if (ValueUtil.isListEmpty(mTbas)) {
                            showAddTags(mAdapter.getDataSource().size() == 0 && (ValueUtil.isStrNotEmpty(type) && type.equals(getString(R.string.dingyue))));
                        } else {
                            vEmpty.setNoData(Constant.BgColor.WHITE);
                            vEmpty.setVisibility(mAdapter.getDataSource().size() == 0 ? View.VISIBLE : View.GONE);
                        }
                        if (!isRefresh) {
                            refreshLayout.finishLoadMore();
                        } else {
                            refreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.checkActState(getActivity());
                        if (vEmpty == null || refreshLayout == null) {
                            return;
                        }
                        if (!isRefresh) {
                            refreshLayout.finishLoadMore();
                        } else {
                            refreshLayout.finishRefresh();
                        }
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                            showAddTags(true);
                        } else {
                            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                                @Override
                                public void back(Object obj) {
                                    refreshLayout.autoRefresh();
                                }
                            }, rvFutureChild);
                        }
                    }

                });
    }


    /**
     * 收藏
     */
    @Override
    public void OnCollect(final InformationContentBean.ListBean bean, final int position) {
        if (!User.getInstance().isLoginIng()) {
            LoginActivity.launch((Activity) getContext());
            return;
        }
        List<CollectBean> mList = new ArrayList<>();
        mList.add(new CollectBean(bean.getNewsId(), bean.isCollect() ? "2" : "1"));
        Api.getInformationService().collectNew(mList)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        mAdapter.getDataSource().get(position).setCollect(!bean.isCollect());
                        mAdapter.notifyDataSetChanged();
                        ToastUtil.showToast(bean.isCollect() ? "已收藏" : "取消收藏");
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                        }
                    }
                });

    }


    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    /**
     * 显示刷新列表
     */
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (!isVisible) {
            if (mAdapter != null)
                mAdapter.setDimissVoiceAndVideo();
        } else {
            if (refreshLayout != null) {
                if (ValueUtil.isStrNotEmpty(type) && type.equals(getString(R.string.dingyue))) {
                    if (mTbas.size() > 0) {
                        if (!User.getInstance().isLoginIng()) {
                            return;
                        }
                        refreshData();
                    }
                } else {
                    refreshData();

                }
            }
        }
    }

    private void refreshData() {
        if (mAdapter != null) {
            if (refreshLayout != null) {
                if (ValueUtil.isListEmpty(mAdapter.getDataSource())) {
                    if (vPermission != null && vPermission.getVisibility() == View.VISIBLE) {
                        InformationFragment.getInformationVIP(getContext(), String.valueOf(colId), function);
                    } else {
                        refreshLayout.autoRefresh();
                    }
                } else {
                    if (vPermission != null && vPermission.getVisibility() == View.VISIBLE) {
                        InformationFragment.getInformationVIP(getContext(), String.valueOf(colId), function);
                    }
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null && ValueUtil.isStrNotEmpty(type) && !type.equals(getString(R.string.flash))) {
            mAdapter.setDimissVoiceAndVideo();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null && ValueUtil.isStrNotEmpty(type) && !type.equals(getString(R.string.flash))) {
            mAdapter.setDimissVoiceAndVideo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//去掉信息栏
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }

    private void showAgainLoad(NetError error) {
        if (refreshLayout == null || rvFutureChild == null || vEmpty == null || vPermission == null) {
            return;
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (refreshLayout != null) {
                    refreshLayout.autoRefresh();
                }
            }
        }, rvFutureChild);
        if (!isRefresh) {
            refreshLayout.finishLoadMore();
        } else {
            refreshLayout.finishRefresh();
        }
    }

}

