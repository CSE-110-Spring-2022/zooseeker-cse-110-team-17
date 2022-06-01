package com.example.team17zooseeker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItineraryItemAdapter extends RecyclerView.Adapter<ItineraryItemAdapter.ViewHolder>{
    int totalDistance = 0;

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

        //Don't display the final entrance gate
        if(position >= Itinerary.getItinerary().size() - 1){ return; };

        //So the distance under each location is the distance to that location
        String currLoc = Itinerary.getItinerary().get(position);
        if(position == 0){
            totalDistance += Itinerary.distance(Itinerary.getCurrLocation() , currLoc);
        } else {
            String prevLoc = Itinerary.getItinerary().get(position - 1);
            totalDistance += Itinerary.distance(prevLoc , currLoc);
        }

        //Set displayed name to node name and not Id
        currLoc = Itinerary.getNameFromId(currLoc);

        String text = currLoc + "\n(" + totalDistance + " feet)";
        holder.setText(text);
    }

    @Override
    public int getItemCount() {
        if(Itinerary.getItinerary() != null)
            return Itinerary.getItinerary().size();
        else
            return 0;
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
