package com.example.SchoolApp.Activities.Chatting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Models.Message;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.widget.ListPopupWindow.MATCH_PARENT;

public class ChattingActivity extends AppCompatActivity {


   private String Recieved_email ;
   private String Recieved_img_path ;
   private String Recieved_user_name ;
   private Message msg ;
   private ArrayList<Message> messages ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Intent i = getIntent();

        Recieved_email  =  i.getStringExtra("email");
        Recieved_img_path  =  i.getStringExtra("img_path");
        Recieved_user_name  =  i.getStringExtra("user_name");

        TextView txt =  findViewById(R.id.user_name_title);
        txt.setText(Recieved_user_name);

        msg = new Message();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy (HH:mm:ss)", Locale.getDefault());
        msg.setMessageTime(sdf.format(new Date()));
        msg.setSendEmail(ref.CUR_USER.getEmail());
        msg.setReceivedEmail(Recieved_email);
        msg.setReceived_user_name(Recieved_user_name);
        msg.setSend_user_name(ref.CUR_USER.getUsername());

//        i.putExtra("email" ,mData.get(position).getEmail());
//        i.putExtra("user_type" ,mData.get(position).getUserType());
//        i.putExtra("user_name" ,mData.get(position).getUsername());

        LoadMessages();

    }

    private void LoadMessages() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference  = mDatabaseReference.child(FirebaseConstant.MESSAGES);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);

                    if (msg.getReceivedEmail().equals(Recieved_email) && msg.getSendEmail().equals(ref.CUR_USER.getEmail()) ){
                        msg.setSeenFromSender("true");
                        data.getRef().child("seenFromSender").setValue("true");
                        messages.add(msg);
                    }

                    if (msg.getReceivedEmail().equals(ref.CUR_USER.getEmail()) && msg.getSendEmail().equals(Recieved_email) ){
                        messages.add(msg);
                    }
                }
                fillRecycle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void SendMessageAction(View view) {

        EditText msg_text = this.findViewById(R.id.msg_text);
        msg.setMessageText(msg_text.getText().toString());
        msg.setSeenFromSender("true");
        msg_text.setText("");

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.MESSAGES);
        UserDatabaseReference.push().setValue(msg).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                fillRecycle();
                Log.e("Success", "createProfile:Success", task.getException());
            }else {
                Toast.makeText(ChattingActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                Log.e("failure", "createProfile:failure", task.getException());
            }
        });



    }
    private void fillRecycle(){
        ScrollView scroll = findViewById(R.id.messages_scroll);
        scroll.fullScroll(View.FOCUS_DOWN);
        LinearLayout messages_layout = findViewById(R.id.messages_layout);
        messages_layout.removeAllViews();
        for(int i = 0 ; i < messages.size() ; i++ ){
            View view ;
            if (messages.get(i).getReceivedEmail().equals(Recieved_email)){
                view =  getLayoutInflater().inflate(R.layout.send_message_row, null);
                TextView msg = view.findViewById(R.id.message_body);
                msg.setText(messages.get(i).getMessageText());

            }else {
                view = getLayoutInflater().inflate(R.layout.receive_message_row, null);
                ImageView img = view.findViewById(R.id.avatar);
                Picasso.get().load(ref.CUR_USER.getImagePath()).placeholder( R.drawable.loading).into(img) ;

                TextView msg = view.findViewById(R.id.message_body);
                msg.setText(messages.get(i).getMessageText());

                TextView name = view.findViewById(R.id.name);
                name.setText(messages.get(i).getReceived_user_name());

            }
            messages_layout.addView(view);

        }
    }

}
