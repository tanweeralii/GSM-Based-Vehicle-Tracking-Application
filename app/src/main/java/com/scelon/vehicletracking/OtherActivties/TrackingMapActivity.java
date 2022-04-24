package com.scelon.vehicletracking.OtherActivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scelon.vehicletracking.Classes.MainLoader;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.TinyDB;

public class TrackingMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "AddAddressActivity";
    private Context context;
    private TinyDB tinyDB;
    private Toolbar toolbar;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private LocationManager locationManager;

    private String vehicleNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_map);
        context = this;
        tinyDB = new TinyDB(context);
        vehicleNo = getIntent().getStringExtra("vehicleNo");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(vehicleNo);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(tinyDB.getString(Constants.USER_MOBILE_NO)).child("Vehicle").child(vehicleNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MainLoader.Loader(false , findViewById(R.id.somethingWrong));
                if (snapshot.exists() || snapshot.hasChildren()) {
                    String lat = snapshot.child(Constants.LATITUDE).getValue().toString();
                    String lng = snapshot.child(Constants.LONGITUDE).getValue().toString();
                    LatLng latLng = new LatLng(Double.parseDouble(lat) , Double.parseDouble(lng));
                    if (mMap != null){
                        mMap.addMarker(new MarkerOptions().position(latLng)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MainLoader.Loader(true , findViewById(R.id.somethingWrong));
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

        findViewById(R.id.refreshLayout).setVisibility(View.GONE);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}