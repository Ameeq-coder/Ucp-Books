package com.example.ucpbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
LottieAnimationView lottieAnimationView;
EditText uemail,upass;
TextView signup,forgot;
String email,pass;
Button login;
FirebaseAuth auth;
public static final String SHARED_PREFS="sharedPrefs";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lottieAnimationView=findViewById(R.id.logina);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);
        uemail=findViewById(R.id.loemail);
        upass=findViewById(R.id.lopass);
        signup=findViewById(R.id.noacc);
        forgot=findViewById(R.id.forgots);
        login=findViewById(R.id.logined);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Signup.class);
                startActivity(intent);
            }
        });



            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = uemail.getText().toString().trim();
                    pass = upass.getText().toString().trim();
                    if (email.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Enter Your Details", Toast.LENGTH_SHORT).show();
                    } else {
                        Login();

                    }
                }
            });
            forgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,forgot.class);
                    startActivity(intent);
                }
            });
        }


    public void Login() {
         ProgressDialog dialog = new ProgressDialog( MainActivity.this);
        dialog.setMessage("Logging in");
        dialog.show();
        auth=FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(MainActivity.this,NavigationDrawer.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Wrong Email Or Password", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

}