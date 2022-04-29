package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ZooKeeperDatabaseTest {
    private EdgeItemDao edgeDao;
    private GraphItemDao graphDao;
    private NodeItemDao nodeDao;
    private ZooKeeperDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        edgeDao = db.edgeItemDao();
//        graphDao = db.graphItemDao();
        nodeDao = db.nodeItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testGetNode() {
        List<String> tags = new ArrayList<String>();
        tags.add("gorilla");
        tags.add("monkey");
        tags.add("ape");
        tags.add("mammal");

        nodeItem insertedItem = new nodeItem("gorillas", "exhibit", "Gorillas", tags);
        //nodeItem insertedItem2 = new nodeItem("go", "exhibit", "NotGorillas", tags);

        nodeDao.insert(insertedItem);
        nodeItem item = nodeDao.get("gorillas");

        assertEquals(insertedItem.id, item.id);
        assertEquals(insertedItem.kind, item.kind);
        assertEquals(insertedItem.name, item.name);
        assertEquals(insertedItem.tags, item.tags);
    }
}