package com.oppous.anarko.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.oppous.anarko.R;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.AppManager;
import com.squareup.picasso.Picasso;

public class MyPurchasesAdapter extends BaseAdapter{

    public enum Purchase_Type {
        PURCHASE_MASKS,
        PURCHASE_EFFECTS
    }

    private Context context;
    private LayoutInflater inflater;
    private Purchase_Type type;
    private int[] arrImageID;
    private String[] arrImageName;

    public MyPurchasesAdapter(Context context, Purchase_Type type) {
        this.context = context;
        this.type = type;

        switch (this.type){
            case PURCHASE_MASKS:
                this.arrImageID = this.arrMaskIDs;
                this.arrImageName = this.arrMaskNames;
                break;
            case PURCHASE_EFFECTS:
                this.arrImageID = this.arrEffectIDs;
                this.arrImageName = this.arrEffectNames;
                break;
        }

        inflater = (LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    private int[] arrMaskIDs = {
            R.drawable.mask_clown_normal, R.drawable.mask_clown_talk, R.drawable.mask_clown_wink, R.drawable.mask_moneky_normal, R.drawable.mask_moneky_wink,
            R.drawable.mask_skimask_normal, R.drawable.mask_skimask_wink
    };

    private String[] arrMaskNames = {
            "clown normal", "clown talk", "clown wink", "monkey" , "monkey wink", "skimask", "skimask wink"
    };


    private int[] arrEffectIDs = {
            R.drawable.effect_hydrangeas, R.drawable.effect_koala, R.drawable.effect_penguins
    };

    private String[] arrEffectNames = {
            "Hydrangeas", "Koala", "Penguins"
    };

    // abstracts
    @Override
    public int getCount() {
        return this.arrImageID.length;
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
        int idx = arrImageID[position];
        String name = arrImageName[position];

        if (convertView == null){
            cellView = inflater.inflate(R.layout.layout_settings_mypurchases, null);
            cell = new Cell(context, cellView);
            cellView.setTag(cell);
        }else{
            cell = (Cell)cellView.getTag();
        }

        cell.lblName.setText(name);
//        Picasso.with(context)
//                .load(arrImageID[position])
//                .placeholder(R.drawable.mask_clown_normal)
//                .into(cell.imageView);
        Bitmap image = AppManager.decodeSampledBitmapFromResource(context.getResources(),arrImageID[position],50,50);
        cell.imageView.setImageBitmap(image);

        return cellView;
    }

    private class Cell {
        public ImageView imageView;
        public AKTextView lblName;
        public Cell(Context context, View cellView){
            this.lblName = (AKTextView) cellView.findViewById(R.id.item_purchases_lblName);
            this.imageView = (ImageView) cellView.findViewById(R.id.item_purchases_imageView);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
