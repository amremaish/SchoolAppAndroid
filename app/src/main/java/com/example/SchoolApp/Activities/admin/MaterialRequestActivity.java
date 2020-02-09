package com.example.SchoolApp.Activities.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Activities.Adapters.MaterialsAdapter;
import com.example.SchoolApp.Activities.material.AddMaterialActivity;
import com.example.SchoolApp.Activities.material.MaterialsActivity;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MaterialRequestActivity extends AppCompatActivity {
    private ArrayList<Material> matList ;
    public static  MaterialsAdapter MaterialsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_request);
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
                    if (imat.getStatus().equals(FirebaseConstant.NONE)) {
                        matList.add(imat);
                    }
                }

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.mat_req_recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MaterialRequestActivity.this));
                MaterialsAdapter = new MaterialsAdapter(MaterialRequestActivity.this, matList);
                recyclerView.setAdapter(MaterialsAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MaterialRequestActivity.this,"Failed .",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadAllRequests();
    }
}

