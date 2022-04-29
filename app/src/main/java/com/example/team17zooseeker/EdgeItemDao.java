package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface EdgeItemDao {
    @Insert
    String insert(edgeItem EdgeItem);

    @Query("SELECT * FROM `edge_items` WHERE `id`=:id")
    edgeItem get(String id);

//    @Query(SELECT * FROM `edge_items` ORDER BY ``)
//    List<edgeItem> getAll();
}
