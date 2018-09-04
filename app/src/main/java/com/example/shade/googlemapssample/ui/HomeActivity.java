package com.example.shade.googlemapssample.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shade.googlemapssample.PlaceAutocompleteAdapter;
import com.example.shade.googlemapssample.R;
import com.example.shade.googlemapssample.presenter.HomePresenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback ,HomeView {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    int permsRequestCode = 200;
    boolean locationGranted = false;
    boolean gpsGranted = false;
    LatLngBounds latLngBounds;
    private GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteAdapter placeAutocompleteAdapter;
    HomePresenter homePresenter;
    AutoCompleteTextView autoCompleteTextView;
    ImageButton mylocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homePresenter=new HomePresenter(this);
        homePresenter.checkGPSPermission();
        homePresenter.checkLocationPermission();

        if (locationGranted == false && gpsGranted==false)
            finish();
        else if(locationGranted == true && gpsGranted==true)
        {
            init();
            intialaizeSearchbar();
            homePresenter.getDeviceLocation();

        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }


    public void init()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        GoogleApiClient.OnConnectionFailedListener listener=new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        };
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, listener)
                .build();
        latLngBounds=new LatLngBounds(new LatLng(26, 30),new LatLng(36,22));
        placeAutocompleteAdapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient,latLngBounds,null,homePresenter);
        mylocation=(ImageButton) findViewById(R.id.mylocationButton);
        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePresenter.getDeviceLocation();
            }
        });


    }


    public void intialaizeSearchbar()
    {
        autoCompleteTextView.setAdapter(placeAutocompleteAdapter);
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    homePresenter.intialaizeSearchbar(autoCompleteTextView.getText().toString());
                }

                return false;
            }
        });
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void deviceLocationResult(LatLng deviceLocation)
    {
        mMap.addMarker(new MarkerOptions().title("Your Location").position(deviceLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation,16));
    }

    @Override
    public void locationPermissionResult(boolean result)
    {
        locationGranted=result;
    }

    @Override
    public void gpsPermissionResult(boolean result) {
        gpsGranted=result;
    }

    @Override
    public void searchBarResult(LatLng location)
    {
        mMap.addMarker(new MarkerOptions().position(location).title("Your Search"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));

    }

    @Override
    public void requestpermissions(String[] perms, int permsRequestCode) {
        ActivityCompat.requestPermissions(this,perms,permsRequestCode);
    }

    @Override
    public void getPlaceResult(LatLng latLng)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
        autoCompleteTextView.setText(null);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        mMap.addMarker(new MarkerOptions().position(latLng));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(permsRequestCode==requestCode)
        {
            if(grantResults.length>0)
            {
                for(int i=0; i<grantResults.length; i++)
                {
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                        locationGranted=false; return;
                }

                locationGranted=true;
                Log.d("Startnow","All perms are true on result");
            }
        }

    }
}

