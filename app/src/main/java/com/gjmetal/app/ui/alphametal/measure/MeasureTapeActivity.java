package com.gjmetal.app.ui.alphametal.measure;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MeasureNextTapeGridViewAdapter;
import com.gjmetal.app.adapter.alphametal.MeasureTapeGridViewAdapter;
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
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyGridView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.kline.MeasureLeftHorizontalView;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Description:进||出 口测算盘口
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/23  10:14
 */

public class MeasureTapeActivity extends BaseActivity {
    @BindView(R.id.gvTop)
    MyGridView gvTop;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.vTop)
    View vTop;
    @BindView(R.id.vBottom)
    View vBottom;
    @BindView(R.id.gvBottom)
    MyGridView gvBottom;
    @BindView(R.id.vNextTop)
    View vNextTop;
    @BindView(R.id.vNextBottom)
    View vNextBottom;
    private RoomItem bean;
    private MeasureTapeGridViewAdapter measureTapeGridViewAdapter;
    private MeasureNextTapeGridViewAdapter measureNextTapeGridViewAdapter;
    private List<TapeSocket.BlocksBean.ItemsBean> topList;
    private List<TapeSocket.BlocksBean.ItemsBean> bottomList;
    private MeasureLeftHorizontalView.MeasureType measureType = MeasureLeftHorizontalView.MeasureType.IMPORT;
    private String mContract;
    private Map<String, String> mapValue;
    private String roomCode;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_measure_tape);
        KnifeKit.bind(this);
    }

    @Override
    protected void fillData() {
        if (!NetUtil.checkNet(context)) {
            SocketManager.socketHint(context, SocketManager.DISNNECT, titleBar.getTvSocketHint());
        }
        bean = (RoomItem) getIntent().getSerializableExtra(Constant.MODEL);
        measureType = (MeasureLeftHorizontalView.MeasureType) getIntent().getSerializableExtra(Constant.ACT_ENUM);
        if (ValueUtil.isEmpty(bean)) {
            return;
        }
        switch (measureType) {
            case LME:
                mContract = bean.getParityCode();
                gvTop.setVisibility(View.VISIBLE);
                vTop.setVisibility(View.VISIBLE);
                vBottom.setVisibility(View.VISIBLE);
                gvBottom.setVisibility(View.GONE);
                vNextBottom.setVisibility(View.GONE);
                vNextTop.setVisibility(View.GONE);
                initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, bean.getName() + "-" + bean.getParityName());
                break;
            case IMPORT:
                mContract = bean.getProfitCode();
                gvTop.setVisibility(View.VISIBLE);
                vTop.setVisibility(View.VISIBLE);
                vBottom.setVisibility(View.VISIBLE);
                gvBottom.setVisibility(View.VISIBLE);
                vNextBottom.setVisibility(View.VISIBLE);
                vNextTop.setVisibility(View.VISIBLE);
                initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, bean.getName() + "-" + bean.getProfitName());
                break;
        }

        topList = new ArrayList<>();
        bottomList = new ArrayList<>();
        measureTapeGridViewAdapter = new MeasureTapeGridViewAdapter(context, topList);
        measureNextTapeGridViewAdapter = new MeasureNextTapeGridViewAdapter(context, bottomList);
        gvTop.setAdapter(measureTapeGridViewAdapter);
        gvBottom.setAdapter(measureNextTapeGridViewAdapter);

        XLog.e("mContract", "mContract===" + mContract);
        roomCode=SocketManager.getInstance().getTapeRoomCode(mContract);
        getPositionQuotation(true, mContract);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        SocketManager.socketHint(context, socketEvent.getSocketStatus(), titleBar.getTvSocketHint());
        if (!AppUtil.isActivityRunning(context)) {
            return;
        }
        if (socketEvent.isConnectSuccess()) {//断线重连
            getPositionQuotation(true, mContract);
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
                    initData(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getPositionQuotation(final boolean firstLoad, final String contract) {
        if (firstLoad) {
            DialogUtil.waitDialog(context);
        }
        Api.getMarketService().getPositionQuotationNew(contract)
                .compose(XApi.<BaseModel<TapeSocket>>getApiTransformer())
                .compose(XApi.<BaseModel<TapeSocket>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<TapeSocket>>() {
                    @Override
                    public void onNext(BaseModel<TapeSocket> listBaseModel) {
                        showView();
                        if (firstLoad) {
                            DialogUtil.dismissDialog();
                        }
                        if (ValueUtil.isEmpty(listBaseModel.getData()) || ValueUtil.isListEmpty(listBaseModel.getData().getBlocks())) {
                            vBottom.setVisibility(View.GONE);
                            vTop.setVisibility(View.GONE);
                            vNextBottom.setVisibility(View.GONE);
                            vNextTop.setVisibility(View.GONE);
                            gvTop.setVisibility(View.GONE);
                            vEmpty.setVisibility(View.VISIBLE);
                            vEmpty.setNoData(Constant.BgColor.BLUE);
                            return;
                        }
                        updateUI(listBaseModel.getData().getBlocks());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (gvTop == null) {
                            return;
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getPositionQuotation(true, mContract);
                            }
                        }, gvTop, vBottom, vTop, vNextTop, vNextBottom, gvBottom);
                        if (firstLoad) {
                            DialogUtil.dismissDialog();
                        }
                    }
                });
    }

    private void showView() {
        vEmpty.setVisibility(View.GONE);
        switch (measureType) {
            case LME:
                gvBottom.setVisibility(View.GONE);
                vNextBottom.setVisibility(View.GONE);
                vNextTop.setVisibility(View.GONE);
                gvTop.setVisibility(View.VISIBLE);
                vBottom.setVisibility(View.VISIBLE);
                vTop.setVisibility(View.VISIBLE);
                break;
            case IMPORT:
                gvTop.setVisibility(View.VISIBLE);
                gvBottom.setVisibility(View.VISIBLE);
                vNextBottom.setVisibility(View.VISIBLE);
                vNextTop.setVisibility(View.VISIBLE);
                vBottom.setVisibility(View.VISIBLE);
                vTop.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateUI(List<TapeSocket.BlocksBean> blocksBeans) {
        if (ValueUtil.isListNotEmpty(topList)) {
            topList.clear();
        }
        if (ValueUtil.isListNotEmpty(bottomList)) {
            bottomList.clear();
        }
        if (blocksBeans != null && blocksBeans.size() >= 1 && blocksBeans.get(0) != null) {
            topList.addAll(blocksBeans.get(0).getItems());
        }

        if (ValueUtil.isListNotEmpty(topList)) {
            if (topList.size() % 2 != 0) {
                topList.add(new TapeSocket.BlocksBean.ItemsBean("", "", true));
            }
        }
        if (blocksBeans.size() >= 2 && ValueUtil.isListNotEmpty(blocksBeans.get(1).getItems())) {
            gvBottom.setVisibility(View.VISIBLE);
            vNextBottom.setVisibility(View.VISIBLE);
            vNextTop.setVisibility(View.VISIBLE);
            bottomList.addAll(blocksBeans.get(1).getItems());
            if (bottomList.size() % 2 != 0) {
                bottomList.add(new TapeSocket.BlocksBean.ItemsBean("", "", true));
            }
        }

        measureTapeGridViewAdapter.notifyDataSetChanged();
        measureNextTapeGridViewAdapter.notifyDataSetChanged();

        SocketManager.getInstance().addRoom(roomCode);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SocketManager.getInstance().addRoom(roomCode);
        XLog.d(SocketManager.TAG,"onRestart-------盘口");
    }
    private void initData(JSONObject jsonObject) {
        if (mapValue == null || topList == null || bottomList == null) {
            return;
        }
        if (!mContract.equals(mapValue.get("contract"))) {
            return;
        }
        String preClose=mapValue.get("preClose");
        if (ValueUtil.isStrEmpty(preClose)||preClose.equals("- -")){
            measureTapeGridViewAdapter.setPreClose(0,false);
        }else {
            float v= 0;
            try {
                v = Float.parseFloat(preClose);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                measureTapeGridViewAdapter.setPreClose(0,false);
            }
            measureTapeGridViewAdapter.setPreClose(v,true);
        }
        setTopList(jsonObject, topList);
        measureTapeGridViewAdapter.notifyDataSetChanged();
        setTopList(jsonObject, bottomList);
        measureNextTapeGridViewAdapter.notifyDataSetChanged();
    }

    private void setTopList(JSONObject jsonObject, List<TapeSocket.BlocksBean.ItemsBean> list) {
        for (int x = 0; x < list.size(); x++) {
            TapeSocket.BlocksBean.ItemsBean itemsBean = list.get(x);
            Object arrt = itemsBean.getAttr();
            String key ;
            if (arrt instanceof String) {
                key = (String) arrt;
                if (!key.contains(".")) {
                    setZValue(itemsBean, key);
                } else {
                    String[] keys = key.split("\\.");
                    if (keys.length > 0) {
                        Object valuea = "- -";
                        JSONObject jsonObject1 = null;
                        try {
                        for (int j = 0; j < keys.length; j++) {
                                if (j == 0) {
                                    jsonObject1 = jsonObject.getJSONObject(keys[0]);
                                } else {
                                    if (j == keys.length - 1) {
                                        valuea = jsonObject1.get(keys[j]);
                                    } else {
                                        jsonObject1 = jsonObject1.getJSONObject(keys[j]);
                                    }
                                }
                            XLog.e("aaaaa", "valuea==" + valuea.toString() + "-----" + j);
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                        itemsBean.setValue(valuea.toString());
                    }
                }
            } else {
                    List<String> strings = (List<String>) arrt;
                    if (strings != null && strings.size() > 0) {
                        for (int a = 0; a < strings.size(); a++) {
                            key = strings.get(a);
                            if (a == 0) {
                                if (!TextUtils.isEmpty(key)) {
                                    addMark(itemsBean, mapValue.get(key));
                                }
                            } else {
                                String value = mapValue.get(key);
                                if (!TextUtils.isEmpty(value)) {
                                    itemsBean.setValue(itemsBean.getValue() + "/" + ValueUtil.addMark(value));
                                }else {
                                    itemsBean.setValue(itemsBean.getValue() + "/- -");
                                }
                            }
                        }
                    }
            }
        }
    }
    private void  addMark(TapeSocket.BlocksBean.ItemsBean itemsBean,  String value){
            String  strValue="";
            double douValue=0;
            if (ValueUtil.isNumber(value)) {
                strValue = value;
                try {
                    douValue = Double.valueOf(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (douValue > 0) {
                    itemsBean.setIsColor(1);
                    if (!value.contains("+")) {
                        strValue = "+" + strValue;
                    }
                } else if (douValue == 0) {
                    strValue = strValue;
                } else {
                    itemsBean.setIsColor(2);
                    if (!value.contains("-")) {
                        strValue = "-" + strValue;
                    }
                }
                itemsBean.setValue(strValue);
            }
    }

    private void setZValue(TapeSocket.BlocksBean.ItemsBean itemsBean, String key) {
        if (mapValue != null) {
            String value = mapValue.get(key);
            if (!TextUtils.isEmpty(value)) {
                itemsBean.setValue(value);
            }
        }
    }

    public static void launch(Activity context, RoomItem bean, MeasureLeftHorizontalView.MeasureType measureType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, bean);
        bundle.putSerializable(Constant.ACT_ENUM, measureType);
        Router.newIntent(context)
                .to(MeasureTapeActivity.class)
                .data(bundle)
                .launch();
    }

}
