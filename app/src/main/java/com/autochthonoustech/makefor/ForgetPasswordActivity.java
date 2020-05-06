package com.autochthonoustech.makefor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button ResetButton,CancelButton;
    private EditText UserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        initializeFields();

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sendNewPassword();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeFields(){
        UserEmail = findViewById(R.id.email_confirm);
        ResetButton = findViewById(R.id.reset_password_button);
        CancelButton = findViewById(R.id.cancel_password_button);
    }

    private void sendNewPassword(){
        String email = UserEmail.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Email...", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ForgetPasswordActivity.this, "Password Sent Successfully...", Toast.LENGTH_SHORT).show();
                        Intent phoneLoginIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        startActivity(phoneLoginIntent);
                    }else {
                        Toast.makeText(ForgetPasswordActivity.this, "Error Try Again...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
