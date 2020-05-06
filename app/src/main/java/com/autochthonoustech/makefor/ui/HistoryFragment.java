package com.autochthonoustech.makefor.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.autochthonoustech.makefor.R;
import com.autochthonoustech.makefor.adapter.FindQueueAdapter;
import com.autochthonoustech.makefor.model.Queue;
import com.google.firebase.auth.FirebaseAuth;
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
public class HistoryFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String QUEUE = "queue";
    private static final String QUEUE_REF = "Queue";
    private static final String CONTACTS = "Contacts";

    private DatabaseReference ContactsRef, UsersRef;
    private FindQueueAdapter findQueueAdapter;
    private final List<Queue> queueList = new ArrayList<>();
    private RecyclerView FindFriendsRecyclerList;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(int index) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contactsFragmentView = inflater.inflate(R.layout.fragment_history, container, false);

        findQueueAdapter = new FindQueueAdapter(queueList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        FindFriendsRecyclerList = contactsFragmentView.findViewById(R.id.contacts_list_fragment);
        FindFriendsRecyclerList.setLayoutManager(linearLayoutManager);
        FindFriendsRecyclerList.setAdapter(findQueueAdapter);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String currentUserID = mAuth.getCurrentUser().getUid();
            ContactsRef = FirebaseDatabase.getInstance().getReference().child(CONTACTS).child(currentUserID);
            ContactsRef.keepSynced(true);
        }
        UsersRef = FirebaseDatabase.getInstance().getReference().child(QUEUE_REF);
        UsersRef.keepSynced(true);
        queueList.clear();
        ContactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.exists()) {
                        if (data.getValue().equals(QUEUE)) {
                            String queue = data.getKey();
                            assert queue != null;
                            UsersRef.child(queue).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Queue queue = dataSnapshot.getValue(Queue.class);
                                    queueList.add(queue);
                                    findQueueAdapter.notifyDataSetChanged();
                                    FindFriendsRecyclerList.setAdapter(findQueueAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        queueList.clear();

        return contactsFragmentView;
    }

}
