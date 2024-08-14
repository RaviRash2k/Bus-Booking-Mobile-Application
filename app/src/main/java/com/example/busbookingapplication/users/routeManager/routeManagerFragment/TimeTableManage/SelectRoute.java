package com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.registerAndLogin.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectRoute extends Fragment {

    TextView addRoute, hide;
    ImageButton addRouteButton;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Route");
    RecyclerView routeList;
    SelectRouteAdapter myAdapter;
    ArrayList<SelectRouteModel> list;
    EditText searchRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_route, container, false);

        //Display routes
        routeList = rootView.findViewById(R.id.routeList);
        routeList.setHasFixedSize(true);
        routeList.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new SelectRouteAdapter(getContext(), list);
        routeList.setAdapter(myAdapter);

        fetchDataFromDatabase();

        //search
        searchRoute = rootView.findViewById(R.id.searchRoute);
        hide = rootView.findViewById(R.id.hide);

        //Add new route
        addRoute = rootView.findViewById(R.id.addRoute);
        addRouteButton = rootView.findViewById(R.id.addRouteButton);

        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String route = addRoute.getText().toString();

                if(route.isEmpty()){
                    Toast.makeText(getContext(), "Empty Route", Toast.LENGTH_SHORT).show();

                }else{
                    DB.orderByKey().equalTo(route).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(getContext(), "Route already exists!", Toast.LENGTH_SHORT).show();

                            } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setMessage("Are you sure add " + route+ "?");

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            DB.child(route).setValue(route);
                                            Toast.makeText(getContext(), "Route added", Toast.LENGTH_SHORT).show();
                                            addRoute.setText("");
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return rootView;
    }

    private void fetchDataFromDatabase() {
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                //searchRoute
                searchRoute.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {

                            search();

                            return true;
                        }
                        return false;
                    }
                });

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                    String route = itemSnapshot.getKey();

                    SelectRouteModel mainModel = new SelectRouteModel();
                    mainModel.setRoute(route);

                    hide.setVisibility(View.GONE);
                    routeList.setVisibility(View.VISIBLE);

                    list.add(mainModel);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Route", "Database error: " + error.getMessage());
            }
        });
    }

    public void search(){

                String search = searchRoute.getText().toString();
                DB.orderByKey().startAt(search).endAt(search + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        list.clear();

                        if(search.isEmpty()){
                            fetchDataFromDatabase();
                        }else{
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                String route = snapshot.getKey();

                                SelectRouteModel mainModel = new SelectRouteModel();
                                mainModel.setRoute(route);

                                list.add(mainModel);
                            }

                            myAdapter.notifyDataSetChanged();
                        }

                        if (list.isEmpty()) {
                            hide.setVisibility(View.VISIBLE);
                            routeList.setVisibility(View.GONE);

                            hide.setText("No results found for \"" + search +"\"");

                        } else {
                            hide.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}