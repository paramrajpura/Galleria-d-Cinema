/*
 * Copyright (C) 2016 The Android Open Source Project
 */
package app.com.example.android.galleriadcinema;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

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

    private ImageAdapter mImgAdapter;
    private int mPage = 1;
    private boolean mNextPage=false;
    private ArrayList< MovieDetail > movieDetailList = new ArrayList<MovieDetail>();

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

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent loadMovieDetails = new Intent(getContext(),MovieDetailActivity.class)
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
                                                 if (mNextPage) {
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
    private void updateMovieData(String pageNumber) throws ExecutionException, InterruptedException {
        FetchMovieDB checkDB = new FetchMovieDB();
        checkDB.execute(pageNumber);
    }

    /**
     * Async Task for retrieving movie details from TMDB.
     */
    public class FetchMovieDB extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPostExecute(String[] strings) {
            mImgAdapter.setmThumbUrls(strings);
            mImgAdapter.notifyDataSetChanged();
            super.onPostExecute(strings);
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection;
            BufferedReader reader;
            final String SORT_BY_POPULARITY = "1";
            final String QUERY_PAGE = "page";
            final String QUERY_API_KEY = "api_key";
            final String QUERY_SORTBY = "sort_by";
            final String API_KEY = ""; //ENTER API KEY HERE

            // Will contain the raw JSON response as a string.

            Uri.Builder buildURL = new Uri.Builder();
            SharedPreferences preferences = PreferenceManager.
                    getDefaultSharedPreferences(getContext());

            String sortingChoice = preferences.getString(getString(R.string.pref_sort_key),
                    SORT_BY_POPULARITY );

            buildURL.scheme("http").authority("api.themoviedb.org").appendPath("3")
                    .appendPath("discover").appendPath("movie")
                    .appendQueryParameter(QUERY_PAGE, params[0])
                    .appendQueryParameter(QUERY_API_KEY,API_KEY );

            if (sortingChoice.equals(SORT_BY_POPULARITY)){
                buildURL.appendQueryParameter(QUERY_SORTBY, "popularity.desc");
            }
            else{
                buildURL.appendQueryParameter(QUERY_SORTBY, "vote_average.desc");
            }

            try {
                URL url = new URL(buildURL.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                String mMovieDBStr = buffer.toString();
                return extractMovieDetails(mMovieDBStr);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Returns list of url paths for poster thumbnails and extracts movie details for
         * displaying in detail view.
         */
        private String[]  extractMovieDetails(String jsonMovieStr) throws JSONException {

            final String TMDB_RESULT = "results";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_BASE_URL_POSTER = "http://image.tmdb.org/t/p/w185/";
            final String TMDB_BASE_URL_BACKDROP = "http://image.tmdb.org/t/p/w500/";

            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_TITLE = "original_title";
            final String TMDB_RATINGS = "vote_average";
            final String TMDB_BACKDROP_PATH = "backdrop_path";



            JSONObject movieDataJsonObj = new JSONObject(jsonMovieStr);
            JSONArray resultsArray = movieDataJsonObj.getJSONArray(TMDB_RESULT);

            int noOfMovies = resultsArray.length();
            String[] posterPaths = new String[noOfMovies];
            JSONObject movieData;
            String path,date,plot,title,ratings,backdrop;
            for(int i = 0; i < noOfMovies; i++) {
                movieData = resultsArray.getJSONObject(i);
                path = movieData.getString(TMDB_POSTER);

                if( path != null ){
                    date = movieData.getString(TMDB_RELEASE_DATE);
                    plot = movieData.getString(TMDB_OVERVIEW);
                    title = movieData.getString(TMDB_TITLE);
                    ratings = movieData.getString(TMDB_RATINGS);
                    backdrop = TMDB_BASE_URL_BACKDROP + movieData.getString(TMDB_BACKDROP_PATH);

                    posterPaths[i] = TMDB_BASE_URL_POSTER + path;
                    movieDetailList.add(new MovieDetail(title, posterPaths[i], plot, ratings,
                            date, backdrop));
                }
            }
            return posterPaths;
        }
    }
}
