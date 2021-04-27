package com.example.mikeweatherforecast;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Single;
import rx.schedulers.Schedulers;

public class WeatherAPI {
    public static String KEY = "211091242f97b25c31884f4c8fa8cd81";
    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit = null;
    private static RxJavaCallAdapterFactory rxAdapter;

    public interface ApiInterface {
        @GET("weather")
        Single<WeatherDay> getToday(
                @Query("lat") Double lat,
                @Query("lon") Double lon,
                @Query("units") String units,
                @Query("lang") String lang,
                @Query("appid") String appid
        );
    }

    public static Retrofit getClient() {
        if(rxAdapter == null){
            rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(rxAdapter)
                    .build();
        }
        return retrofit;
    }
}
