package com.example.xinglanqianbao.ui.Findpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinglanqianbao.tool.Jsonswith;
import com.example.xinglanqianbao.ui.Login.loginActivity;
import com.example.xinglanqianbao.ui.Register.RegisterActivity;
import com.example.xinglanqianbao.data.DBOpenHelper;
import com.example.xinglanqianbao.databinding.ActivityFindpasswordBinding;
import com.example.xinglanqianbao.network.HttpUtil;
import com.example.xinglanqianbao.MainActivity;
import com.example.xinglanqianbao.R;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;
public class findpassword extends AppCompatActivity implements View.OnClickListener {
    private ActivityFindpasswordBinding findpasswordBinding;
    private DBOpenHelper mDBOpenHelper;
    private boolean islogin ;
    private String gdata ;
    private TextView responseText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findpasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_findpassword);
        mDBOpenHelper = new DBOpenHelper(this);
        initView();
    }
    @SuppressLint("WrongViewCast")
    private void initView(){
        findpasswordBinding.tvCode.setOnClickListener(this);
        findpasswordBinding.ivRegisteractivityBack.setOnClickListener(this);
        findpasswordBinding.btFindpassword.setOnClickListener(this);
        findpasswordBinding.login.setOnClickListener(this);
    }
    private void showResponse(final String response, final int judge) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_registeractivity_back:
                Intent intent1 = new Intent(this, RegisterActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.login:
                Intent intent3 = new Intent(this, loginActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.tv_code:

                break;
            case R.id.bt_findpassword:
                final String phone = findpasswordBinding.etFindUsername.getText().toString().trim();
                final String password = findpasswordBinding.etFindPassword1.getText().toString().trim();
                final  String password1 = findpasswordBinding.etFindPassword2.getText().toString().trim();
                final  String code =findpasswordBinding.etFindPhoneCodes.getText().toString().trim();
                if (!TextUtils.isEmpty(phone) &&!TextUtils.isEmpty(password)&& !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(code)&&phone.length()==11) {
                    new Thread() {
                        public void run() {
                            FormBody requestBody = new FormBody.Builder()
                                    .add("phoneNumber", phone)
                                    .add("password", password)
                                    .add("plainPassword", password1)
                                    .add("authCode", code)
                                    .build();
                            // RequestBody requestBody =  RequestBody.create(form,"phoneNumber"+phone+"?"+"password"+password);
                            HttpUtil.sendOkHttpResponse("http://192.168.7.20:19201/api/user/resetPwd",requestBody, new okhttp3.Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    gdata =response.body().string();
                                    //showResponse(gdata, 2);
                                }
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    // 在这里对异常情况进行处理
                                }
                            });
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run() {
                                    try {
                                        join(1);
                                        Jsonswith.swithjson(gdata);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    try {
                                        join(2);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if(islogin) {
                                        Intent intent2 = new Intent(findpassword.this, MainActivity.class);
                                        startActivity(intent2);
                                        finish();
                                    }else{

                                    }
                                }
                            });
                        }
                    }.start();
                }else {
                    Toast.makeText(findpassword.this,  "验证不通过，信息有误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
