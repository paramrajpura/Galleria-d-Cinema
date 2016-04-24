package app.com.example.android.galleriadcinema;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID ="_id";

    @DataType(DataType.Type.INTEGER) @NotNull @Unique
    public static final String MOVIE_ID = "movie_id";
    
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_NAME = "movie_name";
    
    @DataType(DataType.Type.TEXT) @NotNull 
    public static final String OVERVIEW ="overview";

    @DataType(DataType.Type.TEXT) @NotNull 
    public static final String POSTER_PATH ="poster_path";

    @DataType(DataType.Type.TEXT) @NotNull 
    public static final String THUMB_PATH ="thumb_path";

    @DataType(DataType.Type.TEXT) @NotNull 
    public static final String RELEASE_DATE ="release_date";

    @DataType(DataType.Type.TEXT) @NotNull 
    public static final String USER_RATINGS ="ratings";
}