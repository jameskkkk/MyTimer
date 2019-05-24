package com.example.meiya.mytimer;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.john.waveview.WaveView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WorkingPeriodSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private TextView tvStartTime, tvEndTime;
    private WaveView wvTop, wvBottom;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        date = getIntent().getStringExtra(Constants.DATE);
        mContext = WorkingPeriodSettingActivity.this;
        setContentView(R.layout.activity_working_period_setting);
        wvTop = findViewById(R.id.wv_top);
        wvTop.setProgress(50);
        setTopBGColor(SpUtils.getBgColor(mContext));
        wvBottom = findViewById(R.id.wv_bottom);
        wvBottom.setProgress(50);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvStartTime.setOnClickListener(this);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvEndTime.setOnClickListener(this);
        initData();
    }

    private void initData() {
        tvStartTime.setText(SpUtils.getStartTime(mContext));
        tvEndTime.setText(SpUtils.getEndTime(mContext));
    }

    private void setTopBGColor(int color) {
        switch (color) {
            case SpUtils.BG_BLUE:
                wvTop.setBackgroundColor(getResources().getColor(R.color.blue_normal));
                break;
            case SpUtils.BG_RED:
                wvTop.setBackgroundColor(getResources().getColor(R.color.red_normal));
                break;
            case SpUtils.BG_CYAN:
                wvTop.setBackgroundColor(getResources().getColor(R.color.cyan_normal));
                break;
            case SpUtils.BG_GREEN:
                wvTop.setBackgroundColor(getResources().getColor(R.color.green_normal));
                break;
            case SpUtils.BG_YELLOW:
                wvTop.setBackgroundColor(getResources().getColor(R.color.yellow_normal));
                break;
            case SpUtils.BG_GRAY:
                wvTop.setBackgroundColor(getResources().getColor(R.color.gray_normal));
                break;
            default:
                wvTop.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time:
                showTimePicker(true);
                break;
            case R.id.tv_end_time:
                showTimePicker(false);
                break;
        }
    }

    private void showTimePicker(final boolean start) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minStr = String.valueOf(minute);
                if (minute < 9) {
                    minStr = "0" + minStr;
                }
                String time = hourOfDay + ":" + minStr + ":00";
                if (checkTime(start, time)) {
                    if (start) {
                        SpUtils.setStartTime(mContext, time);
                        tvStartTime.setText(time);
                    } else {
                        SpUtils.setEndTime(mContext, time);
                        tvEndTime.setText(time);
                    }
                } else {
                    Toast.makeText(mContext, "start time must be earlier than end time", Toast.LENGTH_SHORT).show();
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private boolean checkTime(boolean start, String time) {
        long startTime = 0;
        long endTime = 0;
        SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.CURRENT_TIME_PATTERN, Locale.getDefault());
        try {
            if (start) {
                startTime = timeFormat.parse(date + " " + time).getTime();
                endTime = timeFormat.parse(date + " " + SpUtils.getEndTime(mContext)).getTime();
            } else {
                startTime = timeFormat.parse(date + " " + SpUtils.getStartTime(mContext)).getTime();
                endTime = timeFormat.parse(date + " " + time).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startTime < endTime;
    }
}
