package com.example.dell.newapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullQuestionActivity extends AppCompatActivity {

    private RecyclerView mAnswerList;

    private TextView userName;
    private CircleImageView userImage;
    private TextView userQuestion;
    private TextView dateTime;
    private TextView answerCount;
    private ImageButton btnAnswer;

    private DatabaseReference mQuestionDatabase;
    private DatabaseReference mAnswerDatabase;

    private final List<Answers> answersList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private AnswerAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_question);

        userName = (TextView)findViewById(R.id.full_display_name);
        userImage = (CircleImageView)findViewById(R.id.full_profile_image);
        userQuestion = (TextView)findViewById(R.id.full_question);
        dateTime = (TextView)findViewById(R.id.tv_full_date_time);
        answerCount = (TextView)findViewById(R.id.tv_no_of_answers);
        btnAnswer = (ImageButton)findViewById(R.id.full_answer);
        mAnswerList = (RecyclerView)findViewById(R.id.full_answer_list);

        final String questionKey = getIntent().getStringExtra("question_key");

        mAnswerDatabase = FirebaseDatabase.getInstance().getReference().child("Answers");
        mAnswerDatabase.keepSynced(true);

        mQuestionDatabase = FirebaseDatabase.getInstance().getReference().child("Questions").child(questionKey);
        mQuestionDatabase.keepSynced(true);

        mQuestionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String question = dataSnapshot.child("question").getValue().toString();
                String dateandTime = dataSnapshot.child("date_time").getValue().toString();

                userName.setText(name);
                userQuestion.setText(question);
                dateTime.setText(dateandTime);

                if(image != "default") {

                    Picasso.with(FullQuestionActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(FullQuestionActivity.this).load(image).placeholder(R.drawable.avatar).into(userImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerIntent = new Intent(FullQuestionActivity.this,AnswerActivity.class);
                answerIntent.putExtra("question_key",questionKey );
                startActivity(answerIntent);
            }
        });
        mLinearLayout = new LinearLayoutManager(FullQuestionActivity.this);
        mLinearLayout.setReverseLayout(true);
        mLinearLayout.setStackFromEnd(true);
        mAnswerList.setHasFixedSize(true);
        mAnswerList.setLayoutManager(mLinearLayout);

        mAdapter = new AnswerAdapter(answersList);
        mAnswerList.setAdapter(mAdapter);


            loadAnswers();

    }

    /*@Override
    protected void onStart(){
        super.onStart();
        final String questionKey = getIntent().getStringExtra("question_key");

        FirebaseRecyclerAdapter<Answers,AnswerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Answers, AnswerViewHolder>(
                Answers.class,
                R.layout.single_answer_layout,
                AnswerViewHolder.class,
                mAnswerDatabase.orderByChild("question_key").equalTo(questionKey)
        ) {
            @Override
            protected void populateViewHolder(AnswerViewHolder viewHolder, Answers model, int position) {


                if(model.getQuestion_key().contentEquals(questionKey)) {
                    viewHolder.setName(model.getName());
                    viewHolder.setImage(model.getImage(), FullQuestionActivity.this);
                    viewHolder.setDate(model.getDate_time());
                    viewHolder.setAnswer(model.getAnswer());
                }
            }
        };
        mAnswerList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){

            TextView userName = (TextView) mView.findViewById(R.id.single_answer_name);
            userName.setText(name);
        }

        public void setImage(String thumb_image, Context ctx){

            CircleImageView image = (CircleImageView) mView.findViewById(R.id.single_answer_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.avatar).into(image);
        }
        public void setDate(String date){

            TextView dateTime = (TextView) mView.findViewById(R.id.single_answer_date);
            dateTime.setText(date);

        }

        public void setAnswer(String answer){

            TextView userAnswer = (TextView) mView.findViewById(R.id.single_answer);
            userAnswer.setText(answer);
        }
    }*/

    private void loadAnswers() {
        final int[] count = {0};
        final String questionKey = getIntent().getStringExtra("question_key");

        mAnswerDatabase.orderByChild("question_key").equalTo(questionKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                count[0]++;

                Answers message = dataSnapshot.getValue(Answers.class);

                answersList.add(message);
                mAdapter.notifyDataSetChanged();
                answerCount.setText(Integer.toString(count[0]));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent =  new Intent(FullQuestionActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
