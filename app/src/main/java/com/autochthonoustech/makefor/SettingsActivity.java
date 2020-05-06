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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button mUpdateAccountSettings;
    private EditText userName, userStatus;
    private String currentUserID;
    private DatabaseReference RootRef;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            currentUserID = mFirebaseUser.getUid();
        }
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.keepSynced(true);

        initializeFields();
        mUpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }

    private void initializeFields() {
        mUpdateAccountSettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);

        Toolbar SettingsToolBar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("Account Settings");
        }
    }

    private void updateSettings() {
        final String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        String setEmail = mFirebaseUser.getEmail();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your user name first....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write your status....", Toast.LENGTH_SHORT).show();
        } else {
            //create and update user data
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);
            profileMap.put("email", setEmail);
            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(setUserName)
                                        .build();
                                if (user != null) {
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        sendUserToMainActivity();
                                                        Toast.makeText(SettingsActivity.this,
                                                                "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this,
                                                                "Error: ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(SettingsActivity.this,
                                        "Error: ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //get last info user for data, name and status
    private void retrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                            String retrieveUserName = "", retrievesStatus = "";

                            Object obj = dataSnapshot.child("name").getValue();
                            if (obj != null) {
                                retrieveUserName = obj.toString();
                            }
                            Object obj1 = dataSnapshot.child("status").getValue();
                            if (obj1 != null) {
                                retrievesStatus = obj1.toString();
                            }
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                            String retrieveUserName = "", retrievesStatus = "";
                            Object obj = dataSnapshot.child("name").getValue();
                            if (obj != null) {
                                retrieveUserName = obj.toString();
                            }
                            Object obj1 = dataSnapshot.child("status").getValue();
                            if (obj1 != null) {
                                retrievesStatus = obj1.toString();
                            }

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                        }
                        else {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this,
                                    "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
