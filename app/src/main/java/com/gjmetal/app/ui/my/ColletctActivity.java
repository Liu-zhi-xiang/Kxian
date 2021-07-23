package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.information.InfomationCollectAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.event.FontEvent;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.information.CollectBean;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.PopwindowDeleteManager;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApiSubscriber;
import com.gjmetal.star.router.Router;
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
import io.reactivex.functions.Consumer;

/**
 * 收藏列表
 * Created by huangb on 2018/4/8.
 */

public class ColletctActivity extends XBaseActivity implements InfomationCollectAdapter.OnCollectListener {
    @BindView(R.id.rvFutureChild)
    RecyclerView rvFutureChild;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private InfomationCollectAdapter mAdapter;
    private int page = 1;
    //是否刷新
    private boolean isRefresh = true;

    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(context)
                    .to(ColletctActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }

    @Override
    protected int setRootView() {
        return R.layout.activity_collect;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getResources().getString(R.string.mine_collect));
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this,R.color.c9EB2CD));
        titleBar.rightLayout.setVisibility(View.VISIBLE);
        titleBar.tvRight.setVisibility(View.VISIBLE);
        titleBar.tvRight.setText(R.string.clean);

        titleBar.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getDataSource().size() <= 0) {
                    ToastUtil.showToast(getString(R.string.no_data));
                    return;
                }
                new HintDialog(context, getString(R.string.txt_clean_colletct), new DialogCallBack() {
                    @Override
                    public void onSure() {
                        clearNewCollect();
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin() && User.getInstance().isLoginIng()) {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh();
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CollectEvent(CollectEvent collectEvent) {
        if (refreshLayout != null && mAdapter != null) {
            refreshLayout.autoRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FontEvent(FontEvent fontEvent) {
        if(mAdapter!=null) mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new InfomationCollectAdapter(this);
        mAdapter.setOnCollectListener(this);
        rvFutureChild.setLayoutManager(mLayoutManager);
        rvFutureChild.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRefresh = true;
                        getList();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                isRefresh = false;
                getList();
            }
        });
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh();

        refreshLayout.setRefreshHeader(new MyRefreshHender(this, ContextCompat.getColor(this,R.color.cffffff)));
        refreshLayout.setHeaderHeight(60);

        initRecyclerView();

    }

    /**
     * 获取数组
     */
    private void getList() {
        if (isRefresh)
            page = 1;
        HashMap<String, String> params = new HashMap<>();
        params.put("pageSize", Constant.PAGE_SIZE + "");
        params.put("currentPage", page + "");
        addSubscription(Api.getInformationService().queryCollect(params), new XApiSubscriber<BaseModel<InformationContentBean>>() {
            @Override
            protected void onFinish() {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

            }

            @Override
            protected void onSuccess(BaseModel<InformationContentBean> listBaseModel) {
                if (!isRefresh)
                    refreshLayout.finishLoadMore();
                else {
                    refreshLayout.finishRefresh();
                }
                if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                    if (listBaseModel.getData().getList() != null && listBaseModel.getData().getList().size() > 0) {
                        if (vEmpty == null || refreshLayout == null) {
                            return;
                        }
                        if (isRefresh) {
                            mAdapter.setData(listBaseModel.getData().getList());
                        } else {
                            mAdapter.addData(listBaseModel.getData().getList());
                        }
                        if (page * Constant.PAGE_SIZE < listBaseModel.getData().getTotal()) {
                            page++;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                        }
                    } else {
                        vEmpty.setTextNoData("没有收藏");
                        showAgainLoad(null);
                    }
                } else {
                    vEmpty.setTextNoData("没有收藏");
                    showAgainLoad(null);
                }
                DialogUtil.dismissDialog();
            }

            @Override
            protected void onFail(NetError error) {
                showAgainLoad(error);
                DialogUtil.dismissDialog();
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh(false);
                }
            }
        });

    }

    private void showAgainLoad(NetError error) {
        mAdapter.setData(new ArrayList<InformationContentBean.ListBean>());
        GjUtil.showEmptyHint(context, Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    vEmpty.setVisibility(View.GONE);
                    isRefresh = true;
                    DialogUtil.waitDialog(ColletctActivity.this);
                    getList();
                }
            }
        }, refreshLayout);

    }


    /**
     * 滑动监听
     */
    private void initRecyclerView() {
        rvFutureChild.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 0)
                    JZVideoPlayer.releaseAllVideos();
            }
        });

    }

    //取消收藏
    private void cancleCollect(final List<InformationContentBean.ListBean> datalist, final int position) {
        final List<CollectBean> mList = new ArrayList<>();
        mList.add(new CollectBean(datalist.get(position).getNewsId(), "2"));
        DialogUtil.waitDialog(this, getString(R.string.is_nocollect));
        addSubscription(Api.getInformationService().collectNew(mList), new XApiSubscriber<BaseModel<String>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(BaseModel<String> listBaseModel) {
                if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                    datalist.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
                mAdapter.setDimissVoiceAndVideo();
                DialogUtil.dismissDialog();


            }

            @Override
            protected void onFail(NetError error) {
                refreshLayout.finishRefresh();
                DialogUtil.dismissDialog();
                ToastUtil.showToast(error.getMessage());
            }
        });

    }

    //取消所有收藏
    private void clearNewCollect() {
        DialogUtil.waitDialog(this, getString(R.string.is_noallcollect));
        addSubscription(Api.getInformationService().clearNewCollect(), new XApiSubscriber<BaseModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(BaseModel listBaseModel) {
                if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                    List<InformationContentBean.ListBean> datalist = new ArrayList<>();
                    mAdapter.setData(datalist);
                    vEmpty.setTextNoData("没有收藏");
                    showAgainLoad(null);
                }
                DialogUtil.dismissDialog();


            }

            @Override
            protected void onFail(NetError error) {
                DialogUtil.dismissDialog();
                ToastUtil.showToast(error.getMessage());
            }
        });

    }

    @Override
    protected void fillData() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.setDimissVoiceAndVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.setDimissVoiceAndVideo();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    //点击删除的popwindow
    private void setPopWindow(Context c, View view, final List<InformationContentBean.ListBean> itemList, final int position) {
        final PopwindowDeleteManager popwindowDeleteManager = new PopwindowDeleteManager(c);
        popwindowDeleteManager.setPopWindow(c, view);
        popwindowDeleteManager.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindowDeleteManager.dismiss();
                cancleCollect(itemList, position);
            }
        });
    }

    @Override
    public void OnCollect(View view, List<InformationContentBean.ListBean> datalist, int position) {
        setPopWindow(this, view, datalist, position);
    }
}
