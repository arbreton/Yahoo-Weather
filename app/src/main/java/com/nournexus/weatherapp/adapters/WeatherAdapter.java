package com.nournexus.weatherapp.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nournexus.weatherapp.R;
import com.nournexus.weatherapp.WeatherActivity;
import com.nournexus.weatherapp.classes.WeatherClass;
import com.nournexus.weatherapp.listeners.WeatherServiceListener;
import com.nournexus.weatherapp.main.YahooWeatherService;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Andre on 4/5/2017.
 */

public class WeatherAdapter extends ArrayAdapter {




    public List<WeatherClass> list= new ArrayList();

    public WeatherAdapter(Context context, int resource) {
        super(context, resource);
        // TODO Auto-generated constructor stub
    }
    public void add(WeatherClass object) {
        // TODO Auto-generated method stub
        list.add(object);
        super.add(object);
    }
    static class ImgHolder
    {
        TextView NAME;
        TextView LOC;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.list.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.list.get(getCount() - position - 1);
    }
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        View row;
        row = convertView;
        final ImgHolder holder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_layout,parent,false);
            holder = new ImgHolder();
            holder.NAME = (TextView) row.findViewById(R.id.loc_name);
            holder.LOC = (TextView) row.findViewById(R.id.loc_coord);
            row.setTag(holder);
        }
        else
        {
            holder = (ImgHolder) row.getTag();
        }


        final WeatherClass FR = (WeatherClass) getItem(position);
        holder.NAME.setText(FR.getLoc_name());
        holder.NAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                String[] latlong =  holder.NAME.getText().toString().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                WeatherActivity.updateMaplocation(latitude,longitude, holder.LOC.getText().toString());
                YahooWeatherService.refreshWeather(holder.LOC.getText().toString());
            }
        });

        holder.LOC.setText(FR.getLoc_qty());
        holder.LOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                String[] latlong =  holder.NAME.getText().toString().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                WeatherActivity.updateMaplocation(latitude,longitude, holder.LOC.getText().toString());
                YahooWeatherService.refreshWeather(holder.LOC.getText().toString());
            }
        });


        return row;
    }


    }
