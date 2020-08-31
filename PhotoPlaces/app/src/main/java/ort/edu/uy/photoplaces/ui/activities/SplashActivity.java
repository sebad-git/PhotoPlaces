package ort.edu.uy.photoplaces.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ort.edu.uy.photoplaces.R;
import ort.edu.uy.photoplaces.ui.dialogs.Alert;

public class SplashActivity extends AppCompatActivity {

    //Permissions Codes.
    public static final int REQUEST_APP_PERMISSIONS = 1;
    private View logo;

    //Permissions.
    private static final String[] permissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.logo = this.findViewById(R.id.logo_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SplashActivity activity = this;
        Animation animacionSplash = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        animacionSplash.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                if(!activity.hasPermissions(permissions)){ activity.askPermissions(permissions, REQUEST_APP_PERMISSIONS);}
                else{ activity.startApplication(); }
            }
            @Override public void onAnimationRepeat(Animation animation){}});
        this.logo.startAnimation(animacionSplash);

    }

    public boolean hasPermissions(String[] permisionArray){
        boolean validatedPermissions = true;
        for ( String permission : permisionArray) { if(!this.hasPermission(permission)){ validatedPermissions = false; } }
        return validatedPermissions;
    }
    public boolean hasPermission(String permision) {
        if (Build.VERSION.SDK_INT < 23) { return true; }
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permision);
    }

    public void askPermissions(String[] permisos, int requestCode) {
        if(!this.hasPermissions(permisos)){
            ActivityCompat.requestPermissions(this, permisos, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permisos[], int[] permisosConcedidos) {
        if(requestCode == REQUEST_APP_PERMISSIONS){
            if (this.hasPermissions(permisos)) {
                this.startApplication();
            }else{
                new Alert(this,"Permisos","La Aplicacion no puede continuar si no acepta los permisos",
                        android.R.drawable.ic_menu_close_clear_cancel) {
                    @Override
                    public void onConfim() {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                };
            }
        }
    }

    private void startApplication(){
        Intent intent = new Intent(SplashActivity.this, MapActivity.class);
        SplashActivity.this.startActivity(intent);

    }
}
