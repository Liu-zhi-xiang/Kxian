package com.gjmetal.app.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.blankj.utilcode.utils.BarUtils;
import com.blankj.utilcode.utils.ScreenUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by huangb on 2017/3/3.
 * 放IOS touch
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class MyTouchView extends View {
    /**
     * 屏幕宽度
     */
    private float mScreenWhite;
    /**
     * 屏幕高度
     */
    private float mScreenHeight;

    /**
     * 按下的位置
     */
    private float mDownX;
    /**
     * 按下的位置
     */
    private float mDowny;

    /**
     * 最小移动距离
     */
    private int mMiniMove;
    /**
     * 延迟判断
     */
    private CheckForTap mPendingCheckForTap;

    /**
     * 是否达到时间
     */
    private boolean isTimeOut;

    /**
     * 是否顯示
     */
    private boolean isShow;


    /**
     * 距离右边距离
     */
    private int POSITON = 0;

    /**
     * 距离底部
     */
    private int BOTTOM_POSITION = 0;

    /**
     * 父布局高度
     */
    private float Parent_HEIGHT = 0;

    /**
     * 正在动画
     */
    private boolean isOnAnmition = false;

    /**
     * 是否在移動
     */
    private boolean isOnSide = true;

    /**
     * 上个动作是否是移动
     */
    private boolean isMove = false;

    /**
     * 获取底部虚拟按钮的高度
     */
    private int VIRTUALBARHEIGHT = 0;

    /**
     * 动画时间
     */
    private final int ANMITION_TIME = 300;

    /**
     * 放大倍数
     */
    private final int MULTIPLE = 2;


    /**
     * 是否第一次初始化
     */
    private boolean isInit = false;

    public float mLastX = 0;
    /**
     * 初始位置
     */
    private float mInitX = 0;
    private float mInitY = 0;

    /**
     * 是否能touch
     */
    private boolean isCanToch = true;

    /**
     * 默认地址
     */
    private boolean isNomalAddress = false;

    /**
     * 定时器
     */
    public Disposable mDisposable;


    public MyTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        measure(0, 0);
    }

    public MyTouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTouchView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isInit) {
            VIRTUALBARHEIGHT = 0;
            isInit = true;
            mScreenWhite = ScreenUtils.getScreenWidth();
            mScreenHeight = ScreenUtils.getScreenHeight();
            mMiniMove = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            Parent_HEIGHT = mScreenHeight - BarUtils.getStatusBarHeight(getContext());
            if (isNomalAddress) {
                setX(mScreenWhite - getMeasuredWidth() / 2 - POSITON);
//                setY(Parent_HEIGHT - BOTTOM_POSITION - getMeasuredHeight());
                setY(Parent_HEIGHT/2  - getMeasuredHeight() / 2);
            } else {
                setX(mInitX);
                setY(mInitY);

            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanToch) {
            if (mTouchLogoClickListener != null)
                mTouchLogoClickListener.onTouchClick(event.getRawX(), event.getRawY());
            return super.onTouchEvent(event);
        }
        closeMove();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isOnAnmition) {
                    isTimeOut = false;
                    if (mPendingCheckForTap == null) {
                        mPendingCheckForTap = new CheckForTap();
                    }
                    if (isOnSide) {
                        mDownX = event.getRawX();
                        mDowny = event.getRawY();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //x最小移动距离
                if ((Math.abs(event.getRawX() - mDownX) > mMiniMove) || (Math.abs(event.getRawY() - mDowny) > mMiniMove)
                        && !isOnAnmition
                        && (event.getRawY() < Parent_HEIGHT + getMeasuredHeight() - POSITON - VIRTUALBARHEIGHT - 20)) {
                    if (mTouchLogoClickListener != null)
                        mTouchLogoClickListener.OnViewMove(event);
                    isMove = true;
                    if (event.getRawY() <= mScreenHeight) {
                        //防止超出屏幕
                        if (event.getRawX() >= getMeasuredWidth() / 2 && event.getRawX() <= mScreenWhite - getMeasuredWidth() / 2) {
                            setX(event.getRawX() - getMeasuredWidth() / 2);
                            mLastX = event.getRawX();
                        }
                        setY(event.getRawY() - getMeasuredHeight() / 2);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isOnAnmition) {
                    /**
                     *点击
                     */
                    if ((Math.abs(event.getRawX() - mDownX) <= mMiniMove) && !isTimeOut && isOnSide && !isMove) {
//                        if (isShow) {
//                            isShow = false;
//                            setScaleAnimotion();
//                        } else {
//                            isShow = true;
//                            setScaleAnimotion();
//                        }
                        if (getX() < 0 || (getX() + getMeasuredWidth() / 2) >= ScreenUtils.getScreenWidth()) {
                            setMoveOutAnimotion();
                            return true;
                        }
                        if (mTouchLogoClickListener != null)
                            mTouchLogoClickListener.onTouchClick(event.getRawX(), event.getRawY());
                        /**
                         *移动
                         */
                    } else {
                        if (mTouchLogoClickListener != null)
                            mTouchLogoClickListener.onMove();
                        isMove = false;
                        isOnSide = false;
                        if (event.getRawX() > mScreenWhite / 2) {
                            setMoveAnimotion(event.getRawX(), mScreenWhite - getMeasuredWidth() - POSITON + getMeasuredWidth() / 2, false);
                        } else {
                            setMoveAnimotion(event.getRawX(), POSITON + getMeasuredWidth() / 2, false);
                        }
                        startTime();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 設置移動外面動畫
     */
    private void setMoveOutAnimotion() {
        isOnAnmition = true;
        float toX;

        if (getX() > mScreenWhite / 2) {
            toX = mScreenWhite - POSITON - getMeasuredWidth();
        } else {
            toX = POSITON;
        }
        TranslateAnimation animation = new TranslateAnimation(0, toX - getX(), 0, 0);
        animation.setDuration(100);
        animation.setFillAfter(true);
        final float finalToX = toX;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isOnAnmition) {
                    clearAnimation();
                    setX(finalToX);
                    isOnSide = true;
                    isOnAnmition = false;
                    if (mTouchLogoClickListener != null) {
                        mTouchLogoClickListener.OnOutMove();
                    }
                }
            }
        });
        startAnimation(animation);
    }


    /**
     * 設置移動動畫
     */
    private void setMoveAnimotion(final float fromX, float toX, final boolean isOver) {
        isOnAnmition = true;
        float mMoveY = 0;
        /**
         * 如果超過底部
         */
        if (getY() > Parent_HEIGHT - getMeasuredHeight() - BOTTOM_POSITION - VIRTUALBARHEIGHT) {
            mMoveY = Parent_HEIGHT - getY() - BOTTOM_POSITION - getMeasuredHeight() - VIRTUALBARHEIGHT;
        }
        /**
         * 如果超過頂部
         */
        if (getY() - BOTTOM_POSITION < 0) {
            mMoveY = -getY() + BOTTOM_POSITION;
        }

        if (isOver) {
            if (getX() > mScreenWhite / 2) {
                toX = mScreenWhite - POSITON;
            } else {
                toX = POSITON;
            }
        }
        TranslateAnimation animation = new TranslateAnimation(0, toX - getX() - getMeasuredWidth() / 2, 0, mMoveY);
        animation.setDuration(ANMITION_TIME);
        animation.setFillAfter(true);
        final float finalToX = toX;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                setX(finalToX - getMeasuredWidth() / 2);
                //底部
                if (getY() > Parent_HEIGHT - getMeasuredHeight() - BOTTOM_POSITION - VIRTUALBARHEIGHT) {
                    setY(Parent_HEIGHT - getMeasuredHeight() - BOTTOM_POSITION - VIRTUALBARHEIGHT);
                }
                //顶部
                if (getY() - BOTTOM_POSITION < 0) {
                    setY(BOTTOM_POSITION);
                }
                isOnSide = true;
                isOnAnmition = false;
            }
        });
        startAnimation(animation);
    }

    //设置底部
    public void setBottionPosition(int bottom) {
        this.BOTTOM_POSITION = bottom;
    }


    /**
     * 判断是否超过移动时间
     */
    private class CheckForTap implements Runnable {
        public void run() {
            isTimeOut = true;
        }
    }

    /**
     * 设置是否能滑动
     */
    public void setCanTouch(boolean isCan) {
        isCanToch = isCan;
    }

    /**
     * 监听
     */
    public interface OnTouchLogoClickListener {
        void onTouchClick(float v, float v1);

        void onMove();


        void OnViewMove(MotionEvent event);

        void OnOutMove();
    }

    private OnTouchLogoClickListener mTouchLogoClickListener;

    public void addOnTouchLogoClicklistener(OnTouchLogoClickListener onTouchLogoClickListener) {
        if (onTouchLogoClickListener != null)
            mTouchLogoClickListener = onTouchLogoClickListener;
    }

    /**
     * 设置默认位置
     */
    public void setNomalAddress() {
        isNomalAddress = true;
    }

    /**
     * 设置初始位置
     *
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        this.mInitX = x;
        this.mInitY = y;
        this.isNomalAddress = false;
    }


    public void startTime() {
        closeMove();
        mDisposable = Flowable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        closeMove();
                        setMoveAnimotion(0, 0, true);
                    }
                });
    }

    public void closeMove() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closeMove();
    }
}
