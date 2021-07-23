package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.alphametal.QuotationsModel;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.kit.KnifeKit;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description 选择合约
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-13 17:24
 */

public class CounterContractDialog extends Dialog {
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvFinish)
    TextView tvFinish;
    @BindView(R.id.loopMainName)
    LoopView loopMainName;
    @BindView(R.id.loopChildName)
    LoopView loopChildName;


    //接口相关
    private OnDialogClickListener mOnDialogClickListener;// 控件点击接口

    private List<String> mListMainName = new ArrayList<>();
    private List<String> mListChildName = new ArrayList<>();
    private List<QuotationsModel> mQuotationsModelList;
    private List<CrossMetalModel> mCrossMetalModellList;
    private int mainNamePosition;
    private int childNamePosition;
    private int type=1;
    public CounterContractDialog(@NonNull Context context) {
        super(context);
    }

    public CounterContractDialog(@NonNull Context context, int themeResId, List<QuotationsModel> listQuotationItems,
                                 int mainPosition, int childPosition) {
        super(context, themeResId);
        this.mQuotationsModelList = listQuotationItems;
        if (ValueUtil.isListNotEmpty(listQuotationItems)) {
            for (int i = 0; i < listQuotationItems.size(); i++) {
                mListMainName.add(listQuotationItems.get(i).getContractName());
            }
            if (ValueUtil.isListNotEmpty(listQuotationItems.get(mainPosition).getNameList())) {
                for (int i = 0; i < listQuotationItems.get(mainPosition).getNameList().size(); i++) {
                    mListChildName.add(listQuotationItems.get(mainPosition).getNameList().get(i).getName());
                }
            }else {
                    mListChildName.add("null");
            }

        }
        this.mainNamePosition = mainPosition;
        this.childNamePosition = childPosition;
        type=1;
    }
    public CounterContractDialog(@NonNull Context context, int themeResId, List<CrossMetalModel> listQuotationItems,
                                 int mainPosition, int childPosition,int type) {
        super(context, themeResId);
        this.mCrossMetalModellList = listQuotationItems;
        if (ValueUtil.isListNotEmpty(listQuotationItems)) {
            for (int i = 0; i < listQuotationItems.size(); i++) {
                mListMainName.add(listQuotationItems.get(i).getExchangeName());
            }
            if (ValueUtil.isListNotEmpty(listQuotationItems.get(mainPosition).getDataList())) {
                for (int i = 0; i < listQuotationItems.get(mainPosition).getDataList().size(); i++) {
                    mListChildName.add(listQuotationItems.get(mainPosition).getDataList().get(i).getName());
                }
            }else {
                mListChildName.add("null");
            }

        }
        this.mainNamePosition = mainPosition;
        this.childNamePosition = childPosition;
        this.type=2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_counter_contranct, null);
        setContentView(view);
        KnifeKit.bind(this, view);
        if (ValueUtil.isListNotEmpty(mListMainName)) {
            loopMainName.setItems(mListMainName);
        }

        if (ValueUtil.isListNotEmpty(mListChildName)) {
            loopChildName.setItems(mListChildName);
        }
        setCurrentPostion();
        loopChildName.setDividerColor(ContextCompat.getColor(getContext(),R.color.c00000000));
        loopMainName.setDividerColor(ContextCompat.getColor(getContext(),R.color.c00000000));

        loopMainName.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mainNamePosition = index;
                mListChildName.clear();
                if (type==1) {
                    if (ValueUtil.isListNotEmpty(mQuotationsModelList.get(mainNamePosition).getNameList())) {
                        for (int i = 0; i < mQuotationsModelList.get(mainNamePosition).getNameList().size(); i++) {
                            mListChildName.add(mQuotationsModelList.get(mainNamePosition).getNameList().get(i).getName());
                        }
                    }
                }else if (type==2){
                    if (ValueUtil.isListNotEmpty(mCrossMetalModellList.get(mainNamePosition).getDataList())) {
                        for (int i = 0; i < mCrossMetalModellList.get(mainNamePosition).getDataList().size(); i++) {
                            mListChildName.add(mCrossMetalModellList.get(mainNamePosition).getDataList().get(i).getName());
                        }
                    }
                }
                loopChildName.setItems(mListChildName);
                childNamePosition = 0;
                loopChildName.setCurrentPosition(childNamePosition);
            }
        });


        loopChildName.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                childNamePosition = index;

            }
        });



    }

    @Override
    public void onDetachedFromWindow() {
        if (isShowing())
            dismiss();
        super.onDetachedFromWindow();
    }

    @Override
    public void show() {
        super.show();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point point=new Point();
        d.getSize(point);
        p.width = point.x; //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(p);

    }

    @OnClick({R.id.tvCancel, R.id.tvFinish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvFinish:
                if (mListChildName.size() > 0 && mListMainName.size() > 0) {
                    if (mOnDialogClickListener != null) {
                        dismiss();
                        String name = mListMainName.get(mainNamePosition);
                        String country = mListChildName.get(childNamePosition);
                        mOnDialogClickListener.dialogClick(this, view, name, country, mainNamePosition, childNamePosition);
                    }
                } else {
                    dismiss();
                }

                break;
        }
    }

    //设置选中位置
    public void setCurrentPostion() {
        loopMainName.setCurrentPosition(mainNamePosition);
        loopChildName.setCurrentPosition(childNamePosition);
    }

    //设置选中位置
    public void setCurrentPostion( int mainPosition, int childPosition) {
        if (loopMainName==null||loopChildName==null){
            return;
        }
        if (mainPosition<mListMainName.size()&&childPosition<mListChildName.size()) {
            mainNamePosition=mainPosition;
            childNamePosition=childPosition;
            loopMainName.setCurrentPosition(mainNamePosition);
            loopChildName.setCurrentPosition(childNamePosition);
        }
    }
    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.mOnDialogClickListener = listener;
    }

    /**
     * 控件点击事件接口
     */
    public interface OnDialogClickListener {
        void dialogClick(Dialog dialog, View v, String name, String price, int namePosition, int pricePosition);
    }
}
