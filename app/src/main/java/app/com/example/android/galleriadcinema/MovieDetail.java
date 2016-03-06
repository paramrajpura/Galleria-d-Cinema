/*
 * Copyright (C) 2016 The Android Open Source Project
 */

package app.com.example.android.galleriadcinema;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 01-Mar-16.
 */

/**
 * Contains movie details for displaying in Detail Fragment.
 */
public class MovieDetail implements Parcelable {
    String originalTitle;
    String posterURL;
    String overview;
    String userRating;
    String releaseDate;
    String backdropPath;

    public MovieDetail(String title, String URL, String plot, String ratings,String date,
                       String backdrop)
    {
        this.originalTitle = title;
        this.posterURL = URL;
        this.overview = plot;
        this.userRating = ratings;
        this.releaseDate = date;
        this.backdropPath = backdrop;

    }

    protected MovieDetail(Parcel in) {
        originalTitle = in.readString();
        posterURL = in.readString();
        overview = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        backdropPath = in.readString();
    }

    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

    public String toString() {
        return originalTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterURL);
        dest.writeString(overview);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeString(backdropPath);
    }
}
