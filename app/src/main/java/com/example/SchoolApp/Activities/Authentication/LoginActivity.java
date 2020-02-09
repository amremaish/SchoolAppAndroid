package com.example.SchoolApp.Activities.Authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;



public final class LoginActivity extends AppCompatActivity {

    private KProgressHUD hud ;
    private static final String TAG = "SignInActivity";
    private EditText email;
    private EditText password;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashing();
        mFirebaseAuth = FirebaseAuth.getInstance();
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
    }


    private void splashing(){
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
                ImageView bookIconImageView = findViewById(R.id.bookIconImageView);
                bookIconImageView.setVisibility(View.GONE);
                ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);
                loadingProgressBar.setVisibility(View.GONE);
                RelativeLayout relativeLayout =  findViewById(R.id.afterAnimationView);
                relativeLayout.setVisibility(View.VISIBLE);
                RelativeLayout rootView =  findViewById(R.id.rootView);
                rootView.setBackground(ContextCompat.getDrawable(LoginActivity.this, (R.color.colorSplashText)));
            }
            public void onTick(long p0) {
            }
        }.start();
    }
    public final void SignUpAction(View v1) {
        Intent intent = new Intent(LoginActivity.this, SignUp.class);
        this.startActivity(intent);

    }

    private void  AuthLogin(String email , String password){
        mFirebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            loginAsUser(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "email or password isn't correct", Toast.LENGTH_SHORT).show();
                            hud.dismiss();
                        }
                    }
                });

    }
    public void loginAsUser(final String userID){
        DatabaseReference UserDatabaseReference = null;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.USERS).child(userID);
        UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ref.CUR_USER = user;
                hud.dismiss();
                Toast.makeText(LoginActivity.this, "Welcome! you have signed in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, Home.class);
                LoginActivity.this.startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hud.dismiss(); Log.d(TAG, "onCancelled: ");
            }
        });
    }


    public final void LoginAction(View v2) {
  email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        if (email.getText().toString().trim().length() == 0 || password.getText().toString().trim().length() == 0 ) {
            Toast.makeText(LoginActivity.this, "Fill empty field please ", Toast.LENGTH_LONG).show();
            return;
        }
        hud.show();
        AuthLogin(email.getText().toString(),password.getText().toString());

    }


    public void forgetPasswordAction(View view) {
        Intent intent = new Intent(LoginActivity.this, forgetPassword.class);
        LoginActivity.this.startActivity(intent);

    }
}