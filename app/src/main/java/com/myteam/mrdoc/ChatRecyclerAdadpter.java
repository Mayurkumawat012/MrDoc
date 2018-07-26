package com.myteam.mrdoc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatRecyclerAdadpter extends RecyclerView.Adapter<ChatRecyclerAdadpter.ViewHolder>
        {
            private FirebaseFirestore firebaseFirestore;
            private FirebaseAuth firebaseAuth;

            public List<Chats> chatsList;
            public Context context;
            private TextView textView;
            private String userId;

            public ChatRecyclerAdadpter(List<Chats> chatsList) {
                this.chatsList = chatsList;
            }

            @NonNull
            @Override
            public ChatRecyclerAdadpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
                context = parent.getContext();
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseAuth = FirebaseAuth.getInstance();
                userId = firebaseAuth.getCurrentUser().getUid();
                return new ChatRecyclerAdadpter.ViewHolder(view);

            }

            @Override
            public void onBindViewHolder(@NonNull ChatRecyclerAdadpter.ViewHolder holder, int position) {
                holder.setIsRecyclable(false);
                firebaseAuth = FirebaseAuth.getInstance();
                String user_id = firebaseAuth.getCurrentUser().getUid();
                String other_user_id = chatsList.get(position).getUser_id();
                String chatMessage = chatsList.get(position).getMessage();
                holder.setChat_message(chatMessage,user_id,other_user_id);

            }

            @Override
            public int getItemCount() {
                if(chatsList != null) {

                    return chatsList.size();

                } else {

                    return 0;

                }

            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                private View mView;

                private TextView chat_message,temp;
                private ConstraintLayout relativeLayout;
                public ViewHolder(View itemView) {
                    super(itemView);
                    mView = itemView;
                }

                public void setChat_message(String chatMessage, String user_id, String other_user_id) {
                    if(user_id.equals(other_user_id))
                    {
                        chat_message = mView.findViewById(R.id.selfText);
                        relativeLayout = mView.findViewById(R.id.chatuser);
                    }
                    else
                    {
                        relativeLayout = mView.findViewById(R.id.chatself);
                        chat_message = mView.findViewById(R.id.otherchatText);
                    }
                    relativeLayout.getLayoutParams().height = 0;
                    relativeLayout.getLayoutParams().width = 0;
                    relativeLayout.setEnabled(false);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    chat_message.setText(chatMessage);
                }
            }
        }
