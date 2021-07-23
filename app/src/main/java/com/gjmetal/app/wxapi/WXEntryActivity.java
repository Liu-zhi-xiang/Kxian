package com.gjmetal.app.wxapi;

import android.content.Intent;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

/**
 * Created by yuzishun on 2018/4/16.
 */

public class WXEntryActivity extends WechatHandlerActivity{
    @Override
    public void onGetMessageFromWXReq(WXMediaMessage msg) {
        Intent  iL = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(iL);
    }

    @Override
    public void onShowMessageFromWXReq(WXMediaMessage msg) {
        super.onShowMessageFromWXReq(msg);
        if (msg!=null&&msg.mediaObject!=null&&(msg.mediaObject instanceof WXAppExtendObject)){

        }
    }
}
