package com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.registerAndLogin.login.LoginActivity;

public class RouteManagerMenuFragment extends Fragment {

    LinearLayout routeLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_manager_menu, container, false);

        //Logout of customer

        routeLogout = rootView.findViewById(R.id.routeLogout);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        routeLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure logout ?");

                builder.setPositiveButton("logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        editor.clear();
                        editor.apply();

                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return rootView;
    }
}