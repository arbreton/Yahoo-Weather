package com.nournexus.weatherapp.JSONConsumer;

/**
 * Created by Andre on 4/5/2017.
 */


import org.json.JSONObject;

public interface JSONPopulator {
    void populate(JSONObject data);
    JSONObject toJSON();
}
