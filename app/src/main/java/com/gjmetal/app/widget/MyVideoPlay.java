package com.gjmetal.app.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gjmetal.app.R;
import com.gjmetal.app.util.NetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;

/**
 * 视频布局
 * Created by huangb on 2018/4/7.
 */
@SuppressLint("WrongConstant")
public class MyVideoPlay extends JZVideoPlayer {
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;
    public ImageView backButton;
    public ProgressBar bottomProgressBar;
    public ProgressBar loadingProgressBar;
    public TextView titleTextView;
    public ImageView thumbImageView;
    public ImageView tinyBackImageView;
    public LinearLayout batteryTimeLayout;
    public ImageView batteryLevel;
    public TextView videoCurrentTime;
    public TextView replayTextView;
    public TextView clarity;
    public PopupWindow clarityPopWindow;
    public TextView mRetryBtn;
    public LinearLayout mRetryLayout;
    protected MyVideoPlay.DismissControlViewTimerTask mDismissControlViewTimerTask;
    protected Dialog mProgressDialog;
    protected ProgressBar mDialogProgressBar;
    protected TextView mDialogSeekTime;
    protected TextView mDialogTotalTime;
    protected ImageView mDialogIcon;
    protected Dialog mVolumeDialog;
    protected ProgressBar mDialogVolumeProgressBar;
    protected TextView mDialogVolumeTextView;
    protected ImageView mDialogVolumeImageView;
    protected Dialog mBrightnessDialog;
    protected ProgressBar mDialogBrightnessProgressBar;
    protected TextView mDialogBrightnessTextView;
    public static long LAST_GET_BATTERYLEVEL_TIME = 0L;
    public static int LAST_GET_BATTERYLEVEL_PERCENT = 70;
    boolean tmp_test_back = false;
    private Context mContext;
    private OnNoWifiTouchPlayListener mPlayListener;
    private OnNoNetworkTouchPlayListener mNoNetworkTouchPlayListener;

