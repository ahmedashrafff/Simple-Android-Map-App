package com.example.shade.googlemapssample.ui;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public interface HomeView {

    Context getContext();
    void deviceLocationResult(LatLng deviceLocation);
    void locationPermissionResult(boolean result);
    void gpsPermissionResult(boolean result);
    void searchBarResult(LatLng location);
    void requestpermissions(String[] perms,int permsRequestCode);
    void getPlaceResult(LatLng latLng);


}
