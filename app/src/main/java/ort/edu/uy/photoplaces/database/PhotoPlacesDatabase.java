package ort.edu.uy.photoplaces.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ort.edu.uy.photoplaces.model.PictureLocation;
import ort.edu.uy.photoplaces.database.dao.LocationDao;

@Database(entities = {PictureLocation.class}, version = 1, exportSchema = false )
public abstract class PhotoPlacesDatabase extends RoomDatabase {

    private static PhotoPlacesDatabase database;

    public static PhotoPlacesDatabase getDatabase(Context context){
        if(database==null){
            database=Room.databaseBuilder(context,PhotoPlacesDatabase.class, "PhotoPlaces.db")
                    .allowMainThreadQueries().build();
        }
        return database;
    }

    public abstract LocationDao locationDao();

}
