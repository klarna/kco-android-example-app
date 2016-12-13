package com.klarna.androidhybrid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity implements HandshakeListener {

    private static final String KCO_HANDSHAKE = "KCO_HANDSHAKE";
    private static final String KCO_NATIVE = "KCO_NATIVE";

    private FloatingActionButton mFab;
    private WebView mWebView;
    private boolean mConfirmationSnippetLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWebView = setupWebView();
        mWebView.loadUrl("http://klarnacheckout.com/");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private WebView setupWebView() {
        WebView webView = ((WebView) findViewById(R.id.webview));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        }

        webView.setWebViewClient(getNonRedirectingWebViewClient());

        webView.addJavascriptInterface(new JSBridge(this), KCO_NATIVE);
        webView.addJavascriptInterface(new JSBridge(this), KCO_HANDSHAKE);
        return webView;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHandshake(String snippet) {
        if (!mConfirmationSnippetLoading) {
            /*
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("snippet", snippet);
            startActivityForResult(intent, 100);
            mConfirmationSnippetLoading = false;
            */
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String url = data.getStringExtra("url");
            mConfirmationSnippetLoading = true;
            mWebView.loadUrl(url);
        }
    }
}
