package com.example.SchoolApp.Activities.exams;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.SchoolApp.Activities.Adapters.ExamStudentAdapter;
import com.example.SchoolApp.Activities.Adapters.ExamsAdapter;
import com.example.SchoolApp.Models.Exam;
import com.example.SchoolApp.Models.StudentExam;
import com.example.SchoolApp.Models.StudentExamAdapter;
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

public class ExamsActivity extends AppCompatActivity {
    private ArrayList<Exam> examList;
    private  ArrayList<StudentExamAdapter> examAdapterList ;
    private  ArrayList<StudentExam> StudentExamList ;
    public static  ExamsAdapter ExamsAdapter;
    public static  ExamStudentAdapter StudentExamAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
        // if student
        // show all exams of him
        // if teacher
        // show all exams and each exam get all student then put degree for each student
        // if
    }


    private void LoadTeacherExams() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.EXAM);
        UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                examList = new ArrayList<>();
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    Exam iexam = data1.getValue(Exam.class);
                    examList.add(iexam);
                }

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.exams_recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(ExamsActivity.this));
                ExamsAdapter = new ExamsAdapter(ExamsActivity.this, examList);
                recyclerView.setAdapter(ExamsAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExamsActivity.this,"Failed .",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void LoadExam(){

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.EXAM);
        UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    Exam exam = data1.getValue(Exam.class);
                    for(int i = 0 ;i < examAdapterList.size() ; i++){
                            if (examAdapterList.get(i).getExam_id().equals(exam.getId())){
                                examAdapterList.get(i).setMax_grade(exam.getMaxScore());
                                examAdapterList.get(i).setExam_name(exam.getName());
                            }
                    }
                }
                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.exams_recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(ExamsActivity.this));
                StudentExamAdapter = new ExamStudentAdapter(ExamsActivity.this, examAdapterList);
                recyclerView.setAdapter(StudentExamAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExamsActivity.this,"Failed .",Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void LoadStudentExams() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.STUDENT_EXAMS);
        UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                examAdapterList  = new ArrayList<>();
                StudentExamList = new ArrayList<>();
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    StudentExam iexam = data1.getValue(StudentExam.class);
                    if (iexam.getStudent_email().equals(ref.CUR_USER.getEmail())){
                        StudentExamAdapter nexam = new StudentExamAdapter();
                        nexam.setStudent_name(ref.CUR_USER.getUsername());
                        nexam.setExam_id(iexam.getExam_id());
                        nexam.setGrade(iexam.getGrade());
                        examAdapterList.add(nexam);
                        StudentExamList.add(iexam);
                    }
                }
                LoadExam();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExamsActivity.this,"Failed .",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onStart() {
        super.onStart();

        FloatingActionButton addbtn  =  findViewById(R.id.floatingAddAction);
        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.STUDENT)){
            addbtn.setVisibility(View.GONE);
            LoadStudentExams();
        }else if (ref.CUR_USER.getUserType().equals(FirebaseConstant.PARENT)){
            addbtn.setVisibility(View.GONE);
            LoadTeacherExams();
        }else {
            LoadTeacherExams();
        }

    }


    public void AddExamAction(View view) {
        Intent in = new Intent(this , AddExamActivity.class);
        startActivity(in);
    }
}
