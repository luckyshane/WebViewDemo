package com.shane.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Shane on 2018/6/22.
 */

public class DSBridgeDemoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsbridge);

        findViewById(R.id.js_call_native_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DSBridgeDemoActivity.this, DSBridgeJsCallNativeActivity.class));
            }
        });
        findViewById(R.id.native_call_js_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DSBridgeDemoActivity.this, DSBridgeNativeCallJsActivity.class));
            }
        });

    }



}
