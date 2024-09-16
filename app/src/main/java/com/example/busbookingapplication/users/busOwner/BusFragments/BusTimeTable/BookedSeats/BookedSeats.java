package com.example.busbookingapplication.users.busOwner.BusFragments.BusTimeTable.BookedSeats;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busbookingapplication.R;

public class BookedSeats extends AppCompatActivity {

    TextView seatList, location, busNum, dateTravel;
    String bookedSeats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_seats);

        seatList = findViewById(R.id.seatList);
        location = findViewById(R.id.location);
        busNum = findViewById(R.id.busNum);
        dateTravel = findViewById(R.id.dateTravel);

        Intent intent = getIntent();
        bookedSeats = intent.getStringExtra("bookedSeats");
        String route = intent.getStringExtra("route");
        String time = intent.getStringExtra("time");
        String date = intent.getStringExtra("date");

        location.setText(route);
        busNum.setText(time);
        dateTravel.setText(date);

        if(bookedSeats == null || bookedSeats.isEmpty()){
            seatList.setText("No seats book");

        }else{
            seatList.setText(String.format("Booked seats - %s", bookedSeats));
        }
    }
}