package com.gjmetal.app.ui;

import android.content.Intent;
import android.os.Bundle;

import com.gjmetal.app.R;
import com.gjmetal.app.base.App;
import com.gjmetal.app.manager.PushManager;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

/**
 * Description：友盟离线消息推送
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-9 14:01
 */

public class PushActivity extends UmengNotifyClickActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mipush);
        App.addPushActivity(this);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        PushManager.offLinePush(this, body);
    }

}
