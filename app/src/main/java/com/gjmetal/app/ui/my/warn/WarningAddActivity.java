package com.gjmetal.app.ui.my.warn;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.alphametal.MeassureNewLast;
import com.gjmetal.app.model.market.NewLast;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.my.WarnAddMonitorModel;
import com.gjmetal.app.model.my.WarnConfigModel;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.app.widget.popuWindow.KeyBoardPopWindow;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description 添加预警
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-17 16:03
 */

public class WarningAddActivity extends BaseActivity {
    @BindView(R.id.btnWarnSubmit)
    Button btnWarnSubmit;
    @BindView(R.id.tvWarnName)
    TextView tvWarnName;
    @BindView(R.id.tvNewValue)
    TextView tvNewValue;
    @BindView(R.id.tvChangeValue)
    TextView tvChangeValue;
    @BindView(R.id.tvAmountValue)
    TextView tvAmountValue;
    @BindView(R.id.tvWarnType)
    TextView tvWarnType;
    @BindView(R.id.llWarnType)
    RelativeLayout llWarnType;
    @BindView(R.id.tvWarnRange)
    TextView tvWarnRange;
    @BindView(R.id.llWarnRange)
    RelativeLayout llWarnRange;
    @BindView(R.id.etWarnRangeValue)
    EditText etWarnRangeValue;
    @BindView(R.id.tvWarnHz)
    TextView tvWarnHz;
    @BindView(R.id.llWarnHz)
    RelativeLayout llWarnHz;
    @BindView(R.id.tvAddWarnItemRange)
    TextView tvAddWarnItemRange;
    @BindView(R.id.rlWran)
    RelativeLayout rlWran;
    @BindView(R.id.llMain)
    LinearLayout llMain;

    private KeyBoardPopWindow mKeyBoardPopWindow; //键盘
    private SingleChooseDialog mWareTypeDialog; //预警类型
    private SingleChooseDialog mWareRangeDialog; //范围
    private SingleChooseDialog mWareHzDialog; //频率
    private List<String> mWareTypeDatas;
    private List<String> mWareRangeDatas;
    private List<String> mWareHzDatas;

    private String mWareType; //类型
    private String mWareRange;//范围
    private int mWareHzTime;//频率 时间
    private String mWareHzTimeType;//频率 时间类型
    private String mPattern;//预警类型

    private String mContract; //预类型
    private String mName;

    private String mType;  //判断从那个类跳入类
    private String mMonitorType; //数据指标类型

    private int mTouchSlop;
    // 手机按下时的屏幕坐标
    private float mXDown;
    private float mYDown;
    //手机当时所处的屏幕坐标
    private float mXMove;
    private float mYMove;

