package com.example.zerotorch.cloudcolorstorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserInfoActivity extends AppCompatActivity {

    private String sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_user_info_FAB_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tV_userKey = (TextView) findViewById(R.id.content_user_info_tV_userKey);
                Long userKey = Long.parseLong(tV_userKey.getText().toString());

                // Start new activity (ColorEditActivity) - Returns to MainActivity
                Intent intent = new Intent(UserInfoActivity.this, ColorEditActivity.class);
                intent.putExtra("pKey",userKey);
                intent.putExtra("type", MainActivity.REQUEST_CODE.ADD_COLOR);
                startActivityForResult(intent, MainActivity.REQUEST_CODE.ADD_COLOR);
            }
        });

        // Run Async Color Grab
        Bundle bundle = getIntent().getExtras();
        sub = bundle.getString("sub");

        // Make Async Call to get User Key
        if (sub != null) {
            API_User_Key api_user_key = new API_User_Key(this);
            api_user_key.execute(sub);
        }

        if (sub != null) {
            API_Color_Grab api_color_grab = new API_Color_Grab(this);
            api_color_grab.execute(sub);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("UIA","requestCode: " + requestCode + "/resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        // Color Info Variables
        Long colorKey;
        String colorTitle;
        Integer colorValue;

        // User Info Variables
        Long userKey;

        switch (requestCode) {
            case (MainActivity.REQUEST_CODE.ADD_COLOR):
                if (resultCode == MainActivity.RESULT_CODE.RESULT_OK) {
                    Log.d("Request", "ADD_COLOR");

                    // Extract Result Values
                    colorTitle = data.getStringExtra("title");
                    colorValue = data.getIntExtra("value", 0);
                    userKey = data.getLongExtra("pKey", 0);

                    // Add Color Info Class
                    ColorInfoClass addColor = new ColorInfoClass();
                    addColor.set_title(colorTitle);
                    addColor.set_value(colorValue);
                    addColor.set_pKey(userKey);

                    // Add Color
                    API_Add_Color api_add_color = new API_Add_Color(this);
                    api_add_color.execute(addColor);

                    // Refresh from Datastore
                    API_Color_Grab api_color_grab = new API_Color_Grab(this);
                    api_color_grab.execute(sub);
                }
                break;
            case (MainActivity.REQUEST_CODE.UPDATE_COLOR):
                if (resultCode == MainActivity.RESULT_CODE.RESULT_OK) {
                    Log.d("Request", "UPDATE_COLOR");

                    // Extract Result Values
                    colorKey = data.getLongExtra("key", 0);
                    colorTitle = data.getStringExtra("title");
                    colorValue = data.getIntExtra("value", 0);
                    userKey = data.getLongExtra("pKey", 0);

                    // Update Color Info Class
                    ColorInfoClass updateColor = new ColorInfoClass();
                    updateColor.set_key(colorKey);
                    updateColor.set_title(colorTitle);
                    updateColor.set_value(colorValue);
                    updateColor.set_pKey(userKey);

                    // Update Color
                    API_Update_Color api_update_color = new API_Update_Color(this);
                    api_update_color.execute(updateColor);

                    // Refresh from Datastore
                    API_Color_Grab api_color_grab = new API_Color_Grab(this);
                    api_color_grab.execute(sub);
                } else if (resultCode == MainActivity.RESULT_CODE.RESULT_DELETE) {
                    Log.d("Request", "DELETE_COLOR");

                    // Extract Result Values
                    colorKey = data.getLongExtra("key", 0);

                    // Delete Color Info Class
                    ColorInfoClass deleteColor = new ColorInfoClass();
                    deleteColor.set_key(colorKey);

                    // Delete Color
                    API_Delete_Color api_delete_color = new API_Delete_Color(this);
                    api_delete_color.execute(deleteColor);

                    // Refresh from Datastore
                    API_Color_Grab api_color_grab = new API_Color_Grab(this);
                    api_color_grab.execute(sub);
                }
                break;
        }
    }
}
