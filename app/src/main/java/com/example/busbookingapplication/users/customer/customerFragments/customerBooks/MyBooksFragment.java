package com.example.busbookingapplication.users.customer.customerFragments.customerBooks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.busbookingapplication.R;

import java.util.Objects;

public class MyBooksFragment extends Fragment {

    String tab = "myBooks";
    TextView myBooks, passBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_books, container, false);

        tab();

        //tab changing text view's decoration
        myBooks = rootView.findViewById(R.id.myBooks);
        passBooks = rootView.findViewById(R.id.passBooks);

        //click my books button
        myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "myBooks";
                tab();
            }
        });

        //click past books button
        passBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "passBooks";
                tab();
            }
        });


        return rootView;
    }

    public void tab(){

        if(Objects.equals(tab, "myBooks")){
            myBooks.setTextSize(17);
            passBooks.setTextSize(13);

        } else if (Objects.equals(tab, "passBooks")) {
            myBooks.setTextSize(13);
            passBooks.setTextSize(17);
        }
    }
}