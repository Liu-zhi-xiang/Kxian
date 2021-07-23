package com.gjmetal.app.ui.alphametal.subtraction.add;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.alphametal.AddVarietyMeasure;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.alphametal.MonthSubtractionMetal;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.ContainsEmojiUtil;
import com.gjmetal.app.util.DecimalDigitsInputFilter;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.CounterContractDialog;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description:跨品种测算
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/6/26  14:21
 */
@SuppressLint("ValidFragment")
public class AddVarietyMeasureFragment extends DelayerFragment {
    @BindView(R.id.tvVarietyOne)
    TextView tvVarietyOne;
    @BindView(R.id.ivVariety1)
    ImageView ivVariety1;
    @BindView(R.id.tvVarietyContractOne)
    TextView tvVarietyContractOne;
    @BindView(R.id.ivVarietytwo)
    ImageView ivVarietytwo;
    @BindView(R.id.tvComputeModeOne)
    EditText tvComputeModeOne;
    @BindView(R.id.llSelecterComputeModeOne)
    LinearLayout llSelecterComputeModeOne;
    @BindView(R.id.tvVarietyTwo)
    TextView tvVarietyTwo;
    @BindView(R.id.ivVarietyOne)
    ImageView ivVarietyOne;
    @BindView(R.id.tvVarietyContractTwo)
    TextView tvVarietyContractTwo;
    @BindView(R.id.ivVarietyContracttwo)
    ImageView ivVarietyContracttwo;
    @BindView(R.id.tvComputeModeTwo)
    EditText tvComputeModeTwo;
    @BindView(R.id.llSelecterComputeModeTwo)
    LinearLayout llSelecterComputeModeTwo;
    @BindView(R.id.rbifference)
    RadioButton rbifference;
    @BindView(R.id.rbSpecific)
    RadioButton rbSpecific;
    @BindView(R.id.tvVarietyComputeName)
    EditText tvVarietyComputeName;
    @BindView(R.id.bottomView)
    View bottomView;
    @BindView(R.id.btnUpdateData)
    Button btnUpdateData;
    @BindView(R.id.flCounter)
    RelativeLayout flCounter;
    @BindView(R.id.rgCalculate)
    RadioGroup rgCalculate;

    private int cursorPos; //输入表情前的光标位置
    private String inputAfterText; //输入表情前EditText中的文本
    private boolean resetText;//是否重置了EditText的内容
    private CounterContractDialog mCounterContractDialog;
    private CounterContractDialog mCounterContractDialogTwo;
    private SingleChooseDialog mCounterContractOneDialog;
    private SingleChooseDialog mCounterContractOneDialogTwo;
    private List<MonthSubtractionMetal> mMonthContractMetals;//合约1
    private List<MonthSubtractionMetal> mMonthContractMetalsTwo;//合约2
    private ArrayList<CrossMetalModel> mNameLists;//源品种
    private ArrayList<CrossMetalModel> mNameListsTwo;//品种2
    private ArrayList<String> mContractOneLists;
    private ArrayList<String> mContractTwoLists;
    private int mVerirtyOneMainPosition = 0;
    private int mVerirtyTwoMainPosition = 0;
    private int mVerirtyOneChildPosition = 0;
    private int mVerirtyTwoChildPosition = 0;
    private String mMetalCode, mMetalCodeTwo;
    private String mMetalExchange, mMetalExchangeTwo;
    private static final String mMetalType = "CrossMetal";
    private static final String mExpressionSubtract = "Subtract";
    private static final String mExpressionDivide = "Divide";
    private String mMetalNameOne;//品种1
    private String mMetalNameTwo;//品种2
    private String mContractOne;//合约1
    private String mContractTwo; //合约2
    private int mContractPositionOne = -1;
    private int mContractPositionTwo = -1;
    private String mComputeModeOneStr = "1";
    private String mComputeModeTwoStr = "1";
    private String mContractName;
    private boolean isPermission;

