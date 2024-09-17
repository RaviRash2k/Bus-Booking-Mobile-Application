package com.example.busbookingapplication.LostAndFound;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.LostAndFound.Fragment.AddLostOrFindFragment;
import com.example.busbookingapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class LostAndFound extends AppCompatActivity {

    Button newLostFound;
    String tab = "lost";
    TextView lost, found;
    RecyclerView recyclerView;
    LostAndFoundAdapter myAdapter;
    ArrayList<LostAndFoundModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found2);

        // Recycler view setup
        recyclerView = findViewById(R.id.recycleViewForLostAndFound);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(LostAndFound.this));

        list = new ArrayList<>();
        myAdapter = new LostAndFoundAdapter(LostAndFound.this, list);
        recyclerView.setAdapter(myAdapter);

        // Click lost button
        lost = findViewById(R.id.lost);
        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "lost";
                tab();
                fetchDataFromDatabase();
            }
        });

        // Click found button
        found = findViewById(R.id.found);
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "found";
                tab();
                fetchDataFromDatabase();
            }
        });

        // Add new post
        newLostFound = findViewById(R.id.newLostFound);
        newLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddLostOrFindFragment());
            }
        });

        tab();
        fetchDataFromDatabase();
    }

    // Load fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // Tab switch method
    public void tab() {
        if (Objects.equals(tab, "lost")) {
            lost.setTextSize(17f);
            found.setTextSize(13f);
        } else if (Objects.equals(tab, "found")) {
            lost.setTextSize(13f);
            found.setTextSize(17f);
        }
    }

    // Load data based on selected tab
    private void fetchDataFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Lost And Found");

        // Get current user name
        SharedPreferences prf = getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String currentName = prf.getString("userName", "");
        String usertype = prf.getString("type", "");

        ProgressDialog progressDialog = new ProgressDialog(LostAndFound.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                    String status = keySnapshot.child("status").getValue(String.class);

                    if (Objects.equals(status, tab)) {
                        LostAndFoundModel mainModel = new LostAndFoundModel();
                        mainModel.setUserName(keySnapshot.child("user").getValue(String.class));
                        mainModel.setMessage(keySnapshot.child("message").getValue(String.class));
                        mainModel.setStatus(status);
                        mainModel.setId(keySnapshot.getKey());

                        if (currentName.equals(keySnapshot.child("user").getValue(String.class)) || usertype.equals("routeManager")) {
                            mainModel.setDelete(true);
                        }

                        //like count
                        DataSnapshot likesSnapshot = keySnapshot.child("likes");
                        int likeCount = (int) likesSnapshot.getChildrenCount();
                        mainModel.setLike(likeCount);

                        //already liked
                        if(likesSnapshot.hasChild(currentName)){
                            mainModel.setLiked(true);
                        }

                        list.add(mainModel);
                    }
                }
                progressDialog.dismiss();
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
                progressDialog.dismiss();
            }
        });

    }
}
