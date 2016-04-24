package app.com.example.android.galleriadcinema;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {
    public static final String AUTHORITY =
            "app.com.example.android.galleriadcinema.MovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FAVS= "favs";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = MovieDatabase.FAVS) public static class Movies{
        @ContentUri(
                path = Path.FAVS,
                type = "vnd.android.cursor.dir/favs",
                defaultSort = MovieColumns.MOVIE_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVS);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.FAVS + "/#",
                type = "vnd.android.cursor.item/favs",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(int id){
            return buildUri(Path.FAVS, String.valueOf(id));
        }
    }
}