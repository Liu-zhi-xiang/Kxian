package com.gjmetal.app.widget.kline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.model.spot.SpotChartValue;
import com.gjmetal.app.model.spot.SpotYDataValue;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.log.XLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Description：持仓分析
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-29 10:13
 */
@SuppressWarnings("unchecked")
public class SpotPositionAnalysisView extends View implements GestureDetector.OnGestureListener {
    private List<ChooseData> mPointDataList = new ArrayList();//数据源
    private int width;
    private int height;
    private Context context;
    private int HLineNum = 6;
    private int VLineNum = 5;
    private int mTextLeft = dp2px(getContext(), 2);//画y轴文字距离左边的距离
    private int mXTextPaddingLeft = dp2px(getContext(), 5);//画x轴时间

    private int mPaddingLeft = dp2px(getContext(), 18);//竖线距左侧距离
    private int mPaddingBottom = dp2px(getContext(), 25);
    private int mPaddingRightLeft = dp2px(getContext(), 18);
    private float lastHHeight = 0.0f;//绘制纵向的高度
    private List<String> mXDatalist = new ArrayList();//x横向轴的值
    private List<SpotYDataValue> mYDatalist = new ArrayList();//y纵向轴的值
    private List<Float> mHKDatalist = new ArrayList();//横向轴点的值

    private GetViewValue getViewValue = null;//选择的数据接口
    private ChooseData chooseData = new ChooseData();//选择的数据
    private GestureDetectorCompat gestureDetectorCompat = null;

    private boolean isLongPress = false;//长按显示十字线
    private boolean isLongPressTouch = false;//处理手势冲突
    private boolean isRefreshData = false;//是否展开刷新数据
    private float longPressx;
    private Paint showdialogpaint = new Paint();//弹框
    private Paint zhlinepaint = new Paint();//画纵横线
    private Paint xValuePaint = new Paint();//X轴时间

    private Paint mShfeInventory = new Paint();//SHFE 持仓画笔
    private Paint mLmePaint = new Paint();//LME 库存

    private Paint mPointPaint = new Paint();//画圆点 一个点
    private Paint szLinePaint = new Paint();//画十字线值
    private Paint dotsPaint = new Paint();//画圆点

    private float mTextLength;//显示的字体的长度
    private String showText[] = null;//弹出的字体  //= new String[5]
    private int mTextColor[] = {R.color.cE7EDF5, R.color.c9EB2CD};
    private int mTextSize[] = {12, 10, 13, 10, 13};//弹出的字体的大小

