package com.oppous.anarko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.adapters.PayMethodAdapter;
import com.oppous.anarko.classes.AppManager;

import java.util.ArrayList;
import java.util.Map;


public class PayMethodActivity extends AppCompatActivity {

    private ListView listView;
    private PayMethodAdapter adapter;
    private ArrayList<Map<String,String>> stripTokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_method);

        listView = (ListView)findViewById(R.id.activity_payment_listView);

        stripTokens = AppManager.getInstance().getTokens(this,"stripeToken","token");
        adapter = new PayMethodAdapter(this, stripTokens);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == stripTokens.size()){
                    onAddNewCard(null);
                }else{
                    Map<String, String> token = adapter.tokens.get(position);
                    String tokenId = token.get("tokenId");
                    onShowStoreActivity(tokenId);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                if (position == stripTokens.size()){
                    return false;
                }else{
                    AlertView alertView = new AlertView("Are you sure to remove this card?", null, "No", new String[]{"YES"}, null, PayMethodActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int pos) {
                            if (pos == 0){
                                // remove card
                                Map<String, String> token = adapter.tokens.get(position);
                                AppManager.getInstance().removeToken(PayMethodActivity.this, token, "stripeToken", "token");
                                stripTokens = AppManager.getInstance().getTokens(PayMethodActivity.this,"stripeToken","token");
                                adapter.tokens = stripTokens;
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    alertView.setCancelable(true);
                    alertView.show();

                    return true;
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        stripTokens = AppManager.getInstance().getTokens(PayMethodActivity.this,"stripeToken","token");
        adapter.tokens = stripTokens;
        adapter.notifyDataSetChanged();
    }

    private static final int REQ_CODE = 32;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE){
            // TODO: 9/28/2016 Reload Added Cards
            stripTokens.clear();
            stripTokens = AppManager.getInstance().getTokens(this,"stripeToken","token");
            adapter.notifyDataSetChanged();
        }
    }

    protected void onShowStoreActivity(String token){
        Intent intent = new Intent(PayMethodActivity.this, PayStoreActivity.class);
        intent.putExtra("stripe_token", token);
        startActivityForResult(intent, REQ_CODE);
    }

    protected void onAddNewCard(View v) {
        Intent intent = new Intent(this, PayCardActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }

    protected void onBack(View v) {
        this.finish();
    }

    protected void onDone(View v){
        this.finish();
    }
}
