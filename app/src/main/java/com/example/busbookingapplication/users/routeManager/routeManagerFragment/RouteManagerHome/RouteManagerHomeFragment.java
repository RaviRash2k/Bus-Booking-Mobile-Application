package com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.BusManage.BusManage;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.ViewTicket.ViewTickets;

public class RouteManagerHomeFragment extends Fragment {

    TextView busManage, viewTicket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_route_manager_home, container, false);

        //busRegister
        busManage = rootView.findViewById(R.id.busManage);
        busManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BusManage.class));
            }
        });

        //viewTicket
        viewTicket = rootView.findViewById(R.id.viewTicket);
        viewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ViewTickets.class));
            }
        });

        return rootView;
    }
}