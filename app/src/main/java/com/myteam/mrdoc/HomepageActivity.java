package com.myteam.mrdoc;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mauth;
    private BottomNavigationView mainbottomNav;
    private Toolbar mainToolbar;
    private HomeFragment homeFragment;
    private ChatFragment chatFragment;
    private FloatingActionButton button,chatButton;
    private NotificationFragment notificationFragment;
    private CircleImageView circleImageView;
    private TextView usertext;
    private String userid;
    private FirebaseFirestore firebaseFirestore;
    private NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainbottomNav = findViewById(R.id.mainBottomNav);
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        mauth = FirebaseAuth.getInstance();
        userid = mauth.getCurrentUser().getUid();


        getSupportActionBar().setTitle("Mr.Doc");
        mNavigationView = findViewById(R.id.nav_view);
        circleImageView =(CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.imageheader);

        usertext   = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.username);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String name = task.getResult().getString("name");
                    String image = task.getResult().getString("image");
                    usertext.setText(name);
                    RequestOptions placeholderRequest = new RequestOptions();
                    placeholderRequest.placeholder(R.drawable.untitled);

                     Glide.with(HomepageActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(circleImageView);
                }
            }
        });
        //Initialising Fragments
        homeFragment = new HomeFragment();
        mauth = FirebaseAuth.getInstance();
        notificationFragment = new NotificationFragment();
        chatFragment = new ChatFragment();
        chatButton = findViewById(R.id.floatingActionButton);
        button = findViewById(R.id.floatingActionButton2);
        initializeFragment();

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomepageActivity.this,ChatbotActivity.class));
            }
        });

        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
                switch (item.getItemId())
                {
                    case R.id.homeid:
                        button.setEnabled(true);
                        button.setVisibility(View.VISIBLE);
                        chatButton.setVisibility(View.INVISIBLE);
                        chatButton.setEnabled(false);
                        replaceFragment(homeFragment, currentFragment);
                        return true;



                    case R.id.notificationid:
                        button.setVisibility(View.INVISIBLE);
                        chatButton.setVisibility(View.INVISIBLE);
                        chatButton.setEnabled(false);
                        button.setEnabled(false);
                        replaceFragment(notificationFragment, currentFragment);
                        return true;

                    case R.id.chatid:
                        chatButton.setEnabled(true);
                        chatButton.setVisibility(View.VISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        button.setEnabled(false);
                        replaceFragment(chatFragment, currentFragment);
                        return true;

                    default:
                        return false;
                }

            }
        });
    }
    public void addPost(View view)
    {
        startActivity(new Intent(HomepageActivity.this,newPost.class));
    }
    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, chatFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(chatFragment);

        fragmentTransaction.commit();

    }
    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(chatFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == chatFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(chatFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:

                mauth.signOut();
                finish();
                startActivity(new Intent(HomepageActivity.this,MainActivity.class));
                System.exit(0);
                return true;

            case R.id.user_profile_button:

                Intent settingsIntent = new Intent(HomepageActivity.this, UserProfile.class);
                startActivity(settingsIntent);
                return true;

            case R.id.menu_refresh:
                startActivity(getIntent());
                finish();
                return true;

            default:
                return false;


        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
