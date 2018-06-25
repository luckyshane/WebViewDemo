package com.shane.webviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import wendu.dsbridge.DWebView;

/**
 * Created by Shane on 2018/6/22.
 */

public class DSBridgeJsCallNativeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DWebView webView = new DWebView(this);
        setContentView(webView);

        DWebView.setWebContentsDebuggingEnabled(true);
        webView.addJavascriptObject(new JsApi(), null);
        webView.addJavascriptObject(new JsEchoApi(), "echo");

        webView.loadUrl("file:///android_asset/web/js-call-native.html");
    }



}
