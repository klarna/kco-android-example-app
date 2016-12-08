package com.klarna.androidnative;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.klarna.checkout.KlarnaCheckout;
import com.klarna.checkout.SignalListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mFab;

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

                String snippet = readAsset("snippet.html");
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
}
