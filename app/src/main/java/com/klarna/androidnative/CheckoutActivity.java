package com.klarna.androidnative;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.klarna.checkout.KlarnaCheckout;
import com.klarna.checkout.SignalListener;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity {

    private KlarnaCheckout mCheckout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String snippet = getIntent().getStringExtra("snippet");

        findViewById(R.id.scroller).setVisibility(View.VISIBLE);

        mCheckout = new KlarnaCheckout(CheckoutActivity.this);
        mCheckout.setSignalListener(new SignalListener() {
            @Override
            public void onSignal(String eventName, JSONObject jsonObject) {

                Log.e("T", eventName + ": " + jsonObject.toString());

                Toast.makeText(CheckoutActivity.this, eventName + ": " + jsonObject.toString(), Toast.LENGTH_LONG).show();

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

        mCheckout.setSnippet(snippet);

        ViewGroup placeholder = (ViewGroup) findViewById(R.id.klarna_placeholder);
        placeholder.addView(mCheckout.getView());
    }

    private void loadConfirmationSnippet(String url) {
        Intent data = new Intent();
        data.putExtra("url", url);
        setResult(RESULT_OK, data);
        finish();
    }
}
