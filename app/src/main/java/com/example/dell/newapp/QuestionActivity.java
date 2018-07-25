package com.example.dell.newapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionActivity extends AppCompatActivity {

    private EditText query;
    private CircleImageView profileImage;
    private Button postQuery;
    private TextView displayName;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mQuestionsDatabase;
    private FirebaseUser mCurrentuser;
    private FirebaseAuth mAuth;

    private String thumb_image;
    private String name;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mAuth = FirebaseAuth.getInstance();
        mCurrentuser = mAuth.getCurrentUser();
        if(mAuth.getCurrentUser() != null) {
            String current_user_id = mCurrentuser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
            mUserDatabase.keepSynced(true);
            mQuestionsDatabase = FirebaseDatabase.getInstance().getReference().child("Questions");
        }

        mProgress = new ProgressDialog(this);


        query = (EditText) findViewById(R.id.et_query);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        postQuery = (Button) findViewById(R.id.b_post_query);
        displayName = (TextView) findViewById(R.id.tv_display_name);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("name").getValue().toString();
                thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                displayName.setText(name);
                if(!thumb_image.contentEquals("default")) {

                    Picasso.with(QuestionActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(QuestionActivity.this).load(thumb_image).placeholder(R.drawable.avatar).into(profileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postQuery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                String question = query.getText().toString();

                if(!TextUtils.isEmpty(question)) {

                mProgress.setTitle("Posting Your query...");
                mProgress.setMessage("Please wait while your query is posted");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                    HashMap<String, String> userQuestion = new HashMap<String, String>();
                    userQuestion.put("question", question);
                    userQuestion.put("name", name);
                    userQuestion.put("image", thumb_image);
                    userQuestion.put("date_time", currentDate);

                    mQuestionsDatabase.push().setValue(userQuestion).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(QuestionActivity.this, "Your query posted succesfully", Toast.LENGTH_SHORT).show();

                                mProgress.dismiss();
                                Intent mainIntent = new Intent(QuestionActivity.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                                finish();

                            } else {

                                Toast.makeText(QuestionActivity.this, "Unable to post your query", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }else{
                    Toast.makeText(QuestionActivity.this,"You cannot post empty query",Toast.LENGTH_SHORT).show();
                }


            }
        });
        }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }



}
