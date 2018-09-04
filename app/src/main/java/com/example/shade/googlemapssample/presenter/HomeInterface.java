package com.example.shade.googlemapssample.presenter;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public interface HomeInterface {

    void checkGPSPermission();

    void getDeviceLocation();

    void checkLocationPermission();

    void intialaizeSearchbar(String searchString);

    void showGPSDisabledAlertToUser();

    void getPlace(LatLng latLng);

}
