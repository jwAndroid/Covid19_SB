package com.example.covid_sb_kr.SideNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.covid_sb_kr.R;

import java.util.Objects;

public class CovidAboutActivity extends AppCompatActivity {

    WebView mWebView;

    //https://psbs.tistory.com/79 <<성능가속

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_covid_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        // 웹뷰 선언
        mWebView = findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        //웹뷰세팅
        WebSettings mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
         // 컨텐츠 사이즈 맞추기

        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWebSettings.setEnableSmoothTransition(true);
        }

        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 브라우저 캐시 허용 여부
        mWebSettings.setAppCacheEnabled(true);

        mWebView.loadUrl("http://ncov.mohw.go.kr/baroView.do?brdId=4&brdGubun=41");


    }//..........................

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
//        //현재 웹뷰 메모리누수를 막으려면 수동으로 삭제할수밖에...
//        ViewParent parent = (ViewParent) CovidAboutActivity.this.getParent();
//
//        if(parent instanceof ViewGroup){
//            ((ViewGroup) parent).removeView(this);
//        }

        mWebView.loadUrl("about:blank");

        try{
            mWebView.removeAllViews();

        }catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }
}