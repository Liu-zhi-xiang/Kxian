package com.gjmetal.app.ui.alphametal.measure;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.HelperAdapterTwo;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.alphametal.MeasureSocketBean;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.event.BusProvider;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Description: 进口、出口测算子界面2
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/22  20:42
 */
public class MeasureChildFragment extends DelayerFragment {
    private static final int ADDROOM = 1;
    @BindView(R.id.rvFutureChild)
    RecyclerView rvFutureChild;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.viewTab)
    View viewTab;
    @BindView(R.id.tvFutureName)
    AutofitTextView tvFutureName;
    @BindView(R.id.tvFutureBestNew)
    AutofitTextView tvFutureBestNew;
    @BindView(R.id.tvFutureVolume)
    AutofitTextView tvFutureVolume;
    @BindView(R.id.tvFutureUpOrDown)
    AutofitTextView tvFutureUpOrDown;
    private HelperAdapterTwo helperAdapter;
    private List<RoomItem> roomItemArrayList;
    private Map<String, RoomItem> codemap = new HashMap<>();
    private String roomCode;
    private Future.SubItem subItemList;
    private Future future;
    private String type;
    private Handler myHandler;

    @Override
    protected int setRootView() {
        return R.layout.fragment_measure_child;
    }

    @SuppressLint("ValidFragment")
    public MeasureChildFragment(int index, Future.SubItem subItemList) {
        this.subItemList = subItemList;
        indexs = index;
        name = subItemList.getName();
    }

    @SuppressLint("ValidFragment")
    public MeasureChildFragment(int index, Future future) {
        this.future = future;
        indexs = index;
    }

    public MeasureChildFragment() {

    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        tvFutureName.setText("合约月份");
        tvFutureBestNew.setText("对外盘比值");
        tvFutureVolume.setText("进口盈亏");
        tvFutureUpOrDown.setText("盈亏涨跌");
        viewTab.setVisibility(View.VISIBLE);
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                // 要做的事情
                super.handleMessage(msg);
                switch (msg.what) {
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
        if (!isAdded() || !getUserVisibleHint()) {
            return;
        }
        if (socketEvent.isConnectSuccess()) {//重连
            SocketManager.getInstance().addRoom(roomCode);
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        helperAdapter = new HelperAdapterTwo(getActivity());
        rvFutureChild.setLayoutManager(mLayoutManager);
        rvFutureChild.setAdapter(helperAdapter);
        ((SimpleItemAnimator) rvFutureChild.getItemAnimator()).setSupportsChangeAnimations(false);
        helperAdapter.setOnItemClickListener(new HelperAdapterTwo.OnItemClickListener() {
            @Override
            public void onClick(View view, RoomItem monthModel) {
                MeasureChartActivity.launch(getActivity(), monthModel, type);
                AppAnalytics.getInstance().AlphametalOnEvent(getContext(), monthModel.getContract(), null, AppAnalytics.AlphametalChartEvent.CONFIG_ACCESS);

            }

            @Override
            public void onLongClick(View view, RoomItem monthModel) {

            }
        });
        setDatas();
    }

    private void setDatas() {
        if (helperAdapter == null || rvFutureChild == null) {
            return;
        }
        roomItemArrayList = new ArrayList<>();
        if (future != null) {
            if (future.getRoomItem() != null && future.getRoomItem().size() > 0) {
                roomItemArrayList.addAll(future.getRoomItem());
                type = future.getType();
                roomCode = future.getRoomCode();
            }
        } else {
            if (subItemList != null) {
                roomItemArrayList.addAll(subItemList.getRoomItem());
            }
            type = subItemList.getType();
            roomCode = subItemList.getRoomCode();
        }
        helperAdapter.setData(roomItemArrayList);
        helperAdapter.notifyDataSetChanged();
        if (roomItemArrayList.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int x = 0; x < roomItemArrayList.size(); x++) {
                        String[] Profit = roomItemArrayList.get(x).getContract().split(",");
                        if (codemap != null) {
                            if (Profit.length >= 1) {
                                codemap.put(Profit[0], roomItemArrayList.get(x));
                                roomItemArrayList.get(x).setProfitCode(Profit[0]);
                                if (Profit.length >= 2) {
                                    codemap.put(Profit[1], roomItemArrayList.get(x));
                                    roomItemArrayList.get(x).setParityCode(Profit[1]);
                                }
                            }
                        }
                    }
                }
            }).start();
        } else {
            showEmotyDatas();
        }
    }

    private void showEmotyDatas() {
        if (vEmpty == null || rvFutureChild == null) {
            return;
        }
        rvFutureChild.setVisibility(View.GONE);
        vEmpty.setVisibility(View.VISIBLE);
        vEmpty.setNoData(Constant.BgColor.BLUE, R.string.no_data);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisibleToUser) {
        super.onFragmentVisibleChange(isVisibleToUser);
        if (isVisibleToUser) {
            if (ValueUtil.isListEmpty(roomItemArrayList)) {
                showEmotyDatas();
            } else {
                if (vEmpty != null && rvFutureChild != null) {
                    if (vEmpty.getVisibility() == View.VISIBLE) {
                        vEmpty.setVisibility(View.GONE);
                    }
                    if (rvFutureChild.getVisibility() == View.GONE) {
                        rvFutureChild.setVisibility(View.VISIBLE);
                    }
                    myHandler.sendEmptyMessageDelayed(ADDROOM, 800);
                }
            }
        } else {
            myHandler.removeMessages(ADDROOM);
        }
    }


    private void setData(Object[] args) {
        if (codemap == null || helperAdapter == null || roomItemArrayList == null) {
            return;
        }
        try {
            MeasureSocketBean socketBean = new Gson().fromJson(args[0].toString(), MeasureSocketBean.class);
            String room = socketBean.getRoom();
            if (TextUtils.isEmpty(room)||TextUtils.isEmpty(roomCode) || !room.equals(roomCode.toLowerCase())) {
                return;
            }
            String type = socketBean.getData().getContract();
            final RoomItem roomItem = codemap.get(type);
            if (roomItem != null) {
                if (type.contains("Profit")) {
                    if (!roomItem.getProfit().equals(socketBean.getData().getLast())||!socketBean.getData().getUpdown().equals(roomItem.getProfitUpdown())) {
                        if (!roomItem.getProfit().equals(socketBean.getData().getLast())) {
                            roomItem.setProfitState(1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    roomItem.setProfitState(2);
                                    helperAdapter.notifyItemChanged(roomItemArrayList.indexOf(roomItem));
                                }
                            }, 500);
                        }
                        roomItem.setProfit(socketBean.getData().getLast());
                        roomItem.setProfitUpdown(socketBean.getData().getUpdown());
                        helperAdapter.notifyItemChanged(roomItemArrayList.indexOf(roomItem));
                    }
                } else if (type.contains("Parity")) {
                    if (this.type.equals(Constant.MenuType.THREE_FOUR.getValue())) {
                        for (int x = 0; x < helperAdapter.getDataSource().size(); x++) {
                            if (helperAdapter.getDataSource().get(x).getParityCode().equals(type)) {
                                if (!socketBean.getData().getLast().equals(helperAdapter.getDataSource().get(x).getParity())) {
                                    helperAdapter.getDataSource().get(x).setParity(socketBean.getData().getLast());
                                }
                            }
                        }
                        helperAdapter.notifyDataSetChanged();
                    } else {
                        if (!socketBean.getData().getLast().equals(roomItem.getParity())) {
                            roomItem.setParity(socketBean.getData().getLast());
                            helperAdapter.notifyItemChanged(roomItemArrayList.indexOf(roomItem));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        BusProvider.getBus().unregister(this);
        super.onDestroyView();
    }

}
