package com.shane.webviewdemo;

import android.webkit.JavascriptInterface;

import org.json.JSONException;

import wendu.dsbridge.CompletionHandler;

/**
 * Created by du on 16/12/31.
 */

public class JsEchoApi {

    @JavascriptInterface
    public Object syn(Object args) throws JSONException {
        return  Util.checkThread() + " " + args.getClass().getSimpleName();
    }

    @JavascriptInterface
    public void asyn(Object args, CompletionHandler handler){
        handler.complete(Util.checkThread() + " " + args);
    }
}