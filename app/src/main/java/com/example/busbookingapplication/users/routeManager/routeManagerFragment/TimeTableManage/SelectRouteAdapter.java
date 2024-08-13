package com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SelectRouteAdapter extends RecyclerView.Adapter<SelectRouteAdapter.MainViewHolder>{

    Context context;
    ArrayList<SelectRouteModel> list;

    public SelectRouteAdapter(Context context, ArrayList<SelectRouteModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SelectRouteAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.one_route, parent, false);
        return new SelectRouteAdapter.MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectRouteAdapter.MainViewHolder holder, int position) {
        SelectRouteModel model = list.get(position);

        // Set item name
        holder.routeText.setText(model.getRoute());

        //click delete button
        holder.deleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //I will write delete function
            }
        });

        //Click single route item
        holder.singleRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UpdateTimeTable.class);
                i.putExtra("route", model.getRoute());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView routeText;
        ImageButton deleteRoute;
        LinearLayout singleRoute;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            routeText = itemView.findViewById(R.id.routeText);
            deleteRoute = itemView.findViewById(R.id.deleteRoute);
            singleRoute = itemView.findViewById(R.id.singleRoute);
        }
    }
}
