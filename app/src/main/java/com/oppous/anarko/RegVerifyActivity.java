package com.oppous.anarko;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnDismissListener;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.adapters.CountryListAdapter;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.FontManager;
import com.oppous.anarko.classes.GridPasswordView;
import com.oppous.anarko.classes.PasswordType;
import com.oppous.anarko.models.Country;
import com.oppous.anarko.models.CountryList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

public class RegVerifyActivity extends AppCompatActivity implements Comparator<Country>, OnItemClickListener, OnDismissListener {

    private EditText searchEditText;
    private CountryListAdapter adapter;
    private List<Country> allCountriesList;
    private List<Country> selectedCountriesList;
    private ImageView btnFlag;
    private ImageView iconCheck;
    private TextView btnActiveCode;
    private TextView lblCountryCode;
    private TextView lblResendTime;
    private EditText txtPhoneNumber;
    private LinearLayout layoutCountryPicker;
    private LinearLayout layoutRedAlert;
    private GridPasswordView txtSMSpswd;
    private FrameLayout layoutResend;

    private Timer timer;
    private int timeRemaining;
    private boolean isWaiting = false;

    private int countryCode;
    private long nationalMobileNumber;
    private String formatMobileNumber;
    private boolean isValid = false;
    public String registerID;

    private static final int Key_Register_Success = 440;
    private static final int Key_Register_Failed = 441;
    private static final int Key_Login_Success = 442;
    private static final int Key_Login_Failed = 443;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_verify);

        // Initialize Data
        timeRemaining = AKConstants.timeResend;

        // Load Countries
        this.getAllCountries();

        layoutResend = (FrameLayout) findViewById(R.id.activity_phoneverify_layoutResend);
        layoutResend.setVisibility(View.GONE);

        iconCheck = (ImageView) findViewById(R.id.activity_phoneverify_iconCheck);
        iconCheck.setVisibility(View.GONE);

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

                APIManger.getInstance().loginUser(formatMobileNumber, psw, new APIManger.APISuccessListener() {
                    @Override
                    public void onFailure(String error) {
                        implementThread(Key_Login_Failed);
                    }

                    @Override
                    public void onSuccess(Response response) {
                        implementThread(Key_Login_Success);
                    }
                });

            }
        });

        btnActiveCode = (TextView) findViewById(R.id.activity_phoneverify_btnActiveCode);
        btnActiveCode.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));

        lblResendTime = (TextView) findViewById(R.id.activity_phoneverify_lblResendTime);
        lblResendTime.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));

        btnFlag = (ImageView) findViewById(R.id.activity_phoneverify_imgCountryFlag);
        lblCountryCode = (TextView) findViewById(R.id.activity_phoneverify_lblCountryCode);
        lblCountryCode.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
        lblCountryCode.setText(" +" + AKConstants.countryCode + " ▾ ");

        layoutCountryPicker = (LinearLayout) findViewById(R.id.country_picker_layout);
        layoutCountryPicker.setVisibility(View.GONE);

        layoutRedAlert = (LinearLayout) findViewById(R.id.activity_phoneverify_layoutRedAlert);
        layoutRedAlert.getLayoutParams().height = 1;

        ListView countryListView = (ListView) findViewById(R.id.country_code_picker_listview);
        adapter = new CountryListAdapter(this, selectedCountriesList);
        countryListView.setAdapter(adapter);

        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layoutCountryPicker.setVisibility(View.GONE);

                Country country = selectedCountriesList.get(position);
                btnFlag.setImageResource(country.getFlag());
                AKConstants.countryCode = country.getDialCode().replace("+","");
                lblCountryCode.setText(" +" + AKConstants.countryCode + " ▾ ");
            }
        });

        searchEditText = (EditText) findViewById(R.id.country_code_picker_search);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        LinearLayout layoutTop = (LinearLayout) findViewById(R.id.activity_phoneverify_layoutTop);
        layoutTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    layoutCountryPicker.setVisibility(View.GONE);
                    AppManager.hideKeyboard(RegVerifyActivity.this);
                    return true;
                }
                return false;
            }
        });

        txtPhoneNumber = (EditText) findViewById(R.id.activity_phoneverify_txtPhoneNumber);
        txtPhoneNumber.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
        txtPhoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    long inputMobileNumber = 0;
                    try {
                        String result = s.toString();
                        result = result.replaceAll("[\\-\\+\\s\\(\\)]","");
                        inputMobileNumber = Long.valueOf(result);
                        Log.d("trimmed string", result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    countryCode = Integer.parseInt(AKConstants.countryCode);
                    Phonenumber.PhoneNumber mobileNumber = new Phonenumber.PhoneNumber().setCountryCode(countryCode).setNationalNumber(inputMobileNumber);
                    String strPhoneNumber = phoneUtil.format(mobileNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                    isValid = phoneUtil.isValidNumber(mobileNumber);

                    iconCheck.setVisibility(View.VISIBLE);
                    if (isValid == true){
                        iconCheck.setImageResource(R.mipmap.icon_correct);
                        Log.d("phonenumber final = ", strPhoneNumber);
                        nationalMobileNumber = inputMobileNumber;
                        txtPhoneNumber.removeTextChangedListener(this);
                        txtPhoneNumber.setText(strPhoneNumber);
                        txtPhoneNumber.addTextChangedListener(this);
                        //set the position of cursor
                        txtPhoneNumber.setSelection(strPhoneNumber.length());
                        formatMobileNumber = "+"+String.valueOf(countryCode)+String.valueOf(nationalMobileNumber);
                    }else{
                        iconCheck.setImageResource(R.mipmap.icon_cancel);
                        nationalMobileNumber = inputMobileNumber;
                    }
                }
            }
        });

    }

    @SuppressLint("DefaultLocale") private void search(String text) {
        selectedCountriesList.clear();
        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override public int compare(Country lhs, Country rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

    // Privates
    private List<Country> getAllCountries() {
        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<>();
                String allCountriesCode = readEncodedJsonString();
                JSONArray countryArray = new JSONArray(allCountriesCode);
                for (int i = 0; i < countryArray.length(); i++) {
                    JSONObject jsonObject = countryArray.getJSONObject(i);
                    String countryDialCode = jsonObject.getString("dial_code");
                    String countryCode = jsonObject.getString("code");
                    Country country = new Country();
                    country.setCode(countryCode);
                    country.setDialCode(countryDialCode);
                    allCountriesList.add(country);
                }
                Collections.sort(allCountriesList, this);
                selectedCountriesList = new ArrayList<>();
                selectedCountriesList.addAll(allCountriesList);
                return allCountriesList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void startResendTimer(){
        if (timer != null){
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new RecordTask(), 0, 1000);

        btnActiveCode.setText("RESEND CODE");
        btnActiveCode.setTextColor(getResources().getColor(R.color.colorAKButtonDisable));
        btnActiveCode.setEnabled(false);

        layoutResend.setVisibility(View.VISIBLE);
        isWaiting = true;
    }

    private void stopResendTimer(){
        timer.cancel();

        btnActiveCode.setTextColor(getResources().getColor(R.color.colorAKButton));
        btnActiveCode.setEnabled(true);

        layoutResend.setVisibility(View.GONE);
        timeRemaining = AKConstants.timeResend;

        isWaiting = false;
    }

    private void showRedBanner(){
        //TODO: animation to change layoutRedAlert's height as 100px.
        layoutRedAlert.getLayoutParams().height = AKConstants.heightRedAlert;
        layoutRedAlert.requestLayout();
    }

    private void hideRedBanner(){
        layoutRedAlert.getLayoutParams().height = 1;
        layoutRedAlert.requestLayout();
    }


    // Notify Callback
    public void implementThread(final int kind){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (kind){
                    case Key_Register_Failed:
                    {
                        AlertView mAlertView = new AlertView("Error", "Verification Failed?", "Try Again", null, null, RegVerifyActivity.this, AlertView.Style.Alert, RegVerifyActivity.this);
                        mAlertView.setCancelable(true);
                        mAlertView.show();
                    }
                        break;
                    case Key_Register_Success:
                    {
                        startResendTimer();
                    }
                        break;
                    case Key_Login_Failed:
                    {
                        showRedBanner();
                    }
                        break;
                    case Key_Login_Success:
                    {
                        AppManager.getInstance().setMobileNumber(formatMobileNumber);
                        Intent intent = new Intent(RegVerifyActivity.this, CamCaptureActivity.class);
                        startActivity(intent);
                    }
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override // Alert Listener
    public void onItemClick(Object o,int position) {

    }

    @Override
    public void onDismiss(Object o) {
        Toast.makeText(this, "Dismissed", Toast.LENGTH_SHORT).show();
    }

    // Button Events
    public void onCountryFlag(View view){
        layoutCountryPicker.setVisibility(View.VISIBLE);
    }

    public void onSendActiveCode(View view){
        //TODO : phone verification

        if (isWaiting)return;

        if (isValid){

            APIManger.getInstance().registerPhone(formatMobileNumber, "en", new APIManger.APISuccessListener() {
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    implementThread(Key_Register_Failed);
                }

                @Override
                public void onSuccess(Response response) {
                    System.out.println(response);
                    implementThread(Key_Register_Success);
                }
            });

        }else{
            // AlertView
            String mobileNumber = "+"+ AKConstants.countryCode + " " + String.valueOf(nationalMobileNumber);
            AlertView mAlertView = new AlertView("Number confirmation:\n" + mobileNumber, "Is your number above correct?", "EDIT", new String[]{"YES"}, null, this, AlertView.Style.Alert, this);
            mAlertView.setCancelable(true);
            mAlertView.setOnDismissListener(this);
            mAlertView.show();
        }

    }

    // Sub Classes
    private static String readEncodedJsonString() throws java.io.IOException {
        byte[] data = Base64.decode(CountryList.ENCODED_COUNTRY_CODE, Base64.DEFAULT);
        return new String(data, "UTF-8");
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
                        lblResendTime.setText(strTimeRemaining);
                    }
                }
            });
        }
    }
}
