package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GraphItemDao {
    @Insert
    long insert(graphItem GraphItem);

//    @Query("SELECT * FROM `graph_items` INNER JOIN  nodes ON nodes.id = node_id INNER JOIN edges.id = edge_id WHERE ")
    graphItem get(String id);
}
