/*
 * Copyright (C) 2016 The Android Open Source Project
 */
package app.com.example.android.galleriadcinema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a grid view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    protected ImageAdapter mImgAdapter;
    private int mPage = 1;
    private boolean mNextPage=false;
    protected ArrayList< MovieDetail > movieDetailList = new ArrayList<MovieDetail>();

    final String MOVIE_DETAIL_LIST = "MovieDetailList";
    final String SAVED_PAGE_NO = "SavedPageNumber";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort_by) {
            mPage=1;
            mImgAdapter.resetThumbUrls();
            movieDetailList.clear();
            Intent sort = new Intent(getContext(),SortActivity.class);
            startActivity(sort);
            return true;
        }
        else if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onStart() {
        super.onStart();
        boolean isConnected = checkInternetConnection();
        if(!isConnected){
            displayNoConnectionMessage();
        }
        if(mImgAdapter.getCount()==0 && movieDetailList.isEmpty() && isConnected){
            try {
                updateMovieData(String.valueOf(mPage));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_DETAIL_LIST, movieDetailList);
        outState.putInt(SAVED_PAGE_NO, mPage);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mImgAdapter = new ImageAdapter(getContext());
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DETAIL_LIST)){
            movieDetailList = savedInstanceState.getParcelableArrayList(MOVIE_DETAIL_LIST);


            if (movieDetailList != null) {
                int savedListSize = movieDetailList.size();
                String[] posterPaths = new String[savedListSize];
                for(int itr = 0;itr < savedListSize;itr++){
                    posterPaths[itr] = movieDetailList.get(itr).posterURL;
                }
                mImgAdapter.setmThumbUrls(posterPaths);
                mPage=savedInstanceState.getInt(SAVED_PAGE_NO);
            }
        }


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(mImgAdapter);




        final String INTENT_PARCEL_MOVIE_DETAILS = "MovieData";

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent loadMovieDetails = new Intent(getContext(), MovieDetailActivity.class)
                        .putExtra(INTENT_PARCEL_MOVIE_DETAILS, movieDetailList.get(position));
                startActivity(loadMovieDetails);
            }
        });

        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {

                                         @Override
                                         public void onScrollStateChanged
                                                 (AbsListView view, int scrollState) {
                                         }

                                         @Override
                                         public void onScroll
                                                 (AbsListView view, int firstVisibleItem,
                                                  int visibleItemCount,
                                                  int totalItemCount) {
                                             int lastInScreen = firstVisibleItem + visibleItemCount;

                                             if (totalItemCount == 0) {
                                                 return;
                                             }

                                             if ((lastInScreen == totalItemCount)) {
                                                 boolean isConnected = checkInternetConnection();
                                                 if(!isConnected){
                                                     displayNoConnectionMessage();
                                                 }
                                                 if (mNextPage && isConnected) {
                                                     mPage++;
                                                     mNextPage = false;
                                                     try {
                                                         updateMovieData(String.valueOf(mPage));
                                                     } catch (ExecutionException | InterruptedException e) {
                                                         e.printStackTrace();
                                                     }
                                                 }
                                             } else if (!mNextPage) {
                                                 //scrolling inside the list
                                                 mNextPage = true;
                                             }
                                         }
                                     }
        );
        return rootView;
    }

    /**
     * Updates the list of Movie poster thumbnails using AsyncTask.
     */
    protected void updateMovieData(String pageNumber) throws ExecutionException, InterruptedException {
        FetchMovieDB checkDB = new FetchMovieDB();
        checkDB.execute(pageNumber, this);
    }

    protected boolean checkInternetConnection(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    protected void displayNoConnectionMessage(){
            String NO_CONNECTION = "Requires connection to internet";
            Toast.makeText(getContext(),NO_CONNECTION , Toast.LENGTH_SHORT).show();
    }


}
