package com.example.team17zooseeker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import org.jgrapht.Graph;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ZooKeeperDatabase db;
    private NodeItemDao nodeDao;
    private EdgeItemDao edgeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Context context = this;
        ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(context);

        nodeDao = database.nodeItemDao();
        edgeDao  = database.edgeItemDao();

        List<nodeItem> nodes = nodeDao.getAll();
        List<edgeItem> edges = edgeDao.getAll();

        Map<String, nodeItem> nodeMap = nodes.stream().collect(Collectors.toMap(nodeItem::getId, Function.identity()));
        Map<String, edgeItem> edgeMap = edges.stream().collect(Collectors.toMap(edgeItem::getId, Function.identity()));

        Button plan = findViewById(R.id.plan_btn);
        EditText searchBar = findViewById(R.id.search_text);

        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }
}