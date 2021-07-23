package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：空数据
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:24
 */
public class EmptyView extends LinearLayout {
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.tvAddExChange)
    TextView tvAddExChange;//添加自选
    @BindView(R.id.tvRetry)
    TextView tvRetry;
    private int setColor = R.color.cE7EDF5;
    private String nodata = getResources().getString(R.string.no_data);
    private Constant.BgColor mBgColor = Constant.BgColor.BLUE;//默认是蓝色背景

    //没有消息提示
    public void setTextNoData(String nodata) {
        this.nodata = nodata;
    }

    public void setxChangeTextColor(int setColor) {
        this.setColor = setColor;
    }

    public EmptyView(Context context) {
        super(context);
        setupView(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    private void setupView(Context context) {
        inflate(context, R.layout.view_empty, this);
        KnifeKit.bind(this);
    }

    //设置文字提示
    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            tvEmpty.setText(msg);
        }
    }
    //设置文字提示
    public void setText(SpannableStringBuilder msg) {
        tvEmpty.setText(msg);
    }

    //设置文字提示
    public void setText(int msg) {
        tvEmpty.setText(msg);
    }


    //设置文字提示
    public String getText() {
        return tvEmpty.getText().toString();
    }

    //设置背景图片
    public void setImage(int res) {
        ivEmpty.setBackgroundResource(res);
    }

    /**
     * K线休市中
     */
    public void showKNodata() {
        ivEmpty.setBackgroundResource(R.mipmap.ic_common_close);
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        tvRetry.setVisibility(View.GONE);
    }


    /**
     * 图片加文字（添加自选、添加订阅）
     */
    public void showAddHint(Constant.BgColor bgColor, int imgRes, int text, final OnClickListener onClickListener) {
        this.mBgColor=bgColor;
        ivEmpty.setBackgroundResource(imgRes);
        tvAddExChange.setVisibility(View.VISIBLE);
        tvAddExChange.setText(getContext().getText(text));
        tvEmpty.setVisibility(View.GONE);
        tvRetry.setVisibility(View.GONE);
        switch (mBgColor) {
            case BLUE:
                tvAddExChange.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvAddExChange.setBackgroundResource(R.drawable.shape_btn_wai_e7edf5);
                break;
            case WHITE:
                tvAddExChange.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvAddExChange.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
        }
        tvAddExChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });
    }


    /**
     * 暂无数据:
     * 默认提示
     */
    public void setNoData(Constant.BgColor bgColor) {
        this.mBgColor=bgColor;
        tvRetry.setVisibility(View.GONE);
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(nodata);
        ivEmpty.setBackgroundResource(R.mipmap.icon_g_data_nothing_light);
        switch (mBgColor) {
            case BLUE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                break;
            case WHITE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                break;
        }

    }

    /**
     * 暂无无数据：
     * 自定义提示
     *
     * @param text
     */
    public void setNoData(Constant.BgColor bgColor,int text) {
        this.mBgColor = bgColor;
        tvRetry.setVisibility(View.GONE);
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(getResources().getString(text));
        ivEmpty.setBackgroundResource(R.mipmap.icon_g_data_nothing_light);
        switch (mBgColor) {
            case BLUE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                break;
            case WHITE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                break;
        }
    }
    /**
     * 暂无无数据：
     * 自定义提示、文字、图片
     *
     * @param text
     */
    public void setNoData(Constant.BgColor bgColor,int text,int img) {
        this.mBgColor = bgColor;
        tvRetry.setVisibility(View.GONE);
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(getResources().getString(text));
        ivEmpty.setBackgroundResource(img);
        switch (mBgColor) {
            case BLUE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                break;
            case WHITE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                break;
        }
    }
    /**
     * 加载失败：
     * 白色、蓝色背景样式
     */
    public void setOnError(Constant.BgColor bgColor) {
        this.mBgColor = bgColor;
        ivEmpty.setBackgroundResource(R.mipmap.icon_g_loadfail_light);
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvRetry.setVisibility(VISIBLE);
        tvEmpty.setText(R.string.load_error);
        tvRetry.setText(R.string.refresh);
        switch (mBgColor) {
            case BLUE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_e7edf5);
                break;
            case WHITE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
            default:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
        }

    }

    /**
     * 加载失败，点击事件回调
     * @param bgColor
     * @param callBackListener
     */
    public void setOnError(Constant.BgColor bgColor,final CallBackListener callBackListener) {
        this.mBgColor = bgColor;
        ivEmpty.setBackgroundResource(R.mipmap.icon_g_loadfail_light);
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvRetry.setVisibility(VISIBLE);
        tvEmpty.setText(R.string.load_error);
        tvRetry.setText(R.string.refresh);
        tvRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callBackListener.tryAgain();
            }
        });
        switch (mBgColor) {
            case BLUE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_e7edf5);
                break;
            case WHITE:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
            default:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
        }

    }
    /**
     * 网络失败：
     * 白色、蓝色背景样式
     *
     * @param bgColor
     */
    public void setOnNetError(Constant.BgColor bgColor) {
        this.mBgColor = bgColor;
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvRetry.setVisibility(VISIBLE);
        tvEmpty.setText(R.string.net_error);
        tvRetry.setText(R.string.again);
        ivEmpty.setBackgroundResource(R.mipmap.icon_g_nonetwork_light);
        switch (mBgColor) {
            case BLUE://蓝色背景
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_e7edf5);
                break;
            case WHITE://白色背景
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
            default:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
        }

    }

    /**
     * 回调点击事件
     * @param bgColor
     * @param callBackListener
     */
    public void setOnNetError(Constant.BgColor bgColor, final CallBackListener callBackListener) {
        this.mBgColor = bgColor;
        tvAddExChange.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvRetry.setVisibility(VISIBLE);
        tvEmpty.setText(R.string.net_error);
        tvRetry.setText(R.string.again);
        tvRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callBackListener.tryAgain();
            }
        });
        ivEmpty.setBackgroundResource(R.mipmap.icon_g_nonetwork_light);
        switch (mBgColor) {
            case BLUE://蓝色背景
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.cE7EDF5));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_e7edf5);
                break;
            case WHITE://白色背景
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
            default:
                tvEmpty.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setTextColor(ContextCompat.getColor(getContext(),R.color.c9EB2CD));
                tvRetry.setBackgroundResource(R.drawable.shape_btn_wai_9eb2cd);
                break;
        }

    }
  public   interface CallBackListener{
        void tryAgain();
    }
}

