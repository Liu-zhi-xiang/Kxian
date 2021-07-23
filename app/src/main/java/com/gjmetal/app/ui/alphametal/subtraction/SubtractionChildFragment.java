package com.gjmetal.app.ui.alphametal.subtraction;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MonthAdapterTwo;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.alphametal.MeasureSocketBean;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.MenuCheckState;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.alphametal.industry.IndustryFragment;
import com.gjmetal.app.ui.alphametal.subtraction.add.SubtractionAddActivity;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.market.chart.ExchangeChartActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.popuWindow.MarketPopWindow;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:套利测算子界面
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/23  16:19
 */

public class SubtractionChildFragment extends DelayerFragment {
    private static final int ADDROOM = 1;
    @BindView(R.id.tvMonthName)
    TextView tvMonthName;
    @BindView(R.id.tvMonthBestNew)
    TextView tvMonthBestNew;
    @BindView(R.id.tvMonthUpOrDown)
    TextView tvMonthUpOrDown;
    @BindView(R.id.rlMonthListView)
    RecyclerView rlMonthListView;
    @BindView(R.id.llTilie)
    LinearLayout llTilie;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    private MarketPopWindow mMarketPopWindow;
    private String roomCode;
    private int mIndex;
    private int checkUpOrDown = 0;//默认涨跌
    private MonthAdapterTwo mMonthAdapter;
    private Future.SubItem nodeListBeans;
    private List<RoomItem> futureItemArrayList = new ArrayList<>();
    private Map<String, RoomItem> codemap = new HashMap<>();
    private  Handler myHandler ;
    public SubtractionChildFragment() {

    }

