package com.mab.homeguardsecurities;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_post);
        mPostTitle = findViewById(R.id.postTitle);
        mSelectImage= findViewById(R.id.imageSelect);
        mPostBtn = findViewById(R.id.PostBtn);
        mPostDesc = findViewById(R.id.descField);
        mProgressDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        Name =PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name","abc");
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_READ_STORAGE);

    }

    private void startPosting() {
        mProgressDialog.setMessage("Posting to Blog..");
        mProgressDialog.show();
        //final String mName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("name","abc").toString().trim();
        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(desc_val)&&mImageUri!=null){
            StorageReference filepath = mStorageReference.child("Post_images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Date d = new Date();
                    String date =  new SimpleDateFormat(" d MMM yyyy HH:mm").format(d);
                    DatabaseReference newPost = mDatabaseReference.push();
                    newPost.child("Name").setValue(Name);
                    newPost.child("PostTitle").setValue(title_val);
                    newPost.child("PostDesc").setValue(desc_val);
                    newPost.child("Image").setValue(downloadUrl.toString());
                    newPost.child("VoteCount").setValue(0);
                    newPost.child("Date").setValue(date);
                    newPost.child("UpvotedBy").push().setValue(Name);
                    newPost.child("DownvotedBy").push().setValue(Name);
                    mProgressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                }
            });

        }
    }
}
