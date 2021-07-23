package com.gjmetal.app.widget.flowlayout;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import java.util.List;

/**
 *
 * Description: 拖动的flowview 滑动操作类
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/30  9:03
 *
 */
public class DragHandler {
    private final int mSlop;
    private final FlowDragLayout flowDragLayout;
    private final LSwitchViewAnimator mSwitchViewAnimator;

    private View mMobileView;
    private float mLastMotionEventY;
    private float mLastMotionEventX;
    private TagsHoverDrawable mHoverDrawable;
    private float mDownX;
    private float mDownY;
    private int firstClickPosition;
    private TagInfo clickTag;
    private boolean isDrag;

    boolean isDrag() {
        return isDrag;
    }


    public TagInfo getLastTagInfo() {
        return lastTagInfo;
    }

    private TagInfo lastTagInfo = new TagInfo();

    public DragHandler(FlowDragLayout FlowDragLayout) {
        this.flowDragLayout = FlowDragLayout;
        ViewConfiguration vc = ViewConfiguration.get(FlowDragLayout.getContext());
        mSlop = vc.getScaledTouchSlop();
        mSwitchViewAnimator = new LSwitchViewAnimator();
    }

    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        boolean handled = false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionEventY = event.getY();
                mLastMotionEventX = event.getX();
                handled = handleDownEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrag) {
                    mLastMotionEventY = event.getY();
                    mLastMotionEventX = event.getX();
                    handled = handleMoveEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                isDrag = false;
                handled = handleUpEvent();
                mLastMotionEventY = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                isDrag = false;
                handled = handleCancelEvent();
                mLastMotionEventY = -1;
                break;
            default:
                handled = false;
                break;
        }
        return handled;
    }

    private boolean handleDownEvent(@NonNull final MotionEvent event) {
        mDownX = event.getRawX();
        mDownY = event.getRawY();
        return true;
    }

    float deltaX, deltaY;

    private boolean handleMoveEvent(final MotionEvent event) {
        boolean handled = false;

        deltaX = event.getRawX() - mDownX;
        deltaY = event.getRawY() - mDownY;

        TagInfo tagInfo = pointToPosition((int) event.getX(), (int) event.getY());

        if (mHoverDrawable == null && (deltaY * deltaY + deltaX * deltaX > mSlop * mSlop)) {
            if (tagInfo != null) {
                startDragging(tagInfo.dataPosition);
                handled = true;
            }
        } else if (mHoverDrawable != null) {
                mHoverDrawable.handleMoveEvent(event);
            if (tagInfo != null && tagInfo.type == TagInfo.TYPE_TAG_USER && (tagInfo != lastTagInfo || !tagInfo.rect.contains(clickTag.rect))) {
                switchViews(tagInfo);
            }
            flowDragLayout.invalidate();
            handled = true;
        }

        lastTagInfo = tagInfo;
        return handled;
    }

    private void switchViews(TagInfo tagInfo) {
//        flowLayout.setDataPosition(currentPosition, position);
        mSwitchViewAnimator.animateSwitchView(tagInfo);
    }

    void startDragging(int position) {
        isDrag = true;
        mMobileView = flowDragLayout.getChildAt(position);
        if (mMobileView != null) {
            mHoverDrawable = new TagsHoverDrawable(mMobileView,mLastMotionEventY, mLastMotionEventX);
            mMobileView.setVisibility(View.INVISIBLE);
        }
        clickTag = (TagInfo) mMobileView.getTag();
        firstClickPosition = flowDragLayout.getTagInfos().indexOf(clickTag);
    }


    private void getLocation(TagInfo tagInfo,final MotionEvent event){
        if (tagInfo==null) {
            return;
        }
            mMobileView = flowDragLayout.getChildAt(tagInfo.dataPosition);
            if (mMobileView != null) {
                int l = (int) (mMobileView.getLeft() + deltaX);
                int r = (int) (mMobileView.getRight() + deltaX);
                int t = (int) (mMobileView.getTop() + deltaY);
                int b = (int) (mMobileView.getBottom() + deltaY);
                if (l < 0) {
                    l = 0;
                    r = l + mMobileView.getWidth();
                }
                if (t < 0) {
                    t = 0;
                    b = t + mMobileView.getHeight();
                }
                if (r >1043) {
                    r = 1043;
                    l = r - 1043;
                }
                if (b > 309) {
                    b = 309;
                    t = b - 309;
                }
            }
        }


    private boolean handleUpEvent() {
        if (mMobileView == null) {
            return false;
        }
        mMobileView.setVisibility(View.VISIBLE);

        mHoverDrawable = null;
        mMobileView = null;
        return true;
    }

    private boolean handleCancelEvent() {
        return handleUpEvent();
    }

    public void dispatchDraw(@NonNull final Canvas canvas) {
        if (mHoverDrawable != null) {
            mHoverDrawable.draw(canvas);
        }
    }

    private TagInfo pointToPosition(int x, int y) {
        TagInfo resultTagInfo = null;
        int count = 0;
        int row = 0;
        if (mMobileView != null && !clickTag.rect.contains(x, y)) {
            List<TagInfo> tagInfoList = null;
            for (int i = 0; i < flowDragLayout.getRowSparseArray().size(); i++) {
                if (y >= flowDragLayout.getRowSparseArray().get(i).get(0).rect.top && y <= flowDragLayout.getRowSparseArray().get(i).get(0).rect.bottom) {
                    tagInfoList = flowDragLayout.getRowSparseArray().get(i);
                    row = i;
                    break;
                } else {
                    count += flowDragLayout.getRowSparseArray().get(i).size();
                }
            }
            int clickPosition = flowDragLayout.getTagInfos().indexOf(clickTag);
            TagInfo tagInfo;
            if (tagInfoList != null) {
                if (x > tagInfoList.get(tagInfoList.size() - 1).rect.right) {
                    if (row == flowDragLayout.getRowSparseArray().size() - 1) {
                        resultTagInfo = tagInfoList.get(tagInfoList.size() - 1);
                        resultTagInfo.dataPosition = tagInfoList.size() + count - 1;
                    } else {
                        resultTagInfo = flowDragLayout.getRowSparseArray().get(row + 1).get(0);
                        resultTagInfo.dataPosition = count + flowDragLayout.getRowSparseArray().get(row).size();
                    }
                } else {
                    for (int i = 0; i < tagInfoList.size(); i++) {
                        tagInfo = tagInfoList.get(i);
                        if (tagInfo.rect.contains(x, y)) {
                            resultTagInfo = tagInfo;
                            if (x <= (tagInfo.rect.left + tagInfo.rect.right) / 2) {
                                resultTagInfo.dataPosition = count + i;
                            } else {
                                resultTagInfo.dataPosition = i + count + 1;
                            }
                            break;
                        }
                    }
                }
            }

            if (resultTagInfo != null && resultTagInfo.type == TagInfo.TYPE_TAG_USER && resultTagInfo.dataPosition != clickPosition) {
                if (resultTagInfo.dataPosition == flowDragLayout.getTagInfos().size() - 1) {
                    flowDragLayout.getTagInfos().remove(clickTag);
                    flowDragLayout.getTagInfos().add(clickTag);
                } else {
                    if (resultTagInfo.dataPosition < clickPosition) {
                        flowDragLayout.getTagInfos().add(resultTagInfo.dataPosition, clickTag);
                        flowDragLayout.getTagInfos().remove(clickPosition + 1);
                    } else {
                        flowDragLayout.getTagInfos().add(resultTagInfo.dataPosition, clickTag);
                        flowDragLayout.getTagInfos().remove(clickPosition);
//                        if (resultTagInfo.dataPosition - clickPosition != 1) {
//                            flowLayout.getTagInfos().add(resultTagInfo.dataPosition - 1, clickTag);
//                        } else {
//                        }
                    }
                }
            }
        }
        return resultTagInfo;
    }

    private class LSwitchViewAnimator {

        public void animateSwitchView(TagInfo tagInfo) {
            flowDragLayout.getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(tagInfo));
        }

        private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

            private TagInfo tagInfo;

            public AnimateSwitchViewOnPreDrawListener(TagInfo tagInfo) {
                this.tagInfo = tagInfo;
            }

            @Override
            public boolean onPreDraw() {
                flowDragLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                flowDragLayout.startAnimation(tagInfo);
//                mMobileView.setVisibility(View.VISIBLE);
                return true;
            }
        }
    }
}
