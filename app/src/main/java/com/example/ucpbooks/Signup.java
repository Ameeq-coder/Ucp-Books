package com.example.ucpbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
LottieAnimationView lottieAnimationView;
String uname,upass,uroll,uemail;
TextView already;
Button create;
FirebaseAuth auth;
FirebaseUser mUser;
EditText names,rollnos,email,pass;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        names=findViewById(R.id.name);
        rollnos=findViewById(R.id.rollno);
        email=findViewById(R.id.siemail);
        pass=findViewById(R.id.spass);
        lottieAnimationView=findViewById(R.id.lottiels);
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
        create=findViewById(R.id.created);
        already=findViewById(R.id.aready);
        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup.this,MainActivity.class);
                startActivity(intent);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uemail=email.getText().toString().trim();
                upass = pass.getText().toString().trim();
                uroll=rollnos.getText().toString().trim();
                uname=names.getText().toString().trim();
                if(uemail.isEmpty()||upass.isEmpty()||uroll.isEmpty()||uname.isEmpty()){
                    Toast.makeText(Signup.this, "Enter Your Details", Toast.LENGTH_SHORT).show();
                } else if (upass.length()<8) {
                    Toast.makeText(Signup.this, "Password is Too Short ", Toast.LENGTH_SHORT).show();
                }else{
                    Signups(uemail,upass);
                }
            }
        });
    }
    public void Signups(String uemail, String upass) {
        ProgressDialog dialog = new ProgressDialog( Signup.this);
        dialog.setMessage("Creating Account");
        dialog.show();
        auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   Realtime();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Signup.this, "Account Already Exist", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
    }
    public void Realtime() {
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("User Profiles");
        AddData obj=new AddData(uname,uemail,upass,uroll);
        databaseReference.push().setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(Signup.this,NavigationDrawer.class);
                    startActivity(intent);
                    Toast.makeText(Signup.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Signup.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent= new Intent(this,NavigationDrawer.class);
            startActivity(intent);
        }else{
            //No User is Logged in
        }
    }
}