package com.example.SchoolApp.Activities.profile;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {


    private EditText nameText;
    private EditText emailText;
    private EditText authNumberText;
    private EditText AgeText;
    private String firebasePathImg ;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageResult;
    private String email;
    private KProgressHUD hud ;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        inflateUiElements();
        ImageView img = findViewById(R.id.image_profile);
        Picasso.get().load(ref.CUR_USER.getImagePath()).placeholder( R.drawable.loading).into(img) ;
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
    }

    // this method for Inflate the layout for this fragment
    public void inflateUiElements() {
        // احضار الإسم
        nameText = findViewById(R.id.username_txt);
        nameText.setText(ref.CUR_USER.getUsername());
        // احضار الإيميل
        emailText = findViewById(R.id.email_txt);
        emailText.setEnabled(false);
        emailText.setText(ref.CUR_USER.getEmail());
        // احضار رقم التصريح
        authNumberText = findViewById(R.id.phone_number_txt);
        authNumberText.setText(ref.CUR_USER.getPhone_number());
        AgeText = findViewById(R.id.age_txt);
        AgeText.setText(ref.CUR_USER.getAge());

    }


    private void deleteImage(final String userID) {
        inflateUiElements();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference deleteFile = storageReference.child( com.example.SchoolApp.ref.CUR_USER.getEmail() + "/profile_pic");


        deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateData(userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                updateData(userID);
                Log.d("onFailure", "onFailure: did not delete file");
            }
        });
    }

    private void updateData(final String userID){

        final StorageReference ref = storageReference.child( com.example.SchoolApp.ref.CUR_USER.getEmail() + "/profile_pic");
        ref.putFile(imageResult).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downUri = task.getResult();
                    firebasePathImg = downUri.toString();
                    updateUserData();
                }
            }
        });


    }

    private void updateUserData() {

        DatabaseReference UserRoot = FirebaseDatabase.getInstance().getReference().getRoot().child(FirebaseConstant.USERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        UserRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("age").setValue(AgeText.getText().toString());
                dataSnapshot.getRef().child("username").setValue(nameText.getText().toString());
                dataSnapshot.getRef().child("phone_number").setValue(authNumberText.getText().toString());
                dataSnapshot.getRef().child("imagePath").setValue(firebasePathImg);
                Toast.makeText(ProfileActivity.this, "successfully updated . ", Toast.LENGTH_SHORT).show();
                ProfileActivity.this.finish();
                hud.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", "onCancelled: error while getting data");
                hud.dismiss();
            }
        });

    }


    private boolean validation() {
        String name = nameText.getText().toString().trim();
        email = emailText.getText().toString().trim();
        String authNumber = authNumberText.getText().toString().trim();
        String Age = AgeText.getText().toString().trim();
        // لو كان خانة الإسم فاضية
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "write your name", Toast.LENGTH_SHORT).show();
            return false;

        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "write your email", Toast.LENGTH_SHORT).show();
            return false;
            // لو كان خانة الباسورد فاضية
        }else if (TextUtils.isEmpty(authNumber)) {
            Toast.makeText(this, "please write your phone number ", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(Age)) {
            Toast.makeText(this, "please write your Age", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void SubmitAction(View view) {
        if (!validation()){
            return;
        }

        if (imageResult != null){
            FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            deleteImage(mFirebaseUser.getUid());
        }else {
            updateUserData();
        }
        hud.show();
    }


    public void chooseImageAction(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST  && data != null && data.getData() != null  )
        {
            imageResult = data.getData();
            ImageView img = findViewById(R.id.image_profile);
            Picasso.get().load(imageResult).placeholder(R.drawable.loading).into(img) ;
        }
    }
}
