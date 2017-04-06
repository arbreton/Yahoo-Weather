package com.nournexus.weatherapp.classes;

/**
 * Created by Andre on 4/5/2017.
 */

public class WeatherClass {
    private String loc_name;
    private String loc_coord;



    public WeatherClass(String loc_name, String loc_coord) {
        super();
        this.setLoc_name(loc_name);
        this.setLoc_qty(loc_coord);
    }


    public String getLoc_name() {
        return loc_name;
    }
    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }
    public String getLoc_qty() {
        return loc_coord;
    }
    public void setLoc_qty(String loc_coord) {
        this.loc_coord = loc_coord;
    }
}
