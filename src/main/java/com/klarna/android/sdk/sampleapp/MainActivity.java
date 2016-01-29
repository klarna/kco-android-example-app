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

        mStatusText = (TextView)findViewById(R.id.status_text);
        mWebView = (WebView)findViewById(R.id.web_view);
        mWebView.setWebViewClient(getNonRedirectingWebViewClient());

        mKlarnaCheckout = new KlarnaCheckout(this, mWebView);


        mKlarnaCheckout.setSignalListener(new SignalListener() {
            private static final String TAG = "SignalListener";

            @Override
            public void onSignal(String eventName, JSONArray jsonArray) {
                Log.d(TAG, "Got a signal: " + eventName + ", with params: " + (jsonArray!=null ? jsonArray.toString() : null));
                mStatusText.setText(eventName);
                if (eventName.equals(EVENT_COMPLETE)) {
                    try {

                        String url = jsonArray.getJSONObject(0).getString("uri");
                        mKlarnaCheckout.getWebView().loadUrl(url);

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        });

        mWebView.loadUrl("https://www.klarnacheckout.com/");
    }

    /**
     * As the Klarna demo site has redirects we turn this off to prevent external browser taking over.
     *
     * @return
     */
    private WebViewClient getNonRedirectingWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        };
    }


    @Override
    protected void onDestroy() {
        if (mKlarnaCheckout!=null) {
            mKlarnaCheckout.destroy();
        }
        super.onDestroy();
    }
}
