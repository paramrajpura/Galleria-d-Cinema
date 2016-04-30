package app.com.example.android.galleriadcinema.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import app.com.example.android.galleriadcinema.ImageAdapter;
import app.com.example.android.galleriadcinema.R;
import app.com.example.android.galleriadcinema.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrailerActivityFragment extends Fragment {

    ShareActionProvider mShareActionProvider;

    String mShareTrailerStr = "Trailers: ";
    public TrailerActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trailer, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareTrailerStr != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mShareTrailerStr);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);

        getActivity().setTitle(R.string.title_activity_trailer);
        ImageAdapter mImgAdapter = new ImageAdapter(getContext());
        final String TRAILER_KEY_TAG = "TrailerKeys";
        Intent intent = getActivity().getIntent();
        boolean isConnected = Utility.checkInternetConnection(getContext());
        if (intent != null && intent.hasExtra(TRAILER_KEY_TAG) && isConnected) {
            final String[] trailerData = intent.getStringArrayExtra(TRAILER_KEY_TAG);
            GridView trailerView = (GridView) rootView.findViewById(R.id.trailerGridView);

            int savedListSize = trailerData.length;
            String[] trailerThumbPaths = new String[savedListSize];

            final String BASE_THUMB_URL = "http://img.youtube.com/vi";
            final String BASE_VIDEO_URL = "https://www.youtube.com/watch?v=";

            for(int itr = 0;itr < savedListSize;itr++){
                trailerThumbPaths[itr] = BASE_THUMB_URL + "/" + trailerData[itr]
                        +"/default.jpg";
                mShareTrailerStr = mShareTrailerStr + BASE_VIDEO_URL + trailerData[itr] + "\n";
            }
            mImgAdapter.setmThumbUrls(trailerThumbPaths);
            trailerView.setAdapter(mImgAdapter);

            trailerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(BASE_VIDEO_URL + trailerData[position])));
                }
            });
        }

        else {
            displayNoConnectionMessage();
        }

        return rootView;
    }

    protected  void displayNoConnectionMessage(){
        String NO_CONNECTION = "You are using Offline Mode";
        Toast.makeText(getContext(),NO_CONNECTION , Toast.LENGTH_SHORT).show();
    }


}
