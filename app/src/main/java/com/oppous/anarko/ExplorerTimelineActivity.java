package com.oppous.anarko;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oppous.anarko.RecyclerView.RecyclerViewPager;
import com.oppous.anarko.VideoList.ItemsProvider;
import com.oppous.anarko.VideoList.ListItem;
import com.oppous.anarko.VideoList.RecyclerViewItemPositionGetter;
import com.oppous.anarko.VideoList.SingleListViewItemActiveCalculator;
import com.oppous.anarko.classes.AKProgressBar;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.Anarko;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.FontManager;
import com.oppous.anarko.classes.ShareIntentsDialog;
import com.oppous.anarko.fragments.ExplorerCommentsFragment;
import com.oppous.anarko.holder.BaseViewHolder;
import com.oppous.anarko.holder.VideoViewHolder;
import com.oppous.anarko.holder.ViewHolderFactory;
import com.oppous.anarko.interfaces.VideoViewHolderListener;
import com.oppous.anarko.models.BaseItem;
import com.oppous.anarko.models.VideoItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import okhttp3.Response;

public class ExplorerTimelineActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "TimelineActivity";

    private FrameLayout         container_download;
    private FrameLayout         container_fragment;
    private FrameLayout         container_tryagain;

    private AKProgressBar       progressBar_refresh;
    private ProgressBar         progressBar_upload;
    private LinearLayout        statusBar;

    private ImageView btnComments, btnExplorer, btnCamera;
    private AKTextView btnShare;

    /*Test Download Progress*/
    public  int                 duration = 0;
    public  Timer               timer;
    public  boolean             upload_flag = false;

    @Bind(R.id.recycler_view)    RecyclerViewPager mRecyclerView;
    @Bind(R.id.activity_timeline_labelCounts) TextView lblCount;

    @Bind(R.id.activity_timeline_container_search) FrameLayout layoutSearch;
    @Bind(R.id.activity_timeline_txt_search) EditText txtSearch;
    @Bind(R.id.activity_timeline_btn_search) TextView btnSearch;
    @Bind(R.id.activity_timeline_btnRelated) AKTextView btnRelated;

    private int mScrollState;
    private int indexPage = 0;
    private SingleListViewItemActiveCalculator mCalculator;
    final MyAdapter adapter = new MyAdapter(null);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

    private CountDownTimer timerTag;
    private String strRelatedTags;
    public List<Anarko> arrAnarko = new ArrayList<Anarko>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_timeline);
        ButterKnife.bind(this);

        btnComments = (ImageView) findViewById(R.id.activity_timeline_btn_comments);
        btnExplorer = (ImageView) findViewById(R.id.activity_timeline_btn_explorer);
        btnCamera = (ImageView) findViewById(R.id.activity_timeline_btn_camera);
        btnShare = (AKTextView) findViewById(R.id.activity_timeline_btn_share);

        btnComments.setOnClickListener(this);
        btnExplorer.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        layoutSearch.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnRelated.setOnClickListener(this);

        txtSearch.setTypeface(FontManager.getCustomFont(this, getResources().getString(R.string.font_gunplay)));
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){

                    onSearch();

                    return true;
                }
                return false;
            }
        });

        progressBar_refresh = (AKProgressBar) findViewById(R.id.activity_timeline_progressbar_refresh);

        statusBar = (LinearLayout) findViewById(R.id.activity_timeline_container_status);
        statusBar.setVisibility(View.GONE);

        container_download = (FrameLayout) findViewById(R.id.activity_timeline_container_download);
        container_download.setVisibility(View.GONE);

        container_fragment = (FrameLayout) findViewById(R.id.activity_timeline_container_fragment);
        container_fragment.setVisibility(View.GONE);

        /*Upload*/
        progressBar_upload = (ProgressBar) findViewById(R.id.activity_timeline_progressbar_upload);
        container_tryagain = (FrameLayout) findViewById(R.id.activity_timeline_container_tryagain);
        progressBar_upload.setVisibility(View.GONE);
        container_tryagain.setVisibility(View.GONE);
        upload_flag = getIntent().getBooleanExtra("upload", false);
        if (upload_flag) {
            disableButtons();
            progressBar_upload.setVisibility(View.VISIBLE);
        }

        mCalculator = new SingleListViewItemActiveCalculator(adapter,
                new RecyclerViewItemPositionGetter(layoutManager, mRecyclerView));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && adapter.getItemCount() > 0){
                    mCalculator.onScrollStateIdle();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {

                mCalculator.onScrolled(mScrollState);

                int childCount = mRecyclerView.getChildCount();
                int width = mRecyclerView.getChildAt(0).getWidth();
                int padding = (mRecyclerView.getWidth() - width) / 2;
                float scale = 0.95f;

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);

                    float rate = 0;

                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * (1-scale));
                        v.setScaleX(1 - rate * (1-scale));

                    } else {

                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(scale + rate * (1-scale));
                        v.setScaleX(scale + rate * (1-scale));
                    }
                }
            }
        });

        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {

                indexPage = newPosition;
                Anarko anarko = arrAnarko.get(indexPage);
                String strCount = String.valueOf(anarko.comments.length());
                lblCount.setText(strCount);

                startTimerRelatedVideo();
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                float scale = 0.95f;
                if (mRecyclerView.getChildCount() < 3) {
                    if (mRecyclerView.getChildAt(1) != null) {
                        if (mRecyclerView.getCurrentPosition() == 0) {
                            View v1 = mRecyclerView.getChildAt(1);
                            v1.setScaleY(scale);
                            v1.setScaleX(scale);
                        } else {
                            View v1 = mRecyclerView.getChildAt(0);
                            v1.setScaleY(scale);
                            v1.setScaleX(scale);
                        }
                    }
                } else {
                    if (mRecyclerView.getChildAt(0) != null) {
                        View v0 = mRecyclerView.getChildAt(0);
                        v0.setScaleY(scale);
                        v0.setScaleX(scale);
                    }
                    if (mRecyclerView.getChildAt(2) != null) {
                        View v2 = mRecyclerView.getChildAt(2);
                        v2.setScaleY(scale);
                        v2.setScaleX(scale);
                    }
                }
            }
        });


    }

    private void startTimerRelatedVideo(){
        if (timerTag != null){
            timerTag.cancel();
            timerTag = null;
        }
        timerTag = new CountDownTimer(1000,500) {
            @Override
            public void onTick(long millisUntilFinished) {
                // called every 0.5sec
            }

            @Override
            public void onFinish() {
                checkRelatedVideo();
            }
        }.start();

    }

    private void checkRelatedVideo(){
        Anarko anarko = arrAnarko.get(indexPage);
        strRelatedTags = "";
        for (int i = 0; i<anarko.tags.length(); i++){
            try {
                String tag = anarko.tags.getString(i);
                if (i == 0){
                    strRelatedTags = strRelatedTags + tag;
                }else{
                    strRelatedTags = strRelatedTags +";"+ tag;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Log.d("strRelatedTags", strRelatedTags);

        APIManger.getInstance().loadExplore(strRelatedTags, 0, 100, new APIManger.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRelated.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onSuccess(Response response) {

                try {
                    String res = response.body().string();
                    JSONObject resJSONObject = new JSONObject(res);
                    JSONObject dictionary = resJSONObject.getJSONObject("data");
                    final JSONArray payloads = dictionary.getJSONArray("payload");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (payloads.length() > 1 ){
                                btnRelated.setVisibility(View.VISIBLE);
                            }else{
                                btnRelated.setVisibility(View.GONE);
                            }
                        }
                    });

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Upload Codes
        File video = new File(Environment.getExternalStorageDirectory().getPath()+"/5.mp4");
        Log.d("file:",String.valueOf(video.length()));
        File image = new File(Environment.getExternalStorageDirectory().getPath()+"/5.png");
        Log.d("file:",String.valueOf(image.length()));
        APIManger.getInstance().upload(video, image);*/

        refreshExplorer();
    }

    public void refreshExplorer(){
        loadExplorer(null, 0, 100);
    }

    public void loadExplorer(String tag, int from, int to){
        // Load anarko videos from server

        disableButtons();
        container_download.setVisibility(View.VISIBLE);
        progressBar_refresh.startSpinning();

        APIManger.getInstance().loadExplore(tag, from, to, new APIManger.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ExplorerTimelineActivity.this, "Loading Failed. Please Try again...", Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onSuccess(final Response response) {

                try {
                    String res = response.body().string();
                    arrAnarko.clear();

                    JSONObject resJSONObject = new JSONObject(res);
                    JSONObject dictionary = resJSONObject.getJSONObject("data");
                    JSONArray payloads = dictionary.getJSONArray("payload");
                    for (int index = 0; index < payloads.length(); index++) {
                        JSONObject payload = payloads.getJSONObject(index);
                        Anarko anarko = new Anarko();
                        anarko.idx = payload.getString("_id");
                        anarko.name_url = payload.getString("name_url");
                        anarko.tags = payload.getJSONArray("tags");
                        anarko.comments = payload.getJSONArray("comments");
                        anarko.title = payload.getString("title");
                        anarko.description = payload.getString("description");
                        anarko.location = payload.getJSONArray("location");
                        anarko.url = payload.getString("url");
                        anarko.url_video = APIManger.URL_ANARKO + anarko.name_url + ".mp4";
                        anarko.url_thumbnail = APIManger.URL_ANARKO + anarko.name_url + ".jpg";
                        arrAnarko.add(anarko);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            if (arrAnarko.size() > 0){

                                final List<BaseItem> list = new ArrayList<>();

                                for (int idx = 0; idx < arrAnarko.size(); idx++){
                                    Anarko anarko = arrAnarko.get(idx);
                                    list.add(new VideoItem(anarko.url_video, anarko.url_thumbnail));
                                }

                                mRecyclerView.removeAllViews();
                                adapter.setList(list);
                                adapter.notifyDataSetChanged();

                                if (indexPage == 0){
                                    mRecyclerView.smoothScrollBy(1,0);
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            mRecyclerView.smoothScrollBy(0,0);
                                        }
                                    },500);

                                }else{
                                    layoutManager.scrollToPositionWithOffset(indexPage ,0);
                                }

                                Anarko anarko = arrAnarko.get(indexPage);
                                String strCount = String.valueOf(anarko.comments.length());
                                lblCount.setText(strCount);
                            }

                            container_download.setVisibility(View.GONE);
                            progressBar_refresh.stopSpinning();
                            enableButtons();

                            startTimerRelatedVideo();
                    }

                });
            }
        });
    }

    private void longPressed(){
        layoutSearch.setVisibility(View.VISIBLE);
        adapter.videoViewHolder.enableClick(false);
    }

    public void disableButtons() {
        btnShare.setTextColor(getResources().getColor(R.color.colorAKLightGrey));
        btnShare.setEnabled(false);
        btnComments.setEnabled(false);
        btnExplorer.setEnabled(false);
        btnCamera.setEnabled(false);
        mRecyclerView.setEnabled(false);
        if (adapter.videoViewHolder != null){
            adapter.videoViewHolder.enableClick(false);
        }
    }

    public void enableButtons() {
        btnShare.setTextColor(getResources().getColor(R.color.colorAKText));
        btnShare.setEnabled(true);
        btnComments.setEnabled(true);
        btnExplorer.setEnabled(true);
        btnCamera.setEnabled(true);
        mRecyclerView.setEnabled(true);
        if(adapter.videoViewHolder != null){
            adapter.videoViewHolder.enableClick(true);
        }
    }

    // Button Events
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_timeline_btn_comments:
                onComments();
                break;
            case R.id.activity_timeline_btn_explorer:
                refreshExplorer();
                break;
            case R.id.activity_timeline_btn_camera:
                onCamera();
                break;
            case R.id.activity_timeline_btn_share:
                onShare();
                break;

            case R.id.activity_timeline_container_search:
                layoutSearch.setVisibility(View.GONE);
                adapter.videoViewHolder.enableClick(true);
                AppManager.getInstance().hideKeyboard(ExplorerTimelineActivity.this);
                break;

            case R.id.activity_timeline_btn_search:
                onSearch();
                break;

            case R.id.activity_timeline_btnRelated:{
                loadExplorer(strRelatedTags, 0,100);
            }
                break;
        }
    }

    protected void onShare() {

        if (arrAnarko.size() > 0) {
            Anarko anarko = arrAnarko.get(indexPage);
            ShareIntentsDialog share = new ShareIntentsDialog.Builder(this)
                    .setDialogTitle("Share awesome Anarko")
                    .setShareLink(anarko.url_video)
                    .build();

            share.show();
        }
    }

    protected void onComments() {

        Bundle bundle = new Bundle();
        bundle.putInt("indexOfAnarko", indexPage);

        ExplorerCommentsFragment fragment = new ExplorerCommentsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        fragment.anarko = arrAnarko.get(indexPage);
        ft.add(R.id.activity_timeline_container_fragment, fragment).commit();

        disableButtons();
        container_fragment.setVisibility(View.VISIBLE);
    }

    protected void onCamera() {
        Intent intent = new Intent(getApplicationContext(), CamCaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    protected void onSearch() {
        indexPage = 0;
        String tag = txtSearch.getText().toString();
        loadExplorer(tag, 0, 100);

        AppManager.getInstance().hideKeyboard(ExplorerTimelineActivity.this);
        layoutSearch.setVisibility(View.GONE);
    }

    protected void onRefresh(View v) {
        // TODO: 10/12/2016
    }

    protected void onTryAgain(View v) {
        // TODO: 10/1/2016
    }

    // Sub Class
    private class MyAdapter extends RecyclerView.Adapter<BaseViewHolder<? extends BaseItem>>
            implements ItemsProvider, VideoViewHolderListener {

        private List<? extends BaseItem> mListItems;
        private VideoViewHolder videoViewHolder;

        public MyAdapter(List<BaseItem> list) {
            mListItems = new ArrayList<>(); //ItemUtils.generateMockData();
        }

        public void setList(List<BaseItem> list){
            mListItems.clear();
            mListItems = list;
        }

        @Override
        public BaseViewHolder<? extends BaseItem> onCreateViewHolder(ViewGroup parent, int viewType) {
            this.videoViewHolder = (VideoViewHolder) ViewHolderFactory.buildViewHolder(parent, viewType);
            this.videoViewHolder.setVideoViewHolderListener(this);
            return this.videoViewHolder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            BaseItem baseItem = mListItems.get(position);
            holder.onBind(position, baseItem);
        }

        @Override
        public int getItemCount() {
            return mListItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mListItems.get(position).getViewType();
        }

        @Override
        public ListItem getListItem(int position) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
            if (holder instanceof ListItem) {
                return (ListItem) holder;
            }
            return null;
        }

        @Override
        public int listItemSize() {
            return getItemCount();
        }

        @Override
        public void onLongPressed() {
            longPressed();
        }

    }

}
