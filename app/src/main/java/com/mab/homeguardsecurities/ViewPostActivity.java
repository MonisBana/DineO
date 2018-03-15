package com.mab.homeguardsecurities;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewPostActivity extends AppCompatActivity {
    private TextView mName, mPostDesc, mPostTitle,mVoteCount,mDate;
    private ImageButton mUpVoteBtn,mDownVoteBtn,mCommentBtn,mDownVoteInactive,mUpVoteInactive;
    private ImageView mImage;
    private EditText mCommentBox;
    private RecyclerView mCommentlist;
    private DatabaseReference mDatabaseReference,mCommentReference;
    private FirebaseAuth mAuth;
    private String Name;
    private String Post_key;
    private Boolean PostedBy;
    private ImageButton mPostMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        mName = findViewById(R.id.NameField);
        mPostDesc = findViewById(R.id.postDesc);
        mPostTitle = findViewById(R.id.postTitle);
        mVoteCount = findViewById(R.id.voteCount);
        mDate = findViewById(R.id.date);
        mUpVoteBtn = findViewById(R.id.upvoteBtn);
        mDownVoteBtn = findViewById(R.id.downVoteBtn);
        mUpVoteInactive = findViewById(R.id.upvoteBtnInactive);
        mDownVoteInactive = findViewById(R.id.downVoteBtnInactive);
        mImage = findViewById(R.id.image);
        mCommentBtn = findViewById(R.id.commentBtn);
        mCommentBox = findViewById(R.id.commentBox);
        mCommentlist = findViewById(R.id.commentList);
        mPostMenu = findViewById(R.id.postMenu);
        mCommentlist.setHasFixedSize(true);
        mCommentlist.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        Name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name","abc");
        Intent intent = getIntent();
        Post_key = intent.getStringExtra("PostKey");
        PostedBy = intent.getBooleanExtra("PostedBy",Boolean.FALSE);
        //Toast.makeText(this,PostedBy.toString(), Toast.LENGTH_SHORT).show();
        mCommentReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_key).child("Comment");
        mDatabaseReference.child(Post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Name = (String) dataSnapshot.child("Name").getValue();
                String Heading = (String) dataSnapshot.child("PostTitle").getValue();
                String Message = (String) dataSnapshot.child("PostDesc").getValue();
                String Image = (String) dataSnapshot.child("Image").getValue();
                Long VoteCount = (Long) dataSnapshot.child("VoteCount").getValue();
                String Date = (String) dataSnapshot.child("Date").getValue();
                mDate.setText(Date);
                mName.setText(Name);
                mPostDesc.setText(Message);
                mPostTitle.setText(Heading);
                mVoteCount.setText(VoteCount + "");
                Picasso.with(getApplicationContext()).load(Image).into(mImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUpVoteBtn.setOnClickListener(new View.OnClickListener() {
            String abc;
            @Override
            public void onClick(final View view) {
                mDatabaseReference.child(Post_key).child("UpvotedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String UpvoteKey = snapshot.getKey();
                            if(dataSnapshot.child(UpvoteKey).getValue().equals(Name)){
                                abc = "abc";
                            }
                        }
                        if(abc != "abc"){
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(Post_key).hasChild("VoteCount") ){
                                        long mVote= (long) dataSnapshot.child(Post_key).child("VoteCount").getValue();
                                        int mVotes = (int) mVote;
                                        mDatabaseReference.child(Post_key).child("VoteCount").setValue(mVotes+1);
                                        mDatabaseReference.child(Post_key).child("UpvotedBy").push().setValue(Name);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        mDownVoteBtn.setOnClickListener(new View.OnClickListener() {
            String abc;
            @Override
            public void onClick(final View view) {
                mDatabaseReference.child(Post_key).child("DownvotedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String DownvoteKey = snapshot.getKey();
                            if(dataSnapshot.child(DownvoteKey).getValue().equals(Name)){
                                abc = "abc";
                            }
                        }
                        if(abc != "abc"){
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(Post_key).hasChild("VoteCount") ){
                                        long mVote= (long) dataSnapshot.child(Post_key).child("VoteCount").getValue();
                                        int mVotes = (int) mVote;
                                        mDatabaseReference.child(Post_key).child("VoteCount").setValue(mVotes-1);
                                        mDatabaseReference.child(Post_key).child("DownvotedBy").push().setValue(Name);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mComment = String.valueOf(mCommentBox.getText());
                if(!mComment.matches("")) {
                    DatabaseReference newComment = mDatabaseReference.child(Post_key).child("Comment").push();
                    newComment.child("Name").setValue(Name);
                    newComment.child("comment").setValue(mComment);
                    newComment.child("VoteCount").setValue(0);
                    newComment.child("UpvotedBy").push().setValue(Name);
                    newComment.child("DownvotedBy").push().setValue(Name);
                }
                mCommentBox.setText("");
            }
        });
        Toast.makeText(this,PostedBy+"", Toast.LENGTH_SHORT).show();
        if(PostedBy == Boolean.TRUE){
            mPostMenu.setVisibility(View.VISIBLE);
        }
        mPostMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getApplicationContext(),mPostMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_post:
                                Intent intent = new Intent(ViewPostActivity.this,EditPost.class);
                                intent.putExtra("PostKey",Post_key);
                                startActivity(intent);
                                break;
                            case R.id.delete_post:
                                Intent i = new Intent(ViewPostActivity.this,MainActivity.class);
                                mDatabaseReference.child(Post_key).removeValue();
                                startActivity(i);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Post_key = intent.getStringExtra("PostKey");
        PostedBy = intent.getBooleanExtra("PostedBy",Boolean.FALSE);
        FirebaseRecyclerAdapter<Comment,CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(
                Comment.class,
                R.layout.comment_row,
                CommentViewHolder.class,
                mCommentReference
        ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder viewHolder, Comment model, int position) {
                final String comment_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setComment(model.getComment());
                viewHolder.setVoteCount(model.getVoteCount());
                viewHolder.setDate(model.getDate());
                viewHolder.mUpVoteBtn.setOnClickListener(new View.OnClickListener() {
                    String abc;
                    @Override
                    public void onClick(final View view) {
                        mCommentReference.child(comment_key).child("UpvotedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    String UpvoteKey = snapshot.getKey();
                                    if(dataSnapshot.child(UpvoteKey).getValue().equals(Name)){
                                        abc = "abc";
                                    }
                                }
                                if(abc != "abc"){
                                    mCommentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child(comment_key).hasChild("VoteCount") ){
                                                long mVote= (long) dataSnapshot.child(comment_key).child("VoteCount").getValue();
                                                int mVotes = (int) mVote;
                                                mCommentReference.child(comment_key).child("VoteCount").setValue(mVotes+1);
                                                mCommentReference.child(comment_key).child("UpvotedBy").push().setValue(Name);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
                viewHolder.mDownVoteBtn.setOnClickListener(new View.OnClickListener() {
                    String abc;
                    @Override
                    public void onClick(final View view) {
                        mCommentReference.child(comment_key).child("DownvotedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    String UpvoteKey = snapshot.getKey();
                                    if(dataSnapshot.child(UpvoteKey).getValue().equals(Name)){
                                        abc = "abc";
                                    }
                                }
                                if(abc != "abc"){
                                    mCommentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child(comment_key).hasChild("VoteCount") ){
                                                long mVote= (long) dataSnapshot.child(comment_key).child("VoteCount").getValue();
                                                int mVotes = (int) mVote;
                                                mCommentReference.child(comment_key).child("VoteCount").setValue(mVotes-1);
                                                mCommentReference.child(comment_key).child("DownvotedBy").push().setValue(Name);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });

            }
        };
        mCommentlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        mCommentlist.setLayoutManager(layoutManager);
    }
    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton mUpVoteBtn,mDownVoteBtn,mUpVoteInactive,mDownVoteInactive,mPostMenu;
        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mUpVoteBtn = mView.findViewById(R.id.upvoteBtn);
            mDownVoteBtn = mView.findViewById(R.id.downVoteBtn);
            mUpVoteInactive = mView.findViewById(R.id.upvoteBtnInactive);
            mDownVoteInactive = mView.findViewById(R.id.downVoteBtnInactive);
            mPostMenu = mView.findViewById(R.id.postMenu);
        }
        public  void setName(String name){
            TextView mName = mView.findViewById(R.id.NameField);
            mName.setText(name);
        }
        public  void setComment(String comment){
            TextView mComment = mView.findViewById(R.id.comment);
            mComment.setText(comment);
        }
        public  void setVoteCount(Long VoteCount){
            TextView mVoteCount = mView.findViewById(R.id.voteCount);
            mVoteCount.setText(VoteCount + "");
        }
        public  void setDate(String Date){
            TextView date = mView.findViewById(R.id.date);
            date.setText(Date);
        }
    }
}
