package com.example.busbookingapplication.registerAndLogin.register;

import android.content.Intent;
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
import com.example.busbookingapplication.registerAndLogin.login.LoginActivity;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.RouteManagerHome.BusManage.BusManage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    CheckBox typeCustomer, typeBusOwner;
    Button signup;
    TextView toLog;
    String type = "invalid";
    EditText userName, email, password, rePassword;
    DatabaseReference DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);

        DB = FirebaseDatabase.getInstance().getReference().child("Users");

        //Register
        signup = findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = userName.getText().toString();
                String eml = email.getText().toString().trim();
                String pass = password.getText().toString();
                String repass = rePassword.getText().toString();

                //check admin or cus
                typeCustomer = findViewById(R.id.typeCustomer);
                typeBusOwner = findViewById(R.id.typeBusOwner);

                if (typeCustomer.isChecked() && typeBusOwner.isChecked()) {
                    type = "invalid";
                } else if(typeCustomer.isChecked()) {
                    type = "customer";
                } else if (typeBusOwner.isChecked()) {
                    type = "busOwner";
                }else{
                    type = "invalid";
                }

                if(name.isEmpty() || eml.isEmpty() || pass.isEmpty() || repass.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();

                }else if (password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password must need 8 characters", Toast.LENGTH_SHORT).show();

                }else if(!pass.equals(repass)){
                    Toast.makeText(RegisterActivity.this, "Password not matching", Toast.LENGTH_SHORT).show();

                }else if(type.equals("invalid")){
                    Toast.makeText(RegisterActivity.this, "Invalid type", Toast.LENGTH_SHORT).show();

                }
                else{
                    DB.orderByKey().equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(getApplicationContext(), "User Name already exists", Toast.LENGTH_SHORT).show();
                            } else {

                                if (name != null) {

                                    DB.child(name).child("UserName").setValue(name);
                                    DB.child(name).child("Password").setValue(pass);
                                    DB.child(name).child("Email").setValue(eml);
                                    DB.child(name).child("Type").setValue(type);

                                    Toast.makeText(RegisterActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Empty user name", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(RegisterActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //Already have account
        toLog = findViewById(R.id.toLog);

        toLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }
}