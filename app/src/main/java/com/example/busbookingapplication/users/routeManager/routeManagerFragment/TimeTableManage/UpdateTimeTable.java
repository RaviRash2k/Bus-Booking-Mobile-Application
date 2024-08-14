package com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.TimeTable.AddTimeTable.AddNewTimeSlot;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.TimeTable.ViewTimeTable.ViewTimes;

import java.util.Objects;

public class UpdateTimeTable extends AppCompatActivity {

    TextView addTimeTable, viewTimeTable;
    String tab = "viewTimeTable";
    FrameLayout frmTimeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_time_table);

        addTimeTable = findViewById(R.id.addTimeTable);
        viewTimeTable = findViewById(R.id.viewTimeTable);

        tab();

        loadFragment(new ViewTimes(), false);

        // Frame layout
        frmTimeTable = findViewById(R.id.frmTimeTable);

        // viewTimeTable onClick listeners
        viewTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "viewTimeTable";
                tab();
                loadFragment(new ViewTimes(), false);
            }
        });

        // addTimeTable onClick listeners
        addTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "addTimeTable";
                tab();
                loadFragment(new AddNewTimeSlot(), false);
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInsialized) {

        String route = getIntent().getStringExtra("route");

        Bundle bundle = new Bundle();
        bundle.putString("route", route);

        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInsialized) {
            fragmentTransaction.add(R.id.frmTimeTable, fragment);

        } else {

            fragmentTransaction.replace(R.id.frmTimeTable, fragment);
        }

        fragmentTransaction.commit();
    }

    public void tab(){

        if(Objects.equals(tab, "viewTimeTable")){
            viewTimeTable.setTextSize(17);
            addTimeTable.setTextSize(13);

        } else if (Objects.equals(tab, "addTimeTable")) {
            viewTimeTable.setTextSize(13);
            addTimeTable.setTextSize(17);
        }
    }
}