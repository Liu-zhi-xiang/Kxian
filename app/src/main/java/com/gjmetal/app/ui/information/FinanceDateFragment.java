package com.gjmetal.app.ui.information;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.information.DateAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.flash.FinanceDate;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.calendarview.Calendar;
import com.gjmetal.app.widget.calendarview.CalendarLayout;
import com.gjmetal.app.widget.calendarview.CalendarView;
import com.gjmetal.app.widget.calendarview.DateUtil;
import com.gjmetal.app.widget.calendarview.group.GroupRecyclerView;
import com.gjmetal.app.widget.dialog.ChooseDateDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：财经日历
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-11  15:12
 */

public class FinanceDateFragment extends BaseFragment implements CalendarView.OnDateSelectedListener {
    @BindView(R.id.rvFinance)
    RecyclerView rvFinance;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.tvYearMonth)
    TextView tvYearMonth;
    @BindView(R.id.tvSun)
    TextView tvSun;
    @BindView(R.id.tvMon)
    TextView tvMon;
    @BindView(R.id.tvTue)
    TextView tvTue;
    @BindView(R.id.tvWed)
    TextView tvWed;
    @BindView(R.id.tvThu)
    TextView tvThu;
    @BindView(R.id.tvFri)
    TextView tvFri;
    @BindView(R.id.tvSat)
    TextView tvSat;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.recyclerView)
    GroupRecyclerView recyclerView;
    @BindView(R.id.calendarLayout)
    CalendarLayout calendarLayout;
    @BindView(R.id.rlDate)
    RelativeLayout rlDate;
    @BindView(R.id.vXian)
    View vXian;
    @BindView(R.id.tvCurrentToDay)
    TextView tvCurrentToDay;
    @BindView(R.id.ivChooseDate)
    ImageView ivChooseDate;//选择日期

    private String chooseDay;
    private DateAdapter mDateAdapter;
    private int page = 1;
    private int pageSize = AppUtil.getPageSize(58);
    private boolean isRefresh = false;
    private boolean isOnClick = false;
    private int scrllcount = 0;
    private Handler handler = new Handler();

    @Override
    protected int setRootView() {
        return R.layout.fragment_finance_date;
    }

    public FinanceDateFragment() {
    }

    public void initView() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onrefresh();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                isRefresh = false;
                getFinaceList();
            }
        });
        refreshLayout.setEnableLoadMore(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mDateAdapter = new DateAdapter(getActivity());
        rvFinance.setLayoutManager(mLayoutManager);
        rvFinance.setAdapter(mDateAdapter);

        refreshLayout.setRefreshHeader(new MyRefreshHender(getContext(), ContextCompat.getColor(getContext(),R.color.cF5F5F5)));
        refreshLayout.setHeaderHeight(60);

        vEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });
        refreshLayout.autoRefresh();
        initDataView();
    }

    private void initDataView() {
        calendarView.setOnDateSelectedListener(this);
        java.util.Calendar c = java.util.Calendar.getInstance();
        int maxYear = c.get(java.util.Calendar.YEAR) + 1;//最大年份
        calendarView.setRange(Constant.MIN_YEAR, Constant.MIN_MONTH, maxYear, Constant.MAX_MONTH);//设置日期范围
        calendarView.scrollToCurrent();
        calendarView.pageScrollListener(new CalendarView.PageListener() {
            @Override
            public void onPageSelected(int position) {
                isOnClick = false;
                scrllcount++;
                showText();
                if (scrllcount == 26 || scrllcount == 27) {
                    scrllcount = 0;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onrefresh();
                        }
                    }, 110);
                }
            }
        });
    }


    /**
     * 日历事件列表
     */
    private void getFinaceList() {
        if (isRefresh) {
            DialogUtil.waitDialog(getContext());
        }
        Calendar calendar = calendarView.getSelectedCalendar();
        String strMonth = calendar.getMonth() < 10 ? "0" + calendar.getMonth() : calendar.getMonth() + "";
        String strDay = calendar.getDay() < 10 ? "0" + calendar.getDay() : calendar.getDay() + "";
        chooseDay = calendar.getYear() + "-" + strMonth + "-" + strDay;
        if (mDateAdapter != null && page == 1) {
            mDateAdapter.clearData();
        }
        Api.getDateService().getFinanceList(chooseDay, page, pageSize)
                .compose(XApi.<BaseModel<FinanceDate>>getApiTransformer())
                .compose(XApi.<BaseModel<FinanceDate>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<FinanceDate>>() {
                    @Override
                    public void onNext(BaseModel<FinanceDate> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (rvFinance == null || refreshLayout == null) {
                            return;
                        }
                        if (isRefresh) {
                            if (mDateAdapter != null) {
                                mDateAdapter.setData(listBaseModel.getData().getDayEvents());
                            }
                        } else {
                            if (mDateAdapter != null) {
                                mDateAdapter.addData(listBaseModel.getData().getDayEvents());
                            }
                        }
                        if (page * pageSize < listBaseModel.getData().getTotal()) {
                            page++;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                        }
                        if (ValueUtil.isListEmpty(mDateAdapter.getDataSource())) {
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.WHITE, R.string.txt_today_no_event);
                        } else {
                            vEmpty.setVisibility(View.GONE);
                        }
                        if (!isRefresh)
                            refreshLayout.finishLoadMore();
                        else
                            refreshLayout.finishRefresh();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (refreshLayout == null || vEmpty == null || mDateAdapter == null) {
                            return;
                        }
                        showAgainLoad(error);
                    }
                });
    }

    private void showAgainLoad(NetError error) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh(false);
        }
        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    vEmpty.setVisibility(View.GONE);
                    onrefresh();
                }
            }
        });

    }

    //今天
    @OnClick(R.id.tvCurrentToDay)
    public void setTvCurrentToDay() {
        calendarView.scrollToCurrent();
        onrefresh();
    }


    /**
     * 刷新
     */
    private void onrefresh() {
        page = 1;
        isRefresh = true;
        showText();
        getFinaceList();
    }


    //选择日期
    @OnClick(R.id.ivChooseDate)
    public void chooseDate() {
        Calendar calendar = calendarView.getSelectedCalendar();
        ChooseDateDialog chooseDateDialog = new ChooseDateDialog(getActivity(), calendar.getYear(), calendar.getMonth(), calendar.getDay(), new ChooseDateDialog.OnMyDialogListener() {
            @Override
            public void onback(int year, int month, int day) {
                isOnClick = true;
                chooseDay = year + "-" + month + "-" + day;
                calendarView.scrollToCalendar(year, month, day);
                onrefresh();
            }
        });
        chooseDateDialog.show();
    }


    private void showText() {
        Calendar calendar = calendarView.getSelectedCalendar();
        tvYearMonth.setText(calendar.getYear() + "年" + calendar.getMonth() + "月");
        chooseDay = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
        int week = DateUtil.getWeekFormCalendar(calendar);
        switch (week) {
            case 0:
                setTextColor(tvSun, tvMon, tvTue, tvWed, tvThu, tvFri, tvSat);
                break;
            case 1:
                setTextColor(tvMon, tvSun, tvTue, tvWed, tvThu, tvFri, tvSat);
                break;
            case 2:
                setTextColor(tvTue, tvSun, tvMon, tvWed, tvThu, tvFri, tvSat);
                break;
            case 3:
                setTextColor(tvWed, tvSun, tvMon, tvTue, tvThu, tvFri, tvSat);
                break;
            case 4:
                setTextColor(tvThu, tvSun, tvMon, tvTue, tvWed, tvFri, tvSat);
                break;
            case 5:
                setTextColor(tvFri, tvSun, tvMon, tvTue, tvWed, tvThu, tvSat);
                break;
            case 6:
                setTextColor(tvSat, tvSun, tvMon, tvTue, tvWed, tvThu, tvFri);
                break;
        }
    }

    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        showText();
        if (isClick && !isOnClick ) {
            onrefresh();
        }
    }

    private void setTextColor(TextView tvChecked, TextView... textViews) {
        if (tvChecked == null || textViews == null) {
            return;
        }
        tvChecked.setTextColor(ContextCompat.getColor(getContext(),R.color.cFFFFFF));
        tvChecked.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cD4975C));
        for (TextView v : textViews) {
            v.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
        }
        for (TextView v : textViews) {
            v.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cFFFFFF));
        }
    }
}
