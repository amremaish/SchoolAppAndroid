package com.example.SchoolApp.Activities.Chatting;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Activities.Adapters.ChattingAdapter;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.firebase.database.*;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class ChattingMainActivity extends AppCompatActivity {
    private  KProgressHUD hud ;
    private  ArrayList<User> users ;
    private ChattingAdapter ChattingMainActivity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hud.show();
        LoadChat();

    }

    private void fill_recycle(){
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.chat_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChattingMainActivity.this));
        ChattingMainActivity = new ChattingAdapter(ChattingMainActivity.this, users);
        recyclerView.setAdapter(ChattingMainActivity);
    }

    private void LoadChat() {
        // load all teacher

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.USERS );
        UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               users = new ArrayList<>();
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    User user = data1.getValue(User.class);
                    if (user.getEmail().equals(ref.CUR_USER.getEmail())){
                        continue;
                    }
                    if ((user.getUserType().equals(FirebaseConstant.TEACHER) || user.getUserType().equals(FirebaseConstant.ADMIN) ) &&
                            (ref.CUR_USER.getUserType().equals(FirebaseConstant.STUDENT))){
                        users.add(user);
                    }else if (ref.CUR_USER.getUserType().equals(FirebaseConstant.ADMIN) || ref.CUR_USER.getUserType().equals(FirebaseConstant.TEACHER)){
                        users.add(user);
                    }else if (ref.CUR_USER.getUserType().equals(FirebaseConstant.PARENT) &&
                            (user.getUserType().equals(FirebaseConstant.TEACHER)
                            || user.getUserType().equals(FirebaseConstant.ADMIN))){
                        users.add(user);
                    }
                }
                fill_recycle();
                hud.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hud.dismiss();
                Toast.makeText(ChattingMainActivity.this,"Failed .",Toast.LENGTH_SHORT).show();
            }
        });
    }



}
