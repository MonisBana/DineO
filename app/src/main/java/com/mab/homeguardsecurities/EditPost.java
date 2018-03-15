package com.mab.homeguardsecurities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPost extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_STORAGE =100 ;
    private ImageButton mSelectImage;
    private Button mPostBtn;
    private EditText mPostDesc,mPostTitle;
    public static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private String Name;
    private String Post_key;
    private String Image;
    private long VoteCount;
    private Boolean ImageChanged = Boolean.FALSE;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        mPostTitle = findViewById(R.id.postTitle);
        mSelectImage= findViewById(R.id.imageSelect);
        mPostBtn = findViewById(R.id.PostBtn);
        mPostDesc = findViewById(R.id.descField);
        mProgressDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        Name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name","abc");        Intent intent = getIntent();
        Post_key = intent.getStringExtra("PostKey");
        mDatabaseReference.child(Post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Message = (String) dataSnapshot.child("message").getValue();
                Image = (String) dataSnapshot.child("image").getValue();
                VoteCount = (Long) dataSnapshot.child("VoteCount").getValue();
                mPostDesc.setText(Message);
                Picasso.with(getApplicationContext()).load(Image).into(mSelectImage);
                mImageUri = Uri.parse(Image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
                ImageChanged=Boolean.TRUE;
            }
        });
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_READ_STORAGE);

    }
    private void startPosting() {
        mProgressDialog.setMessage("Posting to Blog..");
        mProgressDialog.show();
        //final String mName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name","abc").toString().trim();
        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(desc_val)) {
            if (ImageChanged.equals(Boolean.TRUE)) {
                StorageReference filepath = mStorageReference.child("Post_images").child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Date d = new Date();
                        String date = new SimpleDateFormat(" d MMM yyyy HH:mm").format(d);
                        //DatabaseReference newPost = mDatabaseReference.child(Post_key);
                        try {
                            mDatabaseReference.child(Post_key).child("Name").setValue(Name);
                            mDatabaseReference.child(Post_key).child("PostTitle").setValue(title_val);
                            mDatabaseReference.child(Post_key).child("PostDesc").setValue(desc_val);
                            mDatabaseReference.child(Post_key).child("Image").setValue(downloadUrl.toString());
                            mDatabaseReference.child(Post_key).child("VoteCount").setValue(VoteCount);
                            mDatabaseReference.child(Post_key).child("Date").setValue(date);
                        } catch (Exception e) {
                            Log.d("hello", String.valueOf(e));
                        }
                        mProgressDialog.dismiss();
                        startActivity(new Intent(EditPost.this, MainActivity.class));
                    }
                });


            } else {
                Date d = new Date();
                String date = new SimpleDateFormat(" d MMM yyyy HH:mm").format(d);
                mDatabaseReference.child(Post_key).child("Name").setValue(Name);
                mDatabaseReference.child(Post_key).child("PostTitle").setValue(title_val);
                mDatabaseReference.child(Post_key).child("PostDesc").setValue(desc_val);
                //mDatabaseReference.child(Post_key).child("image").setValue(downloadUrl.toString());
                mDatabaseReference.child(Post_key).child("VoteCount").setValue(VoteCount);
                mDatabaseReference.child(Post_key).child("Date").setValue(date);
                mProgressDialog.dismiss();
                startActivity(new Intent(EditPost.this, MainActivity.class));
            }
        }
    }
}
