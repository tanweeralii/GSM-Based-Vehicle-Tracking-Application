package com.scelon.vehicletracking.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.scelon.vehicletracking.OtherActivties.SignInActivity;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.TinyDB;

public class SplashActivity extends AppCompatActivity {
    private Context context;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        tinyDB = new TinyDB(context);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tinyDB.getInt(Constants.LOGIN_FLAG) == 1 && !tinyDB.getString(Constants.USER_MOBILE_NO).equals("")) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(context, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);

    }
}