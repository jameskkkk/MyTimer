package com.example.meiya.mytimer;

import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RxActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = RxActivity.class.getSimpleName();
    private Context mContext = RxActivity.this;

    private Messenger serviceMessenger;
    private Button bt, btnTest;
    private TextView tv;
    private String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        bt = findViewById(R.id.bt_msg);
        btnTest = findViewById(R.id.btn_test);
        bt.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        tv = findViewById(R.id.tv_msg);
//        rxTest();
    }

    private void getWeather() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
//        Observable<WeatherInfoBean> observable = service.getWeatherById(Constants.XIAMEN_ID, Constants.KEY);
        Observable<WeatherInfoBean> observable = service.getWeatherById(Constants.XIAMEN_ID, Constants.UNIT, Constants.KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherInfoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(WeatherInfoBean weatherInfoBean) {
                        Log.i(TAG, "onNext: " + weatherInfoBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });
    }

    private void weatherTask() {
        Completable observable = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                getWeather();
                emitter.onComplete();
            }
        });
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                        tv.setText(res);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private void rxTest() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("One");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext");
                Toast.makeText(mContext, "Test: " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_msg:
//                getWeather();
//                weatherTask();
                Observable<WeatherInfoBean> observable = WeatherUtils.getInstance(this).getWeather();
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<WeatherInfoBean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(WeatherInfoBean weatherInfoBean) {
                                tv.setText("T: " + weatherInfoBean.getMain().getTemp());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
            case R.id.btn_test:
                Observable<WeatherInfoBean> observable2 = WeatherUtils.getInstance(this).getWeatherById();
                observable2.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherInfoBean>() {
                            @Override
                            public void accept(WeatherInfoBean weatherInfoBean) throws Exception {
                                tv.setText("T: " + weatherInfoBean.getMain().getTemp());
                            }
                        });
//                Observable<WeatherInfoBean> observable1 = WeatherUtils.getInstance(this).getWeatherById();
//                observable1.subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<WeatherInfoBean>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(WeatherInfoBean weatherInfoBean) {
//                                tv.setText("T: " + weatherInfoBean.getMain().getTemp());
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            default:
                break;
        }
    }
}
