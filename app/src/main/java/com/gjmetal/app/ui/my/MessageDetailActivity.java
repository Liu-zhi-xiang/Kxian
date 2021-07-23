package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.my.MessageBean;
import com.gjmetal.app.model.push.NoticeAction;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：消息详情
 * Author: css
 * Email: 1175558532@qq.com
 * Date: 2018-12-18  17:15
 */

public class MessageDetailActivity extends BaseActivity {
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvLook)
    TextView tvLook;
    private MessageBean.ItemListBean itemListBean;
    private NoticeAction noticeAction = null;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_message_detail);
        itemListBean = (MessageBean.ItemListBean) getIntent().getSerializableExtra("data");
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, "详情");
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this,R.color.c9EB2CD));

        titleBar.rightLayout.setVisibility(View.VISIBLE);
        titleBar.tvRight.setVisibility(View.VISIBLE);
        titleBar.tvRight.setText(R.string.txt_delete);
        titleBar.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HintDialog(context, getString(R.string.txt_clean_onemessage), new DialogCallBack() {
                    @Override
                    public void onSure() {
                        deleteMsg();
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
            }
        });

    }
    @Override
    protected void fillData() {
        if (itemListBean.getType() == 1) {
            tvTitleName.setText("系统消息");
            tvContent.setText(itemListBean.getContent());
        } else {
            tvTitleName.setText("智能预警");
            tvContent.setText(itemListBean.getContent());
            if (itemListBean.getExtension() != null) {
                tvLook.setVisibility(View.VISIBLE);

                noticeAction = GsonUtil.fromJson(itemListBean.getExtension(), NoticeAction.class);
                noticeAction.setJson(itemListBean.getExtension());
            }
        }
        tvTime.setText(DateUtil.getStringDateByLong(Long.parseLong(itemListBean.getCreateAt()), 7));

    }

    public void launch(Activity context, MessageBean.ItemListBean itemListBean) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", itemListBean);
            Router.newIntent(context)
                    .to(MessageDetailActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    /**
     * 删除
     */
    private void deleteMsg() {
        DialogUtil.waitDialog(context);
        List<String> list = new ArrayList<>();
        list.add(itemListBean.getId() + "");
        Api.getMyService().deleteMsg(list)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            setResult(10, new Intent());
                            finish();
                        } else {
                            ToastUtil.showToast(listBaseModel.getErrorMsg());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    @OnClick(R.id.tvLook)
    public void onViewClicked() {
        if (TimeUtils.isCanClick()) {
            if (noticeAction != null) {
                messagAtTheExpiry();
            }else {
                ToastUtil.showToast(getResources().getString(R.string.warning_has_expired));
            }
        }
    }
    //预警失效查询
    private void messagAtTheExpiry(){
        DialogUtil.loadDialog(MessageDetailActivity.this);
        Api.getMyService().userMonitorPermission(noticeAction.getContract(),noticeAction.getIndicatorType())
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())&&(boolean)listBaseModel.data) {
                            PushManager.jumpActivity(context, noticeAction);
                        }else {
                            ToastUtil.showToast(getResources().getString(R.string.warning_has_expired));
                        }
                    }
                    @Override
                    protected void onFail(NetError error) {
                        if(error!=null&&error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())){
                            LoginActivity.launch(context);
                        }else {
                            ToastUtil.showToast(error.getMessage());
                        }
                        DialogUtil.dismissDialog();
                    }
                });
    }
}
