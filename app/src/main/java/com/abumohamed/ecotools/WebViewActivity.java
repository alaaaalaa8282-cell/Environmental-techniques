package com.abumohamed.ecotools;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Base64;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class WebViewActivity extends Activity {

    private WebView webView;
    private GeolocationPermissions.Callback pendingGeoCallback;
    private String pendingGeoOrigin;

    // يعترض تنزيلات blob (تصدير Word) وزر الطباعة لأن WebView ما بيدعمهمش افتراضيًا
    private static final String BRIDGE_JS =
            "(function(){if(window.__ecoBridge)return;window.__ecoBridge=true;"
            + "window.print=function(){Android.printPage();};"
            + "document.addEventListener('click',function(e){var a=e.target.closest&&e.target.closest('a[download]');"
            + "if(!a||!a.href||a.href.indexOf('blob:')!==0)return;e.preventDefault();e.stopPropagation();"
            + "var n=a.getAttribute('download')||'file';var x=new XMLHttpRequest();x.open('GET',a.href);x.responseType='blob';"
            + "x.onload=function(){var r=new FileReader();r.onloadend=function(){var d=String(r.result);"
            + "Android.saveFile(n,x.response.type||'application/octet-stream',d.substring(d.indexOf(',')+1));};r.readAsDataURL(x.response);};"
            + "x.send();},true);})();";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        PermissionHelper.requestMissing(this);

        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setGeolocationEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        webView.addJavascriptInterface(new Bridge(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(BRIDGE_JS, null);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (PermissionHelper.hasLocation(WebViewActivity.this)) {
                    callback.invoke(origin, true, false);
                } else {
                    pendingGeoCallback = callback;
                    pendingGeoOrigin = origin;
                    PermissionHelper.requestMissing(WebViewActivity.this);
                }
            }
        });

        String fileName = getIntent().getStringExtra("file");
        if (fileName == null) {
            fileName = "eis_generator.html";
        }
        webView.loadUrl("file:///android_asset/" + fileName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (pendingGeoCallback != null) {
            pendingGeoCallback.invoke(pendingGeoOrigin, PermissionHelper.hasLocation(this), false);
            pendingGeoCallback = null;
            pendingGeoOrigin = null;
        }
    }

    private class Bridge {
        @JavascriptInterface
        public void saveFile(String name, String mime, String base64) {
            try {
                byte[] data = Base64.decode(base64, Base64.DEFAULT);
                String safe = name.replaceAll("[\\\\/:*?\"<>|]", "_");
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues v = new ContentValues();
                    v.put(MediaStore.Downloads.DISPLAY_NAME, safe);
                    v.put(MediaStore.Downloads.MIME_TYPE, mime);
                    v.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    android.net.Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                    try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                        os.write(data);
                    }
                } else {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    dir.mkdirs();
                    try (FileOutputStream os = new FileOutputStream(new File(dir, safe))) {
                        os.write(data);
                    }
                }
                toast("تم الحفظ في مجلد التنزيلات: " + safe);
            } catch (Exception e) {
                toast("تعذر حفظ الملف: " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void printPage() {
            runOnUiThread(() -> {
                PrintManager pm = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                pm.print("EcoTools", webView.createPrintDocumentAdapter("EcoTools"),
                        new PrintAttributes.Builder().build());
            });
        }

        private void toast(String msg) {
            runOnUiThread(() -> Toast.makeText(WebViewActivity.this, msg, Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
