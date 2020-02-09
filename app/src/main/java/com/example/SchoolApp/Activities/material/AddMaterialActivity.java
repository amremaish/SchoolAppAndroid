package com.example.SchoolApp.Activities.material;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.SchoolApp.Activities.Authentication.SignUp;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.tools;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.security.SecureRandom;

public class AddMaterialActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference mDatabaseReference;
    private KProgressHUD hud ;
    private Material mat ;
    private Uri imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Returns the currently signed-in FirebaseUser or null if there is none.
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // referense to the data base to be able to write and read to and from database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Inflate the layout for this fragment
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mat = new Material();
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
    }

    public void chooseMatImageAction(View view) {
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
        }
    }



    private void uploadImage() {

        final StorageReference ref = storageReference.child(com.example.SchoolApp.ref.CUR_USER.getEmail() +"/"+ tools.RandomString());
        ref.putFile(imageResult).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    hud.dismiss();
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downUri = task.getResult();
                    mat.setImgPath(downUri.toString());
                    sendDataToFirebase();
                }
            }
        });

    }
    private void sendDataToFirebase() {
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.MATERIAL);
        UserDatabaseReference.push().setValue(mat).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                hud.dismiss();
                Toast.makeText(AddMaterialActivity.this, "Created Successfully wait admin to review", Toast.LENGTH_SHORT).show();
                Log.e("Success", "createProfile:Success", task.getException());
                AddMaterialActivity.this.finish();
            }else {
                hud.dismiss();
                Toast.makeText(AddMaterialActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                Log.e("failure", "createProfile:failure", task.getException());
            }
        });

    }



    public void submitMaterialAction(View view) {

        if (!validation()){
            return ;
        }
        hud.show();
        uploadImage();
    }

    private boolean validation() {
        EditText titleText = findViewById(R.id.titleText) ;
        EditText descText = findViewById(R.id.descText) ;

        if (titleText.getText().toString().isEmpty()){
            Toast.makeText(this, "choose the title of material", Toast.LENGTH_SHORT).show();
            return false ;
        }else if (descText.getText().toString().isEmpty()){
            Toast.makeText(this, "choose the description of material", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if (imageResult == null) {
            Toast.makeText(this, "please select your image", Toast.LENGTH_SHORT).show();
            return false;
        }
        mat = new Material();
        mat.setDesc(descText.getText().toString());
        mat.setTitle(titleText.getText().toString());
        mat.setUser_email(mFirebaseUser.getEmail());
        mat.setId(tools.RandomString());
        mat.setStatus(FirebaseConstant.NONE);

        return true;
    }
}
