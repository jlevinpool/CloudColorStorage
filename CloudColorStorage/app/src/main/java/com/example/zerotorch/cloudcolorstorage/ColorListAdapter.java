package com.example.zerotorch.cloudcolorstorage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ZeroTorch on 8/11/2016.
 */
public class ColorListAdapter extends ArrayAdapter implements View.OnClickListener{

    private Context context;

    public ColorListAdapter(Context context, ArrayList colorList) {
        super(context, 0, colorList);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ColorInfoClass colorInfo = (ColorInfoClass) getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_color_info, viewGroup, false);
        }

        // Set Color Item Properties - item_color_info.xml
        TextView colorKey = (TextView) view.findViewById(R.id.item_color_info_tV_colorKey);
        colorKey.setText(colorInfo.get_key().toString());
        TextView colorTitle = (TextView) view.findViewById(R.id.item_color_info_tV_colorTitle);
        colorTitle.setText(colorInfo.get_title());
        TextView colorValue = (TextView) view.findViewById(R.id.item_color_info_tV_colorValue);
        colorValue.setText(colorInfo.get_value_HexString());

        // Set Color Sample
        ImageView colorSample = (ImageView) view.findViewById(R.id.item_color_info_image_ColorF);
        colorSample.setBackgroundColor(colorInfo.get_value());

        // Create Click Listener
        RelativeLayout colorLayout = (RelativeLayout) view.findViewById(R.id.item_color_info_rL_layout);
        colorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start new activity (ColorEditActivity) - Returns to UserInfoActivity
                Intent intent = new Intent(context, ColorEditActivity.class);
                intent.putExtra("key",colorInfo.get_key());
                intent.putExtra("title",colorInfo.get_title());
                intent.putExtra("value",colorInfo.get_value());
                intent.putExtra("pKey",colorInfo.get_pKey());
                intent.putExtra("type",MainActivity.REQUEST_CODE.UPDATE_COLOR);
                ((Activity)context).startActivityForResult(intent, MainActivity.REQUEST_CODE.UPDATE_COLOR);
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        // Do Nothing
    }
}
