package com.scelon.vehicletracking.OtherActivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.scelon.vehicletracking.Activities.MainActivity;
import com.scelon.vehicletracking.Classes.MobileVerification;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.AuthEncrypter;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.ImpMethods;
import com.scelon.vehicletracking.Utils.Regex;
import com.scelon.vehicletracking.Utils.TinyDB;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {
    public static final String TAG = "AddVehicleActivity";
    private Context context;
    private TinyDB tinyDB;

    private TextInputEditText et_vehicleNo;
    private Button btn_add;
    private TextView title;

    private String vehicleNo = "";

    private int type = 0;
    ///type
    ///1 = Add
    ///2 = Edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        initViews();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImpMethods.closeSoftKeyboard(context);
                String mVehicleNo = et_vehicleNo.getText().toString().trim();
                verify(mVehicleNo);
            }
        });
    }

    private void setVehicleNo(String vehicleNo) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.VEHICLE_NO, vehicleNo);
        data.put(Constants.LATITUDE, "");
        data.put(Constants.LONGITUDE, "");

        FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(tinyDB.getString(Constants.USER_MOBILE_NO)).child("Vehicle").child(vehicleNo).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                dialog.dismiss();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                dialog.dismiss();
                Log.d(TAG, "onCanceled: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d(TAG, "onFailure: " + e.toString());
                Snackbar.make(ImpMethods.getViewFromContext(context), "Network Problem! Please try again.", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void verify(String vehicleNo) {

        if (!vehicleNo.matches(Regex.VEHICLE_NO)) {
            Toast.makeText(context, "Invalid Vehicle No.", Toast.LENGTH_SHORT).show();
        } else {

            ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage("Verifying...");
            dialog.setCancelable(false);
            dialog.show();

            FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(tinyDB.getString(Constants.USER_MOBILE_NO)).child("Vehicle").child(vehicleNo).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    dialog.dismiss();

                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            Toast.makeText(context, "Vehicle No. already registered!", Toast.LENGTH_SHORT).show();
                        } else {
                            setVehicleNo(vehicleNo);
                        }
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    dialog.dismiss();
                    Log.d(TAG, "onCanceled: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Log.d(TAG, "onFailure: " + e.toString());
                }
            });
        }
    }

    private void initViews() {
        context = this;
        tinyDB = new TinyDB(context);
        et_vehicleNo = findViewById(R.id.et_vehicleNo);
        btn_add = findViewById(R.id.btn_add);
        title = findViewById(R.id.title);
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            title.setText("Add Vehicle");
            btn_add.setText("Register");
        } else if (type == 2) {
            title.setText("Edit Vehicle");
            title.setText("Update");
            vehicleNo = getIntent().getStringExtra("vehicleNo");
            et_vehicleNo.setText(vehicleNo);
        }
    }
}