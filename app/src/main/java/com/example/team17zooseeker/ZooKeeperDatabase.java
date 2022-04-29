package com.example.team17zooseeker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {edgeItem.class, nodeItem.class}, version = 1, exportSchema = false)
public abstract class ZooKeeperDatabase extends RoomDatabase {
    public abstract EdgeItemDao edgeItemDao();
//    public abstract GraphItemDao graphItemDao();
    public abstract NodeItemDao nodeItemDao();
}
