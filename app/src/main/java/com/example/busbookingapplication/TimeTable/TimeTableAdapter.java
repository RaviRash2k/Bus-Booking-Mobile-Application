package com.example.busbookingapplication.TimeTable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.UpdateTimeTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.MainViewHolder>{

    Context context;
    ArrayList<TimeTableModel> list;

    public TimeTableAdapter(Context context, ArrayList<TimeTableModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TimeTableAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.timetable_card, parent, false);
        return new TimeTableAdapter.MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableAdapter.MainViewHolder holder, int position) {
        TimeTableModel model = list.get(position);

        // Set text view details
        holder.busRoute.setText(String.format("%s - %s", model.getStartLocation(), model.getEndLocation()));
        holder.bus.setText(model.getBusNo());
        holder.timeTableDay.setText(model.getDate());
        holder.timeTableStart.setText(model.getStartTime());
        holder.timeTableEnd.setText(model.getEndTime());
        holder.ticketPrice.setText(model.getTicketPrice());

        //get user type
        SharedPreferences prf = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String currentType = prf.getString("type", "");

        //set button visibility
        if(currentType.equals("routeManager")){
            holder.deleteTimeSlot.setVisibility(View.VISIBLE);
            holder.bookTicket.setVisibility(View.GONE);

        } else if (currentType.equals("customer")) {
            holder.bookTicket.setVisibility(View.VISIBLE);
            holder.deleteTimeSlot.setVisibility(View.GONE);
        }

        //click delete button
        holder.deleteTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);  // Use the context directly
                builder.setMessage("Are you sure you want to delete this time slot?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ProgressDialog progressDialog = new ProgressDialog(context);  // Use the correct context
                        progressDialog.setMessage("Deleting time slot...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        //delete slot
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Route");
                        databaseReference.child(model.getStartLocation()).child(model.getSlotID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Entry deleted successfully");
                                } else {
                                    Log.d("Firebase", "Entry deletion failed");
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView timeTableDay, timeTableStart, timeTableEnd, ticketPrice, busRoute, bus;
        Button deleteTimeSlot, bookTicket;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTableDay = itemView.findViewById(R.id.timeTableDay);
            timeTableStart = itemView.findViewById(R.id.timeTableStart);
            timeTableEnd = itemView.findViewById(R.id.timeTableEnd);
            ticketPrice = itemView.findViewById(R.id.ticketPrice);
            busRoute = itemView.findViewById(R.id.busRoute);
            bus = itemView.findViewById(R.id.bus);
            deleteTimeSlot = itemView.findViewById(R.id.deleteTimeSlot);
            bookTicket = itemView.findViewById(R.id.bookTicket);
        }
    }
}