    private ArrayList<String> mMonitorTypeLists;
    private ArrayList<String> mOperationRangeLists;
    private List<WarnConfigModel.IntervalConfigListBean> mIntervalConfigListBeans;
    private WarnConfigModel mWarnConfigModel;
    private String roomCode;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_warning_urer);
    }

    @Override
    protected void fillData() {
        mType = getIntent().getExtras().getString("wareType");
        mMonitorType = getIntent().getExtras().getString("monitorType");
        mName = getIntent().getExtras().getString("name");
        mContract = getIntent().getExtras().getString("contract");
        mPattern = getIntent().getExtras().getString("pattern");
        tvWarnName.setText(mName + mPattern);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getResources().getString(R.string.add_warning));
        titleBar.rightLayout.setVisibility(View.VISIBLE);
        titleBar.tvRight.setVisibility(View.VISIBLE);
        titleBar.tvRight.setText(getResources().getString(R.string.user_warning));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this, R.color.c9EB2CD));
        titleBar.tvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleBar.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转 我的预警 界面
                if (User.getInstance().isLoginIng()) {
                    WarningUserActivity.launch(WarningAddActivity.this);
                } else {
                    LoginActivity.launch(WarningAddActivity.this);
                }

            }
        });
        mMonitorTypeLists = new ArrayList<>();
        mOperationRangeLists = new ArrayList<>();
        mWareTypeDatas = new ArrayList<>();
        mWareRangeDatas = new ArrayList<>();
        mWareHzDatas = new ArrayList<>();
        initKeyBoard();
        setKeyBoardPopWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        SocketManager.socketHint(context, socketEvent.getSocketStatus(), titleBar.getTvSocketHint());
        if (socketEvent.isConnectSuccess()) {//断线重连
            SocketManager.getInstance().addRoom(roomCode);
        }
        if (socketEvent.isPush()) {
            try {
                Object[] jsonArray = socketEvent.getJsonArray();
                Gson gson = new Gson();
                JSONObject jsonObject = (JSONObject) jsonArray[0];
                jsonObject = jsonObject.getJSONObject("data");
                MeassureNewLast datalist = gson.fromJson(jsonObject.toString(), MeassureNewLast.class);

                if (ValueUtil.isNotEmpty(datalist) && datalist.getContract().equals(mContract)) {
                    tvNewValue.setText(datalist.getLast());
                    tvChangeValue.setText(datalist.getUpdown());
                    tvAmountValue.setText(datalist.getPercent());
                    GjUtil.lastUpOrDown(WarningAddActivity.this, tvAmountValue.getText().toString(),
                            tvNewValue, tvChangeValue, tvAmountValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        etWarnRangeValue.setText("");
        getWarningConfigSet(); //预警控件配置类型
        getLast(); //获取测算最新数据
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static void launch(Activity context, String name, String type, String contract, String monitorType, String pattern) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("contract", contract); //CUSpotParity
            bundle.putString("wareType", type);
            bundle.putString("monitorType", monitorType);
            bundle.putString("pattern", pattern);//0=无、1=盈亏、2=比值
            Router.newIntent(context)
                    .to(WarningAddActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    private void setWarningData() {
        mWareTypeDatas.clear();
        mWareRangeDatas.clear();
        mWareHzDatas.clear();
        mMonitorTypeLists.clear();
        mOperationRangeLists.clear();

        int count = mWarnConfigModel.getMonitorCount();
        if (count == mWarnConfigModel.getMaxMonitorCount()) {
            tvAddWarnItemRange.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
            btnWarnSubmit.setVisibility(View.GONE);
            return;
        }

        mMonitorTypeLists.addAll(spiltStr(mWarnConfigModel.getMonitorWay()));
        if (ValueUtil.isListNotEmpty(mMonitorTypeLists)) {
            mWareType = mMonitorTypeLists.get(0);
        }

        mOperationRangeLists.addAll(spiltStr(mWarnConfigModel.getOperation()));
        if (ValueUtil.isListNotEmpty(mOperationRangeLists)) {
            mWareRange = mOperationRangeLists.get(0);
        }

        mWareTypeDatas.addAll(spiltStr(mWarnConfigModel.getMonitorWayName()));
        if (ValueUtil.isListNotEmpty(mWareTypeDatas)) {
            tvWarnType.setText(mWareTypeDatas.get(0));
        }


        mWareRangeDatas.addAll(spiltStr(mWarnConfigModel.getOperationName()));
        if (ValueUtil.isListNotEmpty(mWareRangeDatas)) {
            tvWarnRange.setText(mWareRangeDatas.get(0));
        }

        mIntervalConfigListBeans = mWarnConfigModel.getIntervalConfigList();
        if (ValueUtil.isListNotEmpty(mIntervalConfigListBeans)) {
            for (int i = 0; i < mIntervalConfigListBeans.size(); i++) {
                mWareHzDatas.add(mIntervalConfigListBeans.get(i).getIntervalValue()
                        + mIntervalConfigListBeans.get(i).getUnitName());

            }
            mWareHzTime = mIntervalConfigListBeans.get(0).getIntervalValue();//频率 时间
            mWareHzTimeType = mIntervalConfigListBeans.get(0).getTimeUnit();//频率 时间类型
            if (ValueUtil.isListNotEmpty(mWareHzDatas)) {
                tvWarnHz.setText(mWareHzDatas.get(0));
            }
        }
    }

    private List<String> spiltStr(String str) {
        return Arrays.asList(str.split(";"));
    }


    private void initKeyBoard() {
        ViewConfiguration configuration = ViewConfiguration.get(this);
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        //自定义键盘光标可以自由移动 适用系统版本为android3.0以上
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            setShowSoftInputOnFocus = cls.getMethod(
                    "setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(etWarnRangeValue, false);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void setKeyBoardPopWindow() {
        mKeyBoardPopWindow = new KeyBoardPopWindow(this);
        etWarnRangeValue.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mXDown = event.getRawX();
                        mYDown = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        mXMove = event.getRawX();
                        mYMove = event.getRawY();
                        if (mYMove - mYDown < mTouchSlop && mXMove - mXDown < mTouchSlop) {
                            //点击按钮显示键盘
                            mKeyBoardPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                            GjUtil.openKeyBoard();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mKeyBoardPopWindow.setOnKeyClickListener(new KeyBoardPopWindow.OnKeyClickListener() {
            @Override
            public void onInertKey(String text) {
                int index = etWarnRangeValue.getSelectionStart();
                Editable editable = etWarnRangeValue.getText();
                editable.insert(index, text);
            }

            @Override
            public void onDeleteKey() {
                int last = etWarnRangeValue.getText().length();
                if (last > 0) {
                    //删除最后一位
                    int index = etWarnRangeValue.getSelectionStart();
                    if (index > 0) {
                        etWarnRangeValue.getText().delete(index - 1, index);
                    }
                }
            }

            @Override
            public void onClearKey() {
                etWarnRangeValue.getText().clear();
            }
        });


    }


    //预警控件配置类型
    private void getWarningConfigSet() {
        Api.getMyService().getWarningConfig(mMonitorType)
                .compose(XApi.<BaseModel<WarnConfigModel>>getApiTransformer())
                .compose(XApi.<BaseModel<WarnConfigModel>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<WarnConfigModel>>() {
                    @Override
                    public void onNext(BaseModel<WarnConfigModel> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            mWarnConfigModel = listBaseModel.getData();

                        }
                        tvAddWarnItemRange.setVisibility(View.GONE);
                        llMain.setVisibility(View.VISIBLE);
                        btnWarnSubmit.setVisibility(View.VISIBLE);
                        setWarningData();
                    }

                    @Override
                    protected void onFail(NetError error) {
//                        ToastUtil.showToast(error.getMessage());
                    }
                });

    }


    //获取测算最新数据
    private void getLast() {
        if (mType.equals("ExchangeChartActivity")){
            Api.getMarketService().getNewLast(mContract)
                    .compose(XApi.<BaseModel<NewLast>>getApiTransformer())
                    .compose(XApi.<BaseModel<NewLast>>getScheduler())
                    .subscribe(new ApiSubscriber<BaseModel<NewLast>>() {
                        @Override
                        public void onNext(BaseModel<NewLast> listBaseModel) {
                            if (ValueUtil.isNotEmpty(listBaseModel)&&ValueUtil.isNotEmpty(listBaseModel.getData())) {
                                if (ValueUtil.isStrEmpty(listBaseModel.getData().getLast()) || listBaseModel.getData().getLast().equals("-")) {
                                    tvNewValue.setText("- -");
                                } else {
                                    tvNewValue.setText(listBaseModel.getData().getLast());
                                }

                                if (ValueUtil.isStrEmpty(listBaseModel.getData().getUpdown()) || listBaseModel.getData().getUpdown().equals("-")) {
                                    tvChangeValue.setText("- -");
                                } else {
                                    tvChangeValue.setText(listBaseModel.getData().getUpdown());
                                }
                                if (ValueUtil.isStrEmpty(listBaseModel.getData().getPercent()) || listBaseModel.getData().getPercent().equals("-")) {
                                    tvAmountValue.setText("- -");
                                } else {
                                    tvAmountValue.setText(listBaseModel.getData().getPercent());
                                }
                                GjUtil.lastUpOrDown(WarningAddActivity.this, tvAmountValue.getText().toString(),
                                        tvNewValue, tvChangeValue, tvAmountValue);
                            } else {
                                tvNewValue.setText("- -");
                                tvChangeValue.setText("- -");
                                tvAmountValue.setText("- -");
                                GjUtil.lastUpOrDown(WarningAddActivity.this, tvAmountValue.getText().toString(),
                                        tvNewValue, tvChangeValue, tvAmountValue);
                            }
                        }

                        @Override
                        protected void onFail(NetError error) {
                            tvNewValue.setText("- -");
                            tvChangeValue.setText("- -");
                            tvAmountValue.setText("- -");
                            GjUtil.lastUpOrDown(WarningAddActivity.this, tvAmountValue.getText().toString(),
                                    tvNewValue, tvChangeValue, tvAmountValue);
                        }
                    });
        }else {
            roomCode = SocketManager.getInstance().getTapeRoomCode(mContract);
            SocketManager.getInstance().addRoom(roomCode);
        }
    }

    @OnClick({R.id.btnWarnSubmit, R.id.llWarnType, R.id.llWarnRange, R.id.llWarnHz})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnWarnSubmit: //提交
                if (ValueUtil.isStrEmpty(etWarnRangeValue.getText().toString())) {
                    ToastUtil.showToast(getResources().getString(R.string.select_contenct_no_complete));
                } else {
                    submitWareValue();
                }
                break;

            case R.id.llWarnType: //预警类型
                if (mWareTypeDialog == null) {
                    selecterWareType();
                }
                if (ValueUtil.isListNotEmpty(mWareTypeDatas)) {
                    mWareTypeDialog.show();
                }
                break;

            case R.id.llWarnRange: //范围
                if (mWareRangeDialog == null) {
                    selecterWareRange();
                }
                if (ValueUtil.isListNotEmpty(mWareRangeDatas)) {
                    mWareRangeDialog.show();
                }
                break;

            case R.id.llWarnHz:  //频率
                if (mWareHzDialog == null) {
                    selecterWareHz();
                }
                if (ValueUtil.isListNotEmpty(mWareHzDatas)) {
                    mWareHzDialog.show();
                }
                break;
        }
    }

    //预警类型
    private void selecterWareType() {
        mWareTypeDialog = new SingleChooseDialog(this,
                R.style.Theme_dialog, mWareTypeDatas, tvWarnType.getText().toString());
        mWareTypeDialog.setCancelable(true);
        mWareTypeDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mWareTypeDialog.getWindow().setGravity(Gravity.BOTTOM);
        mWareTypeDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        tvWarnType.setText(price);
                        if (ValueUtil.isListNotEmpty(mMonitorTypeLists)) {
                            mWareType = mMonitorTypeLists.get(position);
                        }
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
    }

    //范围
    private void selecterWareRange() {
        mWareRangeDialog = new SingleChooseDialog(this,
                R.style.Theme_dialog, mWareRangeDatas, tvWarnRange.getText().toString());
        mWareRangeDialog.setCancelable(true);
        mWareRangeDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mWareRangeDialog.getWindow().setGravity(Gravity.BOTTOM);
        mWareRangeDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        tvWarnRange.setText(price);
                        if (ValueUtil.isListNotEmpty(mOperationRangeLists)) {
                            mWareRange = mOperationRangeLists.get(position);
                        }
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
    }


    //频率
    private void selecterWareHz() {
        mWareHzDialog = new SingleChooseDialog(this,
                R.style.Theme_dialog, mWareHzDatas, tvWarnHz.getText().toString());
        mWareHzDialog.setCancelable(true);
        mWareHzDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mWareHzDialog.getWindow().setGravity(Gravity.BOTTOM);
        mWareHzDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        tvWarnHz.setText(price);
                        if (ValueUtil.isListNotEmpty(mIntervalConfigListBeans)) {
                            mWareHzTime = mIntervalConfigListBeans.get(position).getIntervalValue();
                            mWareHzTimeType = mIntervalConfigListBeans.get(position).getTimeUnit();
                        }
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
    }


    //提交
    private void submitWareValue() {
        WarnAddMonitorModel warnAddMonitorModel = new WarnAddMonitorModel();
        warnAddMonitorModel.setFluctuation(etWarnRangeValue.getText().toString()); //浮动动值
        warnAddMonitorModel.setIndicatorRefCode(mContract);  //指标关联Code
        warnAddMonitorModel.setIndicatorType(mMonitorType); //数据指标类型
        warnAddMonitorModel.setIntervalTime(mWareHzTime); //间隔时间
        warnAddMonitorModel.setMonitorName(mName); //指标名称
        warnAddMonitorModel.setMonitorWay(mWareType); //监控方式
        warnAddMonitorModel.setOperator(mWareRange); //> >= <= <
        warnAddMonitorModel.setTimeUnit(mWareHzTimeType); //间隔时间 时分秒
        String d = GsonUtil.toJson(warnAddMonitorModel);

        RequestBody body = RequestBody.create(GsonUtil.toJson(warnAddMonitorModel), MediaType.parse("application/json; charset=utf-8")
        );
        Api.getMyService().addMonitor(body)
                .compose(XApi.<BaseModel<Integer>>getApiTransformer())
                .compose(XApi.<BaseModel<Integer>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Integer>>() {
                    @Override
                    public void onNext(BaseModel<Integer> listBaseModel) {
                        if (ValueUtil.isEmpty(listBaseModel)) {
                            return;
                        }
                        ToastUtil.showToast(listBaseModel.getMessage());
                        if (ValueUtil.isNotEmpty(listBaseModel.getData())) {
                            if (listBaseModel.getData() >= mWarnConfigModel.getMaxMonitorCount()) {
                                tvAddWarnItemRange.setVisibility(View.VISIBLE);
                                llMain.setVisibility(View.GONE);
                                btnWarnSubmit.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (ValueUtil.isNotEmpty(error)) {
                            if (ValueUtil.isStrNotEmpty(error.getMessage())) {
                                ToastUtil.showToast(error.getMessage());
                            }
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (mKeyBoardPopWindow.isShowing()) {
            mKeyBoardPopWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}






















