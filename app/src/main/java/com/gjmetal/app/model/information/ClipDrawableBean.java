package com.gjmetal.app.model.information;

import android.graphics.drawable.ClipDrawable;
import android.widget.SeekBar;

import java.io.Serializable;

/**
 *  Description:  收藏列表
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:20
 *
 */
public class ClipDrawableBean implements Serializable{
   private ClipDrawable mClipDrawable;
   private SeekBar mSeekBar;
   private int position;

    public ClipDrawable getmClipDrawable() {
        return mClipDrawable;
    }

    public void setmClipDrawable(ClipDrawable mClipDrawable) {
        this.mClipDrawable = mClipDrawable;
    }

    public int getPosition() {
        return position;
    }

    public ClipDrawable getClipDrawable() {
        return mClipDrawable;
    }

    public void setClipDrawable(ClipDrawable clipDrawable) {
        mClipDrawable = clipDrawable;
    }

    public SeekBar getSeekBar() {
        return mSeekBar;
    }

    public void setSeekBar(SeekBar seekBar) {
        mSeekBar = seekBar;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ClipDrawableBean(ClipDrawable mClipDrawable, int position) {

        this.mClipDrawable = mClipDrawable;
        this.position = position;
    }

    public ClipDrawableBean(SeekBar seekBar, int position) {
        mSeekBar = seekBar;
        this.position = position;
    }
}