    @SuppressLint("ValidFragment")
    public SubtractionChildFragment(int index, Future.SubItem nodeListBeans) {
        this.mIndex = index;
        this.nodeListBeans = nodeListBeans;
        indexs = index;
        name = nodeListBeans.getName();
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_month_child;
    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                // 要做的事情
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        SocketManager.getInstance().addRoom(roomCode);
                        break;
                    default:
                        break;
                }
            }
        };
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        if (!isAdded()) {
            return;
        }

        if (socketEvent.isConnectSuccess()) {//重连
            if (getUserVisibleHint()) {
                addRoom();
            }
        }
        if (socketEvent.isPush()) {
            try {
                setData(socketEvent.getJsonArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        GjUtil.setRightDrawable(getActivity(), tvMonthUpOrDown, R.mipmap.icon_market_change);
        mMonthAdapter = new MonthAdapterTwo(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlMonthListView.setLayoutManager(linearLayoutManager);
        rlMonthListView.setAdapter(mMonthAdapter);
        ((SimpleItemAnimator) rlMonthListView.getItemAnimator()).setSupportsChangeAnimations(false);
        mMonthAdapter.setOnItemClickListener(new MonthAdapterTwo.OnItemClickListener() {
            @Override
            public void onClick(View view, RoomItem monthModel) {
                if (monthModel != null ) {
                    AppAnalytics.getInstance().AlphametalOnEvent(getContext(), monthModel.getContract(), null, AppAnalytics.AlphametalChartEvent.CONFIG_ACCESS);
                }
                if (monthModel.getType().equals(Constant.MenuType.THREE_NIFE.getValue())) {
                    ExchangeChartActivity.launch(getActivity(), monthModel);//镍铁和行情利率走势图界面复用
                } else {
                    String type;
                    if (nodeListBeans.getId()==-1){
                        type="defined";
                    }else {
                        type="";
                    }
                    SubtractionChartActivity.launch(getActivity(), monthModel,type,5);
                }
            }

            @Override
            public void onLongClick(View view, final RoomItem specific) {
                if (nodeListBeans.getId() == -1 ) {
                    mMarketPopWindow = new MarketPopWindow(getActivity(), view, false, new MarketPopWindow.OnClickListener() {
                        @Override
                        public void onTop() {//置顶
                            topCrossMonthSubtractionContract(futureItemArrayList.indexOf(specific), specific);
                        }

                        @Override
                        public void onDelete() { //删除
                            removeCrossMonthSubtractionContract(futureItemArrayList.indexOf(specific), specific);
                        }

                        @Override
                        public void onEdit() {//编辑
                            mMarketPopWindow.dismiss();
                        }
                    });
                }
            }
        });
        initDatas();
    }

    private void initDatas() {
        if (rlMonthListView == null  || mMonthAdapter == null||vEmpty == null  || llTilie == null) {
            return;
        }
        futureItemArrayList.clear();
        futureItemArrayList.addAll(nodeListBeans.getRoomItem());
        if (ValueUtil.isListNotEmpty(futureItemArrayList)) {
            rlMonthListView.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
            llTilie.setVisibility(View.VISIBLE);
            mMonthAdapter.setData(futureItemArrayList);
            mMonthAdapter.notifyDataSetChanged();
            for (int x = 0; x < futureItemArrayList.size(); x++) {
                codemap.put(futureItemArrayList.get(x).getContract(), futureItemArrayList.get(x));
            }
        } else {
            if (nodeListBeans.getId() != -1 ) {
                showEmotyDatas();
            }
        }

    }

    /**
     * 取出本地选择菜单的标记状态
     */
    private void checkMenuState() {
        if (mMonthAdapter == null) {
            return;
        }
        MenuCheckState menuCheckState = GjUtil.getAlphaMetalMenuCheck(nodeListBeans.getType());
        if (ValueUtil.isEmpty(menuCheckState)) {
            tvMonthUpOrDown.setText("涨跌");
            checkUpOrDown = 0;
            mMonthAdapter.setClickType(checkUpOrDown);
            GjUtil.saveAlphaMetalMenuCheck(new MenuCheckState(nodeListBeans.getType(), checkUpOrDown));
        } else {
            if (menuCheckState.getCheckUpOrDown() == 1) {
                checkUpOrDown = 1;
                tvMonthUpOrDown.setText("涨幅");
                mMonthAdapter.setClickType(checkUpOrDown);
            } else {
                tvMonthUpOrDown.setText("涨跌");
                checkUpOrDown = 0;
                mMonthAdapter.setClickType(checkUpOrDown);
            }
            mMonthAdapter.setClickType(menuCheckState.getCheckUpOrDown());
        }
    }
    private void toAddMonth() {
        DialogUtil.loadDialog(getContext());
        Api.getMyService().readCheckPowerTwo("/rest/basis/addCrossMetal,/rest/basis/addCrossMonthSubtractionContract")
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<CrossMetalModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<CrossMetalModel>> stringBaseModel) {
                        DialogUtil.dismissDialog();
                        if (stringBaseModel.code.equals(Constant.ResultCode.SUCCESS.getValue()) && stringBaseModel.data.get(0).getPermission()) {
                            //有权限;
                            SubtractionAddActivity.launch(getActivity(), stringBaseModel.data.get(0).getPermission(), stringBaseModel.data.get(1).getPermission());
                        } else {
                            ToastUtil.showToast(stringBaseModel.message);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (error.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())) {
                            ToastUtil.showToast(getResources().getString(R.string.not_vip_cheack));
                        } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                        } else {
                            ToastUtil.showToast(error.getMessage());
                        }
                    }
                });
    }

    @OnClick(R.id.tvMonthUpOrDown)
    public void onViewClicked() {
        if (tvMonthUpOrDown.getText().equals(getString(R.string.updownPercent))) {//涨跌
            tvMonthUpOrDown.setText(getString(R.string.upDown));
            checkUpOrDown = 0;
            mMonthAdapter.setClickType(checkUpOrDown);
        } else {//涨幅
            tvMonthUpOrDown.setText(getString(R.string.updownPercent));
            checkUpOrDown = 1;
            mMonthAdapter.setClickType(checkUpOrDown);
        }
        GjUtil.saveAlphaMetalMenuCheck(new MenuCheckState(nodeListBeans.getType(), checkUpOrDown));
    }


    private boolean isfirst=true;
    /**
     * 显示刷新列表
     */
    @Override
    protected void onFragmentVisibleChange(boolean isVisibleToUser) {
        super.onFragmentVisibleChange(isVisibleToUser);
        if (isVisibleToUser) {
            if (nodeListBeans.getType().equals(Constant.MenuType.THREE_FIVE.getValue())) {
                if (indexs == 0 && isfirst) {
                    isfirst = false;
                    return;
                }
            }
            if (vEmpty == null || llTilie == null || rlMonthListView == null) {
                return;
            }

            if (!ValueUtil.isListEmpty(futureItemArrayList)) {
                if (indexs == 0 && nodeListBeans.getId() == -1) {
                    getDatasList();
                } else {
                    addRoom();
                }
                if (rlMonthListView.getVisibility()==View.GONE) {
                    rlMonthListView.setVisibility(View.VISIBLE);
                    llTilie.setVisibility(View.VISIBLE);
                    vEmpty.setVisibility(View.GONE);
                }
                checkMenuState();
            }else {
                if (indexs == 0 && nodeListBeans.getId() == -1) {
                    getDatasList();
                } else {
                    showEmotyDatas();
                }
            }
        }else {
            myHandler.removeMessages(ADDROOM);
        }
    }

    private void getDatasList() {
        Api.getMarketService().getFutures("trade-arbity")
                .compose(XApi.<BaseModel<List<RoomItem>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<RoomItem>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<RoomItem>>>() {
                    @Override
                    public void onNext(BaseModel<List<RoomItem>> listBaseModel) {
                        if (vEmpty == null || llTilie == null || rlMonthListView == null) {
                            return;
                        }
                        List<RoomItem> specificList = listBaseModel.getData();
                        if (futureItemArrayList != null&&futureItemArrayList.size()>0) {
                            futureItemArrayList.clear();
                        }
                        if (ValueUtil.isListNotEmpty(specificList)) {
                            if (rlMonthListView.getVisibility()==View.GONE) {
                                rlMonthListView.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                llTilie.setVisibility(View.VISIBLE);
                            }
                            futureItemArrayList.addAll(specificList);
                            for (int x = 0; x < futureItemArrayList.size(); x++) {
                                codemap.put(futureItemArrayList.get(x).getContract(), futureItemArrayList.get(x));
                            }
                            mMonthAdapter.setData(futureItemArrayList);
                            mMonthAdapter.notifyDataSetChanged();
                            addRoom();
                        }
                        if (futureItemArrayList == null || futureItemArrayList.size() == 0) {
                            showAddEmotyDatas();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error == null) {
                            return;
                        }
                        if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            showAddEmotyDatas();
                        } else {
                            showAgainLoad(error);
                        }
                    }
                });
    }

    private void addRoom()
    {
        if (nodeListBeans != null) {
            if (nodeListBeans.getId() == -1) {
                if (futureItemArrayList.size() > 1) {
                    roomCode = futureItemArrayList.get(0).getRoomCode();
                }
            } else {
                roomCode = nodeListBeans.getRoomCode();
            }
        }
        if (TextUtils.isEmpty(roomCode)) {
            return;
        }
        myHandler.sendEmptyMessageDelayed(ADDROOM, 800);

    }

    private void setData(final Object... args) {
        if (vEmpty == null || llTilie == null || rlMonthListView == null) {
            return;
        }
        try {
            MeasureSocketBean socketBean = new Gson().fromJson(args[0].toString(), MeasureSocketBean.class);
            String room = socketBean.getRoom();
            if (TextUtils.isEmpty(room) ||TextUtils.isEmpty(roomCode)|| !room.equals(roomCode.toLowerCase())) {
                return;
            }
            String contract = socketBean.getData().getContract();
            final RoomItem roomItem = codemap.get(contract);
            if (roomItem != null&&socketBean.getData()!=null) {
                if ( roomItem.getLast().equals(socketBean.getData().getLast())) {
                    roomItem.setState(false);
                } else {
                    roomItem.setState(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            roomItem.setState(false);
                            mMonthAdapter.notifyItemChanged(futureItemArrayList.indexOf(roomItem));
                        }
                    }, 500);
                }

                if (!socketBean.getData().getLast().equals(roomItem.getLast())
                        || !socketBean.getData().getUpdown().equals(roomItem.getUpdown())
                        || !socketBean.getData().getPercent().equals(roomItem.getPercent())) {
                    roomItem.setLast(socketBean.getData().getLast());
                    roomItem.setUpdown(socketBean.getData().getUpdown());
                    roomItem.setPercent(socketBean.getData().getPercent());
                    mMonthAdapter.notifyItemChanged(futureItemArrayList.indexOf(roomItem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAgainLoad(NetError error) {
        if (vEmpty == null || llTilie == null || rlMonthListView == null) {
            return;
        }
        if (ValueUtil.isListEmpty(futureItemArrayList)) {
            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    if (nodeListBeans.getType().equals(IndustryFragment.MENUCODE)) {
                        if (!AlphaMetalFragment.IndustryMeasure) {
                            AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE);
                        }
                    } else {
                        if (!AlphaMetalFragment.Subtraction) {
                            AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_KQJC_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SUBTRACTION);
                        }
                    }
                }
            }, rlMonthListView, llTilie);
        } else {
            rlMonthListView.setVisibility(View.VISIBLE);
            llTilie.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
        }
    }

    private void showEmotyDatas() {
        if (vEmpty == null || llTilie == null || rlMonthListView == null) {
            return;
        }

        rlMonthListView.setVisibility(View.GONE);
        vEmpty.setVisibility(View.VISIBLE);
        llTilie.setVisibility(View.GONE);
        vEmpty.setNoData(Constant.BgColor.BLUE, R.string.no_data);
    }

    //空数据时添加添加自定义
    private void showAddEmotyDatas() {
        if (vEmpty == null || llTilie == null || rlMonthListView == null) {
            return;
        }
        rlMonthListView.setVisibility(View.GONE);
        vEmpty.setVisibility(View.VISIBLE);
        llTilie.setVisibility(View.GONE);

        vEmpty.showAddHint(Constant.BgColor.BLUE, R.mipmap.ic_future_add_nor, R.string.txt_add_custom_tag,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (User.getInstance().isLoginIng()) {
                            toAddMonth();
                        } else {
                            LoginActivity.launch((Activity) getContext());
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        BusProvider.getBus().unregister(this);
        super.onDestroyView();
    }
    //置顶自定义跨月基差
    private void topCrossMonthSubtractionContract(final int postion, final RoomItem specific) {
        DialogUtil.waitDialog(getActivity());
        Api.getAlphaMetalService().topCrossMonthSubtractionContract(specific.getContract())
                .compose(XApi.<BaseModel<Object>>getApiTransformer())
                .compose(XApi.<BaseModel<Object>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Object>>() {
                    @Override
                    public void onNext(BaseModel<Object> listBaseModel) {
                        DialogUtil.dismissDialog();
                        futureItemArrayList.remove(postion);
                        futureItemArrayList.add(0, specific);
                        mMonthAdapter.updateDatas(futureItemArrayList);
                        mMarketPopWindow.dismiss();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        mMarketPopWindow.dismiss();
                    }
                });
    }

    /**
     * 删除自定义跨月基差
     * @param postion
     * @param specific
     */
    private void removeCrossMonthSubtractionContract(final int postion, final RoomItem specific) {
        DialogUtil.waitDialog(getActivity());
        Api.getAlphaMetalService().removeCrossMonthSubtractionContract(specific.getContract())
                .compose(XApi.<BaseModel<Object>>getApiTransformer())
                .compose(XApi.<BaseModel<Object>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Object>>() {
                    @Override
                    public void onNext(BaseModel<Object> listBaseModel) {
                        DialogUtil.dismissDialog();
                        futureItemArrayList.remove(postion);
                        mMonthAdapter.removeElement(postion);
                        mMarketPopWindow.dismiss();
                        if (ValueUtil.isListEmpty(futureItemArrayList)) {
                            showAddEmotyDatas();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        mMarketPopWindow.dismiss();
                    }
                });
    }


}

