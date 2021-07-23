package com.gjmetal.app.widget.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.FileUtils;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.NotificationUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.NumberProgressBar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.tendcloud.tenddata.TCAgent;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * Author: Guimingxing
 * Date: 2017/12/21  14:48
 * Description:版本更新提示
 */
public class VersionDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.tvDialogTitle)
    TextView tvDialogTitle;
    @BindView(R.id.tvDialogContent)
    TextView tvDialogContent;
    @BindView(R.id.btnDialogConfirm)
    Button btnDialogConfirm;
    @BindView(R.id.tvVersionName)
    TextView tvVersionName;
    @BindView(R.id.llHint)
    LinearLayout llHint;
    @BindView(R.id.progressBar)
    NumberProgressBar progressBar;
    @BindView(R.id.rlProgress)
    RelativeLayout rlProgress;
    @BindView(R.id.tvTryAgain)
    TextView tvTryAgain;
    @BindView(R.id.tvOtherDown)
    TextView tvOtherDown;//浏览器更新
    @BindView(R.id.ivClose)
    ImageView ivClose;//取消
    private Context context;
    private String content, title,versionName;
    private String forceUpdate;//0非强制，1强制更新
    private String downUrl;
    private boolean autoUpdate;//自动更新
    private DialogCallBack dialogCallBack;

    public VersionDialog(Context context) {
        super(context);
    }

    public VersionDialog(Context context, int theme) {
        super(context, theme);
    }

    public VersionDialog(Context context,String versionName, String title, String content, String forceUpdate, String downUrl, boolean autoUpdate, DialogCallBack mDialogCallBack) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.title = title;
        this.versionName=versionName;
        this.autoUpdate = autoUpdate;
        this.downUrl = downUrl;
        this.forceUpdate = forceUpdate;
        this.dialogCallBack = mDialogCallBack;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_version_view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        KnifeKit.bind(this);
        initView();
        fillData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private void initView() {
        ivClose.setOnClickListener(this);
        btnDialogConfirm.setOnClickListener(this);
        tvTryAgain.setOnClickListener(this);
        tvOtherDown.setOnClickListener(this);
        if (forceUpdate.equals("N")) {
            ivClose.setVisibility(View.VISIBLE);
            btnDialogConfirm.setVisibility(View.VISIBLE);
        } else if (forceUpdate.equals("Y")) {
            ivClose.setVisibility(View.GONE);
            btnDialogConfirm.setVisibility(View.VISIBLE);
        }

    }

    @SuppressWarnings("deprecation")
    private void fillData() {
        if (title != null) {
            tvDialogTitle.setText(Html.fromHtml(title));
        }
        if(versionName!=null){
            tvVersionName.setText(versionName);
        }
        if (content != null) {
            tvDialogContent.setText(Html.fromHtml(content));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                dialogCallBack.onCancel();
                dismiss();
                break;
            case R.id.tvOtherDown:
                dismiss();
                openBrowserUpdate(context,downUrl);
                break;
            case R.id.btnDialogConfirm:
                if (autoUpdate) {//自动更新，直接安装
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String path = Constant.BASE_DOWN_PATH + Constant.APK_NAME;
                    File file = new File(path);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    App.getContext().startActivity(intent);
                    dismiss();
                } else {
                    if (ValueUtil.isStrEmpty(downUrl)) {
                        ToastUtil.showToast("未获取到下载地址");
                        return;
                    }
                    if (!NetUtil.checkNet(context)) {
                        ToastUtil.showToast(R.string.net_error_hint);
                        return;
                    }
                    if (!NetUtil.isWifi(context)) {
                        new HintDialog(context, "\"当前网络不是Wifi环境，确认要更新？", new com.gjmetal.app.widget.dialog.DialogCallBack() {
                            @Override
                            public void onSure() {
                                startUpdate();
                                startLoadView();
                                dialogCallBack.onSure();
                            }

                            @Override
                            public void onCancel() {
                                dismiss();
                            }
                        }).show();
                    } else {
                        startUpdate();
                        startLoadView();
                        dialogCallBack.onSure();
                    }
                }
                break;
            case R.id.tvTryAgain://点击重试
                if (NetUtil.checkNet(context)) {
                    tvTryAgain.setClickable(false);
                    onlyDownload(downUrl);
                } else {
                    ToastUtil.showToast(R.string.net_error_hint);
                }
                break;
        }
    }


    private void startUpdate() {
        GjUtil.closeMarketTimer();
        Acp.getInstance(context).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        if (NetUtil.checkNet(context)) {
                            onlyDownload(downUrl);
                        } else {
                            ToastUtil.showToast(R.string.net_error_hint);
                        }

                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(context, permissions.toString() + "权限拒绝将无法更新下载", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void startLoadView() {
        btnDialogConfirm.setVisibility(View.GONE);
        rlProgress.setVisibility(View.VISIBLE);
        tvTryAgain.setVisibility(View.GONE);
        tvOtherDown.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if (ValueUtil.isStrNotEmpty(forceUpdate) && forceUpdate.equals("N")) {
                    dismiss();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public interface DialogCallBack {
        void onCancel();

        void onSure();

        void onLoadFinish();
    }

    public void onlyDownload(String url) {
        HttpUtils http = new HttpUtils();
        String fileName = Constant.BASE_DOWN_PATH + Constant.APK_NAME;
        if (FileUtils.fileIsExists(fileName)) {
            FileUtils.delFile(fileName);
        } else {
            FileUtils.createFileDir(Constant.BASE_DOWN_PATH);
        }
        XLog.d("fileName", fileName);
        http.download(url, fileName, true, false, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                dialogCallBack.onLoadFinish();
                String filePath = arg0.result.getPath();
                tvTryAgain.setVisibility(View.GONE);
                tvOtherDown.setVisibility(View.GONE);
                dismiss();
                AppUtil.openAPKFile(context, filePath);
                NotificationUtil.getInstance(context).setNotificationClick(1, filePath);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                GjUtil.startMarketTimer();
                ToastUtil.showToast("下载失败，请检查网络后重试");
                progressBar.setProgress(0);
                tvTryAgain.setVisibility(View.VISIBLE);
                tvOtherDown.setVisibility(View.VISIBLE);
                tvTryAgain.setClickable(true);
                progressBar.setVisibility(View.GONE);
                TCAgent.onError(context, arg0);
                if (ValueUtil.isStrNotEmpty(arg1)) {
                    XLog.d("loaderror---------", arg1);
                }
                NotificationUtil.getInstance(context).cancel(1);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                startLoadView();
                int progress = (int) (current / (total / 100));
                progressBar.setProgress(progress);
                progressBar.setVisibility(View.VISIBLE);
                NotificationUtil.getInstance(context).showNotification(1);
                NotificationUtil.getInstance(context).updateProgress(1, progress);
                NotificationUtil.getInstance(context).updateProgressData(1, total, current);
            }

        });
    }
    /**
     * 用浏览器下载
     *
     * @param context
     * @param apkUrl
     */
    private void openBrowserUpdate(Context context, String apkUrl) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri apk_url = Uri.parse(apkUrl);
            intent.setData(apk_url);
            context.startActivity(intent);//打开浏览器
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
