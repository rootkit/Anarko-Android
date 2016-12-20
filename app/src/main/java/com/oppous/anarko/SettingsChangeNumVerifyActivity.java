package com.oppous.anarko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.classes.AKProgressBar;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.GridPasswordView;
import com.oppous.anarko.classes.PasswordType;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

public class SettingsChangeNumVerifyActivity extends AppCompatActivity {

    public  String formattedNumber;
    public  String verificationCode;
    private GridPasswordView txtSMSpswd;

    private AKTextView btnResendCode, btnResendCall;
    private AKProgressBar progressBar;
    private FrameLayout layoutProgress;

    private Timer timer;
    private int timeRemaining;
    private boolean isWaiting = false;

    private static final int Key_ChangeNum_Failed = 234;
    private static final int Key_ChangeNum_Success = 235;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_changenum_verify);

        formattedNumber = getIntent().getStringExtra("formatNewNumber");

        txtSMSpswd = (GridPasswordView) findViewById(R.id.activity_phoneverify_txtSMSpswd);
        txtSMSpswd.togglePasswordVisibility();
        txtSMSpswd.setPasswordType(PasswordType.NUMBER);
        txtSMSpswd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                //TODO: Nothing on here
            }

            @Override
            public void onInputFinish(String psw) {

                layoutProgress.setVisibility(View.VISIBLE);
                progressBar.startSpinning();

                APIManger.getInstance().changePhoneVerify(formattedNumber, psw, new APIManger.APISuccessListener() {
                    @Override
                    public void onFailure(String error) {
                        implementUIThread(Key_ChangeNum_Failed);
                    }

                    @Override
                    public void onSuccess(Response response) {
                        implementUIThread(Key_ChangeNum_Success);
                    }
                });
            }
        });

        btnResendCall = (AKTextView) findViewById(R.id.activity_changeNum_btnCall);
        btnResendCode = (AKTextView) findViewById(R.id.activity_changeNum_btnResend);

        layoutProgress = (FrameLayout) findViewById(R.id.activity_changeNum_layoutProgress);
        progressBar = (AKProgressBar) findViewById(R.id.activity_changeNum_progress);
        progressBar.content = formattedNumber;

        timeRemaining = AKConstants.timeResend;
        startResendTimer();
    }

    private void startResendTimer(){
        if (timer != null){
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new RecordTask(), 0, 1000);

        btnResendCode.setTextColor(getResources().getColor(R.color.colorAKButtonDisable));
        btnResendCode.setEnabled(false);

        isWaiting = true;
    }

    private void stopResendTimer(){
        timer.cancel();

        String prefix = getResources().getString(R.string.changeNum_resend);
        btnResendCode.setText(prefix);
        btnResendCode.setTextColor(getResources().getColor(R.color.colorAKButton));
        btnResendCode.setEnabled(true);

        timeRemaining = AKConstants.timeResend;

        isWaiting = false;
    }

    private void implementUIThread(final int kind){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutProgress.setVisibility(View.GONE);
                progressBar.stopSpinning();

                switch (kind){
                    case Key_ChangeNum_Failed:
                    {
                        AlertView mAlertView = new AlertView("Error", "Verification Failed?", "Try Again", null, null, SettingsChangeNumVerifyActivity.this, AlertView.Style.Alert, null);
                        mAlertView.setCancelable(true);
                        mAlertView.show();
                    }
                    break;
                    case Key_ChangeNum_Success:
                    {
                        AppManager.getInstance().setMobileNumber(formattedNumber);
                        Intent intent = new Intent(SettingsChangeNumVerifyActivity.this, CamCaptureActivity.class);
                        startActivity(intent);
                    }
                    break;
                }
            }
        });
    }
    // Button Events
    protected void onBack(View view){
        this.finish();
    }

    protected void onResend(View view){
        startResendTimer();
    }

    protected void onCall(View view){

    }

    class RecordTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeRemaining = timeRemaining - 1;
                    if (timeRemaining == 0){
                        stopResendTimer();
                    }else{
                        String strTimeRemaining = String.format("%02d:%02d",timeRemaining/60,timeRemaining%60);
                        String prefix = getResources().getString(R.string.changeNum_resend);
                        btnResendCode.setText(prefix + " " + strTimeRemaining);
                    }
                }
            });
        }
    }

}
