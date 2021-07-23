package com.gjmetal.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;


/**
 *
 * Description:
 *      预览图片蒙层
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/7/3  11:32
 *
 */
public class ImageOverlayView extends RelativeLayout {

    private TextView tvDescription;

    private LinearLayout llBack,llDownload;
    private String url;
   public ImageOverlayViewCallBack imageOverlayViewCallBack;
    public ImageOverlayView(Context context,ImageOverlayViewCallBack imageOverlayViewCallBack) {
        super(context);
        this.imageOverlayViewCallBack=imageOverlayViewCallBack;
        init();
    }
    public ImageOverlayView(Context context) {
        super(context);
        init();
    }
    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDescription(String description) {
        if (tvDescription!=null)
        tvDescription.setText(description+"/"+imgSize);
    }

    public ImageOverlayView setImageOverlayViewCallBack(ImageOverlayViewCallBack imageOverlayViewCallBack) {
        this.imageOverlayViewCallBack = imageOverlayViewCallBack;
        return this;
    }
    public void gone(){
        llDownload.setVisibility(GONE);
    }
    public void visible(){
        llDownload.setVisibility(VISIBLE);
    }
    private void init() {
        View view = inflate(getContext(), R.layout.activity_news_img, this);
        tvDescription = view.findViewById(R.id.tvImgNub);
        llBack=view.findViewById(R.id.llBack);
        llDownload=view.findViewById(R.id.llDownload);
        llDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageOverlayViewCallBack!=null)
                    imageOverlayViewCallBack.onDownload(url);
            }
        });
        llBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageOverlayViewCallBack!=null)
                    imageOverlayViewCallBack.onBack();
            }
        });
    }

    private String  imgSize="1";
    public ImageOverlayView setSelectImg(String  s) {
        imgSize=s;
        return this;
    }

    public void setUrlString(String url) {
        this.url=url;
    }

    public interface  ImageOverlayViewCallBack{
        void onDownload(String url);
        void onBack();
    }

}
