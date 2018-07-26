package com.myteam.mrdoc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    //Log in page auth
    private FirebaseAuth loginauth;
    private EditText emailtext;
    private EditText passwordtext;
    private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginauth=FirebaseAuth.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        emailtext = (EditText) findViewById(R.id.loginemaileditText);
        passwordtext = (EditText) findViewById(R.id.loginpasswordeditText);
        loginProgress = (ProgressBar) findViewById(R.id.progressid);
    }
    public void loginOnclick(View view)
    {
        String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();
        loginProgress.setVisibility(View.VISIBLE);
        if(email.isEmpty()){
            emailtext.setError("Please Enter Email");
            emailtext.requestFocus();
            Toast.makeText(this, "Blank Field", Toast.LENGTH_SHORT).show();
            loginProgress.setVisibility(View.INVISIBLE);
            return;
        }
        if(password.isEmpty()){
            passwordtext.setError("Please Enter Password");
            passwordtext.requestFocus();
            Toast.makeText(this, "Blank Field", Toast.LENGTH_SHORT).show();
            loginProgress.setVisibility(View.INVISIBLE);
            return;
        }
        loginauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                    loginProgress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    if(task.getException() instanceof FirebaseNetworkException)
                    {
                        Toast.makeText(LoginActivity.this, "Could Not Connect! Kindly Check Your Network", Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        String error =task.getException().getMessage();
                        Toast.makeText(LoginActivity.this,error, Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
    }
    //Forgot Password
    public void forgotpass(View view)
    {
        startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
    }
    //New Register
    public void regpage(View view)
    {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
