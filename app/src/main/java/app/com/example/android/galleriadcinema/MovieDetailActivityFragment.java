/*
 * Copyright (C) 2016 The Android Open Source Project
 */

package app.com.example.android.galleriadcinema;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a scroll view for displaying movie details.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String INTENT_PARCEL_MOVIE_DETAILS = "MovieData";
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(INTENT_PARCEL_MOVIE_DETAILS)) {
            MovieDetail mMovieData = intent.getParcelableExtra(INTENT_PARCEL_MOVIE_DETAILS);
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
        }
        return rootView;
    }
}
