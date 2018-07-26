package com.myteam.mrdoc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{
    //Creating Authentication for Firebase
    private FirebaseAuth mainActivityAuth;
    private FirebaseFirestore fires;
    private String userid;
    static  int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Getting Instance For Firebase
        mainActivityAuth=FirebaseAuth.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fires = FirebaseFirestore.getInstance();


        flag=0;

    }
    //Initiating Starting Activity

    @Override
    protected void onStart() {
        super.onStart();
        //Checking if there is currently logged in user
        FirebaseUser currentUser = mainActivityAuth.getCurrentUser();

        if(currentUser == null)
        {
            startActivity(new Intent(MainActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();


        }
        else
        {
            userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            fires.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {

                        if (task.getResult().exists())
                        {
                            startActivity(new Intent(MainActivity.this,HomepageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    }
                }
            });

            startActivity(new Intent(MainActivity.this,UserProfile.class));
            finish();
        }



    }
}
