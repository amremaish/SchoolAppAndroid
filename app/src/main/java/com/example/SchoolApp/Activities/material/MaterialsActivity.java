package com.example.SchoolApp.Activities.material;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Activities.Adapters.MaterialsAdapter;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MaterialsActivity extends AppCompatActivity {
    private ArrayList<Material> matList ;
   public static  MaterialsAdapter MaterialsAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);
        FloatingActionButton addbtn  =   findViewById(R.id.floatingAddAction);
        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.STUDENT)
                || ref.CUR_USER.getUserType().equals(FirebaseConstant.PARENT)){
            addbtn.setVisibility(View.GONE);
        }
        // data to populate the RecyclerView with

    }

    public void AddMaterialAction(View view) {
        Intent in = new Intent(this , AddMaterialActivity.class);
        startActivity(in);
    }

    private void LoadAllRequests() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.MATERIAL);
        UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matList = new ArrayList<>();
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    Material imat = data1.getValue(Material.class);
                    if (imat.getStatus().equals(FirebaseConstant.ACCEPTED)) {
                        matList.add(imat);
                    }
                }

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.material_recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MaterialsActivity.this));
                MaterialsAdapter = new MaterialsAdapter(MaterialsActivity.this, matList);
                recyclerView.setAdapter(MaterialsAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MaterialsActivity.this,"Failed .",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadAllRequests();
    }
}

