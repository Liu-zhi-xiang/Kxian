package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.kit.KnifeKit;

import net.lucode.hackware.magicindicator.MagicIndicator;

import butterknife.BindView;

/**
 * Description：Titlebar封装
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:24
 */
public class Titlebar extends RelativeLayout {
    @BindView(R.id.tvLeft)
    public TextView tvLeft;
    @BindView(R.id.leftLayout)
    public LinearLayout leftLayout;
    @BindView(R.id.tvRight)
    public TextView tvRight;
    @BindView(R.id.rightLayout)
    public LinearLayout rightLayout;
    @BindView(R.id.rlTitle)
    RelativeLayout rlTitle;
    @BindView(R.id.tvTitle)
    AutofitTextView tvTitle;
    @BindView(R.id.ivRight)
    public ImageView ivRight;
    @BindView(R.id.rbFuture)
    RadioButton rbFuture;
    @BindView(R.id.rbSpot)
    RadioButton rbSpot;
    @BindView(R.id.rgView)
    RadioGroup rgView;
    @BindView(R.id.ivLeft)
    public ImageView ivLeft;
    @BindView(R.id.view_shot)
    public View view_shot;
    @BindView(R.id.ivCener)
    ImageView ivCener;
    @BindView(R.id.marketMagicIndicator)
    MagicIndicator marketMagicIndicator;//行情
    @BindView(R.id.ivUpMenu)
    ImageView ivUpMenu;
    @BindView(R.id.tvTabTitle)
    TextView tvTabTitle;
    @BindView(R.id.ivNextMenu)
    ImageView ivNextMenu;
    @BindView(R.id.ivSecondRight)
    ImageView ivSecondRight;
    @BindView(R.id.ivThreeRight)
    ImageView ivThreeRight;
    @BindView(R.id.llTabMenu)
    LinearLayout llTabMenu;
    @BindView(R.id.rgLMEView)
    RadioGroup rgLMEView;
    @BindView(R.id.titleSearchView)
    TitleSearchView titleSearchView;//搜索
    @BindView(R.id.rbFlash)
    RadioButton rbFlash;
    @BindView(R.id.rbCalendar)
    RadioButton rbCalendar;
    @BindView(R.id.rgFlash)
    RadioGroup rgFlash;
    @BindView(R.id.tvSocketHint)
    TextView tvSocketHint;

