package com.example.team17zooseeker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StateDao {

    @Insert
    long insert(State state);

    @Query("SELECT * FROM `state_table`")
    State get();

    @Delete
    int delete(State state);

}
