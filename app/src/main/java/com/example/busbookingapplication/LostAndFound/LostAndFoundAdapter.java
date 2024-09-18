package com.example.busbookingapplication.LostAndFound;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.LostAndFound.Fragment.Comment.Comment;
import com.example.busbookingapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LostAndFoundAdapter extends RecyclerView.Adapter<LostAndFoundAdapter.MainViewHolder>{

    Context context;
    ArrayList<LostAndFoundModel> list;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Lost And Found");
    boolean del = false;

    public LostAndFoundAdapter(Context context, ArrayList<LostAndFoundModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public LostAndFoundAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lost_and_found_card, parent, false);
        return new LostAndFoundAdapter.MainViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LostAndFoundAdapter.MainViewHolder holder, int position) {
        LostAndFoundModel model = list.get(position);

        // Set text view details
        holder.user.setText(model.getUserName());
        holder.message.setText(model.getMessage());

        //get user name
        SharedPreferences sharedPreferences = context.getSharedPreferences("CurrentUser", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");

        del = model.isDelete();

        //delete cards

            holder.lostCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(del) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to delete this?");

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

        //default like
        holder.like.setBackgroundResource(R.drawable.edit_text_background);
        holder.like.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
        holder.like.setText("likes " + model.getLike());

        if(model.isLiked()){
            holder.like.setBackgroundResource(R.drawable.liked_background);
            holder.like.setTextColor(ContextCompat.getColor(context, R.color.text));
        }

        //click like button
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference likesRef = reference.child(model.getId()).child("likes");

                likesRef.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            likesRef.child(userName).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    updateLikeCount(holder, likesRef);
                                    holder.like.setBackgroundResource(R.drawable.edit_text_background);
                                    holder.like.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                                }
                            });
                        } else {

                            likesRef.child(userName).setValue(true).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    updateLikeCount(holder, likesRef);
                                    holder.like.setBackgroundResource(R.drawable.liked_background);
                                    holder.like.setTextColor(ContextCompat.getColor(context, R.color.text));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Error: " + databaseError.getMessage());
                    }
                });
            }
        });

        //click comment button
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Comment commentFragment = new Comment();

                Bundle bundle = new Bundle();
                bundle.putString("postId", model.getId());

                commentFragment.setArguments(bundle);

                ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, commentFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void updateLikeCount(LostAndFoundAdapter.MainViewHolder holder, DatabaseReference likesRef) {
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long likeCount = snapshot.getChildrenCount(); // Count the number of likes
                holder.like.setText("likes " + likeCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }


    private void deleteEntry(LostAndFoundModel model) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        // Delete entry from Firebase
        reference.child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView user, message, like, comment;
        LinearLayout lostCard;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            user = itemView.findViewById(R.id.user);
            lostCard = itemView.findViewById(R.id.lostCard);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
