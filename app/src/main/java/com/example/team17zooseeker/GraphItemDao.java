package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GraphItemDao {
    @Insert
    long insert(edgeItem EdgeItem);

    @Query("SELECT * FROM `graph_items`")
    graphItem get(String id);
}
