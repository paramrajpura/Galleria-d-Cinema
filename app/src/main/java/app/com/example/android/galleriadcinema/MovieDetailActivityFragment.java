/*
 * Copyright (C) 2016 The Android Open Source Project
 */

package app.com.example.android.galleriadcinema;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A placeholder fragment containing a scroll view for displaying movie details.
 */
public class MovieDetailActivityFragment extends Fragment {

    final String externalStoragePath = "file://" +
            Environment.getExternalStorageDirectory().getPath() + "/";

    public MovieDetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String INTENT_PARCEL_MOVIE_DETAILS = "MovieData";
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        MovieDetail mMovieData = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieData = arguments.getParcelable(INTENT_PARCEL_MOVIE_DETAILS);
        }

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(INTENT_PARCEL_MOVIE_DETAILS)) {
            mMovieData = intent.getParcelableExtra(INTENT_PARCEL_MOVIE_DETAILS);
        }

        if(mMovieData!=null){
            getActivity().setTitle(mMovieData.originalTitle);
            ImageView posterView = (ImageView) rootView.findViewById(R.id.imageView);
            ((TextView) rootView.findViewById(R.id.titleTextView))
                    .setText(mMovieData.originalTitle);
            ((TextView) rootView.findViewById(R.id.plotTextView))
                    .setText(mMovieData.overview);
            ((TextView) rootView.findViewById(R.id.ratingsTextView))
                    .setText(mMovieData.userRating);
            ((TextView) rootView.findViewById(R.id.releaseDateTextView))
                    .setText(mMovieData.releaseDate);
            Picasso.with(getContext()).load(mMovieData.backdropPath).into(posterView);

            final String TRAILER_KEY_TAG = "TrailerKeys";
            Button trailerButton = (Button)rootView.findViewById(R.id.trailerButton);
            final MovieDetail finalMMovieData = mMovieData;
            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent trailerList = new Intent(getContext(),trailerActivity.class)
                            .putExtra(TRAILER_KEY_TAG, finalMMovieData.trailerKeys);
                    startActivity(trailerList);
                }
            });

            final String MOVIE_ID = "MovieId";
            Button reviewButton = (Button) rootView.findViewById(R.id.reviewButton);
            final MovieDetail finalMMovieData1 = mMovieData;
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loadReviews = new Intent(getContext(),reviewActivity.class)
                            .putExtra(MOVIE_ID, finalMMovieData1.movieId);
                    startActivity(loadReviews);
                }
            });

            FloatingActionButton favButton=
                    (FloatingActionButton) rootView.findViewById(R.id.favoriteFButton);
            final MovieDetail finalMMovieData2 = mMovieData;
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String posterPath = "p"+ finalMMovieData2.movieId;
                    String backdropPath = "b"+ finalMMovieData2.movieId;

                    Target posterTarget = new CustomTarget(posterPath);
                    Target backdropTarget = new CustomTarget(backdropPath);


                    Picasso.with(getContext()).load(finalMMovieData2.posterURL).into(posterTarget);
                    Picasso.with(getContext()).load(finalMMovieData2.backdropPath).into(backdropTarget);


                    ContentValues cv = new ContentValues();
                    cv.put(MovieColumns.MOVIE_ID, finalMMovieData2.movieId);
                    cv.put(MovieColumns.MOVIE_NAME, finalMMovieData2.originalTitle);
                    cv.put(MovieColumns.OVERVIEW, finalMMovieData2.overview);
                    cv.put(MovieColumns.POSTER_PATH,externalStoragePath+posterPath+".jpg");
                    cv.put(MovieColumns.THUMB_PATH,externalStoragePath+backdropPath+".jpg");
                    cv.put(MovieColumns.RELEASE_DATE, finalMMovieData2.releaseDate);
                    cv.put(MovieColumns.USER_RATINGS, finalMMovieData2.userRating);


                    Cursor c = getActivity().getContentResolver().query(
                            MovieProvider.Movies.withId(Integer.parseInt(finalMMovieData2.movieId)),
                            null, null,null,null);

                    if (c != null && c.getCount() == 0) {
                        getActivity().getContentResolver().insert(MovieProvider.Movies.CONTENT_URI
                                , cv);
                    }
                    if (c != null) {
                        c.close();
                    }

                    Toast.makeText(getContext(),"Added as favorite",Toast.LENGTH_SHORT).show();

                }
            });

        }
        return rootView;
    }
}
