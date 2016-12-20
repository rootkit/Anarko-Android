package com.oppous.anarko;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oppous.anarko.AnimateText.HTextView;
import com.oppous.anarko.AnimateText.HTextViewType;
import com.oppous.anarko.classes.FontManager;
import com.oppous.anarko.fragments.TourVideoFragment;

public class RegTourActivity extends AppCompatActivity{

    private static final String TAG = "RegTourActivity";
    private TextView lblIntroduction;
    private HTextView hTextView_A, hTextView_B;

    private int idx;
    FrameLayout container;
    private LinearLayout btnShowVideo;

    private TourVideoFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_tour);

        btnShowVideo = (LinearLayout)findViewById(R.id.activity_tour_btnShow);
        // Set Fonts
        TextView lblAppName = (TextView) findViewById(R.id.activity_tour_lbl_appname);
        TextView lblSkipTur = (TextView) findViewById(R.id.activity_tour_lbl_tour);
        TextView btnSkipTur = (TextView) findViewById(R.id.activity_tour_btn_skip);
        lblAppName.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
        lblSkipTur.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
        btnSkipTur.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));

        lblIntroduction = (TextView) findViewById(R.id.activity_tour_lblIntroduction);
        lblIntroduction.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));


        hTextView_A = (HTextView) findViewById(R.id.activity_tour_lblIntroductionTop);
        hTextView_A.setTypeface(FontManager.getInstance(getAssets()).getFont("fonts/gunplay.ttf"));
        hTextView_A.setTextColor(getResources().getColor(R.color.colorAKRed));
        hTextView_A.setAnimateType(HTextViewType.EVAPORATE);

        hTextView_B = (HTextView) findViewById(R.id.activity_tour_lblIntroductionBottom);
        hTextView_B.setTypeface(FontManager.getInstance(getAssets()).getFont("fonts/gunplay.ttf"));
        hTextView_B.setTextColor(getResources().getColor(R.color.colorAKText));
        hTextView_B.setAnimateType(HTextViewType.FALL);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize Data
        idx = 0;
        // Show Fade In/Out Animations for Introduction
        showIntroductionAnimation(lblIntroduction);
    }

    // Privates
    private void showIntroductionAnimation(final TextView view){
        final AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation out = new AlphaAnimation(1.0f, 0.0f);
        in.setDuration(1000);
        out.setDuration(1000);

        AnimationSet as = new AnimationSet(true);
        as.addAnimation(in);
        out.setStartOffset(3000);
        as.addAnimation(out);

        view.startAnimation(as);

        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (idx == 0){
//                    String text = RegTourActivity.this.getResources().getString(R.string.tour_content1);
//                    Spannable spannable = new SpannableString(text);
//                    spannable.setSpan(new ForegroundColorSpan(Color.RED), 18, 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    lblIntroduction.setText(spannable, TextView.BufferType.SPANNABLE);

                    String a = RegTourActivity.this.getResources().getString(R.string.tour_content1_a);
                    String b = RegTourActivity.this.getResources().getString(R.string.tour_content1_b);
                    hTextView_A.animateText(a);
                    hTextView_B.animateText(b);

                }else if (idx == 1){

                    String a = RegTourActivity.this.getResources().getString(R.string.tour_content2_a);
                    String b = RegTourActivity.this.getResources().getString(R.string.tour_content2_b);
                    hTextView_A.animateText(a);
                    hTextView_B.animateText(b);
                    hTextView_A.setTextColor(getResources().getColor(R.color.colorAKText));
                    hTextView_A.setTextColor(getResources().getColor(R.color.colorAKRed));
                }else if (idx == 2){

                    String a = RegTourActivity.this.getResources().getString(R.string.tour_content3_a);
                    Spannable spannable = new SpannableString(a);
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), 8, a.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    String b = RegTourActivity.this.getResources().getString(R.string.tour_content3_b);

                    hTextView_A.setTextColor(getResources().getColor(R.color.colorAKText));
                    hTextView_A.setTextColor(getResources().getColor(R.color.colorAKText));

                    hTextView_A.animateText(spannable);
                    hTextView_B.animateText(b);
                }else if (idx == 3){

                    String a = RegTourActivity.this.getResources().getString(R.string.tour_content4_a);
                    String b = RegTourActivity.this.getResources().getString(R.string.tour_content4_b);
                    hTextView_A.animateText(a);
                    hTextView_B.animateText(b);
                    hTextView_A.setTextColor(getResources().getColor(R.color.colorAKText));
                    hTextView_A.setTextColor(getResources().getColor(R.color.colorAKRed));
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                idx = idx + 1;
                if (idx < 4){
                    showIntroductionAnimation(lblIntroduction);
                }else{
                    lblIntroduction.setText(".");
                    //TODO: show tutorial video
                    onWatchTutorial(null);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    protected void onSkipTourVideo(View v) {
        skipTourVideo();
    }

    public void skipTourVideo(){
        btnShowVideo.setVisibility(View.VISIBLE);
        fragment.loadPhoneVerifyActivity();
    }

    // Button Events
    public void onSkipTutorial(View view){
        Intent intent = new Intent(this, RegVerifyActivity.class);
        startActivity(intent);
    }

    public void  onWatchTutorial(View view){
        // TODO: show tutorial video
        container = (FrameLayout) findViewById(R.id.activity_tour_container);
        container.setVisibility(View.VISIBLE);
        fragment = new TourVideoFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.activity_tour_container, fragment).commit();

        btnShowVideo.setVisibility(View.GONE);

    }
}
