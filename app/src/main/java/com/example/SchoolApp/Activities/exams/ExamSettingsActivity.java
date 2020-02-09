package com.example.SchoolApp.Activities.exams;

import android.content.Intent;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Activities.Adapters.ExamsSettingsAdapter;
import com.example.SchoolApp.Models.StudentExam;
import com.example.SchoolApp.Models.StudentExamAdapter;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.google.firebase.database.*;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class ExamSettingsActivity extends AppCompatActivity {

    private String id , max_score , name , teacher_email ;
    private DatabaseReference mDatabaseReference;
    private KProgressHUD hud ;
    private String TAG = "ExamSettingsActivity";
    private ArrayList<StudentExamAdapter> student_list  ;
    public static  ExamsSettingsAdapter ExamsSettingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_details);
        getExamData();
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hud.show();
        LoadStudents();

    }

    private void LoadStudents() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference  UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.USERS);
        UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student_list = new ArrayList<>();
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    User user = data1.getValue(User.class);
                    if (user.getUserType().equals(FirebaseConstant.STUDENT)){
                        StudentExamAdapter exam = new StudentExamAdapter();
                        exam.setExam_name(name);
                        exam.setMax_grade(max_score);
                        exam.setStudentEmail(user.getEmail());
                        exam.setStudent_name(user.getUsername());
                        exam.setExam_id(id);
                        student_list.add(exam);
                    }
                }
                getExamGrade();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hud.dismiss(); Log.d(TAG, "onCancelled: ");
            }
        });
    }

    private void getExamGrade(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference  UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.STUDENT_EXAMS);
        UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    StudentExam exam = data1.getValue(StudentExam.class);
                    String email  = exam.getStudent_email();
                    for (int i = 0 ; i <student_list.size() ; i++ ){
                            if (email.equals(student_list.get(i).getStudentEmail()) && exam.getExam_id().equals(id) ){
                                student_list.get(i).setGrade(exam.getGrade());
                            }
                    }
                }
                fill_recycle();
                hud.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hud.dismiss(); Log.d(TAG, "onCancelled: ");
            }
        });

    }

    private void getExamData() {
        Intent i = getIntent();
        id = i.getStringExtra("id");
        max_score = i.getStringExtra("max_score");
        name = i.getStringExtra("name");
        teacher_email = i.getStringExtra("teacher_email");
    }

    private void fill_recycle(){
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.exam_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExamSettingsActivity.this));
        ExamsSettingsAdapter = new ExamsSettingsAdapter(ExamSettingsActivity.this, student_list);
        recyclerView.setAdapter(ExamsSettingsAdapter);
    }



}

