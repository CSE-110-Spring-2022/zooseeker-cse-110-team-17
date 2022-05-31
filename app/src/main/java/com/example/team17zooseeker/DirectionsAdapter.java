package com.example.team17zooseeker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.ViewHolder> {
    private List<String> directItems = Collections.emptyList();
    private Directions directions;

    private Context currContext = null;

    private Button prev;
    private Button skip;
    private Button next;

    public DirectionsAdapter(Directions directions, Button prev, Button skip, Button next) {
        this.directions = directions;
        this.prev = prev;
        this.next = next;
        this.skip = skip;
    }

    public void setDirectItems(Context context, boolean skipNext){

        if(skipNext){ this.directions.skipDirections(); }

        this.directItems.clear();
        this.directItems = this.directions.createTestDirections(context);

        //If you are already at the exhibit
        if(this.directItems.size() == 0){
            this.directItems.add("You Have Arrived at Your Destination! :D");
        }

        currContext = context;

        configureButtons();
        updateDirectionsSourceTargetViews();
        notifyDataSetChanged();
    }

    public void itineraryUpdated(){
        this.directItems.clear();
        this.directItems = this.directions.createTestDirections(currContext);

        //If you are already at the exhibit
        if(this.directItems.size() == 0){
            this.directItems.add("You Have Arrived at Your Destination! :D");
        }

        configureButtons();
        updateDirectionsSourceTargetViews();
        notifyDataSetChanged();
    }

    public void configureButtons(){
        int index = Directions.getCurrentIndex();
        int size = this.directions.getItinerarySize();

        //Handle next/skip button
        if (index == size - 1) {
            next.setText("FINISH");
            skip.setEnabled(false);
        } else {
            next.setText("NEXT");
            skip.setEnabled(true);
        }

        //Handle prev button
        if (index == 0) {
            prev.setEnabled(false);
        } else {
            prev.setEnabled(true);
        }
    }

    private void updateDirectionsSourceTargetViews() {

        TextView fromTxt = ((DirectionsActivity)currContext).findViewById(R.id.from_text);
        TextView toTxt = ((DirectionsActivity)currContext).findViewById(R.id.to_text);

        String from = Itinerary.getNameFromId(DynamicDirections.getSingleDyno(currContext, (DirectionsActivity)currContext).getClosestLocationID());
        fromTxt.setText("From: " + from);

        String toID = Itinerary.getItinerary().get(Directions.getCurrentIndex());
        String to = Itinerary.getNameFromId(toID);
        ArrayList<String> groupList = Itinerary.getAnimalsVisited(toID);

        //If it isn't a group
        if(groupList.size() != 0){
            StringBuilder strBld = new StringBuilder();
            strBld.append(to + " to see ");
            for(String id : groupList){
                strBld.append(Itinerary.getNameFromId(id) + ", ");
            }
            strBld.deleteCharAt(strBld.length() - 1);
            strBld.deleteCharAt(strBld.length() - 1);
            to = strBld.toString();
        }
        toTxt.setText("To: " + to);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.directions_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDirect(directItems.get(position));
    }

    @Override
    public int getItemCount() {
        return directItems.size();
    }

    @Override
    public long getItemId(int position){
        // not sure what to do with this yet
        return directItems.indexOf(directItems.get(position));
    }

    //Available for testing
    public String getItemName(int pos){ return directItems.get(pos); }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private String direct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.directions_item_text);
        }

        public String getDirectionItem() { return direct; }

        public void setDirect(String direct){
            this.direct = direct;
            this.textView.setText(direct);
        }
    }
}
