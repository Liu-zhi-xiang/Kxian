package com.gjmetal.app.widget.flowlayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gjmetal.app.R;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Description:拖动的flowLayout
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/30  9:03
 *
 */
public class FlowDragLayout extends ViewGroup {
    private final float textSize;
    private final int tagHeight;
    private final int deleteIconMargin=10;
    private final int childViewPadding;
    private final int defaultViewBackground;
    private final int fixViewEditingBackground;
    private final int fixViewEditingTextColor;
    private int textViewSpacing;
    private int verticalSpacing;
    private int defaultTextColor;
    private DragHandler mDragAndDropHandler;
    private AnimatorSet lastAnimatorSet;

    private boolean isMeasureSuccess;
    private OnTagClickListener onTagClickListener;
    private List<TextView> recommentLists = new ArrayList<>();
    public SparseArray<ArrayList<TagInfo>> getRowSparseArray() {
        return rowSparseArray;
    }

    private SparseArray<ArrayList<TagInfo>> rowSparseArray;

    public List<TagInfo> getTagInfos() {
        return tagInfos;
    }

    private List<TagInfo> tagInfos;
    private boolean isSettingAnimation = false;

    public FlowDragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.flowLayout, defStyle, 0);

        textViewSpacing = ViewSizeUtil.getCustomDimen(context, a.getInt(R.styleable.flowLayout_horizontalSpacingSize, 12));
        verticalSpacing = ViewSizeUtil.getCustomDimen(context, a.getInt(R.styleable.flowLayout_verticalSpacingSize, 12));
        tagHeight = ViewSizeUtil.getCustomDimen(context, a.getInt(R.styleable.flowLayout_tagHeight, 26));
        childViewPadding = ViewSizeUtil.getCustomDimen(context, a.getInt(R.styleable.flowLayout_childViewPadding, 16));
        textSize = ViewSizeUtil.getCustomDimen(context, a.getInt(R.styleable.flowLayout_flowLayoutTextSize, 14)) * 1.0f / ViewSizeUtil.getDensity(context);

