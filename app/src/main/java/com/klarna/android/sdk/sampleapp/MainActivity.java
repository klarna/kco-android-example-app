package com.klarna.android.sdk.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.klarna.checkout.KlarnaCheckout;
import com.klarna.checkout.SignalListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private KlarnaCheckout mKlarnaCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);

        this.initKlarnaCheckout();

        //Load the page containing the Klarna Checkout.
        mWebView.loadUrl("https://www.klarnacheckout.com/");
    }

    protected void initKlarnaCheckout() {
        //Attach Activity and WebView to checkout
        mKlarnaCheckout = new KlarnaCheckout(this, "kco-android-example://checkout");
        mKlarnaCheckout.setWebView(mWebView);

        //Attach the listener to handle event messages from checkout.
        mKlarnaCheckout.setSignalListener(new SignalListener() {
            @Override
            public void onSignal(String eventName, JSONObject jsonObject) {
                if (eventName.equals("complete")) {
                    try {
                        String url = jsonObject.getString("uri");
                        mWebView.loadUrl(url);
                    } catch (JSONException e) {
                        Log.e(e.getMessage(), e.toString());
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mKlarnaCheckout != null) {
            mKlarnaCheckout.destroy();
        }
        super.onDestroy();
    }
}
