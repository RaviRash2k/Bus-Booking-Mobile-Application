package com.example.busbookingapplication.LostAndFound.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.busbookingapplication.LostAndFound.LostAndFound;
import com.example.busbookingapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddLostOrFindFragment extends Fragment {

    EditText message;
    CheckBox lost, found;
    Button cancel, done;
    String lostOrFound = "invalid";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_lost_or_find, container, false);

        message = rootView.findViewById(R.id.message);
        lost = rootView.findViewById(R.id.lost);
        found = rootView.findViewById(R.id.found);
        cancel = rootView.findViewById(R.id.cancel);
        done = rootView.findViewById(R.id.done);

        //checkbox
        //lost is checked
        lost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    found.setChecked(false);
                }
            }
        });
        //found is checked
        found.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lost.setChecked(false);
                }
            }
        });

        //click cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        //click done button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPost();
            }
        });

        return rootView;
    }

    public void newPost(){

        String userMessage = message.getText().toString();

        //check lost or found
        if(lost.isChecked()){
            lostOrFound = "lost";

        } else if (found.isChecked()) {
            lostOrFound = "found";
        }

        //store to database
        if(userMessage.isEmpty()){
            Toast.makeText(getContext(), "Please enter message", Toast.LENGTH_SHORT).show();

        } else if (lostOrFound.equals("invalid")) {
            Toast.makeText(getContext(), "Lost or Found ?", Toast.LENGTH_SHORT).show();

        }else{

            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Lost And Found");

            String key = databaseReference.push().getKey();

            //get user name
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
            String userName = sharedPreferences.getString("userName", "default_value");

            Map<String, Object> data = new HashMap<>();

            data.put("message", userMessage);
            data.put("status", lostOrFound);
            data.put("user", userName);

            databaseReference.child(key).setValue(data)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Data stored successfully", Toast.LENGTH_SHORT).show();

                            if (getActivity() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(getContext(), "Error storing data", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}