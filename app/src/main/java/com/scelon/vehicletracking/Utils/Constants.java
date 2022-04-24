package com.scelon.vehicletracking.Utils;

import android.graphics.Color;

public class Constants
{

    public static final String FIREBASE_REFERENCE = "https://vehicle-tracking-system-346511-default-rtdb.firebaseio.com/";
    public static final String LOGIN_FLAG = "LOGIN_FLAG";

    public static String USER_NAME = "user_name";
    public static String USER_MOBILE_NO = "user_mobile_no";
    public static String USER_EMAIL = "user_email";

    public static String VEHICLE_NO = "vehicle_no";
    public static String LATITUDE = "lat";
    public static String LONGITUDE = "long";

    ////////Session Constants

    public static final String CAMP_IMAGE = "Camp Image";
    public static final String PROFILE_IMAGE = "Profile Image";
    public static final String DEMO_PHONE_NO = "0000000119";

    public static String deviceToken = "deviceToken";
    public static String notifications = "notifications";
    public static String notifications_channels = "notifications_channels";
    public static String tones = "tones";
    public static String ringtone = "ringtone";
    public static String vibration = "vibration";
    public static String vibrationVC = "vibrationVC";
    public static String[] vibrationModes = {
            "Deactivated", "Standard", "Short", "Long"
    };
    public static long[] STANDARD_VIBRATION_PATTERN = {1000 , 1000};
    public static long[] SHORT_VIBRATION_PATTERN = {500 , 500};
    public static long[] LONG_VIBRATION_PATTERN = {1500 , 2500};
    public static long[] VIBRATION_NULL = {0};

    public static String light = "light";
    public static String lightDeactivated = "Deactivated";
    public static String[] lightModes = {
            "Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Violet", "Pink", "White"
    };

    public static String E_KYC = "https://mycams.camsonline.com/FreshPurchase/MCFreshPurchase?0ayU8DerqG8GbCggVICnLC8o7bAEBAsuK3HjmNCb9HQ=";
    public static String AMC = "";
    public static String HOME = "";
    public static String CALCULATOR = "";

    public static Integer[] lightModesValues = {
            Color.parseColor("#E24A4E"), // Red
            Color.parseColor("#F39218"), // Orange
            Color.parseColor("#F8CD37"), // Yellow
            Color.parseColor("#80CB6E"), // Green
            Color.parseColor("#55E5E9"), // Cyan
            Color.parseColor("#5ABAED"), // Blue
            Color.parseColor("#CA83E4"), // Violet
            Color.parseColor("#EC5A95"), // Pink
            Color.parseColor("#C0C0C0") // White
    };
}
