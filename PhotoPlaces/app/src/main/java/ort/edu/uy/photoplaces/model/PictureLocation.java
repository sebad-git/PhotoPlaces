package ort.edu.uy.photoplaces.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import java.io.File;

@Entity(tableName = "PICTURE_LOCATIONS",primaryKeys = {"LATITUDE","LONGITUDE"})

public class PictureLocation {

    @ColumnInfo(name = "NAME" )
    public String name;
    @ColumnInfo(name = "LATITUDE")
    public double latitude;
    @ColumnInfo(name = "LONGITUDE")
    public double logitude;
    @ColumnInfo(name = "DESCRIPTION")
    public String description;
    @ColumnInfo(name = "PICTURE_PATH")
    public String picturePath;

    public boolean pictureExists(){
        return ( picturePath !=null && !picturePath.isEmpty() && new File(picturePath).exists());
    }

    @Override
    public String toString() { return String.format("Latitude:[%s] Longitude:[%s]", logitude, latitude); }
}
