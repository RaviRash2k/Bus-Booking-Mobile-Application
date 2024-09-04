package com.example.busbookingapplication.users.customer.customerFragments.customerHome;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.CustomerSearchedSlots.CustomerSearchSlots;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CusHomeFragment extends Fragment {

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    String selectedDate = null, searchDate = null, formattedSearchDate = null;

    TextView travelDateDay, travelDateMonYear, travelDateDate;
    Button searchBus;

    private DatabaseReference databaseReference;

    private Spinner routeSpinnerStart, routeSpinner;

    private List<String> routeList = new ArrayList<>();

    private TextView time;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cus_home, container, false);
        time = rootView.findViewById(R.id.time);

        // Update time every second
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000);
            }
        });

        // Click search button
        searchBus = rootView.findViewById(R.id.searchBus);
        searchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBuses();
            }
        });

        LinearLayout travelDate = rootView.findViewById(R.id.travelDate);

        travelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        // Set date on travel date
        travelDateDay = rootView.findViewById(R.id.travelDateDay);
        travelDateDate = rootView.findViewById(R.id.travelDateDate);
        travelDateMonYear = rootView.findViewById(R.id.travelDateMonYear);

        TextView date = rootView.findViewById(R.id.date);

        // Get current date
        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }

        DateTimeFormatter formatter = null;
        DateTimeFormatter formatterDay = null;
        DateTimeFormatter formatterDate = null;
        DateTimeFormatter formatterMonthYear = null;
        DateTimeFormatter formatterSelectedDate = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("EEEE, yyyy.MM.dd");
            formatterDay = DateTimeFormatter.ofPattern("dd");
            formatterDate = DateTimeFormatter.ofPattern("EEEE");
            formatterMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy");
            formatterSelectedDate = DateTimeFormatter.ofPattern("dd . MM . yyyy");
        }

        String currentDate = null;
        String weekDay = null;
        String weekDate = null;
        String monthAndYear = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = today.format(formatter);
            weekDay = today.format(formatterDay);
            weekDate = today.format(formatterDate);
            monthAndYear = today.format(formatterMonthYear);
            selectedDate = today.format(formatterSelectedDate);
        }

        // Set current date to TextView
        date.setText(currentDate);
        travelDateDay.setText(weekDay);
        travelDateDate.setText(weekDate);
        travelDateMonYear.setText(monthAndYear);

        // Set selected date
        searchDate = weekDay + " . " + month + " . " + year;

        routeSpinnerStart = rootView.findViewById(R.id.routeSpinnerStart);
        routeSpinner = rootView.findViewById(R.id.routeSpinner);

        // Call both spinners
        initializeRouteSpinner(routeSpinnerStart);
        initializeRouteSpinner(routeSpinner);

        return rootView;
    }

    private void initializeRouteSpinner(final Spinner spinner) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Route");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> routeList = new ArrayList<>();
                routeList.add("Select location");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.getValue() instanceof String) {
                        String routeName = snapshot.getValue(String.class);
                        routeList.add(routeName);
                    } else if (snapshot.getValue() instanceof Map) {
                        String routeName = snapshot.getKey();
                        routeList.add(routeName);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, routeList) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.GRAY);
                        }
                        return view;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.GRAY);
                        }
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());
        time.setText(currentTime);
    }

    public void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        travelDateDay = getView().findViewById(R.id.travelDateDay);
                        travelDateMonYear = getView().findViewById(R.id.travelDateMonYear);
                        travelDateDate = getView().findViewById(R.id.travelDateDate);

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth); // No need to add 1 to monthOfYear

                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                        SimpleDateFormat searchDateFormat = new SimpleDateFormat("dd . MM . yyyy", Locale.getDefault());

                        String weekDay = dayFormat.format(selectedDate.getTime());
                        String monAndYear = monthYearFormat.format(selectedDate.getTime());
                        formattedSearchDate = searchDateFormat.format(selectedDate.getTime());

                        travelDateDay.setText(String.valueOf(dayOfMonth));
                        travelDateMonYear.setText(monAndYear);
                        travelDateDate.setText(weekDay);
                    }
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void searchBuses() {
        //ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Searching for buses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Get spinner values
        String startLocation = routeSpinnerStart.getSelectedItem().toString();
        String endLocation = routeSpinner.getSelectedItem().toString();

        // Check valid locations
        if ("Select location".equals(startLocation) || "Select location".equals(endLocation)) {
            Toast.makeText(getContext(), "Please select valid start and end locations.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;

        } else if (startLocation.equals(endLocation)) {
            Toast.makeText(getContext(), "Please select different locations.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;

        } else if (formattedSearchDate == null) {
            Toast.makeText(getContext(), "Please select date.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getActivity(), CustomerSearchSlots.class);
                intent.putExtra("startLocation", startLocation);
                intent.putExtra("endLocation", endLocation);
                intent.putExtra("selectedDate", formattedSearchDate);

                progressDialog.dismiss();
                startActivity(intent);
            }
        }, 2000);
    }

}
