package com.example.gbiscini.test;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mMarker;
    static MapsActivity main;



    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int INITIAL_REQUEST=1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        for(String s: INITIAL_PERMS){
            if (!hasPermission(s))
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        main=this;

        FloatingActionButton cameraButton = (FloatingActionButton) findViewById(R.id.fab);
        cameraButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MapsActivity.this, CameraActivity.class);
                MapsActivity.this.startActivity(cameraIntent);

            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.activity_maps, container, false);
        Button cameraButton = (Button) View.findViewById(R.id.fab);
        cameraButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MapsActivity.this, CameraActivity.class);
                MapsActivity.this.startActivity(myIntent);

            }
        });
        return View;

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.style));
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        // Add a marker in Sydney and move the camera
        Log.d("STATE","DADADA");
        if ( checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

        Intent intent=new Intent(this, GPService.class);
        if(!isServiceRunning(GPService.class)) {
            startService(intent);
        }
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Here you are"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }



        Intent intent = getIntent();
        boolean result=intent.getBooleanExtra("wordFound",false);
        Log.d("RESULT",String.valueOf(result));
        if(result){
            Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(l.getLatitude(),l.getLongitude()))
                    .radius(100).fillColor(0x7F00FF00).strokeColor(Color.GREEN);

            Circle circle = mMap.addCircle(circleOptions);
            if(mMarker!=null){
                mMarker.setPosition(new LatLng(l.getLatitude(),l.getLongitude()));
            }else{
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(l.getLatitude(),l.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.feather_marker))
                        .title("I am here"));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(),l.getLongitude()), 15));

        }
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
    @Override
    public void onRestart(){
        super.onRestart();

        main=this;

    }

    public void changeMarker(Location location){
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(latitude, longitude);


        if(mMarker!=null){
            mMarker.setPosition(latLng);
        }else{
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.feather_marker))
                    .title("I am here"));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
