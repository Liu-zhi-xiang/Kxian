/*
 * Copyright 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gjmetal.app.widget.flowlayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * Description:
 * A Drawable which represents a dragging {@link View}.
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/30  9:07
 *
 */
class TagsHoverDrawable extends BitmapDrawable {

    private  int maxBottom;
    private  int maxRight;
    private  float mDownX;
    private  int mOriginalX;
    /**
     * The original y coordinate of the top of given {@code View}.
     */
    private float mOriginalY;

    /**
     * The original y coordinate of the position that was touched.
     */
    private float mDownY;
    private View mMobileView;

    TagsHoverDrawable(@NonNull final View view, final float downY, final float downX) {
        super(view.getResources(), getBitmapFromView(view));
        mMobileView=view;
        TagInfo tagInfo = (TagInfo) view.getTag();
        ViewGroup parent = (ViewGroup) view.getParent();
        maxBottom = parent.getHeight();
        maxRight = parent.getWidth();
        mOriginalY = tagInfo.rect.top;
        mOriginalX = tagInfo.rect.left;
        mDownY = downY;
        mDownX = downX;
        Log.e("location","maxBottom=="+maxBottom+"" +
                "===maxRight="+maxRight+"===mOriginalY="+mOriginalY+"===mOriginalX="+mOriginalX+"===mDownY="+mDownY+
                "===mDownX="+mDownX+"");
        setBounds(tagInfo.rect.left, tagInfo.rect.top, tagInfo.rect.right, tagInfo.rect.bottom);
    }
    /**
     * Calculates the new position for this {@code HoverDrawable} using given {@link MotionEvent}.
     *
     * @param ev the {@code MotionEvent}.
     *           {@code ev.getActionMasked()} should typically equal {@link MotionEvent#ACTION_MOVE}.
     *                  int dx = (int) event.getRawX() - lastX;
     *                 int dy = (int) event.getRawY() - lastY;
     *                int l = v.getLeft() + dx;
     *                int b = v.getBottom() + dy;
     *                 int r = v.getRight() + dx;
     *                 int t = v.getTop() + dy;
     *                // 下面判断移动是否超出屏幕
     *                 if (l < 0) {
     *                     l = 0;
     *                    r = l + v.getWidth();
     *                 }
     *                 if (t < 0) {
     *                   t = 0;
     *                     b = t + v.getHeight();
     *                 }
     *                 if (r > screenWidth) {
     *                     r = screenWidth;
     *                     l = r - v.getWidth();
     *                }
     *                 if (b > screenHeight) {
     *                     b = screenHeight;
     *                    t = b - v.getHeight();
     *                }
     *                 v.layout(l, t, r, b);
     *
     */
    void handleMoveEvent(@NonNull final MotionEvent ev) {
        ViewGroup parent = (ViewGroup) mMobileView.getParent();
        this.maxBottom = parent.getHeight();
        this.maxRight = parent.getWidth();
        int top = (int) (mOriginalY - mDownY + ev.getY());
        if (top<0){
            top=0;
        }
        int left = (int) (mOriginalX - mDownX + ev.getX());
        if (left<=0){
            left=0;
        }
        int r=left + getIntrinsicWidth();
        int b=top + getIntrinsicHeight();
        if (r>maxRight){
            left= maxRight-mMobileView.getWidth();
            r= maxRight;
        }
        if(b>maxBottom){
            top=(maxBottom-mMobileView.getHeight());
            b= maxBottom;
        }
        Log.e("location2222","===left="+left+"===top="+top+"===right="+r+"===bottom="+b);
        setBounds(left, top, r, b);
    }
    /**
     * Returns whether the user is currently dragging this {@code HoverDrawable} upwards.
     *
     * @return true if dragging upwards.
     */
    boolean isMovingUpwards() {
        return mOriginalY > getBounds().top;
    }

    /**
     * Returns the number of pixels between the original y coordinate of the view, and the current y coordinate.
     * A negative value means this {@code HoverDrawable} is moving upwards.
     *
     * @return the number of pixels.
     */
    int getDeltaY() {
        return (int) (getBounds().top - mOriginalY);
    }

    /**
     * Returns the top coordinate of this {@code HoverDrawable}.
     */
    int getTop() {
        return getBounds().top;
    }

    /**
     * Shifts the original y coordinates of this {@code HoverDrawable} {code height} pixels upwards or downwards,
     * depending on the move direction.
     *
     * @param height the number of pixels this {@code HoverDrawable} should be moved. Should be positive.
     */
    void shift(final int height) {
        int shiftSize = isMovingUpwards() ? -height : height;
        mOriginalY += shiftSize;
        mDownY += shiftSize;
    }

    static Bitmap getBitmapFromView(@NonNull final View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
