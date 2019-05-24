package com.example.meiya.mytimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.hanks.htextview.evaporate.EvaporateTextView;
import com.john.waveview.WaveView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = TimerActivity.class.getSimpleName();

    private long start;
    private long end;
    private long duration;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private EvaporateTextView etvPercentage;
    private TextView tvWeather;
    private ImageView ivWeather;
    private WaveView waveView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabBlue, fabRed, fabCyan, fabGreen, fabYellow, fabGray;
    private Timer mTimer;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.dl_left);
        navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        etvPercentage = findViewById(R.id.etv_percentage);
        tvWeather = findViewById(R.id.tv_weather);
        ivWeather = findViewById(R.id.iv_weather);
        waveView = findViewById(R.id.wave_view);
        setBgColor();
        fabMenu = findViewById(R.id.menu);
        fabMenu.setClosedOnTouchOutside(true);
        fabBlue = findViewById(R.id.fab_blue);
        fabRed = findViewById(R.id.fab_red);
        fabCyan = findViewById(R.id.fab_cyan);
        fabGreen = findViewById(R.id.fab_green);
        fabYellow = findViewById(R.id.fab_yellow);
        fabGray = findViewById(R.id.fab_gray);
        fabBlue.setOnClickListener(this);
        fabRed.setOnClickListener(this);
        fabCyan.setOnClickListener(this);
        fabGreen.setOnClickListener(this);
        fabYellow.setOnClickListener(this);
        fabGray.setOnClickListener(this);
    }

    private void initWeatherInfo() {
        Observable<WeatherInfoBean> observable = WeatherUtils.getInstance(this).getWeatherById();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherInfoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(WeatherInfoBean weatherInfoBean) {
                        Log.i(TAG, "onSubscribe");
                        tvWeather.setText(weatherInfoBean.getMain().getTemp() + " \u2103");
                        setWeatherImg(weatherInfoBean.getWeather().get(0).getIcon());
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        initWeatherInfo();
        getStartAndEndTime();
        if (mTimer == null) {
            mTimer = new Timer();
        }
        getPercentage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mTimer.cancel();
        mTimer = null;
    }

    private void setWeatherImg(String icon) {
        int id = R.drawable.sunny;
        switch (icon) {
            case "01d":
                id = R.drawable.sunny;
                break;
            case "01n":
                id = R.drawable.clearly_night;
                break;
            case "02d":
            case "02n":
                id = R.drawable.partly_cloudy;
                break;
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                id = R.drawable.overcast;
                break;
            case "09d":
            case "09n":
                id = R.drawable.heavy_rain;
                break;
            case "10d":
            case "10n":
                id = R.drawable.light_rain;
                break;
            case "13d":
            case "13n":
                id = R.drawable.snow;
                break;
            case "50d":
                id = R.drawable.foggy;
                break;
            case "50n":
                id = R.drawable.foggy_night;
                break;
            default:
                break;
        }
        ivWeather.setImageResource(id);
    }

    private void getStartAndEndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CURRENT_DATE_PATTERN, Locale.getDefault());
        date = dateFormat.format(new Date());
        String startTimeString = date + " " + SpUtils.getStartTime(TimerActivity.this);
        String endTimeString = date + " " + SpUtils.getEndTime(TimerActivity.this);
        SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.CURRENT_TIME_PATTERN, Locale.getDefault());
        try {
            start = timeFormat.parse(startTimeString).getTime();
            end = timeFormat.parse(endTimeString).getTime();
            duration = end - start;
            Log.i(TAG, "start: " + start + "; end: " + end + "; duration: " + duration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getPercentage() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final long currentTime = System.currentTimeMillis();
                        final String percentage;
                        DecimalFormat decimalFormat = new DecimalFormat("#.000000");
                        if (currentTime < start) {
                            percentage = "Not yet...";
                        } else if (currentTime > end) {
                            percentage = "Off already...";
                        } else {
                            percentage = String.valueOf(decimalFormat.format((currentTime - start)/(float) duration * 100)) + "%";
                        }
                        TimerActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etvPercentage.animateText(percentage);
                                waveView.setProgress((int) ((currentTime - start) * 100 / duration));
                            }
                        });
                    }
                }).start();
            }
        }, 0, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_blue:
                waveView.setBackgroundColor(getResources().getColor(R.color.blue_normal));
                SpUtils.setBgColor(TimerActivity.this, SpUtils.BG_BLUE);
                break;
            case R.id.fab_red:
                waveView.setBackgroundColor(getResources().getColor(R.color.red_normal));
                SpUtils.setBgColor(TimerActivity.this, SpUtils.BG_RED);
                break;
            case R.id.fab_cyan:
                waveView.setBackgroundColor(getResources().getColor(R.color.cyan_normal));
                SpUtils.setBgColor(TimerActivity.this, SpUtils.BG_CYAN);
                break;
            case R.id.fab_green:
                waveView.setBackgroundColor(getResources().getColor(R.color.green_normal));
                SpUtils.setBgColor(TimerActivity.this, SpUtils.BG_GREEN);
                break;
            case R.id.fab_yellow:
                waveView.setBackgroundColor(getResources().getColor(R.color.yellow_normal));
                SpUtils.setBgColor(TimerActivity.this, SpUtils.BG_YELLOW);
                break;
            case R.id.fab_gray:
                waveView.setBackgroundColor(getResources().getColor(R.color.gray_normal));
                SpUtils.setBgColor(TimerActivity.this, SpUtils.BG_GRAY);
                break;
        }
    }

    private void setBgColor() {
        int bgColor = SpUtils.getBgColor(TimerActivity.this);
        switch (bgColor) {
            case SpUtils.BG_BLUE:
                waveView.setBackgroundColor(getResources().getColor(R.color.blue_normal));
                break;
            case SpUtils.BG_RED:
                waveView.setBackgroundColor(getResources().getColor(R.color.red_normal));
                break;
            case SpUtils.BG_CYAN:
                waveView.setBackgroundColor(getResources().getColor(R.color.cyan_normal));
                break;
            case SpUtils.BG_GREEN:
                waveView.setBackgroundColor(getResources().getColor(R.color.green_normal));
                break;
            case SpUtils.BG_YELLOW:
                waveView.setBackgroundColor(getResources().getColor(R.color.yellow_normal));
                break;
            case SpUtils.BG_GRAY:
                waveView.setBackgroundColor(getResources().getColor(R.color.gray_normal));
                break;
            default:
                waveView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_time_period:
                Log.i(TAG, "menu_item_time_period");
                Intent intent = new Intent(TimerActivity.this, WorkingPeriodSettingActivity.class);
                intent.putExtra(Constants.DATE, date);
                TimerActivity.this.startActivity(intent);
                break;
            case R.id.menu_item_rx:
                Intent rxintent = new Intent(TimerActivity.this, RxActivity.class);
                TimerActivity.this.startActivity(rxintent);
            case R.id.menu_item_about:
                Log.i(TAG, "menu_item_about");
                break;
        }
        return false;
    }
}
