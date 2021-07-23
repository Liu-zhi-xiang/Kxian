package com.gjmetal.app.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huangb on 2018/4/7.
 */

public class MediaUtils {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static File file;
    private static RxPermissions mRxPermissions;

    /**
     * Create a file Uri for saving an image or video
     */
    public static Uri getOutputMediaFileUri(Context context, int type) {
        Uri uri = null;
        //适配Android N
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else {
            return Uri.fromFile(getOutputMediaFile(type));
        }
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Constant.BASE_DOWN_PATH + "image");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        file = mediaFile;
        return mediaFile;
    }

    /**
     * 获取视频的第一帧图片
     */
    public static void getImageForVideo(Activity activity, final String videoPath, final OnLoadVideoImageListener listener) {
        mRxPermissions = new RxPermissions(activity);
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    LoadVideoImageTask task = new LoadVideoImageTask(listener);
                    task.execute(videoPath);
                } else {
                    ToastUtil.showToast(R.string.allow_write);
                }

            }
        });

    }

    public static class LoadVideoImageTask extends AsyncTask<String, Integer, File> {
        private OnLoadVideoImageListener listener;

        public LoadVideoImageTask(OnLoadVideoImageListener listener) {
            this.listener = listener;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected File doInBackground(String... params) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            String path = params[0];
            File f = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            try {
                if (path.contains(".png")||path.contains(".jpg")||path.contains(".jpeg")){
                    return f;
                }
                if (path.startsWith("http")) {
                    //获取网络视频第一帧图片
                    mmr.setDataSource(path, new HashMap());
                } else {
                    //本地视频
                    mmr.setDataSource(path);
                }
                Bitmap bitmap = mmr.getFrameAtTime();
                //保存图片
                if (f.exists()) {
                    f.delete();
                }
                FileOutputStream out = new FileOutputStream(f);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmr.release();
            return f;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            try {
                if (listener != null) {
                    listener.onLoadImage(file);
                }
            } catch (Exception e) {

            }
        }
    }

    public interface OnLoadVideoImageListener {
        void onLoadImage(File file);
    }


    /**
     * 获取语音时长
     */
    public static Flowable<String> getVoiceLength(final String url) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) {
                String duration = null;
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                    duration = mediaPlayer.getDuration() + "";
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                } finally {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (duration != null) {
                    e.onNext(duration);
                } else {
                    e.onNext("-1");
                }
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 清理视频图片
     */
    public static void cleanVideoImage() {
        try {
            File dir = new File(Constant.BASE_DOWN_PATH + "image");
            if (!dir.exists() || !dir.isDirectory())
                return;
            for (File file : dir.listFiles()) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
            }
            dir.delete();// 删除目录本身
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
