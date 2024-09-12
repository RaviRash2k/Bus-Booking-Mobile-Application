package com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.BusManage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.RouteManagerHome;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BusManage extends AppCompatActivity {

    EditText busNo, email, ownerNIC, password, rePassword;
    Button create;
    DatabaseReference DB;
    private Spinner busRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_manage);

        busNo = findViewById(R.id.busNo);
        email = findViewById(R.id.email);
        ownerNIC = findViewById(R.id.ownerNIC);
        busRoute = findViewById(R.id.busRoute);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        create = findViewById(R.id.create);

        DB = FirebaseDatabase.getInstance().getReference().child("Users");

        initializeRouteSpinner(busRoute);

        // Create account button click listener
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = busNo.getText().toString().trim();
                String eml = email.getText().toString().trim();
                String nic = ownerNIC.getText().toString().trim();
                String route = busRoute.getSelectedItem().toString();
                String pass = password.getText().toString().trim();
                String repass = rePassword.getText().toString().trim();

                if (num.isEmpty() || eml.isEmpty() || pass.isEmpty() || repass.isEmpty() || nic.isEmpty() || route.isEmpty()) {
                    Toast.makeText(BusManage.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 8) {
                    Toast.makeText(BusManage.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                } else if (nic.length() < 12) {
                    Toast.makeText(BusManage.this, "Invalid NIC", Toast.LENGTH_SHORT).show();
                } else if (!pass.equals(repass)) {
                    Toast.makeText(BusManage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    generateNextBusID(num, eml, nic, route, pass);
                }
            }
        });
    }

    // Initialize route spinner
    private void initializeRouteSpinner(final Spinner spinner) {
        DatabaseReference routeRef = FirebaseDatabase.getInstance().getReference("Route");

        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> routeList = new ArrayList<>();
                routeList.add("Select location");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object value = snapshot.getValue();
                    if (value instanceof String) {
                        routeList.add((String) value);
                    } else if (value instanceof Map) {
                        routeList.add(snapshot.getKey());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(BusManage.this, android.R.layout.simple_spinner_item, routeList) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(position == 0 ? Color.GRAY : Color.GRAY);
                        return view;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(position == 0 ? Color.GRAY : Color.GRAY);
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BusManage.this, "Failed to load routes.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Generate next Bus ID based on the last added bus
    private void generateNextBusID(String busNum, String email, String nic, String route, String password) {
        Query lastBusQuery = DB.orderByKey().startAt("BUSHW").endAt("BUSHW\uf8ff").limitToLast(1);

        lastBusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastID = null;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        lastID = snapshot.getKey();
                    }
                }

                // Generate next bus ID
                String nextID = generateNextID(lastID);
                saveNewBus(nextID, busNum, email, nic, route, password);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BusManage.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to generate the bus ID
    private String generateNextID(String lastID) {
        if (lastID == null || lastID.isEmpty()) {
            return "BUSHW001";
        }

        String numericPart = lastID.substring(5);

        if (numericPart.isEmpty()) {
            return "BUSHW001";
        }

        int number = Integer.parseInt(numericPart);
        number++;
        return String.format("BUSHW%03d", number);
    }

    // Method to save the new bus to Firebase
    private void saveNewBus(String busID, String busNum, String email, String nic, String route, String password) {
        DB.child(busID).child("Bus ID").setValue(busID);
        DB.child(busID).child("Bus Number").setValue(busNum);
        DB.child(busID).child("Email").setValue(email);
        DB.child(busID).child("NIC").setValue(nic);
        DB.child(busID).child("Route").setValue(route);
        DB.child(busID).child("Password").setValue(password);
        DB.child(busID).child("Type").setValue("busOwner");
        DB.child(busID).child("Logged").setValue("no");

        Toast.makeText(BusManage.this, "Data Inserted", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(BusManage.this, RouteManagerHome.class);
        startActivity(i);
        finish();
    }
}
