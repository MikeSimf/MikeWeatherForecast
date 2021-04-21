package com.example.mikeweatherforecast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int INITIAL_REQUEST = 1337;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static Criteria searchProviderCriteria = new Criteria();

    // Location Criteria
    static {
        searchProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
        searchProviderCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        searchProviderCriteria.setCostAllowed(false);
    }

    String TAG = "MY_WEATHER";
    TextView tvTemp;
    ImageView tvImage;
    LinearLayout llForecast;
    WeatherAPI.ApiInterface api;

    LocationManager locationManager;
    Location location;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvImage = (ImageView) findViewById(R.id.ivImage);
        llForecast = (LinearLayout) findViewById(R.id.llForecast);

        api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(searchProviderCriteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        } else {
            locationManager.requestSingleUpdate(provider, this, null);
        }
    }

    public void getWeather(View v) {
        if(location == null) {
            tvTemp.setText("Не удалось получить местоположение");
            return;
        }

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        String units = "metric";
        String key = WeatherAPI.KEY;
        String lang = "ru";

        Log.d(TAG, "OK");

        // get weather for today
        Call<WeatherDay> callToday = api.getToday(lat, lng, units, lang, key);
        callToday.enqueue(new Callback<WeatherDay>() {
            @Override
            public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {
                Log.e(TAG, "onResponse");
                WeatherDay data = response.body();
                Log.d(TAG,response.toString());

                if (response.isSuccessful()) {
                    tvTemp.setText(data.getCity() + " " + data.getTempWithDegree() + " lat = " + lat + " lng = "+ lng);
                    Glide.with(MainActivity.this).load(data.getIconUrl()).into(tvImage);
                }
            }

            @Override
            public void onFailure(Call<WeatherDay> call, Throwable t) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });

        // get weather forecast
        Call<WeatherForecast> callForecast = api.getForecast(lat, lng, units, lang, key);
        callForecast.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                Log.e(TAG, "onResponse");
                WeatherForecast data = response.body();
                //Log.d(TAG,response.toString());

                if (response.isSuccessful()) {
                    SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("E");
                    ViewGroup.LayoutParams paramsTextView = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ViewGroup.LayoutParams paramsImageView = new ViewGroup.LayoutParams(convertDPtoPX(40, MainActivity.this),
                            convertDPtoPX(40, MainActivity.this));

                    int marginRight = convertDPtoPX(15, MainActivity.this);
                    LinearLayout.LayoutParams paramsLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsLinearLayout.setMargins(0, 0, marginRight, 0);

                    llForecast.removeAllViews();

                    for (WeatherDay day : data.getItems()) {
                        if (day.getDate().get(Calendar.HOUR_OF_DAY) == 15) {
                            String date = String.format("%d.%d.%d %d:%d",
                                    day.getDate().get(Calendar.DAY_OF_MONTH),
                                    day.getDate().get(Calendar.WEEK_OF_MONTH),
                                    day.getDate().get(Calendar.YEAR),
                                    day.getDate().get(Calendar.HOUR_OF_DAY),
                                    day.getDate().get(Calendar.MINUTE)
                            );
                            Log.d(TAG, date);
                            Log.d(TAG, day.getTempInteger());
                            Log.d(TAG, "---");

                            // child view wrapper
                            LinearLayout childLayout = new LinearLayout(MainActivity.this);
                            childLayout.setLayoutParams(paramsLinearLayout);
                            childLayout.setOrientation(LinearLayout.VERTICAL);

                            // show day of week
                            TextView tvDay = new TextView(MainActivity.this);
                            String dayOfWeek = formatDayOfWeek.format(day.getDate().getTime());
                            tvDay.setText(dayOfWeek);
                            tvDay.setLayoutParams(paramsTextView);
                            childLayout.addView(tvDay);

                            // show image
                            ImageView ivIcon = new ImageView(MainActivity.this);
                            ivIcon.setLayoutParams(paramsImageView);
                            Glide.with(MainActivity.this).load(day.getIconUrl()).into(ivIcon);
                            childLayout.addView(ivIcon);

                            // show temp
                            TextView tvTemp = new TextView(MainActivity.this);
                            tvTemp.setText(day.getTempWithDegree());
                            tvTemp.setLayoutParams(paramsTextView);
                            childLayout.addView(tvTemp);

                            llForecast.addView(childLayout);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });

    }

    public int convertDPtoPX(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int)(dp * density);
        return px;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}