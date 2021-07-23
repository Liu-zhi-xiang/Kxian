package com.gjmetal.app.util;

import android.Manifest;

import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.my.UpLoadBean;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.widget.dialog.ChoosePhotoDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * 图片浏览
 * Created by huangb on 2019/10/4.
 */

public class ImageUtils {
    //获取图片地址
    public static Observable<UpLoadBean> getPhotoUrl(final BaseActivity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        return rxPermissions
                .request(Manifest.permission.CAMERA)
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) {
                        return aBoolean;
                    }
                })
                .flatMap(new Function<Boolean, Observable<String>>() {
                    @Override
                    public Observable<String> apply(Boolean aBoolean) {
                        return choosePhoto(activity);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<UpLoadBean>>() {
                    @Override
                    public ObservableSource<UpLoadBean> apply(String s) {
                        return upLoadImage(s, activity);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<PhotoInfo>>  getPhotoList(final BaseActivity activity,final List<PhotoInfo> photoInfoList,final int count){
        RxPermissions rxPermissions = new RxPermissions(activity);
        return rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) {
                        return aBoolean;
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<List<PhotoInfo>>>() {
                    @Override
                    public ObservableSource<List<PhotoInfo>> apply(Boolean aBoolean) {
                        return choosePhone(photoInfoList,count);
                    }
                })
                .observeOn(Schedulers.io());
    }

    //上传图片
    public static Observable<UpLoadBean> upLoadImage(final String path, final BaseActivity activity) {
        return Observable.create(new ObservableOnSubscribe<UpLoadBean>() {
            @Override
            public void subscribe(final ObservableEmitter<UpLoadBean> e) {
                File mFile = new File(path);
                RequestBody fbody = RequestBody.create(mFile,MediaType.parse("image/*") );
                RequestBody type = RequestBody.create("1",MediaType.parse("text/plain") );
                Api.getMyService().upLoadFile(fbody, type)
                        .compose(XApi.<BaseModel<UpLoadBean>>getApiTransformer())
                        .compose(XApi.<BaseModel<UpLoadBean>>getScheduler())
                        .subscribe(new ApiSubscriber<BaseModel<UpLoadBean>>() {
                            @Override
                            public void onNext(BaseModel<UpLoadBean> listBaseModel) {
                                e.onNext(listBaseModel.data);
                            }
                            @Override
                            protected void onFail(NetError error) {
                                ToastUtil.showToast(error.getMessage());
                            }
                        });
            }
        });
    }

    /**
     * 选择图片 压缩图片
     *
     * @param mActivity
     * @return
     */
    public static Observable<String> choosePhoto(final BaseActivity mActivity) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> flow) {
                ChoosePhotoDialog choosePhotoDialog = new ChoosePhotoDialog(mActivity, new ChoosePhotoDialog.OnChoosePhotoListener() {
                    @Override
                    public void success(int code, List<PhotoInfo> photoInfoList) {

                        Luban.with(mActivity)
                                .load(new File(photoInfoList.get(0).getPhotoPath()))                                   // 传人要压缩的图片列表
                                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                                .setTargetDir(getExternalStorageDirectory().getPath())                        // 设置压缩后文件存储位置
                                .setCompressListener(new OnCompressListener() { //设置回调
                                    @Override
                                    public void onStart() {
                                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        DialogUtil.waitDialog(mActivity);
                                        flow.onNext(file.getAbsolutePath());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // TODO 当压缩过程出现问题时调用
                                        ToastUtil.showToast("压缩失败");
                                        DialogUtil.dismissDialog();
                                        flow.onError(e);
                                    }
                                }).launch();    //启动压缩

                    }

                    @Override
                    public void failue(int code, String msg) {
                        ToastUtil.showToast(msg);
                        DialogUtil.dismissDialog();
                    }
                });
                choosePhotoDialog.show();
            }
        });

    }

    /**
     *  选择图片
     * @param count  张数
     * @return
     */
    public static Observable<List<PhotoInfo>> choosePhone(final List<PhotoInfo> photoInfoList, final int count){
        return  Observable.create(new ObservableOnSubscribe<List<PhotoInfo>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<PhotoInfo>> e) {
                FunctionConfig functionConfig = new FunctionConfig.Builder()
                        .setEnableCamera(false)
                        .setEnableCrop(false)
                        .setSelected(photoInfoList)
                        .setEnableEdit(false)
                        .setMutiSelectMaxSize(count)
                        .setEnablePreview(false)
                        .build();
                GalleryFinal.openGalleryMuti(Constant.REQUEST_CODE_GALLERY,functionConfig, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                                 e.onNext(resultList);
                    }
                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        e.onNext(null);
                    }
                });
            }
        });
    }
}
