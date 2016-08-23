package com.example.zerotorch.cloudcolorstorage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by ZeroTorch on 8/12/2016.
 */
public class API_User_Key extends AsyncTask<String, Void, Long> {

    // Class Variables
    private Context context;
    private ProgressDialog progressDialog;
    private JSONParser jsonParser = new JSONParser();

    // Class Constructor
    public API_User_Key(Context context) { this.context = context; }

    @Override
    protected void onPreExecute() {
        Log.d("API","API_User_Key/onPreExecute()");
        // Show progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Retrieving User Colors ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected Long doInBackground(String... args) {
        Log.d("API","API_User_Key/doInBackground()");

        // Search for Google sub identifier
        String sub = args[0];

        if (sub == null) {
            return null;
        }

        try {
            //*** Search for user matching Google sub ***
            // Build Parameter List - POST Request
            HashMap<String,String> params = new HashMap<>();
            params.put("sub",String.valueOf(sub));

            // Make POST Request
            JSONObject jsonObject = jsonParser.makeHttpRequest(
                    MainActivity.API_URL + "user/search/", "POST", params);

            // Check JSON response
            if (jsonObject == null) {
                // Adding new user failed
                Log.e("API", "Searching for user failed!");
                return null;
            }

            // Check the length of the returned keys
            JSONArray keys = jsonObject.getJSONArray("keys");
            Long userKey;
            if (keys.length() == 0) {
                // Add a new user via POST Request
                JSONObject newUser = jsonParser.makeHttpRequest(
                        MainActivity.API_URL + "user/", "POST", params);

                if (newUser == null) {
                    // Adding new user failed
                    Log.e("API", "Adding new user failed!");
                    return null;
                }

                userKey = newUser.getLong("key");
            }
            else {
                // Process exiting user
                userKey = keys.getLong(0);
            }

            return userKey;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Long userKey) {
        Log.d("API","API_User_Key/onPostExecute()");
        // Hide progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Log.d("API","userKey: " + userKey.toString());

        if (userKey != null) {
            TextView tV_userKey = (TextView) ((Activity)context).findViewById(R.id.content_user_info_tV_userKey);
            tV_userKey.setText(userKey.toString());
        }
    }
}
