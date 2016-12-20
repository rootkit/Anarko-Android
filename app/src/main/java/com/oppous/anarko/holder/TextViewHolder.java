package com.oppous.anarko.holder;

import android.view.View;
import android.widget.TextView;


import com.oppous.anarko.R;
import com.oppous.anarko.models.TextItem;

import butterknife.Bind;

public class TextViewHolder extends BaseViewHolder<TextItem> {

    @Bind(R.id.text_view)
    TextView mTextView;

    public TextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(int position, TextItem iItem) {
        mTextView.setText(String.format("%s - %s", iItem.getText(), position));
    }
}
