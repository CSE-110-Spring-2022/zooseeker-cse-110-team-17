package com.example.team17zooseeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class NodeListAdapter extends RecyclerView.Adapter<NodeListAdapter.ViewHolder>{
    private List<nodeItem> nodeItems = Collections.emptyList();

    public void setNodeItems(List<nodeItem> nodeItems) {
        this.nodeItems = nodeItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.node_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setNodeItem(nodeItems.get(position));
    }

    @Override
    public int getItemCount() {
        return nodeItems.size();
    }

    public String getItemID(int position) {
        return nodeItems.get(position).id;
    }

    public String getItemName(int position) {
        return nodeItems.get(position).name;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private nodeItem node;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.node_item_text);
        }

        public nodeItem getNodeItem () {
            return node;
        }

        public void setNodeItem(nodeItem node) {
            this.node = node;
            this.textView.setText(node.name);
        }

    }

}
