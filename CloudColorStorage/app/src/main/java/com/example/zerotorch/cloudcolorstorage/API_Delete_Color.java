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
public class API_Delete_Color extends AsyncTask<ColorInfoClass, Void, JSONObject> {

    // Class Variables
    private Context context;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog progressDialog;

    // Class Constructor
    public API_Delete_Color(Context context) { this.context = context; }

    @Override
    protected void onPreExecute() {
        Log.d("API","API_Delete_Color/onPreExecute()");
        // Show progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting Color ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(ColorInfoClass... args) {
        Log.d("API","API_Delete_Color/doInBackground()");
        // Extract arguments
        Long colorKey = args[0].get_key();

        try {
            // Build Parameter List - DELETE Request
            HashMap<String,String> params = new HashMap<>();

            // Make DELETE Request
            JSONObject jsonObject = jsonParser.makeHttpRequest(
                    MainActivity.API_URL + "color/" + colorKey.toString(), "DELETE", params);

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
        Log.d("API","API_Delete_Color/onPostExecute()");
        // Hide progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
