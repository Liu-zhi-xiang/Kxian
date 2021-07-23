package com.gjmetal.app.ui.information;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.CheckInfomationTabsEvent;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.information.InfoMationCheckTabBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 资讯标签
 * Created by huangb on 2018/4/2.
 */

public class InfomationTabsActivity extends BaseActivity {
    @BindView(R.id.flow_use)
    TagFlowLayout mFlow_use;
    @BindView(R.id.flow_unuse)
    TagFlowLayout mFlow_unuse;
    @BindView(R.id.tvYesChose)
    TextView tvYesChose;
    @BindView(R.id.tvNoChose)
    TextView tvNoChose;
    @BindView(R.id.scrollTop)
    ScrollView scrollTop;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;

    private List<InfoMationCheckTabBean.ChildTagListBean> mUsList = new ArrayList<>();//使用
    private List<InfoMationCheckTabBean.ChildTagListBean> mUnUsList = new ArrayList<>();//未使用
    private TagAdapter mTabUserAdapter;
    private TagAdapter mTabUnUserAdapter;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_information_tabs);
        KnifeKit.bind(this);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getResources().getString(R.string.biaoqian));
        getUncheckData();
        scrollTop.setVisibility(View.GONE);
        tvCommit.setVisibility(View.GONE);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin() && User.getInstance().isLoginIng()) {
            getUncheckData();
        }
    }
    @Override
    protected void fillData() {
    }

    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            Router.newIntent(context)
                    .to(InfomationTabsActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    //获取全部
    private void getUncheckData() {
        DialogUtil.waitDialog(this);
        Api.getInformationService().queryTagAndCate().
                compose(XApi.<BaseModel<List<InfoMationCheckTabBean>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<InfoMationCheckTabBean>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<InfoMationCheckTabBean>>>() {
                    @Override
                    public void onNext(BaseModel<List<InfoMationCheckTabBean>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (scrollTop != null) {
                            scrollTop.setVisibility(View.VISIBLE);
                        }
                        if (tvCommit != null) {
                            tvCommit.setVisibility(View.VISIBLE);
                        }
                        if (vEmpty != null) {
                            vEmpty.setVisibility(View.GONE);
                        }
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            if (scrollTop != null) {
                                scrollTop.setVisibility(View.GONE);
                            }
                            if (tvCommit != null) {
                                tvCommit.setVisibility(View.GONE);
                            }
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.WHITE);
                            return;
                        }
                        if (listBaseModel.getData() != null) {
                            for (int i = 0; i < listBaseModel.getData().size(); i++) {
                                for (int j = 0; j < listBaseModel.getData().get(i).getChildTagList().size(); j++) {
                                    if (listBaseModel.getData().get(i).getChildTagList().get(j).isSub()) {//已选标签
                                        mUsList.add(listBaseModel.getData().get(i).getChildTagList().get(j));
                                    } else {
                                        mUnUsList.add(listBaseModel.getData().get(i).getChildTagList().get(j));
                                    }
                                }
                            }
                            checkTabs();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.showEmptyHint(context, Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getUncheckData();
                            }
                        }, tvCommit, scrollTop);
                        DialogUtil.dismissDialog();
                    }
                });
    }

    //检查标签
    private void checkTabs() {
        if (mFlow_unuse == null) {
            return;
        }
        mFlow_unuse.setAdapter(mTabUnUserAdapter = new TagAdapter<InfoMationCheckTabBean.ChildTagListBean>(mUnUsList) {
            @Override
            public View getView(FlowLayout parent, int position, InfoMationCheckTabBean.ChildTagListBean s) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_tab,
                        mFlow_unuse, false);
                TextView tv = linearLayout.findViewById(R.id.tv_tab);
                try {
                    if (ValueUtil.isNotEmpty(s) && ValueUtil.isStrNotEmpty(s.getTagName())) {
                        tv.setText(s.getTagName());
                    } else {
                        tv.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return linearLayout;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                mUsList.add(mUnUsList.get(position));
                mUnUsList.remove(position);
                mTabUnUserAdapter.notifyDataChanged();
                mTabUserAdapter.notifyDataChanged();
            }
        });
        mFlow_use.setAdapter(mTabUserAdapter = new TagAdapter<InfoMationCheckTabBean.ChildTagListBean>(mUsList) {
            @Override
            public View getView(FlowLayout parent, int position, InfoMationCheckTabBean.ChildTagListBean s) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_tab_use, mFlow_use, false);
                TextView tv = linearLayout.findViewById(R.id.tv_tab);
                tv.setText(s.getTagName());
                return linearLayout;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                mUnUsList.add(mUsList.get(position));
                mUsList.remove(position);
                mTabUnUserAdapter.notifyDataChanged();
                mTabUserAdapter.notifyDataChanged();
            }
        });
    }


    /**
     * 完成
     */
    @OnClick(R.id.tv_commit)
    public void commit() {
        DialogUtil.waitDialog(this);
        List<String> mIDList = new ArrayList<>();
        for (int i = 0; i < mUsList.size(); i++) {
            mIDList.add(mUsList.get(i).getTagId() + "");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("tagIds", mIDList);
        Api.getInformationService().saveTags(map)
                .compose(XApi.<BaseModel<Object>>getApiTransformer())
                .compose(XApi.<BaseModel<Object>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Object>>() {
                    @Override
                    public void onNext(BaseModel<Object> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            BusProvider.getBus().post(new CheckInfomationTabsEvent());
                            finish();
                        } else {
                            ToastUtil.showToast(listBaseModel.getErrorMsg());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(ValueUtil.isStrNotEmpty(error.getMessage()) ? error.getMessage() : "提交失败");
                        DialogUtil.dismissDialog();
                    }
                });
    }
}
