package com.example.busbookingapplication.users.busOwner.BusFragments.BusTimeTable;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import com.example.busbookingapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class BusTimeTableFragment extends Fragment {

    TextView selectedDay;
    Button go;
    RecyclerView recyclerView;
    BusTimeTableAdapter myAdapter;
    ArrayList<BusTimeTableModel> list;
    String selectedDate = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus_time_table, container, false);

        recyclerView = rootView.findViewById(R.id.busTimeTable);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new BusTimeTableAdapter(getContext(), list);
        recyclerView.setAdapter(myAdapter);

        // Select date
        selectedDay = rootView.findViewById(R.id.selectedDay);
        selectedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        go = rootView.findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTimeSlots();
            }
        });

        return rootView;
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDate = dayOfMonth + " . " + (monthOfYear + 1) + " . " + year;
                        selectedDay.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadTimeSlots() {

        SharedPreferences prf = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String busNo = prf.getString("busNo", "hey");

        if (selectedDate == null) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference routeReference = FirebaseDatabase.getInstance().getReference("Route");

        routeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot keySnapshot : citySnapshot.getChildren()) {
                        String busNumber = keySnapshot.child("Bus Number").getValue(String.class);
                        String date = keySnapshot.child("Date").getValue(String.class);

                        if (busNumber != null && busNumber.equals(busNo) && date != null && date.equals(selectedDate)) {

                            String time = keySnapshot.child("Start Time").getValue(String.class);
                            String avlSeats = keySnapshot.child("Available Seats").getValue(String.class);
                            String price = keySnapshot.child("Ticket Price").getValue(String.class);
                            String route = keySnapshot.child("Start Location").getValue(String.class) + " - " + keySnapshot.child("End Location").getValue(String.class);

                            ArrayList<String> bookedSeats = new ArrayList<>();

                            DataSnapshot bookedSeatsSnapshot = keySnapshot.child("Booked seats");
                            for (DataSnapshot seatSnapshot : bookedSeatsSnapshot.getChildren()) {
                                String bookedSeat = seatSnapshot.getValue(String.class);
                                if (bookedSeat != null) {
                                    bookedSeats.add(bookedSeat);
                                }
                            }

                            BusTimeTableModel mainModel = new BusTimeTableModel();
                            mainModel.setRoute(route);
                            mainModel.setTime(time);
                            mainModel.setBus(busNo);
                            mainModel.setDate(date);
                            mainModel.setPrice(price);
                            mainModel.setSeat(avlSeats);
                            mainModel.setBookedSeats(bookedSeats);

                            list.add(mainModel);
                        }
                    }
                }

                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "No time slots found for the selected date and bus.", Toast.LENGTH_SHORT).show();
                }

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to read data", databaseError.toException());
            }
        });
    }
}
