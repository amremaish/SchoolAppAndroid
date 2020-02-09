package com.example.SchoolApp.Activities.Authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.*;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import com.example.SchoolApp.Activities.Chatting.ChattingMainActivity;
import com.example.SchoolApp.Activities.Chatting.SendNotification;
import com.example.SchoolApp.Activities.admin.MaterialRequestActivity;
import com.example.SchoolApp.Activities.exams.ExamsActivity;
import com.example.SchoolApp.Activities.material.MaterialsActivity;
import com.example.SchoolApp.Activities.profile.ProfileActivity;
import com.example.SchoolApp.Models.Message;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();

        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.ADMIN)){
            navigationView.inflateMenu(R.menu.activity_admin_drawer);
        }else{
            navigationView.inflateMenu(R.menu.activity_main2_drawer);
        }



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername =  headerView.findViewById(R.id.username_nav);
        ImageView navImage =  headerView.findViewById(R.id.profile_img_nav);
        Picasso.get().load(ref.CUR_USER.getImagePath()).placeholder( R.drawable.loading).into(navImage) ;
        navUsername.setText(ref.CUR_USER.getUsername());
         startNotificationListener();
     //

    }

    private void startNotificationListener() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference  = mDatabaseReference.child(FirebaseConstant.MESSAGES);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg.getReceivedEmail() .equals(ref.CUR_USER.getEmail()) && msg.getSeenFromReceiver().equals("false")){
                        data.getRef().child("seenFromReceiver").setValue("true");
                        SendNotification.notificationDialog(Home.this , msg.getSend_user_name(), msg.getMessageText());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            signout();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile){
         startActivity(new Intent(this , ProfileActivity.class));
        }else if (id == R.id.nav_logout) {
            signout();
        }

        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.ADMIN)){

            if (id == R.id.material_request){
                startActivity(new Intent(this , MaterialRequestActivity.class));
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void signout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("sign out");
        builder.setMessage("do you want sign out ?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            Home.this.finish();
            dialog.dismiss();
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }
    public void openDrawerAction(View view) {
        drawer.openDrawer(Gravity.LEFT);
    }

    public void OpenMaterials(View view) {

        startActivity(new Intent(this , MaterialsActivity.class));
    }

    public void OpenExams(View view)
    {
        startActivity(new Intent(this , ExamsActivity.class));

    }

    public void OpenChat(View view) {
        startActivity(new Intent(this , ChattingMainActivity.class));
    }
}
