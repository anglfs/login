package com.example.xinglanqianbao.tool;

import android.os.CancellationSignal;

import androidx.annotation.NonNull;

/**
 * Created by 李福森 2020/03/23
 */
interface IBiometricPromptImpl {

    void authenticate(boolean loginFlg, @NonNull CancellationSignal cancel,
                      @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);

}
