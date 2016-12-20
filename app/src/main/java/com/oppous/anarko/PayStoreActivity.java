package com.oppous.anarko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.adapters.StoreMaskAdapter;
import com.oppous.anarko.classes.AKItem;
import com.oppous.anarko.classes.AKSwitchButton;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class PayStoreActivity extends AppCompatActivity implements View.OnClickListener {

    private AKSwitchButton btnSwitch;
    private GridView gridView;
    private String stripeToken;
    private AKItem selectedItem;
    private StoreMaskAdapter adapter;

    private static final int KEY_REQUEST_SUCCESS = 969;
    private static final int KEY_REQUEST_FAILED = 968;

    private AKTextView btnEarn;
    private ImageView btnBack, btnExplorer, btnSettings;

    private ImageView imgItem;
    private AKTextView lblItemName, lblBonusName, btnBuy;
    private AKTextView lblItemCost, lblBonusCost, lblTotalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_store);

        stripeToken = getIntent().getStringExtra("stripe_token");

        btnSwitch = (AKSwitchButton) findViewById(R.id.activity_store_switcher);
        btnSwitch.setOnChangeListener(new AKSwitchButton.OnChangeListener() {
            @Override
            public void onChange(int position) {
                switch (position){
                    case 0:
                        loadStoreItems("mask");
                        break;
                    case 1:
                        loadStoreItems("effects");
                        break;
                }
            }
        });


        gridView = (GridView) findViewById(R.id.activity_store_gridView);
        adapter = new StoreMaskAdapter(this, new ArrayList<AKItem>());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<AKItem> items = AppManager.getInstance().arrItems;
                if (items.size() > 0){
                    AKItem item = items.get(position);
                    selectItem(item);
                }
            }
        });

        btnEarn = (AKTextView) findViewById(R.id.activity_store_btnEarn);
        btnEarn.setOnClickListener(this);

        btnBack = (ImageView) findViewById(R.id.activity_store_btnBack);
        btnExplorer = (ImageView) findViewById(R.id.activity_store_btnExplorer);
        btnSettings = (ImageView) findViewById(R.id.activity_store_btnSettings);
        btnBack.setOnClickListener(this);
        btnExplorer.setOnClickListener(this);
        btnSettings.setOnClickListener(this);

        btnBuy = (AKTextView) findViewById(R.id.activity_store_btnBuy);
        btnBuy.setOnClickListener(this);

        imgItem = (ImageView) findViewById(R.id.activity_store_imgItem);
        lblItemName = (AKTextView) findViewById(R.id.activity_store_lblItemName);
        lblItemCost = (AKTextView) findViewById(R.id.activity_store_lblItemCost);
        lblBonusName = (AKTextView) findViewById(R.id.activity_store_lblBonusName);
        lblBonusCost = (AKTextView) findViewById(R.id.activity_store_lblBonusCost);
        lblTotalCost = (AKTextView) findViewById(R.id.activity_store_lblTotalCost);

        lblItemName.setVisibility(View.GONE);
        lblItemCost.setVisibility(View.GONE);
        lblBonusCost.setVisibility(View.GONE);
        lblBonusName.setVisibility(View.GONE);
        lblTotalCost.setVisibility(View.GONE);
        btnBuy.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadStoreItems("mask");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_store_btnEarn:
            {
                if (AppManager.getInstance().arrPurchasedItem.size()>0){
                    Intent intent = new Intent(this, SettingsPurchaseActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(this, PayAchievementsActivity.class);
                    startActivity(intent);
                }
            }
                break;
            case R.id.activity_store_btnBack:
            {
                this.finish();
            }
                break;
            case R.id.activity_store_btnExplorer:
            {
                Intent intent = new Intent(this, ExplorerTimelineActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.activity_store_btnSettings:
            {
                Intent intent = new Intent(this, SettingsMainActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.activity_store_btnBuy:
            {
                APIManger.getInstance().buyItems(stripeToken, selectedItem.idx, selectedItem.price, new APIManger.APISuccessListener() {
                    @Override
                    public void onFailure(String error) {

                    }

                    @Override
                    public void onSuccess(final Response response) {
                        try{
                            String res = response.body().string();
                            JSONObject resJSONObject = new JSONObject(res);
                            JSONArray data = resJSONObject.getJSONArray("data");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // run
                                    AlertView alertView = new AlertView("Purchasing succeed", "", "Ok", null, null, PayStoreActivity.this, AlertView.Style.Alert, null);
                                    alertView.setCancelable(true);
                                    alertView.show();
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
                break;
        }
    }

    private void selectItem(AKItem item){

        selectedItem = item;
        String name = item.name;
        double price = item.price;
        String cost = String.valueOf(price) + "$";
        JSONArray thumbs = item.thumbs;
        String url = "";
        try {
            JSONObject thumb = thumbs.getJSONObject(0);
            url = thumb.getString("url");
        }catch (JSONException e){
            e.printStackTrace();
        }

        lblItemName.setText(name);
        lblItemCost.setText(cost);
        lblItemName.setVisibility(View.VISIBLE);
        lblItemCost.setVisibility(View.VISIBLE);
        btnBuy.setVisibility(View.VISIBLE);

        lblBonusCost.setVisibility(View.GONE);
        lblBonusName.setVisibility(View.GONE);
        lblTotalCost.setVisibility(View.GONE);

        Picasso.with(PayStoreActivity.this)
                .load(url)
                .placeholder(R.drawable.mask_clown_normal)
                .into(imgItem);

    }

    private void loadStoreItems(String type){
        APIManger.getInstance().getStoreList(type, new APIManger.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                implementUIThread(KEY_REQUEST_FAILED);
            }

            @Override
            public void onSuccess(Response response) {
                implementUIThread(KEY_REQUEST_SUCCESS);
            }
        });
    }

    public void implementUIThread(final int kind){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (kind){
                    case KEY_REQUEST_SUCCESS:

                        List<AKItem> items = AppManager.getInstance().arrItems;
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();

                        // select 1st item as default one.
                        if (items.size()>0){
                            selectItem(items.get(0));
                        }

                        break;
                    case KEY_REQUEST_FAILED:
                    {
                        // TODO: 10/17/2016 show alert
                    }
                        break;
                }
            }
        });
    }

}
