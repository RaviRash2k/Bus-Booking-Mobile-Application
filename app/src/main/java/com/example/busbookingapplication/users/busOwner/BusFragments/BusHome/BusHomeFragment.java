package com.example.busbookingapplication.users.busOwner.BusFragments.BusHome;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.busbookingapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusHomeFragment extends Fragment {

    EditText enterTicket;
    Button viewTicket, removeAvlOfTicket;
    LinearLayout ticket;
    TextView date, time, bus, nic, price, busRoute, seats, noTicket;
    String x, y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bus_home, container, false);

//        enterTicket = rootView.findViewById(R.id.enterTicket);
//        viewTicket = rootView.findViewById(R.id.viewTicket);
//        removeAvlOfTicket = rootView.findViewById(R.id.deleteTimeSlot);
//
//        date = rootView.findViewById(R.id.date);
//        time = rootView.findViewById(R.id.time);
//        bus = rootView.findViewById(R.id.bus);
//        busRoute = rootView.findViewById(R.id.busRoute);
//        nic = rootView.findViewById(R.id.nic);
//        price = rootView.findViewById(R.id.price);
//        seats = rootView.findViewById(R.id.seats);
//
//        ticket = rootView.findViewById(R.id.ticket);
//        noTicket = rootView.findViewById(R.id.noTicket);
//
//        viewTicket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchTicket();
//            }
//        });
//
//        removeAvlOfTicket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                unAvailableTicket();
//            }
//        });

        return rootView;
    }

    public void searchTicket() {
        String ticketID = enterTicket.getText().toString();

        if(ticketID.isEmpty()) {
            ticket.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Please enter ticket ID", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
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

                            if (ticketKey != null && status != null && ticketKey.equals(ticketID) && status.equals("pending")) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to expire the ticket?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (x != null && y != null) {
                    DatabaseReference DB = FirebaseDatabase.getInstance().getReference("Booked Tickets");
                    DB.child(x).child(y).child("Status").setValue("traveled")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Ticket was expired", Toast.LENGTH_SHORT).show();
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
