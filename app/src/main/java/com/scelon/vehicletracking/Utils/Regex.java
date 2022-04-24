package com.scelon.vehicletracking.Utils;

public class Regex
{
    public static final String VEHICLE_NO = "^[A-Z|a-z]{2}\\s?[0-9]{1,2}\\s?[A-Z|a-z]{0,3}\\s?[0-9]{4}$";
    public static final String GSTIN = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";
    public static final String YOUTUBE_URL = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(.com)?/.+";
}
