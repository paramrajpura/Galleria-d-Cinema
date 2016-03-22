package app.com.example.android.galleriadcinema;

/**
 * Created by Admin on 10-Mar-16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Async Task for retrieving movie details from TMDB.
 */
public class FetchMovieDB extends AsyncTask<Object, Void, String[]> {


    MainActivityFragment mainFragment ;
    @Override
    protected void onPostExecute(String[] strings) {
        mainFragment.mImgAdapter.setmThumbUrls(strings);
        mainFragment.mImgAdapter.notifyDataSetChanged();
        super.onPostExecute(strings);
    }

    @Override
    protected String[] doInBackground(Object... params) {

        mainFragment = (MainActivityFragment)params[1];

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
                getDefaultSharedPreferences(mainFragment.getContext());


        String sortingChoice = preferences.getString(mainFragment.getString(R.string.pref_sort_key),
                SORT_BY_POPULARITY );

        buildURL.scheme("http").authority("api.themoviedb.org").appendPath("3")
                .appendPath("discover").appendPath("movie")
                .appendQueryParameter(QUERY_PAGE, (String) params[0])
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
            String mMovieDBStr = buffer.toString();
            return extractMovieDetails(mMovieDBStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                mainFragment.movieDetailList.add(new MovieDetail(title, posterPaths[i], plot, ratings,
                        date, backdrop));
            }
        }
        return posterPaths;
    }
}