package com.example.busbookingapplication.users.busOwner.ChangePassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.registerAndLogin.login.LoginActivity;
import com.example.busbookingapplication.users.busOwner.BusOwnerHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusPasswordChange extends AppCompatActivity {

    EditText currentPassword, password, rePassword;
    Button cancel, change;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        super.setContentView(R.layout.activity_bus_password_change);


        currentPassword = findViewById(R.id.currentPassword);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        change = findViewById(R.id.change);
        cancel = findViewById(R.id.cansel);

        // Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusPasswordChange.this, LoginActivity.class));
            }
        });

        // Change button
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    public void changePassword() {
        String currentPass = currentPassword.getText().toString();
        String pass = password.getText().toString();
        String rePass = rePassword.getText().toString();

        String key = getIntent().getStringExtra("userKey");
        String bus = getIntent().getStringExtra("bus");

        if (key == null) {
            Toast.makeText(this, "User key is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        DB.child("Logged").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DB.child("Password").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String storedPassword = dataSnapshot.getValue(String.class);

                            if (storedPassword != null && storedPassword.equals(currentPass)) {

                                if (currentPass.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
                                    Toast.makeText(BusPasswordChange.this, "Fill all fields", Toast.LENGTH_SHORT).show();

                                } else if (pass.length() < 8) {
                                    Toast.makeText(BusPasswordChange.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();

                                } else if (!pass.equals(rePass)) {
                                    Toast.makeText(BusPasswordChange.this, "Passwords do not match", Toast.LENGTH_SHORT).show();

                                } else {
                                    DB.child("Password").setValue(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                sharedPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
                                                editor = sharedPreferences.edit();

                                                editor.putString("userName", key);
                                                editor.putString("type", "busOwner");
                                                editor.putString("busNo", bus);
                                                editor.putBoolean("logged", true);
                                                editor.apply();

                                                Toast.makeText(BusPasswordChange.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(BusPasswordChange.this, BusOwnerHome.class));
                                                finish();

                                            } else {
                                                Toast.makeText(BusPasswordChange.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(BusPasswordChange.this, "Enter correct current password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(BusPasswordChange.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(BusPasswordChange.this, "Failed to delete Logged status", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
