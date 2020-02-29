package com.ux.ok_baby.utils;

import android.graphics.Color;

import com.ux.ok_baby.R;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String POO = "Poo";
    public static final String PEE = "Pee";
    public static final String BOTTLE = "Bottle";
    public static final String BREASTFEEDING = "Breastfeeding";
    public static final String BABY_ID = "baby id";
    public static final int START_BABY_PROF_ACT = 1;
    public static final String USER_ID_TAG = "userID";
    public static final String TIME_PATTERN = "HH:mm";
    public static final int START_ADD_BABY_PROF_ACT = 3;
    public static final int START_EDIT_BABY_PROF_ACT = 2;
    public static final String DATE_PATTERN = "dd/MM/yy";
    public static final String REPORT_TYPE = "report type";
    public static final String IS_NEW_USER_TAG = "isNewUser";
    public static final String BABY_OBJECT_TAG = "BabyObject";
    public static final String OLD_MAIN_BABY_OBJECT_TAG = "OldMainBabyObject";


    public enum ReportType {FOOD, SLEEP, DIAPER, OTHER}

    public enum FoodType {BREASTFEED, BOTTLE}

    public static Map<Integer, String> POO_COLORS = new HashMap<Integer, String>() {{
        put(R.color.black, "Black");
        put(R.color.red, "Red");
        put(R.color.brown, "Brown");
    }};

    public static Map<String, Integer> POO_COLORS1 = new HashMap<String, Integer>() {{
        put("Black", Color.BLACK);
        put("Red", Color.RED);
        put("Brown", Color.parseColor("#FF642C26"));
    }};
}