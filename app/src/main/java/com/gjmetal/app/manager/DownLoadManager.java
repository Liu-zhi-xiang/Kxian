package com.gjmetal.app.manager;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
/**
 * Description：下载管理
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-2 11:59
 */

import java.io.File;

public class DownLoadManager {
    private static volatile DownLoadManager instance = null;
    public static DownLoadManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadManager.class) {
                if (instance == null) {
                    instance = new DownLoadManager();
                }
            }
        }
        return instance;
    }
    public HttpHandler downLoadFile(String url,String fileName,final DownLoadCallBack callBack){

        return  new HttpUtils().download(url, fileName, true, false, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                callBack.onSuccess(arg0);
            }
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                callBack.onFailure(arg0,arg1);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                int progress = (int) (current / (total / 100));
                callBack.onLoading(progress,current);
            }

        });
    }

    public interface DownLoadCallBack{
        void onSuccess(ResponseInfo<File> arg0);
        void onFailure(HttpException arg0, String arg1);
        void onLoading(int progress,long current);
    }

}
