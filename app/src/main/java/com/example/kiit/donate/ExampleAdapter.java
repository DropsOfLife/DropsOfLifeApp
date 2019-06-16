package com.example.kiit.donate;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
   private ArrayList<ExampleItem> mExampleList;
   private OnItemClickListener mListener;
   public interface OnItemClickListener {
       void onItemClick(int position);
       void onCallClick(int position);
   }

   public void setOnItemClickListener(OnItemClickListener listener){
       mListener = listener;
   }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mname,maddress,mnumber,mgroup;
        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.call);
            mname = itemView.findViewById(R.id.examplename);
            maddress = itemView.findViewById(R.id.address);
            mnumber = itemView.findViewById(R.id.contactnumber);
            mgroup = itemView.findViewById(R.id.bgroup);
            mnumber.setPaintFlags(mnumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            mnumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onCallClick(position);
                        }
                    }
                }
            });

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onCallClick(position);
                        }
                    }
                }
            });
        }


    }

    public ExampleAdapter(ArrayList<ExampleItem> exampleList){
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item,parent,false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mname.setText(currentItem.getName());
        holder.maddress.setText(currentItem.getAddress());
        holder.mnumber.setText(currentItem.getNumber());
        holder.mgroup.setText(currentItem.getGroup());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
