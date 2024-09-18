package com.example.busbookingapplication.LostAndFound.Fragment.Comment;

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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MainViewHolder>{

    Context context;
    ArrayList<CommentModel> list;

    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment_card, parent, false);
        return new CommentAdapter.MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MainViewHolder holder, int position) {
        CommentModel model = list.get(position);

        // Set item name
        holder.comment.setText(model.getComment());
        holder.userName.setText(model.getUserName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView comment, userName;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.comment);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
