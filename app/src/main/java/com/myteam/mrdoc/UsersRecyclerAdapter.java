package com.myteam.mrdoc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder>
{
    public List<Users> users_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView nametext;
    public Context context;
    private CircleImageView circleImageView;
    private String User_id;
    private RelativeLayout relativeLayout;


    public UsersRecyclerAdapter(List<Users> users_list) {this.users_list=users_list;
    }

    @NonNull
    @Override
    public UsersRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();

        return new UsersRecyclerAdapter.ViewHolder(view);

        }

    @Override
    public void onBindViewHolder(@NonNull final UsersRecyclerAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);


        String name = users_list.get(position).getName();
        String image = users_list.get(position).getImage();
      final String userid = users_list.get(position).getUserid();
        User_id= firebaseAuth.getCurrentUser().getUid();

        if(userid.equals(User_id)) {
            holder.hide(userid);
        }


           if(!userid.equals(User_id))
           {
               relativeLayout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       holder.newchat(users_list.get(position).getUserid());
                   }
               });

           }
        holder.setUserData(name,image);

    }

    @Override
    public int getItemCount() {
        return users_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            firebaseAuth = FirebaseAuth.getInstance();
            String user_id = firebaseAuth.getCurrentUser().getUid();
            relativeLayout = mView.findViewById(R.id.main_user_cont);

        }

        public void setUserData(String name, String image) {
            circleImageView = mView.findViewById(R.id.user_single_image);
            nametext = mView.findViewById(R.id.user_single_name); RequestOptions requestOptions = new RequestOptions();


            requestOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(circleImageView);
            nametext.setText(name);
        }

        public void hide(String pastuserid) {

                relativeLayout = mView.findViewById(R.id.main_user_cont);
            firebaseAuth = FirebaseAuth.getInstance();
            String user_id = firebaseAuth.getCurrentUser().getUid();
            if(pastuserid.equals(user_id))
            {

                relativeLayout.getLayoutParams().height = 0;
                relativeLayout.getLayoutParams().width = 0;
                relativeLayout.setEnabled(false);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
            else{


                relativeLayout.setEnabled(true);
                relativeLayout.setVisibility(View.VISIBLE);
            }



        }

        public void newchat(String pastuserid) {


            context.startActivity(new Intent(context,ChatActivity.class).putExtra("pastuserid",pastuserid));

        }
    }
}
