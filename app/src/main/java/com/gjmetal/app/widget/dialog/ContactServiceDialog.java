package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.my.ApplyforModel;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.star.kit.KnifeKit;

import java.util.List;

import butterknife.BindView;

/**
 * 申请订阅  | | 订阅成功
 *
 * @author liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/23  17:13
 */
public class ContactServiceDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.tvVipServiceone)
    TextView tvVipServiceone;
    @BindView(R.id.tvVipServicetwo)
    TextView tvVipServicetwo;
    @BindView(R.id.tvVipServicePhoneOne)
    TextView tvVipServicePhoneOne;
    @BindView(R.id.tvVipServicePhonetwo)
    TextView tvVipServicePhonetwo;
    @BindView(R.id.btnDialogCancel)
    Button btnDialogCancel;
    @BindView(R.id.vLIne)
    View vLIne;
    @BindView(R.id.btnDialogConfirm)
    Button btnDialogConfirm;
    @BindView(R.id.llHint)
    LinearLayout llHint;
    @BindView(R.id.tvVipServicethree)
    TextView tvVipServicethree;
    private DialogCallBack returnCallback;
    private String stringone, stringtwo, understandStr, confirm, phoneOne, phoneTwo, but1, trialTimeStr, expiredOneStr, expiredTwoStr, readStr;
    private Context context;
    private int TYPE = 1;

    public ContactServiceDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        TYPE = 1;
    }

    public void setBtnDialogCancel(DialogCallBack returnCallback) {
        this.returnCallback = returnCallback;
    }

    public ContactServiceDialog(Context context, String str1, String but, DialogCallBack returnCallback, int type) {
        super(context, R.style.dialog);
        this.context = context;
        this.returnCallback = returnCallback;
        readStr = str1;
        understandStr = but;
        TYPE = 3;
    }
    public ContactServiceDialog(Context context, String str1, DialogCallBack returnCallback, int type) {
        super(context, R.style.dialog);
        this.context = context;
        this.returnCallback = returnCallback;
        readStr = str1;
        TYPE = 3;
    }
    /**
     * 样式二(一个but)
     * 试阅已发送
     *
     * @param context
     * @param type
     * @param returnCallback
     */
    public ContactServiceDialog(Context context, int type, DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.context = context;
        this.returnCallback = returnCallback;
        TYPE = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_apply_subscribe);
        KnifeKit.bind(this);
        initView();
    }

    private void setBtnText(String cancel, String Confirm) {
        if (!TextUtils.isEmpty(cancel))
            btnDialogCancel.setText(cancel);
        if (!TextUtils.isEmpty(Confirm))
            btnDialogConfirm.setText(Confirm);
    }

    private void initView() {
        initStr();
        switch (TYPE) {
            case 1://(默认无权限提醒订阅)
                if (!TextUtils.isEmpty(trialTimeStr)) {
                    tvVipServicethree.setVisibility(View.VISIBLE);
                }
                btnDialogCancel.setVisibility(View.VISIBLE);
                btnDialogCancel.setEnabled(true);
                vLIne.setVisibility(View.VISIBLE);
                btnDialogCancel.setText(R.string.txt_cancel);
                btnDialogConfirm.setText(getContext().getString(R.string.try_to_apply_for_read));
                break;
            case 2://试阅已发送
                tvVipServiceone.setText( getContext().getString(R.string.dialog_apply_text_five));
                tvVipServicetwo.setText( getContext().getString(R.string.dialog_apply_text_six));
                tvVipServicePhoneOne.setVisibility(View.GONE);
                tvVipServicePhonetwo.setVisibility(View.GONE);
                btnDialogCancel.setVisibility(View.GONE);
                vLIne.setVisibility(View.GONE);
                btnDialogConfirm.setText(getContext().getString(R.string.understand));
                btnDialogCancel.setEnabled(false);
                break;
            case 3://一句话提醒
                tvVipServiceone.setText(readStr);
                tvVipServicePhoneOne.setVisibility(View.GONE);
                tvVipServicePhonetwo.setVisibility(View.GONE);
                tvVipServicetwo.setVisibility(View.GONE);
                btnDialogCancel.setVisibility(View.GONE);
                vLIne.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(understandStr))
                    btnDialogConfirm.setText(understandStr);
                btnDialogCancel.setEnabled(false);
                break;
            case 4://付费内容不能订阅提醒
//                tvVipServiceone.setText( getContext().getString(R.string.dialog_apply_text_three));
//                tvVipServicetwo.setText( getContext().getString(R.string.dialog_apply_text_four));
//                btnDialogCancel.setVisibility(View.VISIBLE);
//                vLIne.setVisibility(View.VISIBLE);
//                btnDialogCancel.setText(getContext().getString(R.string.apply_for_reading));
//                btnDialogCancel.setEnabled(true);
//                btnDialogConfirm.setText(getContext().getString(R.string.my_know));
//                break;
            case 5://试阅已过期
                tvVipServiceone.setText(expiredOneStr);
                tvVipServicetwo.setText(expiredTwoStr);
                btnDialogCancel.setVisibility(View.VISIBLE);
                vLIne.setVisibility(View.VISIBLE);
                btnDialogCancel.setClickable(true);
                btnDialogCancel.setText(R.string.txt_cancel);
                btnDialogConfirm.setText(getContext().getString(R.string.try_to_apply_for_read_info));
                break;
            default:
                break;
        }

        viewOnClick();
    }

    private void viewOnClick() {
        btnDialogCancel.setOnClickListener(this);
        btnDialogConfirm.setOnClickListener(this);
        tvVipServicePhoneOne.setOnClickListener(this);
        tvVipServicePhonetwo.setOnClickListener(this);
    }

    private ApplyforModel applyforModel;

    private void initStr() {
        applyforModel = ApplyforModel.getInstance().getApplyforModel();
        if (applyforModel != null) {
            List<String> deindMsgLsit = applyforModel.getDeindMsg();
            List<String> expiredList = applyforModel.getExpiredMsg();
            List<String> phoneList = applyforModel.getPhones();
            List<String> trialTimeList = applyforModel.getTrialTimeMsg();
            if (deindMsgLsit.size() >= 2) {
                stringone = deindMsgLsit.get(0);
                stringtwo = deindMsgLsit.get(1);
            } else {
                stringone = getContext().getResources().getString(R.string.dialog_apply_text_one);
                stringtwo = getContext().getResources().getString(R.string.dialog_apply_text_two);
            }
            if (expiredList.size() >= 2) {
                expiredOneStr = expiredList.get(0);
                expiredTwoStr = expiredList.get(1);
            } else {
                expiredOneStr = getContext().getResources().getString(R.string.dialog_apply_text_five);
                expiredTwoStr = getContext().getResources().getString(R.string.dialog_apply_text_six);
            }
            if (phoneList.size() >= 2) {
                phoneOne = phoneList.get(0);
                phoneTwo = phoneList.get(1);
            } else {
                phoneOne = getContext().getResources().getString(R.string.service_phone_one);
                phoneTwo = getContext().getResources().getString(R.string.service_phone_two);
            }
            if (trialTimeList.size() > 0) {
                trialTimeStr = applyforModel.getTrialTimeMsg().get(0);
            }
        } else {
            stringone = getContext().getResources().getString(R.string.dialog_apply_text_one);
            stringtwo = getContext().getResources().getString(R.string.dialog_apply_text_two);
            expiredOneStr = getContext().getResources().getString(R.string.dialog_apply_text_five);
            expiredTwoStr = getContext().getResources().getString(R.string.dialog_apply_text_six);
            phoneOne = getContext().getResources().getString(R.string.service_phone_one);
            phoneTwo = getContext().getResources().getString(R.string.service_phone_two);
        }
        tvVipServiceone.setText(stringone);
        tvVipServicetwo.setText(stringtwo);
        tvVipServicePhoneOne.setText(phoneOne);
        tvVipServicePhonetwo.setText(phoneTwo);
        tvVipServicethree.setText(trialTimeStr);
        tvVipServicePhoneOne.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvVipServicePhonetwo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvVipServiceone.setVisibility(View.VISIBLE);
        tvVipServicetwo.setVisibility(View.VISIBLE);
        tvVipServicethree.setVisibility(View.GONE);
        tvVipServicePhoneOne.setVisibility(View.VISIBLE);
        tvVipServicePhonetwo.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(understandStr)) {
            understandStr = getContext().getString(R.string.understand);
        }
        if (TextUtils.isEmpty(readStr)) {
            readStr = getContext().getString(R.string.dialog_apply_text_five);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDialogCancel:
                if (returnCallback != null)
                    returnCallback.onCancel();
                dismiss();
                break;
            case R.id.btnDialogConfirm:
                if (returnCallback != null)
                    returnCallback.onSure();
                dismiss();
                break;
            case R.id.tvVipServicePhoneOne:
                shwoHintDialog(tvVipServicePhoneOne.getText().toString());
                dismiss();
                break;
            case R.id.tvVipServicePhonetwo:
                shwoHintDialog(tvVipServicePhonetwo.getText().toString());
                dismiss();
                break;
        }
    }

    public void shwoHintDialog(final String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        new HintDialog(getContext(), phone, new DialogCallBack() {
            @Override
            public void onSure() {
                PhoneUtil.makePhone(getContext(), phone);
            }

            @Override
            public void onCancel() {

            }
        }).show();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        ReadPermissionsManager.functionTag = "";
    }
}
