package com.mab.homeguardsecurities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView mPostlist;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String Name;
    private Boolean postedBy = Boolean.FALSE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPostlist = findViewById(R.id.postList);
        mPostlist.setHasFixedSize(true);
        mPostlist.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        Name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name", "abc");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PostActivity.class);
                startActivity(i);
            }
        });
        /*if (isFirstTime.equals(Boolean.FALSE)) {
            mUidReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Uid = (Long) dataSnapshot.child("Uid").getValue();
                    Toast.makeText(MainActivity.this, Uid+1+"", Toast.LENGTH_SHORT).show();
                    //mUidReference.setValue(Uid + 1);
                    isFirstTime = Boolean.TRUE;
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isFirstTime", isFirstTime).apply();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("Uid", Uid).apply();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.row,
                PostViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setPostTitle(model.getPostTitle());
                viewHolder.setPostDesc(model.getPostDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setVoteCount(model.getVoteCount());
                viewHolder.setDate(model.getDate());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(post_key).child("Name").getValue().equals(Name)){
                                    postedBy = Boolean.TRUE;
                                }
                                Intent intent = new Intent(MainActivity.this,ViewPostActivity.class);
                                intent.putExtra("PostKey",post_key);
                                intent.putExtra("PostedBy",postedBy);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
                viewHolder.mCommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mComment = String.valueOf(viewHolder.mCommentBox.getText());
                        if(!mComment.matches("")) {
                            DatabaseReference newComment = mDatabaseReference.child(post_key).child("Comment").push();
                            newComment.child("Name").setValue(Name);
                            newComment.child("comment").setValue(mComment);
                            newComment.child("VoteCount").setValue(0);
                            newComment.child("UpvotedBy").push().setValue(Name);
                            newComment.child("DownvotedBy").push().setValue(Name);
                        }
                        Intent intent = new Intent(MainActivity.this,ViewPostActivity.class);
                        intent.putExtra("PostKey",post_key);
                        startActivity(intent);
                    }
                });
                viewHolder.mUpVoteBtn.setOnClickListener(new View.OnClickListener() {
                    String abc;
                    @Override
                    public void onClick(final View view) {
                        mDatabaseReference.child(post_key).child("UpvotedBy").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            if(dataSnapshot.child(post_key).hasChild("VoteCount") ){
                                                long mVote= (long) dataSnapshot.child(post_key).child("VoteCount").getValue();
                                                int mVotes = (int) mVote;
                                                mDatabaseReference.child(post_key).child("VoteCount").setValue(mVotes+1);
                                                mDatabaseReference.child(post_key).child("UpvotedBy").push().setValue(Name);
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
                        mDatabaseReference.child(post_key).child("DownvotedBy").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        if(dataSnapshot.child(post_key).hasChild("VoteCount") ){
                                            long mVote= (long) dataSnapshot.child(post_key).child("VoteCount").getValue();
                                            int mVotes = (int) mVote;
                                            mDatabaseReference.child(post_key).child("VoteCount").setValue(mVotes-1);
                                            mDatabaseReference.child(post_key).child("DownvotedBy").push().setValue(Name);
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
        mPostlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mCommentBtn,mUpVoteBtn,mDownVoteBtn,mUpVoteInactive,mDownVoteInactive;
        EditText mCommentBox;
        TextView mVoteCount;
        public PostViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mVoteCount = mView.findViewById(R.id.voteCount);
            mUpVoteBtn = mView.findViewById(R.id.upvoteBtn);
            mDownVoteBtn = mView.findViewById(R.id.downVoteBtn);
            mCommentBox = mView.findViewById(R.id.commentBox);
            mCommentBtn = mView.findViewById(R.id.commentBtn);
            mUpVoteInactive = mView.findViewById(R.id.upvoteBtnInactive);
            mDownVoteInactive = mView.findViewById(R.id.downVoteBtnInactive);
        }
        public void setName(String Name){
            TextView name = (TextView) mView.findViewById(R.id.NameField);
            name.setText(Name);
        }
        public void setPostTitle(String PostTitle){
            TextView postTitle = mView.findViewById(R.id.postTitle);
            postTitle.setText(PostTitle);
        }
        public void setPostDesc(String PostDesc){
            TextView postDesc = mView.findViewById(R.id.postDesc);
            postDesc.setText(PostDesc);
        }
        public void setImage(Context ctx, String Image){
            ImageView post_image = mView.findViewById(R.id.image);
            Picasso.with(ctx).load(Image).into(post_image);
        }
        public  void setVoteCount(Long Votecount){
            TextView voteCount = mView.findViewById(R.id.voteCount);
            voteCount.setText(Votecount + "");
        }
        public  void setDate(String Date){
            TextView date = mView.findViewById(R.id.date);
            date.setText(Date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mAuth.signOut();
            Intent SignoutIntent = new Intent(MainActivity.this,GSignin.class);
            startActivity(SignoutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
