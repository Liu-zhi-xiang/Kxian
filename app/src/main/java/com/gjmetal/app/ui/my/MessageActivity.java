package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.my.MessageAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.my.MessageBean;
import com.gjmetal.app.util.AppUtil;
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
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Description：消息界面
 * Author: css
 * Email: 1175558532@qq.com
 * Date: 2018-12-20 17:15
 */

public class MessageActivity extends XBaseActivity implements MessageAdapter.OnDeleteMessageLstener {
    @BindView(R.id.rvFutureChild)
    RecyclerView rvFutureChild;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private MessageAdapter mAdapter;
    private int page = 1;
    private int pageSize = AppUtil.getPageSize(55);
    //是否刷新
    boolean isRefresh = true;

    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(context)
                    .to(MessageActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }

    @Override
    protected int setRootView() {
        return R.layout.activity_my_message;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getResources().getString(R.string.mine_message));
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this,R.color.c9EB2CD));
        titleBar.rightLayout.setVisibility(View.VISIBLE);
        titleBar.tvRight.setVisibility(View.VISIBLE);
        titleBar.tvRight.setText(R.string.clean);
        titleBar.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getDataSource().size() <= 0) {
                    return;
                }
                new HintDialog(context, getString(R.string.txt_clean_message), new DialogCallBack() {
                    @Override
                    public void onSure() {
                        clearMessage();
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
        if (baseEvent.isLogin() && refreshLayout != null) {
            refreshLayout.autoRefresh();
        }
    }
    @Override
    protected void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MessageAdapter(this);
        mAdapter.addDeleteMessageListener(this);
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
            public void onLoadMore(RefreshLayout refreshLayout) {
                isRefresh = false;
                getList();
            }
        });

        refreshLayout.setRefreshHeader(new MyRefreshHender(this, ContextCompat.getColor(this,R.color.cffffff)));
        refreshLayout.setHeaderHeight(60);

        refreshLayout.autoRefresh();
    }

    @Override
    protected void fillData() {
    }

    /**
     * 删除
     */
    private void deleteMsg(final List<MessageBean.ItemListBean> itemList, final int position) {
        DialogUtil.waitDialog(context);
        List<String> list = new ArrayList<>();
        list.add(itemList.get(position).getId() + "");
        Api.getMyService().deleteMsg(list)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        DialogUtil.dismissDialog();
                        itemList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        if (itemList.size() <= 0) {
                            isRefresh = true;
                            getList();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                    }
                });
    }

    /**
     * 清空
     */
    private void clearMessage() {
        DialogUtil.waitDialog(context);
        Api.getMyService().clearmsg()
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if(rvFutureChild==null){
                            return;
                        }
                        if(mAdapter!=null){
                            mAdapter.getDataSource().clear();
                        }
                        rvFutureChild.setVisibility(View.GONE);
                        vEmpty.setVisibility(View.VISIBLE);
                        vEmpty.setNoData(Constant.BgColor.WHITE,R.string.no_message);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });
    }

    //获取数据
    private void getList() {
        if (isRefresh)
            page = 1;
        Api.getMyService().getMessageList(page, pageSize)
                .compose(XApi.<BaseModel<MessageBean>>getApiTransformer())
                .compose(XApi.<BaseModel<MessageBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<MessageBean>>() {
                    @Override
                    public void onNext(BaseModel<MessageBean> listBaseModel) {
                        if (refreshLayout == null || vEmpty == null) {
                            return;
                        }
                        if (listBaseModel.getData() != null) {
                            vEmpty.setVisibility(View.GONE);
                            rvFutureChild.setVisibility(View.VISIBLE);
                            if (isRefresh) {
                                mAdapter.setData(listBaseModel.getData().getItemList());
                            } else {
                                mAdapter.addData(listBaseModel.getData().getItemList());
                            }
                            if (page * pageSize < listBaseModel.getData().getTotalCount()) {
                                page++;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        } else {
                            if (vEmpty != null) {
                                vEmpty.setVisibility(View.VISIBLE);
                                vEmpty.setNoData(Constant.BgColor.WHITE,R.string.no_message);
                            }
                            if(refreshLayout!=null){
                                refreshLayout.setEnableLoadMore(false);
                            }
                            if(rvFutureChild!=null){
                                rvFutureChild.setVisibility(View.GONE);
                            }
                        }
                        DialogUtil.dismissDialog();
                        if (!isRefresh)
                            refreshLayout.finishLoadMore();
                        else
                            refreshLayout.finishRefresh();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        showAgainLoad(error);
                        if (refreshLayout != null) {
                            if (!isRefresh)
                                refreshLayout.finishLoadMore();
                            else
                                refreshLayout.finishRefresh();
                        }

                    }
                });
    }

    private void showAgainLoad(NetError error) {
        mAdapter.setData(new ArrayList<MessageBean.ItemListBean>());
        GjUtil.showEmptyHint(context, Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    vEmpty.setVisibility(View.GONE);
                    isRefresh = true;
                    DialogUtil.waitDialog(MessageActivity.this);
                    getList();

                }
            }
        }, rvFutureChild);

    }

    /**
     * 读取消息
     */
    public void redMsgnum(final List<MessageBean.ItemListBean> itemList, final int position) {
        Api.getMyService().readMsg(itemList.get(position).getId() + "")
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        itemList.get(position).setStatus(1);
                        mAdapter.notifyDataSetChanged();
                        launch(itemList.get(position));

                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }


    /**
     * 删除消息
     */
    @Override
    public void OnDelete(View view, List<MessageBean.ItemListBean> itemList, int position) {
        setPopWindow(this, view, itemList, position);
    }

    //点击删除的popwindow
    private void setPopWindow(Context c, View view, final List<MessageBean.ItemListBean> itemList, final int position) {
        final PopwindowDeleteManager popwindowDeleteManager = new PopwindowDeleteManager(c);
        popwindowDeleteManager.setPopWindow(c, view);
        popwindowDeleteManager.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindowDeleteManager.dismiss();
                deleteMsg(itemList, position);
            }
        });
    }

    /**
     * 读取消息
     */
    @Override
    public void redMsg(List<MessageBean.ItemListBean> itemList, int position) {
        if (itemList.get(position).getStatus() == 0) {
            redMsgnum(itemList, position);
        } else {
            launch(itemList.get(position));
        }
    }

    public void launch(MessageBean.ItemListBean itemListBean) {
        if (TimeUtils.isCanClick()) {
            Intent intent = new Intent(this, MessageDetailActivity.class);
            intent.putExtra("data", itemListBean);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                isRefresh = true;
                getList();
            }
        }
    }
}
