/*
 * Copyright (C) 2016 The Android Open Source Project
 */
package app.com.example.android.galleriadcinema.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import app.com.example.android.galleriadcinema.Activities.MainActivity;
import app.com.example.android.galleriadcinema.Activities.MovieDetailActivity;
import app.com.example.android.galleriadcinema.Activities.SortActivity;
import app.com.example.android.galleriadcinema.FetchMovieDB;
import app.com.example.android.galleriadcinema.ImageAdapter;
import app.com.example.android.galleriadcinema.MovieDetail;
import app.com.example.android.galleriadcinema.R;
import app.com.example.android.galleriadcinema.Utility;

/**
 * A placeholder fragment containing a grid view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    public ImageAdapter mImgAdapter;
    private int mPage = 1;
    private boolean mNextPage=false;
    public ArrayList<MovieDetail> movieDetailList = new ArrayList<MovieDetail>();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
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

        int id = item.getItemId();

        if (id == R.id.sort_by) {
            mPage=1;
            mImgAdapter.resetThumbUrls();
            movieDetailList.clear();
            Intent sort = new Intent(getContext(),SortActivity.class);
            startActivity(sort);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onStart() {
        super.onStart();
        boolean isConnected = Utility.checkInternetConnection(getContext());
        if(!isConnected){
            displayNoConnectionMessage();
        }
        if(mImgAdapter.getCount()==0 && movieDetailList.isEmpty()){
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

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    if (((MainActivity)getActivity()).getUIMode()) {
                        Bundle args = new Bundle();
                        args.putParcelable(INTENT_PARCEL_MOVIE_DETAILS, movieDetailList.get(position));
                        MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
                        fragment.setArguments(args);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container
                                        , fragment, DETAILFRAGMENT_TAG)
                                .commit();
                    }
                    else {
                        Intent loadMovieDetails = new Intent(getContext(), MovieDetailActivity.class)
                                        .putExtra(INTENT_PARCEL_MOVIE_DETAILS, movieDetailList.get(position));
                        startActivity(loadMovieDetails);
                    }
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
                                             SharedPreferences preferences = PreferenceManager.
                                                     getDefaultSharedPreferences(getContext());
                                             String sortingChoice = preferences.getString(
                                                     getActivity().getString(R.string.pref_sort_key),"1");
                                             int lastInScreen = firstVisibleItem + visibleItemCount;

                                             if (totalItemCount == 0) {
                                                 return;
                                             }

                                             if ((lastInScreen == totalItemCount)) {
                                                 boolean isConnected = Utility.
                                                         checkInternetConnection(getContext());
                                                 if (!isConnected) {
                                                     displayNoConnectionMessage();
                                                 }
                                                 if (mNextPage && isConnected) {
                                                     try {
                                                         if(!sortingChoice.equals("3")){
                                                             mPage++;
                                                             mNextPage = false;
                                                             updateMovieData(String.valueOf(mPage));
                                                         }

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

    protected  void displayNoConnectionMessage(){
        String NO_CONNECTION = "You are using Offline Mode";
        Toast.makeText(getContext(),NO_CONNECTION , Toast.LENGTH_SHORT).show();
    }
}
