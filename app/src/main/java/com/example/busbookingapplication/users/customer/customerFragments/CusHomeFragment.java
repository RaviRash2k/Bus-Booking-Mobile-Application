package com.example.busbookingapplication.users.customer.customerFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busbookingapplication.R;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import com.example.busbookingapplication.R;
public class CusHomeFragment extends Fragment {

    private TextView time;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cus_home, container, false);
        time = rootView.findViewById(R.id.time);

        // Update the time every second
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        });

        LinearLayout travelDate = rootView.findViewById(R.id.travelDate);

        travelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarPopup(v);
            }
        });

        //set date on travel date
        TextView travelDateDay = rootView.findViewById(R.id.travelDateDay);
        TextView travelDateDate = rootView.findViewById(R.id.travelDateDate);
        TextView travelDateMonYear = rootView.findViewById(R.id.travelDateMonYear);

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("EEEE, yyyy.MM.dd");
            formatterDay = DateTimeFormatter.ofPattern("dd");
            formatterDate = DateTimeFormatter.ofPattern("EEEE");
            formatterMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy");
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
        }

        // Set current date to TextView
        date.setText(currentDate);
        travelDateDay.setText(weekDay);
        travelDateDate.setText(weekDate);
        travelDateMonYear.setText(monthAndYear);

        return rootView;
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());
        time.setText(currentTime);
    }

    private void showCalendarPopup(View anchorView) {
        // Inflate the calendar layout
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_popup, null);

        // Create the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        // Show the PopupWindow
        popupWindow.showAsDropDown(anchorView);

        // Dismiss the PopupWindow when clicked outside
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}