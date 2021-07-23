package com.gjmetal.app.ui.my.warn;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ExpandableListView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.my.MyBaseExpandableListAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.my.UserWarnListModel;
import com.gjmetal.app.model.my.WarnGroup;
import com.gjmetal.app.model.my.WarnItem;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description 我的预警
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-19 10:19
 */

public class WarningUserActivity extends BaseActivity {
    @BindView(R.id.elvWareListChild)
    ExpandableListView elvWareListChild;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    MyBaseExpandableListAdapter mMyBaseExpandableListAdapter;
    private ArrayList<WarnGroup> mGroupDatas = null;
    private ArrayList<WarnItem> mItems = null;
    private boolean mIsUpdate = false;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_warning_user);
    }

    @Override
    protected void fillData() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getResources().getString(R.string.user_warning));
        //数据准备
        mGroupDatas = new ArrayList<>();
        refreshLayout.setRefreshHeader(new MyRefreshHender(context, ContextCompat.getColor(context,R.color.c2A2D4F)));
        refreshLayout.setHeaderHeight(60);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(ValueUtil.isListNotEmpty(mGroupDatas)){
                            mGroupDatas.clear();
                        }
                        prepareData();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.autoRefresh();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isRereshWarning()) {
            mIsUpdate = true;
            baseEvent.setRereshWarning(false);
            prepareData();
        }
    }
    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(context)
                    .to(WarningUserActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }


    /**
     * 组装数据
     *
     * @return
     */
    private void prepareData() {
        Api.getMyService().userMonitorList()
                .compose(XApi.<BaseModel<List<UserWarnListModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<UserWarnListModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<UserWarnListModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<UserWarnListModel>> listBaseModel) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                        mGroupDatas.clear();
                        if (ValueUtil.isEmpty(listBaseModel)) return;
                        List<UserWarnListModel> userWarnListModels = listBaseModel.getData();

                        if (ValueUtil.isListEmpty(userWarnListModels)) {
                            vEmpty.setVisibility(View.VISIBLE);
                            elvWareListChild.setVisibility(View.GONE);
                            vEmpty.setNoData(Constant.BgColor.BLUE,R.string.not_warning);
                            return;
                        }else {
                            vEmpty.setVisibility(View.GONE);
                            elvWareListChild.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < userWarnListModels.size(); i++) {
                            List<UserWarnListModel.UserMonitorDetailListBean> userMonitorDetailListBeans =
                                    userWarnListModels.get(i).getUserMonitorDetailList();
                            mItems = new ArrayList<>();
                            for (int j = 0; j < userMonitorDetailListBeans.size(); j++) {
                                mItems.add(new WarnItem(userMonitorDetailListBeans.get(j).getDisplayItem(),
                                        userMonitorDetailListBeans.get(j).getUserDataMonitorDTO()));
                            }
                            mGroupDatas.add(new WarnGroup(userWarnListModels.get(i).getMonitorName(), mItems));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mIsUpdate){
                                    if (mMyBaseExpandableListAdapter != null) {
                                        mMyBaseExpandableListAdapter.notifyDataSetChanged();
                                        mIsUpdate = false;
                                    }
                                } else {
                                    upDateUI(mGroupDatas);
                                }

                            }
                        });

                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        GjUtil.showEmptyHint(context,Constant.BgColor.BLUE,error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                refreshLayout.autoRefresh();
                            }
                        },elvWareListChild);
                    }
                });


    }


    private void upDateUI(final ArrayList<WarnGroup> groupDatas) {
        mMyBaseExpandableListAdapter = new MyBaseExpandableListAdapter(this, groupDatas);
        mMyBaseExpandableListAdapter.setDeleteItem(new MyBaseExpandableListAdapter.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(View view, int groupPostion, int childPostion) {
                deleteMonitor(groupPostion, childPostion, groupDatas);

            }
        });

        if (elvWareListChild == null) {
            return;
        }

        elvWareListChild.setGroupIndicator(null);
        elvWareListChild.setAdapter(mMyBaseExpandableListAdapter);
        //遍历所有group,将所有项设置成默认展开

        for (int i = 0; i < groupDatas.size(); i++) {
            elvWareListChild.expandGroup(i);
        }
        //设置父布局没有点击事件
        elvWareListChild.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;//这里return true 是父布局没有点击事件
            }
        });
        elvWareListChild.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                WarningEditActivity.launch(WarningUserActivity.this,
                        groupDatas.get(groupPosition).getWarnItems().get(childPosition).getUserDataMonitorDTOBean());
                return true;
            }
        });
    }

    /**
     *  删除数据
     * @param groupPostion
     * @param childPostion
     * @param groupDatas
     */
    private void deleteMonitor(final int groupPostion, final int childPostion, final ArrayList<WarnGroup> groupDatas) {
        List<Integer> ids = new ArrayList<>();
        ids.add(groupDatas.get(groupPostion).getWarnItems().get(childPostion).getUserDataMonitorDTOBean().getId());
        String str =  GsonUtil.toJson(ids);
        RequestBody body = RequestBody.create(GsonUtil.toJson(ids),MediaType.parse("application/json; charset=utf-8")
                );
        Api.getMyService().deleteMonitor(body)
                .compose(XApi.<BaseModel<Integer>>getApiTransformer())
                .compose(XApi.<BaseModel<Integer>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Integer>>() {
                    @Override
                    public void onNext(BaseModel<Integer> listBaseModel) {
                        if (ValueUtil.isEmpty(listBaseModel)) {
                            return;
                        }
                        ToastUtil.showToast(getResources().getString(R.string.txt_delete_success));
                        groupDatas.get(groupPostion).getWarnItems().remove(childPostion);
                        if (groupDatas.get(groupPostion).getWarnItems().size() == 0) {
                            groupDatas.remove(groupPostion);
                        }
                        if (ValueUtil.isListEmpty(groupDatas)) {
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.BLUE, R.string.not_warning);
                        }

                        mMyBaseExpandableListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                    }
                });

    }


}











