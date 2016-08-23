package com.example.zerotorch.cloudcolorstorage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by ZeroTorch on 8/11/2016.
 */
public class API_Color_Grab extends AsyncTask<String, Void, ArrayList<ColorInfoClass>> {

    // Class Variables
    private Context context;
    private ProgressDialog progressDialog;
    private JSONParser jsonParser = new JSONParser();

    // Class Constructor
    public API_Color_Grab(Context context) { this.context = context; }

    @Override
    protected void onPreExecute() {
        Log.d("API","API_Color_Grab/onPreExecute()");
        // Show progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Retrieving User Colors ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected ArrayList<ColorInfoClass> doInBackground(String... args) {
        Log.d("API","API_Color_Grab/doInBackground()");

        // Search for Google sub identifier
        String sub = args[0];
        Log.d("API","sub: " + sub);

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

            // Get user information
            params.clear();  // Clear parameter list
            JSONObject jsonUser = jsonParser.makeHttpRequest(
                    MainActivity.API_URL + "user/" + userKey.toString(), "GET", params);

            if (jsonUser == null) {
                // Getting user info failed
                Log.e("API", "Retrieving user info failed!");
                return null;
            }

            JSONArray jsonColors = jsonUser.getJSONArray("colors");
            ArrayList<ColorInfoClass> colorList = new ArrayList<>();

            // Loop through colors
            for (int j = 0; j < jsonColors.length(); j++) {
                // Color Info Fields
                //      Long key
                //      String title
                //      Integer value
                //      Long pKey
                ColorInfoClass newColor = new ColorInfoClass();
                newColor.set_key(jsonColors.getLong(j));

                // Make GET Request for color info
                JSONObject jsonColor = jsonParser.makeHttpRequest(
                        MainActivity.API_URL + "color/" + newColor.get_key().toString(),
                        "GET", params);

                // Check JSON Response
                if (jsonColor == null) { return null; }
                newColor.set_title(jsonColor.getString("title"));
                newColor.set_value(jsonColor.getInt("value"));
                newColor.set_pKey(jsonColor.getLong("userKey"));

                // Add to User
                colorList.add(newColor);
            }

            if (colorList.size() == 0) {

            }

            return colorList;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<ColorInfoClass> colorList) {
        Log.d("API","API_Color_Grab/onPostExecute()");
        // Hide progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (colorList != null) {
            // Sort List before displaying
            // http://stackoverflow.com/questions/10853205/android-sort-arraylist-by-properties
            Collections.sort(colorList, new Comparator<ColorInfoClass>() {
                @Override
                public int compare(ColorInfoClass A, ColorInfoClass B) {
                    return A.get_title().compareTo(B.get_title());
                }
            });

            // Build List View
            ListView colorListView = (ListView) ((Activity)context).findViewById(R.id.content_user_info_lV_colorList);
            ColorListAdapter colorListAdapter = new ColorListAdapter(context, colorList);
            colorListView.setAdapter(colorListAdapter);
        }
    }
}
