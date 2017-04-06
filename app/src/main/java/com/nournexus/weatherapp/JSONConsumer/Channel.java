package com.nournexus.weatherapp.JSONConsumer;

/**
 * Created by Andre on 4/5/2017.
 */


import org.json.JSONException;
import org.json.JSONObject;

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;
    private String location;

    public Item getItem() {
        return item;
    }

    public String getLocation() {
        return location;
    }

    public Units getUnits() {
        return units;
    }


    @Override
    public void populate(JSONObject data) {
        units = new Units();
        units.populate(data.optJSONObject("units"));
        item = new Item();
        item.populate(data.optJSONObject("item"));
        JSONObject locationData = data.optJSONObject("location");
        String region = locationData.optString("region");
        String country = locationData.optString("country");
        location = String.format("%s, %s", locationData.optString("city"), (region.length() != 0 ? region : country));
    }

    @Override
    public JSONObject toJSON() {

        JSONObject data = new JSONObject();
        try {
            data.put("units", units.toJSON());
            data.put("item", item.toJSON());
            data.put("requestLocation", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

}
