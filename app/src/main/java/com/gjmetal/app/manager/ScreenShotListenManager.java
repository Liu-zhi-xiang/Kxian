package com.gjmetal.app.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.util.QrCodeUtil;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.star.log.XLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * 截屏监听类
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/8/21  11:35
 */
public class ScreenShotListenManager {

    private static final String TAG = "ScreenShotListenManager";

    /**
     * 读取媒体数据库时需要读取的列
     */
    private static final String[] MEDIA_PROJECTIONS = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED,
    };
    /**
     * 读取媒体数据库时需要读取的列, 其中 WIDTH 和 HEIGHT 字段在 API 16 以后才有
     */
    private static final String[] MEDIA_PROJECTIONS_API_16 = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT,
    };

    /**
     * 截屏依据中的路径判断关键字
     */
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap","截屏"
    };

    private static Point sScreenRealSize;

    /**
     * 已回调过的路径
     */
    private final List<String> sHasCallbackPaths = new ArrayList<String>();

    private Context mContext;

    private OnScreenShotListener mListener;

    private long mStartListenTime;

    /**
     * 内部存储器内容观察者
     */
    private MediaContentObserver mInternalObserver;

    /**
     * 外部存储器内容观察者
     */
    private MediaContentObserver mExternalObserver;

    /**
     * 运行在 UI 线程的 Handler, 用于运行监听器回调
     */
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    private boolean isScreening=false;
    private ScreenShotListenManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The context must not be null.");
        }
        mContext = context;

        // 获取屏幕真实的分辨率
        if (sScreenRealSize == null) {
            sScreenRealSize = getRealScreenSize();
            if (sScreenRealSize != null) {
                XLog.e(TAG, "Screen Real Size: " + sScreenRealSize.x + " * " + sScreenRealSize.y);
            } else {
                XLog.e(TAG, "Get screen real size failed.");
            }
        }
    }

    public static ScreenShotListenManager newInstance(Context context) {
        assertInMainThread();
        return new ScreenShotListenManager(context);
    }

    /**
     * 启动监听
     */
    public void startListen() {
        assertInMainThread();

        sHasCallbackPaths.clear();

        // 记录开始监听的时间戳
        mStartListenTime = System.currentTimeMillis();

        // 创建内容观察者
        mInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, mUiHandler);
        mExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mUiHandler);

        // 注册内容观察者
        mContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false,
                mInternalObserver
        );
        mContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                mExternalObserver
        );
    }

    /**
     * 停止监听
     */
    public void stopListen() {
        assertInMainThread();

        // 注销内容观察者
        if (mInternalObserver != null) {
            try {
                mContext.getContentResolver().unregisterContentObserver(mInternalObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mInternalObserver = null;
        }
        if (mExternalObserver != null) {
            try {
                mContext.getContentResolver().unregisterContentObserver(mExternalObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mExternalObserver = null;
        }

        // 清空数据
        mStartListenTime = 0;
        sHasCallbackPaths.clear();
    }

    /**
     * 处理媒体数据库的内容改变
     */
    private void handleMediaContentChange(Uri contentUri) {
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = mContext.getContentResolver().query(
                    contentUri,
                    MEDIA_PROJECTIONS_API_16,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            );

            if (cursor == null) {
                XLog.e(TAG, "Deviant logic.");
                isScreening=false;
                return;
            }
            if (!cursor.moveToFirst()) {
                isScreening=false;
                XLog.e(TAG, "Cursor no data.");
                return;
            }

            // 获取各列的索引
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int dateaddIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED);
            int widthIndex = -1;
            int heightIndex = -1;

            if (Build.VERSION.SDK_INT >= 16) {
                widthIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH);
                heightIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT);
            }

            // 获取行数据
            String data = cursor.getString(dataIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);
            long dateAdd = cursor.getLong(dateaddIndex);
            int width ;
            int height ;
            if (widthIndex >= 0 && heightIndex >= 0) {
                width = cursor.getInt(widthIndex);
                height = cursor.getInt(heightIndex);
            } else {
                // API 16 之前, 宽高要手动获取
                Point size = getImageSize(data);
                width = size.x;
                height = size.y;
            }

            // 处理获取到的第一行数据
            handleMediaRowData(data, dateTaken, dateAdd, width, height);

        } catch (Exception e) {
            isScreening=false;
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                XLog.e(TAG, " cursor.close.");
            }
        }
    }

    private Point getImageSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return new Point(options.outWidth, options.outHeight);
    }


    /**
     * 处理获取到的一行数据
     */
    private void handleMediaRowData(final String data, long dateTaken, long dateAdd, int width, int height) {

        if (checkScreenShot(data, dateTaken, dateAdd, width, height)) {
            XLog.e(TAG, "ScreenShot: path = " + data + "; size = " + width + " * " + height
                    + "; date = " + dateTaken);
            if (mListener != null && !checkCallback(data)) {
                mListener.onShot(data);
//                mergeBitmap_TB((Activity) mContext, data);
            }
        } else {
            // 如果在观察区间媒体数据库有数据改变，又不符合截屏规则，则输出到 log 待分析
            XLog.e(TAG, "Media content changed, but not screenshot: path = " + data
                    + "; size = " + width + " * " + height + "; date = " + dateTaken);
        }
        isScreening=false;
    }

    public void mergeBitmap_TB(Activity mContext, String data) {
        int virtualKeyHeight=0;
        if (isNavigationBarShown(mContext)){
            virtualKeyHeight=getNavigationBarHeight(mContext);
        }
        Bitmap bm = cropBitmap(BitmapFactory.decodeFile(data), virtualKeyHeight);

        Bitmap viewBm = viewSaveToImage(mContext);

        Bitmap bitmap = PictureMergeManager.getPictureMergeManager().mergeBitmap_TB(bm, viewBm, true);

        if (bitmap == null) {
            return;
        }
        WebViewBean shareBean = new WebViewBean();
        shareBean.setTitle("天下金属");
        shareBean.setUrl(Constant.APP_DIALOG_SHARE_UEL);
        shareBean.setBitmap(bitmap);
        setShareDialog(mContext, shareBean);
    }


    /**
     * 计算view的大小,填充布局内容,把view转成图片
     */
    public Bitmap viewSaveToImage(Activity activity) {
        //将布局转化成view对象

        View viewBitmap = LayoutInflater.from(activity).inflate(R.layout.dialog_pictureshare_erweima_two, null);
        ImageView imageView=viewBitmap.findViewById(R.id.ivEwm);
        Bitmap bitmap= QrCodeUtil.addLogo(activity, QrCodeUtil.createQRCode(Constant.APP_DIALOG_SHARE_UEL), BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_new_erweima_logo));
        imageView.setImageBitmap(bitmap);
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        viewBitmap.layout(0, 0, width, height);


        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        viewBitmap.measure(measuredWidth, measuredHeight);
        //然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。接着就可以创建位图并在上面绘制了：
        // 整个View的大小 参数是左上角 和右下角的坐标
        viewBitmap.layout(0, 0, viewBitmap.getMeasuredWidth(), viewBitmap.getMeasuredHeight());
        viewBitmap.setDrawingCacheEnabled(true);
        viewBitmap.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Bitmap llShareImageBitmap = PictureMergeManager.getPictureMergeManager().getViewToBitmap(viewBitmap);
        viewBitmap.destroyDrawingCache();

        return llShareImageBitmap;
    }


    /**
     * @param x      截取高度
     * @param bitmap 屏幕截图图
     * @return 裁剪虚拟键后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int x) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight() ;
        if (w>h){//判断横屏
            w=w-x;
        }else {
            h=h-x;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, null, false);
    }


    private boolean isTimeVaild(long dateAdd) {
        return Math.abs(System.currentTimeMillis() / 1000 - dateAdd) < 1;
    }

    /**
     * 判断指定的数据行是否符合截屏条件
     */
    private boolean checkScreenShot(String data, long dateTaken, long dateAdd, int width, int height) {

        if (!isTimeVaild(dateAdd)) {
            XLog.e(TAG, "无效时间");
            return false;
        }
        /*
         * 判断依据一: 时间判断
         */
        // 如果加入数据库的时间在开始监听之前, 或者与当前时间相差大于10秒, 则认为当前没有截屏
        if (dateTaken < mStartListenTime || (System.currentTimeMillis() - dateTaken) > 10 * 1000) {
            return false;
        }

        /*
         * 判断依据二: 尺寸判断
         */
        if (sScreenRealSize != null) {
            // 如果图片尺寸超出屏幕, 则认为当前没有截屏
            if (
                    !(
                            (width <= sScreenRealSize.x && height <= sScreenRealSize.y)
                                    ||
                                    (height <= sScreenRealSize.x && width <= sScreenRealSize.y)
                    )) {
                return false;
            }
        }

        /*
         * 判断依据三: 路径判断
         */
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        data = data.toLowerCase();
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (String keyWork : KEYWORDS) {
            if (data.contains(keyWork)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否已回调过, 某些手机ROM截屏一次会发出多次内容改变的通知; <br/>
     * 删除一个图片也会发通知, 同时防止删除图片时误将上一张符合截屏规则的图片当做是当前截屏.
     */
    private boolean checkCallback(String imagePath) {
        if (sHasCallbackPaths.contains(imagePath)) {
            return true;
        }
        // 大概缓存15~20条记录便可
        if (sHasCallbackPaths.size() >= 20) {
            for (int i = 0; i < 5; i++) {
                sHasCallbackPaths.remove(0);
            }
        }
        sHasCallbackPaths.add(imagePath);
        return false;
    }

    /**
     * 获取屏幕分辨率
     */
    private Point getRealScreenSize() {
        Point screenSize = null;
        try {
            screenSize = new Point();
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point point=new Point();
            defaultDisplay.getSize(point);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                defaultDisplay.getRealSize(screenSize);
            } else {
                try {
                    Method mGetRawW = Display.class.getMethod("getRawWidth");
                    Method mGetRawH = Display.class.getMethod("getRawHeight");
                    screenSize.set(
                            (Integer) mGetRawW.invoke(defaultDisplay),
                            (Integer) mGetRawH.invoke(defaultDisplay)
                    );
                } catch (Exception e) {
                    screenSize.set(point.x, point.y);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenSize;
    }

    /**
     * 设置截屏监听器
     */
    public void setListener(OnScreenShotListener listener) {
        mListener = listener;
    }

    public  interface OnScreenShotListener {
         void onShot(String imagePath);
    }

    private static void assertInMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            String methodMsg = null;
            if (elements != null && elements.length >= 4) {
                methodMsg = elements[3].toString();
            }
            throw new IllegalStateException("Call the method must be in main thread: " + methodMsg);
        }
    }

    private ShareDialog shareDialog;

    //分享
    private void setShareDialog(Context context, WebViewBean bean) {
        shareDialog = new ShareDialog(1, context, R.style.Theme_dialog, bean);
        shareDialog.setCancelable(false);
        shareDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        shareDialog.getWindow().setGravity(Gravity.CENTER);
        shareDialog.show();
    }
    /**
     * 媒体内容观察者(观察媒体数据库的改变)
     */
    private class MediaContentObserver extends ContentObserver {

        private Uri mContentUri;

        public MediaContentObserver(Uri contentUri, Handler handler) {
            super(handler);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (!isScreening&&!isBackground(mContext)) {
                Log.e(TAG,"截屏");
                isScreening=true;
                handleMediaContentChange(mContentUri);
            }
        }
    }

    /**
     * 判断应用是不是在后台
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    XLog.e(TAG, "处于后台" + appProcess.processName);
                    return true;
                } else {
                    XLog.e(TAG, "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }




    /**
     * 非全面屏下 虚拟按键是否打开
     * @param activity
     * @return
     */
    public static boolean isNavigationBarShown(Activity activity){
        //虚拟键的view,为空或者不可见时是隐藏状态
        View view  = activity.findViewById(android.R.id.navigationBarBackground);
        if(view == null){
            return false;
        }
        int visible = view.getVisibility();

        return visible == View.GONE || visible == View.INVISIBLE ;

    }

    /**
     * 非全面屏下 虚拟键高度(无论是否隐藏)
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height","dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
