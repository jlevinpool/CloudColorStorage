package com.example.zerotorch.cloudcolorstorage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Validates Google login token using Google App Engine backend
 */
public class API_Validate_Login extends AsyncTask<String, Void, JSONObject> {

    // Class Variables
    private Context context;
    private ProgressDialog progressDialog;
    private JSONParser jsonParser = new JSONParser();

    // Class Constructor
    public API_Validate_Login(Context context) { this.context = context; }

    @Override
    protected void onPreExecute() {
        Log.d("API","API_Validate_Login/onPreExecute()");
        // Show progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Validating User Login ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        Log.d("API","API_Validate_Login/doInBackground()");
        // Get token
        String tokenID = args[0];

        try {
            // Build Parameter List - POST Request
            HashMap<String,String> params = new HashMap<>();
            params.put("tokenID",tokenID);

            // Make POST Request
            JSONObject jsonObject = jsonParser.makeHttpRequest(
                    MainActivity.API_URL + "login/", "POST", params);

            // Check JSON response
            if (jsonObject != null) {
                return jsonObject;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.d("API","API_Validate_Login/onPostExecute()");
        // Hide progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        try {
            TextView tV_userID = (TextView) ((Activity) context).findViewById(R.id.activity_login_tV_userID);
            if (jsonObject != null) {
                // Extract userID
                String userID = jsonObject.getString("userID");

                // Set Text Fields
                tV_userID.setText(userID);
            }
            else {
                // Invalid Login - Remove Cloud on-Click
                ((Activity)context).findViewById(R.id.activity_login_img_cloud).setOnClickListener(null);
                tV_userID.setText(null);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
