package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gjmetal.app.R;
import com.gjmetal.app.adapter.my.MyMenuAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.my.AppVersion;
import com.gjmetal.app.model.my.MyMenuItem;
import com.gjmetal.app.model.my.UpLoadBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.market.change.AddMarketTagActivity;
import com.gjmetal.app.ui.my.warn.WarningUserActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ImageUtils;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.CircleImageView;
import com.gjmetal.app.widget.ImageFilter;
import com.gjmetal.app.widget.MyGridView;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.net.XApiSubscriber;
import com.meituan.android.walle.WalleChannelReader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import jp.wasabeef.glide.transformations.BlurTransformation;
import qiu.niorgai.StatusBarCompat;


/**
 * Description：我的界面
 * Author: css
 * Email: 1175558532@qq.com
 * Date: 2018-12-18  17:15
 */

public class MyInformationActivity extends BaseActivity {
    @BindView(R.id.image_bg)
    ImageView imageBg;
    @BindView(R.id.image_head)
    CircleImageView imageHead;
    @BindView(R.id.relative_head)
    RelativeLayout relativeHead;
    @BindView(R.id.tv_login)
    AutofitTextView tvLogin;
    @BindView(R.id.tvBack)
    ImageView tvBack;
    @BindView(R.id.linear_view)
    RelativeLayout linearView;
    @BindView(R.id.gvMenu)
    MyGridView gvMenu;
    @BindView(R.id.llPersonal)
    LinearLayout llPersonal;
    @BindView(R.id.llAboutUs)
    LinearLayout llAboutUs;
    @BindView(R.id.ivNewUpdate)
    ImageView ivNewUpdate;
    @BindView(R.id.rlSystemSetting)
    RelativeLayout rlSystemSetting;
    @BindView(R.id.ivVip)
    ImageView ivVip;
    @BindView(R.id.tvVipTime)
    TextView tvVipTime;
    private MyMenuAdapter myMenuAdapter;
    private List<MyMenuItem> myMenuItems = new ArrayList<>();

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置沉浸式状态栏
            StatusBarCompat.translucentStatusBar(this);
        }
        setContentView(R.layout.activity_my_information);
        KnifeKit.bind(this);
        showDefault();
        SocketManager.getInstance().leaveAllRoom();
    }

    @Override
    protected void fillData() {
        myMenuItems.add(new MyMenuItem(getString(R.string.txt_mymenu_vip), getString(R.string.txt_mymenu_vip_desc), R.mipmap.ic_user_vip, false));
        myMenuItems.add(new MyMenuItem(getString(R.string.txt_mymenu_message), getString(R.string.txt_mymenu_message_desc), R.mipmap.iv_user_bell, true));
        myMenuItems.add(new MyMenuItem(getString(R.string.txt_mymenu_wran), getString(R.string.txt_mymenu_wran_desc), R.mipmap.iv_user_warn, false));
        myMenuItems.add(new MyMenuItem(getString(R.string.txt_mymenu_mychange), getString(R.string.txt_mymenu_change_desc), R.mipmap.iv_user_future, false));
        myMenuItems.add(new MyMenuItem(getString(R.string.txt_mymenu_collect), getString(R.string.txt_mymenu_collect_desc), R.mipmap.iv_user_star, false));
        if (myMenuItems.size() % 2 != 0) {
            myMenuItems.add(new MyMenuItem(null, null, 0, false));
        }
        myMenuAdapter = new MyMenuAdapter(context, myMenuItems);
        gvMenu.setAdapter(myMenuAdapter);
        gvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (User.getInstance().isLoginIng()) {
                            VipUserCenterActivity.launch(context);
                        } else {
                            LoginActivity.launch(context);
                        }
                        break;
                    case 1:
                        if (User.getInstance().isLoginIng()) {
                            MessageActivity.launch(context);
                        } else {
                            LoginActivity.launch(context);
                        }
                        break;
                    case 2:
                        if (User.getInstance().isLoginIng()) {
                            WarningUserActivity.launch(context);
                        } else {
                            LoginActivity.launch(context);
                        }
                        break;
                    case 3:
                        if (User.getInstance().isLoginIng()) {
                            AddMarketTagActivity.launch(MyInformationActivity.this, true);
                        } else {
                            LoginActivity.launch(context);
                        }
                        break;
                    case 4:
                        if (User.getInstance().isLoginIng()) {
                            ColletctActivity.launch(context);
                        } else {
                            LoginActivity.launch(context);
                        }
                        break;
                }
            }
        });
    }


    private void showDefault() {
        User user = User.getInstance().getUser();
        //拿到初始图
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.iv_user_headbg_default);
        //处理得到模糊效果的图
        Bitmap blurBitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurBitmap = ImageFilter.blurBitmap(this, bmp, 20f);
        }
        if (!User.getInstance().isLoginIng()) {
            imageBg.setImageBitmap(blurBitmap);
            tvLogin.setText("点击登录");
            if(myMenuAdapter!=null){
                myMenuAdapter.setMsgNum(0);
            }
            tvVipTime.setVisibility(View.GONE);
            ivVip.setVisibility(View.GONE);
            imageHead.setImageResource(R.mipmap.iv_user_head_default);
        } else {
            if(ValueUtil.isEmpty(user)){
                return;
            }
            tvLogin.setText(user.getNickName());
            if (ValueUtil.isStrNotEmpty(user.getExpireDate())) {
                tvVipTime.setVisibility(View.VISIBLE);
                ivVip.setVisibility(View.VISIBLE);
                tvVipTime.setText("VIP 会员：" + user.getExpireDate()+"到期");
            } else {
                ivVip.setVisibility(View.GONE);
                tvVipTime.setVisibility(View.GONE);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (User.getInstance().isLoginIng()) {
            getUserInfo();
        } else {
            showDefault();
        }
        setUpdate();
    }

    public static void launch(Activity activity) {
        //防止快速点击
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Intent intent = new Intent(activity, MyInformationActivity.class);
            activity.startActivity(intent);
        }
    }


    @OnClick({R.id.relative_head, R.id.tv_login, R.id.tvBack, R.id.llPersonal, R.id.llAboutUs, R.id.rlSystemSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative_head:
                if (User.getInstance().isLoginIng()) {
                    setChooseImageOrPhoto();
                } else {
                    LoginActivity.launch(context);
                }
                break;
            case R.id.tv_login:
                if (!User.getInstance().isLoginIng()) {
                    LoginActivity.launch(context);
                }
                break;
            case R.id.tvBack:
                finish();
                break;
            case R.id.llPersonal:
                if (User.getInstance().isLoginIng()) {
                    CheckUserInfoActivity.launch(this);
                } else {
                    LoginActivity.launch(context);
                }
                break;
            case R.id.llAboutUs:
                AboutUsActivity.launch(this);
                break;
            case R.id.rlSystemSetting:
                SystemSettingActivity.launch(this);
                break;
        }
    }

    //设置选择图片与照相

    private void setChooseImageOrPhoto() {
        ImageUtils.getPhotoUrl(this).subscribe(new Consumer<UpLoadBean>() {
            @Override
            public void accept(final UpLoadBean upLoadBean) {
                addSubscription(Api.getMyService().upLoadHead(upLoadBean.getUrl(), upLoadBean.getFileName()), new XApiSubscriber<BaseModel<Object>>() {
                    @Override
                    protected void onFinish() {

                    }
                    @Override
                    protected void onSuccess(BaseModel<Object> objectBaseModel) {
                        DialogUtil.dismissDialog();
                        try {
                            if (objectBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                                Glide.with(MyInformationActivity.this).load(upLoadBean.getUrl()).into(imageHead);
                                if(ValueUtil.isStrNotEmpty(upLoadBean.getUrl())){
                                    User.getInstance().getUser().setAvatarUrl(upLoadBean.getUrl());
                                }
                                Glide.with(MyInformationActivity.this).load(upLoadBean.getUrl()).bitmapTransform(new BlurTransformation(MyInformationActivity.this, 25)).into(imageBg);
                            } else {
                                ToastUtil.showToast(objectBaseModel.getErrorMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });

            }
        });
    }

    //判断是否可更新
    private void setUpdate() {
        String channel = Constant.DEFAULT_CHANNEL;//设置默认渠道
        if (ValueUtil.isStrNotEmpty(WalleChannelReader.getChannel(context))) {
            channel = WalleChannelReader.getChannel(context);
        }
        String versionName = AppUtil.getAppVersionName(context);
        Api.getMyService().appUpdate(channel, versionName)
                .compose(XApi.<BaseModel<AppVersion>>getApiTransformer())
                .compose(XApi.<BaseModel<AppVersion>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<AppVersion>>() {
                               @Override
                               public void onNext(final BaseModel<AppVersion> baseModel) {
                                   if (!ValueUtil.isEmpty(baseModel.getData())) {
                                       ivNewUpdate.setVisibility(View.VISIBLE);
                                   } else {
                                       ivNewUpdate.setVisibility(View.GONE);
                                   }

                                   if (!User.getInstance().isLoginIng()) {
                                       return;
                                   }
                                   getMessageCount();
                               }

                               @Override
                               protected void onFail(NetError error) {
                                   ivNewUpdate.setVisibility(View.GONE);
                                   if (!User.getInstance().isLoginIng()) {
                                       return;
                                   }
                                   getMessageCount();
                               }
                           }
                );
    }

    /**
     * 获取未读消息数量
     */
    private void getMessageCount() {
        addSubscription(Api.getMyService().msgCount(), new XApiSubscriber<BaseModel<Integer>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(BaseModel<Integer> listBaseModel) {
                if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                    if (myMenuAdapter != null) {
                        myMenuAdapter.setMsgNum(listBaseModel.getData());
                    }
                }
            }

            @Override
            protected void onFail(NetError error) {

            }
        });

    }

    //获取用户信息
    public void getUserInfo() {
        addSubscription(Api.getMyService().getUserInfo(), new XApiSubscriber<BaseModel<User>>() {
            @Override
            protected void onFinish() {

            }
            @Override
            protected void onSuccess(BaseModel<User> listBaseModel) {
                User mUsr = listBaseModel.getData();
                User.getInstance().setUser(mUsr);
                if (!ValueUtil.isStrEmpty(mUsr.getNickName())) {
                    tvLogin.setText(mUsr.getNickName());
                } else {
                    tvLogin.setText("--");
                }
                try {
                    if (!ValueUtil.isEmpty(imageHead) && context != null) {
                        Glide.with(MyInformationActivity.this).load(mUsr.getAvatarUrl()).error(R.mipmap.iv_user_head_default).into(imageHead);
                        Glide.with(MyInformationActivity.this).load(mUsr.getAvatarUrl()).error(R.mipmap.iv_user_headbg_default).bitmapTransform(new BlurTransformation(MyInformationActivity.this, 25)).into(imageBg);
                    }

                } catch (Exception e) {
                    tvLogin.setText("--");
                    e.printStackTrace();
                }
                if (ValueUtil.isNotEmpty(mUsr)) {
                    tvLogin.setText(mUsr.getNickName());
                }
                if (ValueUtil.isStrNotEmpty(mUsr.getExpireDate())) {
                    tvVipTime.setVisibility(View.VISIBLE);
                    ivVip.setVisibility(View.VISIBLE);
                    tvVipTime.setText("VIP 会员：" + mUsr.getExpireDate()+"到期");
                } else {
                    ivVip.setVisibility(View.GONE);
                    tvVipTime.setVisibility(View.GONE);
                }

            }

            @Override
            protected void onFail(NetError error) {
                if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                    SharedUtil.put(Constant.TOKEN, "");//清除本地缓存token
                    showDefault();
                } else {
                    ToastUtil.showToast(error.getMessage());
                }

            }
        });

    }

}
