package com.example.dell.newapp;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dell on 11/28/2017.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>{

    private List<Answers> mAnswerList;

    String answer_key;

    public AnswerAdapter(List<Answers> mMessageList) {
        this.mAnswerList = mMessageList;
    }


    @Override
    public AnswerAdapter.AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_answer_layout,parent,false);

        return new AnswerViewHolder(v);
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder{

        public TextView answer;
        public TextView name;
        public TextView date;
        public CircleImageView image;


        public AnswerViewHolder(View itemView) {
            super(itemView);

            answer = (TextView) itemView.findViewById(R.id.single_answer);
            image = (CircleImageView) itemView.findViewById(R.id.single_answer_image);
            date = (TextView) itemView.findViewById(R.id.single_answer_date);
            name = (TextView) itemView.findViewById(R.id.single_answer_name);

        }
    }
    @Override
    public void onBindViewHolder(final AnswerAdapter.AnswerViewHolder holder, int position) {

        Answers c = mAnswerList.get(position);
        answer_key = c.getQuestion_key();
        String dateTime = c.getDate_time();
        String image = c.getImage();
        String name = c.getName();
        holder.name.setText(name);
        Picasso.with(holder.image.getContext()).load(image).placeholder(R.drawable.avatar).into(holder.image);
        holder.date.setText(dateTime);
        holder.answer.setText(c.getAnswer());
    }

    @Override
    public int getItemCount() {
        return mAnswerList.size();
    }

}

