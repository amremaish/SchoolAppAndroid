package com.example.SchoolApp.Activities.Authentication;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

public class SignUp extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference mDatabaseReference;
    private KProgressHUD hud ;
    private User user ;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageResult;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPassText;
    private EditText authNumberText;
    private EditText AgeText;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Returns the currently signed-in FirebaseUser or null if there is none.
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // referense to the data base to be able to write and read to and from database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Inflate the layout for this fragment
        inflateUiElements();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        inflateUiElements();
    }


    // this method for Inflate the layout for this fragment
    public void inflateUiElements() {
        // ????? ?????
        nameText = findViewById(R.id.username_txt);
        // ????? ???????
        emailText = findViewById(R.id.email_txt);
        // ????? ????????
        passwordText = findViewById(R.id.password_txt);
        // ????? ????? ???????
        confirmPassText = findViewById(R.id.confirm_password_txt);
        // ????? ??? ???????
        authNumberText = findViewById(R.id.phone_number_txt);
        AgeText = findViewById(R.id.age_txt);

    }

    private boolean validation() {
        String name = nameText.getText().toString().trim();
        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        String confirmPass = confirmPassText.getText().toString().trim();
        String authNumber = authNumberText.getText().toString().trim();
        String Age = AgeText.getText().toString().trim();
        // ?? ??? ???? ????? ?????
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "write your name", Toast.LENGTH_SHORT).show();
            return false;
            // ?? ??? ???? ??????? ?????
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "write your email", Toast.LENGTH_SHORT).show();
            return false;
            // ?? ??? ???? ???????? ?????
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "write the password", Toast.LENGTH_SHORT).show();
            return false;
            // ?? ??? ???? ????? ????????
        } else if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "write your confirm password", Toast.LENGTH_SHORT).show();
            return false;
            // ?? ??? ???? ?????? ?? ????? ?????
        } else if (TextUtils.isEmpty(authNumber)) {
            Toast.makeText(this, "please write your phone number ", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(Age)) {
            Toast.makeText(this, "please write your Age", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!password.equals(confirmPass)) {
            Toast.makeText(this, "your password and confirm not the same", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "please write your password greater than 5 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmailAddress(email)) {
            Toast.makeText(this, "please check your email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (imageResult == null) {
            Toast.makeText(this, "please select your image", Toast.LENGTH_SHORT).show();
            return false;
        }
        user = new User();
        user.setAge(Age);
        user.setEmail(email);
        user.setPhone_number(authNumber);
        user.setUsername(name);
        user.setPass(passwordText.getText().toString());

        RadioButton radioTeacher = findViewById(R.id.radioTeacher);
        RadioButton radioStudent = findViewById(R.id.radioStudent);
        if (radioTeacher.isChecked()){
            user.setUserType(FirebaseConstant.TEACHER);
        }else if (radioStudent.isChecked()){
            user.setUserType(FirebaseConstant.STUDENT);
        }else {
            user.setUserType(FirebaseConstant.PARENT);
        }
        ref.CUR_USER = user ;
        return true;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
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
        }
    }


    public void SignUpAction(View view) {
        if (!validation()){
            return;
        }
        hud.show();
        createUserOnFirebaseAuthentication(email , password);
    }

    public void createUserOnFirebaseAuthentication(String email, String password) {

            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e("createUserWithEmail", "createUserWithEmail:success");
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                uploadImage(user.getUid());
                            } else {
                                if (task.getException()instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(SignUp.this, "Your email is already used",Toast.LENGTH_SHORT).show();
                                    hud.dismiss();
                                    return ;
                                }
                                // If sign in fails, display a message to the user.
                                Log.e("createUserWithEmail", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "something wrong .. please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private void sendDataToFirebase(String userID) {
        DatabaseReference donorUserDatabaseReference = mDatabaseReference.child(FirebaseConstant.USERS).child(userID);
        donorUserDatabaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    hud.dismiss();
                    Toast.makeText(SignUp.this, "you have signed up",  Toast.LENGTH_LONG).show();
                    SignUp.this.finish();
                    Log.e("Success", "createProfile:Success", task.getException());
                } else {
                    Log.e("failure", "createProfile:failure", task.getException());
                }
            }
        });

    }


    private void uploadImage(final String userID) {

            final StorageReference ref = storageReference.child(com.example.SchoolApp.ref.CUR_USER.getEmail() + "/profile_pic");
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
                        user.setImagePath(downUri.toString());
                        sendDataToFirebase(userID);

                    }
                }
            });

    }


}
