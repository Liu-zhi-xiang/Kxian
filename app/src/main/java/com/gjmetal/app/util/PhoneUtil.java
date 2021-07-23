package com.gjmetal.app.util;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;
import java.util.regex.Pattern;
/**
 * Description：手机号检测
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:20
 */
public class PhoneUtil {
    public static boolean isPhone(String phoneStr) {
//        Pattern p = Pattern.compile("^1[1,3,4,5,6,7,8][0-9]{9}$");
//        Matcher m = p.matcher(phoneStr);
        if(ValueUtil.isStrEmpty(phoneStr)){
            return false;
        }
        if(ValueUtil.isStrNotEmpty(phoneStr)&&phoneStr.length()!=11){
            return false;
        }
        return !ValueUtil.isStrNotEmpty(phoneStr) || phoneStr.startsWith("1");
    }

    /**
     * @param str
     * @return
     * @TODO 是否为纯数字 @2015-8-25 @下午9:05:10
     * @THINK
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    /**
     * 拨打电话
     * @param ctx
     * @param number
     */
    public static void makePhone(final Context ctx, final String number) {
        //Android 6.0机型开启权限提示
        Acp.getInstance(ctx).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.CALL_PHONE)
                        .build(),
                new AcpListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted() {
                        // 用intent启动拨打电话
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        ctx.startActivity(intent);
                    }
                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(ctx,permissions.toString() + "权限拒绝",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
