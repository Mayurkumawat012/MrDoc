package com.myteam.mrdoc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    private EditText regemail;
    private EditText regpass;
    private ProgressBar regprogress;
    private EditText conf;
    private FirebaseAuth regauth;
    public void RegOnclick(View view)
    {
        String email = regemail.getText().toString();
        String password = regpass.getText().toString();
        String confpass = conf.getText().toString();
        regprogress.setVisibility(View.VISIBLE);
        if(email.isEmpty()){
            regemail.setError("Please Enter Email");
            regemail.requestFocus();
            Toast.makeText(this, "Blank", Toast.LENGTH_SHORT).show();
            regprogress.setVisibility(View.INVISIBLE);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            regemail.setError("Please Enter Valid Email");
            regemail.requestFocus();
            regemail.setText("");
            regprogress.setVisibility(View.INVISIBLE);
            return;

        }
        if(password.isEmpty()){
            regpass.setError("Please Enter Password");
            regpass.requestFocus();
            Toast.makeText(this, "Blank", Toast.LENGTH_SHORT).show();
            regprogress.setVisibility(View.INVISIBLE);
            return;

        }
        if(password.length()<6)
        {
            regprogress.setVisibility(View.INVISIBLE);
            regpass.setError("Minmum Length is six");
            regpass.requestFocus();
            return;

        }
        if(!(confpass.equals(password)))
        {
            regprogress.setVisibility(View.INVISIBLE);
            conf.setError("Password Doesn't Match");
            conf.requestFocus();
            return;
        }
        regauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(RegisterActivity.this, UserProfile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                    regprogress.setVisibility(View.INVISIBLE);


                }
                else
                {
                    if(task.getException() instanceof FirebaseNetworkException)
                    {
                        Toast.makeText(RegisterActivity.this, "Could Not Connect! Kindly Check Your Network", Toast.LENGTH_SHORT).show();
                        regprogress.setVisibility(View.INVISIBLE);
                    }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(RegisterActivity.this, "User Already Registered", Toast.LENGTH_SHORT).show();
                        regprogress.setVisibility(View.INVISIBLE);

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        regprogress.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        regemail = (EditText) findViewById(R.id.regemaileditText);
        regpass = (EditText) findViewById(R.id.regpasswordedittext);
        conf =(EditText) findViewById(R.id.confirm);
        regprogress=(ProgressBar) findViewById(R.id.progressid);
        regauth = FirebaseAuth.getInstance();
    }
    public void logpage(View view)
    {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }
}
