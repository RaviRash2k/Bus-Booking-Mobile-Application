package com.example.busbookingapplication.LostAndFound.Fragment.Comment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.LostAndFound.LostAndFoundModel;
import com.example.busbookingapplication.R;
import com.example.busbookingapplication.users.routeManager.routeManagerFragment.TimeTableManage.UpdateTimeTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        //delete comment
        boolean del = model.isDelete();
        holder.commentCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(del){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want delete comment?");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteEntry(model);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView comment, userName;
        LinearLayout commentCard;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.comment);
            userName = itemView.findViewById(R.id.userName);
            commentCard = itemView.findViewById(R.id.commentCard);
        }
    }

    private void deleteEntry(CommentModel model) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        // Delete entry from Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Lost And Found").child(model.getId()).child("comment").child(model.getUserName());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    progressDialog.dismiss();
                    list.remove(model);
                    notifyDataSetChanged();
                }
            }
        });
    }
}
