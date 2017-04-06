package com.nournexus.weatherapp.JSONConsumer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andre on 4/5/2017.
 */


public class Condition implements JSONPopulator {
    private int code;
    private int temperature;
    private int highTemp;
    private int lowTemp;
    private String description;
    private String day;
    public int getCode() {
        return code;
    }
    public int getTemp() {
        return temperature;
    }
    public int getHighTemp() {
        return highTemp;
    }
    public int getLowTemp() {
        return lowTemp;
    }
    public String getDescription() {
        return description;
    }
    public String getDay() {
        return day;
    }

    @Override
    public void populate(JSONObject data) {
        code = data.optInt("code");
        temperature = data.optInt("temp");
        highTemp = data.optInt("high");
        lowTemp = data.optInt("low");
        description = data.optString("text");
        day = data.optString("day");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        try {
            data.put("code", code);
            data.put("temp", temperature);
            data.put("high", highTemp);
            data.put("low", lowTemp);
            data.put("text", description);
            data.put("day", day);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
