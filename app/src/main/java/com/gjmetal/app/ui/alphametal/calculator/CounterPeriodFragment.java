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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.alphametal.ExervisePriceBean;
import com.gjmetal.app.model.alphametal.QuotationsModel;
import com.gjmetal.app.model.alphametal.Rate;
import com.gjmetal.app.model.alphametal.RateComputerModel;
import com.gjmetal.app.model.alphametal.RateModel;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.my.ApplyForReadWebActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.dialog.CounterContractDialog;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.app.widget.popuWindow.KeyBoardPopWindow;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description 上期所合约
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-13 17:24
 */

public class CounterPeriodFragment extends DelayerFragment {
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
    @BindView(R.id.tvPric)
    TextView tvPric;
    @BindView(R.id.rlSelecterPrice)
    RelativeLayout rlSelecterPrice;
    @BindView(R.id.tvValueShibor)
    TextView tvValueShibor;
    @BindView(R.id.etRelocateValue)
    EditText etRelocateValue;
    @BindView(R.id.tvRelocateBp)
    TextView tvRelocateBp;
    @BindView(R.id.btnComputerValue)
    Button btnComputerValue;
    @BindView(R.id.flCounter)
    RelativeLayout flCounter;
    @BindView(R.id.svCounter)
    NestedScrollView svCounter;
    @BindView(R.id.bottomView)
    View bottomView;
    @BindView(R.id.tvNameShibor)
    TextView tvNameShibor;
    @BindView(R.id.myLinearLayoutView)
    LinearLayout myLinearLayoutView;
    @BindView(R.id.ivRight1)
    ImageView ivRight1;
    @BindView(R.id.ivRight2)
    ImageView ivRight2;

    private List<String> mPriceDatas;//开始价格数组
    private List<QuotationsModel> mListQuotationItems;//合约
    private int mTouchSlop;
    // 手机按下时的屏幕坐标
    private float mXDown;
    private float mYDown;
    //手机当时所处的屏幕坐标
    private float mXMove;
    private float mYMove;
    private int mQuotationsMainPosition = 0;
    private int mQuotationsChildPosition = 0;
    private KeyBoardPopWindow mKeyBoardPopWindow;
    private CounterContractDialog mCounterContractDialog;
    private SingleChooseDialog mCounterPriceDialog;
    private int mKeyBoardHeight = 300;
    private String mOptionsDirection = "0"; //方向 0:看涨 1:看跌
    private String mMenuCode="0"; //期权类型   0:上期所  1.LME
    private String mContract; // 合约Code
    private Long mDueDate;// LME到期时间
    private String mRateValue; //利率价格
    private String mPrice; //行权价格
    private String mPriceTime;//行权价时间
    @BindView(R.id.vPermission)
    ApplyReadView applyReadView;
    private String function=Constant.ApplyReadFunction.ZH_APP_AM_SHFE_MONITOR;
    public CounterPeriodFragment() {

    }

    @SuppressLint("ValidFragment")
    public CounterPeriodFragment(String menuCode) {
        this.mMenuCode = menuCode;
    }


