package com.gjmetal.app.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.model.my.ApplyforModel;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description：申请试阅
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-24 14:59
 */

public class ApplyReadView extends LinearLayout implements View.OnClickListener {
    @BindView(R.id.tvVipServiceone)
    TextView tvVipServiceone;
    @BindView(R.id.tvVipServicetwo)
    TextView tvVipServicetwo;
    @BindView(R.id.tvVipServicePhoneOne)
    TextView tvVipServicePhoneOne;
    @BindView(R.id.tvVipServicePhonetwo)
    TextView tvVipServicePhonetwo;
    @BindView(R.id.tvApplyForRead)
    TextView tvApplyForRead;
    @BindView(R.id.tvVipServicethree)
    TextView tvVipServicethree;
    @BindView(R.id.vip_view)
    TextView vipView;
    private String deindOneStr, deindTwoStr, threeStr, phoneOne, phoneTwo;
    private String expiredOneStr, expiredTwoStr, trialTimeStr;
    private ApplyforModel applyforModel;
    private Context mContext;

    public ApplyReadView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ApplyReadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        inflate(context, R.layout.view_apply_permission, this);
        ButterKnife.bind(this);
        applyforModel = ApplyforModel.getInstance().getApplyforModel();
        if (applyforModel != null) {
            List<String> deindMsgLsit = applyforModel.getDeindMsg();
            List<String> expiredList = applyforModel.getExpiredMsg();
            List<String> phoneList = applyforModel.getPhones();
            List<String> trialTimeList = applyforModel.getTrialTimeMsg();
            if (deindMsgLsit.size() >= 2) {
                deindOneStr = deindMsgLsit.get(0);
                deindTwoStr = deindMsgLsit.get(1);
            } else {
                deindOneStr = getResources().getString(R.string.dialog_apply_text_one);
                deindTwoStr = getResources().getString(R.string.dialog_apply_text_two);
            }
            if (expiredList.size() >= 2) {
                expiredOneStr = expiredList.get(0);
                expiredTwoStr = expiredList.get(1);
            } else {
                expiredOneStr = getResources().getString(R.string.dialog_apply_text_five);
                expiredTwoStr = getResources().getString(R.string.dialog_apply_text_six);
            }
            if (phoneList.size() >= 2) {
                phoneOne = phoneList.get(0);
                phoneTwo = phoneList.get(1);
            } else {
                phoneOne = getResources().getString(R.string.service_phone_one);
                phoneTwo = getResources().getString(R.string.service_phone_two);
            }
            if (trialTimeList.size() > 0) {
                trialTimeStr = applyforModel.getTrialTimeMsg().get(0);
            }
        } else {
            deindOneStr = getResources().getString(R.string.dialog_apply_text_one);
            deindTwoStr = getResources().getString(R.string.dialog_apply_text_two);
            expiredOneStr = getResources().getString(R.string.dialog_apply_text_five);
            expiredTwoStr = getResources().getString(R.string.dialog_apply_text_six);
            phoneOne = getResources().getString(R.string.service_phone_one);
            phoneTwo = getResources().getString(R.string.service_phone_two);
        }
        tvVipServiceone.setText(deindOneStr);
        tvVipServicetwo.setText(deindTwoStr);
        tvVipServicethree.setText(trialTimeStr);
        tvVipServicePhoneOne.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvVipServicePhonetwo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvVipServicePhoneOne.setOnClickListener(this);
        tvVipServicePhonetwo.setOnClickListener(this);
    }

    /**
     * 资讯无权限
     */
    public void showInformationViP(boolean isLogin, final BaseCallBack baseCallBack){
        vipView.setVisibility(VISIBLE);
        tvVipServiceone.setVisibility(GONE);
        tvVipServicethree.setVisibility(GONE);
        if (isLogin) {
            tvVipServicetwo.setVisibility(VISIBLE);
            tvVipServicetwo.setText("请联系客服");
            tvVipServicePhoneOne.setVisibility(VISIBLE);
            tvVipServicePhonetwo.setVisibility(VISIBLE);
            tvApplyForRead.setVisibility(GONE);
        }else {
            tvVipServicetwo.setVisibility(GONE);
            tvVipServicePhoneOne.setVisibility(GONE);
            tvVipServicePhonetwo.setVisibility(GONE);
            tvApplyForRead.setVisibility(VISIBLE);
            tvApplyForRead.setText("点击登录");
        }
        tvApplyForRead.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                if (baseCallBack != null)
                    baseCallBack.back(1);
            }
        });
    }


    /**
     * 已经申请试阅
     *
     * @param color
     * @param baseCallBack
     */

    public void showApply(Context context, int color, int phoneColor, View v, final BaseCallBack baseCallBack, View... goneViews) {
        this.mContext = context;
        if (v != null) {
            v.setVisibility(View.VISIBLE);
        }
        if (goneViews != null && goneViews.length > 0) {
            for (View goneV : goneViews) {
                if (goneV != null) {
                    goneV.setVisibility(View.GONE);
                }
            }
        }
        tvVipServicePhoneOne.setTextColor(ContextCompat.getColor(mContext,phoneColor));
        tvVipServicePhonetwo.setTextColor(ContextCompat.getColor(mContext,phoneColor));
        tvVipServiceone.setTextColor(ContextCompat.getColor(mContext,color));
        tvVipServicetwo.setTextColor(ContextCompat.getColor(mContext,color));
        tvVipServicethree.setTextColor(ContextCompat.getColor(mContext,color));
        tvVipServiceone.setText(deindOneStr);
        tvVipServicetwo.setText(deindTwoStr);
        if (TextUtils.isEmpty(trialTimeStr)) {
            tvVipServicethree.setVisibility(GONE);
        } else {
            tvVipServicethree.setText(trialTimeStr);
        }
        tvVipServicePhoneOne.setText(phoneOne);
        tvVipServicePhonetwo.setText(phoneTwo);
        tvApplyForRead.setText(getResources().getString(R.string.try_to_apply_for_read));
        tvApplyForRead.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                if (baseCallBack != null)
                    baseCallBack.back(1);
            }
        });
        tvVipServicethree.setVisibility(VISIBLE);
    }


    /**
     * 已经申请试阅已过期
     */
    public void showPassDueApply(Context context, View v, int color, int phoneColor, final BaseCallBack baseCallBack, View... goneViews) {
        showApply(context, color, phoneColor, v, baseCallBack, goneViews);
        tvApplyForRead.setText(getContext().getString(R.string.try_to_apply_for_read_info));
        tvVipServiceone.setText(expiredOneStr);
        tvVipServicetwo.setText(expiredTwoStr);
        tvVipServicethree.setVisibility(GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvVipServicePhoneOne:
                shwoHintDialog(tvVipServicePhoneOne.getText().toString());
                break;
            case R.id.tvVipServicePhonetwo:
                shwoHintDialog(tvVipServicePhonetwo.getText().toString());
                break;
        }
    }

    private void shwoHintDialog(final String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }

        new HintDialog(mContext, phone, new DialogCallBack() {
            @Override
            public void onSure() {
                PhoneUtil.makePhone(mContext, phone);
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

}
