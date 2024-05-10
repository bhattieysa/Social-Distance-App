package com.devsoft.socialdistanceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.devsoft.socialdistanceapp.helper.SessionManager;
import com.devsoft.socialdistanceapp.helper.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2; 
    public static double lattitude=0;
    public static double longitude=0;
    private Location mylocation;
    FusedLocationProviderClient fusedLocationClient;
    DatabaseReference databaseReference;
    public static boolean flag = false;
    public static boolean flag_latlng = false;
    ImageView imageView;
    SessionManager manager;
    SweetAlertDialog pDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        databaseReference =  FirebaseDatabase.getInstance().getReference().child("user")
                .child(FirebaseAuth.getInstance().getUid());
        mapFragment.getMapAsync(this);
        imageView = findViewById(R.id.image_walk);
        manager = new SessionManager(getApplicationContext());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissions();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    flag_latlng = false;
                    flag = false;
                    imageView.setImageResource(R.drawable.ic_baseline_directions_walk_24);
                }else {
                    flag_latlng = true;
                    flag = true;
                    imageView.setImageResource(R.drawable.ic_baseline_directions_walk_red_24);
                }
            }
        });

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@NonNull Location location) {
                //Toast.makeText(MapsActivity.this, "...", Toast.LENGTH_SHORT).show();
                LatLng getla = new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(getla,18);
                mMap.animateCamera(cameraUpdate);
                longitude =  location.getLongitude();
                lattitude =   location.getLatitude();
                if (flag_latlng){
                    manager.setLat(lattitude);
                    manager.setLong(longitude);
                    flag_latlng = false;
                }else if (manager.getLat()>0){
                    Location locationA = new Location("point A");
                    locationA.setLatitude(lattitude);
                    locationA.setLongitude(longitude);
                    Location locationB = new Location("point B");
                    locationB.setLatitude(manager.getLat());
                    locationB.setLongitude(manager.getLong());
                    //50000 meters = 50km
                    float distance = locationA.distanceTo(locationB);
                    float distance2 = locationB.distanceTo(locationA);
                    if (5000>distance){
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(manager.getLat(),manager.getLong());
                        markerOptions.position(latLng);
                        mMap.addMarker(markerOptions);
//                        Toast.makeText(MapsActivity.this, , Toast.LENGTH_SHORT).show();
                        manager.setLong(0);
                        manager.setLat(0);
                        imageView.setImageResource(R.drawable.ic_baseline_directions_walk_24);
                        if (!pDialog.isShowing())
                            show_saved_dialog("You have travelled 5KM");
                    }else if (5000>distance2){
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(manager.getLat(),manager.getLong());
                        markerOptions.position(latLng);
                        mMap.addMarker(markerOptions);
                        manager.setLong(0);
                        manager.setLat(0);
                        imageView.setImageResource(R.drawable.ic_baseline_directions_walk_24);
                        if (!pDialog.isShowing())
                            show_saved_dialog("You have travelled 5KM");
                    }
                }
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            user.setLat(lattitude);
                            user.setLon(longitude);
                            databaseReference.setValue(user);
                            setupmarker();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                locationManager.removeUpdates(this);
            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, ""+connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
    private synchronized void setUpGClient() {
        if (googleApiClient==null){
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //googleApiClient.stopAutoManage(getApplicationContext());
        googleApiClient.disconnect();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            lattitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {
                        @Override
                        public void onResult(@NonNull Result result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getApplicationContext(),
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS_GPS, null, 0, 0, 0, null);
                                    } catch (IntentSender.SendIntentException e) {
                                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }


                    });
                }
            }else {
                //googleApiClient=null;
                setUpGClient();
            }
        }else{
//            googleApiClient=null;
            setUpGClient();
        }
    }
    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            setUpGClient();
            getMyLocation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CHECK_SETTINGS_GPS){
            getMyLocation();
        }else {
            Toast.makeText(getApplicationContext(), "Activity result", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 237: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void setupmarker(){
        mMap.clear();
        FirebaseDatabase.getInstance().getReference().child("user")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                User model = snapshot.getValue(User.class);
                                if (!model.getId().equals(FirebaseAuth.getInstance().getUid()))
                                {
                                    Location locationA = new Location("point A");
                                    locationA.setLatitude(lattitude);
                                    locationA.setLongitude(longitude);
                                    Location locationB = new Location("point B");
                                    locationB.setLatitude(model.getLat());
                                    locationB.setLongitude(model.getLon());
                                    //50000 meters = 50km
                                    float distance = locationA.distanceTo(locationB);
                                    float distance2 = locationB.distanceTo(locationA);
                                    if (2>distance){
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        LatLng latLng = new LatLng(model.getLat(),model.getLon());
                                        markerOptions.position(latLng);
                                        markerOptions.title(model.getName());
                                        mMap.addMarker(markerOptions);
                                        if (!pDialog.isShowing())
                                            show_saved_dialog("Someone is in 2m distance.");
                                        if (mediaPlayer!=null){
                                            if (!mediaPlayer.isPlaying()){
                                                initBeepSound();
                                            }
                                        }else {
                                            initBeepSound();
                                        }
                                        //Toast.makeText(MapsActivity.this, "Near", Toast.LENGTH_SHORT).show();
                                    }else if (2>distance2){
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.title(model.getName());
                                        LatLng latLng = new LatLng(model.getLat(),model.getLon());
                                        markerOptions.position(latLng);
                                        mMap.addMarker(markerOptions);
                                        if (!pDialog.isShowing())
                                            show_saved_dialog("Someone is in 2m distance.");
                                        if (mediaPlayer!=null){
                                            if (!mediaPlayer.isPlaying()){
                                                initBeepSound();
                                            }
                                        }else {
                                            initBeepSound();

                                        }
                                        //Toast.makeText(MapsActivity.this, "Near", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            //                                Toast.makeText(getApplicationContext(), "No user Found with in 50 KM range", Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private static final float BEEP_VOLUME = 9.10f;
    private MediaPlayer mediaPlayer;
    private void initBeepSound() {
        if ( true) {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                }
            });

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.alarm);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    public void show_saved_dialog(String message){

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismissWithAnimation();
                if (mediaPlayer!=null){
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                }else {
                    mediaPlayer = null;
                }
            }
        });
        pDialog.show();
    }
}