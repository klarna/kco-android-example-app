package com.klarna.androidnative;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.klarna.checkout.KlarnaCheckout;
import com.klarna.checkout.SignalListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements HandshakeListener {

    private static final String KCO_HANDSHAKE = "KCO_HANDSHAKE";
    private static final String KCO_NATIVE = "KCO_NATIVE";

    private FloatingActionButton mFab;
    private String mSnippet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFab.hide();

                findViewById(R.id.scroller).setVisibility(View.VISIBLE);

                String snippet = mSnippet;
                KlarnaCheckout checkout = new KlarnaCheckout(MainActivity.this);
                checkout.setSignalListener(new SignalListener() {
                    @Override
                    public void onSignal(String eventName, JSONObject jsonObject) {
                        if (eventName.equals("complete")) {
                            try {
                                String url = jsonObject.getString("uri");
                                loadConfirmationSnippet(url);
                            } catch (JSONException e) {
                                Log.e(e.getMessage(), e.toString());
                            }
                        }
                    }
                });

                checkout.setSnippet(snippet);

                ViewGroup placeholder = (ViewGroup) findViewById(R.id.klarna_placeholder);
                placeholder.addView(checkout.getView());
            }
        });
        mFab.hide();

        WebView webView = setupWebView();
        webView.loadUrl("http://klarnacheckout.com/");
    }

    private WebView setupWebView() {
        WebView webView = ((WebView) findViewById(R.id.webview));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
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



    private void loadConfirmationSnippet(String url) {
        Log.e("MainActivity", "loadConfirmationSnippet: " + url);
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

    public String readAsset(String assetFileName) {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = getAssets().open(assetFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
        } catch (IOException ex) {
            Log.e("MainActivity", "Unable to read " + assetFileName + " asset: " + ex.getMessage(), ex);
        }
        return buf.toString();
    }

    @Override
    public void onHandshake(String snippet) {
        mSnippet = snippet;
        mFab.show();
    }

    @Override
    public void onBackPressed() {
        View view = findViewById(R.id.scroller);
        if (mSnippet != null && view.getVisibility() == View.VISIBLE) {
            mFab.show();
            view.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }
}
