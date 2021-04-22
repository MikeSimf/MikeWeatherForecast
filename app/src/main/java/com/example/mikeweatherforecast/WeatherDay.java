package com.example.mikeweatherforecast;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class WeatherDay {
    private static final String WEATHER_ICON_PATH = "https://openweathermap.org/img/w/";

    public class WeatherTemp {
        Double temp;
        Double temp_min;
        Double temp_max;
        Double feels_like;
        Double humidity;
        Double pressure;
    }

    public class WeatherDescription {
        String icon;
        String description;
    }

    public class WeatherWind{
        Double speed;
    }

    public class WeatherSunTime {
        Long sunrise;
        Long sunset;
        Long timezone;
    }

    @SerializedName("main")
    private WeatherTemp temp;

    @SerializedName("weather")
    private List<WeatherDescription> desctiption;

    @SerializedName("name")
    private String city;

    @SerializedName("dt")
    private long timestamp;

    @SerializedName("wind")
    private WeatherWind wind;

    @SerializedName("sys")
    private WeatherSunTime sunTime;

    public WeatherDay(WeatherTemp temp, List<WeatherDescription> desctiption, WeatherWind wind, WeatherSunTime sunTime) {
        this.temp = temp;
        this.desctiption = desctiption;
        this.wind = wind;
        this.sunTime = sunTime;
    }

    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp * 1000);
        return date;
    }

    private String getFormateTime(Long time){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date.getTime());
    }

    public String getSunrise() {
        return getFormateTime(sunTime.sunrise);
    }

    public String getSunset() {
        return getFormateTime(sunTime.sunset);
    }

    public String getWeatherDescription(){
        return desctiption.get(0).description;
    }

    public String getWindSpeed(){
        return String.valueOf(wind.speed);
    }

    public String getWindSpeedInteger(){
        return String.valueOf(wind.speed.intValue());
    }

    public String getHumidity(){
        return String.valueOf(temp.humidity);
    }

    public String getHumidityInteger(){
        return String.valueOf(temp.humidity.intValue());
    }

    public String getTemp() { return String.valueOf(temp.temp); }

    public String getTempMin() { return String.valueOf(temp.temp_min); }

    public String getTempMax() { return String.valueOf(temp.temp_max); }

    public String getTempInteger() { return String.valueOf(temp.temp.intValue()); }

    public String getFeelTemp() { return String.valueOf(temp.feels_like); }

    public String getFeelTempInteger() { return String.valueOf(temp.feels_like.intValue()); }

    public String getTempWithDegree() { return String.valueOf(temp.temp.intValue()) + "\u00B0"; }

    public String getFeelTempWithDegree() { return String.valueOf(temp.feels_like.intValue()) + "\u00B0"; }

    public String getCity() { return city; }

    public String getIcon() { return desctiption.get(0).icon; }

    public String getIconUrl() {
        return WEATHER_ICON_PATH + desctiption.get(0).icon + ".png";
    }
}
