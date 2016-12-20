package com.oppous.anarko.classes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.oppous.anarko.R;
import com.stripe.android.model.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppManager {
    private static AppManager ourInstance = new AppManager();

    private Context context;

//    public List<Anarko> arrAnarko = new ArrayList<Anarko>();
    public List<AKItem> arrItems = new ArrayList<>();
    public List<AKItem> arrPurchasedItem = new ArrayList<>();

    public static AppManager getInstance() {
        return ourInstance;
    }

    private AppManager() {

    }

    //return base path on local storage
    public static String getBasePath() {
        String path = Environment.getExternalStorageDirectory().toString();
        File fileDir = new File(path + "/" + "Anarko" + "/");
        if (!fileDir.exists())
            fileDir.mkdirs();

        return fileDir.getAbsolutePath();
    }

    // Keyboard Hide
    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    // Bitmap Processing
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Set
    public void setContext(Context c){
        this.context = c;
    }

    public void setSession(String clientID, String userID, String token){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("clientID", clientID);
        editor.putString("userID", userID);
        editor.putString("token", token);
        editor.commit();
    }

    public void setMobileNumber(String number){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("mobile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mobileNumber", number);
        editor.commit();
    }

    public void setNotificationAudio(boolean f){

    }

    public void setNotificationComment(boolean f){

    }

    public void setNotificationAll(boolean f){

    }

    // Get
    public Context getContext(){
        return this.context;
    }

    public String getMobileNumber(){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("mobile", Context.MODE_PRIVATE);
        String number = sharedPreferences.getString("mobileNumber",null);
        return "+8613050387411"; //number;
    }
    public Boolean isNotificationAudio(){
        return true;
    }
    public Boolean isNotificationComment(){
        return true;
    }
    public Boolean isNotifiationAll(){
        return true;
    }

    public Map<String,String> getSession(){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("session", Context.MODE_PRIVATE);
        String clientID = sharedPreferences.getString("clientID",null);
        String userID = sharedPreferences.getString("userID",null);
        String token = sharedPreferences.getString("token", null);
        Map<String,String> map = new HashMap<String, String>();
        map.put("clientID",clientID);
        map.put("userID", userID);
        map.put("token", token);

        // TODO: 10/26/2016 we have to remove this codes for production
        Map<String, String> dummySession = new HashMap<>();
        dummySession.put("clientID","69f86c18fc2bcc0c6a96cefcd8aa4eadbc271f96c4019432fdb34cdb164e5c4ced3578433a9895217dd7cf750bb37a54a91d710de690186c88b503d89b55ad011c");
        dummySession.put("userID", "580e137538fada7d16e170a3");
        dummySession.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU3ZmVlYjdlNWQ5NzM4MTNkMzk1YjY2ZCIsImNsaWVudElkIjoiNjlmODZjMThmYzJiY2MwYzZhOTZjZWZjZDhhYTRlYWRiYzI3MWY5NmM0MDE5NDMyZmRiMzRjZGIxNjRlNWM0Y2VkMzU3ODQzM2E5ODk1MjE3ZGQ3Y2Y3NTBiYjM3YTU0YTkxZDcxMGRlNjkwMTg2Yzg4YjUwM2Q4OWI1NWFkMDExYyIsInBob25lIjoiKzg2MTcwNzg3Njg4NzYiLCJpYXQiOjE0NzcyNTM0MzgsImV4cCI6MTQ3NzMzOTg2OH0.IzSnrwF2Nh_TucIuTrEAP6dplB_PPls-cwJecW1SG5c");

        return dummySession;
    }

    // Custom SharedPreferen for Stripe Tokens
    public void saveToken(Context context, List<Map<String,String>> tokens, String prefs_name, String key) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);
        editor = pref.edit();

        Gson gson = new Gson();
        String jsonLocations = gson.toJson(tokens);
        editor.putString(key, jsonLocations);
        editor.commit();
    }

    public void removeToken(Context context, Map<String,String> token, String prefs_name, String key){
        List<Map<String,String>> tokens = getTokens(context, prefs_name, key);
        if (tokens == null)
            tokens = new ArrayList<Map<String,String>>();

        tokens.remove(token);
        saveToken(context,tokens,prefs_name,key);
    }

    public void addToken(Context context, Map<String,String> token, String prefs_name, String key) {
        List<Map<String,String>> tokens = getTokens(context, prefs_name, key);
        if (tokens == null)
            tokens = new ArrayList<Map<String,String>>();

        tokens.add(token);
        saveToken(context, tokens, prefs_name, key);
    }

    public ArrayList<Map<String,String>> getTokens(Context context, String prefs_name, String key) {
        SharedPreferences pref;
        List<Map<String,String>> tokens;

        pref = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);

        if (pref.contains(key)) {
            String jsonLocations = pref.getString(key, null);
            Gson gson = new Gson();
            Map<String,String>[] tokenItems = gson.fromJson(jsonLocations, Map[].class);

            tokens = Arrays.asList(tokenItems);
            tokens = new ArrayList<Map<String,String>>(tokens);
        } else {
            return new ArrayList<Map<String,String>>();
        }

        return (ArrayList<Map<String,String>>)tokens;
    }

    // Contacts
    public List<AKContact> getContacts(){

        List<AKContact> list = new ArrayList<>();

        Cursor phones = this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            int hasPhone = phones.getInt(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (hasPhone > 0){
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                AKContact contact = new AKContact();
                contact.name = name;
                contact.phoneNumber = phoneNumber;
                list.add(contact);
            }

        }
        phones.close();

        return list;
    }
}
