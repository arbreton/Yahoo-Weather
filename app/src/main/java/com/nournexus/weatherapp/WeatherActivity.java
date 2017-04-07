package com.nournexus.weatherapp;

/**
 * Created by Andre on 4/5/2017.
 */


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nournexus.weatherapp.JSONConsumer.Channel;
import com.nournexus.weatherapp.JSONConsumer.Condition;
import com.nournexus.weatherapp.JSONConsumer.Item;
import com.nournexus.weatherapp.JSONConsumer.LocationResult;
import com.nournexus.weatherapp.JSONConsumer.Units;
import com.nournexus.weatherapp.adapters.WeatherAdapter;
import com.nournexus.weatherapp.classes.WeatherClass;
import com.nournexus.weatherapp.fragments.WeatherConditionFragment;
import com.nournexus.weatherapp.listeners.GeocodingServiceListener;
import com.nournexus.weatherapp.listeners.WeatherServiceListener;
import com.nournexus.weatherapp.main.WeatherCacheService;
import com.nournexus.weatherapp.main.GoogleMapsGeocodingService;
import com.nournexus.weatherapp.main.YahooWeatherService;

import java.io.IOException;
import java.util.List;


public class WeatherActivity extends AppCompatActivity implements OnMapReadyCallback, WeatherServiceListener, GeocodingServiceListener, LocationListener {


    public static GoogleMap mMap;
    public static int GET_WEATHER_FROM_CURRENT_LOCATION = 0x00001;
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private YahooWeatherService weatherService;
    private GoogleMapsGeocodingService geocodingService;
    private WeatherCacheService cacheService;
    public static ProgressDialog loadingDialog;
    static Marker marker;
    // weather service fail
    private boolean weatherServicesHasFailed = false;
    private SharedPreferences preferences = null;
    private static Boolean splashScreen = null;

    ListView listview;
    static String[] name = {"London, UK","Ankara, Turkey","Ontario, Canada", "Brasilia, Brazil", "Buenos Aires, Argentina", "Santiago, Chile"};
    static String[] loc = {"51.502242, -0.141465","38.692918, 35.437894","51.356492, -86.564511","-15.804979, -47.877438","-34.620740, -58.409295","-33.488509, -70.577675"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        listview = (ListView) findViewById(R.id.main_list_view);
        WeatherAdapter adapter = new WeatherAdapter(getApplicationContext(),R.layout.row_layout);
        listview.setAdapter(adapter);
        int i = 0;
        for(String Name : name )
        {
            WeatherClass obj = new WeatherClass(loc [i], Name);
            adapter.add(obj);
            i++;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        weatherService = new YahooWeatherService(this);
        weatherService.setTemperatureUnit(preferences.getString(getString(R.string.pref_temperature_unit), null));
        geocodingService = new GoogleMapsGeocodingService(this);
        cacheService = new WeatherCacheService(this);
        if (preferences.getBoolean(getString(R.string.pref_needs_setup), true)) {
            startSettingsActivity();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        String location = null;
        if (preferences.getBoolean(getString(R.string.pref_geolocation_enabled), true)) {
            String locationCache = preferences.getString(getString(R.string.pref_cached_location), null);
            if (locationCache == null) {
                getWeatherFromCurrentLocation();
            } else {
                location = locationCache;
            }
        } else {
            location = preferences.getString(getString(R.string.pref_manual_location), null);
        }
        if (location != null) {
            weatherService.refreshWeather(location);
        }
    }

    public static void updateMaplocation(double latitude, double longitude, final String locationName) {
        final LatLng location = new LatLng(latitude,longitude);
        if(marker == null){
        }else{
            mMap.clear();
        }
        final CameraUpdate locationZ = CameraUpdateFactory.newLatLngZoom(location, 4);
        marker = mMap.addMarker(new MarkerOptions().position(location).title(String.format("%s", locationName)));
        mMap.animateCamera(locationZ);
    }

    private void getWeatherFromCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, GET_WEATHER_FROM_CURRENT_LOCATION);
            return;
        }

        // LocationManager
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Criteria locationCriteria = new Criteria();

        if (isNetworkEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        } else if (isGPSEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        }

        locationManager.requestSingleUpdate(locationCriteria, this, null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == WeatherActivity.GET_WEATHER_FROM_CURRENT_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherFromCurrentLocation();
            } else {
                loadingDialog.hide();

                AlertDialog messageDialog = new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.location_permission_needed))
                        .setPositiveButton(getString(R.string.disable_geolocation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startSettingsActivity();
                            }
                        })
                        .create();

                messageDialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.currentLocation:
                splashScreen = true;
                loadingDialog.show();
                getWeatherFromCurrentLocation();
                return true;
            case R.id.settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void serviceSuccess(Channel channel) {
        loadingDialog.hide();
        Condition condition = channel.getItem().getCondition();
        Units units = channel.getUnits();
        Condition[] forecast = channel.getItem().getForecast();
        int weatherIconImageResource = getResources().getIdentifier("icon_" + condition.getCode(), "drawable", getPackageName());
        weatherIconImageView.setImageResource(weatherIconImageResource);
        temperatureTextView.setText(getString(R.string.temperature_output, condition.getTemp(), units.getTemp()));
        conditionTextView.setText(condition.getDescription());
        locationTextView.setText(channel.getLocation());
        String location = null;
        if (preferences.getBoolean(getString(R.string.pref_geolocation_enabled), true) && splashScreen == true) {
            splashScreen = false;
            String locationCache = preferences.getString(getString(R.string.pref_cached_location), null);
            if (locationCache == null) {
                getWeatherFromCurrentLocation();
            } else {
                location = locationCache;
            }
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                if (location != null) {
                    Toast.makeText(this, location, Toast.LENGTH_SHORT).show();
                    updateMaplocation(latitude,longitude,location);
                }
            }
        } else {
            location = channel.getLocation();
            if (location != null) {
                Toast.makeText(this, location, Toast.LENGTH_SHORT).show();
                updateMaplocation(Item.getLat(),Item.getLongitude(),location);
            }
        }

        for (int day = 0; day < forecast.length; day++) {
            if (day >= 5) {
                break;
            }
            Condition currentCondition = forecast[day];
            int viewId = getResources().getIdentifier("forecast_" + day, "id", getPackageName());
            WeatherConditionFragment fragment = (WeatherConditionFragment) getSupportFragmentManager().findFragmentById(viewId);
            if (fragment != null) {
                fragment.loadForecast(currentCondition, channel.getUnits());
            }
        }
        cacheService.save(channel);
    }

    @Override
    public void serviceFailure(Exception exception) {
        // display error if this is the second failure
        if (weatherServicesHasFailed) {
            loadingDialog.hide();
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            // error doing reverse geocoding, load weather data from cache
            weatherServicesHasFailed = true;
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            cacheService.load(this);
        }
    }

    @Override
    public void geocodeSuccess(LocationResult location) {
        // completed geocoding successfully
        weatherService.refreshWeather(location.getAddress());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.pref_cached_location), location.getAddress());
        editor.apply();
    }

    @Override
    public void geocodeFailure(Exception exception) {
        // GeoCoding failed, try loading weather data from the cache
        cacheService.load(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        geocodingService.refreshLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }




}
