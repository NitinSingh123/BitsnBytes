package com.example.dell.newapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountImageActivity extends AppCompatActivity {

    private CircleImageView displayImage;
    private Button upload;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;

    private ProgressDialog mProgress;

    private static final int GALLERY_PICK = 1;


    Bitmap thumb_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_image);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        displayImage = (CircleImageView) findViewById(R.id.profile_image);
        upload = (Button) findViewById(R.id.b_setUp);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(AccountImageActivity.this);
                mProgress.setTitle("UPLOADING...");
                mProgress.setMessage("Please wait while the setup completes");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                setupAccount();
            }
        });

        displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
    }

    private void setupAccount() {
        mProgress.dismiss();
        Intent mainIntent = new Intent(AccountImageActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        Toast.makeText(AccountImageActivity.this, "Account Succesfully updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(AccountImageActivity.this);
                mProgress.setTitle("UPLOADING...");
                mProgress.setMessage("Please wait while the setup completes");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


                Uri resultUri= result.getUri();
                displayImage.setImageURI(resultUri);

                File thumb_filepath = new File(resultUri.getPath());

                String current_userid = mCurrentUser.getUid();

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte = baos.toByteArray();



                StorageReference filePath = mStorageRef.child("profile_images").child(current_userid + "jpg");
                final StorageReference thumb_filePath = mStorageRef.child("profile_images").child("thumbs").child(current_userid + "jpg");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String download_url = task.getResult().getDownloadUrl().toString();
                            final String user_name = getIntent().getStringExtra("user_name");

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    if (thumb_task.isSuccessful()) {
                                        String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                        HashMap<String,String> userMap = new HashMap<String, String>();
                                        userMap.put("name",user_name);
                                        userMap.put("image",download_url);
                                        userMap.put("thumb_image",thumb_downloadUrl);

                                        mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgress.dismiss();
                                                    Toast.makeText(AccountImageActivity.this, "Account Succesfully updated", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                        });
                                    } else {

                                        mProgress.dismiss();
                                        Toast.makeText(AccountImageActivity.this, "Error in uploading image thumbnail", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });


                        }else{
                            mProgress.hide();
                            Toast.makeText(AccountImageActivity.this,"Error in uploading image",Toast.LENGTH_LONG).show();
                        }
                    }
                });





            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

