package com.example.xinglanqianbao;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import android.hardware.fingerprint.FingerprintManager;
import android.util.Base64;
import android.util.Log;
import javax.crypto.Cipher;

import com.example.xinglanqianbao.tool.ACache;
import com.example.xinglanqianbao.tool.App;
import com.example.xinglanqianbao.tool.BiometricPromptManager;
import com.example.xinglanqianbao.ui.Login.loginActivity;
import com.example.xinglanqianbao.ui.Register.RegisterActivity;
import com.example.xinglanqianbao.databinding.HomeBinding;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private HomeBinding  homeBinding ;
    private Handler handler = new Handler();
    private boolean fingerLoginEnable = false;
    private BiometricPromptManager mManager;
    private ACache aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.home);
        aCache = ACache.get(App.getContext());
        initview();
        checkHardware();
    }

    private void initview(){
        homeBinding.btLogin.setOnClickListener(this);
        homeBinding.btRes.setOnClickListener(this);
        homeBinding.textView5.setOnClickListener(this);
        homeBinding.textView7.setOnClickListener(this);
        homeBinding.textView8.setOnClickListener(this);
    }

    private void checkHardware() {
        if (Build.VERSION.SDK_INT >= 23 ) {
            mManager = BiometricPromptManager.from(this);
            if (mManager.isHardwareDetected() && mManager.hasEnrolledFingerprints() && mManager.isKeyguardSecure()){
                homeBinding.textView5.setText("手机硬件支持指纹登录");
                homeBinding.textView7.setClickable(true);
                homeBinding.textView8.setClickable(true);
            }else {
                homeBinding.textView5.setText("手机硬件不支持指纹登录");
                homeBinding.textView7.setClickable(false);
                homeBinding.textView8.setClickable(false);
            }
        }else {
            homeBinding.textView5.setText("API 低于23,不支持指纹登录");
            homeBinding.textView7.setClickable(false);
            homeBinding.textView8.setClickable(false);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity

    }
    public void onClick(View view) {
        if(view.getId()==R.id.textView5){
            Intent intent = new Intent(this, test.class);
            startActivity(intent);
            finish();
        }
        if(view.getId()==R.id.textView7){
            openFingerLogin("15220229042");
        }
        if (view.getId() == R.id.bt_login) {
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
            finish();
        }
        if (view.getId()==R.id.textView8){
            if (fingerLoginEnable){
                fingerLogin();
            }else {
                Toast.makeText(this, "请先开通指纹登录", Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.bt_res) {
            Intent intent1 = new Intent(this, RegisterActivity.class);
            startActivity(intent1);
            finish();
        }
    }
    private void fingerLogin() {
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(true, new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSucceeded(FingerprintManager.AuthenticationResult result) {
                    try {
                        Cipher cipher = result.getCryptoObject().getCipher();
                        String text = aCache.getAsString("pwdEncode");
                        Log.i("test", "获取保存的加密密码: " + text);
                        byte[] input = Base64.decode(text, Base64.URL_SAFE);
                        byte[] bytes = cipher.doFinal(input);
                        /**
                         * 然后这里用原密码(当然是加密过的)调登录接口
                         */
                        Log.i("test", "解密得出的加密的登录密码: " + new String(bytes));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onSucceeded(android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {
                    try {
                        Cipher cipher = result.getCryptoObject().getCipher();
                        String text = aCache.getAsString("pwdEncode");
                        Log.i("test", "获取保存的加密密码: " + text);
                        byte[] input = Base64.decode(text, Base64.URL_SAFE);
                        byte[] bytes = cipher.doFinal(input);
                        /**
                         * 然后这里用原密码(当然是加密过的)调登录接口
                         */
                        Log.i("test", "解密得出的原始密码: " + new String(bytes));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {
                }

                @Override
                public void onError(int code, String reason) {
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    private void openFingerLogin(final String pwd) {
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(false, new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSucceeded(FingerprintManager.AuthenticationResult result) {
                    try {
                        /**
                         * 加密后的密码和iv可保存在服务器,登录时通过接口根据账号获取
                         */
                        Log.i("test", "原密码: " + pwd);
                        Cipher cipher = result.getCryptoObject().getCipher();
                        byte[] bytes = cipher.doFinal(pwd.getBytes());
                        Log.i("test", "设置指纹时保存的加密密码: " + Base64.encodeToString(bytes,Base64.URL_SAFE));
                        aCache.put("pwdEncode", Base64.encodeToString(bytes,Base64.URL_SAFE));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "设置指纹时保存的加密IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        aCache.put("iv", Base64.encodeToString(iv,Base64.URL_SAFE));
                        fingerLoginEnable = true;
                        Toast.makeText(HomeActivity.this, "开通成功", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onSucceeded(BiometricPrompt.AuthenticationResult result) {
                    try {
                        Cipher cipher = result.getCryptoObject().getCipher();
                        byte[] bytes = cipher.doFinal(pwd.getBytes());
                        //保存加密过后的字符串
                        Log.i("test", "设置指纹保存的加密密码: " + Base64.encodeToString(bytes,Base64.URL_SAFE));
                        aCache.put("pwdEncode", Base64.encodeToString(bytes,Base64.URL_SAFE));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "设置指纹保存的加密IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        aCache.put("iv", Base64.encodeToString(iv,Base64.URL_SAFE));
                        fingerLoginEnable = true;
                        Toast.makeText(HomeActivity.this, "开通成功", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {
                }

                @Override
                public void onError(int code, String reason) {
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

}
