package com.nournexus.weatherapp.JSONConsumer;

/**
 * Created by Andre on 4/5/2017.
 */


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item implements JSONPopulator {
    private Condition condition;
    private Condition[] forecast;
    public Condition getCondition() {
        return condition;
    }
    public Condition[] getForecast() {
        return forecast;
    }

    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));
        JSONArray forecastData = data.optJSONArray("forecast");
        forecast = new Condition[forecastData.length()];
        for (int i = 0; i < forecastData.length(); i++) {
            forecast[i] = new Condition();
            try {
                forecast[i].populate(forecastData.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        try {
            data.put("condition", condition.toJSON());
            data.put("forecast", new JSONArray(forecast));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
