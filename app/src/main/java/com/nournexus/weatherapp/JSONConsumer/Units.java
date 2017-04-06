package com.nournexus.weatherapp.JSONConsumer;

/**
 * Created by Andre on 4/5/2017.
 */


import org.json.JSONException;
import org.json.JSONObject;

public class Units implements JSONPopulator {
    private String temperature;
    public String getTemp() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("temperature", temperature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}
