package com.oppous.anarko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.adapters.MyPurchasesAdapter;
import com.oppous.anarko.classes.AKSwitchButton;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;

import java.util.Map;

import okhttp3.Response;


public class SettingsPurchaseActivity extends AppCompatActivity {

    public GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_purchase);

        MyPurchasesAdapter adapter = new MyPurchasesAdapter(this, MyPurchasesAdapter.Purchase_Type.PURCHASE_MASKS);
        gridView = (GridView) findViewById(R.id.activity_settings_mypurchases_gridView);
        gridView.setAdapter(adapter);

        final AKSwitchButton switcher = (AKSwitchButton) findViewById(R.id.activity_settings_mypurchases_switcher);
        switcher.setOnChangeListener(new AKSwitchButton.OnChangeListener() {
            @Override
            public void onChange(int position) {
                MyPurchasesAdapter newAdpater;
                SettingsPurchaseActivity.this.gridView.invalidateViews();
                switch (position){
                    case 0:
                        newAdpater = new MyPurchasesAdapter(SettingsPurchaseActivity.this, MyPurchasesAdapter.Purchase_Type.PURCHASE_MASKS);
                        SettingsPurchaseActivity.this.gridView.setAdapter(newAdpater);
                        break;
                    case 1:
                        newAdpater = new MyPurchasesAdapter(SettingsPurchaseActivity.this, MyPurchasesAdapter.Purchase_Type.PURCHASE_EFFECTS);
                        SettingsPurchaseActivity.this.gridView.setAdapter(newAdpater);
                        break;
                }

            }
        });

        loadData();
    }

    private void loadData(){
        Map<String, String> session = AppManager.getInstance().getSession();
        String clientID = session.get("clientID");
        APIManger.getInstance().getMasks(clientID, new APIManger.APISuccessListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccess(Response response) {

            }
        });
    }

    // Notify Callback
    public void implementThread(final int kind){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    // Button Events
    protected void onBack(View view){
        //TODO :
        this.finish();
    }
}
