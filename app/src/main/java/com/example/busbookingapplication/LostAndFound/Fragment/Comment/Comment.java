package com.example.busbookingapplication.LostAndFound.Fragment.Comment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busbookingapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Comment extends Fragment {

    RecyclerView commentList;
    CommentAdapter myAdapter;
    ArrayList<CommentModel> list;
    ImageButton send;
    EditText typedComment;
    TextView noComment;
    String postId;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Lost And Found");
    private GestureDetectorCompat gestureDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);


        gestureDetector = new GestureDetectorCompat(getContext(), new SwipeGestureListener());

        // RecyclerView setup
        commentList = rootView.findViewById(R.id.commentList);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new CommentAdapter(getContext(), list);
        commentList.setAdapter(myAdapter);

        // EditText setup
        typedComment = rootView.findViewById(R.id.typedComment);

        // No comment TextView setup
        noComment = rootView.findViewById(R.id.noComment);

        // Send button setup
        send = rootView.findViewById(R.id.send);
        send.setOnClickListener(v -> addComment());

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }

        loadComment();

        rootView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        return rootView;
    }

    // Add comment method
    public void addComment() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");
        String comment = typedComment.getText().toString().trim();

        if (!comment.isEmpty()) {
            if (postId != null) {
                DB.child(postId).child("comment").child(userName).setValue(comment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        typedComment.setText("");
                    }
                });
            }
        }
    }

    // Load comments from Firebase
    public void loadComment() {
        if (postId != null) {
            // Get current user name and type
            SharedPreferences prf = getContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
            String currentName = prf.getString("userName", "");
            String userType = prf.getString("type", "");

            DB.child(postId).child("comment").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();

                    // Iterate through comments
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String user = snapshot.getKey();
                        String comment = snapshot.getValue(String.class);

                        CommentModel mainModel = new CommentModel();
                        mainModel.setComment(comment);
                        mainModel.setUserName(user);
                        mainModel.setId(postId);

                        if (currentName.equals(user) || userType.equals("routeManager")) {
                            mainModel.setDelete(true);
                        }

                        list.add(mainModel);
                    }

                    // Show or hide "no comments" text
                    if (list.isEmpty()) {
                        noComment.setVisibility(View.VISIBLE);
                    } else {
                        noComment.setVisibility(View.GONE);
                    }

                    myAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Error: " + databaseError.getMessage());
                }
            });
        }
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();

            // Check if the swipe is vertical and downward
            if (Math.abs(diffY) > Math.abs(diffX)) {
                if (diffY > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    // Detected swipe down - close the fragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(Comment.this)
                            .commit();
                    return true;
                }
            }
            return false;
        }
    }
}
