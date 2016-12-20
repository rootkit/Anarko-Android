package com.oppous.anarko.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.R;
import com.oppous.anarko.SettingsChangeNumVerifyActivity;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.FontManager;

import java.util.Map;

import okhttp3.Response;


public class ChangeNumFragment extends Fragment implements View.OnClickListener{

    private ViewGroup   container;
    private ImageView iconCheckOld, iconCheckNew;

    private int countryCodeOld, countryCodeNew;
    private long mobileNumberOld, mobileNumberNew;
    private String formatOldNumber, formatNewNumber;
    private boolean isVerifiedOldNumber = false;
    private boolean isValidNewNumber = false;

    private EditText txtCountryCodeOld;
    private EditText txtCountryCodeNew;
    private EditText txtPhoneNumberOld;
    private EditText txtPhoneNumberNew;

    public ChangeNumFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChangeNumFragment newInstance(String param1, String param2) {
        ChangeNumFragment fragment = new ChangeNumFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;

        View view = inflater.inflate(R.layout.fragment_change_num, container, false);
        AKTextView btnCancel = (AKTextView) view.findViewById(R.id.fragment_changeNum_btnCancel);
        btnCancel.setOnClickListener(this);
        AKTextView btnDone = (AKTextView) view.findViewById(R.id.fragment_changeNum_btnDone);
        btnDone.setOnClickListener(this);

        txtCountryCodeOld = (EditText) view.findViewById(R.id.fragment_changeNum_txtCountryCodeOld);
        txtCountryCodeNew = (EditText) view.findViewById(R.id.fragment_changeNum_txtCountryCodeNew);
        txtPhoneNumberOld= (EditText) view.findViewById(R.id.fragment_changeNum_txtPhoneNumberOld);
        txtPhoneNumberNew = (EditText) view.findViewById(R.id.fragment_changeNum_txtPhoneNumberNew);

        txtCountryCodeOld.setTypeface(FontManager.getCustomFont(getActivity(), this.getResources().getString(R.string.font_gunplay)));
        txtCountryCodeNew.setTypeface(FontManager.getCustomFont(getActivity(), this.getResources().getString(R.string.font_gunplay)));
        txtPhoneNumberOld.setTypeface(FontManager.getCustomFont(getActivity(), this.getResources().getString(R.string.font_gunplay)));
        txtPhoneNumberNew.setTypeface(FontManager.getCustomFont(getActivity(), this.getResources().getString(R.string.font_gunplay)));

        iconCheckOld = (ImageView)view.findViewById(R.id.fragment_changeNum_iconCheckOld);
        iconCheckNew = (ImageView)view.findViewById(R.id.fragment_changeNum_iconCheckNew);
        iconCheckOld.setVisibility(View.GONE);
        iconCheckNew.setVisibility(View.GONE);

        txtPhoneNumberOld.addTextChangedListener(new TextWatcher() {
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

                    countryCodeOld = Integer.parseInt(txtCountryCodeOld.getText().toString());
                    Phonenumber.PhoneNumber mobileNumber = new Phonenumber.PhoneNumber().setCountryCode(countryCodeOld).setNationalNumber(inputMobileNumber);
                    String strMobileNumber = phoneUtil.format(mobileNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                    boolean isValid = phoneUtil.isValidNumber(mobileNumber);

                    iconCheckOld.setVisibility(View.VISIBLE);
                    if (isValid == true){

                        Log.d("phonenumber final = ", strMobileNumber);
                        mobileNumberOld = inputMobileNumber;
                        txtPhoneNumberOld.removeTextChangedListener(this);
                        txtPhoneNumberOld.setText(strMobileNumber);
                        txtPhoneNumberOld.addTextChangedListener(this);
                        txtPhoneNumberOld.setSelection(strMobileNumber.length());

                        formatOldNumber = "+"+String.valueOf(countryCodeOld)+String.valueOf(mobileNumberOld);
                        String registeredMobile = AppManager.getInstance().getMobileNumber();
                        if (formatOldNumber.equals(registeredMobile)){
                            iconCheckOld.setImageResource(R.mipmap.icon_correct);
                            isVerifiedOldNumber = true;
                        }else{
                            iconCheckOld.setImageResource(R.mipmap.icon_cancel);
                        }

                    }else{
                        iconCheckOld.setImageResource(R.mipmap.icon_cancel);
                        mobileNumberOld = inputMobileNumber;
                    }
                }
            }
        });

        txtPhoneNumberNew.addTextChangedListener(new TextWatcher() {
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

                    countryCodeNew = Integer.parseInt(txtCountryCodeNew.getText().toString());
                    Phonenumber.PhoneNumber mobileNumber = new Phonenumber.PhoneNumber().setCountryCode(countryCodeNew).setNationalNumber(inputMobileNumber);
                    String strMobileNumber = phoneUtil.format(mobileNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                    boolean isValid = phoneUtil.isValidNumber(mobileNumber);

                    iconCheckNew.setVisibility(View.VISIBLE);
                    if (isValid == true){

                        mobileNumberNew = inputMobileNumber;
                        txtPhoneNumberNew.removeTextChangedListener(this);
                        txtPhoneNumberNew.setText(strMobileNumber);
                        txtPhoneNumberNew.addTextChangedListener(this);
                        txtPhoneNumberNew.setSelection(strMobileNumber.length());

                        formatNewNumber = "+"+String.valueOf(countryCodeNew)+String.valueOf(mobileNumberNew);
                        iconCheckNew.setImageResource(R.mipmap.icon_correct);
                        isValidNewNumber = true;

                    }else{
                        iconCheckNew.setImageResource(R.mipmap.icon_cancel);
                        mobileNumberNew = inputMobileNumber;
                        isValidNewNumber = false;
                    }
                }
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_changeNum_btnCancel:
                // IF Keyboard is shown, then dismiss it.
                AppManager.hideKeyboard(this.getActivity());
                container.setVisibility(View.GONE);
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.fragment_changeNum_btnDone:
                // You have to verify new mobile number with SMS.
            {

                if (isVerifiedOldNumber && isValidNewNumber){
                    AlertView mAlertView = new AlertView("Number Confirmation \n" + formatNewNumber, "Is your number above correct?", "EDIT", new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0){

                                Map<String, String> session = AppManager.getInstance().getSession();
                                String userID = session.get("userID");

                                APIManger.getInstance().changePhone(userID, formatNewNumber, "en", new APIManger.APISuccessListener() {
                                    @Override
                                    public void onFailure(String error) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // TODO: 10/11/2016 show error message
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSuccess(Response response) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(getActivity(), SettingsChangeNumVerifyActivity.class);
                                                intent.putExtra("formatNewNumber",formatNewNumber);

                                                getActivity().startActivityForResult(intent,2);
                                            }
                                        });
                                    }

                                });
                            }
                        }
                    });

                    mAlertView.setCancelable(true);
                    mAlertView.show();

                }else{

                    AlertView mAlertView = new AlertView("Warning", "Your new number does not match.", "Try Again", null, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {

                        }
                    });
                    mAlertView.setCancelable(true);
                    mAlertView.show();
                }

            }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ChangeNumFragment", "onActivityResult");
        ChangeNumFragment.this.container.setVisibility(View.GONE);
        getActivity().getFragmentManager().beginTransaction().remove(ChangeNumFragment.this).commit();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
