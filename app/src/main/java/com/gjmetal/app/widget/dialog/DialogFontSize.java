package com.gjmetal.app.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.SharedUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yuzishun on 2018/4/12.
 */
public abstract class DialogFontSize extends Dialog implements View.OnClickListener{
    private Activity activity;
    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.layout2)
    LinearLayout layout2;
    @BindView(R.id.layout3)
    LinearLayout layout3;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.image_font1)
    ImageView image_font1;
    @BindView(R.id.image_font2)
    ImageView image_font2;
    @BindView(R.id.image_font3)
    ImageView image_font3;
    private int fontSize;
    Unbinder unbinder;
    public DialogFontSize(Activity activity) {
        super(activity, R.style.TransparentFrameWindowStyle);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_font_size);
        unbinder = ButterKnife.bind(this);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setViewLocation();
        setCanceledOnTouchOutside(true);//外部点击取消
    }
    /**
     * 设置dialog位于屏幕底部
     */
    private void setViewLocation(){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = height;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        onWindowAttributesChanged(lp);


    }

    public void change(){
        try{
        int fontSize = SharedUtil.getInt(Constant.DEFAULT_FONT_SIZE);
        if(fontSize==1){
            image_font1.setImageResource(R.mipmap.ic_font_size_res);
            image_font2.setImageResource(R.mipmap.ic_font_size_nor);
            image_font3.setImageResource(R.mipmap.ic_font_size_nor);

        }else if(fontSize==2){
            image_font1.setImageResource(R.mipmap.ic_font_size_nor);
            image_font2.setImageResource(R.mipmap.ic_font_size_res);
            image_font3.setImageResource(R.mipmap.ic_font_size_nor);
        }else if(fontSize==3){
            image_font1.setImageResource(R.mipmap.ic_font_size_nor);
            image_font2.setImageResource(R.mipmap.ic_font_size_nor);
            image_font3.setImageResource(R.mipmap.ic_font_size_res);
        }
        }catch (Exception e){
        }
    }
    public void chang1(){
        image_font1.setImageResource(R.mipmap.ic_font_size_res);
        image_font2.setImageResource(R.mipmap.ic_font_size_nor);
        image_font3.setImageResource(R.mipmap.ic_font_size_nor);

    }
    public void chang2(){
        image_font1.setImageResource(R.mipmap.ic_font_size_nor);
        image_font2.setImageResource(R.mipmap.ic_font_size_res);
        image_font3.setImageResource(R.mipmap.ic_font_size_nor);

    }
    public void chang3(){
        image_font1.setImageResource(R.mipmap.ic_font_size_nor);
        image_font2.setImageResource(R.mipmap.ic_font_size_nor);
        image_font3.setImageResource(R.mipmap.ic_font_size_res);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout1:
                fontSize=1;
                SharedUtil.putInt(Constant.DEFAULT_FONT_SIZE,fontSize);
                Small();
                this.cancel();
                break;
            case R.id.layout2:
                fontSize=2;
                SharedUtil.putInt(Constant.DEFAULT_FONT_SIZE,fontSize);
                in();
                this.cancel();
                break;
            case R.id.layout3:
                fontSize=3;
                SharedUtil.putInt(Constant.DEFAULT_FONT_SIZE,fontSize);
                big();
                this.cancel();
                break;
            case R.id.cancel:
                this.cancel();
                break;

        }
    }

    public abstract void Small();
    public abstract void in();
    public abstract void big();

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}