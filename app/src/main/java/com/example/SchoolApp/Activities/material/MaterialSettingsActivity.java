package com.example.SchoolApp.Activities.material;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.tools;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;

public class MaterialSettingsActivity extends AppCompatActivity {

    private KProgressHUD hud ;
    private EditText titleText;
    private EditText descText;
    private ImageView mat_img ;
    private String img_path , desc , id , user_email , title ;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageResult ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_settings);
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        titleText = findViewById(R.id.mat_title_details);
        descText = findViewById(R.id.mat_desc_details);
        mat_img = findViewById(R.id.mat_img);
        LoadData();
        Picasso.get().load(img_path).into(mat_img);
        descText.setText(desc);
        titleText.setText(title);
        getUserInfo(user_email);
    }

    private void LoadData(){
        Intent i = this.getIntent() ;
        descText.setText(i.getStringExtra("desc"));
        titleText.setText(i.getStringExtra("title"));
        desc =  i.getStringExtra("desc");
        title =  i.getStringExtra("title");
        img_path = i.getStringExtra("img_path" );
        id =  i.getStringExtra("id" );
        user_email =  i.getStringExtra("email");
    }

    public void SaveChangeAction(View view) {
        hud.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
     if (imageResult == null){
            updateData();
        }else {
            uploadImage(mAuth.getUid());
        }
    }


   private  void getUserInfo(final String email){
       DatabaseReference UserRoot = FirebaseDatabase.getInstance().getReference().getRoot().child(FirebaseConstant.USERS);
       UserRoot.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                   User user = dataSnapshot1.getValue(User.class);
                   if (user.getEmail().equals(email)){
                       TextView user_type = findViewById(R.id.user_type);
                       user_type.setText(user.getUserType());
                       TextView user_name = findViewById(R.id.user_name);
                       user_name.setText(user.getUsername());
                       break;
                   }
               }
           }
           @Override
           public void onCancelled(DatabaseError databaseError) {
               Log.d("onCancelled", "onCancelled: error while getting data");
               hud.dismiss();
           }
       });

   }


    private void updateData(){
        DatabaseReference UserRoot = FirebaseDatabase.getInstance().getReference().getRoot().child(FirebaseConstant.MATERIAL);
        UserRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Material mat = dataSnapshot1.getValue(Material.class);
                    if (mat.getId().equals(id)) {
                        dataSnapshot1.getRef().child("title").setValue(titleText.getText().toString());
                        dataSnapshot1.getRef().child("desc").setValue(descText.getText().toString());
                        if (imageResult != null) {
                            dataSnapshot1.getRef().child("imgPath").setValue(imageResult.toString());
                        }
                        FirebaseAuth  mAuth = FirebaseAuth.getInstance();
                        dataSnapshot1.getRef().child("user_email").setValue(mAuth.getCurrentUser().getEmail());
                        dataSnapshot1.getRef().child("id").setValue(id);

                        Toast.makeText(MaterialSettingsActivity.this, "Successfully changed ", Toast.LENGTH_LONG).show();
                        hud.dismiss();
                        MaterialSettingsActivity.this.finish();
                        MaterialDetailsActivity.MaterialDetailsActivity .finish();

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", "onCancelled: error while getting data");
                hud.dismiss();
            }
        });

    }


    private void uploadImage(final String userID) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference  storageReference = storage.getReference();
        final StorageReference ref = storageReference.child(userID +"/"+ tools.RandomString());
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
                    imageResult = downUri ;
                    updateData();
                }
            }
        });
    }
    private void DeleteFromFirebase(){
        hud.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference();
        myref.child(FirebaseConstant.MATERIAL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Material mat = ds.getValue(Material.class);
                    if (mat.getId().equals(id)){
                        ds.getRef().setValue(null);
                        hud.dismiss();
                        Toast.makeText(MaterialSettingsActivity.this, "Successfully deleted.", Toast.LENGTH_LONG).show();
                        MaterialSettingsActivity.this.finish();
                        MaterialDetailsActivity.MaterialDetailsActivity .finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hud.dismiss();
                Log.d("User", databaseError.getMessage());
            }
        });

    }

    public void DeleteMatAction(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DeleteFromFirebase();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
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
            Picasso.get().load(imageResult).into(mat_img);
        }
    }

}
