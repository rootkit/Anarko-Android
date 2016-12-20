package com.oppous.anarko;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oppous.anarko.classes.AKItem;
import com.oppous.anarko.classes.AKTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PayAchievementsActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private ImageView btnBack;
    private AKTextView btnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_achievements);

        listView = (ListView) findViewById(R.id.listView);
        PayAchieveAdapter adapter = new PayAchieveAdapter(this,new ArrayList<JSONObject>());
        listView.setAdapter(adapter);

        btnBack = (ImageView) findViewById(R.id.activity_achieve_btnBack);
        btnBack.setOnClickListener(this);

        btnInvite = (AKTextView) findViewById(R.id.activity_achieve_btnInvite);
        btnInvite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_achieve_btnBack:
            {
                this.finish();
            }
            break;

            case R.id.activity_achieve_btnInvite:
            {
                Intent intent = new Intent(this, PayInviteActivity.class);
                startActivity(intent);
            }
                break;
        }
    }

    public class PayAchieveAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<JSONObject> items;

        public PayAchieveAdapter(Context context, List<JSONObject> items) {
            this.context = context;
            this.items = items;

            inflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        }

        public void setItems(List<JSONObject> items) {
            this.items = items;
        }

        public List<JSONObject> getItems() {
            return this.items;
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 2){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return 7;
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

            if (position == 2) {
                Header header;
                if (convertView == null) {
                    cellView = inflater.inflate(R.layout.layout_achieve_header, null);
                    header = new Header();
                    cellView.setTag(header);
                } else {
                    header = (Header) cellView.getTag();
                }

            }else{
                Cell cell;
                if (convertView == null) {
                    cellView = inflater.inflate(R.layout.layout_achieve_cell, null);
                    cell = new Cell(context, cellView);
                    cellView.setTag(cell);
                } else {
                    cell = (Cell) cellView.getTag();
                }

                String A = "";
                String B = "";
                String C = "";
                String D = "";

                if (position == 0){
                    A = getResources().getString(R.string.store_achieve_if);
                    B = getResources().getString(R.string.store_achieve_10_friends);
                    C = getResources().getString(R.string.store_achieve_get);
                    D = getResources().getString(R.string.store_achieve_earn1);
                }else if(position == 1){
                    A = getResources().getString(R.string.store_achieve_if);
                    B = getResources().getString(R.string.store_achieve_25_friends);
                    C = getResources().getString(R.string.store_achieve_get);
                    D = getResources().getString(R.string.store_achieve_earn2);
                }else if(position == 3){
                    A = getResources().getString(R.string.store_achieve_share);
                    B = getResources().getString(R.string.store_achieve_share_1k);
                    C = getResources().getString(R.string.store_achieve_share_get);
                    D = getResources().getString(R.string.store_achieve_share_earn1);
                }else if(position == 4){
                    A = getResources().getString(R.string.store_achieve_share);
                    B = getResources().getString(R.string.store_achieve_share_1k);
                    C = getResources().getString(R.string.store_achieve_share_get);
                    D = getResources().getString(R.string.store_achieve_share_earn2);
                }else if(position == 5){
                    A = getResources().getString(R.string.store_achieve_share);
                    B = getResources().getString(R.string.store_achieve_share_50k);
                    C = getResources().getString(R.string.store_achieve_share_get);
                    D = getResources().getString(R.string.store_achieve_share_earn3);
                }else if(position == 6){
                    A = getResources().getString(R.string.store_achieve_share);
                    B = getResources().getString(R.string.store_achieve_share_50k);
                    C = getResources().getString(R.string.store_achieve_share_get);
                    D = getResources().getString(R.string.store_achieve_share_earn4);
                }

                cell.lblA.setText(A);
                cell.lblB.setText(B);
                cell.lblC.setText(C);
                cell.lblD.setText(D);

            }


            return cellView;
        }

        private class Cell {
            public TextView lblA, lblB, lblC, lblD, lblMark;

            public Cell(Context context, View cellView) {
                this.lblA = (TextView) cellView.findViewById(R.id.layout_achieve_lblA);
                this.lblB = (TextView) cellView.findViewById(R.id.layout_achieve_lblB);
                this.lblC = (TextView) cellView.findViewById(R.id.layout_achieve_lblC);
                this.lblD = (TextView) cellView.findViewById(R.id.layout_achieve_lblD);
                this.lblMark = (TextView) cellView.findViewById(R.id.layout_achieve_lblMark);
            }
        }

        private class Header {

        }
    }

}
