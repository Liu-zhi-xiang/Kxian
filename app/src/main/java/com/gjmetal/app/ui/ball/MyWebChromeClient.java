package com.gjmetal.app.ui.ball;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
/**
 * Description：webview 拦截dialog 自定义
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-23  16:49
 */
public class MyWebChromeClient extends WebChromeClient {
	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result) {
		new AlertDialog.Builder(view.getContext())
        .setMessage(message)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        })
        .setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        })
        .create()
        .show();
		return super.onJsConfirm(view, url, message, result);
	}
	
	/**
	 * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
	 */
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			JsResult result) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
		builder.setTitle("温馨提示")
				.setMessage(message).setPositiveButton("确定", null);
		// 不需要绑定按键事件
		// 屏蔽keycode等于84之类的按键
		builder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				return true;
			}
		});
		// 禁止响应按back键的事件
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
		result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
		return true;
		//return super.onJsAlert(view, url, message, result);
	}
	
	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, final JsPromptResult result) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				view.getContext());
		builder.setMessage(message);
		final EditText et = new EditText(view.getContext());
		et.setSingleLine();
		et.setText(defaultValue);
		builder.setView(et);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm(et.getText().toString());
					}
				}).setNeutralButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				});
		builder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				return true;
			}
		});
		// 禁止响应按back键的事件
		// builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
		return true;
	}
}
