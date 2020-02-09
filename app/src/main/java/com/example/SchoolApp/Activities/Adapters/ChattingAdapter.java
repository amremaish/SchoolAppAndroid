package com.example.SchoolApp.Activities.Adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SchoolApp.Activities.Chatting.ChattingActivity;
import com.example.SchoolApp.Models.User;
import com.example.SchoolApp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder>{



    private List<User> mData;
    private android.view.LayoutInflater LayoutInflater;
    private AppCompatActivity context ;

    // data is passed into the constructor
    public ChattingAdapter(AppCompatActivity context, List<User> data) {
        this.LayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ChattingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.inflate(R.layout.chatting_row, parent, false);
        return new ChattingAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ChattingAdapter.ViewHolder holder, final int position) {

        holder.chat_user_name.setText(mData.get(position).getUsername());
        holder.chat_user_type.setText(mData.get(position).getUserType());
        Picasso.get().load(mData.get(position).getImagePath()).placeholder( R.drawable.loading).into(holder.prof_img) ;
        holder.chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context , ChattingActivity.class);
                i.putExtra("email" ,mData.get(position).getEmail());
                i.putExtra("user_type" ,mData.get(position).getUserType());
                i.putExtra("user_name" ,mData.get(position).getUsername());
                i.putExtra("img_path" ,mData.get(position).getImagePath());
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

        TextView chat_user_type , chat_user_name ;
        ImageView prof_img ;
        LinearLayout chat_layout ;
        ViewHolder(View itemView) {
            super(itemView);
            chat_layout = itemView.findViewById(R.id.chat_layout);
            prof_img = itemView.findViewById(R.id.prof_img);
            chat_user_type = itemView.findViewById(R.id.chat_user_type);
            chat_user_name = itemView.findViewById(R.id.chat_user_name);
        }

    }
}
