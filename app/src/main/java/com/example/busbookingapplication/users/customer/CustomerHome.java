package com.example.busbookingapplication.users.customer;

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
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.CusHomeFragment;
import com.example.busbookingapplication.users.customer.customerFragments.customerMenu.CustomerMenuFragment;
import com.example.busbookingapplication.users.customer.customerFragments.customerBooks.MyBooksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerHome extends AppCompatActivity {

    private BottomNavigationView bnav;
    private FrameLayout frm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_home);

        bnav = findViewById(R.id.bnavcus);
        frm = findViewById(R.id.frmcus);

        loadFragment(new CusHomeFragment(), false);

        bnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.timeTable) {
                    loadFragment(new MyBooksFragment(), false);

                } else if (itemId == R.id.homeRouteManager) {
                    loadFragment(new CusHomeFragment(), false);

                } else if (itemId == R.id.setting) {
                    loadFragment(new CustomerMenuFragment(), false);

                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInsialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInsialized) {
            fragmentTransaction.add(R.id.frmcus, fragment);

        } else {

            fragmentTransaction.replace(R.id.frmcus, fragment);
        }

        fragmentTransaction.commit();
    }
}