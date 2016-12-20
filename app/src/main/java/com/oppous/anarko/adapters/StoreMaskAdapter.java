package com.oppous.anarko.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.oppous.anarko.R;
import com.oppous.anarko.classes.AKItem;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class StoreMaskAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<AKItem> items;

    public StoreMaskAdapter(Context context, List<AKItem> items) {
        this.context = context;
        this.items = items;

        inflater = (LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    public void setItems(List<AKItem> items){
        this.items = items;
    }

    public List<AKItem> getItems(){
        return this.items;
    }

    // abstracts
    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        Cell cell;

        AKItem item = this.items.get(position);

        String name = item.name;
        JSONArray thumbs = item.thumbs;
        String url = "";
        try {
            JSONObject thumb = thumbs.getJSONObject(0);
            url = thumb.getString("url");
        }catch (JSONException e){
            e.printStackTrace();
        }


        if (convertView == null){
            cellView = inflater.inflate(R.layout.layout_store_item_cell, null);
            cell = new Cell(context, cellView);
            cellView.setTag(cell);
        }else{
            cell = (Cell)cellView.getTag();
        }

        cell.lblName.setText(name);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.mask_clown_normal)
                .into(cell.imageView);

        return cellView;
    }

    private class Cell {
        public ImageView imageView;
        public AKTextView lblName;
        public Cell(Context context, View cellView){
            this.lblName = (AKTextView) cellView.findViewById(R.id.item_store_lblName);
            this.imageView = (ImageView) cellView.findViewById(R.id.item_store_imageView);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
