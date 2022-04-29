package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface GraphItemDao {
    @Query("SELECT * FROM `graph_items`")
    graphItem get(String id);
}
