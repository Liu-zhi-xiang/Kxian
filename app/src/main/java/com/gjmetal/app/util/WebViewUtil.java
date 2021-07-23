package com.gjmetal.app.util;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Description：WebView工具类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:21
 */
public class WebViewUtil {
    @SuppressWarnings("deprecation")
    public static void initSetting(Context mContext, WebView wv) {
        if (wv==null){
            return;
        }
        WebSettings webSettings = wv.getSettings();
        // webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//缩放成单列
//        webSettings.setBuiltInZoomControls(true);// 支持两指缩放
//        webSettings.setUseWideViewPort(true);// 支持双击缩放
        webSettings.setLoadWithOverviewMode(true);// 设置全屏
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        // 增加对JS的支持
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);// ajax 框架请求
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//允许js弹出窗口
        webSettings.setTextZoom(100);//WebView里的字体就不会随系统字体大小设置发生变化了.
        //跨域问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置不缓存数据
        webSettings.setRenderPriority(WebSettings.RenderPriority.LOW); // 设置渲染优先级
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = mContext.getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

}
