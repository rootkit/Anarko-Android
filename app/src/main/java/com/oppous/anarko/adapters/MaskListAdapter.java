package com.oppous.anarko.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.oppous.anarko.R;
import com.oppous.anarko.classes.AppManager;

public class MaskListAdapter extends ArrayAdapter<String> {

    private Activity c;
    private LayoutInflater inflater;

    String[] maskListValues;
    int[] uriAry;
    Bitmap[] bitmaps;

    public MaskListAdapter(Activity c, String[] maskListValues, int[] uriAry) {
        super(c, R.layout.layout_mask_list, maskListValues);
        this.c = c;
        this.maskListValues = maskListValues;
        this.uriAry = uriAry;

        bitmaps = new Bitmap[uriAry.length];
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < uriAry.length; i++) {
            bitmaps[i] = AppManager.decodeSampledBitmapFromResource(c.getResources(), uriAry[i], 150, 150);
        }
    }

    public View getView (int position, View view, ViewGroup parent) {
        View cellView = view;
        Cell cell;

        if (cellView == null) {
            cell = new Cell();
            cellView = inflater.inflate(R.layout.layout_mask_list, null);
            cell.imageView = (ImageView) cellView.findViewById(R.id.mask_list_imgView);
            cellView.setTag(cell);
        } else {
            cell = (Cell) cellView.getTag();
        }

        cell.imageView.setImageBitmap(bitmaps[position]);

        return cellView;
    }

    private class Cell {
        public ImageView imageView;
    }

}
