package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.Map;

@Dao
public interface EdgeItemDao {
    @Insert
    long insert(edgeItem EdgeItem);

    @Insert
    List<Long> insertAll(List<edgeItem> edgeMap);

    @Query("SELECT * FROM `edge_items`")
    List<edgeItem> getAll();

    @Query("SELECT * FROM `edge_items` WHERE `id`=:id")
    edgeItem get(String id);

//    @Query(SELECT * FROM `edge_items` ORDER BY ``)
//    List<edgeItem> getAll();
}
