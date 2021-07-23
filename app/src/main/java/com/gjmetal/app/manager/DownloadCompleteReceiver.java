package com.gjmetal.app.manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.star.log.XLog;

import static android.content.Context.DOWNLOAD_SERVICE;
/**
 * Description：系统下载广播监听
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-6-15 11:59
 */

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.e("onReceive. intent:{}", intent != null ? intent.toUri(0) : null);
        if (intent != null) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//                String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
//                if (TextUtils.isEmpty(type)) {
//                    type = "*/*";
//                }
                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
//                if (uri != null) {
//                    Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
//                    handlerIntent.setDataAndType(uri, type);
//                    context.startActivity(handlerIntent);
//                }
                if(uri!=null){
                    ToastUtil.showToast("下载成功");
                }
            }
        }
    }
}



//
// GjUtil.downloadBySystem(context, url, contentDisposition, mimeType);
//         IntentFilter intentFilter = new IntentFilter();
//         intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//         registerReceiver(receiver, intentFilter);
