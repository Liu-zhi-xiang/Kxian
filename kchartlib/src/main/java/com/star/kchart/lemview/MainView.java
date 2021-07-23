package com.star.kchart.lemview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.star.kchart.R;

import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.IGroupDraw;
import com.star.kchart.comInterface.ILem;
import com.star.kchart.utils.DensityUtil;
import com.star.kchart.utils.DisplayUtil;
import com.star.kchart.utils.StrUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainView extends BaseView implements IGroupDraw<ILem> {
    private Context mContext;
    private int screenWidth = 0;
    private int screenHeight = 0;

    protected int mBackgroundColor;
    protected Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //文字
    private Paint mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //圆点
    private Paint mDotLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //圆点
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //线
    private Paint mImaginaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //虚线
    private Paint mColumnPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //柱子
    private Paint mTextLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //注释文字

    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mCurveMin; //Curve最小值
    private float mCurveMax; //Curve最大值

    private float mVolumeMin; //Volume最小值
    private float mVolumeMax; //Volume最大值

    private float mCurveScaleY = 1; //Y轴单位量
    private float mVolumeScaleY = 1; //Y轴单位量
    private float mScaleX = 1; //x轴的单位量
    private int mGridLeftRows = 5; //  左行数
    private float mColumnPadding = 10;
    private float mColumnWidth; //单位宽度
    private String dateStr = "变动量";

    private final List<ILem> mPoints = new ArrayList<>();
    protected long mPointCount = 0; //点的个数
    private int selectedIndex = -1;
    private int[] volume;

    private ChildView mChildView;


    public MainView(Context context) {
        super(context);
        initView(context);
    }

    public MainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        this.mContext = context;
        mTopPadding=2;
        mTopPadding = DisplayUtil.dip2px(mContext, mTopPadding);
        mBackgroundColor = Color.parseColor("#402A2D4F");
        mBackgroundPaint.setColor(mBackgroundColor);

        mDotPaint.setStyle(Paint.Style.FILL);   //圆点

        mDotLeftPaint.setStyle(Paint.Style.FILL);
        mDotLeftPaint.setStrokeWidth(dp2px(4));

        mTextPaint.setTextSize(dp2px(10)); //文字

        mTextLeftPaint.setDither(true);//注释文字
        mTextLeftPaint.setColor(Color.parseColor("#B1B2B6"));
        mTextLeftPaint.setTextSize(dp2px(13));
//        mTextLeftPaint.setStrokeWidth(dp2px(0.5f));



        mSelectorBackgroundPaint.setDither(true);
        mSelectorBackgroundPaint.setColor(Color.parseColor("#4F5490"));



        mLinePaint.setStrokeWidth(dp2px(1)); //线

        mGridPaint.setColor(Color.parseColor("#15FFFFFF")); //网格线颜色
        mGridPaint.setStrokeWidth(dp2px(1));

        mImaginaryLinePaint.setColor(getColor(R.color.c67D9FF)); //虚线
        mImaginaryLinePaint.setStrokeWidth(dp2px(2));
        mImaginaryLinePaint.setStyle(Paint.Style.STROKE);
        mImaginaryLinePaint.setDither(true);
        DashPathEffect pathEffect = new DashPathEffect(new float[]{15, 15}, 1); //设置虚线
        mImaginaryLinePaint.setPathEffect(pathEffect);

        //获取宽高
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }

    public void initData(Collection<? extends ILem> datas, ChildView childView) {
//        this.selectedIndex = 0;
        mPoints.clear();
        if (datas != null) {
            this.mPoints.addAll(datas);
            mPointCount = mPoints.size();
        }
        mChildView = childView;
        notifyChanged();
    }

    @Override
    protected void notifyChanged() {
        if (mPoints.size() <= 0) {
            invalidate();
            return;
        }
        if (mPoints.size() > 0) {
            mCurveMax = mPoints.get(0).getCurve(); //线
            mCurveMin = mPoints.get(0).getCurve();

            mVolumeMax = mPoints.get(0).getVolume(); //柱子
            mVolumeMin = mPoints.get(0).getVolume();
        }

        for (int i = 0; i < mPoints.size(); i++) {
            ILem point = mPoints.get(i);
            mCurveMax = Math.max(mCurveMax, point.getCurve());
            mCurveMin = Math.min(mCurveMin, point.getCurve());

            mVolumeMax = Math.max(mVolumeMax, point.getVolume());
            mVolumeMin = Math.min(mVolumeMin, point.getVolume());
        }

        //y Curve 轴的缩放值
        mCurveMax = StrUtil.getAndOnePositiveNumber(mCurveMax);
        if (mCurveMax > -100 && mCurveMax < 0) {
            mCurveMax = 0;
        } else {
            mCurveMax = StrUtil.getLemMultipleMinimum(mCurveMax, mGridLeftRows);
        }

        mCurveMin = StrUtil.getAndOnePositiveNumber(mCurveMin) - 1;
        if (mCurveMin < 100 && mCurveMin > 0) {
            mCurveMin = 0;
        } else {
            if (mCurveMin > 0) {
                mCurveMin = StrUtil.getLemMultipleMinimum(mCurveMin, mGridLeftRows) - 5;
            } else {
                mCurveMin = StrUtil.getLemMultipleMinimum(mCurveMin, mGridLeftRows);
            }
        }
        mCurveScaleY = mBaseHeight / Math.abs(mCurveMax - mCurveMin);

        //y Volume 轴的缩放值
        if (mVolumeMax == mVolumeMin) {
            if (mVolumeMax < 0 && mVolumeMin < 0) {
                mVolumeMax = 0;

            } else if (mVolumeMax > 0 && mVolumeMin > 0) {
                mVolumeMin = 0;

            }
        }
        volume = StrUtil.getLemRightValueTwo(mVolumeMax, mVolumeMin);
        mVolumeMax = volume[0];
        mVolumeMin = volume[1];
        mGridRows = volume[2];
        mVolumeScaleY = mBaseHeight / Math.abs(mVolumeMax - mVolumeMin);

        //x轴的缩放值
        mScaleX = (mBaseWidth - mBasePaddingRight - mBasePaddingLeft - 2 * mColumnPadding) / mPointCount;

//        mColumnWidth = mWidth / getMaxPointCount();
        mColumnWidth = dp2px(18);
        mColumnPaint.setStrokeWidth(mColumnWidth * 0.5f); //柱子

        if (mPoints.size() > 0 && selectedIndex > -1 && selectedIndex < mPoints.size()&&isLongPress) {
            mChildView.initData(selectedIndex, mColumnWidth, mCurveMax, mScaleX, mCurveScaleY, mPoints.get(selectedIndex));
        }

        invalidate();
    }

    @Override
    protected void resetChildCiew() {
        if (mChildView!=null){
            mChildView.setSelectedIndex(-1);
        }

    }

    @Override
    protected void calculateSelectedX(float x) {
//        Log.i("---> ； x ：", x + "");
        selectedIndex = (int) (x * 1f / getX(mPoints.size() - 1) * (mPoints.size() - 1) + 0.5f);
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }
        if (selectedIndex > mPoints.size() - 1) {
            selectedIndex = mPoints.size() - 1;
        }
        if (mPoints.size() > 0 && selectedIndex > -1 && selectedIndex < mPoints.size()) {
            mChildView.initData(selectedIndex, mColumnWidth, mCurveMax, mScaleX, mCurveScaleY, mPoints.get(selectedIndex));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景颜色
        canvas.drawColor(mBackgroundColor);
        drawGird(canvas); //绘制网格线
//        drawDefTopTxtpaint(canvas); //画上面的小圆点
//        drawOneTopTxtpaint(canvas);//画上面的小圆点
        if (mChildView == null) {
            return;
        }
        if (mPoints == null || mPoints.size() <= 0 || mWidth == 0 || mHeight == 0) {
            mChildView.initData(-1, mColumnWidth, mCurveMax, mScaleX, mCurveScaleY, null);
            return;
        }
        if (selectedIndex>-1&&isLongPress) {
            mChildView.initData(selectedIndex, mColumnWidth, mCurveMax, mScaleX, mCurveScaleY, mPoints.get(selectedIndex));
        }

//        drawColumn(canvas);  //绘制柱子
        drawLine(canvas); //绘制线

        drawText(canvas); //绘制文字
        if (isLongPress || !isClosePress) {
            ILem point = mPoints.get(selectedIndex);
            drawMainSelector(selectedIndex, point, canvas);
        }
    }

    private void drawOneTopTxtpaint(Canvas canvas) {
        //先画文字
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(getColor(R.color.c9EB2CD));
        float r = dp2px(4);
        mDotPaint.setColor(getColor(R.color.c67D9FF));
        canvas.drawCircle(mBasePaddingLeft + r / 2, mTopPadding / 2 + r / 2, r, mDotPaint);

        //先画文字
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(getColor(R.color.c9EB2CD));
        String hintTxt = "最新报价";
        canvas.drawText(hintTxt, r + mBasePaddingLeft + 10,
                mTopPadding / 2 + getFontBaseLineHeight(mTextPaint) / 2, mTextPaint);
    }

    private void drawDefTopTxtpaint(Canvas canvas) {
        //红色小圆点
        int w = dp2px(8);
        mDotLeftPaint.setColor(getColor(R.color.cF27A68));
        canvas.drawLine(mBasePaddingLeft, mTopPadding / 2, mBasePaddingLeft + w,
                mTopPadding / 2, mDotLeftPaint);
        mDotLeftPaint.setColor(getColor(R.color.c3EB86A));
        canvas.drawLine(mBasePaddingLeft, mTopPadding / 2 + w / 2, mBasePaddingLeft + w,
                mTopPadding / 2 + w / 2, mDotLeftPaint);


        //先画文字
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(getColor(R.color.c9EB2CD));

        canvas.drawText(dateStr, mBasePaddingLeft + w + 10,
                mTopPadding / 2 + getFontBaseLineHeight(mTextPaint) / 2, mTextPaint);

        float r = dp2px(4);
        float mWidth = mTextPaint.measureText(dateStr) + mBasePaddingLeft + dp2px(20);
        mDotPaint.setColor(getColor(R.color.c67D9FF));
        canvas.drawCircle(mWidth + r / 2, mTopPadding / 2 + r / 2, r, mDotPaint);

        //先画文字
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(getColor(R.color.c9EB2CD));
        String hintTxt = "最新报价";
        canvas.drawText(hintTxt, r + mWidth + 10,
                mTopPadding / 2 + getFontBaseLineHeight(mTextPaint) / 2, mTextPaint);

    }

    /**
     * draw选择器
     *
     * @param canvas
     */
    private void drawMainSelector(int selectedIndex, ILem point, Canvas canvas) {
        Paint.FontMetrics metrics = mTextLeftPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        float padding = DensityUtil.dp2px(5);
        float margin = DensityUtil.dp2px(5);
        float width = 0;
        float left = 5;
        float top =10 ;
        float bottom = 10;

        List<String> strings = new ArrayList<>();
        if (TextUtils.isEmpty(point.getDate())){
            strings.add("未知数据");
        }else {
            strings.add(point.getDate());
        }
        strings.add("绝对价");

        if (point.getCurve()!=-1) {
            strings.add(point.getCurve()+"");
        }else {
            strings.add("--");
        }

        for (int i = 0; i < strings.size(); i++) {
            width = Math.max(width, mTextLeftPaint.measureText(strings.get(i)));
        }
        width = width + dp2px(10) ;

        float x = getX(selectedIndex);
        if (x > mWidth / 2) {
            left = margin;
        } else {
            left = mWidth - width - margin;
        }
        float height =padding * 2 + (textHeight - metrics.bottom - metrics.top) / 2 +
                (textHeight + padding/2) * (strings.size() - 1);

        RectF r = new RectF(left, top, left + width,  height );
        canvas.drawRoundRect(r, padding, padding, mSelectorBackgroundPaint);

        float y =  padding  + (textHeight - metrics.bottom - metrics.top) / 2;

        for (int j=0;j<strings.size();j++){
            if (j==0) {
                mTextLeftPaint.setTextSize(dp2px(12));
                mTextLeftPaint.setColor(getColor(R.color.cE7EDF5));
            }else if (j==1){
                mTextLeftPaint.setTextSize(dp2px(10));
                mTextLeftPaint.setColor(getResources().getColor(R.color.c9EB2CD));
            }else {
                mTextLeftPaint.setTextSize(dp2px(13));
                if (point.getVolume()>0){
                    mTextLeftPaint.setColor(getColor(R.color.color_positive_value));
                }else if (point.getVolume()<0){
                    mTextLeftPaint.setColor(getColor(R.color.c3EB86A));
                }else {
                    mTextLeftPaint.setColor(getColor(R.color.cE7EDF5));
                }
            }
            canvas.drawText(strings.get(j), left + padding, y, mTextLeftPaint);
            y += textHeight + padding/2;
        }


    }

    //绘制网格线
    private void drawGird(Canvas canvas) {
//        canvas.drawLine(0, 0, mWidth, 0, mGridPaint); //顶部线
//        canvas.drawLine(0, mHeight, mWidth, mHeight, mGridPaint);//底部线

        if (volume == null || volume.length < 1) {
            return;
        }
        //横向的grid
        if (mGridRows != 0) {
            float rowSpace = mBaseHeight / mGridRows;
            for (int i = 0; i <= mGridRows; i++) {
//                if ((volume[0] - volume[3] * i) == 0 || i == 0 || i == volume[2]) {
                if ( i == 0 || i == volume[2]) {
                    canvas.drawLine(0, mTopPadding + rowSpace * i, mBaseWidth,
                            mTopPadding + rowSpace * i, mGridPaint);
                }
            }
        } else {
            canvas.drawLine(0, mTopPadding, mBaseWidth, mTopPadding, mGridPaint);
            canvas.drawLine(0, mTopPadding + mBaseHeight, mBaseWidth,
                    mTopPadding + mBaseHeight, mGridPaint);
        }

        //纵向的grid
        if (mGridColumns != 0) {
            float columnSpace = (mBaseWidth - mBasePaddingLeft - mBasePaddingRight) / mGridColumns;
            for (int i = 0; i <= mGridColumns; i++) {
                canvas.drawLine(columnSpace * i + mBasePaddingLeft, mTopPadding,
                        columnSpace * i + mBasePaddingLeft, mHeight - mBottomPadding, mGridPaint);
            }
        }

    }


    private void drawColumn(Canvas canvas) {
        //画柱子
        for (int i = 0; i < mPoints.size(); i++) {
            if (mPoints.get(i).getVolume() > 0) {
                mColumnPaint.setColor(getColor(R.color.cF27A68));
            } else if (mPoints.get(i).getVolume() < 0) {
                mColumnPaint.setColor(getColor(R.color.c3EB86A));
            }
            canvas.drawLine(getX(i),
                    getVoluemY(0),
                    getX(i),
                    getVoluemY(mPoints.get(i).getVolume()),
                    mColumnPaint);

        }

    }


    private void drawText(Canvas canvas) {
        try {
        float baseLine = getFontBaseLineHeight(mTextPaint);
        float textHeight = getFontHeight(mTextPaint);

        float rowValue = StrUtil.getAndOnePositiveNumber((mCurveMax - mCurveMin) / (mGridLeftRows - 1));
        float rowSpace = mBaseHeight / mGridLeftRows;

        //画左边的值
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(getResources().getColor(R.color.c6A798E));
        canvas.drawText(StrUtil.getPositiveNumber(mCurveMax), mBaseTextPaddingLeft, baseLine + mTopPadding, mTextPaint);
        canvas.drawText(StrUtil.getPositiveNumber(mCurveMin), mBaseTextPaddingLeft,
                mHeight - mBottomPadding - textHeight + baseLine, mTextPaint);
        for (int i = 1; i < mGridLeftRows - 1; i++) {
            String text = null;
            if (mCurveMin >= 0) {
                text = StrUtil.getPositiveNumber(mCurveMin + rowValue * (mGridLeftRows - 1 - i));
            } else if (mCurveMax <= 0) {
                text = StrUtil.getPositiveNumber(mCurveMax - rowValue * i);
            } else if (Math.abs(mCurveMin - 0) < Math.abs(mCurveMax - 0)) {
                text = StrUtil.getPositiveNumber(mCurveMin + rowValue * (mGridLeftRows - 1 - i));
            } else if (Math.abs(mCurveMin - 0) > Math.abs(mCurveMax - 0)) {
                text = StrUtil.getPositiveNumber(mCurveMax - rowValue * i);
            } else {
                text = StrUtil.getPositiveNumber(mCurveMin + rowValue * (mGridLeftRows - 1 - i));
            }
            if (i < mGridLeftRows / 2) {
                canvas.drawText(text, mBaseTextPaddingLeft,
                        rowSpace * i + baseLine + mTopPadding, mTextPaint);

            } else if (i == mGridLeftRows / 2) {
                canvas.drawText(text, mBaseTextPaddingLeft,
                        rowSpace * i + rowSpace / 2 + baseLine / 2 + mTopPadding, mTextPaint);

            } else if (i > mGridLeftRows / 2) {
                canvas.drawText(text, mBaseTextPaddingLeft,
                        mHeight - mBottomPadding - textHeight + baseLine - rowSpace * (mGridLeftRows - i - 1), mTextPaint);

            }
        }


        //画右边的值
//        int count = volume[2];
//        float rowVolume = volume[3];
//        float rowVolumeSpace = (mBaseHeight - baseLine) / count;
//        mTextPaint.setTextAlign(Paint.Align.RIGHT);
//        mTextPaint.setColor(getResources().getColor(R.color.c9EB2CD));
//        canvas.drawText(StrUtil.getPositiveNumber(mVolumeMin), mBaseWidth - mBaseTextPaddingRight,
//                mBaseHeight + mTopPadding, mTextPaint);
//        for (int i = 0; i < count; i++) {
//            String text = StrUtil.getPositiveNumber(mVolumeMax - rowVolume * i);
//            canvas.drawText(text, mBaseWidth - mBaseTextPaddingRight,
//                    rowVolumeSpace * i + baseLine + mTopPadding, mTextPaint);
//        }

        //画时间
        mTextPaint.setColor(getResources().getColor(R.color.c6A798E));
        float y = mBaseHeight + mTopPadding;
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < mPoints.size(); i++) {
            canvas.save();
            canvas.rotate(270, getX(i) + baseLine / 2, y); //默认以原点旋转，所以的设置旋转点
            canvas.drawText(mPoints.get(i).getDate(),
                    getX(i), y, mTextPaint); //时间 ?? 存在和柱子对齐的问题
            canvas.restore();
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void drawLine(Canvas canvas) {
        try {
        //虚线
        Path path = new Path();
        path.reset();
        path.moveTo(getX(0), getCurveY(mPoints.get(0).getCurve()));
//        for (int i = 1; i < mPoints.size(); i++) {
//            path.lineTo(getX(i), getCurveY(mPoints.get(i).getValue()));
//            canvas.drawPath(path, mImaginaryLinePaint);
//        }
        drawScrollLine(canvas, path);

        //小圆点线
        mDotPaint.setColor(getColor(R.color.c67D9FF));
        float r = mColumnWidth * 0.5f / 2;
        for (int i = 0; i < mPoints.size(); i++) {
            canvas.drawCircle(getX(i),
                    getCurveY(mPoints.get(i).getCurve()),
                    r, mDotPaint);

        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //三级贝塞尔曲线
    private void drawScrollLine(Canvas canvas, Path path) {
        for (int i = 0; i < mPoints.size() - 1; i++) {
            float wt = (getX(i) + getX(i + 1)) / 2;
            PointF p3 = new PointF();
            PointF p4 = new PointF();
            p3.y = getCurveY(mPoints.get(i).getCurve());
            p3.x = wt;

            p4.y = getCurveY(mPoints.get(i + 1).getCurve());
            p4.x = wt;

            path.cubicTo(p3.x, p3.y, p4.x, p4.y, getX(i + 1), getCurveY(mPoints.get(i + 1).getCurve()));
            canvas.drawPath(path, mImaginaryLinePaint);
        }
    }


    /**
     * 获取最大能有多少个点
     */
    public long getMaxPointCount() {
        return mPoints.size();
    }

    private float getColumeHeight(float value) {
        return (value - mCurveMin) * mCurveScaleY;
    }

    private float getX(int i) {
        return mBasePaddingLeft + mColumnPadding + mScaleX * i + mColumnWidth / 2;
    }

    /**
     * 修正y值
     */
    private float getCurveY(float value) {
        return mTopPadding + (mCurveMax - value) * mCurveScaleY;
    }

    private float getVoluemY(float value) {
        return mTopPadding + (mVolumeMax - value) * mVolumeScaleY;
    }

    public void setDateStr(String dateStr) {
        if (dateStr != null || dateStr != "") {
            this.dateStr = dateStr;
        }
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

    //设置指示线的位置
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

}
