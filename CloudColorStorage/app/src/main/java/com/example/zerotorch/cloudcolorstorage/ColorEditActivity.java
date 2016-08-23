package com.example.zerotorch.cloudcolorstorage;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorEditActivity extends AppCompatActivity implements View.OnClickListener {

    // Color Info Class Variables
    private Long colorKey;
    private String colorTitle;
    private Integer colorValue;
    private Long colorPKey;

    // Request Type
    private Integer type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_edit);

        // Get values from Intent Call
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type");
        switch (type) {
            case MainActivity.REQUEST_CODE.ADD_COLOR:
                colorKey = null;
                colorTitle = null;
                colorValue = Color.BLACK;  // Default starting color
                colorPKey = bundle.getLong("pKey");
                break;
            case MainActivity.REQUEST_CODE.UPDATE_COLOR:
                colorKey = bundle.getLong("key");
                colorTitle = bundle.getString("title");
                colorValue = bundle.getInt("value");
                colorPKey = bundle.getLong("pKey");
                break;
        }
        init();
    }

    private void init() {
        // Initialize Fields
        TextView heading = (TextView) findViewById(R.id.activity_color_edit_lbl_Header);
        String headingString = null;
        switch (type) {
            case MainActivity.REQUEST_CODE.ADD_COLOR:
                headingString = "Add New Color";
                break;
            case MainActivity.REQUEST_CODE.UPDATE_COLOR:
                headingString = "Edit Color";
                break;
        }
        heading.setText(headingString);

        final EditText editTitle = (EditText) findViewById(R.id.activity_color_edit_text_Title);
        editTitle.setText(colorTitle);

        TextView editColor = (TextView) findViewById(R.id.activity_color_edit_text_Color);
        editColor.setText(String.format("#%06X", 0xFFFFFFFF & colorValue));

        ImageView colorSample = (ImageView) findViewById(R.id.activity_color_edit_img_ColorF);
        colorSample.setBackgroundColor(colorValue);

        // Set onClick Listeners
        final Button okButton = (Button) findViewById(R.id.activity_color_edit_okButton);
        okButton.setOnClickListener(this);
        if (colorTitle == null) {
            okButton.setEnabled(false);
        }

        Button cancelButton = (Button) findViewById(R.id.activity_color_edit_cancelButton);
        cancelButton.setOnClickListener(this);

        Button deleteButton = (Button) findViewById(R.id.activity_color_edit_deleteColor);
        deleteButton.setOnClickListener(this);
        if (type == MainActivity.REQUEST_CODE.ADD_COLOR) {
            deleteButton.setVisibility(View.GONE);  // Hide delete button
        }

        FrameLayout colorFrame = (FrameLayout) findViewById(R.id.activity_color_edit_frame_Color);
        colorFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve color title
                EditText editTitle = (EditText) findViewById(R.id.activity_color_edit_text_Title);
                colorTitle = editTitle.getText().toString();

                // Start new activity - ColorPickerActivity
                Intent intent = new Intent(ColorEditActivity.this, ColorPickerActivity.class);
                intent.putExtra("value",colorValue);
                startActivityForResult(intent, MainActivity.REQUEST_CODE.COLOR_PICKER);
            }
        });

        // Add text watcher
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check if something is entered
                okButton.setEnabled(!editTitle.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do Nothing
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Update layout based on results from Intent
        switch(requestCode) {
            case (MainActivity.REQUEST_CODE.COLOR_PICKER):
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    // Extract Result Values
                    colorValue = data.getIntExtra("value",0);

                    // Update color value & sample
                    TextView editColor = (TextView) findViewById(R.id.activity_color_edit_text_Color);
                    editColor.setText(String.format("#%06X", 0xFFFFFFFF & colorValue));

                    ImageView colorSample = (ImageView) findViewById(R.id.activity_color_edit_img_ColorF);
                    colorSample.setBackgroundColor(colorValue);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent;
        // Handle button clicks
        switch (view.getId()) {
            case R.id.activity_color_edit_okButton:
                // Retrieve color title
                EditText editTitle = (EditText) findViewById(R.id.activity_color_edit_text_Title);
                colorTitle = editTitle.getText().toString();

                // Return values to previous Activity
                resultIntent = new Intent();

                resultIntent.putExtra("value",colorValue);
                resultIntent.putExtra("key", colorKey);
                resultIntent.putExtra("title", colorTitle);
                resultIntent.putExtra("pKey", colorPKey);

                setResult(MainActivity.RESULT_CODE.RESULT_OK, resultIntent);

                finish();
                break;
            case R.id.activity_color_edit_cancelButton:
                setResult(MainActivity.RESULT_CODE.RESULT_CANCELED);
                finish();  // Do Nothing
                break;
            case R.id.activity_color_edit_deleteColor:
                // Return values to previous Activity
                resultIntent = new Intent();
                resultIntent.putExtra("key",colorKey);
                setResult(MainActivity.RESULT_CODE.RESULT_DELETE, resultIntent);

                finish();
                break;
        }
    }
}
