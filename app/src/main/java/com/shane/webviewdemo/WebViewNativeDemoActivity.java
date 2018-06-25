package com.shane.webviewdemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Shane on 2018/6/22.
 */

public class WebViewNativeDemoActivity extends Activity {
    private static final String SCHEME = "cmiotjs";     // 这玩意不能有下划线，并且全部为小写，否则识别不出来
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_web);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClientEx());
        webView.setWebChromeClient(new WebChromeClientEx());

        webView.addJavascriptInterface(new JsApi(), "AndroidApi");
        webView.loadUrl("file:///android_asset/web/test_native.html");

        findViewById(R.id.native_invoke_js_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callJS("javascript:add(1, 2)");
            }
        });
    }

    private void callJS(final String js) {
        if (Build.VERSION.SDK_INT < 19) {
            webView.loadUrl(js);
        } else {
            webView.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Toast.makeText(WebViewNativeDemoActivity.this, "call " + js + ", ret: " + value, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class WebViewClientEx extends WebViewClient {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (SCHEME.equals(uri.getScheme())) {
                new AlertDialog.Builder(WebViewNativeDemoActivity.this)
                        .setTitle(checkThread() + " " + uri.getAuthority())
                        .setMessage(uri.getQuery())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class WebChromeClientEx extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(WebViewNativeDemoActivity.this)
                    .setTitle(checkThread() + " Android Alert拦截")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(WebViewNativeDemoActivity.this)
                    .setTitle(checkThread() + " Android Confirm拦截")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            new AlertDialog.Builder(WebViewNativeDemoActivity.this)
                    .setTitle(checkThread() + " Android Prompt拦截")
                    .setMessage(message + " Shane")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm("Shane");
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                            dialog.dismiss();
                        }
                    }).show();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String message = consoleMessage.message();
            new AlertDialog.Builder(WebViewNativeDemoActivity.this)
                    .setTitle(checkThread() + " Android Console拦截")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.destroy();

        super.onDestroy();
    }

    @Keep
    private class JsApi {

        @JavascriptInterface
        public void call(String message) {
            new AlertDialog.Builder(WebViewNativeDemoActivity.this)
                    .setTitle(checkThread() + " Android JsApi")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private String checkThread() {
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        return isMainThread? "主线程" : "子线程";
    }



}
