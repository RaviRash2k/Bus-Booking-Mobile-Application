package com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.ViewTicket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busbookingapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewTickets extends AppCompatActivity {

    EditText enterTicket;
    Button viewTicket, removeAvlOfTicket;
    LinearLayout ticket;
    TextView date, time, bus, nic, price, busRoute, seats, noTicket;
    String x, y ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_tickets);

        enterTicket = findViewById(R.id.enterTicket);
        viewTicket = findViewById(R.id.viewTicket);

        removeAvlOfTicket = findViewById(R.id.deleteTimeSlot);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        bus = findViewById(R.id.bus);
        busRoute = findViewById(R.id.busRoute);
        nic = findViewById(R.id.nic);
        price = findViewById(R.id.price);
        seats = findViewById(R.id.seats);

        ticket = findViewById(R.id.ticket);

        noTicket = findViewById(R.id.noTicket);

        viewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTicket();
            }
        });

        removeAvlOfTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unAvailableTicket();
            }
        });
    }

    public void searchTicket() {
        String ticketID = enterTicket.getText().toString();

        if(ticketID.isEmpty()) {

            ticket.setVisibility(View.GONE);
            Toast.makeText(ViewTickets.this, "Please enter ticket ID", Toast.LENGTH_SHORT).show();

        } else {

            ProgressDialog progressDialog = new ProgressDialog(ViewTickets.this);
            progressDialog.setMessage("Searching ticket...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Booked Tickets");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    boolean ticketFound = false;

                    for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {

                        String routeName = routeSnapshot.getKey();

                        for (DataSnapshot ticketSnapshot : routeSnapshot.getChildren()) {

                            String ticketKey = ticketSnapshot.getKey();
                            String status = ticketSnapshot.child("Status").getValue(String.class);

                            assert ticketKey != null;
                            assert status != null;

                            if (ticketKey.equals(ticketID) && status.equals("pending")) {

                                ticketFound = true;

                                ticket.setVisibility(View.VISIBLE);
                                noTicket.setVisibility(View.GONE);

                                x = routeName;
                                y = ticketKey;

                                String busT = ticketSnapshot.child("Bus").getValue(String.class);
                                String dateT = ticketSnapshot.child("Date").getValue(String.class);
                                String timeT = ticketSnapshot.child("Time").getValue(String.class);
                                String priceT = ticketSnapshot.child("Price").getValue(String.class);
                                String nicT = ticketSnapshot.child("Traveler NIC").getValue(String.class);

                                busRoute.setText(routeName);
                                date.setText(String.format("Date- %s", dateT));
                                time.setText(String.format("Time- %s", timeT));
                                bus.setText(busT);
                                price.setText(String.format("Price- %s LKR", priceT));
                                nic.setText(String.format("NIC- %s", nicT));

                                StringBuilder seatsInfo = new StringBuilder();

                                DataSnapshot seatsSnapshot = ticketSnapshot.child("Seats");
                                long seatCount = seatsSnapshot.getChildrenCount();
                                long index = 0;

                                for (DataSnapshot seatSnapshot : seatsSnapshot.getChildren()) {
                                    String seatInfo = seatSnapshot.getValue(String.class);

                                    seatsInfo.append(seatInfo);

                                    if (++index < seatCount) {
                                        seatsInfo.append(", ");
                                    }
                                }
                                seats.setText(String.format("Seats- %s", seatsInfo.toString()));
                            }
                        }
                    }

                    if (!ticketFound) {
                        ticket.setVisibility(View.GONE);
                        noTicket.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FirebaseError", "Failed to read data", databaseError.toException());
                    progressDialog.dismiss();
                }
            });
        }
    }


    public void unAvailableTicket() {


        AlertDialog.Builder builder = new AlertDialog.Builder(ViewTickets.this);
        builder.setMessage("Are you sure expire ticket ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(x != null && y != null) {
                    DatabaseReference DB = FirebaseDatabase.getInstance().getReference("Booked Tickets");
                    DB.child(x).child(y).child("Status").setValue("traveled")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewTickets.this, "Ticket was expired", Toast.LENGTH_SHORT).show();
                                }
                            });
                } 
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
}