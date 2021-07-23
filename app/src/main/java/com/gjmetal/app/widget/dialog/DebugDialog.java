package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;

import butterknife.BindView;

/**
 * Author: Guimingxing
 * Date: 2018/1/8  15:14
 * Description:
 */

public class DebugDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rbHttp)
    RadioButton rbHttp;
    @BindView(R.id.rbHttps)
    RadioButton rbHttps;
    @BindView(R.id.rgHttpSetting)
    RadioGroup rgHttpSetting;
    @BindView(R.id.rbTest)
    RadioButton rbTest;
    @BindView(R.id.rbFromat)
    RadioButton rbFromat;
    @BindView(R.id.rblive)
    RadioButton rblive;
    @BindView(R.id.rbLive2)
    RadioButton rbLive2;
    @BindView(R.id.rbLive3)
    RadioButton rbLive3;
    @BindView(R.id.rbEditIp)
    RadioButton rbEditIp;
    @BindView(R.id.rgUrl)
    RadioGroup rgUrl;
    @BindView(R.id.etIp)
    EditText etIp;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.vCenter)
    View vCenter;
    @BindView(R.id.btnSure)
    Button btnSure;
//    @BindView(R.id.tvTitle)
//    TextView tvTitle;
//    @BindView(R.id.rbHttp)
//    RadioButton rbHttp;
//    @BindView(R.id.rbHttps)
//    RadioButton rbHttps;
//    @BindView(R.id.rgHttpSetting)
//    RadioGroup rgHttpSetting;
//    @BindView(R.id.rbTest)
//    RadioButton rbTest;
//    @BindView(R.id.rbPersonIp)
//    RadioButton rbPersonIp;
//    @BindView(R.id.rbEditIp)
//    RadioButton rbEditIp;
//    @BindView(R.id.rgUrl)
//    RadioGroup rgUrl;
//    @BindView(R.id.etIp)
//    EditText etIp;
//    @BindView(R.id.btnCancel)
//    Button btnCancel;
//    @BindView(R.id.rbFromat)
//    RadioButton rbFromat;
//    @BindView(R.id.btnSure)
//    Button btnSure;

    private int clickPosition = 0;
    private Context mContext;
    private String title;
    private int httpPositon, ipPositon;

    public DebugDialog(Context context) {
        super(context, R.style.TransparentFrameWindowStyle);
    }

    public DebugDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DebugDialog(Context context, String title) {
        super(context, R.style.TransparentFrameWindowStyle);
        this.mContext = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_debug);
        KnifeKit.bind(this);
        initDialog();
        initView();
    }

    private void initDialog() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
    }

    private void initView() {
        if (ValueUtil.isStrNotEmpty(title)) {
            tvTitle.setText(title);
        }
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);

        if (ValueUtil.isStrEmpty(SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_HTTP_POSITION))) {
            httpPositon = 10;
        } else {
            httpPositon = Integer.parseInt(SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_HTTP_POSITION));
        }

        if (ValueUtil.isStrEmpty(SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_IP_POSITION))) {
            ipPositon = Constant.DEBUG_DEFAULT_POS;
        } else {
            ipPositon = Integer.parseInt(SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_IP_POSITION));
        }
        if (httpPositon == 10) {
            rbHttp.setChecked(true);
        } else {
            rbHttps.setChecked(true);
        }

        switch (ipPositon) {
            case 1:
                rbTest.setChecked(true);
                etIp.setVisibility(View.GONE);
                break;
//            case 2:
//                rbFromat.setChecked(true);
//                etIp.setVisibility(View.GONE);
//                break;
            case 3:
                rblive.setChecked(true);
                etIp.setVisibility(View.GONE);
                break;
            case 4:
                rbLive2.setChecked(true);
                etIp.setVisibility(View.GONE);
                break;
            case 5:
                rbLive3.setChecked(true);
                etIp.setVisibility(View.GONE);
                break;
            case 6:
                rbEditIp.setChecked(true);
                String editIp = SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_EDIT_IP);
                if (ValueUtil.isStrNotEmpty(editIp)) {//显示输入的IP
                    etIp.setText(editIp);
                }
                XLog.d("显示输入的IP", editIp);
                etIp.setVisibility(View.VISIBLE);
                break;
        }

        //https 切换
        rgHttpSetting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rbHttp:
                        SharedUtil.put(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_HTTP_POSITION, "10");
                        break;
                    case R.id.rbHttps:
                        SharedUtil.put(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_HTTP_POSITION, "11");
                        break;
                }
            }
        });
        //ip
        rgUrl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbTest:
                        clickPosition = 1;
                        goneEdit();
                        break;
//                    case R.id.rbFromat:
//                        clickPosition = 2;
//                        goneEdit();
//                        break;
                    case R.id.rblive:
                        clickPosition = 3;
                        goneEdit();
                        break;
                    case R.id.rbLive2:
                        clickPosition = 4;
                        goneEdit();
                        break;
                    case R.id.rbLive3:
                        clickPosition = 5;
                        goneEdit();
                        break;
                    case R.id.rbEditIp:
                        clickPosition = 6;
                        etIp.setVisibility(View.VISIBLE);
                        SharedUtil.put(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_IP_POSITION, String.valueOf(clickPosition));
                        break;
                }
            }
        });
    }

    private void goneEdit() {
        etIp.setVisibility(View.GONE);
        SharedUtil.put(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_IP_POSITION, String.valueOf(clickPosition));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSure:
                if (clickPosition == 6) {
                    if (ValueUtil.isStrEmpty(etIp.getText().toString().trim())) {
                        ToastUtil.showToast("请输入IP");
                        return;
                    }
                    SharedUtil.put(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_EDIT_IP, etIp.getText().toString().trim());
                }
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }
}
