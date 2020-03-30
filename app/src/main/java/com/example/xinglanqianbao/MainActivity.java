package com.example.xinglanqianbao;
/**
 * Created by 李福森 2020/1/08
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.xinglanqianbao.ui.Login.loginActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
        private WebView webview ;
        private String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         webview  = findViewById(R.id.home);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebSettings settings= webview.getSettings();
        settings.setDomStorageEnabled(true);

        webview.addJavascriptInterface(new JsInterface(), "control");
        initView();
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initView() {
        // 初始化控件对象
        TextView mBtMainLogout = findViewById(R.id.bt_main_logout);
        // 绑定点击监听器
        mBtMainLogout.setOnClickListener(this);

        try {
            Bundle bundle = this.getIntent().getExtras();
            String code = bundle.getString("code");
            String  data =bundle.getString("data");
            JSONObject jsonObject = new JSONObject(data);
             mobile = jsonObject.getString("mobile");
            mBtMainLogout.setText("http://183.236.93.154:81/mobile="+mobile);
            webview.loadUrl("http://183.236.93.154:81/");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class JsInterface {

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            log("show toast success");
        }
        public String getMobile() {
            return mobile;
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
