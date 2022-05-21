package com.example.team17zooseeker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private NodeItemDao nodeDao;
    private ZooKeeperDatabase database;

    private List<nodeItem> allNodes;

    public AutoCompleteAdapter(@NonNull Context ctx){
        super(ctx, 0);
        this.database = ZooKeeperDatabase.getSingleton(ctx);
        this.nodeDao = database.nodeItemDao();
        this.allNodes = this.nodeDao.getAll();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get title from node item tags
        String title = getItem(position);

        // Converting view to suggestion item.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item, parent, false);
        }

        TextView suggestionName = (TextView) convertView.findViewById(R.id.suggestion_item_name);
        suggestionName.setText(title);

        // Return the completed view to render on screen
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nodeFilter;
    }

    private Filter nodeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            for (nodeItem item : allNodes) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                if(Objects.equals(item.kind, "exhibit")){
                    if(item.name.toLowerCase().contains(filterPattern)){
                        suggestions.add(item.name);
                    }
                    for(String tag: item.tags){
                        if(tag.toLowerCase().contains(filterPattern)){
                            suggestions.add(tag);
                        }
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            clear();
            if(results.values != null){
                //To not allow adding of duplicates
                HashSet<String> resultsSet = new HashSet<String>();

                //Loop through all results including tags triggered
                for(String resultValue : (List<String>) results.values){
                    //For every node compare the results to the item and its tags
                    for(nodeItem item: allNodes){
                        if(resultValue.equals(item.name)){
                            resultsSet.add(resultValue);
                            continue;
                        }
                        //If not a name value check tags
                        for(String tag: item.tags){
                            if(resultValue.equals(tag)){
                                resultsSet.add(item.name);
                                break;
                            }
                        }
                    }
                }
                addAll(resultsSet);
            }
            notifyDataSetChanged();
        }
    };
}