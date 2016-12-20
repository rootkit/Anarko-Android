package com.oppous.anarko.classes;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oppous.anarko.R;
import com.oppous.anarko.adapters.ShareIntentsListAdapter;

import java.util.List;

public class ShareIntentsDialog extends Dialog {
    private Context context;
    private ListView share_list;
    private TextView dialog_title;
    private String sharePath;
    private String shareLink;
    private String title_text;
    private boolean isShowDialogTitle;
    private Button  btn_cancel;
    private Button  btn_done;
    public  int     itemPosition;

    private ShareIntentsDialog(Context context) {
        super(context);
        this.context = context;
        isShowDialogTitle = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_share_intents_dialog);
        share_list = (ListView) findViewById(R.id.list_share_intents);
        dialog_title = (TextView) findViewById(R.id.share_dialog_title);
        btn_cancel = (Button) findViewById(R.id.dialog_share_btn_cancel);
        btn_done = (Button) findViewById(R.id.dialog_share_btn_done);

        final ShareIntentsListAdapter adapter = new ShareIntentsListAdapter(context, this,
                this.sharePath, this.shareLink);

        share_list.setAdapter(adapter);
        share_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
                adapter.setCurrentPosition(position);
                adapter.notifyDataSetChanged();
            }
        });

        this.dialog_title.setText(title_text);
        if (!this.isShowDialogTitle) {
            this.dialog_title.setVisibility(View.GONE);
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareIntentsDialog.this.dismiss();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.toggleSend(itemPosition);
            }
        });

    }

    public static class Builder {
        private String imagePath;
        private String shareLink;
        private String title;
        private boolean isShowDialogTitle;
        private Context context;

        public Builder(Context context) {
            this.context = context;
            title = null;
            isShowDialogTitle = true;
            imagePath = null;
        }

        public Builder setImagePath(String path) {
            this.imagePath = path;
            return this;
        }

        public Builder setShareLink(String link) {
            this.shareLink = link;
            return this;
        }

        public Builder setEnableShowDialogTitle(boolean enableShowTitle) {
            this.isShowDialogTitle = enableShowTitle;
            return this;
        }

        public Builder setDialogTitle(String title) {
            this.title = title;
            return this;
        }

        public ShareIntentsDialog build() {
            return new ShareIntentsDialog(this);
        }

    }

    private ShareIntentsDialog(Builder builder) {
        super(builder.context);
        this.context = builder.context;
        if (builder.title != null) {
            this.title_text = builder.title;

        }
        this.isShowDialogTitle = builder.isShowDialogTitle;
        this.sharePath = builder.imagePath;
        this.shareLink = builder.shareLink;
    }
}
