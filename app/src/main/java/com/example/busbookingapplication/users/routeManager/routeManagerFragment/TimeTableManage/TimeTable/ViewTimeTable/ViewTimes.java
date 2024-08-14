package com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.TimeTable.ViewTimeTable;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.busbookingapplication.R;
public class ViewTimes extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_times, container, false);
    }
}