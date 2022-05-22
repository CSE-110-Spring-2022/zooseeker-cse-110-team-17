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
//    private GraphItemDao graphDao;
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
    public void testGetNodeSame() {
        List<String> tags = new ArrayList<String>();
        tags.add("gorilla");
        tags.add("monkey");
        tags.add("ape");
        tags.add("mammal");

        nodeItem insertedItem = new nodeItem("gorillas", "exhibit", "Gorillas",null,1,1, tags);
        //nodeItem insertedItem2 = new nodeItem("go", "exhibit", "NotGorillas", tags);

        nodeDao.insert(insertedItem);
        nodeItem item = nodeDao.get("gorillas");

        assertEquals(insertedItem.id, item.id);
        assertEquals(insertedItem.kind, item.kind);
        assertEquals(insertedItem.name, item.name);
        assertEquals(insertedItem.tags, item.tags);
    }

    @Test
    public void testGetNodeNotSame() {
        List<String> tags = new ArrayList<String>();
        tags.add("gorilla");
        tags.add("monkey");
        tags.add("ape");
        tags.add("mammal");

        List<String> nottags = new ArrayList<String>();
        tags.add("notgorilla");
        tags.add("notmonkey");
        tags.add("notape");
        tags.add("notmammal");

        nodeItem insertedItem1 = new nodeItem("gorillas", "exhibit", "Gorillas",null,1,1, tags);
        nodeItem insertedItem2 = new nodeItem("notgorillas", "notexhibit", "NotGorillas", null, 0,0, nottags);

        nodeDao.insert(insertedItem1);
        nodeDao.insert(insertedItem2);
        nodeItem item1 = nodeDao.get("gorillas");
        nodeItem item2 = nodeDao.get("notgorillas");

        assertNotEquals(item1.id, item2.id);
        assertNotEquals(item1.kind, item2.kind);
        assertNotEquals(item1.name, item2.name);
        assertNotEquals(item1.tags, item2.tags);
    }

    @Test
    public void testGetEdgeSame() {
        edgeItem insertedItem = new edgeItem("edge-0", "Entrance Way");

        edgeDao.insert(insertedItem);

        edgeItem item = edgeDao.get("edge-0");

        assertEquals(insertedItem.id, item.id);
        assertEquals(insertedItem.street, item.street);
    }

    @Test
    public void testGetEdgeNotSame() {
        edgeItem insertedItem1 = new edgeItem("edge-0", "Entrance Way");
        edgeItem insertedItem2 = new edgeItem("edge-1", "Exit Way");

        edgeDao.insert(insertedItem1);
        edgeDao.insert(insertedItem2);

        edgeItem item1 = edgeDao.get("edge-0");
        edgeItem item2 = edgeDao.get("edge-1");

        assertNotEquals(item1.id, item2.id);
        assertNotEquals(item1.street, item2.street);
    }
}