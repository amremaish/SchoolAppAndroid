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
import com.example.SchoolApp.Activities.material.MaterialDetailsActivity;
import com.example.SchoolApp.Activities.material.MaterialsActivity;
import com.example.SchoolApp.Models.Material;
import com.example.SchoolApp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.ViewHolder> {

    private List<Material> mData;
    private LayoutInflater LayoutInflater;
    private AppCompatActivity context ;

    // data is passed into the constructor
   public MaterialsAdapter(AppCompatActivity context, List<Material> data) {
        this.LayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.inflate(R.layout.material_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final  String title = mData.get(position).getTitle();
        final  String img_path = mData.get(position).getImgPath();
        Picasso.get().load(img_path).placeholder( R.drawable.loading).into(holder.mat_img) ;
        holder.mat_title.setText(title);
        holder.mat_layout.setOnClickListener(v -> {
            String desc = mData.get(position).getDesc();
            String title1 = mData.get(position).getTitle();
            String img_path1 = mData.get(position).getImgPath();
            String id = mData.get(position).getId();
            String email = mData.get(position).getUser_email();
            Intent i = new Intent(context , MaterialDetailsActivity.class);
            i.putExtra("desc" ,desc);
            i.putExtra("title" , title1);
            i.putExtra("img_path" , img_path1);
            i.putExtra("id" ,id);
            i.putExtra("email" ,email);
            context.startActivity(i);
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mat_title ;
        ImageView mat_img ;
        LinearLayout mat_layout ;
        ViewHolder(View itemView) {
            super(itemView);
            mat_img = itemView.findViewById(R.id.mat_img);
            mat_title = itemView.findViewById(R.id.mat_title);
            mat_layout = itemView.findViewById(R.id.mat_layout);
        }

    }

}