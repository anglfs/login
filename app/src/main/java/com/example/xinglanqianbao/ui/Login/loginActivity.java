package com.example.xinglanqianbao.ui.Login;
/**
 * Created by 李福森 2020/1/08
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xinglanqianbao.HomeActivity;
import com.example.xinglanqianbao.MainActivity;
import com.example.xinglanqianbao.network.networkcongfig;
import com.example.xinglanqianbao.tool.App;
import com.example.xinglanqianbao.ui.Findpassword.findpassword;
import com.example.xinglanqianbao.ui.Register.RegisterActivity;
import com.example.xinglanqianbao.data.DBOpenHelper;
import com.example.xinglanqianbao.databinding.ActivityLoginBinding;
import com.example.xinglanqianbao.network.HttpUtil;
import com.example.xinglanqianbao.R;
import com.example.xinglanqianbao.tool.Md5;
import com.example.xinglanqianbao.tool.smscode;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import androidx.databinding.DataBindingUtil;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.xinglanqianbao.tool.URIEncode.encodeURIComponent;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding loginBinding;
    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
    private TextView responseText;
    public static final MediaType form = MediaType.parse("application/json;charset=utf-8");
    public String gdata = "";
    public String sdata = "";
    public boolean islogin =true ;
    public  String formdata;
    public  String smsformdata;
    public  String sign;
    private App urlmd ;
    private  String url ;
    private DBOpenHelper mDBOpenHelper;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        urlmd  = (App) this.getApplication();
        url= urlmd.getUrl();
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
        mDBOpenHelper = new DBOpenHelper(this);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
    private void initView() {
        responseText = (TextView)findViewById(R.id.response_text);
        loginBinding.findpassword.setOnClickListener(this);
        loginBinding.login.setOnClickListener(this);
        loginBinding.Register.setOnClickListener(this);
        loginBinding.imageView.setOnClickListener(this);
        loginBinding.textView2.setOnClickListener(this);
    }
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            loginBinding.textView2.setClickable(false);
            loginBinding.textView2.setText(l/1000+"秒");
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            loginBinding.textView2.setText("重新获取");
            //设置可点击
            loginBinding.textView2.setClickable(true);
        }
    }

    private void showResponse(final String response, final int judge) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }
    public String getdata(String phoneNumber, String password,Integer loginDevice,Integer loginSource)throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("mobile", phoneNumber);
        jsonParam.put("validateCode", password);
        jsonParam.put("loginDevice", loginDevice);
        jsonParam.put("loginSource", loginSource);
        return jsonParam.toString().trim();
    }
    synchronized public void logincheck(){
        try {
            JSONObject jsonObject = new JSONObject(gdata);
            code  = jsonObject.getString("code");
            String data =jsonObject.getString("data");
            if(code.equals("0")){
                loginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(loginActivity.this);
                        progressDialog.setIcon(R.mipmap.ic_launcher);
                        progressDialog.setTitle("登录成功");
                        progressDialog.setMessage("加载中...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                    }
                });
                Bundle bundle=new Bundle();
                bundle.putString("code", code);
                bundle.putString("data", data);
                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }else{
                loginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(loginActivity.this);
                        progressDialog.setIcon(R.mipmap.ic_launcher);
                        progressDialog.setTitle("登录失败");
                        progressDialog.setMessage("请检查手机或验证码是否正确");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onClick(View view) {
        final String phone = loginBinding.username.getText().toString().trim();
        final String password = loginBinding.password.getText().toString().trim();
        switch (view.getId()) {
            case R.id.imageView:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.textView2:
              if(!TextUtils.isEmpty(phone) &&phone.length()==11){
                  Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
                  sign = Md5.md5Decode32(encodeURIComponent(phone+"&key=193006250b4c09247ec02eace69f6a2f"));
                  //showResponse(sign, 2);//方便在众多的信息中调试
                  try {
                      smsformdata =  smscode.getsms(1,phone,sign);
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
                  RequestBody requestBody =  RequestBody.create(form, smsformdata);//json请求用的;
                  HttpUtil.sendOkHttpResponse(url+"/xland/mobile/sendMessage",requestBody, new okhttp3.Callback() {
                      @Override
                      public void onResponse(Call call, Response response) throws IOException {
                          sdata =response.body().string();
                         // showResponse(sdata, 2);//方便在众多的信息中调试
                      }
                      @Override
                      public void onFailure(Call call, IOException e) {
                          // 在这里对异常情况进行处理
                      }
                  });
                  myCountDownTimer.start();
              }else{
                  Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
              }

                break;
            case R.id.Register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
            case R.id.findpassword:
                startActivity(new Intent(this, findpassword.class));
                finish();
                break;
            case R.id.login:
                try {
                     formdata =  getdata(phone,password,1,3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)&&phone.length()==11) {
                    new Thread() {
                        public void run() {
                            /*FormBody requestBody = new FormBody.Builder()
                                    .add("mobile", phone)
                                    .add("regDevice", "1")
                                    .add("validateCode", password)
                                    .build();*/
                            RequestBody requestBody =  RequestBody.create(form, formdata);//json请求用的;
                             HttpUtil.sendOkHttpResponse(url+"/xland/user/login",requestBody, new okhttp3.Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    gdata =response.body().string();
                                    logincheck();
                                //   showResponse(gdata, 2);//方便在众多的信息中调试
                                }
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    // 在这里对异常情况进行处理
                                }
                            });
                        }
                    }.start();
                } else {
                    Toast.makeText(this, "请输入正确的手机号或密码不能为空", Toast.LENGTH_SHORT).show();
                }
        }
    }
}






