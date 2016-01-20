package com.klarna.android.sdk.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.klarna.checkout.sdk.KlarnaCheckout;
import com.klarna.checkout.sdk.SignalListener;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";

    private TextView mStatusText;
    private WebView mWebView;

    private KlarnaCheckout mKlarnaCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatusText = (TextView)findViewById(R.id.status_text);
        mWebView = (WebView)findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mKlarnaCheckout = new KlarnaCheckout(this, mWebView);
//        mKlarnaCheckout.attachToActivity(this);
//        mKlarnaCheckout.setGooglePushMessageToken("test");
//        mKlarnaCheckout.loadCheckoutSnippet("a snippet?");
        mKlarnaCheckout.setSignalListener(new SignalListener() {
            private static final String TAG = "SignalListener";

            @Override
            public void onSignal(String s, JSONArray jsonArray) {
                Log.d(TAG, "Got a signal: " + s + ", with params: " + (jsonArray!=null ? jsonArray.toString() : null));
                mStatusText.setText(s);
            }
        });

        mWebView.loadUrl("https://www.klarnacheckout.com/");
    }

    @Override
    protected void onDestroy() {
        if (mKlarnaCheckout!=null) {
            mKlarnaCheckout.Destroy();
        }
        super.onDestroy();
    }
}
