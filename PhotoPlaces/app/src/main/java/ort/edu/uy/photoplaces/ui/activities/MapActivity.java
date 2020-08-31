package ort.edu.uy.photoplaces.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ort.edu.uy.photoplaces.R;
import ort.edu.uy.photoplaces.model.PictureLocation;
import ort.edu.uy.photoplaces.database.PhotoPlacesDatabase;
import ort.edu.uy.photoplaces.util.BitmapUtil;
import ort.edu.uy.photoplaces.ui.dialogs.Question;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_EDIT = "edit";

    private boolean mapReady;
    private GoogleMap map;
    private View progressBar;
    private FloatingActionButton btnCurrentPosition;
    private FloatingActionButton btnNextMarker;
    private FloatingActionButton btnPreviousMarker;
    private Marker currentMapMarker;
    private List<PictureLocation> pictureLocations;
    private int locationIndex;
    private Location currentGPSLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.progressBar = findViewById(R.id.barra_progreso);
        this.mapReady = false;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
        this.btnCurrentPosition = this.findViewById(R.id.map_btn_current_location);
        this.btnNextMarker = this.findViewById(R.id.map_btn_next_marker);
        this.btnPreviousMarker = this.findViewById(R.id.map_btn_previus_location);
        this.btnCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCurrentLocation();
            }
        });
        this.btnNextMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextLocation();
            }
        });
        this.btnPreviousMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPreviousLocation();
            }
        });
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    getResources().getInteger(R.integer.gps_distance),
                    getResources().getInteger(R.integer.gps_latency), this);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    getResources().getInteger(R.integer.gps_distance),
                    getResources().getInteger(R.integer.gps_latency), this);
        }
    }

    private void displayCurrentLocation(){
        if(mapReady){
            try {
                if(currentMapMarker !=null){ currentMapMarker.remove(); currentMapMarker =null; }
                if(currentGPSLocation!=null){
                    LatLng coords = new LatLng(currentGPSLocation.getLatitude(),currentGPSLocation.getLongitude());
                    try {
                        BitmapDescriptor icon = BitmapUtil.createIcon(this,R.drawable.ic_location);
                        currentMapMarker = map.addMarker(new MarkerOptions()
                                .position(coords).title(getString(R.string.current_location)).icon(icon));
                    }catch (Exception e){ e.printStackTrace(); }
                    map.moveCamera(CameraUpdateFactory.newLatLng(coords));
                    map.animateCamera(CameraUpdateFactory.zoomTo(this.getResources().getInteger(R.integer.map_zoom)));
                }
            }catch (Exception e){ e.printStackTrace(); }
        }
    }

    private void displayNextLocation(){
        if(mapReady){
            try {
                if(pictureLocations !=null && pictureLocations.size()>0){
                    if((locationIndex +1)< pictureLocations.size()){
                        locationIndex++; }
                    else{ locationIndex =0; }
                    PictureLocation pictureLocation = pictureLocations.get(locationIndex);
                    LatLng coords = new LatLng(pictureLocation.latitude,pictureLocation.logitude);
                    map.moveCamera(CameraUpdateFactory.newLatLng(coords));
                    map.animateCamera(CameraUpdateFactory.zoomTo(this.getResources().getInteger(R.integer.marker_zoom)));
                }
            }catch (Exception e){ e.printStackTrace(); }
        }
    }

    private void displayPreviousLocation(){
        if(mapReady){
            try {
                if(pictureLocations !=null && pictureLocations.size()>0){
                    if((locationIndex -1)>0){
                        locationIndex--; }
                    else{ locationIndex =(pictureLocations.size()-1); }
                    PictureLocation pictureLocation = pictureLocations.get(locationIndex);
                    LatLng coords = new LatLng(pictureLocation.latitude,pictureLocation.logitude);
                    map.moveCamera(CameraUpdateFactory.newLatLng(coords));
                    map.animateCamera(CameraUpdateFactory.zoomTo(this.getResources().getInteger(R.integer.marker_zoom)));
                }
            }catch (Exception e){ e.printStackTrace(); }
        }
    }

    @Override
    protected void onResume() {
        super.onResume(); updateMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        this.mapReady =true;
        final Context context = this;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                new Question(context,
                        getString(R.string.map_place_marker),
                        getString(R.string.map_place_marker_question),
                        getString(R.string.dialogo_yes),getString(R.string.dialog_no),
                        android.R.drawable.ic_dialog_map) {
                    @Override
                    public void onConfirm() {
                        try {
                            editLocation(latLng.latitude,latLng.longitude,false);
                        } catch (Exception e) {
                            e.printStackTrace(); Log.e(context.getClass().getName(),e.getMessage());
                        }
                    }
                    @Override public void onCancel() {}
                };
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                new Question(context,
                        getString(R.string.map_edit_marker),
                        getString(R.string.map_edit_marker_question),
                        getString(R.string.dialogo_yes),getString(R.string.dialog_no),
                        android.R.drawable.ic_dialog_map) {
                    @Override
                    public void onConfirm() {
                        try {
                            LatLng posicion = marker.getPosition();
                            editLocation(posicion.latitude,posicion.longitude,true);
                        }
                        catch (Exception e) { e.printStackTrace(); Log.e(context.getClass().getName(),e.getMessage()); }
                    }
                    @Override
                    public void onCancel() {}
                };
                return false;
            }
        });

        this.updateMap();
    }

    private void editLocation(double latitud, double longitud, boolean editar){
        Intent intent = new Intent(this, CreateLocationActivity.class);
        intent.putExtra(PARAM_LATITUDE,latitud);
        intent.putExtra(PARAM_LONGITUDE,longitud);
        intent.putExtra(PARAM_EDIT,editar);
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new Question(this, getString(R.string.app_name), getString(R.string.exit), getString(R.string.dialogo_yes), getString(R.string.dialog_no),
                android.R.drawable.ic_menu_close_clear_cancel) {
            @Override
            public void onConfirm() {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            @Override public void onCancel() {}
        };
    }

    private void updateMap(){
        this.locationIndex =0;
        if(this.mapReady){
            final Context context = this;
            new AsyncTask<Object, Object, Boolean> () {
                @Override
                protected void onPreExecute() { progressBar.setVisibility(View.VISIBLE); map.clear(); }

                @Override
                protected Boolean doInBackground(Object... objs) {
                    Log.i(getClass().getName(),"Updating map.");
                    try {
                        Log.i(getClass().getName(),"Listing Locations");
                        pictureLocations = PhotoPlacesDatabase.getDatabase(context).locationDao().list();
                        Log.i(getClass().getName(),String.format(" Locations found %s", pictureLocations.size()));
                        for (PictureLocation ubicacion: pictureLocations) { publishProgress(new Object[]{ubicacion}); }
                        return true;
                    }
                    catch (Exception e) { e.printStackTrace(); return false;  }
                }

                @Override
                protected void onProgressUpdate(Object... ubicaciones) {
                    PictureLocation ubicacion =(PictureLocation)ubicaciones[0];
                    try {
                        MarkerOptions marcador = new MarkerOptions();
                        LatLng coordenadas = new LatLng(ubicacion.latitude,ubicacion.logitude);
                        marcador.position(coordenadas);
                        marcador.title(ubicacion.name);
                        marcador.snippet(ubicacion.description);
                        if(ubicacion.pictureExists()){
                            try { marcador.icon(BitmapUtil.createIcon(context,ubicacion.picturePath,
                                    context.getResources().getInteger(R.integer.marker_size))); }
                            catch (Exception e) { Log.e(context.getClass().getName(),e.getMessage()); }
                        }
                        marcador.visible(true);
                        marcador.draggable(false);
                        map.addMarker(marcador);
                        map.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
                        map.animateCamera(CameraUpdateFactory.zoomTo(context.getResources().getInteger(R.integer.marker_zoom)));
                    }
                    catch (Exception e) { Log.e(context.getClass().getName(),e.getMessage()); }
                    Log.i(getClass().getName(),String.format(" Marker [%s] added.",ubicacion.toString()));
                }

                @Override
                protected void onPostExecute(Boolean cargaExitosa) {
                    progressBar.setVisibility(View.GONE);
                    displayCurrentLocation();
                    if(!cargaExitosa){
                        Toast.makeText(context,getString(R.string.error_loading_markers),Toast.LENGTH_SHORT).show();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onLocationChanged(Location location) { this.currentGPSLocation = location;  }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
