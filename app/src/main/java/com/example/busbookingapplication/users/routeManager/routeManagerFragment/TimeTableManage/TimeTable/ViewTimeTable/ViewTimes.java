package com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.TimeTable.ViewTimeTable;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.TimeTable.TimeTableAdapter;
import com.example.busbookingapplication.TimeTable.TimeTableModel;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.SelectRouteAdapter;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.SelectRouteModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewTimes extends Fragment {

    TextView startLocation, userSelectedDay;
    Button go;
    String route;
    DatabaseReference DB;
    RecyclerView recyclerView;
    TimeTableAdapter myAdapter;
    ArrayList<TimeTableModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_times, container, false);

        startLocation = rootView.findViewById(R.id.startLocation);
        userSelectedDay = rootView.findViewById(R.id.userSelectedDay);
        go = rootView.findViewById(R.id.go);

        recyclerView = rootView.findViewById(R.id.timeTable);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new TimeTableAdapter(getContext(), list);
        recyclerView.setAdapter(myAdapter);

        // Get route as an intent and set start location
        if (getArguments() != null) {
            route = getArguments().getString("route");
        }
        startLocation.setText(route);

        DB = FirebaseDatabase.getInstance().getReference().child("Route").child(route);

        fetchDataFromDatabase();

        //when click go button
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromDatabase();
            }
        });

        //select date
        userSelectedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        return rootView;
    }

    private void fetchDataFromDatabase() {

        String dateSelected = userSelectedDay.getText().toString();

        if(dateSelected.isEmpty()){
            DB.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    list.clear();

                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        String startLocation = itemSnapshot.child("Start Location").getValue(String.class);
                        String endLocation = itemSnapshot.child("End Location").getValue(String.class);
                        String busNo = itemSnapshot.child("Bus Number").getValue(String.class);
                        String date = itemSnapshot.child("Date").getValue(String.class);
                        String startTime = itemSnapshot.child("Start Time").getValue(String.class);
                        String enterEndTime = itemSnapshot.child("End Time").getValue(String.class);
                        String enterTicketPrice = itemSnapshot.child("Ticket Price").getValue(String.class);

                        TimeTableModel mainModel = new TimeTableModel();
                        mainModel.setStartLocation(startLocation);
                        mainModel.setEndLocation(endLocation);
                        mainModel.setBusNo(busNo);
                        mainModel.setDate(date);
                        mainModel.setStartTime(startTime);
                        mainModel.setEndTime(enterEndTime);
                        mainModel.setEndTime(enterTicketPrice);

                        list.add(mainModel);
                    }
                    myAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ViewTimes", "Database error: " + error.getMessage());
                }
            });

        }else{
            DB.orderByChild("Date").equalTo(dateSelected).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    list.clear();

                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        String startLocation = itemSnapshot.child("Start Location").getValue(String.class);
                        String endLocation = itemSnapshot.child("End Location").getValue(String.class);
                        String busNo = itemSnapshot.child("Bus Number").getValue(String.class);
                        String date = itemSnapshot.child("Date").getValue(String.class);
                        String startTime = itemSnapshot.child("Start Time").getValue(String.class);
                        String endTime = itemSnapshot.child("End Time").getValue(String.class);
                        String ticketPrice = itemSnapshot.child("Ticket Price").getValue(String.class);

                        TimeTableModel mainModel = new TimeTableModel();
                        mainModel.setStartLocation(startLocation);
                        mainModel.setEndLocation(endLocation);
                        mainModel.setBusNo(busNo);
                        mainModel.setDate(date);
                        mainModel.setStartTime(startTime);
                        mainModel.setEndTime(endTime);
                        mainModel.setEndTime(ticketPrice);

                        list.add(mainModel);
                    }
                    myAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ViewTimes", "Database error: " + error.getMessage());
                }
            });

        }
    }

    private void selectDate(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Handle the selected date
                        String selectedDate = dayOfMonth + " . " + (monthOfYear + 1) + " . " + year;
                        userSelectedDay.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}