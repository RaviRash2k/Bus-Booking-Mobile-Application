package com.example.busbookingapplication.registerAndLogin.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.registerAndLogin.register.RegisterActivity;
import com.example.busbookingapplication.users.busOwner.BusOwnerHome;
import com.example.busbookingapplication.users.busOwner.ChangePassword.BusPasswordChange;
import com.example.busbookingapplication.users.customer.CustomerHome;
import com.example.busbookingapplication.users.routeManager.RouteManagerHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    TextView toReg;
    String type;
    EditText userName, password;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Users");
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // edit text
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);

        //Don't have account
        toReg = findViewById(R.id.toReg);
        toReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        //sharedPreferences
        sharedPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //check currently logged
        if(check()){

            SharedPreferences prf2 = getSharedPreferences("CurrentUser", MODE_PRIVATE);
            String x = prf2.getString("type", "");

            switch (x) {
                case "busOwner":
                    startActivity(new Intent(getApplicationContext(), BusOwnerHome.class));
                    finish();
                    break;

                case "customer":
                    startActivity(new Intent(getApplicationContext(), CustomerHome.class));
                    finish();
                    break;

                case "routeManager":
                    startActivity(new Intent(getApplicationContext(), RouteManagerHome.class));
                    finish();
                    break;

                default:
            }
        }

        // click signIn button
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input data
                String name = userName.getText().toString();
                String pass = password.getText().toString();

                if (name.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    DB.orderByKey().equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String userName = userSnapshot.getKey();
                                    String getPass = userSnapshot.child("Password").getValue(String.class);
                                    String getType = userSnapshot.child("Type").getValue(String.class);

                                    if (pass != null && pass.equals(getPass)) {

                                        if ("customer".equals(getType)) {

                                            editor.putString("userName", userName);
                                            editor.putString("type", getType);
                                            editor.putBoolean("logged", true);
                                            editor.apply();

                                            Toast.makeText(LoginActivity.this, "Successfully logged into Customer account", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, CustomerHome.class));
                                            finish();
                                        } else if ("busOwner".equals(getType)) {

                                            boolean logged = userSnapshot.hasChild("Logged");
                                            String busNumber = userSnapshot.child("Bus Number").getValue(String.class);

                                            if(logged){
                                                Intent intent = new Intent(LoginActivity.this, BusPasswordChange.class);
                                                intent.putExtra("userKey", userName);
                                                intent.putExtra("bus", busNumber);
                                                startActivity(intent);
                                                finish();

                                            }else{
                                                editor.putString("userName", userName);
                                                editor.putString("type", getType);
                                                editor.putBoolean("logged", true);
                                                editor.putString("busNo", busNumber);
                                                editor.apply();

                                                Toast.makeText(LoginActivity.this, "Successfully logged into Bus Owner account", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, BusOwnerHome.class));
                                                finish();
                                            }

                                        } else if ("routeManager".equals(getType)) {

                                            editor.putString("userName", userName);
                                            editor.putString("type", getType);
                                            editor.putBoolean("logged", true);
                                            editor.apply();

                                            Toast.makeText(LoginActivity.this, "Successfully logged into route Manager account", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, RouteManagerHome.class));
                                            finish();
                                        }else {
                                            Toast.makeText(LoginActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    //not login
    private boolean check(){
        return sharedPreferences.getBoolean("logged", false);
    }
}