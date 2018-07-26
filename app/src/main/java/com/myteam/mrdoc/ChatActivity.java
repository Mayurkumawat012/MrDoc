package com.myteam.mrdoc;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private Toolbar toolbar;
    private CircleImageView circleImageView;
    private String userid;
    private TextView username;
    private EditText chat_field;
    private ImageView chat_post_btn;

    private RecyclerView chat_list;
    private ChatRecyclerAdadpter chatRecyclerAdapter;
    private List<Chats> chatsList;

    private FirebaseAuth firebaseAuth;

    private String current_user_id;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        getSupportActionBar().setTitle("");

        firebaseFirestore = FirebaseFirestore.getInstance();
        userid=getIntent().getExtras().getString("pastuserid");

        circleImageView = findViewById(R.id.userpic_display);
        username = findViewById(R.id.username_display);
        firebaseFirestore.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                       username.setText(name);

                        RequestOptions requestOptions = new RequestOptions();


                        requestOptions.placeholder(R.drawable.profile_placeholder);
                        Glide.with(ChatActivity.this).applyDefaultRequestOptions(requestOptions).load(image).into(circleImageView);


                    }
                }
            }
        });



        chat_field = findViewById(R.id.chat_field);
        chat_post_btn = findViewById(R.id.chat_post_btn);
        chat_list = findViewById(R.id.chat_recylcer_View);
        chatsList = new ArrayList<>();
        chatRecyclerAdapter = new ChatRecyclerAdadpter(chatsList);
        chat_list.setHasFixedSize(true);
        chat_list.setLayoutManager(new LinearLayoutManager(this));
        chat_list.setAdapter(chatRecyclerAdapter);
        firebaseFirestore.collection("users/" +   userid+ "/" + current_user_id).orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String chatId = doc.getDocument().getId();
                                    Chats chats = doc.getDocument().toObject(Chats.class);
                                    chatsList.add(chats);
                                    chatRecyclerAdapter.notifyDataSetChanged();

                                }
                            }

                        }

                    }
                });

        firebaseFirestore.collection("messages/" + current_user_id + "/" + userid).orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                                    if (!documentSnapshots.isEmpty()) {

                                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                String chatId = doc.getDocument().getId();
                                                                Chats chats = doc.getDocument().toObject(Chats.class);
                                                                chatsList.add(chats);
                                                                chatRecyclerAdapter.notifyDataSetChanged();


                                                                chat_list.smoothScrollToPosition(chatRecyclerAdapter.getItemCount()-1);



                                }
                            }

                        }

                    }
                });
        chat_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = chat_field.getText().toString();


                final Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", comment_message);
                commentsMap.put("user_id", current_user_id);
                commentsMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("messages/" + current_user_id + "/" + userid).add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if(!task.isSuccessful()){

                            Toast.makeText(ChatActivity.this, "Error Sending Message : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        } else { firebaseFirestore.collection("messages/" + userid + "/" + current_user_id).add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if(!task.isSuccessful()){

                                    Toast.makeText(ChatActivity.this, "Error Sending Message : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                            chat_field.setText("");

                        }

                    }
                });
                chat_field.setText("");
            }
        });
        chat_field.setText("");
    }
}
