package com.example.busbookingapplication.users.customer.customerFragments.customerHome.CustomerSearchedSlots;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.registerAndLogin.login.LoginActivity;
import com.example.busbookingapplication.registerAndLogin.register.RegisterActivity;
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.Models.CustomerSearchedAdapter;
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.Models.CustomerSearchedModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerSearchSlots extends AppCompatActivity {

    RecyclerView customerSearchList;
    CustomerSearchedAdapter myAdapter;
    ArrayList<CustomerSearchedModel> list;

    LinearLayout noBusDisplay;

    String startLocation, endLocation, searchDate;

    TextView start, end, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search_slots);

        // Initialize RecyclerView
        customerSearchList = findViewById(R.id.customerSearchList);
        customerSearchList.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve data from intent
        Intent intent = getIntent();
        startLocation = intent.getStringExtra("startLocation");
        endLocation = intent.getStringExtra("endLocation");
        searchDate = intent.getStringExtra("selectedDate");

        // Initialize list and adapter
        list = new ArrayList<>();
        myAdapter = new CustomerSearchedAdapter(this, list);
        customerSearchList.setAdapter(myAdapter);

        //text views
        start = findViewById(R.id.start);
        start.setText(startLocation);

        end = findViewById(R.id.end);
        end.setText(endLocation);

        date = findViewById(R.id.date);
        date.setText(searchDate);

        //Linear layout for display no bus
        noBusDisplay = findViewById(R.id.noBusDisplay);

        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Route").child(startLocation);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String end = snapshot.child("End Location").getValue(String.class);
                    String date = snapshot.child("Date").getValue(String.class);
                    String seats = snapshot.child("Available Seats").getValue(String.class);

//                    if (endLocation.equals(end) && searchDate.equals(date)) {
                    if (Integer.parseInt(seats) > 0 && searchDate.equals(date)) {

                        if(endLocation == end){
                            System.out.println("OK");
                        }else{
                            System.out.println("NO");
                            System.out.println(endLocation);
                            System.out.println(end);
                            System.out.println(date);
                        }

                        CustomerSearchedModel mainModel = new CustomerSearchedModel();

                        String time = snapshot.child("Start Time").getValue(String.class);
                        String bus = snapshot.child("Bus Number").getValue(String.class);
                        String ticket = snapshot.child("Ticket Price").getValue(String.class);
                        String id = snapshot.getKey();

                        mainModel.setBus(bus);
                        mainModel.setTime(time);
                        mainModel.setTicket(ticket);
                        mainModel.setSeats(seats);
                        mainModel.setLocation(startLocation + " - " + endLocation);
                        mainModel.setDate(searchDate);
                        mainModel.setId(id);
                        mainModel.setStart(startLocation);

                        list.add(mainModel);

                    }
                }

                noBuses();
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to read data", databaseError.toException());
            }
        });
    }

    private void noBuses() {
        if (list.isEmpty()) {
            noBusDisplay.setVisibility(View.VISIBLE);
            customerSearchList.setVisibility(View.GONE);
        } else {
            noBusDisplay.setVisibility(View.GONE);
            customerSearchList.setVisibility(View.VISIBLE);
        }
    }

}
