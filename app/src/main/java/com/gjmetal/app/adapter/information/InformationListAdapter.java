package com.gjmetal.app.adapter.information;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.ClipDrawableBean;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.information.InformationWebViewActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.MyVideoPlay;
import com.gjmetal.star.imageloader.ILFactory;
import com.gjmetal.star.imageloader.ILoader;
import com.gjmetal.star.kit.KnifeKit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.droidlover.xrecyclerview.RecyclerAdapter;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 资讯列表
 * Created by huangb on 2018/4/2.
 */

public class InformationListAdapter extends RecyclerAdapter<InformationContentBean.ListBean, InformationListAdapter.BaseViewHodler> {
    private Context mContext;
    public MediaPlayer mMediaPlayer;//音乐播放器
    public int voicePosition = -1;//当前播放的语音
    private Disposable mDisposable;//语音播放倒计时
    private HashMap<Integer, Boolean> mVideoHas = new HashMap<>();//是否在播放视频
    private HashMap<Integer, ClipDrawableBean> mVideoCliList = new HashMap<>();//语音进度动画数组
    private HashMap<Integer, ImageView> mVideoImageList = new HashMap<>();//视频图片数组
    private String mCacheKey;


    //    1： 文字；2：视频；3： 语音； 4： 右侧1张小图；5： 多张小图；6：中间一张大图
    public InformationListAdapter(Context context, String cacheKey) {
        super(context);
        this.mContext = context;
        this.mCacheKey = cacheKey;
    }

