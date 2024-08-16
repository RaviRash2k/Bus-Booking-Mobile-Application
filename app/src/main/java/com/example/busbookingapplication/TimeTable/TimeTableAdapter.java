package com.example.busbookingapplication.TimeTable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.UpdateTimeTable;

import java.util.ArrayList;


public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.MainViewHolder>{

    Context context;
    ArrayList<TimeTableModel> list;

    public TimeTableAdapter(Context context, ArrayList<TimeTableModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TimeTableAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.timetable_card, parent, false);
        return new TimeTableAdapter.MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableAdapter.MainViewHolder holder, int position) {
        TimeTableModel model = list.get(position);

        // Set text view details
        holder.busRoute.setText(String.format("%s - %s", model.getStartLocation(), model.getEndLocation()));
        holder.bus.setText(model.getBusNo());
        holder.timeTableDay.setText(model.getDate());
        holder.timeTableStart.setText(model.getStartTime());
        holder.timeTableEnd.setText(model.getEndTime());
        holder.ticketPrice.setText(model.getTicketPrice());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView timeTableDay, timeTableStart, timeTableEnd, ticketPrice, busRoute, bus;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTableDay = itemView.findViewById(R.id.timeTableDay);
            timeTableStart = itemView.findViewById(R.id.timeTableStart);
            timeTableEnd = itemView.findViewById(R.id.timeTableEnd);
            ticketPrice = itemView.findViewById(R.id.ticketPrice);
            busRoute = itemView.findViewById(R.id.busRoute);
            bus = itemView.findViewById(R.id.bus);
        }
    }
}
