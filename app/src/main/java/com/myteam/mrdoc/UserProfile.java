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

import android.widget.ProgressBar;

import android.widget.Spinner;
import android.widget.Toast;



import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;

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

import java.util.Set;



import de.hdodenhof.circleimageview.CircleImageView;

import id.zelory.compressor.Compressor;
public class UserProfile extends AppCompatActivity {
    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private String user_id;
    private boolean isChanged = false;
    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap compressedImageFile;
    private Spinner dropdown;
    private EditText bdate,mnum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setupImage = findViewById(R.id.setupimage);
        setupName = findViewById(R.id.nameEdit);
        setupBtn = findViewById(R.id.setupButton);
        setupProgress = findViewById(R.id.progressBar2);
        dropdown =findViewById(R.id.spinner);
        user_id = firebaseAuth.getCurrentUser().getUid();
        final String put_user_id = user_id;
        String[] items = new String[]{"","male", "female", "other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserProfile.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        bdate = findViewById(R.id.BdayDate);
        mnum = findViewById(R.id.editNo);
        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);
        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String gender =  task.getResult().getString("gender");
                        String birthday =  task.getResult().getString("birthday");
                        String mobile_no =  task.getResult().getString("Mobile Number");
                        mainImageURI = Uri.parse(image);
                        setupName.setText(name);
                        bdate.setText(birthday);
                        mnum.setText(mobile_no);
                        dropdown.setSelection(((ArrayAdapter<String>)dropdown.getAdapter()).getPosition(gender));
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.untitled);
                        Glide.with(UserProfile.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                }
                else
                {
                    String error = task.getException().getMessage();
                    Toast.makeText(UserProfile.this, error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = setupName.getText().toString();
                final String birth_date = bdate.getText().toString();
                final String mobile_number = mnum.getText().toString();
                final String gender = dropdown.getSelectedItem().toString();
                if(user_name.isEmpty())
                {
                    setupName.setError("Kindly Enter Name");
                    setupName.requestFocus();
                    return;
                }
                if(birth_date.isEmpty())
                {
                    bdate.setError("Kindly Enter Birth Date");
                    bdate.requestFocus();
                    return;
                }
                if(mobile_number.isEmpty())
                {
                    mnum.setError("Kindly Enter Mobile Number");
                    mnum.requestFocus();
                    return;
                }
                if(gender.isEmpty())
                {
                    Toast.makeText(UserProfile.this, "Kindly Select Your Gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mainImageURI==null)
                {
                    Toast.makeText(UserProfile.this, "Please select you profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!Patterns.PHONE.matcher(mobile_number).matches())
                {
                    mnum.setError("Invalid Mobile Number");
                    mnum.requestFocus();
                    return;
                }




                else
                {
                    setupProgress.setVisibility(View.VISIBLE);
                    if (isChanged)
                    {
                        user_id = firebaseAuth.getCurrentUser().getUid();
                        final String put_user_id = user_id;
                        File newImageFile = new File(mainImageURI.getPath());
                        try {
                            compressedImageFile = new Compressor(UserProfile.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();
                        UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);
                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name,birth_date,mobile_number,gender,put_user_id);
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(UserProfile.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }
                            }

                        });



                    } else {



                        storeFirestore(null, user_name,birth_date,mobile_number,gender,put_user_id);

                           }



                }



            }



        });



        setupImage.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {



                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){



                    if(ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){



                        Toast.makeText(UserProfile.this, "Kindly Grant Storage Permission", Toast.LENGTH_LONG).show();

                        ActivityCompat.requestPermissions(UserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);



                    } else {



                        BringImagePicker();



                    }



                } else {



                    BringImagePicker();



                }



            }



        });





    }



    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name,String birth_date,String mobile_number,String gender,String put_user_id) {



        Uri download_uri;



        if(task != null) {



            download_uri = task.getResult().getDownloadUrl();



        } else {



            download_uri = mainImageURI;



        }



        Map<String, String> userMap = new HashMap<>();

        userMap.put("name", user_name);

        userMap.put("image", download_uri.toString());
        userMap.put("gender",gender);
        userMap.put("birthday",birth_date);
        userMap.put("Mobile Number",mobile_number);
        userMap.put("userid",put_user_id);




        firebaseFirestore.collection("users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override

            public void onComplete(@NonNull Task<Void> task) {



                if(task.isSuccessful()){



                    Toast.makeText(UserProfile.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();

                    Intent mainIntent = new Intent(UserProfile.this, MainActivity.class);

                    startActivity(mainIntent);

                    finish();



                } else {



                    String error = task.getException().getMessage();

                    Toast.makeText(UserProfile.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();



                }



                setupProgress.setVisibility(View.INVISIBLE);



            }

        });





    }



    private void BringImagePicker() {



        CropImage.activity()

                .setGuidelines(CropImageView.Guidelines.ON)

                .setAspectRatio(1, 1)

                .start(UserProfile.this);



    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {



                mainImageURI = result.getUri();

                setupImage.setImageURI(mainImageURI);



                isChanged = true;



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {



                Exception error = result.getError();



            }

        }



    }

}