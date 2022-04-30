package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.Map;

@Dao
public interface NodeItemDao {
    @Insert
    long insert(nodeItem NodeItem);

    @Insert
    List<Long> insertAll(List<nodeItem> nodeMap);

    @Query("SELECT * FROM `node_items`")
    List<nodeItem> getAll();

    @Query("SELECT * FROM `node_items` WHERE `id` = :id")
    nodeItem get(String id);
}
