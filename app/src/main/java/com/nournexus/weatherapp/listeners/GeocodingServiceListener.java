package com.nournexus.weatherapp.listeners;

/**
 * Created by Andre on 4/5/2017.
 */


import com.nournexus.weatherapp.JSONConsumer.LocationResult;

public interface GeocodingServiceListener {
    void geocodeSuccess(LocationResult location);
    void geocodeFailure(Exception exception);
}
