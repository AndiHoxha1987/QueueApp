package com.autochthonoustech.makefor.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autochthonoustech.makefor.R;
import com.autochthonoustech.makefor.adapter.FindQueueAdapter;
import com.autochthonoustech.makefor.adapter.MyQueueAdapter;
import com.autochthonoustech.makefor.model.Queue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyQueueFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private DatabaseReference ContactsRef, QueueRef, Queue;
    private FindQueueAdapter findQueueAdapter;
    private MyQueueAdapter myQueueAdapter;
    private final List<Queue> queueList = new ArrayList<>();
    private final List<Queue> myQueueList = new ArrayList<>();
    private RecyclerView FindQueueRecyclerList, MyQueueRecycleList;
    private String currentUserId;
    private ChildEventListener mListener;

    public MyQueueFragment() {
        // Required empty public constructor
    }

    public static MyQueueFragment newInstance(int index) {
        MyQueueFragment fragment = new MyQueueFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_my_queue, container, false);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        Queue = FirebaseDatabase.getInstance().getReference().child("Queue");
        QueueRef = FirebaseDatabase.getInstance().getReference().child("Queue Ref").child(currentUserId);
        findQueueAdapter = new FindQueueAdapter(queueList, getContext());
        myQueueAdapter = new MyQueueAdapter(myQueueList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        FindQueueRecyclerList = itemView.findViewById(R.id.queue_list_fragment);
        FindQueueRecyclerList.setLayoutManager(linearLayoutManager);
        FindQueueRecyclerList.setAdapter(findQueueAdapter);
        FindQueueRecyclerList.setNestedScrollingEnabled(false);
        MyQueueRecycleList = itemView.findViewById(R.id.my_queue_list_fragment);
        MyQueueRecycleList.setLayoutManager(linearLayoutManager1);
        MyQueueRecycleList.setAdapter(myQueueAdapter);
        MyQueueRecycleList.setNestedScrollingEnabled(false);

        return itemView;
    }

    @Override
    public void onStart() {
        super.onStart();
        queueList.clear();
        myQueueList.clear();
        ContactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.exists()) {
                            String s = data.getKey();
                            if (data.getValue().equals("my queue")) {
                                if (s != null) {
                                    Queue.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Queue q = dataSnapshot.getValue(Queue.class);
                                                myQueueList.add(q);
                                                myQueueAdapter.notifyDataSetChanged();
                                                MyQueueRecycleList.setAdapter(myQueueAdapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mListener = QueueRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String key = dataSnapshot.getKey();
                    if (key != null) {
                        Queue.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Queue q = dataSnapshot.getValue(Queue.class);
                                    queueList.add(q);
                                    findQueueAdapter.notifyDataSetChanged();
                                    FindQueueRecyclerList.setAdapter(findQueueAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = dataSnapshot.getKey();
                    if (key != null) {
                        for (int i = 0;i<queueList.size();i++){
                            String qKey = queueList.get(i).getGroupId();
                            if(key.equals(qKey)){
                                queueList.remove(i);
                                findQueueAdapter.notifyDataSetChanged();
                                FindQueueRecyclerList.setAdapter(findQueueAdapter);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mListener!= null){
            QueueRef.removeEventListener(mListener);
        }
    }
}
