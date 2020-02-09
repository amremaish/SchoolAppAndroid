package com.example.SchoolApp.Activities.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SchoolApp.Activities.exams.ExamSettingsActivity;
import com.example.SchoolApp.Activities.material.MaterialDetailsActivity;
import com.example.SchoolApp.Activities.material.MaterialsActivity;
import com.example.SchoolApp.Models.Exam;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ViewHolder> {

    private List<Exam> mData;
    private LayoutInflater LayoutInflater;
    private AppCompatActivity context ;

    // data is passed into the constructor
   public ExamsAdapter(AppCompatActivity context, List<Exam> data) {
        this.LayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.inflate(R.layout.exam_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.exam_title.setText(mData.get(position).getName());
        holder.teacher_exam_name.setText(mData.get(position).getTeacherEmail());
        holder.exam_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context , ExamSettingsActivity.class);
                i.putExtra("id" ,mData.get(position).getId() ) ;
                i.putExtra("max_score" ,mData.get(position).getMaxScore() ) ;
                i.putExtra("name" ,mData.get(position).getName() ) ;
                i.putExtra("teacher_email" ,mData.get(position).getTeacherEmail() ) ;
                context.startActivity(i);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView exam_title , teacher_exam_name;
        LinearLayout exam_layout ;
        ViewHolder(View itemView) {
            super(itemView);
            exam_title = itemView.findViewById(R.id.exam_title);
            teacher_exam_name = itemView.findViewById(R.id.teacher_exam_name);
            exam_layout = itemView.findViewById(R.id.exam_layout);
        }

    }

}