package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.api.Constant;

import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 选择图片dialog
 * Created by huangb on 2018/4/4.
 */

public class ChoosePhotoDialog extends Dialog implements View.OnClickListener {

    private BaseActivity activity;
    private TextView cancelButton;
    private TextView mPhoto;
    private TextView mCamera;
    private OnChoosePhotoListener mOnChoosePhotoListener;

    public ChoosePhotoDialog(BaseActivity activity, OnChoosePhotoListener onChoosePhotoListener) {
        super(activity, R.style.TransparentFrameWindowStyle);
        this.activity = activity;
        this.mOnChoosePhotoListener = onChoosePhotoListener;
    }

    public ChoosePhotoDialog(Context context) {
        super(context, R.style.TransparentFrameWindowStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_photo);
        mPhoto = findViewById(R.id.button_photo);
        mCamera = findViewById(R.id.button_camera);
        cancelButton = findViewById(R.id.button_cancel);
        mPhoto.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.button_camera:
                FunctionConfig functionConfig = new FunctionConfig.Builder()
                        .setEnableCamera(false)
                        .setEnableRotate(true)
                        .setCropSquare(true)
                        .setEnableEdit(true)
                        .setEnablePreview(false)
                        .build();
                GalleryFinal.openCamera(Constant.REQUEST_CODE_CAMERA,functionConfig, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        mOnChoosePhotoListener.success(reqeustCode, resultList);
                    }
                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        mOnChoosePhotoListener.failue(requestCode, errorMsg);
                    }
                });
                dismiss();
                break;
            case R.id.button_photo:
                GalleryFinal.openGallerySingle(Constant.REQUEST_CODE_GALLERY, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        mOnChoosePhotoListener.success(reqeustCode, resultList);
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        mOnChoosePhotoListener.failue(requestCode, errorMsg);
                    }
                });
                //自定义方法的单选
                dismiss();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }

    }


    public interface OnChoosePhotoListener {
        void success(int code, List<PhotoInfo> photoInfoList);

        void failue(int code, String msg);
    }
}

