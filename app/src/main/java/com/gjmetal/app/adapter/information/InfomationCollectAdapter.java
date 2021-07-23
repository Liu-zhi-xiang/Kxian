package com.gjmetal.app.adapter.information;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.ClipDrawableBean;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.information.InformationWebViewActivity;
import com.gjmetal.app.ui.login.LoginActivity;
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
import java.util.List;
import java.util.Map;
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
 *  Description:
 *          收藏列表
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:20
 *
 */
public class InfomationCollectAdapter extends RecyclerAdapter<InformationContentBean.ListBean, InfomationCollectAdapter.BaseViewHodler> {
    private Context mContext;
    public MediaPlayer mMediaPlayer;//音乐播放器
    public int voicePosition = -1;//当前播放的语音
    private Disposable mDisposable;
    public boolean isCheckList = false;
    public Map<Integer, Boolean> mCheckList = new HashMap<>();//检查列表
    private HashMap<Integer, Boolean> mVideoHas = new HashMap<>();
    private HashMap<Integer, ClipDrawableBean> mVideoCliList = new HashMap<>();
    private HashMap<Integer, ImageView> mVideoImageList = new HashMap<>();

    //    1： 文字；2：视频；3： 语音； 4： 右侧1张小图；5： 多张小图；6：中间一张大图
    public InfomationCollectAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public BaseViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //    1： 文字；2：视频；3： 语音； 4： 右侧1张小图；5： 多张小图；6：中间一张大图
        switch (viewType) {
            case 2:
                return new InfomationCollectAdapter.VideoViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_collect_video, parent, false));
            case 3:
                return new InfomationCollectAdapter.VoiceViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_collect_voice, parent, false));
            case 4:
                return new InfomationCollectAdapter.RightViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_collect_right, parent, false));
            case 5:
                return new InfomationCollectAdapter.MostImageViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_collect_main, parent, false));
            case 6:
                return new InfomationCollectAdapter.BigViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_collect_big, parent, false));
            default:
                return new InfomationCollectAdapter.ContentViewHodler(LayoutInflater.from(context).inflate(R.layout.item_infomation_collect_text, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getDataSource().get(position).getNewsType();
    }

    @Override
    public void onBindViewHolder(final BaseViewHodler holder, final int position) {
        final InformationContentBean.ListBean bean = data.get(position);

        holder.tv_yuan_time.setText(GjUtil.diffDate(DateUtil.getStringDateByLong(bean.getPushTime(), 5), TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))));
        holder.tv_yuan_title.setText(ValueUtil.isStrEmpty(bean.getProvide()) ? "-" : bean.getProvide());
        holder.image_heart.setVisibility(View.INVISIBLE);
        holder.image_heart.setImageResource(bean.isCollect() ? R.mipmap.ic_news_star_res : R.mipmap.ic_news_star_nor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.getVip().equals("Y")){
                    if (User.getInstance().isLoginIng()&&bean.getVip().equals("Y")) {
                        ReadPermissionsManager.readPermission(Constant.News.RECORD_NEWS_CODE
                                ,Constant.POWER_RECORD
                                , Constant.News.RECORD_NEWS_MODULE
                                ,context
                                ,null
                                ,Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP,true,false).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())){
                                    InformationWebViewActivity.launch((Activity) context, bean, new WebViewBean(bean.getTitle(), Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl()),bean.getNewsId()), Constant.IntentFrom.MY_COLLECT);
                                }
                            }
                        });
                    } else {
                        LoginActivity.launch((Activity) context);
                    }
                }else {
                    InformationWebViewActivity.launch((Activity) context, bean, new WebViewBean(bean.getTitle(), Constant.ReqUrl.getInforMationUrl(bean.getDetailsUrl()),bean.getNewsId()), Constant.IntentFrom.MY_COLLECT);
                }


            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnCollectListener != null) {
                    mOnCollectListener.OnCollect(holder.itemView, data, position);
                }
                return true;
            }
        });
        if (bean.getVip().equals("Y")){
            holder.tvVip.setVisibility(View.VISIBLE);
        }else {
            holder.tvVip.setVisibility(View.GONE);
        }

//        holder.image_heart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mOnCollectListener != null) {
//                    mOnCollectListener.OnCollect(data.get(position), position);
//                }
//            }
//        });
        GjUtil.setInfromataionTitle(context, holder.tv_content, bean.getRecommend(), bean.getTitle());
        switch (getItemViewType(position)) {
            case 1:
                break;
            case 2:
//判断数据是否为空
                if (bean.getResourceUrl() == null) {
                    ((VideoViewHodler) holder).relative_viode.setVisibility(View.GONE);
                    return;
                } else {
                    ((VideoViewHodler) holder).relative_viode.setVisibility(View.VISIBLE);
                }

                ILFactory.getLoader().loadNet(((VideoViewHodler) holder).mJcVideoPlayerStandard.thumbImageView, bean.getCoverImgs(), new ILoader.Options(R.mipmap.ic_news_loading_video, R.mipmap.ic_news_loading_video));
                //初始化是否播放map
                if (mVideoHas.get(holder.getAdapterPosition()) == null) {
                    mVideoHas.put(position, false);
                }
                //设置视频
                ((VideoViewHodler) holder).mJcVideoPlayerStandard.setUp(bean.getResourceUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
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
                        ToastUtil.showToast(R.string.src_error);
                    }

                });
                break;
            case 3:
