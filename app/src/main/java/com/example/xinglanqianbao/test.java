package com.example.xinglanqianbao;


/**
 * Created by 李福森 2020/1/08
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xinglanqianbao.network.networkcongfig;
import com.example.xinglanqianbao.tool.App;
import com.example.xinglanqianbao.ui.Login.loginActivity;

public class test extends AppCompatActivity implements View.OnClickListener {
    private WebView webview ;
    private App urlmd ;
    private  String url ;
    private String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlmd  = (App) this.getApplication();
        url= urlmd.getUrl();
        setContentView(R.layout.activity_test);
        webview  = findViewById(R.id.home);
        mobile ="15220229041";
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebSettings settings= webview.getSettings();
        settings.setDomStorageEnabled(true);
        webview.addJavascriptInterface(new JsInterface(), "control");
        webview.loadUrl("http://192.168.5.178:8080/#/pages/index/index");
        initView();
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity

    }
    private void initView() {
        // 初始化控件对象
        TextView mBtMainLogout = findViewById(R.id.bt_main_logout);
        // 绑定点击监听器
        mBtMainLogout.setOnClickListener(this);

        try {
            mBtMainLogout.setText("http://192.168.5.218:8081/#/Authen/Welcome"+url);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class JsInterface {

        private String mobile;

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(test.this, toast, Toast.LENGTH_SHORT).show();
            log("show toast success");
        }
        public String getMobile() {
            Toast.makeText(test.this, "15220229041", Toast.LENGTH_SHORT).show();
            return mobile;
        }
        public void setMobile(String mobile) {
            this.mobile = mobile;
            Toast.makeText(test.this, mobile, Toast.LENGTH_SHORT).show();
        }
        public void log(final String msg){
            webview.post(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript: log(" + "'" + msg + "'" + ")");
                }
            });
        }

    }
    public void onClick(View view) {
        if (view.getId() == R.id.bt_main_logout) {
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
