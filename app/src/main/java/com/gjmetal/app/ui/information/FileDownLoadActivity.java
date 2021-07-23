package com.gjmetal.app.ui.information;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.manager.DownLoadManager;
import com.gjmetal.app.model.webview.WebViewFile;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.FileUtils;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.NumberProgressBar;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.router.Router;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：文件下载
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-6-15 17:10
 */
public class FileDownLoadActivity extends BaseActivity {
    @BindView(R.id.ivFileIcon)
    ImageView ivFileIcon;
    @BindView(R.id.tvFileName)
    TextView tvFileName;
    @BindView(R.id.tvSize)
    TextView tvSize;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.btnLoad)
    Button btnLoad;
    @BindView(R.id.progressBar)
    NumberProgressBar progressBar;
    @BindView(R.id.tvLoadState)
    TextView tvLoadState;
    private WebViewFile webViewFile;
    private String fileSize;
    private HttpHandler handler;
    @Override
    protected void initView() {
        webViewFile = (WebViewFile) getIntent().getSerializableExtra(Constant.MODEL);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, webViewFile.getName());
        setContentView(R.layout.acvitity_file_download);
        KnifeKit.bind(this);
        btnLoad.setText(getString(R.string.start_down_load));
    }

    @Override
    protected void fillData() {
        if (ValueUtil.isEmpty(webViewFile)) {
            return;
        }
        tvFileName.setText(ValueUtil.isStrNotEmpty(webViewFile.getName()) ? webViewFile.getName() : "");
        fileSize=GjUtil.fromatFileSize(webViewFile.getSize());
        tvSize.setText(fileSize);
        ivFileIcon.setImageResource(GjUtil.getfileRes(webViewFile.getName()));
    }

    public static void launch(Activity context, WebViewFile webViewFile) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, webViewFile);
        Router.newIntent(context)
                .to(FileDownLoadActivity.class)
                .data(bundle)
                .launch();
    }

    @OnClick(R.id.btnLoad)
    public void loadFile() {
        FileUtils.createFileDir(Constant.WEB_VIEW_FILE);
        String filePath = Constant.WEB_VIEW_FILE + webViewFile.getName();
        String strLoad=btnLoad.getText().toString();
        if(filePath.endsWith(".zip")||filePath.endsWith(".rar")){
            ToastUtil.showToast(getString(R.string.cannot_open_file));
            return;
        }
        if(strLoad.equals(getString(R.string.cancel_down_load))){//取消下载
            if(handler!=null){
                handler.cancel();
            }
        }else if(strLoad.equals(getString(R.string.start_down_load))||strLoad.equals(getString(R.string.again_down_load))){//开始下载
            XLog.d("FileName:", filePath);
            if (FileUtils.fileIsExists(filePath)) {
                FileUtils.delFile(filePath);
            } else {
                FileUtils.createFileDir(Constant.WEB_VIEW_FILE);
            }
            downloadFile(webViewFile.getUrl(), filePath);
            XLog.d("loadUrl:", webViewFile.getUrl());
        }else if(strLoad.equals(getString(R.string.open_down_load))){
            FileUtils.openFileByPath(context,filePath);
        }
    }
    private void downloadFile(String url, final String fileName) {
        handler=DownLoadManager.getInstance().downLoadFile(url, fileName, new DownLoadManager.DownLoadCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                tvLoadState.setText(getString(R.string.load_finish));
                tvLoadState.setVisibility(View.VISIBLE);
                btnLoad.setText(getString(R.string.open_down_load));
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setProgress(0);
                tvLoadState.setVisibility(View.VISIBLE);
                tvLoadState.setText("下载失败，点击重试");
                btnLoad.setText(getString(R.string.again_down_load));
            }
            @Override
            public void onLoading(int progress, long current) {
                progressBar.setProgress(progress);
                String currentLoad=GjUtil.fromatFileSize(current);
                tvLoadState.setVisibility(View.VISIBLE);
                tvLoadState.setText("正在下载("+currentLoad+"/"+fileSize);
                btnLoad.setText(getString(R.string.cancel_down_load));
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
