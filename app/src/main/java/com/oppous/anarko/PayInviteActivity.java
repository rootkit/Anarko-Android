package com.oppous.anarko;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.classes.AKContact;
import com.oppous.anarko.classes.AKCountryCodes;
import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.interfaces.InviteCellCheckListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class PayInviteActivity extends AppCompatActivity implements View.OnClickListener, InviteCellCheckListener {

    private ListView listView;
    static ArrayList<Integer> selectedIndexs = new ArrayList<>();
    private List<AKContact> contacts = new ArrayList<>();
    private PayInviteAdapter adapter;

    private FrameLayout btnInvite, btnCopyLink;
    private ImageView btnBack;
    private AKTextView lblSuperlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_invite);

        btnInvite = (FrameLayout) findViewById(R.id.inviteButton);
        btnInvite.setOnClickListener(this);
        btnCopyLink = (FrameLayout) findViewById(R.id.layout_invite_copylink);
        btnCopyLink.setOnClickListener(this);
        btnBack = (ImageView) findViewById(R.id.activity_invite_btnBack);
        btnBack.setOnClickListener(this);

        lblSuperlink = (AKTextView) findViewById(R.id.lblSuperlink);
        listView = (ListView) findViewById(R.id.listView);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            // should we show explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)){

                // show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response! after the user sees the explanation, try again to request the permission.
            }else{
                // no explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},123);

                // 123 is an requestCode. the callback method gets the result of request.
            }

        }else{
            contacts = AppManager.getInstance().getContacts();
            adapter = new PayInviteAdapter(this, contacts);
            listView.setAdapter(adapter);
        }

        // get super link from server
        APIManger.getInstance().getSuperLink(new APIManger.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onSuccess(final Response response) {

                try{
                    String res = response.body().string();
                    JSONObject resJSONObject = new JSONObject(res);
                    JSONObject data = resJSONObject.getJSONObject("data");
                    final String superlink = data.getString("url");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lblSuperlink.setText(superlink);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.inviteButton:{
                if (contacts.size() > 0 && PayInviteActivity.selectedIndexs.size() > 0){
                    String phone = AppManager.getInstance().getMobileNumber();

                    JSONArray friends = new JSONArray();
                    for (int idx = 0; idx < PayInviteActivity.selectedIndexs.size(); idx++){
                        if (PayInviteActivity.selectedIndexs.contains(-1) == false){
                            AKContact contact = contacts.get(idx);
                            String friendphone = contact.phoneNumber;
                            if (friendphone.contains("+") == false){
                                TelephonyManager tm = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                                String countryCode = tm.getNetworkCountryIso();
                                String prefixCode = AKCountryCodes.getCode(countryCode);
                                friendphone = "+" + prefixCode + friendphone;
                            }
                            friends.put(friendphone);
                        }
                    }

                    APIManger.getInstance().inviteFriends(phone, friends, new APIManger.APISuccessListener() {
                        @Override
                        public void onFailure(String error) {

                        }

                        @Override
                        public void onSuccess(Response response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertView mAlertView = new AlertView("Your invitation were successfully sent", null, "Done", null, null, PayInviteActivity.this, AlertView.Style.Alert, null);
                                    mAlertView.setCancelable(true);
                                    mAlertView.show();
                                }
                            });
                        }
                    });

                }else{
                    AlertView mAlertView = new AlertView("Select your friends", null, "Okay", null, null, PayInviteActivity.this, AlertView.Style.Alert, null);
                    mAlertView.setCancelable(true);
                    mAlertView.show();
                }

            }
                break;
            case R.id.activity_invite_btnBack:{
                this.finish();
            }
                break;
            case R.id.layout_invite_copylink:{

                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(PayInviteActivity.this.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("superlink",lblSuperlink.getText().toString());
                clipboardManager.setPrimaryClip(clip);

                /*ClipData data = clipboardManager.getPrimaryClip();
                if (data != null){
                    ClipData.Item item = data.getItemAt(0);
                    String text = item.getText().toString();
                }*/

                Toast.makeText(PayInviteActivity.this,"Copied super link", Toast.LENGTH_LONG).show();
            }
                break;
        }
    }

    @Override
    public void OnCheckedCell(int idx, boolean isChecked) {
        if (idx < 0){
            for (int i = -1; i < contacts.size(); i++){
                if (isChecked){
                    if (!selectedIndexs.contains(i)){
                        selectedIndexs.add(i);
                    }
                }else{
                    if (selectedIndexs.contains(i)) {
                        int index = selectedIndexs.indexOf(i);
                        selectedIndexs.remove(index);
                    }
                }
            }
        }else{
            if (isChecked){
                if (!selectedIndexs.contains(idx)){
                    selectedIndexs.add(idx);
                }
            }else{
                if (selectedIndexs.contains(idx)){
                    int index = selectedIndexs.indexOf(idx);
                    selectedIndexs.remove(index);
                }
            }

            if (selectedIndexs.contains(-1)){
                if (selectedIndexs.size() <= contacts.size()){
                    int index = selectedIndexs.indexOf(-1);
                    selectedIndexs.remove(index);
                }
            }else{
                if (selectedIndexs.size() >= contacts.size()){
                    selectedIndexs.add(-1);
                }
            }

        }

        adapter.notifyDataSetChanged();
    }

    public class PayInviteAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<AKContact> items;

        public PayInviteAdapter(Context context, List<AKContact> items) {
            this.context = context;
            this.items = items;

            inflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        }

        public void setItems(List<AKContact> items) {
            this.items = items;
        }

        public List<AKContact> getItems() {
            return this.items;
        }

        @Override
        public int getCount() {
            if (items.size() > 1){
                return items.size()+1;
            }else{
                return items.size();
            }
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
            if (convertView == null) {
                cellView = inflater.inflate(R.layout.layout_invite_cell, null);
                cell = new Cell(context, cellView);
                cell.setInviteCellCheckListener(PayInviteActivity.this);
                cellView.setTag(cell);
            } else {
                cell = (Cell) cellView.getTag();
            }

            if (items.size() > 1){
                if (position == 0){
                    cell.idx = position-1;
                    cell.lblName.setTextColor(getResources().getColor(R.color.colorAKText));
                    cell.lblName.setText(getResources().getString(R.string.store_invite_select_all));
                }else{
                    cell.idx = position-1;
                    AKContact contact = items.get(position-1);
                    cell.lblName.setText(contact.name);
                    cell.lblName.setTextColor(getResources().getColor(R.color.colorAKLightGrey));
                }
            }else{
                cell.idx = position;
                AKContact contact = items.get(position);
                cell.lblName.setText(contact.name);
            }

            if (PayInviteActivity.selectedIndexs.contains(cell.idx)){
                cell.checkBox.setChecked(true);
                cell.isChecked = true;
            }else{
                cell.checkBox.setChecked(false);
                cell.isChecked = false;
            }

            return cellView;
        }

        private class Cell {

            public int idx;
            public boolean isChecked;
            public CheckBox checkBox;
            public TextView lblName;
            private InviteCellCheckListener listener;


            public Cell(Context context, View cellView) {

                this.isChecked = false;
                this.checkBox = (CheckBox) cellView.findViewById(R.id.invite_cell_chBox);
                this.lblName = (TextView) cellView.findViewById(R.id.layout_invite_lblName);

//                this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        listener.OnCheckedCell(idx, isChecked);
//                        Cell.this.isChecked = isChecked;
//                    }
//                });

                this.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isChecked = !isChecked;
                        listener.OnCheckedCell(idx,isChecked);
                    }
                });

            }

            public void setInviteCellCheckListener(InviteCellCheckListener listener){
                this.listener = listener;
            }

        }

    }

}
