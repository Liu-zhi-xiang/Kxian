package com.gjmetal.app.widget.popuWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.gjmetal.app.R;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.widget.NumKeyView;

/**
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-14 16:14
 */

public class KeyBoardPopWindow extends PopupWindow {
    private Context mContext;

    private NumKeyView mKeyView;
    private LinearLayout mLinearlayout;
    private View mPopView;
    private RelativeLayout mRelativeLayout;

    private OnKeyClickListener mOnKeyClickListener;

    public KeyBoardPopWindow(Context context) {
        super(context);
        initView(context);
    }

    public KeyBoardPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public KeyBoardPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }



    private void initView(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.keyboard_pop, null);
        setContentView(view);
        setTouchable(true);
        setFocusable(false); //设置焦点，是否点击外部会消失
        setBackgroundDrawable(new ColorDrawable());
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.PopWindowstyle);


        mKeyView = view.findViewById(R.id.keyboardview);
        mRelativeLayout = view.findViewById(R.id.iv_hide);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GjUtil.closeKeyBoard();
                dismiss();
            }
        });

        mKeyView.setOnKeyPressListener(new NumKeyView.OnKeyPressListener() {
            @Override
            public void onInertKey(String text) {
                mOnKeyClickListener.onInertKey(text);
            }

            @Override
            public void onDeleteKey() {
                mOnKeyClickListener.onDeleteKey();
            }

            @Override
            public void onClearKey() {
                mOnKeyClickListener.onClearKey();
            }
        });

    }

    public NumKeyView getKeyView() {
        return mKeyView;
    }

    public void setOnKeyClickListener(OnKeyClickListener onKeyClickListener) {
        if (onKeyClickListener != null) {
            this.mOnKeyClickListener = onKeyClickListener;
        }
    }

    public interface OnKeyClickListener {
        void onInertKey(String text);

        void onDeleteKey();

        void onClearKey();
    }


}














