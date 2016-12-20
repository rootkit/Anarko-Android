package com.oppous.anarko.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oppous.anarko.R;
import com.oppous.anarko.classes.FontManager;

import java.lang.reflect.Field;

public class SettingsMenuAdapter extends BaseAdapter {

    private Context context;
    int[] menus;
    LayoutInflater inflater;

    private int getResId(String drawableName) {

        try {
            Class<R.drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("SettingsMenuAdapter", "Failure to get drawable id.", e);
        }
        return -1;
    }

    public SettingsMenuAdapter(Context context,int[]menus){
        super();
        this.context = context;
        this.menus = menus;
        inflater = (LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return menus.length;
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
        int menu = menus[position];

        if (convertView == null){
            cellView = inflater.inflate(R.layout.layout_settings_menu, null);
            cell = new Cell(context, cellView);
            cellView.setTag(cell);
        }else{
            cell = (Cell)cellView.getTag();
        }

        cell.textView.setText(context.getResources().getString(menu));

        return cellView;
    }

    private class Cell {
        public LinearLayout layout;
        public TextView textView;
        public Cell(Context context, View cellView){
            this.textView = (TextView) cellView.findViewById(R.id.row_settingsMenu);
            this.textView.setTypeface(FontManager.getCustomFont(context, context.getResources().getString(R.string.font_gunplay)));

            this.layout = (LinearLayout) cellView.findViewById(R.id.row_settingLayout);
        }
    }
}
