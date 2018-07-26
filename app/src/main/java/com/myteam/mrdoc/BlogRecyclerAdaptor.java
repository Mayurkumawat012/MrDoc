package com.myteam.mrdoc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdaptor extends RecyclerView.Adapter<BlogRecyclerAdaptor.ViewHolder>{
    public List<BlogPost> blog_list;
    public Context context;
    private ImageView blogImageView;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private CircleImageView circleImageView;
    private TextView textView,blogdate,commentnum;
    private Button delpost;
    private ImageView blogCommentBtn;
    public BlogRecyclerAdaptor(List<BlogPost> blog_list)
    {
        this.blog_list = blog_list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);
        String image_url = blog_list.get(position).getImage_url();
       String thumbUri = blog_list.get(position).getImage_thumb();
       String user_id = blog_list.get(position).getUser_id();
        holder.setBlogImage(image_url,thumbUri);
        Date date= blog_list.get(position).getTimestamp();
        holder.setDate(date);
        //delete posts

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        final String userpic = task.getResult().getString("image");
                        final String username = task.getResult().getString("name");
                        holder.setUserdata(userpic,username);
                    }
                    }

            }
        });

        firebaseFirestore.collection("Posts").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        final String userpic = task.getResult().getString("image");
                        final String username = task.getResult().getString("name");
                        holder.setUserdata(userpic,username);
                    }
                }

            }
        });


        //count likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });
        //count comments
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int comcount = documentSnapshots.size();

                    holder.updateCommentsCount(comcount);

                } else {

                    holder.updateCommentsCount(0);

                }

            }
        });
        //get like color changed
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                  holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.like_button_red));

                } else {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.like_button_gray));

                }

            }
        });


        //likes add
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                        }

                    }
                });
            }
        });
        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });




    }

    @NonNull
    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView descView;
        private View mView;
        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        private ImageView blogCommentBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            blogLikeBtn = mView.findViewById(R.id.like_button);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);

            blogCommentBtn = mView.findViewById(R.id.comment_button);

        }
        public  void setDescText(String descText)
        {
            descView = mView.findViewById(R.id.desc_text);
            descView.setText(descText);
        }
        public void setBlogImage(String downloadUri,String thumbUri)
        {

            blogImageView = mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }
       public void setUserdata(String image,String name)
       {
           circleImageView = mView.findViewById(R.id.blog_user_pic);
           textView = mView.findViewById(R.id.blog_user_name);
           RequestOptions requestOptions = new RequestOptions();


           requestOptions.placeholder(R.drawable.profile_placeholder);
           Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(circleImageView);
           textView.setText(name);
       }


        public void setDate(Date date)
        {
           long millisec =date.getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisec)).toString();
            blogdate=mView.findViewById(R.id.blog_date);
            blogdate.setText(dateString);
        }

        public void updateLikesCount(int count)
        {
            String likecount = count + " Likes";
            blogLikeCount.setText(likecount);
        }

        public void updateCommentsCount(int comcount) {
            commentnum = mView.findViewById(R.id.blog_comment_count);
            commentnum.setText(comcount+" Comments");
        }
    }
}