    @Override
    public BaseViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //    1： 文字；2：视频；3： 语音； 4： 右侧1张小图；5： 多张小图；6：中间一张大图
        switch (viewType) {
            case 2:
                return new VideoViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_video, parent, false));
            case 3:
                return new VoiceViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_voice, parent, false));
            case 4:
                return new RightViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_right, parent, false));
            case 5:
                return new MostImageViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_main, parent, false));
            case 6:
                return new BigViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_big, parent, false));
            default:
                return new ContentViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_text, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getDataSource().get(position).getNewsType();
    }

    @Override
    public void onBindViewHolder(final BaseViewHodler holder, final int position) {
//        JZVideoPlayer.releaseAllVideos();
        final InformationContentBean.ListBean bean = data.get(position);
        holder.tv_yuan_time.setText(GjUtil.diffDate(DateUtil.getStringDateByLong(bean.getPushTime(), 5), TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))));

        String title = null;
        if (ValueUtil.isStrNotEmpty(bean.getProvide())) {
            title = bean.getProvide().replace(" ", "");
        }
        holder.tv_yuan_title.setText(ValueUtil.isStrEmpty(title) ? "-" : title);
        holder.image_heart.setImageResource(bean.isCollect() ? R.mipmap.ic_news_star_res : R.mipmap.ic_news_star_nor);
        //设置标题已读未读颜色
        holder.tv_content.setTextColor(bean.isHasRead() ? ContextCompat.getColor(context,R.color.c6A798E) : ContextCompat.getColor(context,R.color.c202239));
        if (bean.getVip().equals("Y") && holder.tvVip != null) {
            holder.tvVip.setVisibility(View.VISIBLE);
        } else {
            holder.tvVip.setVisibility(View.GONE);
        }
        holder.llCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppAnalytics.getInstance().onEvent(context, "info_collect");
                if (mOnCollectListener != null) {
                    mOnCollectListener.OnCollect(data.get(position), position);
                }
            }
        });
        GjUtil.setInfromataionTitle(context, holder.tv_content, bean.getRecommend(), bean.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.getVip().equals("Y")) {
                    ReadPermissionsManager.readPermission(Constant.News.RECORD_NEWS_CODE
                            , Constant.POWER_RECORD
                            , Constant.News.RECORD_NEWS_MODULE
                            , context
                            , null
                            , Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP, true, false).subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                                bean.setHasRead(true);
                                notifyDataSetChanged();
                                GjUtil.setInforMationReadStasus(mCacheKey, bean.getNewsId());
                                InformationWebViewActivity.launch((Activity) context, bean, new WebViewBean(bean.getTitle(), Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl()), bean.getNewsId()), Constant.IntentFrom.INFORMATION);
                            }
                        }
                    });
                } else {
                    bean.setHasRead(true);
                    notifyDataSetChanged();
                    GjUtil.setInforMationReadStasus(mCacheKey, bean.getNewsId());
                    InformationWebViewActivity.launch((Activity) context, bean, new WebViewBean(bean.getTitle(), Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl()), bean.getNewsId()), Constant.IntentFrom.INFORMATION);
                }

            }
        });
        switch (getItemViewType(holder.getAdapterPosition())) {
            case 1:
                break;
            case 2:
                //判断数据是否为空
                ((VideoViewHodler) holder).rlNotWifiPlay.setVisibility(View.GONE);
                ((VideoViewHodler) holder).rlFaillPaly.setVisibility(View.GONE);
                if (bean.getResourceUrl() == null) {
                    ((VideoViewHodler) holder).relative_viode.setVisibility(View.GONE);
                    return;
                } else {
                    ((VideoViewHodler) holder).relative_viode.setVisibility(View.VISIBLE);
                }
                if (((VideoViewHodler) holder).mJcVideoPlayerStandard.thumbImageView != null) {
                    ILFactory.getLoader().loadNet(((VideoViewHodler) holder).mJcVideoPlayerStandard.thumbImageView, bean.getCoverImgs(), new ILoader.Options(R.mipmap.ic_news_loading_video, R.mipmap.ic_news_loading_video));
                }

                //初始化是否播放map
                if (mVideoHas.get(holder.getAdapterPosition()) == null) {
                    mVideoHas.put(position, false);
                }
                try {
                    //设置视频
                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.setUp(bean.getResourceUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "");
                    //添加视频播放监听
                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.addOnVideoListener(new MyVideoPlay.OnVideoListener() {
                        @Override
                        public void onPrepared() {
                            setDimissVoice();
                            for (Integer key : mVideoHas.keySet()) {
                                mVideoHas.put(key, false);
                            }
                            mVideoHas.put(position, true);
                        }

                        @Override
                        public void onComplete() {
                            for (Integer key : mVideoHas.keySet()) {
                                mVideoHas.put(key, false);
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onError() {
                            ((VideoViewHodler) holder).rlFaillPaly.setVisibility(View.VISIBLE);
                            ((VideoViewHodler) holder).tvViewFaillPaly.setText(mContext.getResources().getString(R.string.video_fill_play));
                            ((VideoViewHodler) holder).btnFaillPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((VideoViewHodler) holder).rlFaillPaly.setVisibility(View.GONE);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.onEvent(0);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.startVideo();
                                }
                            });
                        }

                    });

                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.setNoWifiTouchListener(new MyVideoPlay.OnNoWifiTouchPlayListener() {
                        @Override
                        public void setTouListener() {
                            ((VideoViewHodler) holder).rlNotWifiPlay.setVisibility(View.VISIBLE);
                            ((VideoViewHodler) holder).mJcVideoPlayerStandard.startButton.setVisibility(View.GONE);
                            ((VideoViewHodler) holder).btnCencalPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((VideoViewHodler) holder).rlNotWifiPlay.setVisibility(View.GONE);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.startButton.setVisibility(View.VISIBLE);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.clearFloatScreen();
                                }
                            });

                            ((VideoViewHodler) holder).btnConfirmPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((VideoViewHodler) holder).rlNotWifiPlay.setVisibility(View.GONE);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.onEvent(103);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.startVideo();
                                    JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED = true;
                                }
                            });

                        }
                    });
                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.setNotworkTouchListener(new MyVideoPlay.OnNoNetworkTouchPlayListener() {
                        @Override
                        public void setTouListener() {
                            ((VideoViewHodler) holder).rlFaillPaly.setVisibility(View.VISIBLE);
                            ((VideoViewHodler) holder).tvViewFaillPaly.setText(mContext.getResources().getString(R.string.video_notwork_not_play));
                            ((VideoViewHodler) holder).btnFaillPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((VideoViewHodler) holder).rlFaillPaly.setVisibility(View.GONE);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.onEvent(0);
                                    ((VideoViewHodler) holder).mJcVideoPlayerStandard.startVideo();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                //判空
                if (bean.getResourceUrl() == null) {
                    ((VoiceViewHodler) holder).linearVioce.setVisibility(View.GONE);
                    return;
                } else {
                    ((VoiceViewHodler) holder).linearVioce.setVisibility(View.VISIBLE);
                }
                ((VoiceViewHodler) holder).sbPlayBar.setProgress(0);
                /**
                 * 获取语音长度
                 */
                //时长
                ((VoiceViewHodler) holder).tvPlayTime.setText(ValueUtil.isStrNotEmpty(bean.getSeconds()) ? AppUtil.checkVoiceTime2(bean.getSeconds()) : "");

                mVideoCliList.put(position, new ClipDrawableBean(((VoiceViewHodler) holder).sbPlayBar, position));
                mVideoCliList.get(position).getSeekBar().setProgress(0);

                /**
                 * 是否播放
                 */
                if (ValueUtil.isStrEmpty(bean.getSeconds())) {
                    ((VoiceViewHodler) holder).ivPalyStop.setVisibility(View.GONE);
                } else {
                    ((VoiceViewHodler) holder).ivPalyStop.setVisibility(View.VISIBLE);
                }
                mVideoImageList.put(position, ((VoiceViewHodler) holder).ivPalyStop);
                /**
                 * 当前播放就启动动画和显示进度
                 */
                if (voicePosition == position) {
                    startVoicePlayAnimation();
                } else {
                    stopVoicePlayAnimation(((VoiceViewHodler) holder).ivPalyStop);
                }


                /**
                 * 语音播放
                 */
                ((VoiceViewHodler) holder).ivPalyStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view1) {
                        /**
                         * 语音时长为空不处理
                         */
                        if (ValueUtil.isStrEmpty(bean.getSeconds())) {
                            return;
                        }
                        /**
                         *判断是否在播放动画
                         */
                        if (voicePosition == position) {
                            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                mMediaPlayer.pause();
                                stopVoicePlayAnimation(((VoiceViewHodler) holder).ivPalyStop);
                                return;
                            } else {
                                if (mMediaPlayer != null) {
                                    mMediaPlayer.start();
                                    startVoicePlayAnimation();
                                }
                                return;
                            }
                        } else {
                            setDimissVoiceAndVideo();
                        }

                        initMediaPlay(position, bean, holder);

                        if (mDisposable == null || mDisposable.isDisposed())
                            statDaojishi();

                    }
                });
                //拖拽
                ((VoiceViewHodler) holder).sbPlayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int countProgress = seekBar.getProgress();
                        initMediaPlay(position, bean, holder);
                        if (mDisposable != null && !mDisposable.isDisposed()) {
                            mDisposable.dispose();
                            mDisposable = null;
                        }
                        mMediaPlayer.seekTo(mMediaPlayer.getDuration() / 100 * countProgress);
                        if (mDisposable == null || mDisposable.isDisposed())
                            statDaojishi();
                    }
                });
                ((VoiceViewHodler) holder).sbPlayBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                break;
            case 4:
                if (bean.getCoverImgs() != null) {
                    String[] strArrayImg = bean.getCoverImgs().split(",");
                    ILFactory.getLoader().loadNet(((RightViewHodler) holder).image_right, strArrayImg[0], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                    ((RightViewHodler) holder).image_right.setVisibility(View.VISIBLE);
                } else {
                    ((RightViewHodler) holder).image_right.setVisibility(View.GONE);
                    return;
                }
                break;
            case 5:
                ((MostImageViewHodler) holder).image_second.setVisibility(View.GONE);
                ((MostImageViewHodler) holder).image_third.setVisibility(View.GONE);
                ((MostImageViewHodler) holder).image_first.setVisibility(View.GONE);
                if (bean.getCoverImgs() == null) {
                    return;
                }

                String[] strArray = bean.getCoverImgs().split(",");
                if (strArray.length > 2) {
                    ((MostImageViewHodler) holder).image_first.setVisibility(View.VISIBLE);
                    ((MostImageViewHodler) holder).image_second.setVisibility(View.VISIBLE);
                    ILFactory.getLoader().loadNet(((MostImageViewHodler) holder).image_second, strArray[1], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                    ILFactory.getLoader().loadNet(((MostImageViewHodler) holder).image_first, strArray[0], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                    ((MostImageViewHodler) holder).image_third.setVisibility(View.VISIBLE);
                    ILFactory.getLoader().loadNet(((MostImageViewHodler) holder).image_third, strArray[2], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                } else if (strArray.length > 1) {
                    ((MostImageViewHodler) holder).image_first.setVisibility(View.VISIBLE);
                    ((MostImageViewHodler) holder).image_second.setVisibility(View.VISIBLE);
                    ILFactory.getLoader().loadNet(((MostImageViewHodler) holder).image_second, strArray[1], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                    ILFactory.getLoader().loadNet(((MostImageViewHodler) holder).image_first, strArray[0], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                } else if (strArray.length > 0) {
                    ((MostImageViewHodler) holder).image_first.setVisibility(View.VISIBLE);
                    ILFactory.getLoader().loadNet(((MostImageViewHodler) holder).image_first, strArray[0], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                }
                break;
            case 6:
                if (bean.getCoverImgs() != null) {
                    ((BigViewHodler) holder).image_main.setVisibility(View.VISIBLE);
                } else {
                    ((BigViewHodler) holder).image_main.setVisibility(View.GONE);
                    return;
                }
                if (ValueUtil.isStrNotEmpty(bean.getCoverImgs())) {
                    ILFactory.getLoader().loadNet(((BigViewHodler) holder).image_main, bean.getCoverImgs(), new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
                }
                break;
        }
    }

    public void initMediaPlay(final int position, InformationContentBean.ListBean bean, final BaseViewHodler holder) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(bean.getResourceUrl());
                mMediaPlayer.prepareAsync();

                /**
                 * 语音播放准备监听(准备完毕开启动画和进度)
                 */
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        voicePosition = position;
                        mp.start();
                        startVoicePlayAnimation();
                        notifyDataSetChanged();
                    }

                });
                /**
                 * 语音播放结束监听
                 */
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        setDimissVoice();
                    }
                });
                /**
                 * 语音播放失败监听
                 */
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        if (mVideoCliList.get(voicePosition) != null)
                            mVideoCliList.get(position).getSeekBar().setProgress(0);

                        stopVoicePlayAnimation(((VoiceViewHodler) holder).ivPalyStop);
                        return false;
                    }
                });
                // 网络流媒体的缓冲监听
                mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                        Log.d("--->", percent + "");
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.showToast("播放失败");
        }
    }


    /**
     * 关闭语音和视频
     */
    public void setDimissVoiceAndVideo() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
                mDisposable = null;
            }
            voicePosition = -1;
            notifyDataSetChanged();
        }
        JZVideoPlayer.releaseAllVideos();
    }

    //关闭语音
    public void setDimissVoice() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
                mDisposable = null;
            }
            voicePosition = -1;
            notifyDataSetChanged();
        }
    }


    //音频
    public static class VoiceViewHodler extends BaseViewHodler {
        @BindView(R.id.linear_vioce)
        RelativeLayout linearVioce;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_paly_stop)
        ImageView ivPalyStop;
        @BindView(R.id.sb_play_Bar)
        SeekBar sbPlayBar;
        @BindView(R.id.tv_play_time)
        TextView tvPlayTime;
        @BindView(R.id.tvVip)
        TextView tvVip;

        public VoiceViewHodler(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }


    //右侧
    public static class RightViewHodler extends BaseViewHodler {
        @BindView(R.id.image_right)
        ImageView image_right;

        public RightViewHodler(View itemView) {
            super(itemView);
        }
    }


    //多图
    public static class MostImageViewHodler extends BaseViewHodler {
        @BindView(R.id.image_first)
        ImageView image_first;
        @BindView(R.id.image_second)
        ImageView image_second;
        @BindView(R.id.image_third)
        ImageView image_third;
        @BindView(R.id.tvVip)
        TextView tvVip;

        public MostImageViewHodler(View itemView) {
            super(itemView);
        }
    }

    //大图
    public static class BigViewHodler extends BaseViewHodler {
        @BindView(R.id.image_main)
        ImageView image_main;
        @BindView(R.id.tvVip)
        TextView tvVip;

        public BigViewHodler(View itemView) {
            super(itemView);
        }
    }

    //视频
    public static class VideoViewHodler extends BaseViewHodler {
        @BindView(R.id.image_video)
        ImageView image_video;
        @BindView(R.id.image_start)
        ImageView image_start;
        @BindView(R.id.player_list_video)
        MyVideoPlay mJcVideoPlayerStandard;
        @BindView(R.id.relative_viode)
        RelativeLayout relative_viode;

        @BindView(R.id.rlNotWifiPlay)
        RelativeLayout rlNotWifiPlay;
        @BindView(R.id.btnCencalPlay)
        Button btnCencalPlay;
        @BindView(R.id.btnConfirmPlay)
        Button btnConfirmPlay;


        @BindView(R.id.rlFaillPaly)
        RelativeLayout rlFaillPaly;
        @BindView(R.id.btnFaillPlay)
        Button btnFaillPlay;
        @BindView(R.id.tvViewFaillPaly)
        TextView tvViewFaillPaly;
        @BindView(R.id.tvVip)
        TextView tvVip;

        public VideoViewHodler(View itemView) {
            super(itemView);
        }
    }

    //文字
    public static class ContentViewHodler extends BaseViewHodler {
        @BindView(R.id.tvVip)
        TextView tvVip;

        public ContentViewHodler(View itemView) {
            super(itemView);
        }
    }

    public static class BaseViewHodler extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_yuan_time)
        TextView tv_yuan_time;
        @BindView(R.id.tv_yuan_title)
        TextView tv_yuan_title;
        @BindView(R.id.image_heart)
        ImageView image_heart;
        @BindView(R.id.llInformation)
        LinearLayout llInformation;
        @BindView(R.id.llCollect)
        LinearLayout llCollect;
        @BindView(R.id.tvVip)
        TextView tvVip;

        public BaseViewHodler(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }

    }

    public interface OnCollectListener {
        void OnCollect(InformationContentBean.ListBean bean, int position);
    }

    private OnCollectListener mOnCollectListener;

    /**
     * 收藏点击
     */
    public void setOnCollectListener(OnCollectListener onCollectListener) {
        if (onCollectListener != null)
            this.mOnCollectListener = onCollectListener;
    }

    /**
     * 开始语音动画
     */
    public void startVoicePlayAnimation() {
        if (mMediaPlayer == null
                || !mMediaPlayer.isPlaying()
                || voicePosition == -1
                || mVideoImageList.get(voicePosition) == null) {
            return;
        }
        mVideoImageList.get(voicePosition).setImageResource(R.drawable.ic_news_voice_stop);
    }

    /**
     * 关闭语音动画
     */
    public void stopVoicePlayAnimation(ImageView view) {
        view.setImageResource(R.drawable.ic_news_voice_paly);
    }


    /**
     * 开始倒计时
     */
    private void statDaojishi() {
        mDisposable = Flowable.interval(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        if (mMediaPlayer != null && mVideoCliList.get(voicePosition) != null && mVideoCliList.get(voicePosition).getPosition() == voicePosition) {
                            mVideoCliList.get(voicePosition).getSeekBar().setProgress((mMediaPlayer.getCurrentPosition() *
                                    100 / mMediaPlayer.getDuration()));
                        }
                    }
                });
    }

}

