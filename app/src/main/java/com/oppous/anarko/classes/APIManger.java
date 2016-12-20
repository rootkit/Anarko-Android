package com.oppous.anarko.classes;

import android.content.Context;
import android.util.Log;

import com.oppous.anarko.AKConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.ProgressListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIManger {

    public interface APISuccessListener{
        void onFailure(String error);
        void onSuccess(Response response);
    }

    private static APIManger ourInstance = new APIManger();

    public static APIManger getInstance() {
        return ourInstance;
    }

    private Context context;

    private static final int KEY_REQUEST_REGISTER = 383;
    private static final int KEY_REQUEST_CHANGE_PHONE = 384;
    private static final int KEY_REQUEST_CHANGE_VERIFY = 385;
    private static final int KEY_REQUEST_LOGIN = 386;
    private static final int KEY_REQUEST_RETOKEN = 387;
    private static final int KEY_REQUEST_COMMENT = 388;
    private static final int KEY_REQUEST_MASKS = 389;
    private static final int KEY_REQUEST_STORE = 390;
    private static final int KEY_REQUEST_REPORT = 391;
    private static final int KEY_REQUEST_INVITE = 392;
    private static final int KEY_REQUEST_FEEDBACK = 393;
    private static final int KEY_REQUEST_BUY = 394;

    private static final String URL_BASE = "http://ec2-52-4-43-153.compute-1.amazonaws.com:8443/api/v1/";
    private static final String URL_VERSION = "http://ec2-52-4-43-153.compute-1.amazonaws.com:8443/version";
    private static final String URL_REG_PHONE = "regphone";             // type:POST register phone number
    private static final String URL_CHA_PHONE = "changeNumber";         // type:POST change phone number
    private static final String URL_CHA_VERIFY = "verifyChangeNumber";  // type:POST change phone number
    private static final String URL_LOGIN = "login";                    // type:POST login the user
    private static final String URL_REFRESH = "refresh_token";          // type:POST generate new token
    private static final String URL_UPLOAD = "vidform";                 // type:POST add Anarko and upload video
    private static final String URL_EXPLORE = "explore";                // type:GET  return anarko's
    private static final String URL_REPORT = "vid/report";              // type:POST vid_id, reason, phone, comment
    public static final String URL_ANARKO = "http://ec2-52-4-43-153.compute-1.amazonaws.com:8443/api/v1/vid/";
    private static final String URL_MASKS = "getMyMasks/";              // type:POST
    private static final String URL_STORE = "store";                    // type:POST
    private static final String URL_INVITE = "invite";                  // type:POST
    private static final String URL_LOCATION = "location/";             // type:GET
    private static final String URL_SUPERLINK = "getSuperlinkUrl/";     // type:GET
    private static final String URL_FEEDBACK = "feedback";              // type:POST
    private static final String URL_BUY = "buy/mask";                   // type:POST

    private OkHttpClient client;

    private APIManger() {
        client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
    }

    public void checkVersion(final APISuccessListener listener){
        String url = URL_VERSION;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();

                try {
                    JSONObject resJSONObject = new JSONObject(res);
                    JSONObject status = resJSONObject.getJSONObject("status");
                    JSONObject version = resJSONObject.getJSONObject("version");
                    JSONObject build = resJSONObject.getJSONObject("build");

                    Log.e("response",res);
                    listener.onSuccess(response);

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure("API is not working properly.");
                }
            }
        });
    }

    public void registerPhone(String phone, String lang, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", phone);
            jsonObject.put("lang", lang);
            this.requestParam(jsonObject, URL_BASE+URL_REG_PHONE,KEY_REQUEST_REGISTER, listener);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changePhone(String userID, String phone, String lang, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userID);
            jsonObject.put("phone", phone);
            jsonObject.put("lang", lang);
            this.requestParam(jsonObject, URL_BASE+URL_CHA_PHONE+"/"+userID,KEY_REQUEST_CHANGE_PHONE, listener);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changePhoneVerify(String phone, String code, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", phone);
            jsonObject.put("code", code);
            this.requestParam(jsonObject, URL_BASE+URL_CHA_VERIFY, KEY_REQUEST_CHANGE_VERIFY, listener);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loginUser(String phone, String code, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", phone);
            jsonObject.put("code", code);
            this.requestParam(jsonObject, URL_BASE+URL_LOGIN, KEY_REQUEST_LOGIN, listener);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refreshToken(String clientID, String oldToken, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clientId", clientID);
            jsonObject.put("token", oldToken);
            this.requestParam(jsonObject, URL_BASE+URL_REFRESH, KEY_REQUEST_RETOKEN, listener);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postFeedBack(String comment, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("comment", comment);
            this.requestParam(jsonObject, URL_BASE+URL_FEEDBACK, KEY_REQUEST_FEEDBACK,listener);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void postComment(String postID, String comment, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("comment", comment);
            this.requestParam(jsonObject, URL_BASE+"vid/"+postID+"/comments", KEY_REQUEST_COMMENT,listener);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void reportAnarko(String postID, AKConstants.Report_Type reason, String phone, String comment, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("vid_id", postID);
            jsonObject.put("reason", reason);
            jsonObject.put("phone", phone);
            jsonObject.put("comment", comment);
            this.requestParam(jsonObject, URL_BASE+URL_REPORT, KEY_REQUEST_REPORT,listener);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void inviteFriends(String phone, JSONArray friends, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", phone);
            jsonObject.put("friends", friends);
            this.requestParam(jsonObject, URL_BASE+URL_INVITE, KEY_REQUEST_INVITE,listener);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void buyItems(String stripeToken, String maskid, double amount, final APISuccessListener listener){
        Map<String,String>session = AppManager.getInstance().getSession();
        String userID = session.get("userID");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", userID);
            jsonObject.put("stripeToken", stripeToken);
            jsonObject.put("maskid", maskid);
            jsonObject.put("amount", amount);
            this.requestParam(jsonObject, URL_BASE+URL_BUY, KEY_REQUEST_BUY,listener);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getMasks(String user_ID, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", user_ID);
            this.requestParam(jsonObject, URL_BASE+URL_MASKS, KEY_REQUEST_MASKS,listener);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void requestParam(JSONObject params, String url, final int key, final APISuccessListener listener){
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, params.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFailure(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    switch (key){
                        case KEY_REQUEST_REGISTER:
                        {
                            try {
                                String res = response.body().string();
                                Log.d("register phone response",res);
                                listener.onSuccess(response);
                            }catch(Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }

                        break;
                        case KEY_REQUEST_CHANGE_PHONE:
                        {
                            try {
                                String res = response.body().string();
                                Log.d("change phone response",res);
                                listener.onSuccess(response);
                            }catch(Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_CHANGE_VERIFY:
                        {
                            try {

                                String res = response.body().string();
                                JSONObject resJSONObject = new JSONObject(res);
                                JSONObject dictionary = resJSONObject.getJSONObject("data");
                                String clientId = dictionary.getString("clientId");
                                String userId = dictionary.getString("_id");
                                String token = dictionary.getString("token");
                                AppManager.getInstance().setSession(clientId,userId,token);

                                listener.onSuccess(response);

                            } catch (Exception e) {
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_LOGIN:
                        {
                            try {
                                String res = response.body().string();
                                JSONObject resJSONObject = new JSONObject(res);
                                JSONObject dictionary = resJSONObject.getJSONObject("data");
                                String clientId = dictionary.getString("clientId");
                                String userId = dictionary.getString("_id");
                                String token = dictionary.getString("token");
                                AppManager.getInstance().setSession(clientId,userId,token);

                                listener.onSuccess(response);

                            } catch (Exception e) {
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_COMMENT:
                        {
                            try {
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_FEEDBACK:
                        {
                            try {
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_REPORT:
                        {
                            try {
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_INVITE:
                        {
                            try {
                                String res = response.body().string();
                                Log.d("invite response",res);
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }

                        break;

                        case KEY_REQUEST_MASKS:
                        {
                            try {
                                String res = response.body().string();
                                Log.d("mask response",res);
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_BUY:
                        {
                            try {
                                String res = response.body().string();
                                Log.d("buy response",res);
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                        case KEY_REQUEST_STORE:
                        {
                            try {
                                String res = response.body().string();
                                Log.d("store response",res);
                                listener.onSuccess(response);
                            }catch (Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        break;

                    }

                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getSuperLink(final APISuccessListener listener){
        Map<String,String> session = AppManager.getInstance().getSession();
        String userID = session.get("userID");
        String token = session.get("token");
        String url = URL_BASE+URL_SUPERLINK+userID+"?token="+token;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response);
            }
        });
    }

    public void getAddress(JSONArray location, final APISuccessListener listener){
        try {
            int lat = location.getInt(0);
            int lng = location.getInt(1);
            String url = URL_BASE+URL_LOCATION+lat+","+lng;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFailure(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getStoreList(String type, final APISuccessListener listener){

        String url = URL_BASE+URL_STORE+"/"+type;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();

                AppManager.getInstance().arrItems.clear();
                try {
                    JSONObject resJSONObject = new JSONObject(res);
                    JSONArray data = resJSONObject.getJSONArray("data");
                    for (int index = 0; index < data.length(); index++){
                        JSONObject jsonItem = data.getJSONObject(index);
                        AKItem item = new AKItem();
                        item.idx = jsonItem.getString("_id");
                        item.name = jsonItem.getString("name");
                        item.description = jsonItem.getString("description");
                        item.type = jsonItem.getString("type");
                        item.price = jsonItem.getDouble("price");
                        item.isPrivate=jsonItem.getInt("private");
                        item.creator=jsonItem.getString("creator");
                        item.minVersionReq=jsonItem.getInt("minVersionReq");
                        item.__v=jsonItem.getInt("__v");
                        item.active=jsonItem.getInt("active");
                        item.thumbs=jsonItem.getJSONArray("thumbs");
                        item.packages=jsonItem.getJSONArray("package");
                        item.isFree=jsonItem.getInt("isFree");
                        AppManager.getInstance().arrItems.add(item);
                    }

                    listener.onSuccess(response);

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure("API is not working properly.");
                }
            }
        });
    }

    public void loadExplore(String tag, int from, int size, final APISuccessListener listener){
        try {
            Map<String,String> map = AppManager.getInstance().getSession();
            String token = map.get("token");
            String url;
            if (tag == null){
                url = URL_BASE+URL_EXPLORE+"?token="+token+"&from="+from+"&size="+size;
            }else{
                url = URL_BASE+URL_EXPLORE+"?token="+token+"&from="+from+"&size="+size+"&tags="+tag;
            }

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFailure(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void download(String url) {
        // Callback could not access to interface
        final ProgressListener progressResponseListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesRead, long contentLength, boolean done) {
                Log.e("TAG", "bytesRead:" + bytesRead);
                Log.e("TAG", "contentLength:" + contentLength);
                Log.e("TAG", "done:" + done);
                if (contentLength != -1) {
                    Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
                }
                Log.e("TAG", "================================");
            }
        };

        // Request
        final Request request1 = new Request.Builder()
                .url(url)
                .build();

        ProgressHelper.addProgressResponseListener(client, progressResponseListener).newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Downloading", "error ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("Downloading", response.body().string());
            }
        });

    }

    public void upload(File video, File image) {

        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesWrite, long contentLength, boolean done) {
                Log.e("TAG", "bytesWrite:" + bytesWrite);
                Log.e("TAG", "contentLength" + contentLength);
                Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
                Log.e("TAG", "done:" + done);
                Log.e("TAG", "================================");
            }
        };

        Map<String,String> map = AppManager.getInstance().getSession();
        String token = map.get("token");

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("tags", "android")
                .addFormDataPart("title","upload testing")
                .addFormDataPart("description","This video has been made on Android version :)")
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"video\";filename=\"video.mp4\""),
                        RequestBody.create(MediaType.parse("application/octet-stream"), video))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"thumbnail.png\""),
                        RequestBody.create(MediaType.parse("image/png"), image))
                .build();

        final Request request = new Request.Builder()
                .url(URL_BASE+URL_UPLOAD+"?token="+token)
                .post(ProgressHelper.addProgressRequestListener(requestBody, progressListener))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Uploading", "error ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("Uploading", response.body().string());
            }
        });

    }

    public void setContext(Context c){
        this.context = c;
    }

    public Context getContext(){
        return this.context;
    }
}
