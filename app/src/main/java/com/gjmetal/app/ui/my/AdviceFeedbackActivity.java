package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.my.SinglelineGridAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.model.my.AdviceModel;
import com.gjmetal.app.model.my.PhotoFileModel;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.ContainsEmojiUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.ImageUtils;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.SinglelineGridView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Description: 反馈意见
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/24  13:28
 */
public class AdviceFeedbackActivity extends XBaseActivity {
    @BindView(R.id.gvSingleline)
    SinglelineGridView gvSingleline;
    @BindView(R.id.etExplainToContent)
    EditText etExplainToContent;//意见
    @BindView(R.id.etRegisteredPhone)
    EditText etRegisteredPhone;//手机号
    @BindView(R.id.tvContentLength)
    TextView tvContentLength;//填写意见的字符长度
    @BindView(R.id.tvPhotoLength)
    TextView tvPhotoLength;//图片的张数
    @BindView(R.id.tvCommit)
    Button tvCommit;

    private SinglelineGridAdapter adapter;
    private List<PhotoInfo> mPhotoList;
    private String mPhone, mPhotoLength, mOpinion, mOpinionLength;
    //输入表情前的光标位置
    private int cursorPos;
    //输入表情前EditText中的文本
    private String inputAfterText;
    //是否重置了EditText的内容
    private boolean resetText;

    @Override
    protected void initView() {
        mPhotoList = new ArrayList<>();
        adapter = new SinglelineGridAdapter(this, 4, mPhotoList);
        gvSingleline.setAdapter(adapter);
        gvSingleline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter.getCount() - 1) {
                    setChooseImageOrPhoto();
                }
            }
        });
        adapter.setOnClickDeleteListener(new SinglelineGridAdapter.OnClickDeleteListener() {
            @Override
            public void delete(int x) {
                mPhotoLength = mPhotoList.size() + "/4";
                tvPhotoLength.setText(mPhotoLength);
            }
        });
        etExplainToContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!resetText) {
                    cursorPos = etExplainToContent.getSelectionEnd();
                    // 这里用s.toString()而不直接用s是因为如果用s，
                    // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，
                    // inputAfterText也就改变了，那么表情过滤就失败了
                    inputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    if (count >= 2) {//表情符号的字符长度最小为2
                        CharSequence input = s.subSequence(cursorPos, cursorPos + count);
                        if (ContainsEmojiUtil.isEmojiTwo(input.toString())) {
                            resetText = true;
                            ToastUtil.showToast(R.string.contains_emoji);

                            //是表情符号就将文本还原为输入表情符号之前的内容
                            etExplainToContent.setText(inputAfterText);
                            CharSequence text = etExplainToContent.getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mOpinion = s.toString();
                mOpinionLength = s.length() + "/200";
                tvContentLength.setText(mOpinionLength);
            }
        });
        etRegisteredPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPhone = s.toString();
            }
        });
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uploadImg) {
                    upPhonto("1", "1", "1");
                } else {
                    ToastUtil.showToast(R.string.text_uploadimg);
                }
            }
        });
        ViewUtil.showInputMethodManager(etExplainToContent);
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int setRootView() {
        return R.layout.activity_advice_feedback;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.advice_feedback));
    }

    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            Router.newIntent(context)
                    .to(AdviceFeedbackActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    //设置选择图片与照相
    private void setChooseImageOrPhoto() {
        ImageUtils.getPhotoList(this, mPhotoList, 4).subscribe(new Consumer<List<PhotoInfo>>() {
            @Override
            public void accept(List<PhotoInfo> photoInfos) {
                if (photoInfos != null) {
                    mPhotoList.clear();
                    mPhotoList.addAll(photoInfos);
                }
            }
        });

    }

    private boolean uploadImg = false;

    //上传截图
    private void upPhonto(String thumbnailsH, String thumbnailsW, String type) {
        if (TextUtils.isEmpty(mOpinion) || mOpinion.length() < 10) {
            ToastUtil.showToast(getString(R.string.more_than_10_words));
            uploadImg = false;
            return;
        }
        final AdviceModel adviceModel = new AdviceModel();
        adviceModel.setContent(mOpinion);
        if (TextUtils.isEmpty(mPhone)) {
            mPhone = "";
        }
        adviceModel.setContact(mPhone);
        try {
            adviceModel.setAppVersion(AppUtil.getAppVersionName(context));
            adviceModel.setDeviceVersion(AppUtil.getClientModel());
            adviceModel.setSystemVersion(AppUtil.getOSVersionCode());
            adviceModel.setNetwork(NetUtil.NetType(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mPhotoList == null || mPhotoList.size() == 0) {
            DialogUtil.loadDialog(this);
            advice(adviceModel);
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型
                .addFormDataPart("type", "5");

        for (int x = 0; x < mPhotoList.size(); x++) {
            File file = new File(mPhotoList.get(x).getPhotoPath());
            if (file != null) {
                RequestBody imageBody = RequestBody.create(file,MediaType.parse("multipart/form-data"));
                builder.addFormDataPart("file", file.getName(), imageBody);
            }
        }
        DialogUtil.waitDialog(context);
        uploadImg = true;
        List<MultipartBody.Part> parts = builder.build().parts();
        Api.getMyService().goUploadPictures(parts).compose(XApi.<BaseModel<List<PhotoFileModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<PhotoFileModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<PhotoFileModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<PhotoFileModel>> listBaseModel) {
                        List<PhotoFileModel> filebeans = listBaseModel.data;
                        StringBuilder imgs = new StringBuilder();
                        if (filebeans != null) {
                            for (int x = 0; x < filebeans.size(); x++) {
                                if (x == 0) {
                                    imgs = new StringBuilder(filebeans.get(x).getUrl());
                                } else {
                                    imgs.append(",").append(filebeans.get(x).getUrl());
                                }
                            }
                            adviceModel.setImages(imgs.toString());
                            advice(adviceModel);
                        } else {
                            ToastUtil.showToast(getString(R.string.Image_upload_failed));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        uploadImg = false;
                        ToastUtil.showToast(error.getMessage());
                    }
                });
    }

    //提交反馈
    private void advice(AdviceModel adviceModel) {
        RequestBody body = RequestBody.create(GsonUtil.toJson(adviceModel),MediaType.parse("application/json; charset=utf-8")
                );
        Api.getMyService().complaintAndAdvice(body).compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> listBaseModel) {
                        uploadImg = false;
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(R.string.txt_submit_success);
                        finish();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        uploadImg = false;
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoLength = mPhotoList.size() + "/4";
        tvPhotoLength.setText(mPhotoLength);
        adapter.notifyDataSetChanged();
    }

}
