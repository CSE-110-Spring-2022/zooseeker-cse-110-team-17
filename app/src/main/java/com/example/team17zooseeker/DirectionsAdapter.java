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


    public void setDirectItems(Context context, boolean forward, boolean skipNext){
        if(skipNext){
            this.directions.skipDirections();
        }
        this.directItems.clear();
        this.directItems = this.directions.createTestDirections(context, forward); //Not using database
        //If you already at the exhibit
        if(this.directItems.size() == 0){
            this.directItems.add("You are already at the exit! :-)");
        }
        int index = Directions.getCurrentIndex();
        int size = this.directions.getItinerarySize();
             if (index == size - 2 && !DirectionsActivity.theLastButtonPressedWasPrevious) {
                next.setText("FINISH");
                skip.setEnabled(false);
             } else {
                next.setText("NEXT");
                skip.setEnabled(true);
             }
             if (size == 2 || index == 0) {
                 prev.setEnabled(false);
                 prev.setClickable(false);
             } else if(index == 1 && DirectionsActivity.theLastButtonPressedWasPrevious){
                 prev.setEnabled(false);
                 prev.setClickable(false);
             } else {
                 prev.setEnabled(true);
                 prev.setClickable(true);
             }
        notifyDataSetChanged();
        currContext = context;
    }

    public void itineraryUpdated(){
        this.directItems = this.directions.createTestDirections(currContext, true);
        int index = Directions.getCurrentIndex();
        int size = this.directions.getItinerarySize();
        if (index == size - 2) {
            next.setText("FINISH");
            skip.setEnabled(false);
        } else {
            next.setText("NEXT");
            skip.setEnabled(true);
        }
        if (size == 2 || index == 0) {
            prev.setEnabled(false);
            prev.setClickable(false);
        } else if(index == 1 && DirectionsActivity.theLastButtonPressedWasPrevious){
            prev.setEnabled(false);
            prev.setClickable(false);
        } else {
            prev.setEnabled(true);
            prev.setClickable(true);
        }
        notifyDataSetChanged();
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
