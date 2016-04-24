package app.com.example.android.galleriadcinema;//package com.sam_chordas.android.schematicplanets.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;



@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {
    private MovieDatabase(){}

    public static final int VERSION = 1;


        @Table(MovieColumns.class) public static final String FAVS = "favs";

}