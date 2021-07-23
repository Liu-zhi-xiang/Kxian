package com.gjmetal.app.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.widget.ImageOverlayView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Description:
 * 图片预览
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/7/2  16:57
 */
public class ImgPreviewUtil {

    private   Boolean [] imgstate;
    //显示图片

    public static ImgPreviewUtil getInstance(){
        return new ImgPreviewUtil();
    }
    public  void showPicker(final ImageOverlayView overlayView, final Context context, int startPosition, List<String> images) {
        mPosition=0;
        imgstate=new Boolean[images.size()];
        for (int x=0;x<images.size();x++){
            imgstate[x]=false;
        }
        final ImageViewer imageViewer = new ImageViewer.Builder<String>(context, images)
                .setFormatter(getCustomFormatter())
                .setImageChangeListener(getImageChangeListener(images, overlayView))
                .setOverlayView(overlayView)
                .setStartPosition(startPosition)
                .setBackgroundColor(ContextCompat.getColor(context,R.color.c202239))
//                .setCustomImageRequestBuilder(ImageRequestBuilder.newBuilderWithResourceId())
                .setCustomDraweeHierarchyBuilder(getGenericDraweeHierarchyBuilder(context))
                .show();
        overlayView.setImageOverlayViewCallBack(new ImageOverlayView.ImageOverlayViewCallBack() {
            @Override
            public void onDownload(final String url) {
                String name = url;
                name = name.substring(name.lastIndexOf("/") + 1, url.length());
                savePicture(name, url, context);
            }

            @Override
            public void onBack() {
                imageViewer.onDismiss();
            }
        });
    }

    private static ImageViewer.Formatter<String> getCustomFormatter() {
        return new ImageViewer.Formatter<String>() {
            @Override
            public String format(String customImage) {
                return customImage;
            }
        };
    }

    public  GenericDraweeHierarchyBuilder getGenericDraweeHierarchyBuilder(Context context) {
        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                .setFailureImage(R.mipmap.ic_common_picture_fail)
                .setProgressBarImage(R.drawable.loading_anim)
                .setPlaceholderImage(R.mipmap.ic_common_picture_fail);
        return hierarchyBuilder;
    }


    private static  int mPosition=0;
    private  ImageViewer.OnImageChangeListener getImageChangeListener(final List<String> images, final ImageOverlayView overlayView) {
        return new ImageViewer.OnImageChangeListener() {
            @Override
            public void onImageChange(final int position) {
                if (overlayView == null) {
                    return;
                }
                if (images != null && images.size() > position) {
                    String image = images.get(position);
                    overlayView.setDescription((position+1) + "");
                    overlayView.setUrlString(image);
                    if (imgstate[position]){
                        overlayView.visible();
                    }else {
                        overlayView.gone();
                    }
                    mPosition=position;
                }
            }

            @Override
            public void onImgFailure(int position) {
                imgstate[position]=true;
                if (mPosition==position){
                    overlayView.visible();
                }
            }
        };
    }

    //Glide保存图片
    public  void savePicture(final String fileName, String url, final Context context) {
        DialogUtil.loadDialog(context);
        Glide.with(context).load(url).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                try {
                    savaFileToSD(fileName, bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //往SD卡写入文件的方法
    public  void savaFileToSD(String filename, byte[] bytes) throws Exception
    {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Constant.DEFAULT_CACHE_FOLDER;
            File dir1 = new File(filePath);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            filename = filePath + "/" + filename;
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename);
            output.write(bytes);
            //将bytes写入到输出流中
            output.close();
            //关闭输出流
            DialogUtil.dismissDialog();
            ToastUtil.showToast("下载成功");
        } else {
            DialogUtil.dismissDialog();
            ToastUtil.showToast("下载失败");
        }
    }


    public static  void getPermissions (final Context context, final PermissionsCallBack permissionsCallBack, final String... permissions)
    {
        RxPermissions rxPermission = new RxPermissions((Activity) context);
        rxPermission.requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            if (NetUtil.checkNet(context)&&permission.name.contains("WRITE_EXTERNAL_STORAGE")) {
                                if (permissionsCallBack!=null)
                                    permissionsCallBack.pass();
                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            if(ValueUtil.isStrNotEmpty(permission.name)&&permission.name.contains("WRITE_EXTERNAL_STORAGE")){
                                if (permissionsCallBack!=null)
                                    permissionsCallBack.pass();
                            }
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            if (permissionsCallBack!=null)
                                permissionsCallBack.notPass();
                        }
                    }
                });
    }
  public   interface PermissionsCallBack{
        void pass();
        void notPass();
    }
}
