package com.autochthonoustech.makefor.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
public class FindQueueFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView FindQueueRecyclerList;
    private DatabaseReference QueueRef;
    private EditText enterName, enterCode;
    private TextView noResult;
    private FindQueueAdapter findQueueAdapter;
    private final List<Queue> queueList = new ArrayList<>();
    private String currentUser;

    public FindQueueFragment() {
        // Required empty public constructor
    }

    public static FindQueueFragment newInstance(int index) {
        FindQueueFragment fragment = new FindQueueFragment();
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
        View itemView = inflater.inflate(R.layout.fragment_find_queue, container, false);
        QueueRef = FirebaseDatabase.getInstance().getReference().child("Queue");
        QueueRef.keepSynced(true);
        findQueueAdapter = new FindQueueAdapter(queueList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUser = mAuth.getCurrentUser().getUid();
        }
        enterName = itemView.findViewById(R.id.search_queue_text_input);
        enterCode = itemView.findViewById(R.id.search_code_text_input);
        Button searchButtonByName = itemView.findViewById(R.id.search_queue_button);
        Button searchButtonByCode = itemView.findViewById(R.id.search_by_code_button);
        noResult = itemView.findViewById(R.id.no_result);
        FindQueueRecyclerList = itemView.findViewById(R.id.find_queue_recycler_list);
        FindQueueRecyclerList.setLayoutManager(linearLayoutManager);
        FindQueueRecyclerList.setAdapter(findQueueAdapter);
        searchButtonByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueList.clear();
                findQueueAdapter.notifyDataSetChanged();
                FindQueueRecyclerList.setAdapter(findQueueAdapter);
                searchQueueByName();
            }
        });

        searchButtonByCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueList.clear();
                findQueueAdapter.notifyDataSetChanged();
                FindQueueRecyclerList.setAdapter(findQueueAdapter);
                searchQueueByCode();
            }
        });

        return itemView;
    }

    private void searchQueueByName() {
        final String email = enterName.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "first write your text...", Toast.LENGTH_SHORT).show();
        } else {
            QueueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final Queue queue = postSnapshot.getValue(Queue.class);
                        assert queue != null;
                        if (!currentUser.equals(queue.getAdmin())) {
                            if (queue.getName().contains(email)) {
                                noResult.setVisibility(View.GONE);
                                queueList.add(queue);
                                findQueueAdapter.notifyDataSetChanged();
                                FindQueueRecyclerList.setAdapter(findQueueAdapter);
                            } else {
                                if (queueList.size() == 0) {
                                    noResult.setVisibility(View.VISIBLE);
                                }
                            }
                        }else {
                            noResult.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            enterName.setText("");
        }
    }

    private void searchQueueByCode() {
        final String code = enterCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getContext(), "first write your text...", Toast.LENGTH_SHORT).show();
        } else {
            QueueRef.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final Queue queue = postSnapshot.getValue(Queue.class);
                        assert queue != null;
                        if (!currentUser.equals(queue.getAdmin())) {
                            if (queue.getStatus().contains(code)) {
                                noResult.setVisibility(View.GONE);
                                queueList.add(queue);
                                findQueueAdapter.notifyDataSetChanged();
                                FindQueueRecyclerList.setAdapter(findQueueAdapter);
                            } else {
                                if (queueList.size() == 0) {
                                    noResult.setVisibility(View.VISIBLE);
                                }
                            }
                        }else {
                            noResult.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            enterCode.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        queueList.clear();
    }

}
