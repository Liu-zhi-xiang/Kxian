package com.gjmetal.app.ui.alphametal.subtraction.add;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.alphametal.AddOrEditMonth;
import com.gjmetal.app.model.alphametal.MonthSubtractionMetal;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description: 跨月基差
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/6/26  14:19
 */
@SuppressLint("ValidFragment")
public class AddBasisSwapFragment extends BaseFragment {
    @BindView(R.id.tvVariety)
    TextView tvVariety;
    @BindView(R.id.ivRight1)
    ImageView ivRight1;
    @BindView(R.id.llSelecterVariety)
    RelativeLayout llSelecterVariety;
    @BindView(R.id.tvContractOne)
    TextView tvContractOne;
    @BindView(R.id.ivRight2)
    ImageView ivRight2;
    @BindView(R.id.llSelecterContractOne)
    RelativeLayout llSelecterContractOne;
    @BindView(R.id.tvContractTwo)
    TextView tvContractTwo;
    @BindView(R.id.ivRight3)
    ImageView ivRight3;
    @BindView(R.id.llSelecterContractTwo)
    RelativeLayout llSelecterContractTwo;
    @BindView(R.id.tvComputeMode)
    TextView tvComputeMode;
    @BindView(R.id.llSelecterComputeMode)
    RelativeLayout llSelecterComputeMode;

    private Context mContext;
    private ArrayList<String> mNameLists;
    private ArrayList<String> mContractOneLists, mContractRootLists;
    private ArrayList<String> mContractTwoLists;
    private List<MonthSubtractionMetal> mMonthVarietyMetals; //品种
    private List<MonthSubtractionMetal> mMonthContractMetals;//合约
    private String mMetalCode;
    private String mMetalExchange ;
    private String mMetalType ;
    private String mMetalName;//品种
    private String mContractOne;//合约1
    private String mContractTwo; //合约2
    private String mNameOne;
    private String mNameTwo;
    private int mSortOne;
    private int mSortTwo;
    private SingleChooseDialog mCounterVarietyDialog;
    private SingleChooseDialog mCounterContractOneDialog;
    private SingleChooseDialog mCounterContractTwoDialog;

    private boolean isPermission;
    @SuppressLint("ValidFragment")
    public AddBasisSwapFragment(boolean isPermission) {
        this.isPermission = isPermission;
    }
    @Override
    protected int setRootView() {
        return R.layout.fragment_add_subtraction_view;
    }


