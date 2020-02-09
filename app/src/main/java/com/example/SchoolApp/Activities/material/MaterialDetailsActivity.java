package com.example.SchoolApp.Activities.material;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

public class MaterialDetailsActivity extends AppCompatActivity {

    private TextView titleText;
    private EditText descText;
    private ImageView mat_img ;
    private KProgressHUD hud ;
    public static  MaterialDetailsActivity MaterialDetailsActivity ;
    private String img_path , desc , id , user_email , title ;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_details);

        FloatingActionButton addbtn = findViewById(R.id.floatingSettingsActionButton);
        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.STUDENT)
                || ref.CUR_USER.getUserType().equals(FirebaseConstant.PARENT)){
            addbtn.setVisibility(View.GONE);
        }
        LinearLayout admin_status = findViewById(R.id.admin_status_layer);
        admin_status.setVisibility(View.GONE);
        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.ADMIN)){
            admin_status.setVisibility(View.VISIBLE);
        }
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        MaterialDetailsActivity = this ;
        titleText = findViewById(R.id.mat_title_details);
        descText = findViewById(R.id.mat_desc_details);
        mat_img = findViewById(R.id.mat_img);

        descText.setFocusable(false);
        LoadData();
        Picasso.get().load(img_path).into(mat_img);
        descText.setText(desc);
        titleText.setText(title);
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


    public void EditMaterialAction(View view) {
        Intent i = new Intent(this , MaterialSettingsActivity.class);
        i.putExtra("desc" , desc);
        i.putExtra("title" , title);
        i.putExtra("img_path" ,img_path);
        i.putExtra("id" ,id);
        i.putExtra("email" ,user_email);
        startActivity(i);


    }


    private void updateStatus(final String status){
        DatabaseReference UserRoot = FirebaseDatabase.getInstance().getReference().getRoot().child(FirebaseConstant.MATERIAL);
        UserRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Material mat = dataSnapshot1.getValue(Material.class);
                    if (mat.getId().equals(id)) {
                        dataSnapshot1.getRef().child("status").setValue(status);
                        Toast.makeText(MaterialDetailsActivity.this,"material was " + status ,Toast.LENGTH_SHORT).show();
                        hud.dismiss();
                        MaterialDetailsActivity.finish();
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


    public void AcceptAction(View view) {
        updateStatus(FirebaseConstant.ACCEPTED);

    }

    public void RefuseAction(View view) {
        updateStatus(FirebaseConstant.REFUSED);
    }
}