    @Override
    protected int setRootView() {
        return R.layout.fragment_counter_period;
    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        initData();
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    private void initData() {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        //自定义键盘光标可以自由移动 适用系统版本为android3.0以上

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(etRelocateValue, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        svCounter.fullScroll(ScrollView.FOCUS_DOWN);
        mListQuotationItems = new ArrayList<>();
        mPriceDatas = new ArrayList<>();
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
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            initRedioGroup();
            setKeyBoardPopWindow();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getQuotationsData(); //获取合约数据
                    getRateData(); //Shibor(¥,O/N)
                }
            }).start();
        }
    }

    /**
     * 合约
     */
    private void getQuotationsData() {
        Api.getAlphaMetalService().getOptionsQuotations(mMenuCode)
                .compose(XApi.<BaseModel<List<QuotationsModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<QuotationsModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<QuotationsModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<QuotationsModel>> listBaseModel) {
                        mListQuotationItems.clear();
                        if (ValueUtil.isEmpty(listBaseModel)) {
                            return;
                        }
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            mListQuotationItems.addAll(listBaseModel.getData());
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error == null) {
                            return;
                        }
                        mListQuotationItems.clear();
                    }
                });

    }

    /**
     * 行权价格
     */
    private void getExervisePrice() {
        Rate rate = new Rate();
        rate.setContract(mContract);
        rate.setDueDate(mDueDate);
        rate.setOptionsType(mMenuCode);
        rate.setOptionsDirection(mOptionsDirection);
        RequestBody body = RequestBody.create(GsonUtil.toJson(rate),MediaType.parse("application/json; charset=utf-8")
                );
        Api.getAlphaMetalService().getExervisePriceTwo(body)
                .compose(XApi.<BaseModel<ArrayList<ExervisePriceBean>>>getApiTransformer())
                .compose(XApi.<BaseModel<ArrayList<ExervisePriceBean>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<ArrayList<ExervisePriceBean>>>() {
                    @Override
                    public void onNext(BaseModel<ArrayList<ExervisePriceBean>> listBaseModel) {
                        mPriceDatas.clear();
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            return;
                        }
                        List<ExervisePriceBean> list = listBaseModel.getData();
                        mPriceTime = list.get(0).getUpdateDate();
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            for (int x = 0; x < list.size(); x++) {
                                mPriceDatas.add(list.get(x).getPrice());
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        mPriceDatas.clear();
                        ToastUtil.showToast(error.getMessage());
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
                        if (tvNameShibor == null) return;
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


    private void initRedioGroup() {
        counterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbUp:
                        getQuotationsData();
                        mOptionsDirection = "0";
                        break;

                    case R.id.rbDown:
                        getQuotationsData();
                        mOptionsDirection = "1";
                        break;
                    default:
                        break;
                }
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
                            //点击按钮显示键盘
                            mKeyBoardPopWindow.showAtLocation(flCounter, Gravity.BOTTOM, 0, 0);
                            GjUtil.openKeyBoard();
                            mKeyBoardHeight = mKeyBoardPopWindow.getKeyView().getMeasuredHeight();
                            bottomView.setMinimumHeight(mKeyBoardHeight);
                            bottomView.setVisibility(View.VISIBLE);

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


    @OnClick({R.id.llSelecterContract, R.id.rlSelecterPrice, R.id.btnComputerValue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSelecterContract:     //选择合约
                if (ValueUtil.isListNotEmpty(mListQuotationItems)) {
                    if (mCounterContractDialog == null) {
                        initContractDialog();
                    }
                    mCounterContractDialog.show();
                } else {
                    ToastUtil.showToast(getResources().getString(R.string.no_getdata));
                }
                break;
            case R.id.rlSelecterPrice:    //行权价格
                if (ValueUtil.isListNotEmpty(mPriceDatas)) {
                    if (mCounterPriceDialog == null) {
                        initPriceDialog();
                    }
                    mCounterPriceDialog.show();
                } else {
                    ToastUtil.showToast(getResources().getString(R.string.selecter_price_text));
                }
                break;

            case R.id.btnComputerValue:  //计算结果
                AppAnalytics.getInstance().onEvent(getContext(),"alpha_optioncalculators_calculate");
                initComputerValue();

                break;


        }
    }

    /**
     * 选择合约
     */
    private void initContractDialog() {
        mCounterContractDialog = new CounterContractDialog(getContext(),
                R.style.Theme_dialog, mListQuotationItems, mQuotationsMainPosition, mQuotationsChildPosition);
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
                        mDueDate = mListQuotationItems.get(mQuotationsMainPosition).getNameList()
                                .get(mQuotationsChildPosition).getEndDate();
                    }
                }

                new Thread(() ->{
                        getExervisePrice(); //获取行权价格
                    }
                ).start();

            }
        });
    }

    /**
     * 行权价格
     */
    private void initPriceDialog() {
        mCounterPriceDialog = new SingleChooseDialog(getContext(), R.style.Theme_dialog, mPriceDatas, tvPric.getText().toString());
        mCounterPriceDialog.setCancelable(true);
        mCounterPriceDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mCounterPriceDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterPriceDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        mPrice = price;
                        tvPric.setText(mPrice);
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
    }


    /**
     * 计算结果
     */
    private void initComputerValue() {
        RateComputerModel rateComputerModel = new RateComputerModel();
        rateComputerModel.setOptionsType(mMenuCode);
        rateComputerModel.setOptionsDirection(mOptionsDirection);
        if (ValueUtil.isStrEmpty(mContract)) {
            ToastUtil.showToast(getString(R.string.option_not_completed));
            return;
        }
        rateComputerModel.setContract(mContract);

        if (ValueUtil.isStrEmpty(mPrice)) {
            ToastUtil.showToast(getString(R.string.option_not_completed));
            return;
        }
        rateComputerModel.setExercisePrice(mPrice);
        rateComputerModel.setRate(mRateValue);
        rateComputerModel.setFloatNum(etRelocateValue.getText().toString().trim());

        ComputerValueActivity.launch(getActivity(), tvContranct.getText().toString(), tvNameShibor.getText().toString(),
                "CounterPeriodFragment", mPriceTime, rateComputerModel);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(ApplyEvent applyEvent) {
       ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
           @Override
           public void onSubscibeDialogCancel() {
               if (applyReadView != null)
                   applyReadView.showPassDueApply(getActivity(),applyReadView, R.color.cD4975C, R.color.cffffff, new BaseCallBack() {
                       @Override
                       public void back(Object obj) {
                           ApplyForReadWebActivity.launch(getActivity(), function, "2");
                       }
                   }, svCounter);
           }

           @Override
           public void onSubscibeDialogShow() {
               if (applyReadView != null)
                   applyReadView.showApply(getActivity(),R.color.cD4975C, R.color.cffffff, applyReadView, new BaseCallBack() {
                       @Override
                       public void back(Object obj) {
                           ApplyForReadWebActivity.launch(getActivity(), function, "1");
                       }
                   }, svCounter);
           }

           @Override
           public void onSubscibeYesShow() {
               if (svCounter != null) {
                   svCounter.setVisibility(View.VISIBLE);
               }
               if (applyReadView != null) {
                   applyReadView.setVisibility(View.GONE);
               }
           }

           @Override
           public void onSubscibeError(NetError error) {

           }

           @Override
           public void onUnknown() {

           }
       });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getBus().unregister(this);
    }
}
