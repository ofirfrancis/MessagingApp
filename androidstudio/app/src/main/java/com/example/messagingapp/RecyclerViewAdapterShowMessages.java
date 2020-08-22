package com.example.messagingapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterShowMessages extends RecyclerView.Adapter<RecyclerViewAdapterShowMessages.ViewHolder> {

    private String[][] messages;
    private LayoutInflater inflater;
    private ItemClickListener  clickListener;
    private Context context;

    public RecyclerViewAdapterShowMessages(Context context, String[][] messages){
        this.inflater = LayoutInflater.from(context);
        this.messages = messages;
        this.context = context;

    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View view = inflater.inflate(R.layout.message_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
//        input text into textview, change rounded corners, change position of image and text, change height of image based in text view, change colour of image
        holder.messageTextView.setText(messages[position][1]);
//        Log.d("message", String.valueOf(messages[position]));
//        Log.d("message", String.valueOf(messages[position][1]));
        float density = holder.itemView.getContext().getResources().getDisplayMetrics().density;
        holder.messageTextView.measure(0,0);
        holder.imageView.measure(0,0);
        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams( holder.imageView.getLayoutParams().width,(int) (holder.messageTextView.getMeasuredHeight()+(20 * density))));
        Log.d("", String.valueOf(density));
        Log.d("", String.valueOf(holder.imageView.getMeasuredHeight()));
        Log.d("", String.valueOf(holder.messageTextView.getMeasuredHeight()));
        GradientDrawable gradientDrawable = (GradientDrawable) holder.imageView.getBackground().getCurrent();
        if (!messages[position][0].equals(MainActivity.phoneString)){
            gradientDrawable.setColor(Color.parseColor("#666666"));
            gradientDrawable.setCornerRadii(new float[] {30*density,30*density,30*density,10*density,30*density,30*density,30*density,10*density});
        }



    }

    @Override
    public int getItemCount(){
        return messages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView messageTextView;
        ImageView imageView;


        ViewHolder(View itemView){
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageText);
            imageView = itemView.findViewById(R.id.messageOval);
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
        return messages[id];
    }


    public void  setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
