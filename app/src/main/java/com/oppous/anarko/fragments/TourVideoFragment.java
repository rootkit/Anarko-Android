package com.oppous.anarko.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.oppous.anarko.R;
import com.oppous.anarko.RegTourActivity;
import com.oppous.anarko.RegVerifyActivity;
import com.oppous.anarko.classes.AKTextView;

public class TourVideoFragment extends Fragment  {
    private static final String TAG = "TourVideoFragment";

    private ViewGroup container;
    public  AKTextView btnSkip;

    public TourVideoFragment() {
        // Required empty public constructor
    }


    public static TourVideoFragment newInstance(String param1, String param2) {
        TourVideoFragment fragment = new TourVideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_video, container, false);

        final VideoView videoView = (VideoView) view.findViewById(R.id.fragment_tourVideo_videoView);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.GONE);

                RegTourActivity activity = (RegTourActivity) getActivity();
                activity.skipTourVideo();
            }
        });

        Uri url = Uri.parse("android.resource://com.oppous.anarko/" + R.raw.video);
        videoView.setVideoURI(url);
        videoView.start();

        this.container = container;

        btnSkip = (AKTextView) view.findViewById(R.id.fragment_tourVideo_btnSkip);

        return view;
    }


    public void loadPhoneVerifyActivity(){
        Intent intent = new Intent(getActivity(), RegVerifyActivity.class);
        getActivity().startActivity(intent);
        TourVideoFragment.this.container.setVisibility(View.GONE);
        getActivity().getFragmentManager().beginTransaction().remove(TourVideoFragment.this).commit();
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