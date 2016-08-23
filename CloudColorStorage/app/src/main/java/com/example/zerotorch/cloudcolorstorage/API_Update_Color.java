package com.example.zerotorch.cloudcolorstorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ZeroTorch on 8/12/2016.
 */
public class API_Update_Color extends AsyncTask<ColorInfoClass, Void, JSONObject> {

    // Class Variables
    private Context context;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog progressDialog;

    // Class Constructor
    public API_Update_Color(Context context) { this.context = context; }

    @Override
    protected void onPreExecute() {
        Log.d("API","API_User_Key/onPreExecute()");
        // Show progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating Color ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(ColorInfoClass... args) {
        Log.d("API","API_User_Key/doInBackground()");
        // Extract arguments
        Long colorKey = args[0].get_key();
        String colorTitle = args[0].get_title();
        Integer colorValue = args[0].get_value();
        Long colorPKey = args[0].get_pKey();

        try {
            // Build Parameter List - PUT Request
            HashMap<String, String> params = new HashMap<>();
            params.put("key", colorKey.toString());
            params.put("title", colorTitle);
            params.put("value", colorValue.toString());

            // Make PUT Request
            JSONObject jsonObject = jsonParser.makeHttpRequest(
                    MainActivity.API_URL + "color/", "PUT", params);

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
        Log.d("API","API_User_Key/onPostExecute()");
        // Hide progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
