package com.gjmetal.app.ui.market;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.OtcOptionsLeftAdapter;
import com.gjmetal.app.adapter.market.OtcOptionsRightAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.OtcOptionMenu;
import com.gjmetal.app.model.market.OtcOptions;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.SwapScrollView;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.app.widget.dialog.SmartChooseDateDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import dev.xesam.android.toolbox.timer.CountTimer;
import io.reactivex.functions.Consumer;

/**
 * Description：场外期权
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-30 9:23
 */


public class MarketOtcOptionsFragment extends BaseFragment {
    @BindView(R.id.tvGoods)
    TextView tvGoods;
    @BindView(R.id.leftRv)
    RecyclerView leftRv;
    @BindView(R.id.llLeft)
    LinearLayout llLeft;
    @BindView(R.id.leftCardView)
    CardView leftCardView;
    @BindView(R.id.rightRv)
    RecyclerView rightRv;
    @BindView(R.id.svRight)
    SwapScrollView svRight;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.tvOtcAvg)
    TextView tvOtcAvg;
    @BindView(R.id.vTopLine)
    View vTopLine;
    @BindView(R.id.tvOtcDate)
    AutofitTextView tvOtcDate;
    @BindView(R.id.llTabMenu)
    LinearLayout llTabMenu;
    @BindView(R.id.ivToright)
    ImageView ivToright;
    @BindView(R.id.ivToLeft)
    ImageView ivToLeft;
    @BindView(R.id.tvSellPercent)
    TextView tvSellPercent;
    private int mDx = 0;
    private int mDy = 0;
    private SmartChooseDateDialog smartChooseDateDialog;
    private List<RoomItem> otcOptionsList = new ArrayList<>();
    private CountTimer countTimer;
    private int menuId;
    private int index = 0;
    private int parentIndex;//父的容器位置
    private OtcOptionsLeftAdapter otcOptionsLeftAdapter;
    private OtcOptionsRightAdapter otcOptionsRightAdapter;
    private boolean hasLoadMenu = false;
    private List<String> optionNames = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private List<OtcOptionMenu> otcOptionList = new ArrayList<>();
    private String optionType;
    private String defaultDate = "- -";//默认选中日期
    private int viewPosition;
    private int screenWidth = 0;
    private int rightItemWidth = 0;

    @Override
    protected int setRootView() {
        return R.layout.fragment_market_otcoptions;
    }

    @SuppressLint("ValidFragment")
    public MarketOtcOptionsFragment(int parentIndex, int index, Future future) {
        this.parentIndex = parentIndex;
        this.index = index;
        menuId = future.getId();
    }

    public MarketOtcOptionsFragment() {
    }



    @SuppressLint("RestrictedApi")
    public void initView() {
        BusProvider.getBus().register(this);
        screenWidth = AppUtil.getScreenWidth(getActivity());
        rightItemWidth = screenWidth / 3;
        initRecycler();
        onRefresh();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (!isAdded()) {
            return;
        }
        if (baseEvent.isCloseMarketTimer()) {
            if (countTimer != null) {
                countTimer.cancel();
            }
        } else if (baseEvent.isLogin() || baseEvent.isStartMarketTimer()) {
            int marketIndex = SharedUtil.getInt(Constant.MARKET_PAGE_INDEX_1);
            int mainPageSelected = SharedUtil.getInt(Constant.MAIN_PAGE_SELECTED);
            if (mainPageSelected == Constant.POSITION_0 && marketIndex == parentIndex && leftRv != null) {
                startTimer();
            }
        }
    }
    @OnClick({R.id.tvOtcAvg, R.id.tvOtcDate})
    public void event(View view) {
        switch (view.getId()) {
            case R.id.tvOtcAvg:
                if (ValueUtil.isListEmpty(optionNames)) {
                    return;
                }
                GjUtil.setRightDrawable(getContext(), tvOtcAvg, R.mipmap.ic_chart_up);
                GjUtil.showSingleDialog(getContext(), optionNames, tvOtcAvg.getText().toString(), new SingleChooseDialog.OnDialogClickListener() {
                    @Override
                    public void dialogClick(Dialog dialog, View v, String value, int position) {
                        if (ValueUtil.isStrNotEmpty(value)) {
                            tvOtcAvg.setText(value);
                            optionType = otcOptionList.get(position).getOptionCode();
                            dates = otcOptionList.get(position).getDates();
                            if (ValueUtil.isListNotEmpty(dates)) {
                                tvOtcDate.setText(dates.get(0));
                            }
                            GjUtil.setRightDrawable(getContext(), tvOtcAvg, R.mipmap.ic_chart_down);
                            onRefresh();
                        }
                    }

                    @Override
                    public void onDismiss() {
                        GjUtil.setRightDrawable(getContext(), tvOtcAvg, R.mipmap.ic_chart_down);
                    }
                });
                break;
            case R.id.tvOtcDate:
                if (ValueUtil.isListEmpty(dates)) {
                    return;
                }
                GjUtil.setRightDrawable(getContext(), tvOtcDate, R.mipmap.ic_chart_up);
                smartChooseDateDialog = new SmartChooseDateDialog(getActivity(), dates.get(0), dates.get(dates.size() - 1), tvOtcDate.getText().toString(), new SmartChooseDateDialog.OnMyDialogListener() {
                    @Override
                    public void onback(String year, String month, String day) {
                        String value = year + "/" + month + "/" + day;
                        if (ValueUtil.isListEmpty(dates)) {
                            return;
                        }
                        boolean has = false;
                        for (String s : dates) {
                            if (ValueUtil.isStrNotEmpty(s) && s.contains("-") || s.contains("/")) {
                                s = s.replace("-", "/");
                                if (s.equals(value)) {
                                    has = true;
                                    break;
                                }
                            }
                        }
                        if (has) {
                            if (ValueUtil.isStrNotEmpty(value) && ValueUtil.isListNotEmpty(dates)) {
                                tvOtcDate.setText(value);
                                GjUtil.setRightDrawable(getContext(), tvOtcDate, R.mipmap.ic_chart_down);
                                if (otcOptionsLeftAdapter != null && otcOptionsRightAdapter != null) {
                                    otcOptionsList = new ArrayList<>();
                                    otcOptionsLeftAdapter.getDataSource().clear();
                                    otcOptionsRightAdapter.getDataSource().clear();
                                }
                                onRefresh();
                            }
                            smartChooseDateDialog.dismiss();
                        } else {
                            ToastUtil.showToast("该日期不可选");
                        }
                    }
                });
                smartChooseDateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        GjUtil.setRightDrawable(getContext(), tvOtcDate, R.mipmap.ic_chart_down);
                    }
                });
                smartChooseDateDialog.show();
                break;
        }
    }

    private void initRecycler() {
        otcOptionsLeftAdapter = new OtcOptionsLeftAdapter(getContext(), dates, optionType, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                jumpActivity(obj);
            }
        });
        final LinearLayoutManager leftllm = new LinearLayoutManager(getContext());
        leftllm.setOrientation(LinearLayoutManager.VERTICAL);
        leftRv.setLayoutManager(leftllm);
        leftRv.setAdapter(otcOptionsLeftAdapter);
        leftRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    rightRv.scrollBy(dx, dy);
                }
            }
        });
        otcOptionsRightAdapter = new OtcOptionsRightAdapter(getContext(), screenWidth, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                jumpActivity(obj);
            }
        });
        LinearLayoutManager rightllm = new LinearLayoutManager(getContext());
        rightllm.setOrientation(LinearLayoutManager.VERTICAL);
        rightRv.setLayoutManager(rightllm);
        rightRv.setAdapter(otcOptionsRightAdapter);
        rightRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    leftRv.scrollBy(dx, dy);
                }
            }
        });

        svRight.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    svRight.startScrollerTask();
                }
                return false;
            }
        });


        svRight.setHandler(new Handler());
        svRight.setScrollViewListener(new SwapScrollView.ScrollViewListener() {
            @Override
            public void onScrollStoped() {
            }

            @Override
            public void onScrollToLeftEdge() {
                viewPosition = 1;
                switchViewPosition(viewPosition);
            }

            @Override
            public void onScrollToRightEdge() {
                //滑动到最右边时消失
                viewPosition = -1;
                switchViewPosition(viewPosition);
            }

            @Override
            public void onScrollToMiddle() {
                viewPosition = 0;
                switchViewPosition(viewPosition);
            }

            @Override
            public void onScrollChanged(SwapScrollView scrollView, int x, int y, int oldx, int oldy) {
                mDx = x;
                mDy = y;
            }

            @Override
            public void onScrollChanged(SwapScrollView.ScrollType scrollType) {
                if (scrollType == SwapScrollView.ScrollType.IDLE) {
                    XLog.d("滑动距离", "屏宽：" + screenWidth + "/" + rightItemWidth + "/" + mDx);
                    if (mDx > rightItemWidth) {
                        svRight.scrollTo(screenWidth / 2, mDy);
                    } else {
                        svRight.scrollTo(5, mDy);
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("market_" + menuId + "_time");

    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageEnd("market_" + menuId + "_time");
    }

    private void jumpActivity(Object obj) {
        GjUtil.closeMarketTimer();
        if (ValueUtil.isEmpty(obj)) {
            return;
        }
        RoomItem bean = (RoomItem) obj;
        bean.setOptionCode(optionType);
        bean.setDateList(dates);
        bean.setId(menuId);
        bean.setDefaultDate(defaultDate);
        bean.setSelectedDate(tvOtcDate.getText().toString());
        OtoOptionsDetailActivity.launch(getActivity(), bean);
    }

    private void switchViewPosition(int viewPosition) {
        if (ivToright == null || ivToLeft == null) {
            return;
        }
        switch (viewPosition) {
            case 1:
                ivToright.setVisibility(View.VISIBLE);
                ivToLeft.setVisibility(View.GONE);
                break;
            case 0:
                ivToright.setVisibility(View.VISIBLE);
                ivToLeft.setVisibility(View.GONE);
                break;
            case -1:
                ivToright.setVisibility(View.GONE);
                ivToLeft.setVisibility(View.VISIBLE);
                break;
        }

    }

    /**
     * 启动定时器
     *
     * @param
     */
    public void startTimer() {
        onRefresh();
        if (countTimer != null) {
            countTimer.cancel();
        }
        countTimer = new CountTimer(2000) {
            @Override
            public void onStart(long millisFly) {
            }

            @Override
            public void onCancel(long millisFly) {
            }

            @Override
            public void onPause(long millisFly) {
            }

            @Override
            public void onResume(long millisFly) {

            }

            @Override
            public void onTick(long millisFly) {
                onRefresh();
            }
        };
        countTimer.start();
    }


    /**
     * 显示刷新列表
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            int mainPageSelected = SharedUtil.getInt(Constant.MAIN_PAGE_SELECTED);
            if (mainPageSelected==Constant.POSITION_0 && leftRv != null) {
                startTimer();
            }
        } else {
            GjUtil.closeMarketTimer();
        }
    }

    private void onRefresh() {
        if (!isAdded()) {
            return;
        }
        getOtcOptionsMenu();
    }

    /**
     * 获取期权菜单
     */
    private void getOtcOptionsMenu() {
        if (hasLoadMenu) {
            getOtcOptionsList();
        } else {
            Api.getMarketService().getOptionName(menuId)
                    .compose(XApi.<BaseModel<List<OtcOptionMenu>>>getApiTransformer())
                    .compose(XApi.<BaseModel<List<OtcOptionMenu>>>getScheduler())
                    .subscribe(new ApiSubscriber<BaseModel<List<OtcOptionMenu>>>() {
                        @Override
                        public void onNext(BaseModel<List<OtcOptionMenu>> listBaseModel) {
                            if (tvOtcAvg == null) {
                                return;
                            }
                            hasLoadMenu = true;
                            optionNames = new ArrayList<>();
                            dates = new ArrayList<>();
                            otcOptionList = new ArrayList<>();
                            if (ValueUtil.isNotEmpty(listBaseModel.getData())) {
                                if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                                    otcOptionList.addAll(listBaseModel.getData());
                                    for (OtcOptionMenu bean : otcOptionList) {
                                        optionNames.add(bean.getName());
                                    }
                                    if (otcOptionList.size() > 1) {
                                        GjUtil.setRightDrawable(getContext(), tvOtcAvg, R.mipmap.ic_chart_down);
                                        tvOtcAvg.setClickable(true);
                                    } else {
                                        tvOtcAvg.setClickable(false);
                                    }
                                    tvOtcAvg.setText(otcOptionList.get(0).getName());
                                    optionType = otcOptionList.get(0).getOptionCode();
                                } else {
                                    tvOtcAvg.setText("- -");
                                }
                                if (ValueUtil.isListNotEmpty(listBaseModel.getData().get(0).getDates())) {
                                    defaultDate = listBaseModel.getData().get(0).getDefaultDate();
                                    tvOtcDate.setText(defaultDate);
                                    dates.addAll(listBaseModel.getData().get(0).getDates());
                                    if (listBaseModel.getData().get(0).getDates().size() > 1) {
                                        GjUtil.setRightDrawable(getContext(), tvOtcDate, R.mipmap.ic_chart_down);
                                        tvOtcDate.setClickable(true);
                                    } else {
                                        tvOtcDate.setClickable(false);
                                    }
                                } else {
                                    tvOtcDate.setText("- -");
                                }
                            } else {
                                tvOtcDate.setClickable(false);
                                tvOtcAvg.setClickable(false);
                                tvOtcAvg.setText("- -");
                                tvOtcDate.setText("- -");
                            }
                            getOtcOptionsList();
                        }

                        @Override
                        protected void onFail(NetError error) {
                            hasLoadMenu = false;
                            showAgainLoad(error);
                        }
                    });
        }
    }

    /**
     * 获取期权数据
     */
    private void getOtcOptionsList() {
        Api.getMarketService().queryQuotingAtm(menuId, optionType, tvOtcDate.getText().toString())
                .compose(XApi.<BaseModel<OtcOptions>>getApiTransformer())
                .compose(XApi.<BaseModel<OtcOptions>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<OtcOptions>>() {
                    @Override
                    public void onNext(BaseModel<OtcOptions> listBaseModel) {
                        try {
                            if (llTop == null) {
                                return;
                            }
                            if (ValueUtil.isEmpty(listBaseModel.getData())) {
                                if (llTop != null) {
                                    llTop.setVisibility(View.GONE);
                                }
                                if (ivToright != null) {
                                    ivToright.setVisibility(View.GONE);
                                }
                                if (ivToLeft != null) {
                                    ivToLeft.setVisibility(View.GONE);
                                }
                                if (vEmpty != null) {
                                    vEmpty.setVisibility(View.VISIBLE);
                                    vEmpty.setNoData(Constant.BgColor.BLUE);
                                }
                            } else {
                                if (ValueUtil.isListEmpty(listBaseModel.getData().getResult())) {
                                    if (llTop != null) {
                                        llTop.setVisibility(View.GONE);
                                    }
                                    if (ivToright != null) {
                                        ivToright.setVisibility(View.GONE);
                                    }
                                    if (ivToLeft != null) {
                                        ivToLeft.setVisibility(View.GONE);
                                    }
                                    if (vEmpty != null) {
                                        vEmpty.setVisibility(View.VISIBLE);
                                        vEmpty.setNoData(Constant.BgColor.BLUE);
                                    }
                                } else {
                                    try {
                                        if (llTop != null) {
                                            llTop.setVisibility(View.VISIBLE);
                                        }
                                        if (vEmpty != null) {
                                            vEmpty.setVisibility(View.GONE);
                                        }
                                        if (ivToright != null) {
                                            ivToright.setVisibility(View.VISIBLE);
                                        }
                                        if (ivToLeft != null) {
                                            ivToLeft.setVisibility(View.VISIBLE);
                                        }
                                        otcOptionsList = listBaseModel.getData().getResult();
                                        upDateUI();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showAgainLoad(error);
                    }
                });
    }

    private void showAgainLoad(NetError error) {
        if (countTimer != null) {
            countTimer.cancel();
        }
        if (vEmpty == null || ValueUtil.isListNotEmpty(otcOptionsList)) {
            return;
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                hasLoadMenu = false;
                onRefresh();
            }
        }, llTop, ivToright, ivToLeft);
    }

    private void upDateUI() {
        if (ValueUtil.isListNotEmpty(otcOptionsList)) {
            for (RoomItem firstBean : otcOptionsLeftAdapter.getDataSource()) {
                for (RoomItem bean : otcOptionsList) {
                    if (bean.getContractId() != null) {
                        if (firstBean.getContractId().equals(bean.getContractId())) {
                            if (ValueUtil.isStrNotEmpty(firstBean.getStrike()) && firstBean.getStrike().equals(bean.getStrike())) {//标的价格
                                bean.setStrikeState(null);//平仓
                            } else {
                                bean.setStrikeState(1);
                            }
                            if (ValueUtil.isStrNotEmpty(firstBean.getBuy()) && firstBean.getBuy().equals(bean.getBuy())) {//买
                                bean.setBuyState(null);//平仓
                            } else {
                                bean.setBuyState(1);
                            }
                            if (ValueUtil.isStrNotEmpty(firstBean.getSell()) && firstBean.getSell().equals(bean.getSell())) {//卖
                                bean.setSellState(2);//平仓
                            } else {
                                bean.setSellState(1);
                            }
                            if (ValueUtil.isStrNotEmpty(firstBean.getBuyPer()) && firstBean.getBuyPer().equals(bean.getBuyPer())) {//买价%
                                bean.setBuyPerState(null);//平仓
                            } else {
                                bean.setBuyPerState(1);
                            }
                            if (ValueUtil.isStrNotEmpty(firstBean.getSellPer()) && firstBean.getSellPer().equals(bean.getSellPer())) {//卖价%
                                bean.setSellPerState(null);//平仓
                            } else {
                                bean.setSellPerState(1);
                            }
                        }
                    } else {
                        bean.setStrikeState(null);//平仓
                        bean.setBuyState(null);
                        bean.setSellState(null);
                        bean.setBuyPerState(null);
                        bean.setSellPerState(null);
                    }
                }
            }
            otcOptionsLeftAdapter.setData(otcOptionsList);
            otcOptionsRightAdapter.setData(otcOptionsList);
            switchViewPosition(viewPosition);
            //最新数据波动背景变色
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isListNotEmpty(otcOptionsList)) {
                        otcOptionsLeftAdapter.notifyDataSetChanged();
                        otcOptionsRightAdapter.notifyDataSetChanged();
                        switchViewPosition(viewPosition);
                        for (RoomItem bean : otcOptionsList) {
                            bean.setStrikeState(null);
                            bean.setBuyState(null);
                            bean.setSellState(null);
                            bean.setBuyPerState(null);
                            bean.setSellPerState(null);
                        }
                    }
                }
            }, 500);
        }
    }


    @Override
    public void onDestroyView() {
        if (countTimer != null) {
            countTimer.cancel();
        }
        BusProvider.getBus().unregister(this);
        super.onDestroyView();
    }


}
