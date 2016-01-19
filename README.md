# Klarna Checkout SDK Sample App

## SDK Integration
_information provided from JavaDocs_

### KlarnaCheckout

#### Primary checkout object for merchant integration.

#### Standard checkout initialization:

```java
KlarnaCheckout checkout = new KlarnaCheckout( myCurrentActivity );
checkout.setSignalListener(..an implementation of the SignalListener interface..);
checkout.loadCheckoutSnippet( ..the html snippet from merchant server.. );
WebView webView = checkout.getWebView();
```

_(the returned WebView can then be placed in layout placeholder or fragment as required)_

#### "Hybrid" checkout initialization:
```java
KlarnaCheckout checkout = new KlarnaCheckout( myCurrentActivity , myWebView);
checkout.setSignalListener(..an implementation of the SignalListener interface..);
```
_("myWebView" is a WebView instance that has loaded a web page which already contains the checkout code snippet)_

#### Required Manifest entries:
The internal BrowserActivity MUST be declared in the "Application" section with the following name tag:

    android:name="com.klarna.checkout.sdk.browser.BrowserActivity"

#### Standard permissions for core SDK:

    "android.permission.INTERNET"
    "android.permission.WRITE_EXTERNAL_STORAGE"

#### Optional permissions if using GCM:

    "android.permission.WAKE_LOCK"
    "com.google.android.c2dm.permission.RECEIVE"

#### Optional permissions for Cellular switch on android 5+:

    "android.permission.ACCESS_NETWORK_STATE"
    "android.permission.CHANGE_NETWORK_STATE"
    "android.permission.CHANGE_WIFI_STATE"

#### To include the library in your project, edit the build.gradle file and add to the "dependencies" section the line:

    compile (name: 'sdk-release', ext: 'aar')

#### Next add the line to the "flarDirs" section in "repositories":

    dirs '..path to library location..'

#### Lastly sync the gradle in android studio.