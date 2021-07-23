package com.gjmetal.app.widget.explist;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseWebViewActivity;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.spot.SpotChildFragment;

/**
 * Created by hgh on 2018/3/30.
 * 可悬浮 的分组
 */

public class DockingExpandableListView extends ExpandableListView implements OnScrollListener {

    private View mDockingHeader;

    private int mDockingHeaderWidth;

    private int mDockingHeaderHeight;

    private boolean mDockingHeaderVisible;

    private int mDockingHeaderState = IDockingController.DOCKING_HEADER_HIDDEN;

    private IDockingHeaderUpdateListener mListener;

    private boolean isIntercept = false;//判断是否拦截

    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;
    private float mYDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mXMove;
    private float mYMove;

    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;


    public DockingExpandableListView(Context context) {
        this(context, null);
    }

    public DockingExpandableListView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

    }


    public DockingExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        setOnScrollListener(this);

        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();;
    }


    public void setDockingHeader(View header, IDockingHeaderUpdateListener listener) {

        mDockingHeader = header;

        mListener = listener;

    }


    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mDockingHeader != null) {

            measureChild(mDockingHeader, widthMeasureSpec, heightMeasureSpec);

            mDockingHeaderWidth = mDockingHeader.getMeasuredWidth();

            mDockingHeaderHeight = mDockingHeader.getMeasuredHeight();

        }

    }


    @Override

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mDockingHeader != null) {

            mDockingHeader.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight);

        }

    }


    @Override

    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

        if (mDockingHeaderVisible) {

            // draw header view instead of adding into view hierarchy

            drawChild(canvas, mDockingHeader, getDrawingTime());

        }

    }


    @Override

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        long packedPosition = getExpandableListPosition(firstVisibleItem);

        int groupPosition = getPackedPositionGroup(packedPosition);

        int childPosition = getPackedPositionChild(packedPosition);


        // update header view based on first visible item

        // IMPORTANT: refer to getPackedPositionChild():

        // If this group does not contain a child, returns -1. Need to handle this case in controller.

        updateDockingHeader(groupPosition, childPosition);

    }


    @Override

    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }


    private void updateDockingHeader(int groupPosition, int childPosition) {

        if (getExpandableListAdapter() == null) {

            return;

        }


        if (getExpandableListAdapter() instanceof IDockingController) {

            IDockingController dockingController = (IDockingController) getExpandableListAdapter();

            mDockingHeaderState = dockingController.getDockingState(groupPosition, childPosition);

            switch (mDockingHeaderState) {

                case IDockingController.DOCKING_HEADER_HIDDEN:

                    mDockingHeaderVisible = false;

                    break;

                case IDockingController.DOCKING_HEADER_DOCKED:

                    if (mListener != null) {

                        mListener.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition));

                    }

                    // Header view might be "GONE" status at the beginning, so we might not be able

                    // to get its width and height during initial measure procedure.

                    // Do manual measure and layout operations here.

                    mDockingHeader.measure(

                            MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST),

                            MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST));

                    mDockingHeader.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight);

                    mDockingHeaderVisible = true;

                    break;

                case IDockingController.DOCKING_HEADER_DOCKING:

                    if (mListener != null) {

                        mListener.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition));

                    }


                    View firstVisibleView = getChildAt(0);

                    int yOffset;

                    if (firstVisibleView.getBottom() < mDockingHeaderHeight) {

                        yOffset = firstVisibleView.getBottom() - mDockingHeaderHeight;

                    } else {

                        yOffset = 0;

                    }


                    // The yOffset is always non-positive. When a new header view is "docking",

                    // previous header view need to be "scrolled over". Thus we need to draw the

                    // old header view based on last child's scroll amount.

                    mDockingHeader.measure(

                            MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST),

                            MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST));

                    mDockingHeader.layout(0, yOffset, mDockingHeaderWidth, mDockingHeaderHeight + yOffset);

                    mDockingHeaderVisible = true;

                    break;

            }

        }

    }

    public void setIntercept(boolean intercept) {
        this.isIntercept = intercept;
    }

    @Override

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //解决group 标题头view无点击事件问题
        if (ev.getAction() == MotionEvent.ACTION_DOWN && mDockingHeaderVisible) {
            Rect rect = new Rect();
            mDockingHeader.getDrawingRect(rect);

            if (rect.contains((int) ev.getX(), (int) ev.getY())
                    && mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED) {
                // Hit header view area, intercept the touch event
                return true;

            }

        }

        if (isIntercept) {
            return !isIntercept;
        }
        return super.onInterceptTouchEvent(ev);

    }


    // Note: As header view is drawn to the canvas instead of adding into view hierarchy,

    // it's useless to set its touch or click event listener. Need to handle these input

    // events carefully by ourselves.

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mDockingHeaderVisible) {

            Rect rect = new Rect();
            mDockingHeader.getDrawingRect(rect);


            switch (ev.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    if (rect.contains((int) ev.getX(), (int) ev.getY())) {

                        // forbid event handling by list view's item

                        return true;

                    }

                    break;
                case MotionEvent.ACTION_UP:
                    //获取报价按钮，xml 的第二个RelativeLayout 中的imageview
                    ImageView imageView = (ImageView) ((RelativeLayout) ((LinearLayout) mDockingHeader).getChildAt(1)).getChildAt(2);
                    Rect rect1 = new Rect();
                    imageView.getHitRect(rect1);
                    if (rect1.contains((int) ev.getX(), (int) ev.getY()) && imageView.getTag() != null && imageView.getVisibility() == VISIBLE) {
                        BaseWebViewActivity.launch((Activity) getContext(), new WebViewBean(getContext().getString(R.string.txt_spot_desc), Constant.ReqUrl.getDefaultHtmlUrl(imageView.getTag().toString())));
                        return true;
                    }
                    TextView tvAnalysis = (TextView) ((RelativeLayout) ((LinearLayout) mDockingHeader).getChildAt(1)).getChildAt(3);
                    Rect rect2 = new Rect();
                    tvAnalysis.getHitRect(rect2);
                    getParent().requestDisallowInterceptTouchEvent(false);//禁止事件传递到子视图
                    if (rect2.contains((int) ev.getX(), (int) ev.getY()) && tvAnalysis.getTag() != null && tvAnalysis.getVisibility() == VISIBLE) {
                        Spot mSpot = (Spot) tvAnalysis.getTag();
                        SpotChildFragment.checkAnalysis(getContext(),mSpot);
                        return true;
                    }
                    break;
            }
        }
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }


}
