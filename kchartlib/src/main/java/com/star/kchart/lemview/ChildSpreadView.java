package com.star.kchart.lemview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.star.kchart.R;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.IGroupDraw;
import com.star.kchart.comInterface.ILem;

import static com.star.kchart.utils.DensityUtil.dp2px;

public class ChildSpreadView extends View implements IGroupDraw<ILem> {
    private Context mContext;


    private int mBasePaddingLeft = 50;

    private int mTopPadding = dp2px(30); //据顶部
    protected int mBottomPadding = dp2px(55);//距底部
    private float mColumnPadding = 10;

    private int mBackgroundColor;
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDotRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //圆点
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //线

    private ILem mILem;
    private int selectedIndex = -1;
    private float mColumnWidth;
    private float mCurveMax; //Curve最大值
    private float mCurveScaleY = 1; //Y轴单位量
    private float mScaleX = 1; //x轴的单位量

    private ClickLmePointListener mClickLmePointListener;


    public ChildSpreadView(Context context) {
        super(context);
        initView(context);
    }

    public ChildSpreadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChildSpreadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        mBackgroundColor = Color.parseColor("#00000000");
        mBackgroundPaint.setColor(mBackgroundColor);

        mGridPaint.setStrokeWidth(getDimension(R.dimen.chart_grid_line_width));
        mGridPaint.setColor(getColor(R.color.chart_green));


        mDotRingPaint.setStrokeWidth(dp2px(2)); //圆弧
        mDotRingPaint.setStyle(Paint.Style.STROKE);
        mDotRingPaint.setColor(getColor(R.color.c6774FF));

    }

    public void initData(int selectedIndex, float columnWidth, float curveMax, float scaleX,
                         float curveScaleY, ILem lem) {
        this.selectedIndex = selectedIndex;
        this.mColumnWidth = columnWidth;
        this.mCurveMax = curveMax;
        this.mILem = lem;
        this.mCurveScaleY = curveScaleY;
        this.mScaleX = scaleX;
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景颜色
        canvas.drawColor(mBackgroundColor);

        if (mILem == null || selectedIndex == -1) {
            drawSelector(selectedIndex);
            return;
        }

        drawLongClickLine(canvas); //长按指示线


    }


    private void drawLongClickLine(Canvas canvas) {

        float r = mColumnWidth * 0.5f / 2;
        canvas.drawCircle(getX(selectedIndex), getCurveY(mILem.getOrLast()),
                r, mDotRingPaint);

        //画指示线
        if (selectedIndex < 0) return;
        mLinePaint.setColor(getColor(R.color.c6774FF));
        mLinePaint.setStrokeWidth(dp2px(1));
        float x = getX(selectedIndex);
        //轴线

//        canvas.drawLine(x, mTopPadding, x, mHeight - mBottomPadding, mLinePaint);//Y
        if (mTopPadding < (getCurveY(mILem.getOrLast()) - mColumnWidth * 0.25f)) {
            canvas.drawLine(x, mTopPadding, x,
                    getCurveY(mILem.getOrLast()) - mColumnWidth * 0.25f,
                    mLinePaint);
        }
        if ((getCurveY(mILem.getOrLast()) + mColumnWidth * 0.25f) < (this.getHeight() - mBottomPadding)) {
            canvas.drawLine(x, getCurveY(mILem.getOrLast()) + mColumnWidth * 0.25f,
                    x, this.getHeight() - mBottomPadding, mLinePaint);
        }
        drawSelector(selectedIndex);

    }

    /**
     * draw选择器
     *
     */
    private void drawSelector(int selectedIndex) {
        if (mClickLmePointListener != null) {
            mClickLmePointListener.onClickPointListener(selectedIndex, mILem);
        }
        /**
         *  以后添加弹框（待开发）
         */
    }


    /**
     * 修正y值
     */
    private float getCurveY(float value) {
        return mTopPadding + (mCurveMax - value) * mCurveScaleY;
    }

    private float getX(int i) {
        return mBasePaddingLeft + mColumnPadding + mScaleX * i + mColumnWidth / 2;
    }

    //设置指示线的位置
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        invalidate();
    }


    public float getDimension(@DimenRes int resId) {
        return getResources().getDimension(resId);
    }

    public int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }


    @Override
    public void drawTranslated(@Nullable ILem lastPoint, @NonNull ILem curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseGroupView view, int position) {

    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseGroupView view, int position, float x, float y) {

    }

    @Override
    public float getMaxValue(ILem point) {
        return 0;
    }

    @Override
    public float getMinValue(ILem point) {
        return 0;
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return null;
    }

    @Override
    public void setTargetColor(int... color) {

    }

    @Override
    public void setOnClickPointListener(ILem point) {

    }



    public void setOnClickPointListener(ClickLmePointListener listener) {
        if (listener != null) {
            mClickLmePointListener = listener;
        }
    }

    public interface ClickLmePointListener {
        void onClickPointListener(int postion, ILem mILem);
    }

}
