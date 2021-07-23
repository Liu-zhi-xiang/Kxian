package com.gjmetal.app.ui.market.chart;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.TapeGridViewAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.socket.TapeSocket;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Description:盘口
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/24  15:56
 */

public class TapeSocketActivity extends BaseActivity {
    @BindView(R.id.gvTape)
    GridView gvTape;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.vTop)
    View vTop;
    @BindView(R.id.vBottom)
    View vBottom;
    @BindView(R.id.tvSocketHint)
    TextView tvSocketHint;
    private RoomItem futureItem;
    private TapeGridViewAdapter tapeGridViewAdapter;
    private List<TapeSocket.BlocksBean.ItemsBean> tapeList;
    private Map<String, String> mapValue;
    private String roomCode;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_tape);
    }

    @Override
    protected void fillData() {

        if (!NetUtil.checkNet(context)) {
            SocketManager.socketHint(context, SocketManager.DISNNECT, titleBar.getTvSocketHint());
        }
        futureItem = (RoomItem) getIntent().getSerializableExtra(Constant.MODEL);
        if (ValueUtil.isEmpty(futureItem)) {
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, ValueUtil.isStrNotEmpty(futureItem.getName()) ? futureItem.getName() + "盘口" : "");

        tapeList = new ArrayList<>();
        vTop.setVisibility(View.VISIBLE);
        vBottom.setVisibility(View.VISIBLE);
        tapeGridViewAdapter = new TapeGridViewAdapter(context, tapeList);
        gvTape.setAdapter(tapeGridViewAdapter);
        tapeGridViewAdapter.notifyDataSetInvalidated();
        roomCode = SocketManager.getInstance().getTapeRoomCode(futureItem.getContract().toLowerCase());
        getPositionQuotation(true, futureItem.getContract());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SocketEvent socketEvent){
        SocketManager.socketHint(context, socketEvent.getSocketStatus(), titleBar.getTvSocketHint());
        if (!AppUtil.isActivityRunning(context)) {
            return;
        }
        if (socketEvent.isConnectSuccess()) {//断线重连
            getPositionQuotation(true, futureItem.getContract());
        }
        if (socketEvent.isPush()) {
            try {
                Object[] jsonArray = socketEvent.getJsonArray();
                Gson gson = new Gson();
                JSONObject jsonObject = (JSONObject) jsonArray[0];
                String room=jsonObject.getString("room");
                jsonObject = jsonObject.getJSONObject("data");
                if (room.equals(roomCode.toLowerCase())) {
                    mapValue = gson.fromJson(jsonObject.toString(), Map.class);
                    initData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private double preSettleInt = 0, preCloseint = 0;

    private void initData() {
        if (mapValue == null) {
            return;
        }
        String preSettle = mapValue.get("preSettle");
        String preClose = mapValue.get("preClose");
        if (!TextUtils.isEmpty(preSettle) && !preSettle.equals("- -") && !preSettle.equals("-")) {
            try {
                preSettleInt = Double.valueOf(preSettle);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                preSettleInt = 0;
            }
        } else {
            preSettleInt = 0;
        }
        if (!TextUtils.isEmpty(preClose) && !preClose.equals("- -") && !preClose.equals("-")) {
            try {
                preCloseint = Double.valueOf(preClose);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                preCloseint = 0;
            }
        } else {
            preCloseint = 0;
        }
        for (int x = 0; x < tapeList.size(); x++) {
            TapeSocket.BlocksBean.ItemsBean itemsBean = tapeList.get(x);
            Object arrt = itemsBean.getAttr();
            String key ;
            if (arrt instanceof String) {
                key = (String) arrt;
                if (!TextUtils.isEmpty(key)) {
                    setZValue(itemsBean, key);
                }
            } else {
                List<String> strings = (List<String>) arrt;
                if (strings != null && strings.size() > 0) {
                    for (int a = 0; a < strings.size(); a++) {
                        key = strings.get(a);
                        if (!TextUtils.isEmpty(key)) {
                            String value = mapValue.get(key);
                            if (!TextUtils.isEmpty(value)) {
                                if (a == 0) {
                                    itemsBean.setValue(value);
                                    if (key.equals("updown")) {
                                        double updown ;
                                        try {
                                            updown = Double.valueOf(value);
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                            updown = 0;
                                        }
                                        if (updown > 0) {
                                            itemsBean.setValue( ValueUtil.addMark(value));
                                            itemsBean.setIsColor(1);
                                        } else if (updown < 0) {
                                            itemsBean.setValue( ValueUtil.addMark(value));
                                            itemsBean.setIsColor(2);
                                        } else {
                                            itemsBean.setIsColor(0);
                                        }
                                    }
                                } else {
                                    itemsBean.setValue(itemsBean.getValue() + "/" + ValueUtil.addMark(value));
                                }
                            }
                        }
                    }
                }

            }
        }
        if (tapeGridViewAdapter != null) {
            tapeGridViewAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        SocketManager.getInstance().addRoom(roomCode);
        XLog.d(SocketManager.TAG,"onRestart-------盘口");
    }

    private void setZValue(TapeSocket.BlocksBean.ItemsBean itemsBean, String key) {
        String value = mapValue.get(key);
        itemsBean.setValue(value);
        if (TextUtils.isEmpty(value) || value.equals("- -") || value.equals("-")) {
            return;
        }
        switch (key) {
            case "- -":
            case "-":
                break;
            case "ask1p":
            case "bid1p":
            case "last":
            case "open":
            case "highest":
            case "lowest":
            case "average":
                double j ;
                try {
                    j = Double.valueOf(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    j = 0;
                }
                if (preSettleInt == 0) {
                    if (preCloseint == 0) {
                        itemsBean.setIsColor(0);
                    } else {
                        if (j > preCloseint) {
                            itemsBean.setIsColor(1);
                        } else if (j == preCloseint) {
                            itemsBean.setIsColor(0);
                        } else if (j < preCloseint) {
                            itemsBean.setIsColor(2);
                        }
                    }
                } else {
                    if (j > preSettleInt) {
                        itemsBean.setIsColor(1);
                    } else if (j == preSettleInt) {
                        itemsBean.setIsColor(0);
                    } else if (j < preSettleInt) {
                        itemsBean.setIsColor(2);
                    }
                }
                break;
            case "upLimit":
                itemsBean.setIsColor(1);
                break;
            case "loLimit":
                itemsBean.setIsColor(2);
                break;
            default:
                break;
        }
    }

    private void getPositionQuotation(final boolean firstLoad, String contract) {
        if (firstLoad) {
            DialogUtil.waitDialog(context);
        }
        Api.getMarketService().getPositionQuotationNew(contract)
                .compose(XApi.<BaseModel<TapeSocket>>getApiTransformer())
                .compose(XApi.<BaseModel<TapeSocket>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<TapeSocket>>() {
                    @Override
                    public void onNext(BaseModel<TapeSocket> listBaseModel) {
                        if (vEmpty != null) {
                            vEmpty.setVisibility(View.GONE);
                        }
                        if (gvTape != null) {
                            gvTape.setVisibility(View.VISIBLE);
                        }
                        if (vBottom != null) {
                            vBottom.setVisibility(View.VISIBLE);
                        }
                        if (vTop != null) {
                            vTop.setVisibility(View.VISIBLE);
                        }
                        if (firstLoad) {
                            DialogUtil.dismissDialog();
                        }
                        if (ValueUtil.isEmpty(listBaseModel.getData())||ValueUtil.isListEmpty(listBaseModel.getData().getBlocks())) {
                            vBottom.setVisibility(View.GONE);
                            vTop.setVisibility(View.GONE);
                            gvTape.setVisibility(View.GONE);
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.BLUE);
                            return;
                        }
                        updateUI(listBaseModel.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (gvTape == null) {
                            return;
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getPositionQuotation(true, futureItem.getContract());
                            }
                        }, gvTape, vBottom, vTop);
                        if (firstLoad) {
                            DialogUtil.dismissDialog();
                        }
                    }
                });

    }

    private void updateUI(TapeSocket datalist) {
        if (datalist != null && datalist.getBlocks() != null && datalist.getBlocks().size() >= 1) {
            if (ValueUtil.isListNotEmpty(tapeList)) {
                tapeList.clear();
            }
            List<TapeSocket.BlocksBean.ItemsBean> list = datalist.getBlocks().get(0).getItems();
            if (ValueUtil.isListNotEmpty(list)) {
                tapeList.addAll(list);
            }
            tapeGridViewAdapter.notifyDataSetChanged();

            SocketManager.getInstance().addRoom(roomCode);
        }

    }


    public static void launch(Activity context, RoomItem futureItem) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, futureItem);
            Router.newIntent(context)
                    .to(TapeSocketActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

}