    private Context mContext;
    private OnClickListener leftOnClickListener;
    private OnClickListener rightOnClickListener;
    private RadioGroup.OnCheckedChangeListener checkedChangeListener;
    private TitleSyle style;

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View v = inflate(context, R.layout.view_title_bar_base, this);
        KnifeKit.bind(v);
    }

    //显示红点是否影藏
    public void setRedMsgView(boolean show) {
        view_shot.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置标题栏的背景色
     *
     * @param colorId
     */
    public void setTitleBackgroundColor(int colorId) {
        rlTitle.setBackgroundColor(ContextCompat.getColor(mContext,colorId));
    }

    /**
     * 设置标题栏的背景图片
     *
     * @param resId
     */
    public void setTitleBackgroundImage(int resId) {
        rlTitle.setBackgroundResource(resId);
    }

    public void hideLeftView(boolean hide) {
        if (hide) {
            leftLayout.setVisibility(View.INVISIBLE);
        } else {
            leftLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideRightView(boolean hide) {
        if (hide) {
            rightLayout.setVisibility(View.INVISIBLE);
        } else {
            rightLayout.setVisibility(View.VISIBLE);
        }
    }

    public void initStyle(TitleSyle titleSyle, String title) {
        this.style = titleSyle;
        initBaseView(titleSyle, title, "");
    }

    public void initStyle(TitleSyle titleSyle, String title, String strRight) {
        this.style = titleSyle;
        initBaseView(titleSyle, title, strRight);
    }

    private void initBaseView(TitleSyle titleSyle, String title, String strRight) {
        if (ValueUtil.isStrNotEmpty(title)) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
        }
        marketMagicIndicator.setVisibility(View.GONE);
        rlTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.c2A2D4F));
        ivSecondRight.setVisibility(View.GONE);
        ivThreeRight.setVisibility(View.GONE);
        switch (titleSyle) {
            case ONLY_TITLE:
                leftLayout.setVisibility(View.INVISIBLE);
                rightLayout.setVisibility(View.INVISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case NO_TITLE:
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.INVISIBLE);
                rightLayout.setVisibility(View.INVISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case LEFT_BTN:
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.INVISIBLE);
                tvLeft.setVisibility(View.VISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case RIGHT_BTN:
                tvRight.setVisibility(View.VISIBLE);
                if (ValueUtil.isStrNotEmpty(strRight)) {
                    tvRight.setText(strRight);
                }
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                ivRight.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case RADIO_GROUP_ADD:
                rgView.setVisibility(View.VISIBLE);
                rbFuture.setText(getResources().getText(R.string.has_change));
                rbSpot.setText(getResources().getText(R.string.all_change));
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(VISIBLE);
                ivRight.setVisibility(View.GONE);

                rgLMEView.setVisibility(GONE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                tvRight.setVisibility(View.VISIBLE);
                if (ValueUtil.isStrNotEmpty(strRight)) {
                    tvRight.setText(strRight);
                } else {
                    tvRight.setText("");
                }
                tvRight.setTextColor(ContextCompat.getColor(mContext,R.color.c9EB2CD));
                tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.f16));
                rgFlash.setVisibility(GONE);
                break;
            case RIGHT_IMAGE:
                tvRight.setVisibility(View.GONE);
                tvLeft.setVisibility(View.VISIBLE);
                ivLeft.setVisibility(View.GONE);
                ivRight.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case CENTER_IMAGE:
                tvRight.setVisibility(View.GONE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                ivRight.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                ivCener.setVisibility(View.VISIBLE);
                ivCener.setBackgroundResource(R.mipmap.login_logo);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case RADIO_GROUP_ADD_SEARCH:
                rgView.setVisibility(View.VISIBLE);
                rbFuture.setText(getResources().getText(R.string.has_change));
                rbSpot.setText(getResources().getText(R.string.all_change));
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(INVISIBLE);
                ivRight.setVisibility(View.GONE);
                ivRight.setBackgroundResource(R.mipmap.nav_button_search);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                tvRight.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case HOME_MENU:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                rightLayout.setVisibility(View.INVISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case HOME_MENU_SEARCH:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                marketMagicIndicator.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setBackgroundResource(R.mipmap.nav_button_search);
                rightLayout.setVisibility(View.VISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case HOME_MARKET://行情
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                marketMagicIndicator.setVisibility(View.GONE);
                llTabMenu.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setBackgroundResource(R.mipmap.ic_navbar_search_nor);
                rightLayout.setVisibility(View.VISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case HOME_HELPER:
                rgView.setVisibility(View.GONE);
                leftLayout.setVisibility(View.GONE);
                marketMagicIndicator.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case HELPER_DETAIL:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                marketMagicIndicator.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                ivLeft.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case HOME_MENU_DATE:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setBackgroundResource(R.drawable.ic_news_calendar_nor);
                rightLayout.setVisibility(View.VISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case MARKET_SEARCH:
                leftLayout.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                ivLeft.setVisibility(View.VISIBLE);
                titleSearchView.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;

            case INFORMATION_SEARCH:
                leftLayout.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                ivLeft.setVisibility(View.VISIBLE);
                titleSearchView.setVisibility(View.GONE);
                titleSearchView.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;

            case MARKET_SEARCH_DETAIL:
                leftLayout.setVisibility(View.GONE);
                titleSearchView.setVisibility(View.VISIBLE);
                titleSearchView.showMarginLeft(false);
                rightLayout.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case INFORMATION_SEARCH_DETAIL:
                leftLayout.setVisibility(View.GONE);
                titleSearchView.setVisibility(View.VISIBLE);
                titleSearchView.showMarginLeft(true);
                rightLayout.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case LOGIN:
                leftLayout.setVisibility(View.GONE);
                ivRight.setBackgroundResource(R.mipmap.nav_button_shut);
                tvTitle.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rlTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.c3));
                rightLayout.setVisibility(VISIBLE);
                ivRight.setVisibility(View.VISIBLE);
                rgFlash.setVisibility(GONE);
                break;
            case LEFT_AND_SEARCH:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(VISIBLE);
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setBackgroundResource(R.mipmap.nav_button_search);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_back);
                tvRight.setVisibility(View.GONE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case WEB_CLOSE:
                tvRight.setVisibility(View.GONE);
                tvLeft.setVisibility(View.VISIBLE);
                ivRight.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_shut);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case LEFT_TEXT_RIGHT:
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.VISIBLE);
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setBackgroundResource(R.mipmap.iv_chart_share);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;

            case INFORMATION_WEBVIEW:
                ivThreeRight.setVisibility(View.VISIBLE);
                ivSecondRight.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.GONE);
                tvLeft.setVisibility(View.VISIBLE);
                ivRight.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_shut);
                ivRight.setBackgroundResource(R.mipmap.ic_navbar_share_nor);
                ivSecondRight.setBackgroundResource(R.mipmap.ic_navbar_font_nor);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case INFORMATION_WEBVIEW_VIP:
                ivThreeRight.setVisibility(View.VISIBLE);
                ivSecondRight.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.GONE);
                tvLeft.setVisibility(View.VISIBLE);
                ivRight.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.nav_button_shut);
