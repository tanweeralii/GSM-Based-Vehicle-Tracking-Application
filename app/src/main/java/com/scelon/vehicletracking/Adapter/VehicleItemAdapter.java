package com.scelon.vehicletracking.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.scelon.vehicletracking.Activities.MainActivity;
import com.scelon.vehicletracking.Model.VehicleModel;
import com.scelon.vehicletracking.OtherActivties.AddVehicleActivity;
import com.scelon.vehicletracking.OtherActivties.TrackingMapActivity;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.ImpMethods;
import com.scelon.vehicletracking.Utils.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VehicleItemAdapter extends RecyclerView.Adapter<VehicleItemAdapter.ViewHolder> {
    private static final String TAG = "VehicleItemAdapter";
    private ArrayList<VehicleModel> vehicleModels = new ArrayList<>();
    private Context context;

    public VehicleItemAdapter(ArrayList<VehicleModel> vehicleModels, Context context) {
        this.vehicleModels = vehicleModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(vehicleModels.get(position).vehicleNo);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vehicleModels.get(position).lat.equals("") || vehicleModels.get(position).lng.equals("")){
                    Toast.makeText(context, "Tracking is not enabled for this vehicle for now!", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(context, TrackingMapActivity.class);
                    intent.putExtra("vehicleNo", vehicleModels.get(position).vehicleNo);
                    context.startActivity(intent);
                }
            }
        });
        holder.ib_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.getAdapterPosition() , v);
            }
        });
    }

    private void showPopupMenu(int position , View view) {
        PopupMenu popupMenu = new PopupMenu((Activity) context, view);
        popupMenu.getMenu().add("Edit");
        popupMenu.getMenu().add("Delete");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (""+item.getTitle()) {
                    case "Edit":
                        Intent intent = new Intent(context , AddVehicleActivity.class);
                        intent.putExtra("type" , 2);
                        intent.putExtra("vehicleNo" , vehicleModels.get(position).vehicleNo);
                        context.startActivity(intent);
                        return true;
                    case "Delete":
                        deleteVehicle(vehicleModels.get(position).vehicleNo , position);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void deleteVehicle(String vehicleNo , int position) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.VEHICLE_NO, vehicleNo);

        FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(new TinyDB(context).getString(Constants.USER_MOBILE_NO)).child("Vehicle").child(vehicleNo).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Deleted Successful!", Toast.LENGTH_SHORT).show();
                    for(int i = 0 ; vehicleModels.size() > i ; i++){
                        if(vehicleModels.get(i).equals(vehicleNo)){
                            vehicleModels.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                        }
                    }
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

    @Override
    public int getItemCount() {
        return vehicleModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageButton ib_more;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            ib_more = itemView.findViewById(R.id.ib_more);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
