package com.example.busbookingapplication.users.customer.customerFragments.customerBooks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.busbookingapplication.R;
import com.example.busbookingapplication.TimeTable.TimeTableAdapter;
import com.example.busbookingapplication.TimeTable.TimeTableModel;
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.CustomerSearchedSlots.BookingTicket.BookingTicket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyBooksFragment extends Fragment {

    String tab = "myBooks";
    TextView myBooks, passBooks, noBookText;
    RecyclerView recyclerView;
    MyBooksAdapter myAdapter;
    ArrayList<MyBooksModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_books, container, false);

        //recycler view
        recyclerView = rootView.findViewById(R.id.recycleViewForcustomerBooks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new MyBooksAdapter(getContext(), list);
        recyclerView.setAdapter(myAdapter);

        // Initialize the TextViews
        myBooks = rootView.findViewById(R.id.myBooks);
        passBooks = rootView.findViewById(R.id.passBooks);
        noBookText = rootView.findViewById(R.id.noBookText);

        tab();
        fetchDataFromDatabase();

        // Click "My Books" button
        myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "myBooks";
                tab();
                fetchDataFromDatabase();
            }
        });

        // Click "Past Books" button
        passBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "passBooks";
                tab();
                fetchDataFromDatabase();
            }
        });

        return rootView;
    }

    public void tab() {
        if (Objects.equals(tab, "myBooks")) {
            myBooks.setTextSize(17f);
            passBooks.setTextSize(13f);

        } else if (Objects.equals(tab, "passBooks")) {
            myBooks.setTextSize(13f);
            passBooks.setTextSize(17f);
        }
    }

    private void fetchDataFromDatabase() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Booked Tickets");

        // Get current user name
        SharedPreferences prf = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String currentName = prf.getString("userName", "");

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot bookingSnapshot : locationSnapshot.getChildren()) {

                        String customer = bookingSnapshot.child("Customer").getValue(String.class);
                        String status = bookingSnapshot.child("Status").getValue(String.class);

                        boolean pending = currentName.equals(customer) && Objects.equals(status, "pending") && tab.equals("myBooks");
                        boolean traveled = currentName.equals(customer) && Objects.equals(status, "traveled") && tab.equals("passBooks");

                        if (pending || traveled) {
                            MyBooksModel mainModel = new MyBooksModel();

                            mainModel.setRoute(locationSnapshot.getKey());
                            mainModel.setTicketID(bookingSnapshot.getKey());
                            mainModel.setDate(bookingSnapshot.child("Date").getValue(String.class));
                            mainModel.setTime(bookingSnapshot.child("Time").getValue(String.class));
                            mainModel.setBus(bookingSnapshot.child("Bus").getValue(String.class));
                            mainModel.setPrice(bookingSnapshot.child("Price").getValue(String.class));
                            mainModel.setTurnID(bookingSnapshot.child("Route ID").getValue(String.class));
                            mainModel.setStatus(status);

                            // Retrieve seat array
                            DataSnapshot seatsSnapshot = bookingSnapshot.child("Seats");
                            if (seatsSnapshot.exists()) {

                                List<String> seatList = new ArrayList<>();

                                for (DataSnapshot seatSnapshot : seatsSnapshot.getChildren()) {
                                    String seat = seatSnapshot.getValue(String.class);
                                    seatList.add(seat);
                                }
                                mainModel.setSeats(TextUtils.join(", ", seatList));
                                mainModel.setSeatCount(seatList.size());

                            }

                            list.add(mainModel);
                            progressDialog.dismiss();
                        }
                    }
                }

                if (list.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noBookText.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noBookText.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }

}
