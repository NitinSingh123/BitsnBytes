package com.example.dell.newapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
   private RecyclerView mQuestionsList;

    private TextView displayName;
    private TextView question;
    private CircleImageView displayImage;
    private LinearLayoutManager mLinearLayout;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mQuestionsDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrenUser;

    private View mMainView;
    String thumb_image;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_home, container, false);
        displayName = (TextView)mMainView.findViewById(R.id.tv_name_main);
        question = (TextView) mMainView.findViewById(R.id.et_question_main);
        displayImage = (CircleImageView)mMainView.findViewById(R.id.image_main);
        mQuestionsList = (RecyclerView)mMainView.findViewById(R.id.questions_list);

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent QuestionIntent = new Intent(getContext(),QuestionActivity.class);
                startActivity(QuestionIntent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            mCurrenUser = mAuth.getCurrentUser();
            String current_user_id = mCurrenUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
            mUserDatabase.keepSynced(true);



            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    displayName.setText(name);


                    Picasso.with(getContext()).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(displayImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(thumb_image).placeholder(R.drawable.avatar).into(displayImage);
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
        mLinearLayout = new LinearLayoutManager(getContext());
        mLinearLayout.setReverseLayout(true);
        mLinearLayout.setStackFromEnd(true);

        mQuestionsList.setHasFixedSize(true);
        mQuestionsList.setLayoutManager(mLinearLayout);

        return mMainView;

    }




    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            String current_user_id = mAuth.getCurrentUser().getUid();

            mQuestionsDatabase = FirebaseDatabase.getInstance().getReference().child("Questions");
            mQuestionsDatabase.keepSynced(true);

            FirebaseRecyclerAdapter<questions, QuestionsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<questions, QuestionsViewHolder>(
                    questions.class,
                    R.layout.single_question_layout,
                    QuestionsViewHolder.class,
                    mQuestionsDatabase
            ) {
                @Override
                protected void populateViewHolder(QuestionsViewHolder viewHolder, questions model, int position) {

                    final String questionKey = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent fullIntent = new Intent(getContext(),FullQuestionActivity.class);
                            fullIntent.putExtra("question_key",questionKey);
                            startActivity(fullIntent);
                        }
                    });

                    viewHolder.setQuestion(model.getQuestion());
                    viewHolder.setName(model.getName());
                    viewHolder.setDate(model.getDate_time());
                    viewHolder.setImage(model.getImage(),getContext());


                }
            };
            mQuestionsList.setAdapter(firebaseRecyclerAdapter);
        }

    }

    public static class QuestionsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView displayQuestion;

        public QuestionsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setQuestion(String question){
            displayQuestion = (TextView)mView.findViewById(R.id.tv_question);
            displayQuestion.setText(question);
        }

        public void setName(String name){
            TextView questionName = (TextView) mView.findViewById(R.id.tv_name_question);
            questionName.setText(name);
        }

        public void setDate(String date){
            TextView postDate = (TextView) mView.findViewById(R.id.tv_date);
            postDate.setText(date);
        }

        public void setImage(String imageUrl,Context ctx){
            CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.question_image);
            Picasso.with(ctx).load(imageUrl).placeholder(R.drawable.avatar).into(thumb_image);
        }
    }
}
