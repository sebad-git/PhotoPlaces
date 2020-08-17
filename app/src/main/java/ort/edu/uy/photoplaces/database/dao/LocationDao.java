package ort.edu.uy.photoplaces.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ort.edu.uy.photoplaces.model.PictureLocation;


@Dao
public interface LocationDao {

    @Query("SELECT * FROM PICTURE_LOCATIONS")
    List<PictureLocation> list();

    @Query("SELECT * FROM PICTURE_LOCATIONS WHERE LATITUDE = :latitude AND LONGITUDE = :longitude")
    PictureLocation buscar(double latitude, double longitude);

    @Insert
    void save(PictureLocation location);

    @Update
    void update(PictureLocation location);

    @Delete
    void delete(PictureLocation location);
}
