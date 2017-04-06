package com.nournexus.weatherapp.fragments;

/**
 * Created by Andre on 4/5/2017.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nournexus.weatherapp.R;
import com.nournexus.weatherapp.JSONConsumer.Condition;
import com.nournexus.weatherapp.JSONConsumer.Units;

public class WeatherConditionFragment extends Fragment {
    private ImageView weatherIconImageView;
    private TextView dayLabelTextView;
    private TextView highTemperatureTextView;
    private TextView lowTemperatureTextView;

    public WeatherConditionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_condition, container, false);

        weatherIconImageView = (ImageView) view.findViewById(R.id.weatherIconImageView);
        dayLabelTextView = (TextView) view.findViewById(R.id.dayTextView);
        highTemperatureTextView = (TextView) view.findViewById(R.id.highTemperatureTextView);
        lowTemperatureTextView = (TextView) view.findViewById(R.id.lowTemperatureTextView);

        return view;
    }

    public void loadForecast(Condition forecast, Units units) {
        int weatherIconImageResource = getResources().getIdentifier("icon_" + forecast.getCode(), "drawable", getActivity().getPackageName());

        weatherIconImageView.setImageResource(weatherIconImageResource);
        dayLabelTextView.setText(forecast.getDay());
        highTemperatureTextView.setText(getString(R.string.temperature_output, forecast.getHighTemp(), units.getTemp()));
        lowTemperatureTextView.setText(getString(R.string.temperature_output, forecast.getLowTemp(), units.getTemp()));


    }
}
