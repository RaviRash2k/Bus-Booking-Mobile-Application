package com.example.busbookingapplication.users.busOwner.BusFragments.BusHome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.busbookingapplication.LostAndFound.LostAndFound;
import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.ViewTicket.ViewTickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BusHomeFragment extends Fragment {

    private TextView time, date;
    Button viewTicket, lost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus_home, container, false);

        time = rootView.findViewById(R.id.time);
        date = rootView.findViewById(R.id.date);

        //Button
        viewTicket = rootView.findViewById(R.id.viewTicket);
        lost = rootView.findViewById(R.id.lost);

        updateTimeAndDate();

        //click view ticket button
        viewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewTickets.class);
                startActivity(intent);
            }
        });

        //click lost and found button
        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LostAndFound.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTime = timeFormat.format(calendar.getTime());
        time.setText(currentTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, yyyy/MM/dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        date.setText(currentDate);
    }
}
