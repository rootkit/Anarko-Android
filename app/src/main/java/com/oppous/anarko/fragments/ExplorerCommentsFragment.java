package com.oppous.anarko.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oppous.anarko.ExplorerTimelineActivity;
import com.oppous.anarko.R;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.Anarko;
import com.oppous.anarko.classes.AppManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import at.blogc.android.views.ExpandableTextView;
import okhttp3.Response;


public class ExplorerCommentsFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "CommentsFragment";

    private Integer indexOfAnarko;
    public Anarko anarko;

    private ImageView   btn_comment;
    private ImageView   btn_report;
    private EditText    txt_comment;
    private Button      btn_send;

    private ListView    listComments;
    private ViewGroup   mContainer;
    private CommentListAdapter adapter;

    public ExplorerCommentsFragment() {

    }

    public static ExplorerCommentsFragment newInstance(String param1, String param2) {
        ExplorerCommentsFragment fragment = new ExplorerCommentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContainer = container;

        View view = inflater.inflate(R.layout.fragment_explorer_comments, container, false);
        btn_comment = (ImageView) view.findViewById(R.id.fragment_comments_btn_comment);
        btn_report = (ImageView) view.findViewById(R.id.fragment_comments_btn_report);
        txt_comment = (EditText) view.findViewById(R.id.fragment_comments_txt_comment);
        btn_send = (Button) view.findViewById(R.id.fragment_comments_btn_send);

        btn_report.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        Bundle bundle = getArguments();
        this.indexOfAnarko = bundle.getInt("indexOfAnarko");
//        this.anarko = AppManager.getInstance().arrAnarko.get(this.indexOfAnarko);

        if (anarko != null){
            listComments = (ListView) view.findViewById(R.id.listView);
            adapter = new CommentListAdapter(getActivity(), this.anarko.comments);
            adapter.anarko = this.anarko;
            listComments.setAdapter(adapter);
        }

        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_comments_btn_send:

                final String comment = txt_comment.getText().toString();
                if (!comment.isEmpty() && comment != null){

                    APIManger.getInstance().postComment(anarko.idx, comment, new APIManger.APISuccessListener() {
                        @Override
                        public void onFailure(String error) {
                            Log.d(TAG, error);
                        }

                        @Override
                        public void onSuccess(Response response) {
                            Log.d(TAG, response.toString());

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.comments.put(comment);
                                    adapter.notifyDataSetChanged();
                                    listComments.smoothScrollToPosition(adapter.comments.length()-1);
                                }
                            });

                        }

                    });

                    txt_comment.setText("");
                    hideKeyboard();
                }

                break;
            case R.id.fragment_comments_btn_report:
                ExplorerReportFragment fragment = new ExplorerReportFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("indexOfAnarko",indexOfAnarko);
                fragment.setArguments(bundle);
                fragment.anarko = this.anarko;
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_timeline_container_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.fragment_comments_btn_comment:
                mContainer.setVisibility(View.GONE);
                ExplorerTimelineActivity activity = (ExplorerTimelineActivity)getActivity();
                activity.enableButtons();
                activity.getFragmentManager().beginTransaction().remove(this).commit();
                break;
            default:
                break;
        }
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class CommentListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        public JSONArray comments;
        public Anarko anarko;

        private int getResId(String drawableName) {

            try {
                Class<R.drawable> res = R.drawable.class;
                Field field = res.getField(drawableName);
                int drawableId = field.getInt(null);
                return drawableId;
            } catch (Exception e) {
                Log.e("CountryCodePicker", "Failure to get drawable id.", e);
            }
            return -1;
        }

        public CommentListAdapter(Context context, JSONArray comments) {
            super();
            this.context = context;
            this.comments = comments;
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return comments.length()+1;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;

            if (position == 0)
            {
                final Header header;

                if (convertView == null) {
                    header = new Header();
                    cellView = inflater.inflate(R.layout.layout_comment_header, null);
                    header.lblTags = (TextView) cellView.findViewById(R.id.fragment_comments_lblTag);
                    header.lblCounts = (AKTextView) cellView.findViewById(R.id.fragment_comments_lblCount);
                    header.lblAddress = (TextView) cellView.findViewById(R.id.fragment_comments_label_address);

                    final ExpandableTextView expandableTextView = (ExpandableTextView) cellView.findViewById(R.id.expandableTextView);
                    final TextView buttonToggle = (TextView) cellView.findViewById(R.id.fragment_comments_btn_more);
                    final ImageView iconLess = (ImageView) cellView.findViewById(R.id.fragment_comments_iconLess);

                    // Description
                    String description = anarko.description;
                    expandableTextView.setText(description);
                    expandableTextView.setAnimationDuration(1000L);
                    expandableTextView.setInterpolator(new OvershootInterpolator());

                    expandableTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            int lineNumber = expandableTextView.getLineCount();
                            if (lineNumber < 3){
                                buttonToggle.setVisibility(View.GONE);
                            }
                        }
                    });

                    buttonToggle.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {
                            if (expandableTextView.isExpanded())
                            {
                                expandableTextView.collapse();
                                buttonToggle.setText(R.string.comments_btn_expand);
                                iconLess.setVisibility(View.GONE);
                            }
                            else
                            {
                                expandableTextView.expand();
                                buttonToggle.setText(R.string.comments_btn_collapse);
                                iconLess.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    expandableTextView.setOnExpandListener(new ExpandableTextView.OnExpandListener()
                    {
                        @Override
                        public void onExpand(final ExpandableTextView view)
                        {
                            Log.d(TAG, "ExpandableTextView expanded");
                        }

                        @Override
                        public void onCollapse(final ExpandableTextView view)
                        {
                            Log.d(TAG, "ExpandableTextView collapsed");
                        }
                    });


                    cellView.setTag(header);
                } else {
                    header = (Header) cellView.getTag();
                }

                header.lblCounts.setText(String.valueOf(this.anarko.comments.length()));

                // Set Tags
                String strTags = "";
                for (int i=0;i<anarko.tags.length();i++){
                    try {
                        strTags = strTags + "#" + anarko.tags.get(i).toString() + " ";
                    }catch (Exception e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                }

                header.lblTags.setText(strTags);

                header.lblAddress.setText(" ");
                APIManger.getInstance().getAddress(this.anarko.location, new APIManger.APISuccessListener() {
                    @Override
                    public void onFailure(String error) {

                    }

                    @Override
                    public void onSuccess(final Response response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String res = response.body().string();
                                    JSONObject resJSONObject = new JSONObject(res);
                                    String address = resJSONObject.getString("data");
                                    header.lblAddress.setText(address);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            }
            else
            {
                Cell cell;
                String comment = "";
                if (convertView == null) {
                    cell = new Cell();
                    cellView = inflater.inflate(R.layout.layout_comment_cell, null);
                    cell.textView = (TextView) cellView.findViewById(R.id.row_title);
                    cell.imageView = (ImageView) cellView.findViewById(R.id.row_icon);
                    cellView.setTag(cell);
                } else {
                    cell = (Cell) cellView.getTag();
                }

                try{
                    comment = this.comments.get(position-1).toString();
                    cell.textView.setText(comment);
                }catch (Exception e){
                    Log.d(TAG, e.getLocalizedMessage());
                }
            }

            return cellView;
        }

        private class Cell {
            public TextView textView;
            public ImageView imageView;
        }

        private class Header {
            public AKTextView lblCounts;
            public TextView lblTags;
            public TextView lblAddress;
        }
    }
}
