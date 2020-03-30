package com.example.xinglanqianbao.tool;

import android.app.Application;
import android.content.Context;

/**
 * Created by 李福森 2020/03/23
 */
public class App extends Application {

    private String  testurl = "http://183.236.93.154:8081";
    private String produrl ="http://183.236.93.154:8081";

    public String getUrl() {
        return testurl;
    }
    public void setUrl(String url) {
        this.testurl = url;
    }
    public String getProdurl() { return produrl; }
    public void setProdurl(String produrl) { this.produrl = produrl; }

    private static Context context;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        if (instance == null) {
            instance = this;
        }

    }
    public static Context getContext() {
        return context;
    }
    public static App getInstance() {
        return instance;
    }
}