//                ivRight.setBackgroundResource(R.mipmap.ic_navbar_share_nor);
                ivSecondRight.setBackgroundResource(R.mipmap.ic_navbar_font_nor);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(GONE);
                break;
            case TABLE_BAR_LEFT:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                rightLayout.setVisibility(View.INVISIBLE);
                rgLMEView.setVisibility(VISIBLE);
                rgFlash.setVisibility(GONE);
                break;
            case FLASH_GROUP:
                rgView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.GONE);
                ivLeft.setVisibility(View.VISIBLE);
                ivLeft.setBackgroundResource(R.mipmap.icon_g_user);
                rightLayout.setVisibility(View.INVISIBLE);
                rgLMEView.setVisibility(GONE);
                rgFlash.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    public RadioGroup getRgFlash(){
        return  rgFlash;
    }
    public RadioButton getGrFlsahLife(){
        return rbFlash;
    }
    public RadioButton getGrFlsahRight(){
        return rbCalendar;
    }
    public LinearLayout getRightView() {
        return rightLayout;
    }

    public ImageView getRightImage() {
        return ivRight;
    }

    public AutofitTextView getTitle() {
        return tvTitle;
    }

    public String getRightText() {
        return this.tvRight.getText().toString();
    }

    public ImageView getIvLeft() {
        return this.ivLeft;
    }

    public TextView getTvSocketHint(){
        return this.tvSocketHint;
    }

    public ImageView getIvSecondRight() {
        return this.ivSecondRight;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public void setLeftBtnOnclick(OnClickListener onClickListener) {
        this.leftOnClickListener = onClickListener;
        leftLayout.setOnClickListener(leftOnClickListener);
    }

    public TitleSearchView getTitleSearchView() {
        return titleSearchView;
    }

    public RadioGroup getRadioGroup() {
        return rgView;
    }

    //搜索输入
    public ClearEditText getEtSearch() {
        return titleSearchView.getEtSearch();
    }

    //取消搜索
    public TextView getCancelSearch() {
        return titleSearchView.getCancelSearch();
    }


    public ImageView getIvThreeRight() {
        return this.ivThreeRight;
    }

    public void setCheckedChangeListener(RadioGroup.OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
        rgView.setOnCheckedChangeListener(checkedChangeListener);
    }

    public void setRightBtnOnClick(OnClickListener onClickListener) {
        this.rightOnClickListener = onClickListener;
        rightLayout.setOnClickListener(rightOnClickListener);
    }

    public void setRightBtnCanSave(boolean canSave) {
        if (canSave) {
            tvRight.setAlpha(1.0f);
        } else {
            tvRight.setAlpha(0.5f);
        }
        rightLayout.setClickable(canSave);
    }

    public enum TitleSyle {
        WEB_CLOSE,//webview 关闭
        ONLY_TITLE,
        NO_TITLE,//无标题和按钮
        LEFT_BTN,//显示返回和标题
        RIGHT_BTN,
        RADIO_GROUP_ADD,//行情自选
        RADIO_GROUP_ADD_SEARCH,
        HOME_MENU,
        HELPER_DETAIL,//沪伦比值详情
        HOME_HELPER,//交易助手
        HOME_MENU_SEARCH,
        HOME_MARKET,//行情
        INFORMATION_WEBVIEW,
        INFORMATION_WEBVIEW_VIP,
        HOME_MENU_DATE,//日历
        RIGHT_IMAGE,//右上角显示图标
        CENTER_IMAGE,
        MARKET_SEARCH,//搜索
        LOGIN,//登录、注册
        LEFT_AND_SEARCH,//搜索+返回按钮
        INFORMATION_SEARCH,//资讯搜索
        INFORMATION_SEARCH_DETAIL,//资讯搜索详情
        MARKET_SEARCH_DETAIL,//行情搜索
        LEFT_TEXT_RIGHT,
        TABLE_BAR_LEFT,
        FLASH_GROUP,//快报-日历
    }
}