    private BroadcastReceiver battertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int percent = level * 100 / scale;
                MyVideoPlay.LAST_GET_BATTERYLEVEL_PERCENT = percent;
                MyVideoPlay.this.setBatteryLevel();
                MyVideoPlay.this.getContext().unregisterReceiver(MyVideoPlay.this.battertReceiver);
            }

        }
    };

    public MyVideoPlay(Context context) {
        super(context);
    }

    public MyVideoPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(final Context context) {
        super.init(context);
        mContext = context;
        this.batteryTimeLayout = this.findViewById(R.id.battery_time_layout);
        this.bottomProgressBar = this.findViewById(R.id.bottom_progress);
        this.titleTextView = this.findViewById(R.id.title);
        this.backButton = this.findViewById(R.id.back);
        this.thumbImageView = this.findViewById(R.id.thumb);
        this.loadingProgressBar = this.findViewById(R.id.loading);
        this.tinyBackImageView = this.findViewById(R.id.back_tiny);
        this.batteryLevel = this.findViewById(R.id.battery_level);
        this.videoCurrentTime = this.findViewById(R.id.video_current_time);
        this.replayTextView = this.findViewById(R.id.replay_text);
        this.clarity = this.findViewById(R.id.clarity);
        this.mRetryBtn = this.findViewById(R.id.retry_btn);
        this.mRetryLayout = this.findViewById(R.id.retry_layout);
        this.thumbImageView.setOnClickListener(this);
        this.backButton.setOnClickListener(this);
        this.tinyBackImageView.setOnClickListener(this);
        this.clarity.setOnClickListener(this);
        this.mRetryBtn.setOnClickListener(this);
        this.startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("JiaoZiVideoPlayer", "onClick start [" + this.hashCode() + "] ");
                if(dataSourceObjects == null || JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_url), 0).show();
                    return;
                }

                if(currentState == 0) {
                    if (!NetUtil.checkNet(context)){
//                        ToastUtil.showToast(R.string.net_error);

                        if (mNoNetworkTouchPlayListener != null){
                            mNoNetworkTouchPlayListener.setTouListener();
                        }

                        return;
                    }
                    if(!JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") && !JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/") && !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                        showWifiDialog();
                        return;
                    }

                    startVideo();
                    onEvent(0);
                } else if(currentState == 3) {
                    onEvent(3);
                    Log.d("JiaoZiVideoPlayer", "pauseVideo [" + hashCode() + "] ");
                    JZMediaManager.pause();
                    onStatePause();
                } else if(currentState == 5) {
                    onEvent(4);
                    JZMediaManager.start();
                    onStatePlaying();
                } else if(currentState == 6) {
                    onEvent(2);
                    startVideo();
                }
            }
        });
    }

    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        if (this.dataSourceObjects == null || JZUtils.getCurrentFromDataSource(dataSourceObjects, this.currentUrlMapIndex) == null || !JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex).equals(JZUtils.getCurrentFromDataSource(dataSourceObjects, this.currentUrlMapIndex))) {
            if (this.isCurrentJZVD() && JZUtils.dataSourceObjectsContainsUri(dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                long position = 0L;

                try {
                    position = JZMediaManager.getCurrentPosition();
                } catch (IllegalStateException var8) {
                    var8.printStackTrace();
                }

                if (position != 0L) {
                    JZUtils.saveProgress(this.getContext(), JZMediaManager.getCurrentDataSource(), position);
                }

                JZMediaManager.instance().releaseMediaPlayer();
            } else if (this.isCurrentJZVD() && !JZUtils.dataSourceObjectsContainsUri(dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                this.startWindowTiny();
            } else if (!this.isCurrentJZVD() && JZUtils.dataSourceObjectsContainsUri(dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                if (JZVideoPlayerManager.getCurrentJzvd() != null && JZVideoPlayerManager.getCurrentJzvd().currentScreen == 3) {
                    this.tmp_test_back = true;
                }
            } else if (!this.isCurrentJZVD() && !JZUtils.dataSourceObjectsContainsUri(dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
            }

            this.dataSourceObjects = dataSourceObjects;
            this.currentUrlMapIndex = defaultUrlMapIndex;
            this.currentScreen = screen;
            this.objects = objects;
            this.onStateNormal();
            if (objects.length != 0) {
                this.titleTextView.setText(objects[0].toString());
            }

            if (this.currentScreen == 2) {
                this.fullscreenButton.setImageResource(R.drawable.jz_shrink);
                this.backButton.setVisibility(0);
                this.tinyBackImageView.setVisibility(4);
//                this.batteryTimeLayout.setVisibility(0);
                if (((LinkedHashMap) dataSourceObjects[0]).size() == 1) {
                    this.clarity.setVisibility(8);
                } else {
                    this.clarity.setText(JZUtils.getKeyFromDataSource(dataSourceObjects, this.currentUrlMapIndex));
                    this.clarity.setVisibility(0);
                }

                this.changeStartButtonSize((int) this.getResources().getDimension(R.dimen.jz_start_button_w_h_fullscreen));
            } else if (this.currentScreen != 0 && this.currentScreen != 1) {
                if (this.currentScreen == 3) {
                    this.tinyBackImageView.setVisibility(0);
                    this.setAllControlsVisiblity(4, 4, 4, 4, 4, 4, 4);
//                    this.batteryTimeLayout.setVisibility(8);
                    this.clarity.setVisibility(8);
                }
            } else {
                this.fullscreenButton.setImageResource(R.drawable.jz_enlarge);
                this.backButton.setVisibility(8);
                this.tinyBackImageView.setVisibility(4);
                this.changeStartButtonSize((int) this.getResources().getDimension(R.dimen.jz_start_button_w_h_normal));
//                this.batteryTimeLayout.setVisibility(8);
                this.clarity.setVisibility(8);
            }

            this.setSystemTimeAndBattery();
            if (tmp_test_back) {
                tmp_test_back = false;
                JZVideoPlayerManager.setFirstFloor(this);
                backPress();
            }

        }
    }

    public void changeStartButtonSize(int size) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) loadingProgressBar.getLayoutParams();
        lp1.height = size;
        lp1.width = size;
    }

    public int getLayoutId() {
        return R.layout.my_video;
    }

    public void onStateNormal() {
        super.onStateNormal();
        this.changeUiToNormal();
    }

    public void onStatePreparing() {
        if (mListener != null) {
            mListener.onPrepared();
        }
        super.onStatePreparing();
        this.changeUiToPreparing();
    }

    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        super.onStatePreparingChangingUrl(urlMapIndex, seekToInAdvance);
        this.loadingProgressBar.setVisibility(0);
        this.startButton.setVisibility(4);
    }

    public void onStatePlaying() {
        super.onStatePlaying();
        this.changeUiToPlayingClear();
    }

    public void onStatePause() {
        super.onStatePause();
        this.changeUiToPauseShow();
        this.cancelDismissControlViewTimer();
    }

    public void onStateError() {
        if (mListener != null) {
            mListener.onError();
        }
        super.onStateError();
        this.changeUiToError();
    }

    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        this.changeUiToComplete();
        this.cancelDismissControlViewTimer();
        this.bottomProgressBar.setProgress(100);
        if (mListener != null) {
            mListener.onComplete();
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case 0:
                case 2:
                default:
                    break;
                case 1:
                    this.startDismissControlViewTimer();
                    if (this.mChangePosition) {
                        long duration = this.getDuration();
                        int progress = (int) (this.mSeekTimePosition * 100L / (duration == 0L ? 1L : duration));
                        this.bottomProgressBar.setProgress(progress);
                    }

                    if (!this.mChangePosition && !this.mChangeVolume) {
                        this.onEvent(102);
                        this.onClickUiToggle();
                    }
            }
        } else if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case 0:
                    this.cancelDismissControlViewTimer();
                    break;
                case 1:
                    this.startDismissControlViewTimer();
            }
        }

        return super.onTouch(v, event);
    }

    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.thumb) {
            if (this.dataSourceObjects == null || JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex) == null) {
                Toast.makeText(this.getContext(), this.getResources().getString(R.string.no_url), 0).show();
                return;
            }

            if (this.currentState == 0) {
                if (!JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex).toString().startsWith("file") && !JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex).toString().startsWith("/") && !JZUtils.isWifiConnected(this.getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    if (NetUtil.checkNet(mContext)) {
                        this.showWifiDialog();
                    }
                    return;
                }

                this.onEvent(101);
                this.startVideo();
            } else if (this.currentState == 6) {
                this.onClickUiToggle();
            }
        } else if (i == R.id.surface_container) {
            this.startDismissControlViewTimer();
        } else if (i == R.id.back) {
            backPress();
        } else if (i == R.id.back_tiny) {
            if (JZVideoPlayerManager.getFirstFloor().currentScreen == 1) {
                quitFullscreenOrTinyWindow();
            } else {
                backPress();
            }
        } else if (i == R.id.clarity) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService("layout_inflater");
            final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.jz_layout_clarity, null);
            OnClickListener mQualityListener = new OnClickListener() {
                public void onClick(View v) {
                    int index = ((Integer) v.getTag()).intValue();
                    MyVideoPlay.this.onStatePreparingChangingUrl(index, MyVideoPlay.this.getCurrentPositionWhenPlaying());
                    MyVideoPlay.this.clarity.setText(JZUtils.getKeyFromDataSource(MyVideoPlay.this.dataSourceObjects, MyVideoPlay.this.currentUrlMapIndex));

                    for (int j = 0; j < layout.getChildCount(); ++j) {
                        if (j == MyVideoPlay.this.currentUrlMapIndex) {
                            ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#fff85959"));
                        } else {
                            ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#ffffff"));
                        }
                    }

                    if (MyVideoPlay.this.clarityPopWindow != null) {
                        MyVideoPlay.this.clarityPopWindow.dismiss();
                    }

                }
            };

            for (int j = 0; j < ((LinkedHashMap) this.dataSourceObjects[0]).size(); ++j) {
                String key = JZUtils.getKeyFromDataSource(this.dataSourceObjects, j);
                TextView clarityItem = (TextView) View.inflate(this.getContext(), R.layout.jz_layout_clarity_item, null);
                clarityItem.setText(key);
                clarityItem.setTag(Integer.valueOf(j));
                layout.addView(clarityItem, j);
                clarityItem.setOnClickListener(mQualityListener);
                if (j == this.currentUrlMapIndex) {
                    clarityItem.setTextColor(Color.parseColor("#fff85959"));
                }
            }

            this.clarityPopWindow = new PopupWindow(layout, -2, -2, true);
            this.clarityPopWindow.setContentView(layout);
            this.clarityPopWindow.showAsDropDown(this.clarity);
            layout.measure(0, 0);
            this.clarityPopWindow.update(this.clarity, -40, 46, Math.round((float) (layout.getMeasuredWidth() * 2)), layout.getMeasuredHeight());
        } else if (i == R.id.retry_btn) {
            if (this.dataSourceObjects == null || JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex) == null) {
                Toast.makeText(this.getContext(), this.getResources().getString(R.string.no_url), 0).show();
                return;
            }

            if (!JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex).toString().startsWith("file") && !JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex).toString().startsWith("/") && !JZUtils.isWifiConnected(this.getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                this.showWifiDialog();
                return;
            }

            this.initTextureView();
            this.addTextureView();
            JZMediaManager.setDataSource(this.dataSourceObjects);
            JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex));
            this.onStatePreparing();
            this.onEvent(1);
        }

    }

    public void showWifiDialog() {
//        super.showWifiDialog();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
//        builder.setMessage(this.getResources().getString(R.string.video_not_wifi));
//        builder.setPositiveButton(this.getResources().getString(R.string.video_wifi_confirm), new android.content.DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                MyVideoPlay.this.onEvent(103);
//                MyVideoPlay.this.startVideo();
//                JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED = true;
//            }
//        });
//        builder.setNegativeButton(this.getResources().getString(R.string.video_wifi_cancel), new android.content.DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                MyVideoPlay.this.clearFloatScreen();
//            }
//        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(DialogInterface dialog) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();

//        new HintDialog(getContext(), this.getResources().getString(R.string.video_not_wifi), this.getResources().getString(R.string.video_wifi_cancel), this.getResources().getString(R.string.video_wifi_confirm), new DialogCallBack() {
//            @Override
//            public void onSure() {
//                MyVideoPlay.this.onEvent(103);
//                MyVideoPlay.this.startVideo();
//                JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED = true;
//            }
//
//            @Override
//            public void onCancel() {
//                MyVideoPlay.this.clearFloatScreen();
//            }
//        }).show();


        if (mPlayListener != null){
            mPlayListener.setTouListener();
        }


    }
    public void setNoWifiTouchListener(OnNoWifiTouchPlayListener listener){
        mPlayListener = listener;
    }

    public void setNotworkTouchListener(OnNoNetworkTouchPlayListener listener){
        mNoNetworkTouchPlayListener = listener;
    }


    public interface OnNoWifiTouchPlayListener{
        void setTouListener();
    }


    public interface OnNoNetworkTouchPlayListener{
        void setTouListener();
    }


    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        this.cancelDismissControlViewTimer();
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        if (this.currentState == 3) {
            this.dissmissControlView();
        } else {
            this.startDismissControlViewTimer();
        }

    }

    public void onClickUiToggle() {
        if (this.bottomContainer.getVisibility() != 0) {
            this.setSystemTimeAndBattery();
            this.clarity.setText(JZUtils.getKeyFromDataSource(this.dataSourceObjects, this.currentUrlMapIndex));
        }

        if (this.currentState == 1) {
            this.changeUiToPreparing();
            if (this.bottomContainer.getVisibility() != 0) {
                this.setSystemTimeAndBattery();
            }
        } else if (this.currentState == 3) {
            if (this.bottomContainer.getVisibility() == 0) {
                this.changeUiToPlayingClear();
            } else {
                this.changeUiToPlayingShow();
            }
        } else if (this.currentState == 5) {
            if (this.bottomContainer.getVisibility() == 0) {
                this.changeUiToPauseClear();
            } else {
                this.changeUiToPauseShow();
            }
        }

    }

    public void setSystemTimeAndBattery() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String time=dateFormater.format(date);
        this.videoCurrentTime.setText(time);
        if (System.currentTimeMillis() - LAST_GET_BATTERYLEVEL_TIME > 30000L) {
            LAST_GET_BATTERYLEVEL_TIME = System.currentTimeMillis();
            this.getContext().registerReceiver(this.battertReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        } else {
            this.setBatteryLevel();
        }

    }

    public void setBatteryLevel() {
        int percent = LAST_GET_BATTERYLEVEL_PERCENT;
        if (percent < 15) {
            this.batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_10);
        } else if (percent >= 15 && percent < 40) {
            this.batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_30);
        } else if (percent >= 40 && percent < 60) {
            this.batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_50);
        } else if (percent >= 60 && percent < 80) {
            this.batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_70);
        } else if (percent >= 80 && percent < 95) {
            this.batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_90);
        } else if (percent >= 95 && percent <= 100) {
            this.batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_100);
        }

    }

    public void onCLickUiToggleToClear() {
        if (this.currentState == 1) {
            if (this.bottomContainer.getVisibility() == 0) {
                this.changeUiToPreparing();
            }
        } else if (this.currentState == 3) {
            if (this.bottomContainer.getVisibility() == 0) {
                this.changeUiToPlayingClear();
            }
        } else if (this.currentState == 5) {
            if (this.bottomContainer.getVisibility() == 0) {
                this.changeUiToPauseClear();
            }
        } else if (this.currentState == 6 && this.bottomContainer.getVisibility() == 0) {
            this.changeUiToComplete();
        }

    }

    public void setProgressAndText(int progress, long position, long duration) {
        super.setProgressAndText(progress, position, duration);
        if (progress != 0) {
            this.bottomProgressBar.setProgress(progress);
        }

    }

    public void setBufferProgress(int bufferProgress) {
        super.setBufferProgress(bufferProgress);
        if (bufferProgress != 0) {
            this.bottomProgressBar.setSecondaryProgress(bufferProgress);
        }

    }

    public void resetProgressAndTime() {
        super.resetProgressAndTime();
        this.bottomProgressBar.setProgress(0);
        this.bottomProgressBar.setSecondaryProgress(0);
    }

    public void changeUiToNormal() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(0, 4, 0, 4, 0, 4, 4);
                this.updateStartImage();
                break;
            case 2:
                this.setAllControlsVisiblity(0, 4, 0, 4, 0, 4, 4);
                this.updateStartImage();
            case 3:
        }

    }

    public void changeUiToPreparing() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(4, 4, 4, 0, 0, 4, 4);
                this.updateStartImage();
                break;
            case 2:
                this.setAllControlsVisiblity(4, 4, 4, 0, 0, 4, 4);
                this.updateStartImage();
            case 3:
        }

    }

    public void changeUiToPlayingShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(0, 0, 0, 4, 4, 4, 4);
                this.updateStartImage();
                break;
            case 2:
                this.setAllControlsVisiblity(0, 0, 0, 4, 4, 4, 4);
                this.updateStartImage();
            case 3:
        }

    }

    public void changeUiToPlayingClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(4, 4, 4, 4, 4, 0, 4);
                break;
            case 2:
                this.setAllControlsVisiblity(4, 4, 4, 4, 4, 0, 4);
            case 3:
        }

    }

    public void changeUiToPauseShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(0, 0, 0, 4, 4, 4, 4);
                this.updateStartImage();
                break;
            case 2:
                this.setAllControlsVisiblity(0, 0, 0, 4, 4, 4, 4);
                this.updateStartImage();
            case 3:
        }

    }

    public void changeUiToPauseClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(4, 4, 4, 4, 4, 0, 4);
                break;
            case 2:
                this.setAllControlsVisiblity(4, 4, 4, 4, 4, 0, 4);
            case 3:
        }

    }

    public void changeUiToComplete() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(0, 4, 0, 4, 0, 4, 4);
                this.updateStartImage();
                break;
            case 2:
                this.setAllControlsVisiblity(0, 4, 0, 4, 0, 4, 4);
                this.updateStartImage();
            case 3:
        }

    }

    public void changeUiToError() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                this.setAllControlsVisiblity(4, 4, 0, 4, 4, 4, 0);
                this.updateStartImage();
                break;
            case 2:
                this.setAllControlsVisiblity(0, 4, 0, 4, 4, 4, 0);
                this.updateStartImage();
            case 3:
        }

    }

    public void setAllControlsVisiblity(int topCon, int bottomCon, int startBtn, int loadingPro, int thumbImg, int bottomPro, int retryLayout) {
        this.topContainer.setVisibility(topCon);
        this.bottomContainer.setVisibility(bottomCon);
        this.startButton.setVisibility(startBtn);
        this.loadingProgressBar.setVisibility(loadingPro);
        this.thumbImageView.setVisibility(thumbImg);
        this.bottomProgressBar.setVisibility(bottomPro);
        this.mRetryLayout.setVisibility(retryLayout);
    }

    public void updateStartImage() {
        if (this.currentState == 3) {
            this.startButton.setVisibility(0);
            this.startButton.setImageResource(R.drawable.jz_click_pause_selector);
            this.replayTextView.setVisibility(4);
        } else if (this.currentState == 7) {
            this.startButton.setVisibility(4);
            this.replayTextView.setVisibility(4);
        } else if (this.currentState == 6) {
            this.startButton.setVisibility(0);
            this.startButton.setImageResource(R.drawable.jz_click_replay_selector);
            this.replayTextView.setVisibility(0);
        } else {
            this.startButton.setImageResource(R.drawable.news_play_selector);
            this.replayTextView.setVisibility(4);
        }

    }

    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (this.mProgressDialog == null) {
            View localView = LayoutInflater.from(this.getContext()).inflate(R.layout.jz_dialog_progress, null);
            this.mDialogProgressBar = localView.findViewById(R.id.duration_progressbar);
            this.mDialogSeekTime = localView.findViewById(R.id.tv_current);
            this.mDialogTotalTime = localView.findViewById(R.id.tv_duration);
            this.mDialogIcon = localView.findViewById(R.id.duration_image_tip);
            this.mProgressDialog = this.createDialogWithView(localView);
        }

        if (!this.mProgressDialog.isShowing()) {
            this.mProgressDialog.show();
        }

        this.mDialogSeekTime.setText(seekTime);
        this.mDialogTotalTime.setText(" / " + totalTime);
        this.mDialogProgressBar.setProgress(totalTimeDuration <= 0L ? 0 : (int) (seekTimePosition * 100L / totalTimeDuration));
        if (deltaX > 0.0F) {
            this.mDialogIcon.setBackgroundResource(R.drawable.jz_forward_icon);
        } else {
            this.mDialogIcon.setBackgroundResource(R.drawable.jz_backward_icon);
        }

        this.onCLickUiToggleToClear();
    }

    public void dismissProgressDialog() {
        super.dismissProgressDialog();
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
        }

    }

    public void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        if (this.mVolumeDialog == null) {
            View localView = LayoutInflater.from(this.getContext()).inflate(R.layout.jz_dialog_volume, null);
            this.mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip);
            this.mDialogVolumeTextView = localView.findViewById(R.id.tv_volume);
            this.mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar);
            this.mVolumeDialog = this.createDialogWithView(localView);
        }

        if (!this.mVolumeDialog.isShowing()) {
            this.mVolumeDialog.show();
        }

        if (volumePercent <= 0) {
            this.mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_close_volume);
        } else {
            this.mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_add_volume);
        }

        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }

        this.mDialogVolumeTextView.setText(volumePercent + "%");
        this.mDialogVolumeProgressBar.setProgress(volumePercent);
        this.onCLickUiToggleToClear();
    }

    public void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (this.mVolumeDialog != null) {
            this.mVolumeDialog.dismiss();
        }

    }

    public void showBrightnessDialog(int brightnessPercent) {
        super.showBrightnessDialog(brightnessPercent);
        if (this.mBrightnessDialog == null) {
            View localView = LayoutInflater.from(this.getContext()).inflate(R.layout.jz_dialog_brightness, null);
            this.mDialogBrightnessTextView = localView.findViewById(R.id.tv_brightness);
            this.mDialogBrightnessProgressBar = localView.findViewById(R.id.brightness_progressbar);
            this.mBrightnessDialog = this.createDialogWithView(localView);
        }

        if (!this.mBrightnessDialog.isShowing()) {
            this.mBrightnessDialog.show();
        }

        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }

        this.mDialogBrightnessTextView.setText(brightnessPercent + "%");
        this.mDialogBrightnessProgressBar.setProgress(brightnessPercent);
        this.onCLickUiToggleToClear();
    }

    public void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
        if (this.mBrightnessDialog != null) {
            this.mBrightnessDialog.dismiss();
        }

    }

    public Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(this.getContext(), R.style.jz_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(8);
        window.addFlags(32);
        window.addFlags(16);
        window.setLayout(-2, -2);
        android.view.WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = 17;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    public void startDismissControlViewTimer() {
        this.cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        this.mDismissControlViewTimerTask = new MyVideoPlay.DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(this.mDismissControlViewTimerTask, 2500L);
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }

        if (this.mDismissControlViewTimerTask != null) {
            this.mDismissControlViewTimerTask.cancel();
        }

    }

    public void onAutoCompletion() {
        super.onAutoCompletion();
        this.cancelDismissControlViewTimer();
    }

    public void onCompletion() {
        super.onCompletion();
        this.cancelDismissControlViewTimer();
        if (this.clarityPopWindow != null) {
            this.clarityPopWindow.dismiss();
        }

    }

    public void dissmissControlView() {
        if (this.currentState != 0 && this.currentState != 7 && this.currentState != 6) {
            this.post(new Runnable() {
                public void run() {
                    MyVideoPlay.this.bottomContainer.setVisibility(4);
                    MyVideoPlay.this.topContainer.setVisibility(4);
                    MyVideoPlay.this.startButton.setVisibility(4);
                    if (MyVideoPlay.this.clarityPopWindow != null) {
                        MyVideoPlay.this.clarityPopWindow.dismiss();
                    }

                    if (MyVideoPlay.this.currentScreen != 3) {
                        MyVideoPlay.this.bottomProgressBar.setVisibility(0);
                    }

                }
            });
        }

    }

    public class DismissControlViewTimerTask extends TimerTask {
        public DismissControlViewTimerTask() {
        }

        public void run() {
            MyVideoPlay.this.dissmissControlView();
        }
    }


    public interface OnVideoListener {
        void onPrepared();

        void onComplete();

        void onError();
    }

    private OnVideoListener mListener;

    public void addOnVideoListener(OnVideoListener listener) {
        if (listener != null) {
            this.mListener = listener;
        }
    }

}
