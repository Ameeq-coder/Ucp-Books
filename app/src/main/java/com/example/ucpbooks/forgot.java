package com.example.ucpbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgot extends AppCompatActivity {
    TextView back;
    Button Forgot;
    EditText foremail;
    String emails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        Forgot=findViewById(R.id.sendcode);
        foremail=findViewById(R.id.femail);
        back=findViewById(R.id.blogin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(forgot.this,MainActivity.class);
                startActivity(intent);
            }
        });
        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emails=foremail.getText().toString().trim();
                if(emails.isEmpty()){
                    Toast.makeText(forgot.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
                }
                else {
                    forgotpass();
                }


            }
        });
    }

    private void forgotpass() {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forgot.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(forgot.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(forgot.this, "Error", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    }



