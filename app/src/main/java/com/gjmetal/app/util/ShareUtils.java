package com.gjmetal.app.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.model.information.CollectBean;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享工具类
 * Created by yuzishun on 2018/4/16.
 * 修改by star
 */
public class ShareUtils {

    /**
     * 收藏
     *
     * @param bean
     */
    public static void collect(final InformationContentBean.ListBean bean) {
        final List<CollectBean> mList = new ArrayList<>();
        mList.add(new CollectBean(bean.getNewsId(), bean.isCollect() ? "2" : "1"));
        Api.getInformationService().collectNew(mList)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            BusProvider.getBus().post(new CollectEvent(mList, true));
                            if (bean.isCollect() == true) {
                                ToastUtil.showToast("取消收藏");
                                bean.setCollect(false);
                            } else {
                                ToastUtil.showToast("收藏成功");
                                bean.setCollect(true);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    /**
     * 分享到
     *
     * @param mContext
     * @param shareType
     * @param shareBean
     */
    public static void shareBitmapTo(Context mContext, Constant.ShareType shareType, WebViewBean shareBean) {
        OnekeyShare oks = new OnekeyShare();
        if (shareType == Constant.ShareType.QQ) {//不支持setImageData与FilePath()方法
            if (isQQClientInstalled(mContext)) {//是否安装了qq
                oks.setImagePath(shareBean.getImgUrl());
            } else {//没有安装QQ不支持单张图片分享
                oks.setText(ValueUtil.isStrNotEmpty(shareBean.getDesc()) ? shareBean.getDesc() : "实时快讯");
                oks.setTitle(ValueUtil.isStrNotEmpty(shareBean.getTitle()) ? shareBean.getTitle() : "实时快讯");
                oks.setTitleUrl(ValueUtil.isStrNotEmpty(shareBean.getUrl()) ? shareBean.getUrl() : "https://demo.shmet.com/");//https://pp.myapp.com/ma_icon/0/icon_52677230_1533510007/96
                oks.setImageUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/04b21447a56a64beb342f2778aa959db96dd86214");//应用宝icon
            }
            oks.setPlatform(QQ.NAME);
        } else if (shareType.equals(Constant.ShareType.SINA)) {
            oks.setImageData(shareBean.getBitmap());
            oks.setPlatform(SinaWeibo.NAME);
        } else if (shareType.equals(Constant.ShareType.WECHAT)) {
            oks.setImageData(shareBean.getBitmap());
            oks.disableSSOWhenAuthorize();
            oks.setPlatform(Wechat.NAME);
        } else if (shareType.equals(Constant.ShareType.WECHAT_FRIENDS)) {
            oks.setImageData(shareBean.getBitmap());
            oks.disableSSOWhenAuthorize();
            oks.setPlatform(WechatMoments.NAME);
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.i("throwable", "onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

                XLog.e("shareError---", throwable.getCause() + "/" + throwable.getMessage());


            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.i("throwable", "onCancel");
            }
        });
        oks.show(mContext);
    }

    /**
     * 分享到
     *
     * @param mContext
     * @param shareType
     * @param shareBean
     */
    public static void shareTo(Context mContext, Constant.ShareType shareType, WebViewBean shareBean) {
        if (ValueUtil.isEmpty(shareBean)) {
            ToastUtil.showToast("未获取到分享信息");
            return;
        }
        XLog.d("imgurl", shareBean.getImgUrl());
        OnekeyShare oks = new OnekeyShare();
        if (ValueUtil.isStrNotEmpty(shareBean.getImgUrl())) {
            oks.setImageUrl(shareBean.getImgUrl());
        }
        oks.setTitle(shareBean.getTitle());
        if (shareType == Constant.ShareType.QQ) {
            oks.setText(shareBean.getDesc());
            oks.setTitleUrl(shareBean.getUrl());
            oks.setPlatform(QQ.NAME);
        } else if (shareType.equals(Constant.ShareType.SINA)) {
            oks.setTitleUrl(shareBean.getUrl());
            oks.setText(shareBean.getTitle() + shareBean.getUrl());
            oks.setPlatform(SinaWeibo.NAME);
        } else if (shareType.equals(Constant.ShareType.WECHAT)) {
            oks.disableSSOWhenAuthorize();
            oks.setText(shareBean.getDesc());
            oks.setUrl(shareBean.getUrl());
            oks.setPlatform(Wechat.NAME);
        } else if (shareType.equals(Constant.ShareType.WECHAT_FRIENDS)) {
            oks.disableSSOWhenAuthorize();
            oks.setText(shareBean.getDesc());
            oks.setUrl(shareBean.getUrl());
            oks.setPlatform(WechatMoments.NAME);
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {


            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        oks.show(mContext);
    }


    /**
     * 分享图片到
     *
     * @param mContext
     * @param shareType
     * @param shareBean
     */
    public static void shareImageTo(Context mContext, Constant.ShareType shareType, WebViewBean shareBean) {
        if (ValueUtil.isEmpty(shareBean)) {
            ToastUtil.showToast("未获取到分享信息");
            return;
        }
        if (ValueUtil.isStrEmpty(shareBean.getImgUrl())) {
            ToastUtil.showToast("未获取到分享图片");
            return;
        }
        XLog.d("imgurl", shareBean.getImgUrl());
        OnekeyShare oks = new OnekeyShare();
        oks.setImageUrl(shareBean.getImgUrl());
        oks.setTitle(shareBean.getTitle());
        if (shareType == Constant.ShareType.QQ) {
            oks.setPlatform(QQ.NAME);
        } else if (shareType.equals(Constant.ShareType.SINA)) {
            oks.setPlatform(SinaWeibo.NAME);
        } else if (shareType.equals(Constant.ShareType.WECHAT)) {
            oks.disableSSOWhenAuthorize();
            oks.setPlatform(Wechat.NAME);
        } else if (shareType.equals(Constant.ShareType.WECHAT_FRIENDS)) {
            oks.disableSSOWhenAuthorize();
            oks.setPlatform(WechatMoments.NAME);
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {


            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        oks.show(mContext);
    }


    /**
     * 判断是否安装了微博
     *
     * @param context
     * @return
     */
    public static boolean isWeiboInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否安装了QQ
     *
     * @param context
     * @return
     */
    public static boolean isQQClientInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

}
