package com.example.shade.googlemapssample.presenter;

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
import android.util.Log;
import android.widget.Toast;

import com.example.shade.googlemapssample.ui.HomeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomePresenter implements HomeInterface
{
    HomeView homeView;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void checkGPSPermission()
    {
        Context context=homeView.getContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            homeView.gpsPermissionResult(true);
        }
        else
        {
            homeView.gpsPermissionResult(false);
            showGPSDisabledAlertToUser();
        }

    }

    @Override
    public void getDeviceLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(homeView.getContext());

        Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    Location currentLocation = (Location) task.getResult();
                    homeView.deviceLocationResult(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                }
                else
                {
                }

            }
        });

    }

    @Override
    public void checkLocationPermission() {

        int permsRequestCode=200;

        Log.d("Startnow","Checking Perms");
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION
                ,Manifest.permission.ACCESS_FINE_LOCATION};

        if (ActivityCompat.checkSelfPermission(homeView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.checkSelfPermission(homeView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                homeView.locationPermissionResult(true);
                Log.d("Startnow","All Perms are true");

            }
            else
            {
                homeView.locationPermissionResult(false);
                homeView.requestpermissions(perms,permsRequestCode);
            }

        }

        else
        {
            homeView.locationPermissionResult(false);
            homeView.requestpermissions(perms,permsRequestCode);
        }


    }

    @Override
    public void intialaizeSearchbar(String searchString)
    {
        Geocoder geocoder = new Geocoder(homeView.getContext());
        List<Address> list = new ArrayList<>();
        try
        {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(list.size() > 0)
        {
            Address address = list.get(0);
            LatLng location=new LatLng(address.getLatitude(),address.getLongitude());
            homeView.searchBarResult(location);
        }

    }

    @Override
    public void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(homeView.getContext());
        alertDialogBuilder.setMessage("Enable GPS?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                               homeView.getContext().startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    @Override
    public void getPlace(LatLng placeLatLng) {
        homeView.getPlaceResult(placeLatLng);

    }


}
