package com.scelon.vehicletracking.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.scelon.vehicletracking.R;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SuppressLint("SimpleDateFormat")
public class ImpMethods {

    private static final String TAG = "ImpMethods";

    public static String getMediaStoreRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.d(TAG, "getMediaStoreRealPathFromURI: " + cursor.getString(column_index));
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean isAppOnForeground(Context context) {
        boolean ret = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    public static String getMainAppFolder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Environment.DIRECTORY_DCIM + File.separator + context.getString(R.string.real_app_name);
        } else {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), File.separator + context.getString(R.string.real_app_name));
            if (!root.exists()) {
                root.mkdir();
            }
            return root.getAbsolutePath();
        }
    }

    public static View getViewFromContext(Context context) {
        return ((Activity) context).getWindow().getDecorView().getRootView();
    }

    public static Boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
            return networkInfo != null && networkInfo.isConnected();
        } else {
            return false;
        }
    }

    public static void setClipboard(Context context, String text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static void showErrorSnackbar(Context context, View v, String str) {
        Snackbar snack = Snackbar.make(v, str, Snackbar.LENGTH_LONG);
        TextView tv = (TextView) (snack.getView()).findViewById(R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "manrope_medium.ttf");
        tv.setTypeface(font);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.setMargins(0, ImpMethods.DimensToPx(R.dimen._52sdp, context), 0, 0);
        view.setLayoutParams(params);
        snack.getView().setBackgroundColor(context.getResources().getColor(R.color.ErrorMsg));
        snack.show();
    }

    public static void oneTimeClickCountdownMenu(MenuItem menuItem) {
        menuItem.setEnabled(false);
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                menuItem.setEnabled(true);
            }
        }.start();
    }

    public static void oneTimeClickCountdown(View menuItem) {
        menuItem.setEnabled(false);
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                menuItem.setEnabled(true);
            }
        }.start();
    }

    public static ArrayList<String> getStringFilter(String toFilter, ArrayList<String> strings) {
        ArrayList<String> FilteredArray = new ArrayList<>();

        // perform your search here using the searchConstraint String.
        for (int i = 0; i < strings.size(); i++) {
            String data = strings.get(i);
            if (data.toLowerCase().startsWith(toFilter) || data.toLowerCase().contains(toFilter)) {
                FilteredArray.add(data);
            } else if (data.toUpperCase().startsWith(toFilter) || data.toUpperCase().contains(toFilter)) {
                FilteredArray.add(data);
            }
        }
        return FilteredArray;
    }

    public static void closeSoftKeyboard(Context context) {
        ((InputMethodManager) Objects.requireNonNull(context).getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getRootView().getApplicationWindowToken(), 0);
    }

    public static int DimensToPx(int dimen, Context context) {
        Log.d(TAG, "DimensToPx: " + (int) (context.getResources().getDimension(dimen)));
        return (int) (context.getResources().getDimension(dimen));
    }

    public static boolean isMobileNoValid(String mobile) {
        return mobile.matches("\\d{10}");
    }

    @SuppressLint("MissingPermission")
    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceId = mTelephony.getImei();
                } else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", "   " + deviceId);
        return deviceId;
    }


    public static String getDateFromMillies(long milliSeconds, String formate) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(formate);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String ChangeDateFormat(String inputFormat, String outputFormat, String data) {
        DateFormat input = new SimpleDateFormat(inputFormat);
        DateFormat output = new SimpleDateFormat(outputFormat);
        Date date = null;
        try {
            date = input.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("parseError", "ChangeDateFormate: " + e.toString());
        }
        return output.format(date);
    }

    public static String getDateMMMFromMillies(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String ChangeTimeFormate(String inputDateStr) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat("hh:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        Date date = inputFormat.parse(inputDateStr);
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

    public static long getTimeInMillies(String inputFormate, String data) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(inputFormate); // I assume d-M, you may refer to M-d for month-day instead.
            Date date = null; // You will need try/catch around this

            date = formatter.parse(data);
            //assert date != null;
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "getTimeInMillies: " + e.toString());
            return 0;
        }
    }

    public static ArrayList<String> convertIntoArrayList(String[] strings) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (String s : strings) {
            stringArrayList.add(s);
        }
        return stringArrayList;
    }

    public static void improveWebViewPerformance(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
    }
}
