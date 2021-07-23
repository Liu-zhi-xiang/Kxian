package com.gjmetal.app.widget.kline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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
import com.gjmetal.app.model.spot.SpotPriceValue;
import com.gjmetal.star.log.XLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Description：现货报价
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-29 10:12
 */
@SuppressWarnings("unchecked")
public class SpotPriceView extends View implements GestureDetector.OnGestureListener {
    private List<ChooseData> klistBaseModel = new ArrayList();//k数据
    private int width;
    private int height;
    private Context context;
    private int HLineNum = 6;
    private int VLineNum = 5;
    private int mTextLeft = dp2px(getContext(), 2);//画y轴文字距离左边的距离
    private int mPaddingLeft = dp2px(getContext(), 18);
    private int mPaddingRight = dp2px(getContext(), 18);
    private int mPaddingBottom = dp2px(getContext(), 25);
    private int mPaddingRightLeft = dp2px(getContext(), 0);
    private float lastHHeight = 0.0f;//绘制纵向的高度
    private List<String> mXDatalist = new ArrayList();//x横向轴的值
    private List<String> mYDatalist = new ArrayList();//y纵向轴的值
    private List<Float> mHKDatalist = new ArrayList();//横向轴点的值
    private List<SpotPriceValue> mVKDatalist = new ArrayList();//纵向轴点的值
    private GetViewValue getViewValue = null;//选择的数据接口
    private ChooseData chooseData = new ChooseData();//选择的数据
    private GestureDetectorCompat gestureDetectorCompat = null;

    private boolean isLongPress = false;//长按显示十字线
    private boolean isLongPressTouch = false;//处理手势冲突
    private boolean isRefreshData = false;//是否展开刷新数据
    private float loogPressx;
    private Paint showdialogpaint = new Paint();//弹框
    private Paint zhlinepaint = new Paint();//画纵横线
    private Paint zhLineValuePaint = new Paint();//画纵横线值
    private Paint kLineValuePaint = new Paint();//画k线值

