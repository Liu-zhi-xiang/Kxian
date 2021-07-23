package com.gjmetal.app.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

/**
 * Description:
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/7/7  11:22
 */
public class DecimalDigitsInputFilter implements InputFilter {
    private final int decimalDigits;

    public DecimalDigitsInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Log.e("InputFilter","source=="+source+"===start=="+start+"===end==="+end+"==dest==="+dest+"===dstart=="+dstart+"==dend=="+dend);
        int dotPos = -1;
        int len = dest.length();
        for (int i = 0; i < len; i++) {
            char c = dest.charAt(i);
            if (c == '.' || c == ',') {
                dotPos = i;
                break;
            }
        }
        if (dotPos >= 0) {
            // protects against many dots
            if (source.equals(".") || source.equals(","))
            {
                return "";
            }
            if (source.equals("")){
                return null;
            }
            if (dotPos>9){
                ToastUtil.showToast("大于0，最大亿位，最小小数点后6位");
                return "";
            }
            // if the text is entered before the dot
            if (dend <= dotPos) {
                if (dotPos > 8) {
                    ToastUtil.showToast("大于0，最大亿位，最小小数点后6位");
                    return "";
                }
                return null;
            }

            if (len - dotPos > decimalDigits) {
                ToastUtil.showToast("大于0，最大亿位，最小小数点后6位");
                return "";
            }
        }else {
            if (len+source.length()>9){
                if (source.equals(".") ){
                    return null;
                }
                ToastUtil.showToast("大于0，最大亿位，最小小数点后6位");
                return "";
            }
        }
        return null;
    }
}
