package com.example.team17zooseeker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItineraryItemAdapter extends RecyclerView.Adapter<ItineraryItemAdapter.ViewHolder>{

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.itinerary_item, parent,false);

        return new ViewHolder(view);
    }

    //Setting viewHolder text values to the Itinerary Values
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currLoc = Itinerary.getItinerary().get(position);
        String nextLoc;
        //If the current position is the end map to exit
        if(position + 1 > Itinerary.getItinerary().size() - 1){
            nextLoc = Itinerary.getItinerary().get(0);
        } else {
            nextLoc = Itinerary.getItinerary().get(position + 1);
        }

        String text=currLoc + "\n(" + Itinerary.distance(currLoc ,nextLoc) + " feet)";
        holder.setText(text);
    }

    @Override
    public int getItemCount() {
        return Itinerary.getItinerary().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.itinerary_textItem);
        }

        public void setText(String title) {
            this.textView.setText(title);
        }
    }
}
