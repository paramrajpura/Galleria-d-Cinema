package app.com.example.android.galleriadcinema;


public class Movie{
    public int movieId;
    public String name;
    public String overview;
    public String posterPath;
    public String thumbPath;
    public String releaseDate;
    public String ratings;

    public Movie(){}

    public Movie(int movieId, String name, String overview,String posterPath, String thumbPath, String releaseDate, String ratings){
        this.movieId = movieId;
        this.name = name;
        this.overview = overview;
        this.posterPath = posterPath;
        this.thumbPath = thumbPath;
        this.releaseDate = releaseDate;
        this.ratings = ratings;
    }
    public String getName(){
        return name;
    }

    public int getMovieId(){
        return movieId;
    }

    public String getOverview(){
        return overview;
    }

    public String getPosterPath(){
        return posterPath;
    }
    public String getThumbPath(){
        return thumbPath;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
    public String getRatings(){
        return ratings;
    }
}