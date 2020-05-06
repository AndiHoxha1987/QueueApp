package com.autochthonoustech.makefor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.autochthonoustech.makefor.adapter.QueueActivityAdapter;
import com.autochthonoustech.makefor.model.People;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_ID;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_NAME;

public class QueueActivity extends AppCompatActivity {

    private DatabaseReference Queue;
    private QueueActivityAdapter findQueueAdapter;
    private final List<People> myQueueList = new ArrayList<>();
    private RecyclerView FindQueueRecyclerList;
    private String currentQueueId;
    private String currentQueueName;
    private ChildEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        currentQueueId = getIntent().getStringExtra(INTENT_EXTRA_QUEUE_ID);
        currentQueueName = getIntent().getStringExtra(INTENT_EXTRA_QUEUE_NAME);

        Queue = FirebaseDatabase.getInstance().getReference().child("Queue").child(currentQueueId);
        initializeControllers();
        myQueueList.clear();

        findQueueAdapter = new QueueActivityAdapter(myQueueList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        FindQueueRecyclerList = findViewById(R.id.queue_recycle_activity);
        FindQueueRecyclerList.setLayoutManager(linearLayoutManager);
        FindQueueRecyclerList.setAdapter(findQueueAdapter);
        mListener = Queue.child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    People ppl = dataSnapshot.getValue(People.class);
                    myQueueList.add(ppl);
                    findQueueAdapter.notifyDataSetChanged();
                    FindQueueRecyclerList.setAdapter(findQueueAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {

                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        if (position == 0) {
                            final People myList = findQueueAdapter.getItemsAtPosition(position);
                            String key = myList.getKey();
                            deleteItem(key);
                            myQueueList.remove(myList);
                            findQueueAdapter.notifyDataSetChanged();
                            FindQueueRecyclerList.setAdapter(findQueueAdapter);
                        }else{
                            findQueueAdapter.notifyDataSetChanged();
                            FindQueueRecyclerList.setAdapter(findQueueAdapter);
                        }
                    }
                }).attachToRecyclerView(FindQueueRecyclerList);
    }

    private void initializeControllers() {
        Toolbar chatToolBar = findViewById(R.id.queue_activity_bar);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(currentQueueName);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (layoutInflater != null && actionBar != null) {
            View actionBarView = layoutInflater.inflate(R.layout.app_bar_layout, null);
            actionBar.setCustomView(actionBarView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mListener!=null){
            Queue.child("members").removeEventListener(mListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.queue_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(QueueActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.delete_all:
                deleteAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAll() {
        Queue.child("members").removeValue();
    }

    private void deleteItem(String key) {
        Queue.child("members").child(key).removeValue();
    }
}
