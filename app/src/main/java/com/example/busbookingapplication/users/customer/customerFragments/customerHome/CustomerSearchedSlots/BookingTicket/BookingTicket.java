package com.example.busbookingapplication.users.customer.customerFragments.customerHome.CustomerSearchedSlots.BookingTicket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.customer.CustomerHome;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class BookingTicket extends AppCompatActivity {

    TextView date, location, bus, seatCount, selectedSeatsView, ticketPrice;
    ImageView inc, dec;
    EditText nic;
    Spinner seatSelectionSpinner;
    Button bookButton;
    int seat = 1, avlSeats;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference("Route");
    ArrayList<String> selectedSeats = new ArrayList<>();
    ArrayList<Integer> bookedSeats = new ArrayList<>();
    ArrayAdapter<String> seatAdapter;
    String ticket, userNIC;
    Boolean click;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_ticket);

        //intent
        intent = getIntent();
        ticket = intent.getStringExtra("ticket");

        // UI components
        date = findViewById(R.id.date);
        location = findViewById(R.id.location);
        bus = findViewById(R.id.bus);
        seatCount = findViewById(R.id.seatCount);
        seatSelectionSpinner = findViewById(R.id.seatSelectionSpinner);
        selectedSeatsView = findViewById(R.id.selectedSeatsView);
        ticketPrice = findViewById(R.id.ticketPrice);
        bookButton = findViewById(R.id.bookButton);
        nic = findViewById(R.id.nic);

        // Set intent values for TextViews
        date.setText(intent.getStringExtra("date"));
        location.setText(intent.getStringExtra("route"));
        bus.setText(intent.getStringExtra("bus"));
        ticketPrice.setText(String.format("Rs %s", ticket));

        seatCount.setText(String.valueOf(seat));

        inc = findViewById(R.id.inc);
        dec = findViewById(R.id.dec);

        //get available seats on database
        DB.child(Objects.requireNonNull(intent.getStringExtra("start")))
                .child(Objects.requireNonNull(intent.getStringExtra("id"))).child("Available Seats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            avlSeats = Integer.parseInt(dataSnapshot.getValue(String.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        // Increase seat count
        inc.setOnClickListener(v -> {
            if (seat < 6) {

                if(avlSeats > seat){

                    seat++;
                    seatCount.setText(String.valueOf(seat));
                    ticket = String.valueOf(Integer.parseInt(ticket) * seat);
                    ticketPrice.setText(String.format("Rs %s", ticket));
                    setupSeatSelectionSpinner(intent);

                }else{
                    Toast.makeText(BookingTicket.this, "Available " + avlSeats + " seats only", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(BookingTicket.this, "You can select a maximum of 6 seats", Toast.LENGTH_SHORT).show();
            }
        });

        // Decrease seat count
        dec.setOnClickListener(v -> {
            if (seat > 1) {
                seat--;
                seatCount.setText(String.valueOf(seat));

                if (selectedSeats.size() > seat) {
                    selectedSeats.remove(selectedSeats.size() - 1);
                }

                ticket = String.valueOf(Integer.parseInt(ticket) * seat);
                ticketPrice.setText(String.format("Rs %s", ticket));
                setupSeatSelectionSpinner(intent);
            }
        });

        // Book button click event
        bookButton.setOnClickListener(v -> {

            //get NIC
            userNIC = nic.getText().toString();

            if (click && userNIC.length() == 12) {

                new AlertDialog.Builder(BookingTicket.this)
                        .setMessage("Are you sure you want to book the selected seats?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> {

                            ProgressDialog progressDialog = new ProgressDialog(BookingTicket.this);
                            progressDialog.setMessage("Booking seats...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            ArrayList<String> allSeats = new ArrayList<>();

                            for (Integer seat : bookedSeats) {
                                allSeats.add(String.valueOf(seat));
                            }

                            allSeats.addAll(selectedSeats);

                            DB.child(Objects.requireNonNull(intent.getStringExtra("start")))
                                    .child(Objects.requireNonNull(intent.getStringExtra("id")))
                                    .child("Booked seats")
                                    .setValue(allSeats)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()) {

                                            saveUserToDatabase();
                                            updateAvailableSeats();

                                            startActivity(new Intent(BookingTicket.this, CustomerHome.class));
                                            Toast.makeText(BookingTicket.this, "Seats booked successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(BookingTicket.this, "Failed to book seats. Please try again.", Toast.LENGTH_SHORT).show();
                                        }

                                        progressDialog.dismiss();
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            } else {
                if(userNIC.isEmpty()){
                    Toast.makeText(BookingTicket.this, "Please enter NIC.", Toast.LENGTH_SHORT).show();

                } else if (userNIC.length() != 12) {
                    Toast.makeText(BookingTicket.this, "Invalid NIC. Must be 12 characters.", Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(BookingTicket.this, "Please select seats.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setupSeatSelectionSpinner(intent);
    }

    private void setupSeatSelectionSpinner(Intent intent) {

        DatabaseReference bookedSeatsRef = DB.child(Objects.requireNonNull(intent.getStringExtra("start")))
                .child(Objects.requireNonNull(intent.getStringExtra("id")))
                .child("Booked seats");

        bookedSeatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot seatSnapshot : dataSnapshot.getChildren()) {
                    Integer seatNumber = Integer.valueOf(Objects.requireNonNull(seatSnapshot.getValue(String.class)));
                    bookedSeats.add(seatNumber);
                }

                runOnUiThread(() -> {
                    ArrayList<String> seatNumbers = new ArrayList<>();
                    seatNumbers.add("select seat");
                    for (int i = 1; i <= 45; i++) {
                        if (!(bookedSeats.contains(i))) {
                            seatNumbers.add(String.valueOf(i));
                        }
                    }

                    seatAdapter = new ArrayAdapter<>(BookingTicket.this, android.R.layout.simple_spinner_item, seatNumbers);
                    seatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    seatSelectionSpinner.setAdapter(seatAdapter);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        seatSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedSeat = (String) parentView.getItemAtPosition(position);

                if (selectedSeats.contains(selectedSeat)) {
                    selectedSeats.remove(selectedSeat);
                    ((TextView) selectedItemView).setTextColor(Color.BLACK);

                } else {
                    if (selectedSeats.size() < seat && !selectedSeat.equals("select seat")) {
                        selectedSeats.add(selectedSeat);

                        ((TextView) selectedItemView).setTextColor(Color.BLACK);

                    } else if (selectedSeats.size() >= seat) {
                        Toast.makeText(BookingTicket.this, "You can select up to " + seat + " seats", Toast.LENGTH_SHORT).show();
                    }
                }

                click = selectedSeats.size() == seat;
                updateSelectedSeatsView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });
    }

    private void updateSelectedSeatsView() {
        selectedSeatsView.setText(selectedSeats.toString());
    }

    public void saveUserToDatabase() {

        DatabaseReference booksDB = FirebaseDatabase.getInstance().getReference("Booked Tickets");

        String route = intent.getStringExtra("route");
        String key = booksDB.push().getKey();
        String routeID = intent.getStringExtra("id");
        String date = intent.getStringExtra("date");
        String bus = intent.getStringExtra("bus");
        String time = intent.getStringExtra("time");

        // Get user from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);

        assert route != null;
        assert key != null;
        assert routeID != null;
        assert userName != null;

        booksDB.child(route).child(key).child("Route ID").setValue(routeID);
        booksDB.child(route).child(key).child("Price").setValue(ticket);
        booksDB.child(route).child(key).child("Customer").setValue(userName);
        booksDB.child(route).child(key).child("Seats").setValue(selectedSeats);
        booksDB.child(route).child(key).child("Bus").setValue(bus);
        booksDB.child(route).child(key).child("Time").setValue(time);
        booksDB.child(route).child(key).child("Date").setValue(date);
        booksDB.child(route).child(key).child("Traveler NIC").setValue(userNIC);
        booksDB.child(route).child(key).child("Status").setValue("pending");
    }

    public void updateAvailableSeats(){

        int updatedSeats = avlSeats - seat;
        DB.child(Objects.requireNonNull(intent.getStringExtra("start")))
                .child(Objects.requireNonNull(intent.getStringExtra("id"))).child("Available Seats").setValue(String.valueOf(updatedSeats));

    }
}
