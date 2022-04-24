package com.scelon.vehicletracking.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.scelon.vehicletracking.Adapter.VehicleItemAdapter;
import com.scelon.vehicletracking.Classes.MainLoader;
import com.scelon.vehicletracking.Model.VehicleModel;
import com.scelon.vehicletracking.OtherActivties.AddVehicleActivity;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.ImpMethods;
import com.scelon.vehicletracking.Utils.Regex;
import com.scelon.vehicletracking.Utils.TinyDB;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Context context;
    private TinyDB tinyDB;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initViews();
        getData();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , AddVehicleActivity.class);
                intent.putExtra("type" , 1);
                startActivity(intent);
            }
        });
        findViewById(R.id.refreshLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    private void getData() {
        MainLoader.Loader(true , findViewById(R.id.LL_loader));
        MainLoader.Loader(false , findViewById(R.id.notFound));
        MainLoader.Loader(false , findViewById(R.id.somethingWrong));

        FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(tinyDB.getString(Constants.USER_MOBILE_NO)).child("Vehicle").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                MainLoader.Loader(false , findViewById(R.id.LL_loader));

                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        ArrayList<VehicleModel> vehicleModels = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if (dataSnapshot.exists()) {
                                vehicleModels.add(new VehicleModel(dataSnapshot.child(Constants.VEHICLE_NO).getValue().toString(),
                                        dataSnapshot.child(Constants.LATITUDE).getValue().toString() ,
                                        dataSnapshot.child(Constants.LONGITUDE).getValue().toString()));
                            }
                        }
                        recyclerView.setAdapter(new VehicleItemAdapter(vehicleModels , context));
                    } else {
                        Toast.makeText(context, "No Data Found!", Toast.LENGTH_SHORT).show();
                        MainLoader.Loader(true , findViewById(R.id.notFound));
                    }
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                MainLoader.Loader(false , findViewById(R.id.LL_loader));
                MainLoader.Loader(true , findViewById(R.id.somethingWrong));
                Log.d(TAG, "onCanceled: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MainLoader.Loader(false , findViewById(R.id.LL_loader));
                MainLoader.Loader(true , findViewById(R.id.somethingWrong));
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh)
        {
            getData();
            return true;
        }else
        {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu , menu);
        return true;
    }

    private void initViews() {
        tinyDB = new TinyDB(context);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Vehicle Tracking");
        toolbar.setNavigationIcon(null);
        recyclerView = findViewById(R.id.recyclerView);
        btn_add = findViewById(R.id.btn_add);
    }
}