    private Paint KlinedotsPaint = new Paint();//画圆点 一个点
    private Paint szLinePaint = new Paint();//画十字线值
    private Paint dotsPaint = new Paint();//画圆点
    private int kWidth;//K线轴的宽度
    private float kHeight;//K线轴的高度
    private Bitmap mBitmapLogo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_app_logo);
    private boolean isMessureData = false;//只计算一次数据
    private String repeatXValue = "";//是否X轴有重复的值
    private float tempValue;
    private float Vmax,Vmin;
    private int isFirstTouch=0;
    public SpotPriceView(Context context) {
        super(context);
        init(context);
    }

    public SpotPriceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpotPriceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化控件
    public void init(Context context) {
        this.context = context;
        gestureDetectorCompat = new GestureDetectorCompat(context, this);
        setZHlinePaint();
        setzhLineValuePaint();
        setKLineValuePaint();
        setKlinedotsPaint();
        setszLinePaint();
        setdotPaint();
        setShowdialogPaint();
    }

    //设置弹框画笔画笔
    private void setShowdialogPaint() {
        showdialogpaint.setAntiAlias(true);
        showdialogpaint.setStyle(Paint.Style.FILL);
        showdialogpaint.setTextSize(dp2px(context, 13));
        showdialogpaint.setTypeface(Typeface.DEFAULT_BOLD);
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
    private void setzhLineValuePaint() {
        //画文字
        zhLineValuePaint.setAntiAlias(true);
        zhLineValuePaint.setTextSize(dp2px(getContext(), 10));
        zhLineValuePaint.setTextAlign(Paint.Align.LEFT);
        zhLineValuePaint.setColor(ContextCompat.getColor(getContext(),R.color.c6A798E));
    }

    //设置k线的值画笔
    private void setKLineValuePaint() {
        kLineValuePaint.setStrokeWidth(dp2px(context, 1));
        kLineValuePaint.setAntiAlias(true);
        kLineValuePaint.setColor(ContextCompat.getColor(getContext(),R.color.c6774FF));
        kLineValuePaint.setStyle(Paint.Style.STROKE);
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
    private void setKlinedotsPaint() {
        KlinedotsPaint.setAntiAlias(true);
        KlinedotsPaint.setColor(ContextCompat.getColor(getContext(),R.color.c6774FF));
        KlinedotsPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isFirstTouch++;
                if(isFirstTouch==1){
                    getViewValue.onTouch(true);
                }else {
                    getViewValue.onTouch(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                loogPressx = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isLongPressTouch = false;
                isLongPress = false;
                isFirstTouch=0;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                isLongPressTouch = false;
                isLongPress = false;
                isFirstTouch=0;
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
        kHeight = height - mPaddingBottom;
        kWidth = getMeasuredWidth() - mPaddingLeft - mPaddingRight;
        lastHHeight = kHeight + dp2px(context, 4f);//画取网格的高度与值的高度
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - mPaddingRightLeft * 2;
        height = h;
        kHeight = height - mPaddingBottom;
        kWidth = w - mPaddingLeft - mPaddingRight;
        lastHHeight = kHeight + dp2px(context, 4f);//画取网格的高度与值的高度
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(),R.color.c2A2D4F));
        if (isRefreshData && klistBaseModel != null && klistBaseModel.size() > 0) {
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
        loogPressx = motionEvent.getX();
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
        if (chartDataList != null) {
            this.klistBaseModel = chartDataList;
        }

        isMessureData = true;
        invalidate();
    }

    //k线 数据值转化屏幕值
    private void setUpdateKValue() {
        isMessureData = false;
        //x轴展示的值
        mXDatalist.clear();
        String publishDate[];
        int hxValue = 1;
        int num = klistBaseModel.size();
        if (klistBaseModel.size() > 5) {
            num = 5;
            hxValue = Math.round(klistBaseModel.size() / 4);
        }
        for (int i = 0; i < num; i++) {
            if (i == 4) {
                publishDate = klistBaseModel.get(klistBaseModel.size() - 1).getPublishDate().split("/");
            } else {
                publishDate = klistBaseModel.get(i * hxValue).getPublishDate().split("/");
            }
            mXDatalist.add(publishDate[1] + "/" + publishDate[2]);
        }

        //y轴展示的值
        boolean ispremium = false;//判断是不是升贴水
        Vmax = (float) Double.parseDouble(klistBaseModel.get(0).getMiddle());//最大值
        Vmin = Vmax;//最小值
        for (int i = 0; i < klistBaseModel.size(); i++) {
            ispremium = klistBaseModel.get(i).isPremium();
            float middle = (float) Double.parseDouble(klistBaseModel.get(i).getMiddle());
            if (middle > Vmax) {
                Vmax = middle;
            }
            if (middle < Vmin) {
                Vmin = middle;
            }
        }
        Vmin=(int)Vmin;
        Vmax=(int)Math.ceil(Vmax);
        XLog.d("MaxValue",Vmax+"/Min="+Vmin);
        tempValue = Math.abs((Vmax - Vmin));//最大与最小之间的值
        float hyValue = tempValue / 4;//两点之间的间隔
        XLog.d("hyValue",hyValue+"/tempValue="+tempValue);
        mYDatalist.clear();
        if (tempValue == 0) {//最小值与最大值之差为零
            for (int i = 0; i < 5; i++) {
                mYDatalist.add(setIsPremiumIntValue(ispremium, (int) ((Vmax + 2) - i)));
            }
        } else {
            for (int i = 0; i < 5; i++) {
                if (i == 0) {
                    float yValue = (Vmax - i * hyValue);
                    mYDatalist.add(setIsPremiumIntValue(ispremium, (int) Math.ceil(yValue)));
                } else if (i == 4) {
                    mYDatalist.add(setIsPremiumIntValue(ispremium, (int) Vmin));//添加最后一位值
                } else {
                    int yValue = (int) (Vmax - i * hyValue);
                    mYDatalist.add(setIsPremiumIntValue(ispremium, yValue));
                }
            }

        }
        //划K线的值转成屏幕值
        mVKDatalist.clear();
        mHKDatalist.clear();
        //y屏幕
        float kValueHeight;
        for (int i = 0; i < klistBaseModel.size(); i++) {
            float middle = (float) Double.parseDouble(klistBaseModel.get(i).getMiddle());
            if (tempValue == 0) {
                kValueHeight = (kHeight - (kHeight * (middle - (Vmin - 2)) / ((Vmax + 2) - (Vmin - 2)))) + dp2px(context, 2.5f);
            } else {
                kValueHeight = (kHeight - (kHeight * (middle - Vmin) / tempValue)) + dp2px(context, 2.5f);
            }
            mVKDatalist.add(new SpotPriceValue(kValueHeight,false));

        }

        //x屏幕
        float kValueWidth;
        if (klistBaseModel.size() == 1) {//一个点处理
            kValueWidth = kWidth / 2 + mPaddingLeft;
            mHKDatalist.add(kValueWidth);
        } else {
            float klistSize = klistBaseModel.size() - 1 + 0.00f;
            for (int i = 0; i < klistBaseModel.size(); i++) {
                kValueWidth = kWidth / klistSize * i + mPaddingLeft;
                mHKDatalist.add(kValueWidth);
            }
        }
    }

    public static float formatFloat(float value) {
        BigDecimal decimal = new BigDecimal(String.valueOf(value));
        DecimalFormat format = new DecimalFormat("####.####");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return Float.parseFloat(format.format(decimal.doubleValue()));
    }

    //判断是不是升贴水
    private String setIsPremium(boolean ispremium, float yValue, String premiums) {
        String UDValue;
        if (ispremium) {//升贴水
            if (yValue > 0) {
                UDValue = "升" + premiums;
            } else if (yValue < 0) {
                UDValue = premiums.replace("-", "贴");
            } else {
                UDValue = "" + premiums;
            }
        } else {
            UDValue = "" + premiums;
        }
        return UDValue;
    }

    //判断是不是升贴水
    private String setIsPremiumIntValue(boolean ispremium, int yValue) {
        String UDValue;
        if (ispremium) {//升贴水
            if (yValue > 0) {
                UDValue = "升" + yValue;
            } else if (yValue < 0) {
                UDValue = "贴" + Math.abs(yValue);
            } else {
                UDValue = yValue + "";
            }
        } else {
            UDValue = yValue + "";
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
            canvas.drawLine(mPaddingLeft, (hline * i), kWidth + mPaddingRight, (hline * i), zhlinepaint);
        }
        //画纵线
        float vline = kWidth / VLineNum;
        for (int i = 0; i <= VLineNum; i++) {
            canvas.drawLine((vline * i) + mPaddingLeft, 0, (vline * i) + mPaddingLeft, lastHHeight, zhlinepaint);
        }

    }

    //画纵横线的值
    private void setDrawaVHTextValue(Canvas canvas) {
        repeatXValue = "";
        //获取文字的高度
        float baseLine = setTextHeight(zhLineValuePaint);
        //字体的宽度
        float textWidth = zhLineValuePaint.measureText(mXDatalist.get(0));

        for (int i = 0; i < mXDatalist.size(); i++) {
            if (mXDatalist.size() == 1) {//一个点处理
                canvas.drawText(mXDatalist.get(i), kWidth / 2 - textWidth / 2 + mPaddingRight, lastHHeight + mPaddingBottom / 2 + baseLine / 2, zhLineValuePaint);//x轴的展示值
            } else {
                if (!repeatXValue.equals(mXDatalist.get(i))) {
                    canvas.drawText(mXDatalist.get(i), ((kWidth / (mXDatalist.size() - 1) - textWidth / (mXDatalist.size() - 1)) * i) + mPaddingRight, lastHHeight + mPaddingBottom / 2 + baseLine / 2, zhLineValuePaint);//x轴的展示值
                }
            }
            repeatXValue = mXDatalist.get(i);
        }
        for (int i = 0; i < mYDatalist.size(); i++) {
            canvas.drawText(mYDatalist.get(i), mTextLeft, (float) ((lastHHeight - baseLine) / (mYDatalist.size() - 1 + 0.0) * i + baseLine), zhLineValuePaint);//y轴的展示值
        }
        //画0虚线
        if(Vmin<0||Vmin==0||tempValue==0){
            float yZeroHeight = (kHeight - (kHeight * (0 - Vmin) / tempValue)) + dp2px(context, 2.5f);
            drawZeroLine(canvas,yZeroHeight);
        }

    }

    //画K线图
    private void setDrawXYLineValue(Canvas canvas) {
        if (klistBaseModel.size() == 1) {
            canvas.drawCircle(mHKDatalist.get(0), mVKDatalist.get(0).getyValue(), dp2px(context, 5f), KlinedotsPaint);//画圆点线
        } else {
            Path path = new Path();
            path.moveTo(mHKDatalist.get(0), mVKDatalist.get(0).getyValue());
            //画贝塞尔曲线
            PointF p1 = new PointF();
            PointF p2 = new PointF();
            for (int i = 0; i < klistBaseModel.size() - 1; i++) {
                p1.y = mVKDatalist.get(i).getyValue();
                p1.x = (mHKDatalist.get(i) + mHKDatalist.get(i + 1)) / 2;
                p2.y = mVKDatalist.get(i + 1).getyValue();
                p2.x = (mHKDatalist.get(i) + mHKDatalist.get(i + 1)) / 2;
                path.cubicTo(p1.x, p1.y, p2.x, p2.y, mHKDatalist.get(i + 1), mVKDatalist.get(i + 1).getyValue());
            }
            canvas.drawPath(path, kLineValuePaint);
        }
    }

    /**
     * Y 轴0 画虚线
     * @param canvas
     */

    @SuppressLint("NewApi")
    private void drawZeroLine(Canvas canvas,float y){
        DashPathEffect pathEffect = new DashPathEffect(new float[] { 10,10 }, 0);
        Paint paint = new Paint();
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(getContext().getResources().getColor(R.color.c6774FF));
        paint.setAntiAlias(true);
        paint.setPathEffect(pathEffect);
        Path path = new Path();
        path.moveTo(mPaddingLeft, y);
        path.lineTo(kWidth + mPaddingRight, y);
        canvas.drawPath(path, paint);

    }

    //画十字线画弹框
    private void setLoogLine(Canvas canvas) {
        if (isLongPress) {
            //画实线
            float distHalf;//两点之间的一半
            float klistSize = (float) (mHKDatalist.size() - 1 + 0.00);
            if (klistSize == 0.00) {
                klistSize = (float) (mHKDatalist.size() + 0.00);
            }
            distHalf = (float) (kWidth / klistSize / 2.0);
            for (int i = 0; i < mHKDatalist.size(); i++) {
                if ((loogPressx - distHalf) <= mHKDatalist.get(i) && (loogPressx + distHalf) > mHKDatalist.get(i)) {
                    canvas.drawLine(mHKDatalist.get(i), 0, mHKDatalist.get(i), lastHHeight, szLinePaint);//画实线
                    canvas.drawCircle(mHKDatalist.get(i), mVKDatalist.get(i).getyValue(), dp2px(context, 2.5f), dotsPaint);//画圆点线

                    boolean premium = klistBaseModel.get(i).isPremium();
                    float middle = Float.parseFloat(klistBaseModel.get(i).getMiddle());

                    if (getViewValue != null) {
                        chooseData.setDate(klistBaseModel.get(i).getPublishDate() + " " + klistBaseModel.get(i).getPublishTime());
                        chooseData.setPrice(setIsPremium(premium, middle, klistBaseModel.get(i).getMiddle()));
                        if (premium) {
                            chooseData.setValue(klistBaseModel.get(i).getContract());
                        } else {
                            chooseData.setValue("");
                        }
                        getViewValue.setGetViewValue(chooseData, isLongPress, isLongPressTouch);
                    }
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
        void onTouch(boolean isTouch);
    }

    //判断是不是长按
    public boolean getLongPress() {
        return isLongPress;
    }

    public void setLongPress(boolean isLongPress) {
        this.isLongPress = isLongPress;
    }

}

