package com.nournexus.weatherapp.listeners;

/**
 * Created by Andre on 4/5/2017.
 */


import com.nournexus.weatherapp.JSONConsumer.Channel;

public interface WeatherServiceListener {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