    @Override
    protected void initView() {
        mContext=getContext();
        mNameLists = new ArrayList<>();
        mContractOneLists = new ArrayList<>();
        mContractTwoLists = new ArrayList<>();
        mContractRootLists = new ArrayList<>();

        tvVariety.setText(mMetalName);
        getAddMonthCounterVarietys(false);
    }
    private void getAddMonthCounterVarietys(final boolean showDialog) {
        mNameLists.clear();
        DialogUtil.waitDialog(mContext);
        Api.getAlphaMetalService().getCrossMonthSubtractionMetal()
                .compose(XApi.<BaseModel<List<MonthSubtractionMetal>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<MonthSubtractionMetal>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<MonthSubtractionMetal>>>() {
                    @Override
                    public void onNext(BaseModel<List<MonthSubtractionMetal>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isEmpty(listBaseModel)) return;
                        mMonthVarietyMetals = listBaseModel.getData();
                        if (ValueUtil.isListNotEmpty(mMonthVarietyMetals)) {
                            for (int i = 0; i < mMonthVarietyMetals.size(); i++) {
                                mNameLists.add(mMonthVarietyMetals.get(i).getName());
                            }
                        }
                        if (showDialog) {
                            if (mCounterVarietyDialog == null) {
                                initVarietyDialog();
                            }
                            mCounterVarietyDialog.show();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });

    }

    private void getAddMonthCounterContracts() {
        mContractOneLists.clear();
        mContractTwoLists.clear();
        mContractRootLists.clear();
        Api.getAlphaMetalService().getCrossMonthSubtractionList(mMetalCode,mMetalExchange)
                .compose(XApi.<BaseModel<List<MonthSubtractionMetal>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<MonthSubtractionMetal>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<MonthSubtractionMetal>>>() {
                    @Override
                    public void onNext(BaseModel<List<MonthSubtractionMetal>> listBaseModel) {
                        if (ValueUtil.isEmpty(listBaseModel)) return;
                        mMonthContractMetals = listBaseModel.getData();
                        if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                            for (int i = 0; i < mMonthContractMetals.size(); i++) {
                                mContractOneLists.add(mMonthContractMetals.get(i).getName());
                                mContractTwoLists.add(mMonthContractMetals.get(i).getName());
                                mContractRootLists.add(mMonthContractMetals.get(i).getName());
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }


    @OnClick({R.id.llSelecterVariety,
            R.id.llSelecterContractOne,
            R.id.llSelecterContractTwo,
            R.id.btnUpdateData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSelecterVariety: //品种
                if (ValueUtil.isListEmpty(mNameLists)) {
                    getAddMonthCounterVarietys(true);
                    return;
                }
                if (mCounterVarietyDialog == null) {
                    initVarietyDialog();
                }
                mCounterVarietyDialog.show();
                break;

            case R.id.llSelecterContractOne://合约1
                if (ValueUtil.isListEmpty(mContractOneLists)) {
                    ToastUtil.showToast(mContext.getResources().getString(R.string.select_contenct));
                    return;
                }
                filtrateListTwo();
                if (mCounterContractOneDialog == null) {
                    initContractOneDialog();
                }
                mCounterContractOneDialog.show();
                break;

            case R.id.llSelecterContractTwo://合约2
                if (ValueUtil.isListEmpty(mContractTwoLists)) {
                    ToastUtil.showToast(mContext.getResources().getString(R.string.select_contenct));
                    return;
                }
                filtrateListTwo();
                if (mCounterContractTwoDialog == null) {
                    initContractTwoDialog();
                }
                mCounterContractTwoDialog.show();
                break;

            case R.id.btnUpdateData: //提交
                updateDatas();
                break;
        }
    }

    //品种
    private void initVarietyDialog() {
        mCounterVarietyDialog = new SingleChooseDialog(mContext, R.style.Theme_dialog, mNameLists, tvVariety.getText().toString());
        mCounterVarietyDialog.setCancelable(true);
        Objects.requireNonNull(mCounterVarietyDialog.getWindow()).setWindowAnimations(R.style.dialogWindowAnim);
        mCounterVarietyDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterVarietyDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                if (v.getId() == R.id.tvFinish) {
                    mMetalName = price;
                    tvVariety.setText(price);
                    clearDatas();
                    if (ValueUtil.isListNotEmpty(mMonthVarietyMetals)) {
                        mMetalCode = mMonthVarietyMetals.get(position).getMetalCode();
                        mMetalExchange = mMonthVarietyMetals.get(position).getExchange();
                        mMetalType = "Subtraction";
                        getAddMonthCounterContracts();
                    }
                }
            }

            @Override
            public void onDismiss() {

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onPageStart("alpha_arbitrage_0_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPageStart("alpha_arbitrage_0_time");
    }

    private void clearDatas() {
        tvContractOne.setText("");
        tvContractTwo.setText("");
        tvComputeMode.setText("");
        mNameOne = "";
        mNameTwo = "";
        mContractOne = "";
        mContractTwo = "";
    }

    //合约1
    private void initContractOneDialog() {
        mCounterContractOneDialog = new SingleChooseDialog(mContext,
                R.style.Theme_dialog, mContractOneLists, mContractOne);
        mCounterContractOneDialog.setCancelable(true);
        Objects.requireNonNull(mCounterContractOneDialog.getWindow()).setWindowAnimations(R.style.dialogWindowAnim);
        mCounterContractOneDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterContractOneDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                if (v.getId() == R.id.tvFinish) {
                    if (!mNameOne.equals(price)) {
                        if (selectRootPositiontwo == -1) {
                            selectRootPositionOne = position;
                        } else {
                            if (position < selectRootPositiontwo) {
                                selectRootPositionOne = position;
                            } else {
                                selectRootPositionOne = position + 1;
                            }
                        }
                    }
                    mNameOne = price;
                    tvContractOne.setText(price);

                    if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                        mContractOne = mMonthContractMetals.get(selectRootPositionOne).getContract();
                        mSortOne = mMonthContractMetals.get(selectRootPositionOne).getSort();
                    }
                    if (ValueUtil.isStrNotEmpty(mNameOne) && ValueUtil.isStrNotEmpty(mNameTwo)) {
                        if (mSortOne < mSortTwo) {
                            tvComputeMode.setText(mNameOne + "-" + mNameTwo);
                        } else {
                            tvComputeMode.setText(mNameTwo + "-" + mNameOne);
                        }
                    }
                }
            }

            @Override
            public void onDismiss() {

            }
        });

    }

    //合约2
    private void initContractTwoDialog() {
        mCounterContractTwoDialog = new SingleChooseDialog(mContext,
                R.style.Theme_dialog, mContractTwoLists, mContractTwo);
        mCounterContractTwoDialog.setCancelable(true);
        Objects.requireNonNull(mCounterContractTwoDialog.getWindow()).setWindowAnimations(R.style.dialogWindowAnim);
        mCounterContractTwoDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterContractTwoDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                if (v.getId() == R.id.tvFinish) {
                    if (!mNameTwo.equals(price)) {
                        if (selectRootPositionOne == -1) {
                            selectRootPositiontwo = position;
                        } else {
                            if (position < selectRootPositionOne) {
                                selectRootPositiontwo = position;
                            } else {
                                selectRootPositiontwo = position + 1;
                            }
                        }
                    }
                    mNameTwo = price;
                    tvContractTwo.setText(price);
                    if (ValueUtil.isListNotEmpty(mMonthContractMetals)) {
                        mContractTwo = mMonthContractMetals.get(selectRootPositiontwo).getContract();
                        mSortTwo = mMonthContractMetals.get(selectRootPositiontwo).getSort();
                    }
                    if (ValueUtil.isStrNotEmpty(mNameOne) && ValueUtil.isStrNotEmpty(mNameTwo)) {
                        if (mSortOne < mSortTwo) {
                            tvComputeMode.setText(mNameOne + "-" + mNameTwo);
                        } else {
                            tvComputeMode.setText(mNameTwo + "-" + mNameOne);
                        }
                    }
                }
            }

            @Override
            public void onDismiss() {

            }
        });

    }

    private void updateDatas() {
        if (ValueUtil.isStrEmpty(mMetalName) || ValueUtil.isStrEmpty(mNameOne)
                || ValueUtil.isStrEmpty(mNameTwo) || ValueUtil.isStrEmpty(mContractOne)
                || ValueUtil.isStrEmpty(mContractTwo)) {
            ToastUtil.showToast(mContext.getResources().getString(R.string.select_contenct_no_complete));
            return;
        }

        if (mNameOne.equals(mNameTwo)) {
            ToastUtil.showToast(mContext.getResources().getString(R.string.select_contenct_no_equal));
            return;
        }

        AddOrEditMonth addOrEditMonth = new AddOrEditMonth();
        if (mSortOne < mSortTwo) {
            addOrEditMonth.setLeftContract(mContractOne);
            addOrEditMonth.setLeftName(mNameOne);
            addOrEditMonth.setMetal(mMetalCode);
            addOrEditMonth.setRightContract(mContractTwo);
            addOrEditMonth.setRightName(mNameTwo);

        } else {
            addOrEditMonth.setLeftContract(mContractTwo);
            addOrEditMonth.setLeftName(mNameTwo);
            addOrEditMonth.setMetal(mMetalCode);
            addOrEditMonth.setRightContract(mContractOne);
            addOrEditMonth.setRightName(mNameOne);
        }


        DialogUtil.waitDialog(getActivity());

        RequestBody body = RequestBody.create(GsonUtil.toJson(addOrEditMonth),MediaType.parse("application/json; charset=utf-8"));
        Api.getAlphaMetalService().addCrossMonthSubtractionContract(body)
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
                            LoginActivity.launch(getActivity());
                        } else {
                            assert error != null;
                            ToastUtil.showToast(" " + error.getMessage());
                        }
                    }
                });
    }

    private int selectRootPositionOne = -1, selectRootPositiontwo = -1;
    //二级筛选
    private void filtrateListTwo() {
        mContractOneLists.clear();
        mContractTwoLists.clear();
        mContractOneLists.addAll(mContractRootLists);
        mContractTwoLists.addAll(mContractRootLists);
        if (selectRootPositionOne != -1) {
            mContractTwoLists.remove(selectRootPositionOne);
        }
        if (selectRootPositiontwo != -1) {
            mContractOneLists.remove(selectRootPositiontwo);
        }
    }
}
