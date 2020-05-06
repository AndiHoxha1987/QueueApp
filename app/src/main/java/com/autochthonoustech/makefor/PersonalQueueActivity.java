package com.autochthonoustech.makefor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.autochthonoustech.makefor.model.People;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_ID;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_NAME;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_ID;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_NAME;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_STATUS;

public class PersonalQueueActivity extends AppCompatActivity {

    private String currentUserId, currentQueueName, currentQueueId;
    private TextView name, status, number;
    private ChildEventListener mListener;
    private long l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_queue);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        currentUserId = getIntent().getStringExtra(INTENT_EXTRA_ID);
        String currentUserName = getIntent().getStringExtra(INTENT_EXTRA_NAME);
        String currentUserStatus = getIntent().getStringExtra(INTENT_EXTRA_STATUS);
        currentQueueName = getIntent().getStringExtra(INTENT_EXTRA_QUEUE_NAME);
        currentQueueId = getIntent().getStringExtra(INTENT_EXTRA_QUEUE_ID);

        initializeControllers();

        name.setText(currentUserName);
        status.setText(currentUserStatus);

        FirebaseDatabase.getInstance().getReference().child("Queue").child(currentQueueId)
                .child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    l = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        l++;
                        People ppl = data.getValue(People.class);
                        assert ppl != null;
                        if (currentUserId.equals(ppl.getId())) {
                            String s = Long.toString(l);
                            number.setText(s);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(PersonalQueueActivity.this, "Finished",
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PersonalQueueActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mListener = FirebaseDatabase.getInstance().getReference().child("Queue").child(currentQueueId)
                .child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                People ppl = dataSnapshot.getValue(People.class);
                assert ppl != null;
                if(currentUserId.equals(ppl.getId())){
                    Toast.makeText(PersonalQueueActivity.this, "Finished",
                            Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Queue Ref").child(currentUserId).child(currentQueueId).removeValue();
                    Intent i = new Intent(PersonalQueueActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                l--;
                String s = Long.toString(l);
                number.setText(s);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initializeControllers() {
        Toolbar chatToolBar = findViewById(R.id.personal_queue_activity_bar);
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

        name = findViewById(R.id.personal_name);
        status = findViewById(R.id.personal_status);
        number = findViewById(R.id.personal_queue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mListener!=null){
            FirebaseDatabase.getInstance().getReference().child("Queue").child(currentQueueId)
                    .child("members").removeEventListener(mListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(PersonalQueueActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
