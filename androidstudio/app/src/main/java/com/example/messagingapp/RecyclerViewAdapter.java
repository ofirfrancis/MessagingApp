package com.example.messagingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String[][] data;
    private LayoutInflater inflater;
    private ItemClickListener  clickListener;
    private Context context;

    public RecyclerViewAdapter(Context context, String[][] data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;

    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View view = inflater.inflate(R.layout.main_menu_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        holder.nameTextView.setText(data[position][1] + data[position][2]);
    }

    @Override
    public int getItemCount(){
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nameTextView;


        ViewHolder(View itemView){
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(clickListener != null){
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public String[] getItem(int id){
        return data[id];
    }


    public void  setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
