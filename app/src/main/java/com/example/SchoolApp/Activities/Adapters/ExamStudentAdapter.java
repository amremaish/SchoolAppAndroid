package com.example.SchoolApp.Activities.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Models.StudentExamAdapter;
import com.example.SchoolApp.R;

import java.util.List;

public class ExamStudentAdapter  extends RecyclerView.Adapter<ExamStudentAdapter.ViewHolder>{

    private List<StudentExamAdapter> mData;
    private android.view.LayoutInflater LayoutInflater;
    private AppCompatActivity context ;

    // data is passed into the constructor
    public ExamStudentAdapter(AppCompatActivity context, List<StudentExamAdapter> data) {
        this.LayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ExamStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.inflate(R.layout.exam_settings_row, parent, false);
        return new ExamStudentAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ExamStudentAdapter.ViewHolder holder, final int position) {
        holder.exam_name.setText(mData.get(position).getExam_name());
        holder.student_name.setText(mData.get(position).getStudent_name());
        holder.tap_grade.setVisibility(View.GONE);
        if (mData.get(position).getGrade() != null) {
            holder.grade.setText(mData.get(position).getGrade() + "/" + mData.get(position).getMax_grade());
        }else{
            holder.grade.setText("?/" + mData.get(position).getMax_grade());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView exam_name , student_name , grade, tap_grade;
        ViewHolder(View itemView) {
            super(itemView);
            exam_name = itemView.findViewById(R.id.exam_name);
            student_name = itemView.findViewById(R.id.student_name);
            grade = itemView.findViewById(R.id.grade);
            tap_grade = itemView.findViewById(R.id.tap_grade);

        }

    }
}
