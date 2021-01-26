package com.example.leafpiction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.leafpiction.Model.PhotoRequestModel;
import com.example.leafpiction.Model.PhotoResponseModel;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;
import com.example.leafpiction.Util.Tools;
import com.google.gson.Gson;

import org.json.JSONObject;

public class UploadTask {

    private Activity activity;
    private PhotoRequestModel model;

    HistoryDatabaseCRUD dbHandler;

    private static String API_UPLOAD = "http://192.168.43.67:5000/apiupload";
    public static String SUCCESS_RESPONSE_CODE = "0";
//    public static String FAILED_RESPONSE_CODE = "9998";
//    public static String NODATA_FAILED_RESPONSE_CODE = "9997";

    public UploadTask(Activity activity, PhotoRequestModel model){
        this.activity = activity;
        this.model = model;
    }

    public void execute(){

        final ProgressDialog progressDialog = ProgressDialog.show(activity,"",activity.getString(R.string.progressLoading),true,false);
        String jsonString = new Gson().toJson(model);
        JSONObject json = null;

        try {
            json = new JSONObject(jsonString);
        } catch (Exception e){}

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, API_UPLOAD, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String responseString = Tools.fix(response).toString();
                PhotoResponseModel responseModel = new Gson().fromJson(responseString, PhotoResponseModel.class);
                String code = responseModel.getCode();

                if(SUCCESS_RESPONSE_CODE.equalsIgnoreCase(code)) {
                    dbHandler = new HistoryDatabaseCRUD();
                    dbHandler.UpdateUploaded(activity.getApplicationContext(), model.getId());

                    Toast.makeText(activity, activity.getString(R.string.finished) , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(activity, activity.getString(R.string.failed) , Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "Error Uploading", Toast.LENGTH_SHORT).show();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(request);
    }
}
