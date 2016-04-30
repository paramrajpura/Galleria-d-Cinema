package app.com.example.android.galleriadcinema;

/**
 * Created by Admin on 10-Mar-16.
 */

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import app.com.example.android.galleriadcinema.Fragments.MainActivityFragment;

/**
 * Async Task for retrieving movie details from TMDB.
 */
public class FetchMovieDB extends AsyncTask<Object, Void, String[]> {


    MainActivityFragment mainFragment;
    private  WeakReference<MainActivityFragment> mMainFragmentReference;
    private final String QUERY_API_KEY = "api_key";
    private final String API_KEY = Utility.API_KEY;
    private final String LOG_TAG = "FetchDB";
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_NAME = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_POSTERPATH = 4;
    static final int COL_THUMBPATH = 5;
    static final int COL_RELEASEDATE = 6;
    static final int COL_RATINGS = 7;
    @Override
    protected void onPostExecute(String[] strings) {
        if(strings!=null){
            mainFragment.mImgAdapter.setmThumbUrls(strings);
        }
        mainFragment.mImgAdapter.notifyDataSetChanged();
        super.onPostExecute(strings);
    }

    @Override
    protected String[] doInBackground(Object... params) {

        //mainFragment = (MainActivityFragment) params[1];
        mMainFragmentReference = new WeakReference<>((MainActivityFragment) params[1]);
        mainFragment = mMainFragmentReference.get();
        final String SORT_BY_POPULARITY = "1";
        final String FAVORITES = "3";
        final String QUERY_PAGE = "page";

        if(mainFragment.getContext()!=null) {
            Uri.Builder buildURL = new Uri.Builder();
            SharedPreferences preferences = PreferenceManager.
                    getDefaultSharedPreferences(mainFragment.getContext());


            String sortingChoice = preferences.getString(mainFragment.getString(R.string.pref_sort_key),
                    SORT_BY_POPULARITY);
            if (sortingChoice.equals(FAVORITES)) {
                Cursor c = mainFragment.getActivity().getContentResolver().query
                        (MovieProvider.Movies.CONTENT_URI, null, null, null, null);
                int totalMovies = 0;
                if (c != null) {
                    totalMovies = c.getCount();
                }
                String[] posterPaths = new String[totalMovies];
                if (c != null && c.moveToFirst()) {

                    String date, plot, title, ratings, backdrop, movie_id;
                    for (int i = 0; i < totalMovies; i++) {
                        date = c.getString(COL_RELEASEDATE);
                        plot = c.getString(COL_OVERVIEW);
                        title = c.getString(COL_MOVIE_NAME);
                        ratings = c.getString(COL_RATINGS);
                        movie_id = c.getString(COL_MOVIE_ID);
                        backdrop = c.getString(COL_THUMBPATH);
                        posterPaths[i] = c.getString(COL_POSTERPATH);
                        try {
                            mainFragment.movieDetailList.add(new MovieDetail(title, posterPaths[i], plot,
                                    ratings, date, backdrop, movie_id, extractTrailerKeys(movie_id)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        c.moveToNext();
                    }
                    c.close();
                }
                return posterPaths;
            } else {
                if (Utility.checkInternetConnection(mainFragment.getContext())) {
                    buildURL.scheme("http").authority("api.themoviedb.org").appendPath("3").appendPath("movie");


                    if (sortingChoice.equals(SORT_BY_POPULARITY)) {
                        buildURL.appendPath("popular");
                    } else {
                        buildURL.appendPath("top_rated");
                    }

                    buildURL.appendQueryParameter(QUERY_PAGE, (String) params[0])
                            .appendQueryParameter(QUERY_API_KEY, API_KEY);
                    try {
                        return extractMovieDetails(urlConnectFetchData(buildURL.build().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns list of url paths for poster thumbnails and extracts movie details for
     * displaying in detail view.
     */
    private String[] extractMovieDetails(String jsonMovieStr) throws JSONException {

        final String TMDB_RESULT = "results";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_BASE_URL_POSTER = "http://image.tmdb.org/t/p/w185/";
        final String TMDB_BASE_URL_BACKDROP = "http://image.tmdb.org/t/p/w500/";

        final String TMDB_MOVIE_ID = "id";
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
        String path, date, plot, title, ratings, backdrop, movie_id;
        for (int i = 0; i < noOfMovies; i++) {
            movieData = resultsArray.getJSONObject(i);
            path = movieData.getString(TMDB_POSTER);

            if (path != null) {
                date = movieData.getString(TMDB_RELEASE_DATE);
                plot = movieData.getString(TMDB_OVERVIEW);
                title = movieData.getString(TMDB_TITLE);
                ratings = movieData.getString(TMDB_RATINGS);
                movie_id = movieData.getString(TMDB_MOVIE_ID);
                backdrop = TMDB_BASE_URL_BACKDROP + movieData.getString(TMDB_BACKDROP_PATH);

                posterPaths[i] = TMDB_BASE_URL_POSTER + path;
                mainFragment.movieDetailList.add(new MovieDetail(title, posterPaths[i], plot,
                        ratings, date, backdrop, movie_id,extractTrailerKeys(movie_id)));
            }
        }
        return posterPaths;
    }

    private String[] extractTrailerKeys(String movieId) throws JSONException {
        boolean isConnected = Utility.checkInternetConnection(mainFragment.getContext());
        if(isConnected){
            Uri.Builder buildURL = new Uri.Builder();
            buildURL.scheme("http").authority("api.themoviedb.org").appendPath("3").appendPath("movie")
                    .appendPath(movieId).appendPath("videos")
                    .appendQueryParameter(QUERY_API_KEY, API_KEY);
            String jsonTrailerData = urlConnectFetchData(buildURL.build().toString());
            final String TMDB_RESULT = "results";
            final String TMDB_VIDEO_KEY = "key";


            JSONObject trailerDataJsonObj = new JSONObject(jsonTrailerData);
            JSONArray resultsArray = trailerDataJsonObj.getJSONArray(TMDB_RESULT);

            int noOfTrailers = resultsArray.length();
            String[] trailerKeys = new String[noOfTrailers];
            JSONObject trailerData;
            String videoKey;
            for (int i = 0; i < noOfTrailers; i++) {
                trailerData = resultsArray.getJSONObject(i);
                videoKey = trailerData.getString(TMDB_VIDEO_KEY);

                if (videoKey != null) {
                    trailerKeys[i] = videoKey;
                }
            }
            return trailerKeys;
        }
        else {
            return new String[0];
        }
    }

    String urlConnectFetchData(String builtUrl){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(builtUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
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
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}