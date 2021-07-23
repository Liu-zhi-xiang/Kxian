package com.gjmetal.app.manager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.my.AppVersion;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.VersionDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.meituan.android.walle.WalleChannelReader;

/**
 * Description：版本更新管理类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-11 10:22
 */
public class AppVersionManager {
    private static volatile AppVersionManager instance = null;

    public static AppVersionManager getInstance() {
        if (instance == null) {
            synchronized (AppVersionManager.class) {
                if (instance == null) {
                    instance = new AppVersionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 登录、首页、设置界面检查版本更新
     *
     * @param context
     * @param clickCheck
     */
    public static void updateVersion(final Context context, final boolean clickCheck, final ImageView vNew, final VersionDialog.DialogCallBack dialogCallBack) {
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
                                   try {
                                   if (ValueUtil.isEmpty(baseModel.getData())) {
                                       dialogCallBack.onCancel();
                                       if (clickCheck) {
                                           ToastUtil.showToast(R.string.txt_now_isnews_app);
                                       }
                                       if (vNew != null) {
                                           vNew.setVisibility(View.GONE);
                                       }
                                   } else {
                                       if (vNew != null) {
                                           vNew.setVisibility(View.VISIBLE);
                                       } else {
                                           AppVersion appVersion=baseModel.getData();
                                           new VersionDialog(context,appVersion.getVersionName(),appVersion.getTitle(), appVersion.getDescription(), appVersion.getForceSign(), appVersion.getApkUrl(), false, new VersionDialog.DialogCallBack() {
                                               @Override
                                               public void onCancel() {
                                                   dialogCallBack.onCancel();
                                               }

                                               @Override
                                               public void onSure() {
                                                   dialogCallBack.onSure();
                                               }

                                               @Override
                                               public void onLoadFinish() {

                                               }
                                           }).show();

                                       }
                                   }
                                   }catch (Exception e){
                                       e.printStackTrace();
                                   }
                               }
                               @Override
                               protected void onFail(NetError error) {
                                   dialogCallBack.onCancel();
                               }
                           }
                );
    }
}
