package com.example.busbookingapplication.users.busOwner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.busOwner.BusFragments.BusHome.BusHomeFragment;
import com.example.busbookingapplication.users.busOwner.BusFragments.BusMenu.BusMenuFragment;
import com.example.busbookingapplication.users.busOwner.BusFragments.BusTimeTable.BusTimeTableFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BusOwnerHome extends AppCompatActivity {

    private BottomNavigationView bnav;
    private FrameLayout frm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bus_owner);

        bnav = findViewById(R.id.busOwner);
        frm = findViewById(R.id.frmBus);

        loadFragment(new BusHomeFragment(), false);

        bnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.busTimeTable) {
                    loadFragment(new BusTimeTableFragment(), false);

                } else if (itemId == R.id.busHome) {
                    loadFragment(new BusHomeFragment(), false);

                } else if (itemId == R.id.busMenu) {
                    loadFragment(new BusMenuFragment(), false);

                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInsialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInsialized) {
            fragmentTransaction.add(R.id.frmBus, fragment);

        } else {

            fragmentTransaction.replace(R.id.frmBus, fragment);
        }

        fragmentTransaction.commit();
    }
}