//判空
                if (bean.getResourceUrl() == null) {
                    ((VoiceViewHodler) holder).linear_vioce.setVisibility(View.GONE);
                    return;
                } else {
                    ((VoiceViewHodler) holder).linear_vioce.setVisibility(View.VISIBLE);
                }
                /**
                 * 获取语音长度
                 */
                //时长
                ((VoiceViewHodler) holder).tv_vioce_cont.setText(AppUtil.checkVoiceTime(bean.getSeconds()));
                ((VoiceViewHodler) holder).image_voice.setTag(position);
                /**
                 * 是否播放
                 */
                if (ValueUtil.isStrEmpty(bean.getSeconds())) {
                    ((VoiceViewHodler) holder).tv_vioce_cont.setVisibility(View.GONE);
                    ((VoiceViewHodler) holder).audio_player_iv_wave.setVisibility(View.VISIBLE);
                    ((AnimationDrawable) ((VoiceViewHodler) holder).audio_player_iv_wave.getDrawable()).start();
                } else {
                    ((VoiceViewHodler) holder).tv_vioce_cont.setVisibility(View.VISIBLE);
                    ((VoiceViewHodler) holder).audio_player_iv_wave.setVisibility(View.GONE);
                    ((AnimationDrawable) ((VoiceViewHodler) holder).audio_player_iv_wave.getDrawable()).stop();
                }
                mVideoImageList.put(position, ((VoiceViewHodler) holder).image_voice);
                /**
                 * 当前播放就启动动画和显示进度
                 */
                if (voicePosition == position) {
                    startVoicePlayAnimation();
                    ((VoiceViewHodler) holder).downloadProgressId.setVisibility(View.VISIBLE);
                } else {
                    ((VoiceViewHodler) holder).downloadProgressId.setVisibility(View.GONE);
                    stopVoicePlayAnimation(((VoiceViewHodler) holder).image_voice);
                }


                /**
                 * 复用进度动画修改
                 */
                ClipDrawable mClipDrawable = (ClipDrawable) ((VoiceViewHodler) holder).downloadProgressId.getBackground();
                mVideoCliList.put(position, new ClipDrawableBean(mClipDrawable, position));
                mVideoCliList.get(position).getmClipDrawable().setLevel(0);
                /**
                 * 语音播放
                 */
                ((VoiceViewHodler) holder).linear_vioce.setOnClickListener(new View.OnClickListener() {
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
                                stopVoicePlayAnimation(((VoiceViewHodler) holder).image_voice);
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
                        try {
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
                                    stopVoicePlayAnimation(((VoiceViewHodler) holder).image_voice);
                                    if (mVideoCliList.get(voicePosition) != null)
                                        mVideoCliList.get(voicePosition).getmClipDrawable().setLevel(0);
                                    return false;
                                }
                            });
                            if (mDisposable == null || mDisposable.isDisposed())
                                statDaojishi();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastUtil.showToast("播放失败");
                        }
                    }
                });
                break;
            case 4:
                if (bean.getCoverImgs() != null) {
                    ((RightViewHodler) holder).image_right.setVisibility(View.VISIBLE);
                } else {
                    ((RightViewHodler) holder).image_right.setVisibility(View.GONE);
                    return;
                }
                String[] strArrayImg = bean.getCoverImgs().split(",");
                ILFactory.getLoader().loadNet(((RightViewHodler) holder).image_right, strArrayImg[0], new ILoader.Options(R.mipmap.ic_news_loading_picture, R.mipmap.ic_news_loading_picture));
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
                            mVideoCliList.get(voicePosition).getmClipDrawable().setLevel(mMediaPlayer.getCurrentPosition() * 10000 /
                                    mMediaPlayer.getDuration());
                        }
                    }
                });
    }

    /**
     * 关闭语音和适配
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

    public void setShowCheck() {
        isCheckList = !isCheckList;
        notifyDataSetChanged();
    }


    //音频
    public static class VoiceViewHodler extends BaseViewHodler {
        @BindView(R.id.tv_vioce_cont)
        TextView tv_vioce_cont;
        @BindView(R.id.linear_vioce)
        RelativeLayout linear_vioce;
        @BindView(R.id.image_voice)
        ImageView image_voice;
        @BindView(R.id.downloadProgressId)
        View downloadProgressId;
        @BindView(R.id.audio_player_iv_wave)
        ImageView audio_player_iv_wave;
        @BindView(R.id.tvVip)
        TextView tvVip;
        public VoiceViewHodler(View itemView) {
            super(itemView);
        }
    }


    //右侧
    public static class RightViewHodler extends BaseViewHodler {
        @BindView(R.id.image_right)
        ImageView image_right;
        @BindView(R.id.tvVip)
        TextView tvVip;
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
        @BindView(R.id.tvVip)
        TextView tvVip;

        public VideoViewHodler(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    //文字
    public static class ContentViewHodler extends BaseViewHodler {

        public ContentViewHodler(View itemView) {
            super(itemView);
        }
    }

    public interface OnCollectListener {
        void OnCollect(View view, List<InformationContentBean.ListBean> datalist, int position);
    }

    private InfomationCollectAdapter.OnCollectListener mOnCollectListener;

    public void setOnCollectListener(InfomationCollectAdapter.OnCollectListener onCollectListener) {
        if (onCollectListener != null)
            this.mOnCollectListener = onCollectListener;
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
        @BindView(R.id.tvVip)
        TextView tvVip;
        public BaseViewHodler(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }


    public void startVoicePlayAnimation() {
        if (mMediaPlayer == null
                || !mMediaPlayer.isPlaying()
                || voicePosition == -1
                || mVideoImageList.get(voicePosition) == null) {
            return;
        }
        mVideoImageList.get(voicePosition).setImageResource(R.drawable.voice_from_icon);
        ((AnimationDrawable) mVideoImageList.get(voicePosition).getDrawable()).start();
    }

    public void stopVoicePlayAnimation(ImageView view) {
        if (view.getDrawable() != null && view.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) view.getDrawable()).stop();
        }
        view.setImageResource(R.drawable.news_icon_voice_3);
    }

}

