/*
 * Copyright (C) 2016 The Android Open Source Project
 */
package app.com.example.android.galleriadcinema;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 14-Feb-16.
 */

/**
 * Customized adapter for GridView containing Movie Poster Thumbnails.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mThumbUrls = new ArrayList<String>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void setmThumbUrls(String[] imgUrls){
        List < String > toAdd = Arrays.asList(imgUrls);
        mThumbUrls.addAll(toAdd);
    }

    public void resetThumbUrls(){
        mThumbUrls.clear();
    }

    public int getCount() {
        return mThumbUrls.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 277));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
        }
        else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(mThumbUrls.get(position)).into(imageView);
        return imageView;
    }

}