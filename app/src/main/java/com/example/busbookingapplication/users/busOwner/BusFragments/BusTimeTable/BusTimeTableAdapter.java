package com.example.busbookingapplication.users.busOwner.BusFragments.BusTimeTable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.busOwner.BusFragments.BusTimeTable.BookedSeats.BookedSeats;

import java.util.ArrayList;

public class BusTimeTableAdapter extends RecyclerView.Adapter<BusTimeTableAdapter.MainViewHolder>{
    Context context;
    ArrayList<BusTimeTableModel> list;

    public BusTimeTableAdapter(Context context, ArrayList<BusTimeTableModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BusTimeTableAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.bus_card, parent, false);
        return new BusTimeTableAdapter.MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BusTimeTableAdapter.MainViewHolder holder, int position) {
        BusTimeTableModel model = list.get(position);

        holder.bus.setText(model.getBus());
        holder.time.setText(String.format("Time - %s", model.getTime()));
        holder.date.setText(String.format("Date - %s", model.getDate()));
        holder.route.setText(model.getRoute());
        holder.price.setText(String.format("Price - %s LKR", model.getPrice()));
        holder.seat.setText(String.format("Available Seats - %s", model.getSeat()));

        holder.timeSlotCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookedSeats.class);

                String bookedSeats = String.join(", ", model.getBookedSeats());

                intent.putExtra("bookedSeats", bookedSeats);
                intent.putExtra("route", model.getRoute());
                intent.putExtra("time", model.getTime());
                intent.putExtra("date", model.getDate());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView time, date, price, route, bus, seat;
        LinearLayout timeSlotCard;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            route = itemView.findViewById(R.id.route);
            bus = itemView.findViewById(R.id.bus);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            seat = itemView.findViewById(R.id.avl);
            price = itemView.findViewById(R.id.price);
            timeSlotCard = itemView.findViewById(R.id.timeSlotCard);
        }
    }

}
