package com.example.busbookingapplication.users.customer.customerFragments.customerHome.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.customer.customerFragments.customerHome.CustomerSearchedSlots.BookingTicket.BookingTicket;

import java.util.ArrayList;

public class CustomerSearchedAdapter extends RecyclerView.Adapter<CustomerSearchedAdapter.MainViewHolder>{
    Context context;
    ArrayList<CustomerSearchedModel> list;

    public CustomerSearchedAdapter(Context context, ArrayList<CustomerSearchedModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.time_slot_card_customer, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        CustomerSearchedModel model = list.get(position);

        // Set bus num
        holder.bus.setText(model.getBus());

        // Set time
        holder.time.setText(model.getTime());

        // Set ticket price
        holder.ticketPrice.setText(model.getTicket());

        // Set available seats
        holder.availableSeats.setText(model.getSeats());

        //click book button
        holder.bookTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookingTicket.class);
                intent.putExtra("route", model.getLocation());
                intent.putExtra("date", model.getDate());
                intent.putExtra("bus", model.getBus());
                intent.putExtra("ticket", model.getTicket());
                intent.putExtra("id", model.getId());
                intent.putExtra("start", model.getStart());
                intent.putExtra("time", model.getTime());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView bus, availableSeats, ticketPrice, time;
        Button bookTicket;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            bus = itemView.findViewById(R.id.bus);
            time = itemView.findViewById(R.id.time);
            ticketPrice = itemView.findViewById(R.id.ticketPrice);
            availableSeats = itemView.findViewById(R.id.availableSeats);
            bookTicket = itemView.findViewById(R.id.bookTicket);
        }
    }
}
