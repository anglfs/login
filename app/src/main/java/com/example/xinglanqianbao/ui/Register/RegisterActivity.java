package com.example.xinglanqianbao.ui.Register;
/**
 * Created by 李福森 2020/1/08
 */
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.xinglanqianbao.HomeActivity;
import com.example.xinglanqianbao.MainActivity;
import com.example.xinglanqianbao.data.DBOpenHelper;
import com.example.xinglanqianbao.databinding.ActivityRegisterBinding;
import com.example.xinglanqianbao.network.HttpUtil;
import com.example.xinglanqianbao.R;
import com.example.xinglanqianbao.network.networkcongfig;
import com.example.xinglanqianbao.tool.App;
import com.example.xinglanqianbao.tool.Md5;
import com.example.xinglanqianbao.tool.smscode;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.xinglanqianbao.tool.URIEncode.encodeURIComponent;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityRegisterBinding registerBinding;
    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
    private String realCode;
    public static final MediaType form = MediaType.parse("application/json;charset=utf-8");
    private TextView responseText;
    private DBOpenHelper mDBOpenHelper;
    public  String formdata;
    private  String gdata ;
    public  String smsformdata;
    public  String sign;
    private App urlmd ;
    private  String url ;
    private String code;
    private  boolean islogin =true ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding  = DataBindingUtil.setContentView(this, R.layout.activity_register);
        urlmd  = (App) this.getApplication();
        url= urlmd.getUrl();
        initView();
      //  mDBOpenHelper = new DBOpenHelper(this);
       // mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
       // realCode = Code.getInstance().getCode().toLowerCase();
    }
    @SuppressLint("WrongViewCast")
    private void initView(){
        responseText = (TextView)findViewById(R.id.response_text2);
        registerBinding.tvCode.setOnClickListener(this);
        registerBinding.imageView2.setOnClickListener(this);
        registerBinding.btRegisteractivityRegister.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
    private void showResponse(final String response, final int judge) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }
    public String getdata(String phoneNumber, String password,String code)throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("mobile", phoneNumber);
        jsonParam.put("invitationCode ", "1234");
        jsonParam.put("regDevice", 1);
        jsonParam.put("regSource", 2);
        jsonParam.put("validateCode", code);
        return jsonParam.toString().trim();
    }
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            registerBinding.tvCode.setClickable(false);
            registerBinding.tvCode.setText(l/1000+"秒");
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            registerBinding.tvCode.setText("重新获取");
            //设置可点击
            registerBinding.tvCode.setClickable(true);
        }
    }
    synchronized public void rescheck(){
        try {
            JSONObject jsonObject = new JSONObject(gdata);
            code  = jsonObject.getString("code");
            String data =jsonObject.getString("data");
            if(code.equals("0")){
                RegisterActivity .this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(RegisterActivity.this);
                        progressDialog.setIcon(R.mipmap.ic_launcher);
                        progressDialog.setTitle("注册成功");
                        progressDialog.setMessage("加载中...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                    }
                });
                Bundle bundle=new Bundle();
                bundle.putString("code", code);
                bundle.putString("data", data);
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }else{
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(RegisterActivity.this);
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
        final String phone = registerBinding.etFindUsername.getText().toString().trim();
        final String password = registerBinding.etRegisteractivityPassword1.getText().toString().trim();
        final  String code =registerBinding.etRegisteractivityPhoneCodes.getText().toString().trim();
        switch (view.getId()) {
            case R.id.imageView2:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.tv_code:
                if(!TextUtils.isEmpty(phone) &&phone.length()==11){
                    Toast.makeText(RegisterActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    sign = Md5.md5Decode32(encodeURIComponent(phone+"&key=193006250b4c09247ec02eace69f6a2f"));
                    //showResponse(sign, 2);//方便在众多的信息中调试
                    try {
                        smsformdata =  smscode.getsms(3,phone,sign);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestBody requestBody =  RequestBody.create(form, smsformdata);//json请求用的;
                    HttpUtil.sendOkHttpResponse(url+"/xland/mobile/sendMessage",requestBody, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            gdata =response.body().string();
                         // showResponse(gdata, 2);//方便在众多的信息中调试
                        }
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // 在这里对异常情况进行处理
                        }
                    });
                    myCountDownTimer.start();
                }else{
                    Toast.makeText(RegisterActivity.this, "手机号不正确", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_registeractivity_register:
                try {
                    formdata =  getdata(phone,password,code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(phone) &&!TextUtils.isEmpty(password)&& !TextUtils.isEmpty(code)&&phone.length()==11) {
                   new Thread() {
                        public void run() {
                            /*FormBody requestBody = new FormBody.Builder()
                                    .add("mobile", phone)
                                    .add("nickName", password)
                                    .add("validateCode", code)
                                    .build();*/
                            RequestBody requestBody =  RequestBody.create(form, formdata);
                            HttpUtil.sendOkHttpResponse(url+"/xland/user/register",requestBody, new okhttp3.Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    gdata =response.body().string();
                                   // showResponse(gdata, 2);
                                    rescheck();
                                }
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    // 在这里对异常情况进行处理
                                }
                            });
                        }
                    }.start();
                }else {
                    Toast.makeText(RegisterActivity.this, "信息有误，注册失败", Toast.LENGTH_SHORT).show();
                }
        }
    }
}

