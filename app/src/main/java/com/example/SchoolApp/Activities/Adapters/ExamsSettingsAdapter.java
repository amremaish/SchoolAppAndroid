package com.example.SchoolApp.Activities.Adapters;

import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Activities.exams.AddExamActivity;
import com.example.SchoolApp.Activities.material.MaterialDetailsActivity;
import com.example.SchoolApp.Activities.material.MaterialSettingsActivity;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.Models.StudentExam;
import com.example.SchoolApp.Models.StudentExamAdapter;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.example.SchoolApp.firebase.FirebaseConstant;
import com.example.SchoolApp.ref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

public class ExamsSettingsAdapter extends RecyclerView.Adapter<ExamsSettingsAdapter.ViewHolder> {

    private List<StudentExamAdapter> usersData;
    private LayoutInflater LayoutInflater;
    private AppCompatActivity context ;
    private KProgressHUD hud ;

    // data is passed into the constructor
   public ExamsSettingsAdapter(AppCompatActivity context, List<StudentExamAdapter> data) {
       hud = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        this.LayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.usersData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.inflate(R.layout.exam_settings_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.exam_name.setText(usersData.get(position).getExam_name());
        holder.student_name.setText(usersData.get(position).getStudent_name());
        if (usersData.get(position).getGrade() != null) {
            holder.grade.setText(usersData.get(position).getGrade() + "/" + usersData.get(position).getMax_grade());
        }else{
            holder.grade.setText("?/" + usersData.get(position).getMax_grade());
        }
        if (ref.CUR_USER.getUserType().equals(FirebaseConstant.PARENT)){
            holder.tap_grade.setVisibility(View.GONE);
            return ;
        }
        holder.exam_settings_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder[] builder = {new AlertDialog.Builder(context)};
                builder[0].setTitle("Grade of " + usersData.get(position).getStudent_name());
                // Set up the input
                final EditText input = new EditText(context);
                if (usersData.get(position).getGrade() != null) {
                    input.setText(usersData.get(position).getGrade());
                }
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder[0].setView(input);

                // Set up the buttons
                builder[0].setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredValue = input.getText().toString();

                        Double parsed  = Double.parseDouble(enteredValue);
                        Double parsedMax   =  Double.parseDouble( usersData.get(position).getMax_grade());
                        if (parsed > parsedMax ){
                            Toast.makeText(context, "Must grade less than max grade.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UpdateExamGrade(enteredValue ,usersData.get(position) );

                    }
                });
                builder[0].setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder[0].show();
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return usersData.size();
    }

    private void UpdateExamGrade(final String enteredValue , final StudentExamAdapter exam){
        hud.show();

        if (exam.getGrade() == null){
            addNewGrade(enteredValue , exam);
        }else {
            updateGrade(enteredValue , exam);
        }


    }

    private void addNewGrade(final String enteredValue , final StudentExamAdapter exam) {
       final StudentExam nexam = new StudentExam();
       nexam.setExam_id(exam.getExam_id());
       nexam.setGrade(enteredValue);
       nexam.setStudent_email(exam.getStudentEmail());

        DatabaseReference  mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserDatabaseReference = mDatabaseReference.child(FirebaseConstant.STUDENT_EXAMS);
        UserDatabaseReference.push().setValue(nexam).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    hud.dismiss();
                    Toast.makeText(context, "Successfully added !!", Toast.LENGTH_SHORT).show();
                    Log.e("Success", "createProfile:Success", task.getException());

                }else {
                    hud.dismiss();
                    Toast.makeText(context, "Failed !!", Toast.LENGTH_SHORT).show();
                    Log.e("failure", "createProfile:failure", task.getException());
                }
            }
        });

    }

    private void updateGrade(final String enteredValue , final StudentExamAdapter exam) {

        DatabaseReference UserRoot = FirebaseDatabase.getInstance().getReference().getRoot().child(FirebaseConstant.STUDENT_EXAMS);
        UserRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    StudentExam iexam = dataSnapshot1.getValue(StudentExam.class);
                    if (iexam.getExam_id().equals(exam.getExam_id())  && iexam.getStudent_email().equals(exam.getStudentEmail())) {
                        dataSnapshot1.getRef().child("grade").setValue(enteredValue);
                    }
                }
                hud.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", "onCancelled: error while getting data");
                hud.dismiss();
            }
        });
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView exam_name , student_name , grade , tap_grade;
        LinearLayout exam_settings_layout ;
        ViewHolder(View itemView) {
            super(itemView);
            tap_grade = itemView.findViewById(R.id.tap_grade);
            exam_name = itemView.findViewById(R.id.exam_name);
            student_name = itemView.findViewById(R.id.student_name);
            grade = itemView.findViewById(R.id.grade);
            exam_settings_layout = itemView.findViewById(R.id.exam_settings_layout);
        }

    }

}