//        deleteIconWidth = ViewSizeUtil.getCustomDimen(context,a.getInt(R.styleable.flowLayout_deleteIconWidth, 1));
//        deleteIconMargin = ViewSizeUtil.getCustomDimen(context, a.getInt(R.styleable.flowLayout_deleteIconMargin, 1));
//

        defaultTextColor = a.getColor(R.styleable.flowLayout_defaultTextColor, 0xffffffff);
        defaultViewBackground = a.getResourceId(R.styleable.flowLayout_defaultViewBackground, R.drawable.shape_information_tag_res);

        fixViewEditingTextColor = a.getColor(R.styleable.flowLayout_fixViewTextColor, 0xffffffff);
        fixViewEditingBackground = a.getResourceId(R.styleable.flowLayout_fixViewEditingBackground, R.drawable.shape_information_tag_res);


        a.recycle();
    }

    public FlowDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowDragLayout(Context context) {
        this(context, null);
    }

    public void setDefault() {
        mDragAndDropHandler = null;
    }

    public void setIsEdit(boolean isEdit) {
        if (tagInfos != null) {
            if (isEdit) {
                for (int i = 0; i < tagInfos.size(); i++) {
                    setOnLongClick(getChildAt(i), i);
                }
                for (int i = 0; i < recommentLists.size(); i++) {
                    setTextViewColor(recommentLists.get(i), fixViewEditingBackground, fixViewEditingTextColor);
                    recommentLists.get(i).setOnClickListener(null);
                }
                requestLayout();
            } else {
                recommentLists.clear();
                initData();
            }
        } else {
            Toast.makeText(getContext(),R.string.flowLayout_set_edit_tips, Toast.LENGTH_SHORT).show();
        }
    }


    public void deleteTag(TagInfo tagInfo) {
        tagInfos.remove(tagInfo);
        removeAllViews();
        isMeasureSuccess = false;
        addTags(tagInfos);
        setIsEdit(true);
//        if (onTagClickListener != null) {
//            onTagClickListener.onTagDelete(tagInfo,j);
//        }
    }

    public void initData() {
        removeAllViews();
        isMeasureSuccess = false;
        setTags(tagInfos);
    }

    public void addTag(TagInfo tagInfo, boolean isEdit) {
        tagInfos.add(tagInfo);
        removeAllViews();
        isMeasureSuccess = false;
        addTags(tagInfos);
        setIsEdit(isEdit);
    }

    public void setTags(List<TagInfo> tagInfos) {
        addTags(tagInfos);
        requestLayout();
    }


    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    public void addTags(List<TagInfo> tagInfos) {
        this.tagInfos = tagInfos;
        for (int i = 0; i < tagInfos.size(); i++) {
            addListViewTextView(tagInfos, i);
        }
        if (getChildCount() > tagInfos.size()) {
            removeViews(tagInfos.size(), getChildCount() - tagInfos.size());
        }
    }


    public void addListViewTextView(List<TagInfo> tagInfos, final int i) {
        TagInfo tagInfo;
        TextView button;
        tagInfo = tagInfos.get(i);
        tagInfo.childPosition = i;
        if (i < getChildCount()) {
            button = (TextView) getChildAt(i);
        } else {
            button = new TextView(getContext());
            if (tagInfo.type == TagInfo.TYPE_TAG_SERVICE) {
                recommentLists.add(button);
            }

            setTextViewColor(button, defaultViewBackground, defaultTextColor);

            button.setGravity(Gravity.CENTER);
//            button.setTextSize(dp2px(14));
//
//            button.setPadding(dp2px(12),6,dp2px(12),6);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, tagHeight);
            addView(button, layoutParams);
        }
        button.setText(tagInfo.tagName);
        button.setTag(tagInfo);
        button.setTag(tagInfo);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTagClickListener != null) {
                    onTagClickListener.onTagClick((TagInfo) v.getTag(),i);
                    TagInfo tagInfo = (TagInfo) v.getTag();
                    deleteTag(tagInfo);
                }
            }
        });
    }


    private void setTextViewColor(TextView textView, int backgroundRes, int color) {
        textView.setBackgroundResource(backgroundRes);
        textView.setTextColor(color);
    }

    void setOnLongClick(View button, int i) {
        if (mDragAndDropHandler != null) {
            final int finalI = i;
            TagInfo tagInfo = (TagInfo) getChildAt(finalI).getTag();
            if (tagInfo.type == TagInfo.TYPE_TAG_USER) {
                button.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        startDragging(finalI);
                        return true;
                    }
                });
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int  height =MeasureSpec.getSize(heightMeasureSpec);
        if (!isMeasureSuccess && tagInfos != null) {
            final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(tagHeight, MeasureSpec.EXACTLY);

            rowSparseArray = FlowLayoutUtils.getTagRects(
                    tagInfos,
                    deleteIconMargin,
                    width - deleteIconMargin,
                    (int) (textSize * ViewSizeUtil.getDensity(getContext())),
                    tagHeight,
                    textViewSpacing,
                    verticalSpacing,
                    childViewPadding, new FlowLayoutUtils.onGetTagListener() {
                        @Override
                        public void onGetTag(int position, TagInfo tagInfo) {
                            getChildAt(position).measure(MeasureSpec.makeMeasureSpec(tagInfo.rect.width(), MeasureSpec.EXACTLY), childHeightMeasureSpec);

                        }
                    });
        }
        if (rowSparseArray != null && rowSparseArray.size() > 0) {
            List<TagInfo> tagInfos = rowSparseArray.get(rowSparseArray.size() - 1);
            if (tagInfos != null && tagInfos.size() > 0) {
                setMeasuredDimension(width, tagInfos.get(tagInfos.size() - 1).rect.bottom);
            } else {
                setMeasuredDimension(width, 0);
            }
        } else {
            setMeasuredDimension(width, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isMeasureSuccess && tagInfos != null) {
            TagInfo tagInfo;
            for (int i = 0; i < tagInfos.size(); i++) {
                tagInfo = getTagInfos().get(i);
                getChildAt(i).layout(tagInfo.rect.left, tagInfo.rect.top, tagInfo.rect.right, tagInfo.rect.bottom);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.dispatchDraw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.dispatchDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragAndDropHandler != null) {
            return mDragAndDropHandler.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void enableDragAndDrop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            throw new UnsupportedOperationException("Drag and drop is only supported API levels 14 and up!");
        }
        mDragAndDropHandler = new DragHandler(this);
    }

    public void startDragging(final int position) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.startDragging(position);
        }
    }

    private void sortTag() {
        rowSparseArray = FlowLayoutUtils.getTagRects(tagInfos, deleteIconMargin, getMeasuredWidth() - deleteIconMargin, (int) (textSize * ViewSizeUtil.getDensity(getContext()) + 0.5f), tagHeight, textViewSpacing, verticalSpacing, childViewPadding, null);
    }

    public void startAnimation(final TagInfo lastTagInfo) {
        if (isSettingAnimation) {
            return;
        }
        sortTag();
        TagInfo tagInfo;
        Rect rect;
        List<Animator> animationList = new ArrayList<>();
        for (int i = 0; i < tagInfos.size(); i++) {
            rect = new Rect();
            tagInfo = (TagInfo) getChildAt(i).getTag();
            getChildAt(i).getHitRect(rect);
            if (getChildAt(i).isShown()) {
                if (rect.left != tagInfo.rect.left) {
                    animationList.add(getObjectAnimator(tagInfo.rect.left, "x", getChildAt(i), 250));
                }
                if (rect.top != tagInfo.rect.top) {
                    animationList.add(getObjectAnimator(tagInfo.rect.top, "y", getChildAt(i), 250));
                }
            } else {
                animationList.add(getObjectAnimator(tagInfo.rect.left, "x", getChildAt(i), 0));
                animationList.add(getObjectAnimator(tagInfo.rect.top, "y", getChildAt(i), 0));

            }
        }
        lastAnimatorSet = new AnimatorSet();
        lastAnimatorSet.playTogether(animationList);
        lastAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isSettingAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isSettingAnimation = false;
                if (mDragAndDropHandler.getLastTagInfo() != null && lastTagInfo != mDragAndDropHandler.getLastTagInfo()) {
                    startAnimation(mDragAndDropHandler.getLastTagInfo());
                } else if (tagInfos.get(tagInfos.size() - 1).rect.bottom != getMeasuredHeight()) {
                    isMeasureSuccess = true;
                    requestLayout();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isSettingAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        lastAnimatorSet.start();
    }

    @NonNull
    public ObjectAnimator getObjectAnimator(int value, String property, View view, long duration) {
        ObjectAnimator x;
        x = ObjectAnimator.ofFloat(view, property, value);
        x.setDuration(duration);
        return x;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragAndDropHandler != null && mDragAndDropHandler.isDrag()) {
            requestDisallowInterceptTouchEvent(true);
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);

        }

    }
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

}

