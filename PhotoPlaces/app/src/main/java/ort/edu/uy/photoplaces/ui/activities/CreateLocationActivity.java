package ort.edu.uy.photoplaces.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import ort.edu.uy.photoplaces.BuildConfig;
import ort.edu.uy.photoplaces.R;
import ort.edu.uy.photoplaces.database.PhotoPlacesDatabase;
import ort.edu.uy.photoplaces.model.PictureLocation;
import ort.edu.uy.photoplaces.util.BitmapUtil;

public class CreateLocationActivity extends AppCompatActivity {
    private View container;
    private EditText txtName;
    private EditText txtDescription;
    private ImageView imgPicture;
    private ImageView saveButton;
    private ImageView cancelButton;
    private double pp_latitude;
    private double pp_longitude;
    private String pp_photoPath;
    private boolean editing;
    private boolean isNewPicture;

    private static final int CAMERA_REQUEST = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);
        this.container = findViewById(R.id.cp_container);
        this.txtName = this.findViewById(R.id.cp_name);
        this.saveButton = this.findViewById(R.id.cp_btn_save);
        this.cancelButton = this.findViewById(R.id.cu_btn_cancel);
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { saveLocation(); onBackPressed(); }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onBackPressed(); }
        });
        this.imgPicture = this.findViewById(R.id.cp_picture);
        this.imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { takePicure(); }
        });
        this.txtDescription = this.findViewById(R.id.cu_descripcion);
        this.isNewPicture = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
        this.container.startAnimation(animation);
        if(this.isNewPicture){this.isNewPicture =false; return; }
        try {
            Intent parameters = getIntent();
            this.pp_latitude = parameters.getDoubleExtra(MapActivity.PARAM_LATITUDE,-1d);
            this.pp_longitude = parameters.getDoubleExtra(MapActivity.PARAM_LONGITUDE,-1d);
            this.editing = parameters.getBooleanExtra(MapActivity.PARAM_EDIT,false);
            if(this.editing){
                //Cargar UbicacionMapa.
                PictureLocation pictureLocation = PhotoPlacesDatabase.getDatabase(this)
                        .locationDao().buscar(this.pp_latitude,this.pp_longitude);
                if(pictureLocation!=null){
                    this.txtName.setText(pictureLocation.name);
                    this.txtDescription.setText(pictureLocation.description);
                    this.pp_photoPath =pictureLocation.picturePath;
                    if(pictureLocation.pictureExists()){ this.displayPicture(); }
                }
            }
        }catch (Exception e){ e.printStackTrace(); }
    }

    private void takePicure() {
        File pictureFile = null;
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                final String fecha = new SimpleDateFormat(getString(R.string.file_date_pattern)).format(new Date());
                final String fileName = String.format(getString(R.string.file_name), fecha);
                final File picturesDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                pictureFile = new File(picturesDirectory,fileName);
                pictureFile.createNewFile();
                pp_photoPath = pictureFile.getAbsolutePath();
                Uri pictureUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", pictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                this.isNewPicture =true;
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
            else{ if(pictureFile!=null){ pictureFile.delete(); } }
        }
        catch (Exception e) {
            if(pictureFile!=null){ pictureFile.delete(); }
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST) {
                if(resultCode == RESULT_OK){ this.displayPicture(); }
                else{ if(pp_photoPath !=null){ new File(pp_photoPath).delete(); }; }
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
            e.printStackTrace(); Log.e(getClass().getName(),e.getMessage());
        }
    }

    private void displayPicture(){
        try {
            Bitmap thumbnail = BitmapUtil.createMiniature(this, pp_photoPath,
                    getResources().getInteger(R.integer.miniature_size));
            this.imgPicture.setImageBitmap(thumbnail);
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
            e.printStackTrace(); Log.e(getClass().getName(),e.getMessage());
        }
    }

    private void saveLocation(){
        try {
            final PictureLocation pictureLocation = new PictureLocation();
            pictureLocation.logitude = pp_longitude;
            pictureLocation.latitude = pp_latitude;
            pictureLocation.name = txtName.getText().toString();
            pictureLocation.description = txtDescription.getText().toString();
            pictureLocation.picturePath = pp_photoPath;
            if(editing){ PhotoPlacesDatabase.getDatabase(this).locationDao().update(pictureLocation); }
            else{ PhotoPlacesDatabase.getDatabase(this).locationDao().save(pictureLocation); }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,getString(R.string.error_saving),Toast.LENGTH_SHORT).show();
        }
    }

}
