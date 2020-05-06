package com.autochthonoustech.makefor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.autochthonoustech.makefor.model.Queue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewQueueActivity extends AppCompatActivity {

    private Button createQueueButton;
    private EditText queueName, queueStatus;
    private String currentUserID;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_queue);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            currentUserID = mFirebaseUser.getUid();
        }
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.keepSynced(true);

        initializeFields();
        createQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });
    }

    private void initializeFields() {
        createQueueButton = findViewById(R.id.create_queue_button);
        queueName = findViewById(R.id.set_queue_name);
        queueStatus = findViewById(R.id.set_queue_status);

        Toolbar SettingsToolBar = findViewById(R.id.create_queue_toolbar);
        setSupportActionBar(SettingsToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("Create Queue");
        }
    }

    private void createGroup() {
        final String setQueueName = queueName.getText().toString();
        final String setQueueStatus = queueStatus.getText().toString();
        final String queueKey = RootRef.child("Queue").push().getKey();

        if (TextUtils.isEmpty(setQueueName)) {
            Toast.makeText(this, "Please write your queue name first....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setQueueStatus)) {
            Toast.makeText(this, "Please write your status....", Toast.LENGTH_SHORT).show();
        } else {
            Queue newGroup = new Queue(currentUserID, setQueueName, setQueueStatus, queueKey, null);
            if (queueKey != null) {
                RootRef.child("Queue").child(queueKey).setValue(newGroup)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateNewQueueActivity.this,
                                            "Queue Created Successfully...", Toast.LENGTH_SHORT).show();
                                    RootRef.child("Contacts").child(currentUserID).child(queueKey).setValue("my queue");
                                    Intent intent = new Intent(CreateNewQueueActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(CreateNewQueueActivity.this,
                                            "Error: Try Again  ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

