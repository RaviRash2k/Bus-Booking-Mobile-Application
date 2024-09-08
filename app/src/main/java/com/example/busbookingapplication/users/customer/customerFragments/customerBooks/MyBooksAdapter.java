package com.example.busbookingapplication.users.customer.customerFragments.customerBooks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.MainViewHolder>{

    Context context;
    ArrayList<MyBooksModel> list;

    public MyBooksAdapter(Context context, ArrayList<MyBooksModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyBooksAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mybook_card, parent, false);
        return new MyBooksAdapter.MainViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyBooksAdapter.MainViewHolder holder, int position) {
        MyBooksModel model = list.get(position);

        // Set text view details
        holder.route.setText(model.getRoute());
        holder.ticketID.setText(model.getTicketID());
        holder.bus.setText(model.getBus());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.price.setText(model.getPrice() + " LKR");
        holder.seats.setText(model.getSeats());
        holder.turnID.setText(model.getTurnID());

        if("traveled".equals(model.getStatus())){
            holder.canselTicket.setVisibility(View.GONE);
        } else if ("pending".equals(model.getStatus())) {
            holder.canselTicket.setVisibility(View.VISIBLE);
        }

        //click delete button
        holder.canselTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to cansel?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Cansel booking...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Booked Tickets");
                        databaseReference.child(model.getRoute()).child(model.getTicketID()).child("Status").setValue("canceled").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
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

        TextView route, ticketID, date, time, price, bus, seats, turnID;
        Button canselTicket;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            route = itemView.findViewById(R.id.route);
            ticketID = itemView.findViewById(R.id.ticketID);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            price = itemView.findViewById(R.id.price);
            bus = itemView.findViewById(R.id.bus);
            seats = itemView.findViewById(R.id.seats);
            turnID = itemView.findViewById(R.id.turnID);
            canselTicket = itemView.findViewById(R.id.canselTicket);
        }
    }
}
