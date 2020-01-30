package com.ux.ok_baby.utils;

import com.ux.ok_baby.R;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int FOOD_TAB = 1;
    public static final int OTHER_TAB = 3;
    public static final int SLEEP_TAB = 0;
    public static final String POO = "Poo";
    public static final String PEE = "Pee";
    public static final int DIAPER_TAB = 2;
    public static final String FOOD = "Food";
    public static final String LEFT = "Left";
    public static final String OTHER = "Other";
    public static final String RIGHT = "Right";
    public static final String SLEEP = "Sleep";
    public static final String BOTTLE = "Bottle";
    public static final String DIAPER = "Diaper";
    public static final String BABY_ID = "baby id";
    public static final int START_BABY_PROF_ACT = 1;
    public static final String TIME_PATTERN = "HH:mm";
    public static final String USER_ID_TAG = "userID";
    public static final int START_BABY_PROF_EDIT_ACT = 2;
    public static final String DATE_PATTERN = "dd/MM/yy";
    public static final String REPORT_TYPE = "report type";
    public static final String IS_NEW_USER_TAG = "isNewUser";
    public static final String BABY_OBJECT_TAG = "BabyObject";
    public static final String BREASTFEEDING = "Breastfeeding";


    public enum ReportType {FOOD, SLEEP, DIAPER, OTHER}
    public static Map<Integer, String> POO_COLORS = new HashMap<Integer, String>() {{
        put(R.color.black, "Black");
        put(R.color.red, "Red");
        put(R.color.brown, "Brown");
    }};
}