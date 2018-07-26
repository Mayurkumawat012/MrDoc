package com.myteam.mrdoc;
import android.Manifest;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.net.Uri;

import android.os.Build;

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.text.TextUtils;

import android.util.Patterns;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Spinner;
import android.widget.Toast;



import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;

import com.theartofdev.edmodo.cropper.CropImageView;



import java.io.ByteArrayOutputStream;

import java.io.File;

import java.io.IOException;

import java.util.HashMap;

import java.util.Map;

import java.util.Random;
import java.util.Set;
import java.util.UUID;


import de.hdodenhof.circleimageview.CircleImageView;

import id.zelory.compressor.Compressor;

public class newPost extends AppCompatActivity {
    private static final int MAX_LENGTH = 100;
    private Uri mainImageURI = null;
    private ImageView setpost;
    private EditText newPostDesc;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth newPostAuth;
    private String user_id;
    private Toolbar newPostToolbar;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        newPostToolbar = findViewById(R.id.toolbarpost);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setpost = findViewById(R.id.postimage);
        newPostDesc = findViewById(R.id.descriptionid);
        newPostProgress = findViewById(R.id.progressbarid);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        newPostAuth = FirebaseAuth.getInstance();
        user_id = newPostAuth.getCurrentUser().getUid();
    }

    public void setPost(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(newPost.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(newPost.this, "Kindly Grant Storage Permission", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(newPost.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                BringImagePicker();
            }
        } else {
            BringImagePicker();
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .start(newPost.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setpost.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    public void postButton(View view) {
        final String description = newPostDesc.getText().toString();
        if (!(description.isEmpty()) && mainImageURI != null)
        {
            newPostProgress.setVisibility(View.VISIBLE);
             final String randomname = UUID.randomUUID().toString();

                        File newImageFile = new File(mainImageURI.getPath());
                        try {
                            compressedImageFile = new Compressor(newPost.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);
                            }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();
                        UploadTask filePath = storageReference.child("post_images").child(randomname + ".jpg").putBytes(imageData);
                        filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                final String downloadUri = task.getResult().getDownloadUrl().toString();

                                if (task.isSuccessful()) {

                                    File newThumbFile = new File(mainImageURI.getPath());
                                    try {

                                        compressedImageFile = new Compressor(newPost.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();

                                    UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                            .child(randomname + ".jpg").putBytes(thumbData);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                            Map<String, Object> postMap = new HashMap<>();
                                            postMap.put("image_url", downloadUri);
                                            postMap.put("image_thumb", downloadthumbUri);
                                            postMap.put("desc", description);
                                            postMap.put("user_id", user_id);
                                            postMap.put("timestamp", FieldValue.serverTimestamp());
                                            firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(newPost.this, "Post Uploaded", Toast.LENGTH_LONG).show();
                                                        Intent mainIntent = new Intent(newPost.this, MainActivity.class);
                                                        startActivity(mainIntent);
                                                        finish();

                                                    } else {


                                                    }

                                                    newPostProgress.setVisibility(View.INVISIBLE);

                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            //Error handling

                                        }
                                    });


                                } else {

                                    newPostProgress.setVisibility(View.INVISIBLE);

                                }

                            }
                        });


        }
        else
        {
            if(mainImageURI==null)
            {
                Toast.makeText(newPost.this, "Blank Image", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Descrition is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

    }




}


