package com.gjmetal.app.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description：关键字高亮
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-2  13:32
 */
public class TextUtil {


    /**
     * textview 设置拦截链接跳转
     * @param mContext
     * @param tv
     */
    public static void setLinkClickIntercept(Context mContext, TextView tv) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) tv.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            if (urls.length == 0) {
                return;
            }
            SpannableStringBuilder spannable = new SpannableStringBuilder(text);
            // 只拦截 http:// URI
            LinkedList<String> myurls = new LinkedList<String>();
            for (URLSpan uri : urls) {
                String uriString = uri.getURL();
                if (uriString.indexOf("https://") == 0) {
                    myurls.add(uriString);
                }
            }
            //循环把链接发过去
            for (URLSpan uri : urls) {
                String uriString = uri.getURL();
                if (uriString.indexOf("https://") == 0) {
                    MyURLSpan myURLSpan = new MyURLSpan(mContext,uriString,myurls);
                    spannable.setSpan(myURLSpan, sp.getSpanStart(uri),
                            sp.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            tv.setText(spannable);
        }
    }


    /**
     * 处理TextView中的链接点击事件
     * 链接的类型包括：url，号码，email，地图
     * 这里只拦截url，即 http:// 开头的URI
     */
    public static class MyURLSpan extends ClickableSpan {
        private String mUrl;
        private LinkedList<String> mUrls; // 根据需求，一个TextView中存在多个link的话，这个和我求有关，可已删除掉
        private Context mContext;

        MyURLSpan(Context context, String url, LinkedList<String> urls) {
            mUrl = url;
            mUrls = urls;
            mContext = context;
        }

        @Override
        public void onClick(View widget) {
            // 这里你可以做任何你想要的处理
            // 比如在你自己的应用中用webview打开，而不是打开系统的浏览器
            String info = new String();
            if (mUrls.size() == 1) {
                // 只有一个url，根据策略弹出提示对话框
                info = mUrls.get(0);
            } else {
                // 多个url，弹出选择对话框，意思一下
                info = mUrls.get(0) + "\n" + mUrls.get(1);
            }
//            InformationWebViewActivity.launch((Activity) mContext, new BaseEvent.WebViewBean("", info));
//            Uri uri = Uri.parse(mUrl);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }


    /**
     * 给关键字加高亮颜色
     * @param title
     * @param keyword
     * @return
     */
    public static String matcherSearchTitle(String title,String keyword) {
        String content = title;
        String wordReg = "(?i)" + keyword;//用(?i)来忽略大小写
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile(wordReg).matcher(content);
        while (matcher.find()) {
            //这样保证了原文的大小写没有发生变化
            matcher.appendReplacement(sb, "<font color=\"#D4975C\">" + matcher.group() + "</font>");
        }
        matcher.appendTail(sb);
        content = sb.toString();
        //如果匹配和替换都忽略大小写,则可以用以下方法
        //content = content.replaceAll(wordReg,"<font color=\"#ff0014\">"+keyword+"</font>");
        return content;
    }


    /**
     * 过滤空格和回车
     *
     * @param editText
     */
    public static void setEditTextFilter(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }


}
