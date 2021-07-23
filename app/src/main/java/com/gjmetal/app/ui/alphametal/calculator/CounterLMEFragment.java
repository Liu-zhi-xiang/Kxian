package com.gjmetal.app.ui.alphametal.calculator;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.alphametal.QuotationsModel;
import com.gjmetal.app.model.alphametal.RateComputerModel;
import com.gjmetal.app.model.alphametal.RateModel;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.KeyBoardUtils;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.CounterContractDialog;
import com.gjmetal.app.widget.dialog.CounterLmeDateDialog;
import com.gjmetal.app.widget.popuWindow.KeyBoardPopWindow;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.star.kchart.utils.DateUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description LME合约
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-13 17:25
 */

public class CounterLMEFragment extends BaseFragment {
    @BindView(R.id.rbUp)
    RadioButton rbUp;
    @BindView(R.id.rbDown)
    RadioButton rbDown;
    @BindView(R.id.counterRadioGroup)
    RadioGroup counterRadioGroup;
    @BindView(R.id.tvContranct)
    TextView tvContranct;
    @BindView(R.id.llSelecterContract)
    RelativeLayout llSelecterContract;
    @BindView(R.id.etPric)
    EditText etPric;
    @BindView(R.id.rlSelecterPrice)
    RelativeLayout rlSelecterPrice;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.llDateContract)
    RelativeLayout llDateContract;
    @BindView(R.id.tvValueShibor)
    TextView tvValueShibor;
    @BindView(R.id.tvNameShibor)
    TextView tvNameShibor;
    @BindView(R.id.etRelocateValue)
    EditText etRelocateValue;
    @BindView(R.id.tvRelocateBp)
    TextView tvRelocateBp;
    @BindView(R.id.btnComputerValue)
    Button btnComputerValue;
    @BindView(R.id.bottomView)
    View bottomView;
    @BindView(R.id.svCounter)
    NestedScrollView svCounter;
    @BindView(R.id.flCounter)
    RelativeLayout flCounter;

    private int mQuotationsMainPosition = 0;
    private int mQuotationsChildPosition = 0;
    private List<QuotationsModel> mListQuotationItems;//合约
    private String mOptionsDirection = "0"; //方向 0:看涨 1:看跌
    private String mMenuCode; //期权类型   0:上期所  1.LME
    private String mContract; // 合约Code
    private String mRateValue; //利率价格
    private int mTouchSlop;
    private float mXDown; // 手机按下时的屏幕坐标
    private float mYDown;
    private float mXMove; //手机当时所处的屏幕坐标
    private float mYMove;
    private int mKeyBoardHeight;
    private KeyBoardPopWindow mKeyBoardPopWindow;
    private CounterContractDialog mCounterContractDialog;
    private CounterLmeDateDialog mCounterLmeDateDialog;

    public CounterLMEFragment() {

    }
    @SuppressLint("ValidFragment")
    public CounterLMEFragment(String menuCode) {
        this.mMenuCode = menuCode;
    }


    @Override
    protected int setRootView() {
        return R.layout.fragment_counter_lme;
    }

    @Override
    protected void initView() {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        //自定义键盘光标可以自由移动 适用系统版本为android3.0以上

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(
                        "setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(etRelocateValue, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        svCounter.fullScroll(ScrollView.FOCUS_DOWN);
        mListQuotationItems = new ArrayList<>();

        initRedioGroup();
        setKeyBoardPopWindow();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getQuotationsData(false); //获取合约数据
                getRateData(); //Shibor(¥,O/N)
            }
        }).start();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (!isAdded()) {
            return;
        }
        if (baseEvent.isExitKeyBoard()) {
            if (mKeyBoardPopWindow != null) {
                mKeyBoardPopWindow.dismiss();
            }
            bottomView.setVisibility(View.GONE);

        }
    }
    private void initRedioGroup() {
        counterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbUp:
                        getQuotationsData(false);
                        mOptionsDirection = "0";
                        break;

                    case R.id.rbDown:
                        getQuotationsData(false);
                        mOptionsDirection = "1";
                        break;
                    default:
                        break;
                }
            }
        });

    }

    //合约
    private void getQuotationsData(final boolean showDialog) {
        DialogUtil.waitDialog(getActivity());
        Api.getAlphaMetalService().getOptionsQuotations(mMenuCode)
                .compose(XApi.<BaseModel<List<QuotationsModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<QuotationsModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<QuotationsModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<QuotationsModel>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if(ValueUtil.isListNotEmpty(mListQuotationItems)){
                            mListQuotationItems.clear();
                        }
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                                mListQuotationItems.addAll(listBaseModel.getData());
                            }
                            if(showDialog){
                                initContractDialog();
                            }
                        }


                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                        if(ValueUtil.isListNotEmpty(mListQuotationItems)){
                            mListQuotationItems.clear();
                        }

                    }
                });

    }


    //Shibor(¥,O/N)
    private void getRateData() {
        Api.getAlphaMetalService().getRate(mMenuCode)
                .compose(XApi.<BaseModel<RateModel>>getApiTransformer())
                .compose(XApi.<BaseModel<RateModel>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<RateModel>>() {
                    @Override
                    public void onNext(BaseModel<RateModel> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            if (ValueUtil.isNotEmpty(listBaseModel.getData())) {
                                if (ValueUtil.isStrNotEmpty(listBaseModel.getData().getRateName())) {
                                    tvNameShibor.setText(listBaseModel.getData().getRateName());
                                } else {
                                    tvNameShibor.setText("- -");
                                }

                                if (ValueUtil.isStrNotEmpty(listBaseModel.getData().getRateValue())) {
                                    mRateValue = listBaseModel.getData().getRateValue();
                                    tvValueShibor.setText(mRateValue + "%");
                                } else {
                                    tvValueShibor.setText("- -");
                                }
                            } else {
                                tvNameShibor.setText("- -");
                                tvValueShibor.setText("- -");
                            }
                        } else {
                            tvNameShibor.setText("- -");
                            tvValueShibor.setText("- -");
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        tvNameShibor.setText("- -");
                        tvValueShibor.setText("- -");
                    }
                });
    }


    private void setKeyBoardPopWindow() {
        mKeyBoardPopWindow = new KeyBoardPopWindow(getContext());
        etRelocateValue.setOnTouchListener(new View.OnTouchListener() {

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
                            if (!KeyBoardUtils.isSoftShowing(getActivity())) {
                                //点击按钮显示键盘
                                mKeyBoardPopWindow.showAtLocation(flCounter, Gravity.BOTTOM, 0, 0);
                                GjUtil.openKeyBoard();
                                mKeyBoardHeight = mKeyBoardPopWindow.getKeyView().getMeasuredHeight();
                                bottomView.setMinimumHeight(mKeyBoardHeight);
                                bottomView.setVisibility(View.VISIBLE);
                            }

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
                int index = etRelocateValue.getSelectionStart();
                if ((index == 0 && !StrUntils.matchAddSubMark(text))) {
                    ToastUtil.showToast(getResources().getString(R.string.key_board_add_sub_mark));

                } else if ((index > 0 && StrUntils.matchAddSubMark(text))) {
                    ToastUtil.showToast(getResources().getString(R.string.key_board_add_sub_mark));

                } else {
                    if (index == 1 && text.equals(".")) {
                        text = "0.";
                    }
                    if (text.equals(".") && etRelocateValue.getText().toString().contains(".")) {
                        ToastUtil.showToast(getResources().getString(R.string.key_board_point));
                        return;
                    }
                    Editable editable = etRelocateValue.getText();
                    editable.insert(index, text);

                }
            }

            @Override
            public void onDeleteKey() {
                int last = etRelocateValue.getText().length();
                if (last > 0) {
                    //删除最后一位
                    int index = etRelocateValue.getSelectionStart();
                    if (index > 0) {
                        etRelocateValue.getText().delete(index - 1, index);
                    }
                }
            }

            @Override
            public void onClearKey() {
                etRelocateValue.getText().clear();
            }
        });


    }


    @OnClick({R.id.llSelecterContract, R.id.llDateContract, R.id.btnComputerValue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSelecterContract:  //选择合约
                initContractDialog();
                break;
            case R.id.llDateContract:     //到期日
                initDateDialog();
                break;
            case R.id.btnComputerValue: //计算结果
                AppAnalytics.getInstance().onEvent(getContext(),"alpha_optioncalculators_calculate");
                initComputerValue();
                break;
        }
    }


    //选择合约
    private void initContractDialog() {
        if(ValueUtil.isListEmpty(mListQuotationItems)){
            getQuotationsData(true);//解决网络问题，接口请求失败时，重新请求并弹dialog
            ToastUtil.showToast(R.string.no_getdata);
            return;
        }
        mCounterContractDialog = new CounterContractDialog(getContext(), R.style.Theme_dialog, mListQuotationItems, mQuotationsMainPosition, mQuotationsChildPosition);
        mCounterContractDialog.setCancelable(true);
        mCounterContractDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mCounterContractDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterContractDialog.setOnDialogClickListener(new CounterContractDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String name, String price, int mainPosition, int childPosition) {
                tvContranct.setText(name + price);
                mQuotationsMainPosition = mainPosition;
                mQuotationsChildPosition = childPosition;
                if (ValueUtil.isListNotEmpty(mListQuotationItems)) {
                    if (ValueUtil.isListNotEmpty(mListQuotationItems.get(mQuotationsMainPosition).getNameList())) {
                        mContract = mListQuotationItems.get(mQuotationsMainPosition).getNameList()
                                .get(mQuotationsChildPosition).getOptionsCode();

                    }
                }
            }
        });
        mCounterContractDialog.show();
    }


    //到期日
    private void initDateDialog() {
        Long date;
        if (ValueUtil.isStrNotEmpty(tvDate.getText().toString())) {
            date = DateUtil.dateToStamp(tvDate.getText().toString(), 2);
        } else {
            date = System.currentTimeMillis();
        }
        mCounterLmeDateDialog = new CounterLmeDateDialog(getContext(), R.style.Theme_dialog, date);
        mCounterLmeDateDialog.setCancelable(true);
        mCounterLmeDateDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mCounterLmeDateDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterLmeDateDialog.setOnDialogClickListener(new CounterLmeDateDialog.OnDialogClickListener() {

            @Override
            public void dialogClick(Dialog dialog, View v, long date) {
                tvDate.setText(DateUtil.getStringDateByLong(date, 2));
            }
        });
        mCounterLmeDateDialog.show();
    }


    //计算结果
    private void initComputerValue() {
        RateComputerModel rateComputerModel = new RateComputerModel();
        rateComputerModel.setOptionsType(mMenuCode);
        rateComputerModel.setOptionsDirection(mOptionsDirection);
        if (ValueUtil.isStrEmpty(mContract)) {
            ToastUtil.showToast("请选择合约");
            return;
        }
        rateComputerModel.setContract(mContract);

        if (ValueUtil.isStrEmpty(etPric.getText().toString().trim())) {
            ToastUtil.showToast("选项未完成");
            return;

        } else if (Integer.valueOf(etPric.getText().toString().trim()) % 5 != 0) {
            ToastUtil.showToast("行权价格5倍数正整数");
            return;

        }

        rateComputerModel.setExercisePrice(etPric.getText().toString().trim());

        rateComputerModel.setRate(mRateValue);

        if (ValueUtil.isStrEmpty(tvDate.getText().toString().trim())) {
            ToastUtil.showToast("选项未完成");
            return;
        }
        rateComputerModel.setDueDate(DateUtil.dateToStamp(tvDate.getText().toString().trim(), 2));
        rateComputerModel.setFloatNum(etRelocateValue.getText().toString().trim());
        ComputerValueActivity.launch(getActivity(), tvContranct.getText().toString(), tvNameShibor.getText().toString(),
                "CounterLMEFragment","", rateComputerModel);

    }


}




















