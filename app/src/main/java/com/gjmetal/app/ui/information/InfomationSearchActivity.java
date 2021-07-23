package com.gjmetal.app.ui.information;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.information.InformationSearchAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 资讯搜索
 * Created by huangb on 2018/4/3.
 */

public class InfomationSearchActivity extends BaseActivity {
    @BindView(R.id.tvClearHistorySearch)
    TextView tvClearHistorySearch;
    @BindView(R.id.tlSearchHistory)
    TagFlowLayout tlSearchHistory;
    @BindView(R.id.rlSearchTop)
    RelativeLayout rlSearchTop;
    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.relative_main)
    RelativeLayout relativeMain;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private InformationSearchAdapter mSearchAdapter;
    private String searchKey;
    private int currentPage = 1;
    private int pageSize=AppUtil.getPageSize(45);
    private TagAdapter historyAdapter;
    private String cacheReadKey = "informationSearch";
    private boolean isOnfresh;
    @Override
    protected void initView() {
        initTitleSyle(Titlebar.TitleSyle.MARKET_SEARCH, "");
        setContentView(R.layout.activity_information_search);
        KnifeKit.bind(this);
        titleBar.getCancelSearch().setVisibility(View.VISIBLE);
        titleBar.getCancelSearch().setText(getText(R.string.txt_search));
        titleBar.getEtSearch().setHint(R.string.txt_search_to_want);
        //搜索
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        cacheReadKey = SharedUtil.get(Constant.ACCOUNT, Constant.LOGIN_NAME);//用户名作为缓存id
        rvSearch.setAdapter(mSearchAdapter = new InformationSearchAdapter(this, cacheReadKey, searchKey));
        BusProvider.getBus().register(this);
        ViewUtil.showInputMethodManager(titleBar.getEtSearch());

        titleBar.getEtSearch().requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(titleBar.getEtSearch(), InputMethodManager.SHOW_IMPLICIT);

        getHistoryList();
        titleBar.getEtSearch().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rlSearchTop.setVisibility(View.GONE);
                searchKey = s.toString().trim();
                vEmpty.setVisibility(View.GONE);
                if (ValueUtil.isStrEmpty(searchKey)) {
                    getHistoryList();
                } else {
                    mSearchAdapter.clearData();
                    rvSearch.setVisibility(View.GONE);
                }
            }
        });
        titleBar.getEtSearch().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {//搜索
                    searchAction();
                }
                return false;
            }
        });

        titleBar.getCancelSearch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAction();
            }
        });
        titleBar.getEtSearch().setMaxLines(1);
        titleBar.getEtSearch().setSingleLine();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage = 1;
                        isOnfresh=true;
                        getSearchList(false, searchKey, currentPage);
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setRefreshHeader(new MyRefreshHender(this, ContextCompat.getColor(this,R.color.cffffff)));
        refreshLayout.setHeaderHeight(60);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                currentPage = currentPage + 1;
                isOnfresh=false;
                getSearchList(false, searchKey, currentPage);
            }
        });
        refreshLayout.setEnableLoadMore(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CollectEvent(CollectEvent collectEvent) {
        if (ValueUtil.isNotEmpty(collectEvent)) {
            for (int i = 0; i < collectEvent.mList.size(); i++) {
                for (int j = 0; j < mSearchAdapter.getDataSource().size(); j++) {
                    if (collectEvent.mList.get(i).getNewsId() == mSearchAdapter.getDataSource().get(j).getNewsId()) {
                        mSearchAdapter.getDataSource().get(j).setCollect(!mSearchAdapter.getDataSource().get(j).isCollect());
                    }
                }
            }
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void fillData() {
    }

    @OnClick({R.id.tvClearHistorySearch})
    public void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.tvClearHistorySearch:
                SharedUtil.ListDataSave.clean("searchHistory", Constant.HISTORY_LIST);
                rlSearchTop.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 开始搜索
     */
    private void searchAction() {
        if (ValueUtil.isStrEmpty(searchKey)) {
            ToastUtil.showToast("请输入搜索关键字");
            return;
        }
        ViewUtil.hideInputMethodManager(titleBar.getEtSearch());
        currentPage = 1;
        refreshLayout.setEnableLoadMore(true);
        getSearchList(true, searchKey, currentPage);

    }

    /**
     * 获取搜索记录
     */
    public void getHistoryList() {
        final List<String> list = SharedUtil.ListDataSave.getDataList("searchHistory", Constant.HISTORY_LIST,String.class);
        refreshLayout.setVisibility(View.GONE);
        rlSearchTop.setVisibility(View.VISIBLE);
        tlSearchHistory.setAdapter(historyAdapter = new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_tab, tlSearchHistory, false);
                TextView tv = linearLayout.findViewById(R.id.tv_tab);
                tv.setText(ValueUtil.isStrNotEmpty(s) ? s : "");
                return linearLayout;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                rlSearchTop.setVisibility(View.GONE);
                ViewUtil.hideInputMethodManager(baseLayout);
                historyAdapter.notifyDataChanged();
                searchKey = list.get(position);
                GjUtil.cacheSearchHistory(searchKey);
                titleBar.getEtSearch().setText(searchKey);
                titleBar.getEtSearch().setSelection(searchKey.length());
                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayout.autoRefresh();
            }
        });
    }

    /**
     * 获取搜索内容列表
     */
    private void getSearchList(final boolean showDialog, final String key, int currentPage) {
        if (showDialog) {
            DialogUtil.waitDialog(context);
        }
        refreshLayout.setVisibility(View.VISIBLE);
        Api.getInformationService().searchNews(searchKey, currentPage, pageSize)
                .compose(XApi.<BaseModel<List<InformationContentBean.ListBean>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<InformationContentBean.ListBean>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<InformationContentBean.ListBean>>>() {
                    @Override
                    public void onNext(BaseModel<List<InformationContentBean.ListBean>> listBaseModel) {
                        if (showDialog) {
                            DialogUtil.dismissDialog();
                        }
                        updateUI(key, listBaseModel.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (showDialog) {
                            DialogUtil.dismissDialog();
                        }

                        GjUtil.showEmptyHint(context,Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                if (refreshLayout != null) {
                                    refreshLayout.autoRefresh();
                                }
                            }
                        }, rvSearch);
                        if (refreshLayout != null) {
                            if(isOnfresh){
                                refreshLayout.finishRefresh(false);
                            }else {
                                refreshLayout.finishLoadMore(false);
                            }

                        }
                    }
                });
    }

    private void updateUI(String searchKey, List<InformationContentBean.ListBean> data) {

        if (ValueUtil.isListNotEmpty(data) && ValueUtil.isStrNotEmpty(cacheReadKey)) {
            GjUtil.getInformationReadStatus(cacheReadKey, data);//获取缓存阅读状态
        }
        refreshLayout.finishLoadMore();
        if (currentPage == 1) {
            refreshLayout.finishRefresh();
            if (ValueUtil.isListEmpty(data)) {
                rlSearchTop.setVisibility(View.GONE);
                refreshLayout.setEnableLoadMore(false);
                rvSearch.setVisibility(View.GONE);
                vEmpty.setVisibility(View.VISIBLE);
                vEmpty.setNoData(Constant.BgColor.WHITE, R.string.search_no_data, R.mipmap.icon_g_search_nothing_light);
            } else {
                refreshLayout.setEnableLoadMore(data.size() >= pageSize);
                mSearchAdapter.setData(data);
                vEmpty.setVisibility(View.GONE);
                rvSearch.setVisibility(View.VISIBLE);
            }
        } else {
            if (ValueUtil.isListEmpty(data)) {
                refreshLayout.setEnableLoadMore(false);
            } else {
                refreshLayout.setEnableLoadMore(true);
                mSearchAdapter.addData(data);
            }
        }
        if (mSearchAdapter == null) {
            return;
        }
        mSearchAdapter.setSearchKey(searchKey);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }


}
