package com.autochthonoustech.makefor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autochthonoustech.makefor.PersonalQueueActivity;
import com.autochthonoustech.makefor.R;
import com.autochthonoustech.makefor.model.People;
import com.autochthonoustech.makefor.model.Queue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_ID;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_NAME;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_ID;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_NAME;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_STATUS;

public class FindQueueAdapter extends RecyclerView.Adapter<FindQueueAdapter.FindQueueViewHolder> {

    private final List<Queue> userSearch;
    private DatabaseReference ContactRef, RootRef;
    private final Context mContext;
    private String currentUser, currentUserName, currentUserStatus, listId;
    private ValueEventListener mListener;

    public FindQueueAdapter(List<Queue> userSearch, Context context) {
        this.userSearch = userSearch;
        this.mContext = context;
    }

    public static class FindQueueViewHolder extends RecyclerView.ViewHolder {
        final TextView userName;
        final TextView userStatus;
        final TextView numberInQueue;
        final TextView myNumberInQueue;
        final Button addQueue;

        public FindQueueViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            addQueue = itemView.findViewById(R.id.add_friend_button);
            numberInQueue = itemView.findViewById(R.id.number_in_queue);
            myNumberInQueue = itemView.findViewById(R.id.my_number);
        }
    }

    @NonNull
    @Override
    public FindQueueAdapter.FindQueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.queue_display_layout, viewGroup, false);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        RootRef = FirebaseDatabase.getInstance().getReference();
        ContactRef = RootRef.child("Contacts").child(currentUser);

        RootRef.child("Users").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue(String.class);
                    currentUserStatus = dataSnapshot.child("status").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return new FindQueueAdapter.FindQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindQueueAdapter.FindQueueViewHolder findFriendViewHolder, final int i) {
        final Queue queue = userSearch.get(i);
        final String id = queue.getGroupId();
        listId = id;
        final String queueName = queue.getName();
        findFriendViewHolder.userName.setText(queue.getName());
        findFriendViewHolder.userStatus.setText(queue.getStatus());
        findFriendViewHolder.numberInQueue.setVisibility(View.VISIBLE);
        mListener = RootRef.child("Queue").child(id).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long i = dataSnapshot.getChildrenCount();
                    if (i == 1) {
                        String ONE_PERSON = "1 Person";
                        findFriendViewHolder.numberInQueue.setText(ONE_PERSON);
                    } else {
                        String people = i + " People";
                        findFriendViewHolder.numberInQueue.setText(people);
                    }
                    int j = 1;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("id").getValue().equals(currentUser)) {
                            findFriendViewHolder.addQueue.setVisibility(View.GONE);
                            findFriendViewHolder.myNumberInQueue.setVisibility(View.VISIBLE);
                            findFriendViewHolder.myNumberInQueue.setText(String.valueOf(j));
                            findFriendViewHolder.numberInQueue.setVisibility(View.GONE);
                            findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent = new Intent(mContext, PersonalQueueActivity.class);
                                    chatIntent.putExtra(INTENT_EXTRA_ID, currentUser);
                                    chatIntent.putExtra(INTENT_EXTRA_NAME, currentUserName);
                                    chatIntent.putExtra(INTENT_EXTRA_STATUS, currentUserStatus);
                                    chatIntent.putExtra(INTENT_EXTRA_QUEUE_NAME, queueName);
                                    chatIntent.putExtra(INTENT_EXTRA_QUEUE_ID, id);
                                    mContext.startActivity(chatIntent);
                                }
                            });
                        }
                        j++;
                    }
                } else {
                    String NO_PPL = "0 People";
                    findFriendViewHolder.numberInQueue.setText(NO_PPL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findFriendViewHolder.addQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String key = RootRef.child("Queue").child(id).child("members").push().getKey();
                People ppl = new People(currentUserName, currentUserStatus, currentUser, key);
                assert key != null;
                RootRef.child("Queue").child(id)
                        .child("members").child(key).setValue(ppl).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Queue Saved", Toast.LENGTH_SHORT).show();
                            findFriendViewHolder.addQueue.setVisibility(View.GONE);
                            ContactRef.child(id).setValue("queue");
                            RootRef.child("Queue Ref").child(currentUser).child(id).setValue("1");
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return userSearch.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull FindQueueViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mListener != null) {
            RootRef.child("Queue").child(listId).child("members").removeEventListener(mListener);
        }

    }
}


