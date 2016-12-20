package com.oppous.anarko.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oppous.anarko.R;
import com.oppous.anarko.models.Country;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class CountryListAdapter extends BaseAdapter {

  private Context context;
  List<Country> countries;
  LayoutInflater inflater;

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

  public CountryListAdapter(Context context, List<Country> countries) {
    super();
    this.context = context;
    this.countries = countries;
    inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount() {
    return countries.size();
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
  public View getView(int position, View convertView, ViewGroup parent) {
    View cellView = convertView;
    Cell cell;
    Country country = countries.get(position);

    if (convertView == null) {
      cell = new Cell();
      cellView = inflater.inflate(R.layout.layout_country_list, null);
      cell.textView = (TextView) cellView.findViewById(R.id.row_title);
      cell.imageView = (ImageView) cellView.findViewById(R.id.row_icon);
      cell.textViewCountryCode = (TextView) cellView.findViewById(R.id.row_countryCode);
      cellView.setTag(cell);
    } else {
      cell = (Cell) cellView.getTag();
    }

    cell.textView.setText(country.getName());
    cell.textViewCountryCode.setText(country.getDialCode());

    String drawableName = "flag_" + country.getCode().toLowerCase(Locale.ENGLISH);
    int drawableId = getResId(drawableName);
    country.setFlag(drawableId);
    cell.imageView.setImageResource(drawableId);
    return cellView;
  }

  private class Cell {
    public TextView textView;
    public ImageView imageView;
    public TextView textViewCountryCode;
  }
}