package com.example.SchoolApp.Activities.exams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SchoolApp.Activities.material.AddMaterialActivity;
import com.example.SchoolApp.Models.Exam;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.example.SchoolApp.tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AddExamActivity extends AppCompatActivity {


    private Exam exam ;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference   mDatabaseReference;
    private KProgressHUD hud ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Returns the currently signed-in FirebaseUser or null if there is none.
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // referense to the data base to be able to write and read to and from database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        TextView exam_title = findViewById(R.id.exam_teacher_name) ;
        exam_title.setText(ref.CUR_USER.getUsername());
        // Inflate the layout for this fragment
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
    }

    public void submitExamAction(View view) {

        if (!validation()){
            return ;
        }
        hud.show();
        sendDataToFirebase();

    }

    private boolean validation() {
        EditText exam_title = findViewById(R.id.add_exam_name) ;
        EditText max_score_exam = findViewById(R.id.max_score_exam) ;

        if (exam_title.getText().toString().isEmpty()){
            Toast.makeText(this, "Choose the title of exam", Toast.LENGTH_SHORT).show();
            return false ;
        }else if (max_score_exam.getText().toString().isEmpty()){
            Toast.makeText(this, "Select the score of exam ", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if (!max_score_exam.getText().toString().matches("-?\\d+(.\\d+)?")){
            Toast.makeText(this, "be sure the score contains numbers only ", Toast.LENGTH_SHORT).show();
            return false ;
        }

        exam = new Exam();
        exam.setMaxScore(max_score_exam.getText().toString());
        exam.setName(exam_title.getText().toString());
        exam.setId(tools.RandomString());
        exam.setTeacherEmail(ref.CUR_USER.getUsername());


        return true;
    }
    private void sendDataToFirebase() {
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.EXAM);
        UserDatabaseReference.push().setValue(exam).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                hud.dismiss();
                Toast.makeText(AddExamActivity.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                Log.e("Success", "createProfile:Success", task.getException());
                AddExamActivity.this.finish();
            }else {
                hud.dismiss();
                Toast.makeText(AddExamActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                Log.e("failure", "createProfile:failure", task.getException());
            }
        });

    }
}
