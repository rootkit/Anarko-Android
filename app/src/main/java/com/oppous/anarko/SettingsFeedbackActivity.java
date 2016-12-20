package com.oppous.anarko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;

import okhttp3.Response;

public class SettingsFeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    AKTextView btnCancel, btnSend, btnPrivacy, btnTerms;
    EditText txtFeedBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_feedback);

        btnCancel = (AKTextView) findViewById(R.id.activity_feedback_btnCancel);
        btnSend = (AKTextView) findViewById(R.id.activity_feedback_btnSend);
        btnPrivacy = (AKTextView) findViewById(R.id.activity_feedback_btnPrivacy);
        btnTerms = (AKTextView) findViewById(R.id.activity_feedback_btnTerms);

        btnCancel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnTerms.setOnClickListener(this);
        btnPrivacy.setOnClickListener(this);

        txtFeedBack = (EditText) findViewById(R.id.activity_feedback_txtFeedBack);

        FrameLayout layoutContent = (FrameLayout) findViewById(R.id.activity_settings_layoutFeedback);
        layoutContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    AppManager.hideKeyboard(SettingsFeedbackActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_feedback_btnCancel: {
                this.finish();
            }
                break;
            case R.id.activity_feedback_btnSend:{
                String feedback = txtFeedBack.getText().toString();
                if (feedback.length() > 0){
                    APIManger.getInstance().postFeedBack(feedback, new APIManger.APISuccessListener() {
                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertView newAlert = new AlertView("Error", "Something went wrong... Try Again", "Okay", null, null, SettingsFeedbackActivity.this, AlertView.Style.Alert,null);
                                    newAlert.show();
                                }
                            });
                        }

                        @Override
                        public void onSuccess(Response response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertView newAlert = new AlertView("Thanks for your feedback", "Your feedback has been sent successfully", "Okay", null, null, SettingsFeedbackActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            SettingsFeedbackActivity.this.finish();
                                        }
                                    });
                                    newAlert.show();
                                }
                            });
                        }
                    });
                }else{
                    AlertView newAlert = new AlertView("Warning", "You could not send empty feedback. Please try again.", "Okay", null, null, SettingsFeedbackActivity.this, AlertView.Style.Alert,null);
                    newAlert.show();
                }
            }
                break;
            case R.id.activity_feedback_btnPrivacy:{

            }
                break;
            case R.id.activity_feedback_btnTerms:{

            }
                break;
        }
    }
}
