package com.example.team17zooseeker;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class nodeItem {
    public String id;
    public String kind;
    public String name;
    public List<String> tags;

    nodeItem(String id, String kind, String name, List<String> tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "nodeItem{" +
                "id='" + id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                '}';
    }

    public static Map<String, nodeItem> loadNodeInfoJSON(Context context, String path) throws IOException {
        InputStream input = context.getAssets().open(path);
        Reader reader = new InputStreamReader(input);

        Gson gson = new Gson();
        Type type = new TypeToken<List<nodeItem>>(){}.getType();
        List<nodeItem> zooData = gson.fromJson(reader, type);

        // This code is equivalent to:
        //
        // Map<String, ZooData.NodeInfo> indexedZooData = new HashMap();
        // for (ZooData.NodeInfo datum : zooData) {
        //   indexedZooData[datum.id] = datum;
        // }
        //
        Map<String, nodeItem> indexedZooData = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));
        return indexedZooData;
    }
}
