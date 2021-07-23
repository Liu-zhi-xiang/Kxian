package com.gjmetal.app.ui.alphametal.lme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.SwapDetailLeftAdapter;
import com.gjmetal.app.adapter.alphametal.SwapDetailRightAdapter;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.alphametal.LMEDetailVoListModel;
import com.gjmetal.app.model.alphametal.SwapDetailRightItems;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.MyDrawLogo;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.SwapScrollView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description 调期费详情
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-12 10:36
 */

public class SwapDetailActivity extends BaseActivity {
    @BindView(R.id.leftRecycler)
    RecyclerView leftRecycler;
    @BindView(R.id.rightRecycler)
    RecyclerView rightRecycler;
    @BindView(R.id.tvLodMore)
    TextView tvLodMore;
    @BindView(R.id.swapEmpty)
    EmptyView swapEmpty;
    @BindView(R.id.rlLogoImage)
    View rlLogoImage;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.llRight)
    SwapScrollView llRight;

    private String mMetalName;
    private String mMetalCode;
    private Context mContext;
    private ArrayList<String> mContractArrayList;
    private ArrayList<SwapDetailRightItems> mItemsArrayList;
    private SwapDetailLeftAdapter mLeftAdapter;
    private SwapDetailRightAdapter mRightAdapter;
    private int mPage = 1; //页数
    private int mPageSize = AppUtil.getPageSize(32); //每页条数
    private int mPages;
    private boolean mHasNext = true;

    public static void launch(Activity context, String metalName, String metalCode) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putString("name", metalName);
            bundle.putString("code", metalCode);
            Router.newIntent(context)
                    .to(SwapDetailActivity.class)
                    .data(bundle)
                    .launch();
        }
    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_swap_detail);
        KnifeKit.bind(this);
        mContext = SwapDetailActivity.this;
        rlLogoImage.setBackground(new MyDrawLogo(mContext, -30));

        mContractArrayList = new ArrayList<>();
        mItemsArrayList = new ArrayList<>();
        mMetalName = getIntent().getExtras().getString("name");
        mMetalCode = getIntent().getExtras().getString("code");
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, mMetalName + "调期费详情");
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                App.finishSingActivity(SwapDetailActivity.this);
            }
        });

        tvLodMore.setVisibility(View.GONE);

        cardView.setCardElevation(0);

        initRecycler();

    }

    //设置RecyclerView数据和布局
    private void initRecycler() {
        mLeftAdapter = new SwapDetailLeftAdapter(mContext);
        final LinearLayoutManager leftllm = new LinearLayoutManager(mContext);
        leftllm.setOrientation(LinearLayoutManager.VERTICAL);
        leftRecycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        leftRecycler.setLayoutManager(leftllm);
        leftRecycler.setAdapter(mLeftAdapter);
        leftRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    rightRecycler.scrollBy(dx, dy);
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    if (mHasNext && mPages >= mPage) {
                        tvLodMore.setVisibility(View.VISIBLE);
                        tvLodMore.setText(mContext.getResources().getString(R.string.start_load_more));
                    }
                }
            }
        });

        mRightAdapter = new SwapDetailRightAdapter(mContext);
        llRight.setHandler(new Handler());
        LinearLayoutManager rightllm = new LinearLayoutManager(mContext);
        rightllm.setOrientation(LinearLayoutManager.VERTICAL);
        rightRecycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        rightRecycler.setLayoutManager(rightllm);
        rightRecycler.setAdapter(mRightAdapter);
        rightRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    leftRecycler.scrollBy(dx, dy);
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    mPageSize = 20;
                    mPage++;
                    if (mHasNext && mPages >= mPage) {
                        tvLodMore.setVisibility(View.VISIBLE);
                        tvLodMore.setText(mContext.getResources().getString(R.string.start_load_more));
                        getPageDatas();
                    }
                }
            }
        });

        llRight.setScrollViewListener(new SwapScrollView.ScrollViewListener() {
            @Override
            public void onScrollStoped() {

            }

            @Override
            public void onScrollToLeftEdge() {

            }

            @Override
            public void onScrollToRightEdge() {

            }

            @Override
            public void onScrollToMiddle() {

            }

            @Override
            public void onScrollChanged(SwapScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (x != 0) {
                    cardView.setContentPadding(0, 0, 5, 0);
                    cardView.setCardElevation(8);
                } else {
                    cardView.setContentPadding(0, 0, 0, 0);
                    cardView.setCardElevation(0);
                }
            }

            @Override
            public void onScrollChanged(SwapScrollView.ScrollType scrollType) {

            }
        });


    }

    @Override
    protected void fillData() {
        getPageDatas();
    }

    private void getPageDatas() {
        Api.getAlphaMetalService().getRtLMEDetailVOList(mMetalCode, mPage, mPageSize)
                .compose(XApi.<BaseModel<LMEDetailVoListModel>>getApiTransformer())
                .compose(XApi.<BaseModel<LMEDetailVoListModel>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<LMEDetailVoListModel>>() {
                    @Override
                    public void onNext(BaseModel<LMEDetailVoListModel> listBaseModel) {
                        if (listBaseModel.code.equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())){
                            ReadPermissionsManager.showSubscibeDialog(context,SwapDetailActivity.this,Constant.ApplyReadFunction.ZH_APP_SPOT_LME_COMEX_STOCK,true,false);
                            return;
                        }
                        mContractArrayList.clear();
                        mItemsArrayList.clear();

                        leftRecycler.setVisibility(View.VISIBLE);
                        rightRecycler.setVisibility(View.VISIBLE);
                        swapEmpty.setVisibility(View.GONE);

                        if (mPage != 1) {
                            tvLodMore.setText(mContext.getResources().getString(R.string.stop_load_more));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvLodMore.setVisibility(View.GONE);
                                }
                            }, 1000);
                        }


                        if (ValueUtil.isEmpty(listBaseModel.getData())) return;

                        mPages = listBaseModel.getData().getPages();
                        mHasNext = listBaseModel.getData().isHasNext();

                        List<LMEDetailVoListModel.DataListBean> dataList = listBaseModel.getData().getDataList();
                        if (ValueUtil.isListEmpty(dataList)) return;


                        for (int i = 0; i < dataList.size(); i++) {
                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getAlias())) {
                                mContractArrayList.add(dataList.get(i).getAlias());
                            } else {
                                mContractArrayList.add("- -");
                            }
                        }

                        for (int i = 0; i < dataList.size(); i++) {
                            SwapDetailRightItems items = new SwapDetailRightItems();
                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getLast())) {
                                items.setNewPrices(dataList.get(i).getLast());
                            } else {
                                items.setNewPrices("- -");
                            }

                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getPriceDiff())) {
                                items.setUpDowms(dataList.get(i).getPriceDiff());
                            } else {
                                items.setUpDowms("- -");
                            }

                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getBid())) {
                                items.setBuy(dataList.get(i).getBid());
                            } else {
                                items.setBuy("- -");
                            }


                            items.setBuyNums(dataList.get(i).getBidSize() + "");


                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getBidTime())) {
                                items.setBuyDates(dataList.get(i).getBidTime());
                            } else {
                                items.setBuyDates("- -");
                            }

                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getAsk())) {
                                items.setSell(dataList.get(i).getAsk());
                            } else {
                                items.setSell("- -");
                            }

                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getAskSize())) {
                                items.setSellNums(dataList.get(i).getAskSize());
                            } else {
                                items.setSellNums("- -");
                            }

                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getAskTime())) {
                                items.setSellDates(dataList.get(i).getAskTime());
                            } else {
                                items.setSellDates("- -");
                            }

                            if (ValueUtil.isStrNotEmpty(dataList.get(i).getPreClose())) {
                                items.setYTDPut(dataList.get(i).getPreClose());
                            } else {
                                items.setYTDPut("- -");
                            }

                            mItemsArrayList.add(items);
                        }

                        if (ValueUtil.isListEmpty(mContractArrayList) || ValueUtil.isListEmpty(mItemsArrayList)) {
                            leftRecycler.setVisibility(View.GONE);
                            rightRecycler.setVisibility(View.GONE);
                            swapEmpty.setVisibility(View.VISIBLE);
                            tvLodMore.setVisibility(View.GONE);
                        } else {
                            if (mPage == 1) {
                                mLeftAdapter.setData(mContractArrayList);
                                mRightAdapter.setData(mItemsArrayList);

                            } else if (mPage > 1) {
                                mLeftAdapter.addData(mContractArrayList);
                                mRightAdapter.addData(mItemsArrayList);

                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, swapEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getPageDatas();
                            }
                        }, tvLodMore, leftRecycler, rightRecycler);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onResume(this);//调期费详情停留时间
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPause(this);//调期费详情停留时间
    }
}











