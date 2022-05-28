package com.example.team17zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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

@Entity(tableName = "node_items")
public class nodeItem {
    @PrimaryKey()
    @NonNull
    public String id;

    @NonNull
    public String kind;

    public String name;
    public String group_id;

    public float lat;
    public float lng;

    @TypeConverters(nodeItemConverter.class)

    public List<String> tags;

    //public String testString;

    nodeItem(@NonNull String id, @NonNull String kind, String name, String group_id, float lat, float lng, List<String> tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.group_id = group_id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "nodeItem{" +
                "id='" + id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", group_id='" + group_id + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", tags=" + tags +
                '}';
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getKind() {
        return name;
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
