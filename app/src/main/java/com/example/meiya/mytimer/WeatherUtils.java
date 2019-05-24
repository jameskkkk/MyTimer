package com.example.meiya.mytimer;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherUtils {

    private final static String TAG = WeatherUtils.class.getSimpleName();
    private Context mContext;
    private WeatherService service;
    private volatile static WeatherUtils mWeatherUtils;

    private WeatherUtils(Context context) {
        mContext = context.getApplicationContext();
        //init okhttp client
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).addInterceptor(loggingInterceptor).build();
        //init retrofit client
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(WeatherService.class);
    }

    synchronized static WeatherUtils getInstance(Context context) {
        if (mWeatherUtils == null) {
            synchronized (WeatherUtils.class) {
                mWeatherUtils = new WeatherUtils(context);
            }
        }
        return mWeatherUtils;
    }

    Observable<WeatherInfoBean> getWeatherById() {
        return service.getWeatherById(Constants.XIAMEN_ID, Constants.UNIT, Constants.KEY);
    }

    Observable<WeatherInfoBean> getWeather() {
        return service.getWeather("London,us", Constants.KEY);
    }
}
