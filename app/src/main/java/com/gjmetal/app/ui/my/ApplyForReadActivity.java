package com.gjmetal.app.ui.my;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.model.my.ApplyforModel;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ContactServiceDialog;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description: 申请订阅
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/24  11:01
 */
public class ApplyForReadActivity extends XBaseActivity {
    @BindView(R.id.tvCompanyLength)
    TextView tvCompanyLength;//公司名长度
    @BindView(R.id.tvContentLength)
    TextView tvContentLength;//关注内容长度
    @BindView(R.id.tvCommit)
    Button tvCommit;//提交
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etCompanyName)
    EditText etCompanyName;
    @BindView(R.id.etRegisteredPhone)
    EditText etRegisteredPhone;
    @BindView(R.id.etAttentionToContent)
    EditText etAttentionToContent;
    private ContactServiceDialog contactServiceDialogTwo;//避免与父类重名
    private String mName, mCompanyName, mRegisteredPhone, mAttention, mCompanyNameLength, mAttentionLength, mFunction;

    @Override
    protected int setRootView() {
        return R.layout.activity_apply_for_read;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.try_to_apply_for_read));
    }

    @Override
    protected void initView() {
        mFunction = getIntent().getStringExtra(Constant.INFO);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mName = s.toString();
            }
        });
        etCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCompanyName = s.toString();
                if (TextUtils.isEmpty(mCompanyName)) {
                    mCompanyNameLength = "0/200";
                } else {
                    mCompanyNameLength = mCompanyName.length() + "/200";
                }
                tvCompanyLength.setText(mCompanyNameLength);
            }
        });
        etRegisteredPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mRegisteredPhone = s.toString();
            }
        });
        if (user!=null&&!TextUtils.isEmpty(user.getMobile()))
        etRegisteredPhone.setText(user.getMobile());
        etAttentionToContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAttention = s.toString();
                if (TextUtils.isEmpty(mAttention)) {
                    mAttentionLength = "0/200";
                } else {
                    mAttentionLength = mAttention.length() + "/200";
                }
                tvContentLength.setText(mAttentionLength);
            }
        });
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBoby();
            }
        });
    }

    @Override
    protected void fillData() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void createBoby() {
        ApplyforModel applyforModel = new ApplyforModel();
        if (!TextUtils.isEmpty(mName)) {
            applyforModel.setName(mName);
        } else {
            ToastUtil.showToast(getString(R.string.contact_name));
            return;
        }
        if (!TextUtils.isEmpty(mCompanyName)) {
            applyforModel.setCompany(mCompanyName);
        } else {
            ToastUtil.showToast(getString(R.string.fill_in_the_company_name));
            return;
        }
        if (!TextUtils.isEmpty(mRegisteredPhone)) {
            if (PhoneUtil.isPhone(mRegisteredPhone)) {
                applyforModel.setMobile(mRegisteredPhone);
            }else {
                ToastUtil.showToast(getString(R.string.txt_phone_error));
                return;
            }
        } else {
            ToastUtil.showToast(getString(R.string.fill_in_the_contact_information));
            return;
        }
        if (!TextUtils.isEmpty(mAttention)) {
            applyforModel.setConcerned(mAttention);
        }
//        else {
//            ToastUtil.show(getString(R.string.fill_in_what_you_want_to_pay_attention_to));
//            return;
//        }
        applyforModel.setFunction(mFunction);
        applyforModel.setUserId(user.getId());
        applyforModel.setPlatform("天下金属APP");
        applyforRead(applyforModel);
    }

    private void applyforRead(ApplyforModel applyforModel) {
        DialogUtil.loadDialog(this);
        RequestBody body = RequestBody.create(GsonUtil.toJson(applyforModel),MediaType.parse("application/json; charset=utf-8")
                );
        Api.getMyService().goApplyForRead(body)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.isError()) {
                            ToastUtil.showToast(listBaseModel.message);
                            return;
                        }
                        if (contactServiceDialogTwo == null) {
                            contactServiceDialogTwo = new ContactServiceDialog(ApplyForReadActivity.this, 2, new DialogCallBack() {
                                @Override
                                public void onSure() {
                                    BusProvider.getBus().post(new ApplyEvent(mFunction, "1"));
                                    finish();
                                }

                                @Override
                                public void onCancel() {
                                    BusProvider.getBus().post(new ApplyEvent(mFunction, "1"));
                                    finish();
                                }
                            });
                        }
                        contactServiceDialogTwo.show();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });
    }
}
