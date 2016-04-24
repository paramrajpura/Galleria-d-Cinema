package app.com.example.android.galleriadcinema;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 09-Apr-16.
 */
public class FetchReviews extends AsyncTask<Object, Void, String[]> {
    final String LOG_TAG = "FetchReviews";
    reviewActivityFragment reviewFragment;

    @Override
    protected void onPostExecute(String[] strings) {
        reviewFragment.setListData(strings);
        super.onPostExecute(strings);
    }


    @Override
    protected String[] doInBackground(Object... params) {

        reviewFragment = (reviewActivityFragment) params[2];
        final String QUERY_PAGE = "page";
        final String QUERY_API_KEY = "api_key";
        final String API_KEY = Utility.API_KEY;
        Uri.Builder buildURL = new Uri.Builder();

        buildURL.scheme("http").authority("api.themoviedb.org").appendPath("3").appendPath("movie");

        buildURL.appendPath((String) params[1]).appendPath("reviews")
                .appendQueryParameter(QUERY_PAGE, String.valueOf(params[0]))
                .appendQueryParameter(QUERY_API_KEY, API_KEY);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
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
            return FormatDataForReviews(buffer.toString());
        }catch (JSONException | IOException e) {
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

    private String[] FormatDataForReviews(String jsonReviews) throws JSONException {
        final String TMDB_RESULT = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        JSONObject reviewDataJsonObj = new JSONObject(jsonReviews);
        JSONArray resultsArray = reviewDataJsonObj.getJSONArray(TMDB_RESULT);

        int noOfReviews = resultsArray.length();
        String[] reviewList = new String[noOfReviews];


        JSONObject reviewData;
        String reviewString,reviewAuthor;
        for (int i = 0; i < noOfReviews; i++) {
            reviewData = resultsArray.getJSONObject(i);

            reviewAuthor = reviewData.getString(REVIEW_AUTHOR);
            reviewString = reviewData.getString(REVIEW_CONTENT);

            if (reviewString != null) {
                reviewList[i] = "Author:" + reviewAuthor + "\n\n" + reviewString + "\n";
            }
        }
        return reviewList;
    }

}
