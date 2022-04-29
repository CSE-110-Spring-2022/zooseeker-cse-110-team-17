package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface NodeItemDao {
    @Insert
    String insert(nodeItem NodeItem);

    @Query("SELECT * FROM `node_items` WHERE `id` = :id")
    nodeItem get(String id);
}