    @SuppressLint("ValidFragment")
    public AddVarietyMeasureFragment(boolean isPermission) {
        this.isPermission = isPermission;
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_variety_measure_view;
    }

    @Override
    protected void initView() {
        mNameLists = new ArrayList<>();
        mNameListsTwo = new ArrayList<>();
        mContractOneLists = new ArrayList<>();
        mContractTwoLists = new ArrayList<>();
        getAddMonthCounterVarietys(false);
        tvComputeModeOne.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6)});
        tvComputeModeOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mComputeModeOneStr = s.toString();
            }
        });

        tvComputeModeTwo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6)});
        tvComputeModeTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mComputeModeTwoStr = s.toString();
            }
        });
        tvVarietyComputeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!resetText) {
                    cursorPos = tvVarietyComputeName.getSelectionEnd();
                    inputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    if (count >= 2) {//表情符号的字符长度最小为2
                        CharSequence input = s.subSequence(cursorPos, cursorPos + count);
                        if (ContainsEmojiUtil.isEmojiTwo(input.toString())) {
                            resetText = true;
                            ToastUtil.showToast(R.string.contains_emoji);
                            //是表情符号就将文本还原为输入表情符号之前的内容
                            tvVarietyComputeName.setText(inputAfterText);
                            CharSequence text = tvVarietyComputeName.getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mContractName = s.toString();
            }
        });
        rgCalculate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setVarietyName();
            }
        });
    }

    private int varietytype = 1;

    @OnClick({R.id.tvVarietyTwo, R.id.tvVarietyOne, R.id.tvVarietyContractOne, R.id.tvVarietyContractTwo, R.id.btnUpdateData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvVarietyOne:    //选择品种1
                varietytype = 1;
                if (ValueUtil.isListNotEmpty(mNameLists)) {
                    if (mCounterContractDialog == null) {
                        initContractDialog(mVerirtyOneMainPosition, mVerirtyOneChildPosition);
                    }
                    mCounterContractDialog.show();
                } else {
                    getAddMonthCounterVarietys(true);
                }
                break;
            case R.id.tvVarietyTwo:     //选择品种2
                varietytype = 2;
                if (ValueUtil.isListNotEmpty(mNameLists)) {
                    if (mCounterContractDialogTwo == null) {
                        initContractDialogTwo(mVerirtyTwoMainPosition, mVerirtyTwoChildPosition);
                    }
                    mCounterContractDialogTwo.show();
                } else {
                    getAddMonthCounterVarietys(true);
                }
                break;
            case R.id.tvVarietyContractOne:  // 合约1

                if (ValueUtil.isStrEmpty(mMetalNameOne)) {
                    ToastUtil.showToast(getContext().getResources().getString(R.string.select_contenct));
                    return;
                }
                if (ValueUtil.isListEmpty(mContractOneLists)) {
                    ToastUtil.showToast(getContext().getResources().getString(R.string.empty));

                    return;
                }
                if (mContractPositionTwo != -1 && isVarietySame()) {
                    filtrateListTwo(1);
                }
                if (mCounterContractOneDialog == null) {
                    initContractOneDialog();
                }
                mCounterContractOneDialog.show();
                break;
            case R.id.tvVarietyContractTwo: // 合约2

                if (ValueUtil.isStrEmpty(mMetalNameTwo)) {
                    ToastUtil.showToast(getContext().getResources().getString(R.string.select_contenct));
                    return;
                }
                if (ValueUtil.isListEmpty(mContractTwoLists)) {
                    ToastUtil.showToast(getContext().getResources().getString(R.string.empty));
                    return;
                }
                if (mContractPositionOne != -1 && isVarietySame()) {
                    filtrateListTwo(2);
                }
                if (mCounterContractOneDialogTwo == null) {
                    initContractOneDialogTwo();
                }
                mCounterContractOneDialogTwo.show();

                break;
            case R.id.btnUpdateData:

                updateDatas();
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("alpha_arbitrage_1_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageStart("alpha_arbitrage_1_time");
    }

    //品种1
    private void getAddMonthCounterVarietys(final boolean showDialog) {
        mNameListsTwo.clear();
        DialogUtil.waitDialog(getContext());
        Api.getAlphaMetalService().getCrossMetal()
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<CrossMetalModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<CrossMetalModel>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isEmpty(listBaseModel)) return;
                        if (mNameLists != null) {
                            mNameLists.clear();
                        }
                        mNameLists.addAll(listBaseModel.getData());
                        mNameListsTwo.addAll(listBaseModel.getData());
                        if (showDialog) {
                            if (varietytype == 1) {
                                if (mCounterContractDialog == null) {
                                    initContractDialog(mVerirtyOneMainPosition, mVerirtyOneChildPosition);
                                }
                                mCounterContractDialog.show();
                            } else {
                                if (mCounterContractDialogTwo == null) {
                                    initContractDialogTwo(mVerirtyTwoMainPosition, mVerirtyTwoChildPosition);
                                }
                                mCounterContractDialogTwo.show();
                            }

                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });

    }

    /**
     * 合约1
     *
     * @param mMetalCode
     * @param mMetalExchange
     */
    private void getAddMonthCounterContracts(String mMetalCode, String mMetalExchange) {
        if (varietytype == 1) {
            mContractOneLists.clear();
        } else {
            mContractTwoLists.clear();
        }
        Api.getAlphaMetalService().getCrossMonthSubtractionList(mMetalCode,mMetalExchange)
                .compose(XApi.<BaseModel<List<MonthSubtractionMetal>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<MonthSubtractionMetal>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<MonthSubtractionMetal>>>() {
                    @Override
                    public void onNext(BaseModel<List<MonthSubtractionMetal>> listBaseModel) {
                        if (ValueUtil.isEmpty(listBaseModel)) return;
                        if (varietytype == 1) {
                            mMonthContractMetals = listBaseModel.getData();
                            if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                                for (int i = 0; i < mMonthContractMetals.size(); i++) {
                                    mContractOneLists.add(mMonthContractMetals.get(i).getName());
                                }
                            }
                            //回归合约2,（前面过滤掉的合约）
                            if (ValueUtil.isListNotEmpty(mMonthContractMetalsTwo)) {
                                if ( mContractTwoLists!=null){
                                    mContractTwoLists.clear();
                                }
                                for (int i = 0; i < mMonthContractMetalsTwo.size(); i++) {
                                    mContractTwoLists.add(mMonthContractMetalsTwo.get(i).getName());
                                }
                            }

                        } else if (varietytype == 2) {
                            mMonthContractMetalsTwo = listBaseModel.getData();
                            if (ValueUtil.isListNotEmpty(mMonthContractMetalsTwo)) {
                                for (int i = 0; i < mMonthContractMetalsTwo.size(); i++) {
                                    mContractTwoLists.add(mMonthContractMetalsTwo.get(i).getName());
                                }
                            }
                            //回归合约1,（前面过滤掉的合约）
                            if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                                if ( mContractOneLists!=null){
                                    mContractOneLists.clear();
                                }
                                for (int i = 0; i < mMonthContractMetals.size(); i++) {
                                    mContractOneLists.add(mMonthContractMetals.get(i).getName());
                                }
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }


    private int selectOne = -1, selectTwo = -1;

    //选择品种1
    private void initContractDialog(int mainPosition, int childPosition) {
        if (mNameListsTwo.size() > mainPosition && mNameListsTwo.get(mainPosition).getDataList().size() > childPosition) {

            mCounterContractDialog = new CounterContractDialog(getContext(),
                    R.style.Theme_dialog, mNameListsTwo, mainPosition, childPosition, 2);
            mCounterContractDialog.setCancelable(true);
            mCounterContractDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
            mCounterContractDialog.getWindow().setGravity(Gravity.BOTTOM);
            mCounterContractDialog.setOnDialogClickListener(new CounterContractDialog.OnDialogClickListener() {

                @Override
                public void dialogClick(Dialog dialog, View v, String name, String price, int mainPosition, int childPosition) {
                    if (ValueUtil.isListEmpty(mNameListsTwo)) {
                        return;
                    }

                    selectOne = 0;

                    mVerirtyOneMainPosition = mainPosition;
                    mVerirtyOneChildPosition = childPosition;

                    mMetalNameOne = price;
                    tvVarietyOne.setText(price);
                    mContractOne = "";
                    tvVarietyContractOne.setText("");
                    mContractPositionOne = -1;
                    mMetalCode = mNameListsTwo.get(mainPosition).getDataList().get(childPosition).getMetalCode();
                    mMetalExchange = mNameListsTwo.get(mainPosition).getDataList().get(childPosition).getExchange();
                    if (selectTwo != -1 && isVarietySame()) {
                        filtrateListOne();
                    } else {
                        getAddMonthCounterContracts(mMetalCode, mMetalExchange);
                    }
                    setVarietyName();
                }
            });

        }
    }


    //选择品种2
    private void initContractDialogTwo(int mainPosition, int childPosition) {

        if (mNameListsTwo.size() > mainPosition && mNameListsTwo.get(mainPosition).getDataList().size() > childPosition) {

            mCounterContractDialogTwo = new CounterContractDialog(getContext(),
                    R.style.Theme_dialog, mNameListsTwo, mainPosition, childPosition, 2);
            mCounterContractDialogTwo.setCancelable(true);
            mCounterContractDialogTwo.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
            mCounterContractDialogTwo.getWindow().setGravity(Gravity.BOTTOM);
            mCounterContractDialogTwo.setOnDialogClickListener(new CounterContractDialog.OnDialogClickListener() {
                @Override
                public void dialogClick(Dialog dialog, View v, String name, String price, int mainPosition, int childPosition) {
                    if (ValueUtil.isListEmpty(mNameListsTwo)) {
                        return;
                    }
                    selectTwo = 0;
                    mVerirtyTwoMainPosition = mainPosition;
                    mVerirtyTwoChildPosition = childPosition;
                    mMetalNameTwo = price;
                    tvVarietyTwo.setText(price);
                    mContractTwo = "";
                    tvVarietyContractTwo.setText("");
                    mContractPositionTwo = -1;
                    mMetalCodeTwo = mNameListsTwo.get(mainPosition).getDataList().get(childPosition).getMetalCode();
                    mMetalExchangeTwo = mNameListsTwo.get(mainPosition).getDataList().get(childPosition).getExchange();
                    if (selectOne != -1 && isVarietySame()) {
                        filtrateListOne();
                    } else {
                        getAddMonthCounterContracts(mMetalCodeTwo, mMetalExchangeTwo);
                    }
                    setVarietyName();
                }
            });

        }
    }

    //选择合约1
    private void initContractOneDialog() {
        mCounterContractOneDialog = new SingleChooseDialog(getContext(),
                R.style.Theme_dialog, mContractOneLists, mContractOne);
        mCounterContractOneDialog.setCancelable(true);
        mCounterContractOneDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mCounterContractOneDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterContractOneDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        //对比合约2记录合约1的position
                        if (!TextUtils.isEmpty(mContractOne)
                                && !mContractOne.equals(price)
                                && isVarietySame()) {
                            if (mContractPositionTwo == -1) {
                                mContractPositionOne = position;
                            } else {
                                if (position < mContractPositionTwo) {
                                    mContractPositionOne = position;
                                } else {
                                    mContractPositionOne = position + 1;
                                }
                            }
                        }else {
                            mContractPositionOne = position;
                        }

                        mContractOne = price;
                        tvVarietyContractOne.setText(price);
                        setVarietyName();
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
    }

    //选择合约2
    private void initContractOneDialogTwo() {
        mCounterContractOneDialogTwo = new SingleChooseDialog(getContext(),
                R.style.Theme_dialog, mContractTwoLists, mContractTwo);
        mCounterContractOneDialogTwo.setCancelable(true);
        mCounterContractOneDialogTwo.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mCounterContractOneDialogTwo.getWindow().setGravity(Gravity.BOTTOM);
        mCounterContractOneDialogTwo.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        //对比合约1记录合约2的position
                        if (!TextUtils.isEmpty(mContractOne)
                                && !mContractTwo.equals(price)
                                && isVarietySame()) {
                            if (mContractPositionOne == -1) {
                                mContractPositionTwo = position;
                            } else {
                                if (position < mContractPositionOne) {
                                    mContractPositionTwo = position;
                                } else {
                                    mContractPositionTwo = position + 1;
                                }
                            }
                        }else {
                            mContractPositionTwo = position;
                        }

                        mContractTwo = price;
                        tvVarietyContractTwo.setText(price);
                        setVarietyName();
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
    }

    private boolean isVarietySame() {
        return (mVerirtyTwoMainPosition == mVerirtyOneMainPosition) && (mVerirtyTwoChildPosition == mVerirtyOneChildPosition);
    }

    //二级筛选
    private void filtrateListTwo(int type) {
        mContractOneLists.clear();
        mContractTwoLists.clear();
        if (type == 1) {
            mMonthContractMetals = mMonthContractMetalsTwo;
            if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                for (int i = 0; i < mMonthContractMetals.size(); i++) {
                    mContractOneLists.add(mMonthContractMetals.get(i).getName());
                    mContractTwoLists.add(mMonthContractMetals.get(i).getName());
                }
            }
        } else {
            mMonthContractMetalsTwo = mMonthContractMetals;
            if (ValueUtil.isListNotEmpty(mMonthContractMetalsTwo)) {
                for (int i = 0; i < mMonthContractMetalsTwo.size(); i++) {
                    mContractOneLists.add(mMonthContractMetalsTwo.get(i).getName());
                    mContractTwoLists.add(mMonthContractMetalsTwo.get(i).getName());
                }
            }
        }
        if (mContractPositionOne != -1) {
            mContractTwoLists.remove(mContractPositionOne);
        }
        if (mContractPositionTwo != -1) {
            mContractOneLists.remove(mContractPositionTwo);
        }
    }
    //二级筛选
    private void filtrateListOne() {
        if (varietytype == 1) {
            mMonthContractMetals = mMonthContractMetalsTwo;
            if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                mContractOneLists.clear();
                for (int i = 0; i < mMonthContractMetals.size(); i++) {
                    mContractOneLists.add(mMonthContractMetals.get(i).getName());
                }
            }
        } else {
            mMonthContractMetalsTwo = mMonthContractMetals;
            if (ValueUtil.isListNotEmpty(mMonthContractMetalsTwo)) {
                mContractTwoLists.clear();
                for (int i = 0; i < mMonthContractMetalsTwo.size(); i++) {
                    mContractTwoLists.add(mMonthContractMetalsTwo.get(i).getName());
                }
            }
        }
    }
    //设置名称
    private void setVarietyName() {
        if (TextUtils.isEmpty(mContractOne) || TextUtils.isEmpty(mContractTwo)) {
            tvVarietyComputeName.setText("");
        } else {
            if (rbifference.isChecked()) {
                tvVarietyComputeName.setText(mContractOne + "-" + mContractTwo);
            } else {
                tvVarietyComputeName.setText(mContractOne + "/" + mContractTwo);
            }
        }
    }


    private void updateDatas() {
        AddVarietyMeasure addOrEditMonth = new AddVarietyMeasure();
        if (TextUtils.isEmpty(mMetalNameOne)
                || TextUtils.isEmpty(mMetalNameTwo)
                || TextUtils.isEmpty(mContractOne)
                || TextUtils.isEmpty(mContractTwo)) {
            ToastUtil.showToast(getString(R.string.select_contenct_no_complete));
            return;
        }
        if (TextUtils.isEmpty(mComputeModeOneStr) && TextUtils.isEmpty(mComputeModeTwoStr)) {
            mComputeModeOneStr = "1";
            mComputeModeTwoStr = "1";
        }
        if (isVarietySame()&&mContractPositionTwo==mContractPositionOne){
            ToastUtil.showToast(getString(R.string.heyue_not_equal));
            return;
        }
        char one = mComputeModeOneStr.charAt(0);
        char two = mComputeModeTwoStr.charAt(0);
        if (one == '.' || two == '.') {
            ToastUtil.showToast(getString(R.string.decimal_point_digit));
            return;
        }
        if (TextUtils.isEmpty(mContractName)) {
            setVarietyName();
        }


        DialogUtil.loadDialog(getContext());
        addOrEditMonth.setLeftExchange(mNameListsTwo.get(mVerirtyOneMainPosition).getDataList().get(mVerirtyOneChildPosition).getExchange());
        addOrEditMonth.setLeftMetal(mNameListsTwo.get(mVerirtyOneMainPosition).getDataList().get(mVerirtyOneChildPosition).getMetalCode());
        addOrEditMonth.setLeftContract(mMonthContractMetals.get(mContractPositionOne).getContract());
        addOrEditMonth.setLeftName(mMonthContractMetals.get(mContractPositionOne).getName());


        addOrEditMonth.setRightExchange(mNameListsTwo.get(mVerirtyTwoMainPosition).getDataList().get(mVerirtyTwoChildPosition).getExchange());
        addOrEditMonth.setRightMetal(mNameListsTwo.get(mVerirtyTwoMainPosition).getDataList().get(mVerirtyTwoChildPosition).getMetalCode());
        addOrEditMonth.setRightContract(mMonthContractMetalsTwo.get(mContractPositionTwo).getContract());
        addOrEditMonth.setRightName(mMonthContractMetalsTwo.get(mContractPositionTwo).getName());

        XLog.e("cccccccccccc","getContract one==="+mMonthContractMetals.get(mContractPositionOne).getContract()
                +"=====getContract two==="+mMonthContractMetalsTwo.get(mContractPositionTwo).getContract());
        addOrEditMonth.setLeftCfc(mComputeModeOneStr);
        addOrEditMonth.setRightCfc(mComputeModeTwoStr);
        if (rbifference.isChecked()) {
            addOrEditMonth.setExpressionType(mExpressionSubtract);
        } else {
            addOrEditMonth.setExpressionType(mExpressionDivide);
        }
        addOrEditMonth.setType(mMetalType);

        addOrEditMonth.setName(mContractName);
//        String d = GsonUtil.toJson(addOrEditMonth);
        RequestBody body = RequestBody.create(GsonUtil.toJson(addOrEditMonth),MediaType.parse("application/json; charset=utf-8")
                );
        Api.getAlphaMetalService().addCrossMetal(body)
                .compose(XApi.<BaseModel<Object>>getApiTransformer())
                .compose(XApi.<BaseModel<Object>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Object>>() {
                    @Override
                    public void onNext(BaseModel<Object> listBaseModel) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(listBaseModel.getMessage());
                        App.finishSingActivity(getActivity());
                        GjUtil.refershMeMonth();
                        GjUtil.setBackMonth();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            ToastUtil.showToast(getString(R.string.txt_login_time_out));
                            LoginActivity.launch(getActivity());
                        } else {
                            ToastUtil.showToast(" " + error.getMessage());
                        }
                    }
                });
    }
}
