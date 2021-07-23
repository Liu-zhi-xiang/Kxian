package com.gjmetal.app.ui.alphametal.calculator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.model.alphametal.ComputerResult;
import com.gjmetal.app.model.alphametal.RateComputerModel;
import com.gjmetal.app.model.market.ShareContent;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.QrCodeUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.star.kchart.utils.StrUtil;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description 期权计算器计算结果页面
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-17 16:02
 */

public class ComputerValueActivity extends BaseActivity {
    @BindView(R.id.tvOrientationValue)
    TextView tvOrientationValue; //方向
    @BindView(R.id.tvContranctValue)
    TextView tvContranctValue; //合约
    @BindView(R.id.tvPriceValue)
    TextView tvPriceValue; //行权价格
    @BindView(R.id.tvNewsValue)
    TextView tvNewsValue; //标的最新价
    @BindView(R.id.tvDateValue)
    TextView tvDateValue; //到期日
    @BindView(R.id.tvShiborValue)
    TextView tvShiborValue; //Shibor
    @BindView(R.id.tvFloatValue)
    TextView tvFloatValue; //浮动
    @BindView(R.id.tvComputerResultValue)
    TextView tvComputerResultValue; //计算结果
    @BindView(R.id.tvVolatilityValue)
    TextView tvVolatilityValue; //隐含波动率
    @BindView(R.id.tvExplain)
    TextView tvExplain;//合约说明
    @BindView(R.id.tvCurrentPriceValue)
    TextView tvCurrentPriceValue; //现价
    @BindView(R.id.tvReckonValue)
    TextView tvReckonValue; //估算价值
    @BindView(R.id.llCalculate)
    LinearLayout llCalculate;
    @BindView(R.id.tvShibor)
    TextView tvShibor;
    @BindView(R.id.tvOrientation)
    TextView tvOrientation;
    @BindView(R.id.tvContranct)
    TextView tvContranct;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvNews)
    TextView tvNews;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvFloat)
    TextView tvFloat;
    @BindView(R.id.tvVolatility)
    TextView tvVolatility;
    @BindView(R.id.tvCurrentPrice)
    TextView tvCurrentPrice;
    @BindView(R.id.tvReckon)
    TextView tvReckon;
    @BindView(R.id.rlShareEWM)
    RelativeLayout rlShareEWM;
    @BindView(R.id.ivEwm)
    ImageView ivEwm;
    @BindView(R.id.tvTxjs)
    TextView tvTxjs;

    private boolean isShow = false;//是不是弹出图标
    private ShareDialog shareDialog = null;//分享的dialog
    private String mType; //判断那个页面进入的
    private String mName; //合约名字
    private String mShibor; //利率名字
    private String mTime; //行权日期
    private RateComputerModel mComputerModel;
    private ShareContent shareContent = new ShareContent();//分享的内容
    private final int IMAGESSUCCESS = 1000;//生成图片
    private MyRunnable myRunnable = null;//放在子线程进行处理
    private Thread mythread = null;

    private Bitmap bitmap = null, llCalculatewBitmap = null, rlShareEWMBitmap = null, ivEwmbitmap = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IMAGESSUCCESS:
                    if (isShow) {
                        basetouch.setVisibility(View.GONE);
                    }
                    try {
                        llCalculatewBitmap = PictureMergeManager.getPictureMergeManager().getViewToBitmap(llCalculate);
                        rlShareEWMBitmap = PictureMergeManager.getPictureMergeManager().getViewToBitmap(rlShareEWM);
                        bitmap = PictureMergeManager.getPictureMergeManager().mergeBitmap_TB(llCalculatewBitmap, rlShareEWMBitmap, true);

                        if (isShow) {
                            basetouch.setVisibility(View.VISIBLE);
                        }
                        setShareDialog(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.activity_computer_value);
    }

    @Override
    protected void fillData() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_TEXT_RIGHT, getString(R.string.result));
        isShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        Bundle bundle = getIntent().getExtras();
        mType = bundle.getString("type");
        mName = bundle.getString("name");
        mShibor = bundle.getString("shibor");
        mTime = bundle.getString("time");
        mComputerModel = bundle.getParcelable("model");

        if (mComputerModel.getOptionsDirection().equals("0")) {
            tvOrientationValue.setText(getString(R.string.call_option));
        } else if (mComputerModel.getOptionsDirection().equals("1")) {
            tvOrientationValue.setText(getString(R.string.put_option));
        }
        tvShibor.setText(ValueUtil.isStrNotEmpty(mShibor) ? mShibor : "- -");
        tvContranctValue.setText(ValueUtil.isStrNotEmpty(mName) ? mName : "- -");
        tvPriceValue.setText(ValueUtil.isStrNotEmpty(mComputerModel.getExercisePrice())
                ? mComputerModel.getExercisePrice() : "- -");

        tvShiborValue.setText(ValueUtil.isStrNotEmpty(mComputerModel.getRate())
                ? mComputerModel.getRate() + "%" : "- -");
        tvFloatValue.setText(ValueUtil.isStrNotEmpty(mComputerModel.getFloatNum())
                ? mComputerModel.getFloatNum() + "bp" : "0bp");


        if (mType.equals("CounterPeriodFragment")) {
            tvCurrentPrice.setText(getString(R.string.current_price)+"(￥)");
            tvReckon.setText(getString(R.string.estimated_value)+"(¥)");

        } else if (mType.equals("CounterLMEFragment")) {
            tvCurrentPrice.setText(getString(R.string.current_price)+"($)");
            tvReckon.setText(getString(R.string.estimated_value)+"($)");

        }

        myRunnable = new MyRunnable();
        titleBar.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mythread = new Thread(myRunnable);
                mythread.start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mType.equals("CounterPeriodFragment")) {
                    initRateOptionscal(); //初始化上期所计算结果

                } else if (mType.equals("CounterLMEFragment")) {
                    initRateOptionscal();//初始化LME计算结果
                }
            }
        }).start();


        //设置二维码
        try {
            ivEwmbitmap = QrCodeUtil.addLogo(this, QrCodeUtil.createQRCode(Constant.APP_DIALOG_SHARE_UEL), BitmapFactory.decodeResource(getResources(), R.mipmap.iv_new_erweima_logo));
            ivEwm.setImageBitmap(ivEwmbitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        rlShareEWM.setBackgroundResource(R.color.c2A2D4F);
        tvTxjs.setTextColor(ContextCompat.getColor(this,R.color.cFFFFFF));

    }


    public static void launch(Activity context, String name, String shibor, String type,String time, RateComputerModel model) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("name", name);
            bundle.putString("shibor", shibor);
            bundle.putString("time", time);
            bundle.putParcelable("model", model);
            Router.newIntent(context)
                    .to(ComputerValueActivity.class)
                    .data(bundle)
                    .launch();
        }
    }


    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            handler.sendEmptyMessage(IMAGESSUCCESS);
        }
    }

    //分享
    private void setShareDialog(Bitmap bitmap) {
        shareContent.setBitmap(bitmap);
        shareContent.setUrl(Constant.APP_DIALOG_SHARE_UEL);
        shareDialog = new ShareDialog(2, this, R.style.Theme_dialog, shareContent);
        shareDialog.setCancelable(false);
        shareDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        shareDialog.getWindow().setGravity(Gravity.CENTER);
        shareDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && mythread != null) {
            handler.removeMessages(IMAGESSUCCESS);
            handler.removeCallbacksAndMessages(null);
            if (mythread != null) {
                handler.removeCallbacks(mythread);
                mythread = null;
            }
            handler = null;
        }
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (llCalculatewBitmap != null && llCalculatewBitmap.isRecycled()) {
            llCalculatewBitmap.recycle();
        }
        if (rlShareEWMBitmap != null && rlShareEWMBitmap.isRecycled()) {
            rlShareEWMBitmap.recycle();
        }
        if (ivEwmbitmap != null && ivEwmbitmap.isRecycled()) {
            ivEwmbitmap.recycle();
        }
    }

    //初始化上期所计算结果
    private void initRateOptionscal() {
        RequestBody body = RequestBody.create(GsonUtil.toJson(mComputerModel),MediaType.parse("application/json; charset=utf-8")
                );
        Api.getAlphaMetalService().getRateOptionscal(body)
                .compose(XApi.<BaseModel<ComputerResult>>getApiTransformer())
                .compose(XApi.<BaseModel<ComputerResult>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<ComputerResult>>() {
                    @Override
                    public void onNext(BaseModel<ComputerResult> listBaseModel) {
                        ComputerResult computerResult = listBaseModel.getData();
                        if (ValueUtil.isEmpty(computerResult)) {
                            return;
                        }

                        tvExplain.setText(getString(R.string.fetch)+"("+mName+")"+mComputerModel.getExercisePrice()+getString(R.string.exercise_price)+","+mTime+getString(R.string.quotation_calculation));
                        if (ValueUtil.isStrNotEmpty(computerResult.getLastPrice())) {
                            tvNewsValue.setText(computerResult.getLastPrice()); //标的最新价格
                        } else {
                            tvNewsValue.setText("- -");
                        }

                        if (ValueUtil.isStrNotEmpty(computerResult.getFloatNum())) {
                            tvFloatValue.setText(computerResult.getFloatNum() + "bp");
                        } else {
                            tvFloatValue.setText("+0bp");
                        }
                        tvDateValue.setText(DateUtil.getStringDateByLong(computerResult.getDueDate(), 2)); //到期日期


                        tvComputerResultValue.setText(getString(R.string.result)+"(" +
                                DateUtil.getStringDateByLong(computerResult.getResultTime(), 7) + ")"); //计算结果时间

                        if (ValueUtil.isStrNotEmpty(computerResult.getImpliedVolatility())) {
                            tvVolatilityValue.setText(StrUtil.floatToString(
                                    Float.valueOf(computerResult.getImpliedVolatility()) * 100, 2)); //隐含波动率
                        } else {
                            tvVolatilityValue.setText("- -");
                        }

                        if (ValueUtil.isStrNotEmpty(computerResult.getCurrentPrice())) {
                            tvCurrentPriceValue.setText(computerResult.getCurrentPrice()); //现价
                        } else {
                            tvCurrentPriceValue.setText("- -");
                        }

                        if (ValueUtil.isStrNotEmpty(computerResult.getImputedPrice())) {
                            tvReckonValue.setText(computerResult.getImputedPrice()); //估算价格
                        } else {
                            tvReckonValue.setText("- -");
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        tvNewsValue.setText("- -");
                        tvDateValue.setText("- -");
                        tvComputerResultValue.setText(R.string.result+"(- -)");
                        tvVolatilityValue.setText("- -");
                        tvCurrentPriceValue.setText("- -");
                        tvReckonValue.setText("- -");

                    }
                });
    }


}













