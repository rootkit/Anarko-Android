package com.oppous.anarko.adapters;

import android.content.Context;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oppous.anarko.R;
import com.stripe.android.model.Card;

import java.util.ArrayList;
import java.util.Map;

public class PayMethodAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;

    public ArrayList<Map<String,String>> tokens;

    public PayMethodAdapter(Context context, ArrayList<Map<String,String>> tokens) {
        super();
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tokens = tokens;
    }

    @Override
    public int getCount() {
        return this.tokens.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == this.tokens.size()){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View cellView = convertView;
        Cell cell;
        Header header;

        if (position == this.tokens.size()){
            if (cellView == null){
                header = new Header();
                cellView = inflater.inflate(R.layout.layout_payment_button, null);
                cellView.setTag(header);
            }else{
                header = (Header) cellView.getTag();
            }

        }else{
            if (cellView == null){
                cell = new Cell();
                cellView = inflater.inflate(R.layout.layout_paymethod_cell, null);
                cell.lblCardName = (TextView) cellView.findViewById(R.id.layout_payMethod_lblCardName);
                cell.imgCard = (ImageView) cellView.findViewById(R.id.layout_payMethod_imgCard);
                cell.imgCheck = (ImageView) cellView.findViewById(R.id.layout_payMethod_imgCheck);
                cellView.setTag(cell);
            }else{
                cell = (Cell) cellView.getTag();
            }

            Map<String, String> token = tokens.get(position);
            String last4 = token.get("last4");
            cell.lblCardName.setText(last4);
            String type = token.get("type");
            if (type != null){
                if (type.equals("American Express")){
                    cell.imgCard.setImageResource(R.drawable.icon_store_amex_enabled);
                }else if (type.equals("Discover")){
                    cell.imgCard.setImageResource(R.drawable.icon_store_card_discover_enabled);
                }else if (type.equals("JCB")){
                    cell.imgCard.setImageResource(R.drawable.icon_store_card_jcb_enabled);
                }else if (type.equals("DinersClub")){
                    cell.imgCard.setImageResource(R.drawable.icon_store_card_diners_enabled);
                }else if (type.equals("VISA")){
                    cell.imgCard.setImageResource(R.drawable.icon_store_card_visa_enabled);
                }else if (type.equals("MasterCard")){
                    cell.imgCard.setImageResource(R.drawable.icon_store_card_mastercard_enabled);
                }else {
                    cell.imgCard.setImageResource(R.drawable.icon_store_card_google_enabled);  // Unknown
                }
            }
        }

        return cellView;
    }

    private class Cell {
        public TextView lblCardName;
        public ImageView imgCard;
        public ImageView imgCheck;
    }

    private class Header{

    }
}
