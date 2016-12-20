package com.oppous.anarko.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oppous.anarko.R;
import com.oppous.anarko.classes.ShareIntentsDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShareIntentsListAdapter extends BaseAdapter {
    private Context context;
    private String imagePath;
    private String shareLink;
    private List<ResolveInfo> listResolve;
    private PackageManager pm;
    private ShareIntentsDialog dialog;
    private Typeface typeface = null;
    private int     currentPosition = -1;

    public ShareIntentsListAdapter(Context context, ShareIntentsDialog dialog,
                                   String imagePath, String shareLink) {
        this.context = context;
        this.imagePath = imagePath;
        this.dialog = dialog;
        this.shareLink = shareLink;
        pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
//        sendIntent.setType("imag/*");
//        File f = new File(imagePath);
//        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareLink);//Html.fromHtml("<p>Here is awesome video"+shareLink+"</p>"));
        listResolve = pm.queryIntentActivities(sendIntent, 0);
    }

    public void setTypeface(Typeface tf) {
        typeface = tf;
    }

    @Override
    public int getCount() {
        if (listResolve == null)
            return 0;
        return listResolve.size();
    }

    @Override
    public Object getItem(int position) {
        return listResolve.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.share_intent_item, parent,
                    false);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.share_intent_item_icon);
            holder.text = (TextView) convertView
                    .findViewById(R.id.share_intent_item_text);
            if (typeface != null) {
                holder.text.setTypeface(typeface);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ResolveInfo info = listResolve.get(position);
        Drawable icon = info.loadIcon(pm);
        holder.image.setImageDrawable(icon);
        holder.text.setText(info.loadLabel(pm));
        final ActivityInfo activityInfo = info.activityInfo;
        final ComponentName componentName = new ComponentName(
                activityInfo.applicationInfo.packageName, activityInfo.name);

        if (position == currentPosition) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorAKButtonYellow));
        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorAKText));
        }

        return convertView;
    }

    public void toggleSend(int position) {
        final ResolveInfo info = listResolve.get(position);
        final ActivityInfo activityInfo = info.activityInfo;
        final ComponentName componentName = new ComponentName(
                activityInfo.applicationInfo.packageName, activityInfo.name);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setClassName(activityInfo.packageName, activityInfo.name);
        intent.putExtra(Intent.EXTRA_TEXT, shareLink);
        Log.d("test", shareLink);
        intent.setComponent(componentName);
        context.startActivity(intent);

        dialog.dismiss();
    }

    private class ViewHolder {
        public ImageView image;
        public TextView text;
    }

}
