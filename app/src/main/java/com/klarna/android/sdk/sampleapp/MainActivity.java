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
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";

    private TextView mStatusText;
    private WebView mWebView;
    public static final String EVENT_COMPLETE = "complete";

    private KlarnaCheckout mKlarnaCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatusText = (TextView) findViewById(R.id.status_text);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });


        //Attach Activity and WebView to checkout
        mKlarnaCheckout = new KlarnaCheckout(this, mWebView);

        //Attach the listener to handle event messages from checkout.
        mKlarnaCheckout.setSignalListener(new SignalListener() {
            private static final String TAG = "SignalListener";

            @Override
            public void onSignal(String eventName, JSONArray data) {
                if (eventName.equals(EVENT_COMPLETE)) {
                    try {
                        String url = data.getJSONObject(0).getString("uri");
                        mKlarnaCheckout.getWebView().loadUrl(url);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        });

        //Load the page containing the Klarna Checkout.
        mWebView.loadUrl("https://www.klarnacheckout.com/");
    }

    @Override
    protected void onDestroy() {
        if (mKlarnaCheckout != null) {
            mKlarnaCheckout.destroy();
        }
        super.onDestroy();
    }
}
