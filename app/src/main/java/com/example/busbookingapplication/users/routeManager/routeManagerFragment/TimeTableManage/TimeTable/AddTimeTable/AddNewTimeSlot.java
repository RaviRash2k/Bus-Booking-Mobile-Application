package com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.TimeTable.AddTimeTable;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.registerAndLogin.register.RegisterActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AddNewTimeSlot extends Fragment {

    private String route;
    TextView turnDay, startTime, endTime, fromLocation;
    EditText busNo, toLocation, ticketPrice;
    LinearLayout daySet, startTimeSet, endTimeSet;
    Button add;

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    int hourStart = calendar.get(Calendar.HOUR_OF_DAY);
    int minuteStart = calendar.get(Calendar.MINUTE);

    int hourEnd = calendar.get(Calendar.HOUR_OF_DAY);
    int minuteEnd = calendar.get(Calendar.MINUTE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_time_slot, container, false);

        //get route from main activity as intent
        if (getArguments() != null) {
            route = getArguments().getString("route");
        }
        //set from location
        fromLocation = rootView.findViewById(R.id.fromLocation);
        fromLocation.setText(route);

        //ticket price
        ticketPrice = rootView.findViewById(R.id.ticketPrice);

        //assign selected date and time text views
        turnDay = rootView.findViewById(R.id.turnDay);
        startTime = rootView.findViewById(R.id.startTime);
        endTime = rootView.findViewById(R.id.endTime);

        //set date
        daySet = rootView.findViewById(R.id.daySet);
        daySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        //set start time
        startTime = rootView.findViewById(R.id.startTime);
        startTimeSet = rootView.findViewById(R.id.startTimeSet);
        startTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartTime();
            }
        });

        //set end date
        endTime = rootView.findViewById(R.id.endTime);
        endTimeSet = rootView.findViewById(R.id.endTimeSet);
        endTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndTime();
            }
        });

        //when click add button
        add = rootView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get entered values
                busNo = rootView.findViewById(R.id.busNo);
                String enteredBus = busNo.getText().toString();

                toLocation = rootView.findViewById(R.id.toLocation);
                String enteredToLocation = toLocation.getText().toString();

                String date = turnDay.getText().toString();
                String start = startTime.getText().toString();
                String end = endTime.getText().toString();
                String ticketCost = ticketPrice.getText().toString();

                if(enteredBus.isEmpty() || enteredToLocation.isEmpty() || date.isEmpty() || start.isEmpty() || end.isEmpty() || ticketCost.isEmpty()) {
                    Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();

                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure add to time table?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Adding time slot...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Route");
                            String key = DB.push().getKey();

                            // data to Firebase
                            DB.child(route).child(key).child("Bus Number").setValue(enteredBus);
                            DB.child(route).child(key).child("End Location").setValue(enteredToLocation);
                            DB.child(route).child(key).child("Start Location").setValue(route);
                            DB.child(route).child(key).child("Date").setValue(date);
                            DB.child(route).child(key).child("Start Time").setValue(start);
                            DB.child(route).child(key).child("Ticket Price").setValue(ticketCost);
                            DB.child(route).child(key).child("Available Seats").setValue("45");
                            DB.child(route).child(key).child("End Time").setValue(end);

                            DB.child(route).child(key).child("End Time").setValue(end).addOnCompleteListener(task -> {

                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Time slot added", Toast.LENGTH_SHORT).show();

                                    // Clear all inputs
                                    busNo.setText("");
                                    toLocation.setText("");
                                    turnDay.setText("");
                                    startTime.setText("");
                                    endTime.setText("");
                                    ticketPrice.setText("");

                                } else {
                                    Toast.makeText(getContext(), "Failed to add time slot", Toast.LENGTH_SHORT).show();
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
            }
        });

        return rootView;
    }

    public void setDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Handle the selected date
                        String selectedDate = dayOfMonth + " . " + (monthOfYear + 1) + " . " + year;
                        turnDay.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    public void setStartTime(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String amPm;
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                            if (hourOfDay > 12) {
                                hourOfDay -= 12;
                            }
                        } else {
                            amPm = "AM";
                            if (hourOfDay == 0) {
                                hourOfDay = 12;
                            }
                        }

                        String selectedTime = String.format("%02d : %02d %s", hourOfDay, minute, amPm);
                        startTime.setText(selectedTime);
                    }
                },
                hourStart, minuteStart, true);

        timePickerDialog.show();
    }

    public void setEndTime(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String amPm;
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                            if (hourOfDay > 12) {
                                hourOfDay -= 12;
                            }
                        } else {
                            amPm = "AM";
                            if (hourOfDay == 0) {
                                hourOfDay = 12;
                            }
                        }

                        String selectedTime = String.format("%02d : %02d %s", hourOfDay, minute, amPm);
                        endTime.setText(selectedTime);
                    }
                },
                hourEnd, minuteEnd, true);

        timePickerDialog.show();
    }
}
