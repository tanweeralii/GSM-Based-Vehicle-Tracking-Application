package com.scelon.vehicletracking.OtherActivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.scelon.vehicletracking.Classes.MainLoader;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.ImpMethods;
import com.scelon.vehicletracking.Utils.TinyDB;

public class AdvanceWebViewActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private Context context;
    private TinyDB tinyDB;

    private String title, url;

    private Toolbar toolbar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_web_view);
        context = this;
        initViews();
        initData();
        findViewById(R.id.refreshLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImpMethods.isConnected(context)) {

                    webView = (WebView) findViewById(R.id.webView);
                    webView.loadUrl(getIntent().getStringExtra("url"));
                    ImpMethods.improveWebViewPerformance(webView);
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            Log.d(TAG + "_where", String.valueOf(1));
                            return super.shouldOverrideUrlLoading(view, request);
                        }

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            MainLoader.Loader(true, findViewById(R.id.LL_loader));
                            Log.d(TAG + "_where", String.valueOf(2));
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            MainLoader.Loader(false, findViewById(R.id.LL_loader));
                            Log.d(TAG + "_where", String.valueOf(3));
                        }
                    });
                } else {
                    MainLoader.Loader(false, findViewById(R.id.LL_loader));
                    MainLoader.Loader(true, findViewById(R.id.somethingWrong));
                }
            }
        });
    }

    private void initData() {
        if (ImpMethods.isConnected(context)) {

            webView = (WebView) findViewById(R.id.webView);
            webView.loadUrl(url);
            ImpMethods.improveWebViewPerformance(webView);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Log.d(TAG + "_where", String.valueOf(1));
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    MainLoader.Loader(true, findViewById(R.id.LL_loader));
                    Log.d(TAG + "_where", String.valueOf(2));
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    MainLoader.Loader(false, findViewById(R.id.LL_loader));
                    Log.d(TAG + "_where", String.valueOf(3));
                }
            });
        } else {
            MainLoader.Loader(false, findViewById(R.id.LL_loader));
            MainLoader.Loader(true, findViewById(R.id.somethingWrong));
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            Log.d(TAG + "_where", String.valueOf(4));
        } else {
            super.onBackPressed();
            Log.d(TAG + "_where", String.valueOf(5));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh: {
                webView.reload();
                return true;
            }
            default:
                return false;
        }
    }

    private void initViews() {
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        webView = findViewById(R.id.webView);
    }

}