package com.example.meiya.mytimer;

import android.content.Context;
import android.content.SharedPreferences;

class SpUtils {

    static final int BG_BLUE = 1;
    static final int BG_RED = 2;
    static final int BG_CYAN = 3;
    static final int BG_GREEN = 4;
    static final int BG_YELLOW = 5;
    static final int BG_GRAY = 6;

    private static final String SP_NAME = "MyTimerSP";
    private static final String BG_COLOR = "bg_color";
    private static final String START_TIME = "start_time";
    private static final String DEFAULT_START_TIME = "8:40:00";
    private static final String END_TIME = "end_time";
    private static final String DEFAULT_END_TIME = "17:40:00";

    static int getBgColor(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(BG_COLOR, 0);
    }

    static void setBgColor(Context context, int color) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(BG_COLOR, color);
        editor.apply();
    }

    static String getStartTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(START_TIME, DEFAULT_START_TIME);
    }

    static  void setStartTime(Context context, String start) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(START_TIME, start);
        editor.apply();
    }

    static String getEndTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(END_TIME, DEFAULT_END_TIME);
    }

    static  void setEndTime(Context context, String end) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(END_TIME, end);
        editor.apply();
    }
}
