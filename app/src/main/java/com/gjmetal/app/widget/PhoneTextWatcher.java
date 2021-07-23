package com.gjmetal.app.widget;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.gjmetal.app.util.ValueUtil;


/**
 * 手机号输入显344中间有空格
 *
 * @Author: star
 * @Email: guimingxing@163.com
 * @Date: 2016/11/10 15:25
 */
public class PhoneTextWatcher implements TextWatcher {
    private EditText _text;
    int beforeLen = 0;
    int afterLen = 0;
    private callBackText mCallBack;

    public PhoneTextWatcher(EditText _text, callBackText mCallBack) {
        this._text = _text;
        this.mCallBack = mCallBack;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeLen = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String txt = _text.getText().toString();
        afterLen = txt.length();
        if(ValueUtil.isStrNotEmpty(s.toString())){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                    continue;
                } else {
                    sb.append(s.charAt(i));
                    if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
            if (!sb.toString().equals(s.toString())) {
                int index = start + 1;
                if (sb.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
                _text.setText(sb.toString());
                _text.setSelection(index);
            }

            if (afterLen < beforeLen) {
                if (txt.endsWith(" ")) {
                    _text.setText(new StringBuffer(txt).deleteCharAt(txt.lastIndexOf(" ")).toString());
                    _text.setSelection(_text.getText().length());
                }
            }
        }else {
            txt=null;
        }
        mCallBack.backObj(txt);


    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public interface callBackText {
        void backObj(String s);
    }

}
