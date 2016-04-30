/*
 * Copyright (C) 2016 The Android Open Source Project
 */

package app.com.example.android.galleriadcinema.Fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import app.com.example.android.galleriadcinema.Activities.ReviewActivity;
import app.com.example.android.galleriadcinema.Activities.TrailerActivity;
import app.com.example.android.galleriadcinema.CustomTarget;
import app.com.example.android.galleriadcinema.MovieColumns;
import app.com.example.android.galleriadcinema.MovieDetail;
import app.com.example.android.galleriadcinema.MovieProvider;
import app.com.example.android.galleriadcinema.R;

/**
 * A placeholder fragment containing a scroll view for displaying movie details.
 */
public class MovieDetailActivityFragment extends Fragment {
    MovieDetail mMovieData = null;
    final String externalStoragePath = "file://" +
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    FloatingActionButton favButton;
    public MovieDetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String INTENT_PARCEL_MOVIE_DETAILS = "MovieData";
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);


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
                    Intent trailerList = new Intent(getContext(),TrailerActivity.class)
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
                    Intent loadReviews = new Intent(getContext(),ReviewActivity.class)
                            .putExtra(MOVIE_ID, finalMMovieData1.movieId);
                    startActivity(loadReviews);
                }
            });

            favButton= (FloatingActionButton) rootView.findViewById(R.id.favoriteFButton);
            Cursor cCheckFav = getActivity().getContentResolver().query(
                    MovieProvider.Movies.withId(Integer.parseInt(mMovieData.movieId)),
                    null, null,null,null);
            if(cCheckFav!=null){
                if(cCheckFav.getCount()!=0){
                    favButton.setImageResource(android.R.drawable.star_big_on);
                }
                cCheckFav.close();
            }


            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE",
                                "android.permission.READ_EXTERNAL_STORAGE"};
                        if (ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                !=PackageManager.PERMISSION_GRANTED){
                            int permsRequestCode = 200;
                            requestPermissions(perms, permsRequestCode);
                        }
                        else{
                            saveData();
                        }
                    }
                    else {saveData();}

                }
            });

        }
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    saveData();
                }
                break;

        }

    }

    private void saveData() {
        String posterPath = "p"+ mMovieData.movieId;
        String backdropPath = "b"+ mMovieData.movieId;

        Target posterTarget = new CustomTarget(posterPath);
        Target backdropTarget = new CustomTarget(backdropPath);
        Picasso.with(getContext()).load(mMovieData.posterURL).
                into(posterTarget);
        Picasso.with(getContext()).load(mMovieData.backdropPath).
                into(backdropTarget);

        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.MOVIE_ID, mMovieData.movieId);
        cv.put(MovieColumns.MOVIE_NAME, mMovieData.originalTitle);
        cv.put(MovieColumns.OVERVIEW, mMovieData.overview);
        cv.put(MovieColumns.POSTER_PATH,externalStoragePath+posterPath+".jpg");
        cv.put(MovieColumns.THUMB_PATH,externalStoragePath+backdropPath+".jpg");
        cv.put(MovieColumns.RELEASE_DATE, mMovieData.releaseDate);
        cv.put(MovieColumns.USER_RATINGS, mMovieData.userRating);


        Cursor c = getActivity().getContentResolver().query(
                MovieProvider.Movies.withId(Integer.parseInt(mMovieData.movieId)),
                null, null,null,null);
        if(c!=null){
            if (c.getCount() == 0) {
                getActivity().getContentResolver().insert(MovieProvider.Movies.CONTENT_URI
                        , cv);
                c.close();
                favButton.setImageResource(android.R.drawable.star_big_on);
                Toast.makeText(getContext(),"Added as favorite",Toast.LENGTH_SHORT).show();
            }
            else{
                getActivity().getContentResolver().delete(MovieProvider.Movies.withId(
                        Integer.parseInt(mMovieData.movieId)),null,null);
                favButton.setImageResource(android.R.drawable.star_big_off);
                Toast.makeText(getContext(),"Removed from favorites",Toast.LENGTH_SHORT).show();
            }
        }


    }
}
