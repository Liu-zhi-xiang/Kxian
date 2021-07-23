package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.model.my.UpLoadBean;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.ImageUtils;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.CircleImageView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApiSubscriber;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 修改用户信息
 * Created by huangb on 2018/4/4.
 */

public class CheckUserInfoActivity extends XBaseActivity {
    @BindView(R.id.iv_head)
    CircleImageView image_head;
    @BindView(R.id.linear_head)
    LinearLayout linearHead;
    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.linear_name)
    RelativeLayout linearName;
    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.ivNext)
    ImageView ivNext;
    @BindView(R.id.linear_phone)
    RelativeLayout linearPhone;
    @BindView(R.id.linear_update)
    RelativeLayout linearUpdate;
    @BindView(R.id.tvMembersId)
    TextView tvMembersId;

    @Override
    protected void initView() {
    }


    @Override
    protected int setRootView() {
        return R.layout.activity_check_userinfo;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getResources().getString(R.string.check_userperson));
    }

    @Override
    protected void fillData() {
        tvPhone.setText(ValueUtil.isStrNotEmpty(user.getMobile()) ? user.getMobile() : "");
        tvUserName.setText(ValueUtil.isStrNotEmpty(user.getNickName()) ? user.getNickName() : "");
        XLog.d("headUrl",user.getAvatarUrl()+"/");
        Glide.with(CheckUserInfoActivity.this).load(user.getAvatarUrl()).error(R.mipmap.iv_user_head_default).into(image_head);
        tvMembersId.setText(user.getMembersId()+"");
    }

    //头像点击
    @OnClick(R.id.linear_head)
    public void headClick() {
        ImageUtils.getPhotoUrl(this).subscribe(new Consumer<UpLoadBean>() {
            @Override
            public void accept(final UpLoadBean upLoadBean) {
                addSubscription(Api.getMyService().upLoadHead(upLoadBean.getUrl(), upLoadBean.getFileName()), new XApiSubscriber<BaseModel<Object>>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(BaseModel<Object> objectBaseModel) {
                        if (objectBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            Glide.with(CheckUserInfoActivity.this).load(upLoadBean.getUrl()).error(R.mipmap.iv_user_head_default).into(image_head);
                        } else {
                            ToastUtil.showToast(objectBaseModel.getErrorMsg());
                        }
                        DialogUtil.dismissDialog();
                    }

                    @Override
                    protected void onFail(NetError throwable) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(throwable.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ValueUtil.isNotEmpty(user)&&tvUserName!=null){
            tvUserName.setText(ValueUtil.isStrNotEmpty(user.getNickName()) ? user.getNickName() : "");
            tvPhone.setText(ValueUtil.isStrNotEmpty(user.getMobile()) ? user.getMobile() : "");
        }
    }

    //名字点击
    @OnClick(R.id.linear_name)
    public void nameClick() {
        CheckUserNameActivity.launch(this);
    }

    //手机点击
    @OnClick(R.id.linear_phone)
    public void phoneClick() {
        CheckUserPhoneActivity.launch(this);
    }


    @OnClick(R.id.linear_update)
    public void onViewClicked() {
        UpdatePasswordActivity.launch(this);
    }

    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(context)
                    .to(CheckUserInfoActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }
}
