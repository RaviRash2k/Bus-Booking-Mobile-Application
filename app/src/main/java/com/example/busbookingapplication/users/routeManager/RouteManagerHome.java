package com.example.busbookingapplication.users.routeManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.customer.customerFragments.customerBooks.MyBooksFragment;
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.CusHomeFragment;
import com.example.busbookingapplication.users.customer.customerFragments.customerMenu.CustomerMenuFragment;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.RouteManagerHomeFragment;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.SelectRoute;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RouteManagerHome extends AppCompatActivity {

    private BottomNavigationView bnav;
    private FrameLayout frm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_route_manager_home);

        bnav = findViewById(R.id.bnavRoute);
        frm = findViewById(R.id.frmRoute);

        loadFragment(new RouteManagerHomeFragment(), false);

        bnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.timeTable) {
                    loadFragment(new SelectRoute(), false);

                } else if (itemId == R.id.homeRouteManager) {
                    loadFragment(new RouteManagerHomeFragment(), false);

                } else if (itemId == R.id.setting) {
                    loadFragment(new RouteManagerHomeFragment(), false);

                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInsialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInsialized) {
            fragmentTransaction.add(R.id.frmRoute, fragment);

        } else {

            fragmentTransaction.replace(R.id.frmRoute, fragment);
        }

        fragmentTransaction.commit();
    }
}