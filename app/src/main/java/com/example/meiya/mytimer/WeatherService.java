package com.example.meiya.mytimer;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather")
    Observable<WeatherInfoBean> getWeather(@Query("q") String location, @Query("appid") String key);

    @GET("data/2.5/weather")
    Observable<WeatherInfoBean> getWeatherById(@Query("id") String cityId, @Query("units") String units, @Query("appid") String key);

}
