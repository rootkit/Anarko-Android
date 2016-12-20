package com.oppous.anarko.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.IdRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oppous.anarko.AKConstants;
import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.R;
import com.oppous.anarko.classes.AKRadioGroup;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.Anarko;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.FontManager;

import okhttp3.Response;

public class ExplorerReportFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ReportFragment";

    private String report_content = "";

    private ViewGroup   container;
    private Button      btn_send;
    private Button      btn_cancel;
    private ListView    listView;
    private AlertView   mAlertView;
    private EditText    txt_report;
    private AKRadioGroup    radioGroup_report;
    private Integer indexOfAnarko;
    public Anarko anarko;

    private AKConstants.Report_Type report_type = AKConstants.Report_Type.Undefined;

    public ExplorerReportFragment() {

    }

    public static ExplorerReportFragment newInstance(String param1, String param2) {
        ExplorerReportFragment fragment = new ExplorerReportFragment();
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
        View view = inflater.inflate(R.layout.fragment_explorer_report, container, false);

        Bundle bundle = getArguments();
        this.indexOfAnarko = bundle.getInt("indexOfAnarko");
//        this.anarko = AppManager.getInstance().arrAnarko.get(this.indexOfAnarko);

        radioGroup_report = (AKRadioGroup) view.findViewById(R.id.fragment_report_radioGroup);
        radioGroup_report.setOnCheckedChangeListener(new AKRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(AKRadioGroup radioGroupPlus, @IdRes int i) {
                switch (i){
                    case R.id.fragment_report_sexual:
                        report_type = AKConstants.Report_Type.Sexual;
                        break;
                    case R.id.fragment_report_violent:
                        report_type = AKConstants.Report_Type.Violent;
                        break;
                    case R.id.fragment_report_hateful:
                        report_type = AKConstants.Report_Type.Hateful;
                        break;
                    case R.id.fragment_report_harmful:
                        report_type = AKConstants.Report_Type.Harmful;
                        break;
                    case R.id.fragment_report_child:
                        report_type = AKConstants.Report_Type.Child_Abuse;
                        break;
                    case R.id.fragment_report_spam:
                        report_type = AKConstants.Report_Type.Spam;
                        break;
                    case R.id.fragment_report_other:
                        report_type = AKConstants.Report_Type.Other;
                        showAlertForOther();
                        break;
                }
            }
        });

        btn_cancel = (Button) view.findViewById(R.id.fragment_report_btn_cancel);
        btn_send = (Button) view.findViewById(R.id.fragment_report_btn_send);
        btn_cancel.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        initFont(view);
        return view;
    }

    private void showAlertForOther(){
        String contentString = getActivity().getResources().getString(R.string.report_alert_content);
        String sendString = getActivity().getResources().getString(R.string.report_send);
        mAlertView = new AlertView("", contentString, sendString, null,
                null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == AlertView.CANCELPOSITION){
                    report_content = txt_report.getText().toString();

                    AppManager.getInstance().hideKeyboard(getActivity());

                    sendReport();
                }

            }
        });

        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.layout_report_alert_form, null);
        txt_report = (EditText) extView.findViewById(R.id.dialog_alert_txt_report);
        mAlertView.addExtView(extView);
        mAlertView.show();
    }

    private void sendReport(){

        if (mAlertView != null) mAlertView.dismiss();

        if (report_type == AKConstants.Report_Type.Undefined){
            AlertView newAlert = new AlertView("Warning", "Report Contents should not be empty", "Okay", null, null, getActivity(), AlertView.Style.Alert,null);
            newAlert.show();
        }else{
            String mobileNumber = AppManager.getInstance().getMobileNumber();
            APIManger.getInstance().reportAnarko(this.anarko.idx, report_type, mobileNumber, report_content, new APIManger.APISuccessListener() {
                @Override
                public void onFailure(String error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSuccess(Response response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Your report has been sent!", Toast.LENGTH_SHORT).show();
                            dismissFragment();
                        }
                    });
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_report_btn_cancel:
                dismissFragment();
                break;
            case R.id.fragment_report_btn_send:
            {
                sendReport();
            }
                break;
            default:
                break;
        }
    }

    public void dismissFragment() {
        ExplorerCommentsFragment fragment = new ExplorerCommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("indexOfAnarko",indexOfAnarko);
        fragment.anarko = this.anarko;
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_timeline_container_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void initFont(View v) {
        Button btn_cancel = (Button) v.findViewById(R.id.fragment_report_btn_cancel);
        btn_cancel.setTypeface(FontManager.getCustomFont(getActivity(), getResources().getString(R.string.font_gunplay)));
        Button btn_send = (Button) v.findViewById(R.id.fragment_report_btn_send);
        btn_send.setTypeface(FontManager.getCustomFont(getActivity(), getResources().getString(R.string.font_gunplay)));

        TextView lbl_terms = (TextView) v.findViewById(R.id.fragment_report_label_terms);
        lbl_terms.setTypeface(FontManager.getCustomFont(getActivity(), getResources().getString(R.string.font_roboto)));

        Spannable spannable = new SpannableString(getResources().getString(R.string.report_terms));
        String str = spannable.toString();
        int iStart = str.indexOf("Community Guidelines.");
        int iEnd = iStart + 21;
        SpannableString ssText = new SpannableString(spannable);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //your code at here.
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.colorAKButton));
            }
        };

        ssText.setSpan(clickableSpan, iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lbl_terms.setText(ssText);
        lbl_terms.setHighlightColor(Color.TRANSPARENT);
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
