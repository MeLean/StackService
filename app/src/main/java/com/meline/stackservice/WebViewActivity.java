package com.meline.stackservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

public class WebViewActivity extends AppCompatActivity {
    final private static int PERCENTAGE_TO_CHARGE_AD = 50;
    InterstitialAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferencesUtils spUtils = new SharedPreferencesUtils(this, getString(R.string.sh_name));
        String url = spUtils.getStringFromSharedPreferences(getString(R.string.sh_url));

        WebView webView = (WebView) findViewById(R.id.wvSite);
        webView.getSettings().setAppCachePath(WebViewActivity.this.getCacheDir().getPath());
        webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
                return true;
            }
        });

        //running ads
        Random random = new Random();
        int num = random.nextInt(100);
        if (num <= PERCENTAGE_TO_CHARGE_AD) {//50% chance to fire a interstitial ad
            ad = new InterstitialAd(this);
            ad.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
            ad.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    ad.show();
                }
            });
            requestNewInterstitial();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesUtils spUtils = new SharedPreferencesUtils(this, getString(R.string.sh_name));
        int closeInt = spUtils.getIntFromSharedPreferences(getString(R.string.sh_close_time));
        long time = 1000 * closeInt; //milliseconds * closeInt in seconds
        if (time > 0) {
            closeActivityAfter(time);
        }
    }

    private void closeActivityAfter(long millisecondsWait) {
        Runnable closeActivityRunnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        Handler waitUntilCloseHandler = new Handler();
        waitUntilCloseHandler.postDelayed(closeActivityRunnable, millisecondsWait);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        ad.loadAd(adRequest);
    }
}