    private Bitmap mBitmapLogo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_app_logo);
    private boolean isMessureData = false;//只计算一次数据
    private String repeatXValue = "";//是否X轴有重复的值


    public SpotPositionAnalysisView(Context context) {
        super(context);
        init(context);
    }

    public SpotPositionAnalysisView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpotPositionAnalysisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化控件
    public void init(Context context) {
        this.context = context;
        gestureDetectorCompat = new GestureDetectorCompat(context, this);
        setZHlinePaint();
        setxValuePaint();
        setmShfeInventory();
        setmPointPaint();
        setszLinePaint();
        setdotPaint();
        setShowdialogPaint();
    }

    //设置弹框画笔画笔
    private void setShowdialogPaint() {
        showdialogpaint.setAntiAlias(true);
        showdialogpaint.setStyle(Paint.Style.FILL);
        showdialogpaint.setTextSize(dp2px(context, 12));
        showdialogpaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        showdialogpaint.setColor(ContextCompat.getColor(getContext(),R.color.c4F5490));
    }

    //设置纵横线画笔
    private void setZHlinePaint() {
        zhlinepaint.setColor(ContextCompat.getColor(getContext(),R.color.c50ffffff));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            zhlinepaint.setStrokeWidth(0.3f);
        } else {
            zhlinepaint.setStrokeWidth(1);
        }
        zhlinepaint.setAntiAlias(true);
    }

    //设置纵横的值画笔
    private void setxValuePaint() {
        //画文字
        xValuePaint.setAntiAlias(true);
        xValuePaint.setTextSize(dp2px(getContext(), 10));
        xValuePaint.setTextAlign(Paint.Align.LEFT);
        xValuePaint.setColor(ContextCompat.getColor(getContext(),R.color.c6A798E));
    }

    //设置画笔
    private void setmShfeInventory() {
        mShfeInventory.setStrokeWidth(dp2px(context, 1));
        mShfeInventory.setAntiAlias(true);
        mShfeInventory.setColor(ContextCompat.getColor(getContext(),R.color.c6774FF));
        mShfeInventory.setStyle(Paint.Style.STROKE);

        mLmePaint.setStrokeWidth(dp2px(context, 1));
        mLmePaint.setAntiAlias(true);
        mLmePaint.setColor(ContextCompat.getColor(getContext(),R.color.cEFD521));
        mLmePaint.setStyle(Paint.Style.STROKE);
    }

    //设置十字线的值画笔
    private void setszLinePaint() {
        szLinePaint.setAntiAlias(true);
        szLinePaint.setStyle(Paint.Style.STROKE);
        szLinePaint.setStrokeWidth(dp2px(context, 1.1f));
        szLinePaint.setColor(ContextCompat.getColor(getContext(),R.color.cffffff));
    }

    //设置圆点的值画笔
    private void setdotPaint() {
        dotsPaint.setStrokeWidth(2);
        dotsPaint.setAntiAlias(true);
        dotsPaint.setColor(ContextCompat.getColor(getContext(),R.color.chart_FF6600));
        dotsPaint.setStyle(Paint.Style.FILL);
    }

    //设置k线一个点  圆点的值画笔
    private void setmPointPaint() {
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(ContextCompat.getColor(getContext(),R.color.c6774FF));
        mPointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                longPressx = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isLongPressTouch = false;
                isLongPress = false;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                isLongPressTouch = false;
                isLongPress = false;
                invalidate();
                break;

        }
        if (isLongPressTouch) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() - mPaddingRightLeft * 2;
        height = getMeasuredHeight();
        lastHHeight = height + dp2px(context, 4f);//画取网格的高度与值的高度

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - mPaddingRightLeft * 2;
        height = h - mPaddingBottom;
        lastHHeight = height + dp2px(context, 4f);//画取网格的高度与值的高度
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(),R.color.c2A2D4F));
        if (isRefreshData && mPointDataList != null && mPointDataList.size() > 0) {
            if (isMessureData) {//只计算一次数据
                setUpdateKValue();
            }
            //画水印
            drawMainViewLogo(canvas);
            //画纵横线
            setDrawVHLine(canvas);
            //画纵横的值
            setDrawaVHTextValue(canvas);
            //画K线图
            setDrawXYLineValue(canvas);
            //画十字线
            setLoogLine(canvas);
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        longPressx = motionEvent.getX();
        isLongPress = true;
        isLongPressTouch = true;
        invalidate();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        motionEvent.getX();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


    //刷新数据
    public void setRefreshData(boolean isRefreshData, List<ChooseData> chartDataList) {
        this.isRefreshData = isRefreshData;
        if(ValueUtil.isListNotEmpty(mPointDataList)){
            mPointDataList.clear();
        }
        if (chartDataList != null) {
            for (ChooseData bean : chartDataList) {//过滤掉数据为空的点
                if (ValueUtil.isStrNotEmpty(bean.getLmeValue()) && bean.getLmeValue().equals("- -") && ValueUtil.isStrNotEmpty(bean.getShfeValue()) && bean.getShfeValue().equals("- -")) {
                    // mPointDataList.add(bean);
                } else {
                    mPointDataList.add(bean);
                }
            }

        }
        isMessureData = true;
        invalidate();
    }

    //k线 数据值转化屏幕值
    private void setUpdateKValue() {
        if (ValueUtil.isListEmpty(mPointDataList)) {
            return;
        }
        isMessureData = false;
        //x轴展示的值
        mXDatalist.clear();
        String publishDate[];
        int hxValue = 1;
        int num = mPointDataList.size();

        if (mPointDataList.size() > 5) {
            num = 5;
            hxValue = Math.round(mPointDataList.size() / 4);
        }
        try {
            for (int i = 0; i < num; i++) {
                if (i == 4) {
                    publishDate = mPointDataList.get(mPointDataList.size() - 1).getDateStr().split("/");
                } else {
                    publishDate = mPointDataList.get(i * hxValue).getDateStr().split("/");
                }
                mXDatalist.add(publishDate[1] + "/" + publishDate[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //y轴展示的值
        if (mPointDataList == null) {
            return;
        }
        boolean ispremium = mPointDataList.get(0).isPremium();//判断是不是升贴水
        SpotChartValue bean = getMaxAndMinValue(mPointDataList);
        float leftMax = bean.getLeftMax();
        float leftMin = bean.getLeftMin();
        float rightMax = bean.getRightMax();
        float rightMin = bean.getRightMin();

        XLog.d("RightMaxMin=", rightMax + "/" + rightMin);
        XLog.d("LeftMaxMin=", leftMax + "/" + leftMin);

        if (leftMax - leftMin != 0) {
            leftMax += Math.abs(leftMax * 0.05f);
            leftMin -= Math.abs(leftMin * 0.05f);
        }
        if (rightMax - rightMin != 0) {
            rightMax += Math.abs(rightMax * 0.05f);
            rightMin -= Math.abs(rightMin * 0.05f);
        }
        float leftTempValue = leftMax - leftMin;
        float rightTempValue = rightMax - rightMin;
        XLog.d("rightTempValue=", rightTempValue + "");
        float lefAvg = Math.abs(leftTempValue) / 4;//两点之间的间隔
        float rightAvg = Math.abs(rightTempValue) / 4;//两点之间的间隔
        XLog.d("rightAvg=", rightAvg + "");
        XLog.d("leftAvg=", lefAvg + "");
        mYDatalist.clear();
        if ((leftMax - leftMin) == 0 && (rightMax - rightMin == 0)) {//最小值与最大值之差为零
            for (int i = 0; i < 5; i++) {
                mYDatalist.add(new SpotYDataValue(setIsPremiumIntValue(ispremium, (leftMax + 2) - i), setIsPremiumIntValue(ispremium, (rightMax + 2) - i)));
            }
        } else {
            for (int i = 0; i < 5; i++) {
                if (i == 0) {
                    String leftY = null;
                    String rightY = null;
                    if (leftMax - leftMin == 0) {
                        leftY = setIsPremiumIntValue(ispremium, (leftMax + 2) - i);
                    } else {
                        leftY = setIsPremiumIntValue(ispremium, formatFloat(leftMax));
                    }
                    if (rightMax - rightMin == 0) {
                        rightY = setIsPremiumIntValue(ispremium, (rightMax + 2) - i);
                    } else {
                        rightY = setIsPremiumIntValue(ispremium, formatFloat(rightMax));
                    }
                    mYDatalist.add(new SpotYDataValue(leftY, rightY));
                } else if (i == 4) {
                    float left = 0;
                    float right = 0;
                    if (leftMax - leftMin == 0) {
                        left = (leftMax + 2) - i;
                    } else {
                        left = leftMin;
                    }
                    if (rightMax - rightMin == 0) {
                        right = (rightMax + 2) - i;
                    } else {
                        right = rightMin;
                    }
                    mYDatalist.add(new SpotYDataValue(setIsPremiumIntValue(ispremium, left), setIsPremiumIntValue(ispremium, right)));//添加最后一位值
                } else {
                    float leftY = 0;
                    float rightY = 0;
                    if (leftMax - leftMin == 0) {
                        leftY = (leftMax + 2) - i;
                    } else {
                        leftY = formatFloat(leftMax - i * lefAvg);
                    }
                    if (rightMax - rightMin == 0) {
                        rightY = (rightMax + 2) - i;
                    } else {
                        rightY = formatFloat(rightMax - i * rightAvg);
                    }
                    mYDatalist.add(new SpotYDataValue(setIsPremiumIntValue(ispremium, leftY), setIsPremiumIntValue(ispremium, rightY)));
                }
            }
        }
        //划K线的值转成屏幕值
        mHKDatalist.clear();
        //y屏幕
        for (int i = 0; i < mPointDataList.size(); i++) {
            float mShfeInventory = mPointDataList.get(i).getmShfeInventory();
            float mLme = mPointDataList.get(i).getmLmeValue();
            if (leftTempValue == 0) {
                mShfeInventory = (height - (height * (mShfeInventory - (leftMin - 2)) / ((leftMax + 2) - (leftMin - 2)))) + dp2px(context, 2.5f);
            } else {
                mShfeInventory = (height - (height * (mShfeInventory - leftMin) / leftTempValue)) + dp2px(context, 2.5f);
            }
            if (rightTempValue == 0) {
                mLme = (height - (height * (mLme - (rightMin - 2)) / ((rightMax + 2) - (rightMin - 2)))) + dp2px(context, 2.5f);
            } else {
                mLme = (height - (height * (mLme - rightMin) / rightTempValue)) + dp2px(context, 2.5f);
            }
            mPointDataList.get(i).setmYShfeInventory(mShfeInventory);
            mPointDataList.get(i).setmYLmeValue(mLme);
        }

        //x曲线宽度
        float kValueWidth;
        if (mPointDataList.size() == 1) {//一个点处理
            kValueWidth = width / 2 + mPaddingRightLeft;
            mHKDatalist.add(kValueWidth);
        } else {
            float klistSize = mPointDataList.size() - 1 + 0.0000f;
            for (int i = 0; i < mPointDataList.size(); i++) {
                kValueWidth = width / klistSize * i + mPaddingRightLeft;
                mHKDatalist.add(kValueWidth);
            }
        }
    }

    /**
     * 使用DecimalFormat来完成四舍五入，但是传入的是float类型，几轮测试才发现一个问题，传入的float会被转为double类型，大家都知道float是4位，double是8位，强转肯定会造成进度丢失。
     *
     * @param value
     * @return
     */
    public static float formatFloat(float value) {
        BigDecimal decimal = new BigDecimal(String.valueOf(value));
        DecimalFormat format = new DecimalFormat("####.####");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return Float.parseFloat(format.format(decimal.doubleValue()));
    }

    //判断是不是升贴水
    private String setIsPremiumIntValue(boolean ispremium, float yValue) {
        DecimalFormat df = new DecimalFormat("0.0000");//保留3位小数
        df.setRoundingMode(RoundingMode.HALF_UP);
        String UDValue;
        if (ispremium) {//升贴水
            if (yValue > 0) {
                UDValue = "升" + df.format(yValue);
            } else if (yValue < 0) {
                UDValue = "贴" + df.format(Math.abs(yValue));
            } else {
                UDValue = df.format(yValue);
            }
        } else {
            UDValue = df.format(yValue);
        }
        return UDValue;
    }

    // 添加视图水印
    public void drawMainViewLogo(Canvas canvas) {
        if (mBitmapLogo != null) {
            int mLeft = getWidth() / 2 - mBitmapLogo.getWidth() / 2;
            int mTop = (height - mPaddingBottom) / 2 - mBitmapLogo.getHeight() / 2;
            canvas.drawBitmap(mBitmapLogo, mLeft, mTop, null);
        }
    }

    //字体的高度
    private float setTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        return (textHeight - fm.bottom - fm.top) / 2;
    }

    //画纵横线
    private void setDrawVHLine(Canvas canvas) {
        if (height == 0 || width == 0) {
            return;
        }
        float hline = lastHHeight / HLineNum;
        for (int i = 0; i <= HLineNum; i++) {
            canvas.drawLine(mPaddingRightLeft, (hline * i), width + mPaddingRightLeft, (hline * i), zhlinepaint);
        }
        //画纵线
        float vline;
        vline = width / VLineNum;
        for (int i = 0; i <= VLineNum; i++) {
            canvas.drawLine((vline * i) + mPaddingLeft, 0, (vline * i) + mPaddingLeft, lastHHeight, zhlinepaint);
        }

    }

    //画纵横线的值
    private void setDrawaVHTextValue(Canvas canvas) {
        repeatXValue = "";
        //获取文字的高度
        float baseLine = setTextHeight(xValuePaint);
        //字体的宽度
        float textWidth = xValuePaint.measureText(mXDatalist.get(0));
        for (int i = 0; i < mXDatalist.size(); i++) {
            if (mXDatalist.size() == 1) {//一个点处理
                canvas.drawText(mXDatalist.get(i), (width / 2 - textWidth / 2), lastHHeight + mPaddingBottom / 2 + baseLine / 2, xValuePaint);//x轴的展示值
            } else {
                if (!repeatXValue.equals(mXDatalist.get(i))) {
                    canvas.drawText(mXDatalist.get(i), ((((width + mXTextPaddingLeft * mXDatalist.size()) / (mXDatalist.size() - 1) - textWidth / (mXDatalist.size() - 1)) * i)) + mXTextPaddingLeft, lastHHeight + mPaddingBottom / 2 + baseLine / 2, xValuePaint);//x轴的展示值
                }
            }
            repeatXValue = mXDatalist.get(i);
        }
        for (int i = 0; i < mYDatalist.size(); i++) {
            canvas.drawText(mYDatalist.get(i).getLeftYvalue(), mTextLeft, (float) ((lastHHeight - baseLine) / (mYDatalist.size() - 1 + 0.0000) * i + baseLine), xValuePaint);//y轴左边的展示值
            canvas.drawText(mYDatalist.get(i).getRightYvalue(), width - mTextLeft, (float) ((lastHHeight - baseLine) / (mYDatalist.size() - 1 + 0.0000) * i + baseLine), xValuePaint);//y轴右边的展示值
        }
    }


    //画贝塞尔曲线
    private void setDrawXYLineValue(Canvas canvas) {
        if (mPointDataList.size() == 1) {
            canvas.drawCircle(mHKDatalist.get(0), mPointDataList.get(0).getmYShfeInventory(), dp2px(context, 5f), mPointPaint);//画圆点线
            canvas.drawCircle(mHKDatalist.get(0), mPointDataList.get(0).getmYLmeValue(), dp2px(context, 5f), mPointPaint);//画圆点线
        } else {
            Path inventoryPath = new Path();
            Path lmePath = new Path();
            inventoryPath.moveTo(mHKDatalist.get(0), mPointDataList.get(0).getmYShfeInventory());
            lmePath.moveTo(mHKDatalist.get(0), mPointDataList.get(0).getmYLmeValue());
            PointF p1 = new PointF();
            PointF p2 = new PointF();
            PointF p3 = new PointF();
            PointF p4 = new PointF();

            for (int i = 0; i < mPointDataList.size() - 1; i++) {
                //SHFE
                String shfeValue = mPointDataList.get(i).getShfeValue();
                String shfeValue2 = mPointDataList.get(i + 1).getShfeValue();
                p1.x = (mHKDatalist.get(i) + mHKDatalist.get(i + 1)) / 2;
                p2.x = (mHKDatalist.get(i) + mHKDatalist.get(i + 1)) / 2;
                if (ValueUtil.isStrNotEmpty(shfeValue) && !shfeValue.equals("- -")) {
                    p1.y = mPointDataList.get(i).getmYShfeInventory();
                }
                if (ValueUtil.isStrNotEmpty(shfeValue2) && !shfeValue2.equals("- -")) {
                    p2.y = mPointDataList.get(i + 1).getmYShfeInventory();
                    inventoryPath.cubicTo(p1.x, p1.y, p2.x, p2.y, mHKDatalist.get(i + 1), mPointDataList.get(i + 1).getmYShfeInventory());
                }
                //LME
                String lmeValue = mPointDataList.get(i).getLmeValue();
                String lmeValue2 = mPointDataList.get(i + 1).getLmeValue();
                p4.x = (mHKDatalist.get(i) + mHKDatalist.get(i + 1)) / 2;
                p3.x = (mHKDatalist.get(i) + mHKDatalist.get(i + 1)) / 2;
                if (ValueUtil.isStrNotEmpty(lmeValue) && !lmeValue.equals("- -")) {
                    p3.y = mPointDataList.get(i).getmYLmeValue();
                }
                if (ValueUtil.isStrNotEmpty(lmeValue2) && !lmeValue2.equals("- -")) {
                    p4.y = mPointDataList.get(i + 1).getmYLmeValue();
                    lmePath.cubicTo(p3.x, p3.y, p4.x, p4.y, mHKDatalist.get(i + 1), mPointDataList.get(i + 1).getmYLmeValue());
                }
            }
            canvas.drawPath(inventoryPath, mShfeInventory);
            canvas.drawPath(lmePath, mLmePaint);
        }
    }

    //画弹框
    private void setLoogLine(Canvas canvas) {
        if (isLongPress) {
            float distHalf;//两点之间的一半
            float klistSize = (float) (mHKDatalist.size() - 1 + 0.0000);
            if (klistSize == 0.00) {
                klistSize = (float) (mHKDatalist.size() + 0.0000);
            }
            distHalf = (float) (width / klistSize / 2.0);
            for (int i = 0; i < mHKDatalist.size(); i++) {
                if ((longPressx - distHalf) <= mHKDatalist.get(i) && (longPressx + distHalf) > mHKDatalist.get(i)) {
                    canvas.drawLine(mHKDatalist.get(i), 0, mHKDatalist.get(i), lastHHeight, szLinePaint);//画实线
                    String date = mPointDataList.get(i).getDateStr();
                    String shfeValue = mPointDataList.get(i).getShfeValue();
                    String lmeValue = mPointDataList.get(i).getLmeValue();
                    if (ValueUtil.isStrNotEmpty(shfeValue) && !shfeValue.equals("- -") && ValueUtil.isStrNotEmpty(lmeValue) && !lmeValue.equals("- -")) {
                        showText = new String[5];
                        showText[0] = date;
                        showText[1] = mPointDataList.get(i).getShfeName();
                        showText[2] = shfeValue;
                        showText[3] = mPointDataList.get(i).getLmeName();
                        showText[4] = lmeValue;
                    } else if (ValueUtil.isStrNotEmpty(shfeValue) && !shfeValue.equals("- -") && ValueUtil.isStrNotEmpty(lmeValue) && lmeValue.equals("- -")) {
                        showText = new String[3];
                        showText[0] = date;
                        showText[1] = mPointDataList.get(i).getShfeName();
                        showText[2] = shfeValue;
                    } else if (ValueUtil.isStrNotEmpty(shfeValue) && shfeValue.equals("- -") && ValueUtil.isStrNotEmpty(lmeValue) && !lmeValue.equals("- -")) {
                        showText = new String[3];
                        showText[0] = date;
                        showText[1] = mPointDataList.get(i).getLmeName();
                        showText[2] = lmeValue;
                    }

                }
            }

            //画弹框
            showdialogpaint.setTextSize(dp2px(context, 12));
            showdialogpaint.setColor(ContextCompat.getColor(getContext(),R.color.c4F5490));
            float left = 0;
            float htext = dp2px(context, 15);
//            //获取字体宽的最大值长度 和总得字体的高度

            if(showText==null){
                return;
            }
            for (int i = 0; i < showText.length; i++) {
                showdialogpaint.setTextSize(dp2px(context, mTextSize[i]));
                if (showText[i] != null && showText[i].length() > 0) {
                    mTextLength = Math.max(mTextLength, showdialogpaint.measureText(showText[i]));
                    htext = htext + setTextHeight(showdialogpaint) + dp2px(context, 5);
                }
            }
            //左右显示弹框的宽度与高度
            float right;
            if (longPressx > width / 2) {
                left = dp2px(context, 10);
                right = left + mTextLength + dp2px(context, 20);
            } else {
                left = width - mTextLength + dp2px(context, 10);
                right = left + mTextLength + dp2px(context, 20);
            }
            float bottom = htext - dp2px(context, 9);
            RectF rectF = new RectF(left, dp2px(context, 5), right, bottom);
            canvas.drawRoundRect(rectF, 10, 10, showdialogpaint);
            //画弹框数据
            htext = dp2px(context, 15);
            for (int i = 0; i < showText.length; i++) {
                if (i % 2 == 0) {
                    showdialogpaint.setColor(ContextCompat.getColor(getContext(),mTextColor[0]));
                } else {
                    showdialogpaint.setColor(ContextCompat.getColor(getContext(),mTextColor[1]));
                }
                showdialogpaint.setTextSize(dp2px(context, mTextSize[i]));
                if (showText[i] != null && showText[i].length() > 0) {
                    htext = htext + setTextHeight(showdialogpaint) + dp2px(context, 3.5f);
                    canvas.drawText(showText[i], rectF.left + (rectF.width() - mTextLength) / 2, htext - dp2px(context, 10), showdialogpaint);
                }
                if (getViewValue != null) {
                    getViewValue.setGetViewValue(chooseData, isLongPress, isLongPressTouch);
                }
            }
        } else {
            if (getViewValue != null) {
                getViewValue.setGetViewValue(new ChooseData(), isLongPress, isLongPressTouch);//取消现货列表的控件显示隐藏
            }
        }
    }

    //画多少横线
    public void setHLineNum(int HLineNum) {
        this.HLineNum = HLineNum;
    }

    //画多少纵线
    public void setVLineNum(int VLineNum) {
        this.VLineNum = VLineNum;
    }

    //dp转px
    public int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setGetViewValue(GetViewValue getViewValue) {
        this.getViewValue = getViewValue;
    }

    public interface GetViewValue {
        void setGetViewValue(ChooseData chooseData, boolean isLongPress, boolean isLongPressTouch);
    }

    //判断是不是长按
    public boolean getLongPress() {
        return isLongPress;
    }

    public void setLongPress(boolean isLongPress) {
        this.isLongPress = isLongPress;
    }

    /**
     * 获取Y轴最值
     *
     * @return
     */
    public SpotChartValue getMaxAndMinValue(List<ChooseData> arryList) {
        SpotChartValue spotChartValue = new SpotChartValue();
        if (arryList == null) {
            return null;
        }
        float leftMax = mPointDataList.get(0).getmShfeInventory();//最大值
        float leftMin = leftMax;//最小值


        float rightMax = mPointDataList.get(0).getmLmeValue();//最大值
        float rightMin = rightMax;//最小值

        for (int i = 0; i < mPointDataList.size(); i++) {
            float leftCenter = mPointDataList.get(i).getmShfeInventory();
            if (leftCenter > leftMax) {
                leftMax = leftCenter;
            }
            if (leftCenter < leftMin) {
                leftMin = leftCenter;
            }
            float rightCenter = mPointDataList.get(i).getmLmeValue();
            if (rightCenter > rightMax) {
                rightMax = rightCenter;
            }
            if (rightCenter < rightMin) {
                rightMin = rightCenter;
            }
        }
        spotChartValue.setLeftMax(leftMax);
        spotChartValue.setLeftMin(leftMin);
        spotChartValue.setRightMax(rightMax);
        spotChartValue.setRightMin(rightMin);
        return spotChartValue;
    }
}
