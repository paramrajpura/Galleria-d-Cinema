package app.com.example.android.galleriadcinema;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class reviewActivityFragment extends Fragment {

    private int pageNumber = 1;
    private ArrayAdapter mListAdapter;
    private boolean mNextPage=true;
    private List<String> reviewList;

    public reviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_activity_review);
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        final String MOVIE_ID = "MovieId";
        Intent intent = getActivity().getIntent();
        final String movieId = intent.getStringExtra(MOVIE_ID);


        reviewList = new ArrayList<String>(Arrays.asList(new String[0]));


        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mListAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_review, // The name of the layout ID.
                        R.id.reviewTextView, // The ID of the textview to populate.
                        reviewList);
        if(Utility.checkInternetConnection(getContext())){
            try {
                updateReviewData(pageNumber,movieId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            displayNoConnectionMessage();
        }



        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.reviewListView);
        listView.setAdapter(mListAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                                                 boolean isConnected = Utility.
                                                         checkInternetConnection(getContext());
                                                 if (mNextPage && isConnected) {
                                                     pageNumber++;
                                                     mNextPage = false;
                                                     try {
                                                         updateReviewData(pageNumber,movieId);
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

    public void setListData(String[] data){
        List < String > toAdd = Arrays.asList(data);
        reviewList.addAll(toAdd);
        mListAdapter.notifyDataSetChanged();
    }

    protected void updateReviewData(int pageNumber, String movieId) throws ExecutionException, InterruptedException {
        FetchReviews getReviews = new FetchReviews();
        getReviews.execute(pageNumber,movieId,this);
    }

    protected  void displayNoConnectionMessage(){
        String NO_CONNECTION = "You are using Offline Mode";
        Toast.makeText(getContext(),NO_CONNECTION , Toast.LENGTH_SHORT).show();
    }

}
