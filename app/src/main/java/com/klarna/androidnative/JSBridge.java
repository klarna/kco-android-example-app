package com.klarna.androidnative;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class JSBridge {
    protected static final String TAG = "KCO_JSBridge";


    HandshakeListener listener;

    protected JSBridge(HandshakeListener listener) {
        this.listener = listener;
    }

    @JavascriptInterface
    public void postMessage(String message) {
        postMessage(message, "");
    }

    @JavascriptInterface
    public void postMessage(String message, String origin) {
        handle(message);
    }

    public void handle(String messageObjectAsString) {

        try {
            JSONObject messageObject = new JSONObject(messageObjectAsString);
            String action = messageObject.getString("action");
            if (!"handshake".equals(action)) {
                return;
            }

            if (messageObject.has("snippet")) {

                String snippet = messageObject.getString("snippet");
                listener.onHandshake(snippet);
                Log.i(TAG, "TEST APP HANDSHAKE OK");

            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }


}
