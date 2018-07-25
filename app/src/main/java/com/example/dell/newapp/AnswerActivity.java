package com.example.dell.newapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AnswerActivity extends AppCompatActivity {

    private TextView answerQuestion;
    private TextView buttonSubmit;
    private ImageButton cancelButton;
    private EditText answer;

    private DatabaseReference mQuestionDatabase;
    private DatabaseReference mAnswerDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    String userName;
    String thumb_image;
    String currentDate;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        final String questionKey = getIntent().getStringExtra("question_key");

        answerQuestion = (TextView)findViewById(R.id.tv_answer_question);
        cancelButton = (ImageButton)findViewById(R.id.btn_cancel);
        buttonSubmit =  (TextView) findViewById(R.id.btn_submit);
        answer = (EditText) findViewById(R.id.et_answer);
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        String current_user_id = mCurrentUser.getUid();

        mAnswerDatabase = FirebaseDatabase.getInstance().getReference().child("Answers");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("name").getValue().toString();
                thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mQuestionDatabase = FirebaseDatabase.getInstance().getReference().child("Questions").child(questionKey);

        mQuestionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String question = dataSnapshot.child("question").getValue().toString();
                answerQuestion.setText(question);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullIntent = new Intent(AnswerActivity.this,FullQuestionActivity.class);
                fullIntent.putExtra("question_key",questionKey);
                fullIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(fullIntent);
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answer.getText().toString();
                if (!TextUtils.isEmpty(userAnswer)) {
                    mProgress.setTitle("Posting Your Answer...");
                    mProgress.setMessage("Please wait while your answer is posted");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();


                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    }


                    HashMap<String, String> answerMap = new HashMap<String, String>();
                    answerMap.put("name", userName);
                    answerMap.put("image", thumb_image);
                    answerMap.put("date_time", currentDate);
                    answerMap.put("answer", userAnswer);
                    answerMap.put("question_key",questionKey);

                    mAnswerDatabase.push().setValue(answerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();

                                Toast.makeText(AnswerActivity.this,"Your Answer posted succesfully",Toast.LENGTH_SHORT).show();
                                Intent fullIntent = new Intent(AnswerActivity.this,FullQuestionActivity.class);
                                fullIntent.putExtra("question_key",questionKey);
                                startActivity(fullIntent);

                            } else {
                                mProgress.dismiss();

                                Toast.makeText(AnswerActivity.this,"Unable to post your answer",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else{
                    Toast.makeText(AnswerActivity.this,"You cannot post an empty answer",Toast.LENGTH_SHORT).show();
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