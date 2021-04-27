package com.example.mikeweatherforecast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.SingleSubscriber;

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
    TextView tvTemp, tvDesc, tvSunrise, tvSunset, tvHumidity, tvWindSpeed, tvFeelTemp;
    ImageView tvImage;
    WeatherAPI.ApiInterface api;

    LocationManager locationManager;
    Location location;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemp = findViewById(R.id.tvTemp);
        tvDesc = findViewById(R.id.tvDesc);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvFeelTemp = findViewById(R.id.tvFeelTemp);
        tvImage = findViewById(R.id.ivImage);

        api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(searchProviderCriteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        } else {
            locationManager.requestSingleUpdate(provider, this, null);
        }
    }

    public void showWeatherData(WeatherDay data){
        tvTemp.setText(data.getCity() + " " + data.getTempWithDegree());
        Glide.with(MainActivity.this).load(data.getIconUrl()).into(tvImage);
        tvDesc.setText(data.getWeatherDescription());
        tvSunrise.setText("Восход в "+data.getSunrise());
        tvSunset.setText("Закат в "+data.getSunset());
        tvHumidity.setText("Влажность: "+data.getHumidityInteger());
        tvWindSpeed.setText("Скорость ветра: "+data.getWindSpeedInteger());
        tvFeelTemp.setText("Чувствуется: "+data.getFeelTempWithDegree());
    }

    public void getWeather() {
        if(location == null) {
            tvTemp.setText("Не удалось получить местоположение");
            return;
        }

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        String units = "metric";
        String key = WeatherAPI.KEY;
        String lang = "ru";

        //Call<WeatherDay> callToday = api.getToday(lat, lng, units, lang, key);

        SingleSubscriber<WeatherDay> observer = new SingleSubscriber<WeatherDay>() {
            @Override
            public void onSuccess(WeatherDay data) {
                showWeatherData(data);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        };


        api.getToday(lat, lng, units, lang, key).subscribe(observer);


        /*callToday.enqueue(new Callback<WeatherDay>() {
            @Override
            public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {
                Log.e(TAG, "onResponse");
                WeatherDay data = response.body();
                Log.d(TAG,response.toString());

                if (response.isSuccessful()) {
                    showWeatherData(data);
                }
            }

            @Override
            public void onFailure(Call<WeatherDay> call, Throwable t) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });*/

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