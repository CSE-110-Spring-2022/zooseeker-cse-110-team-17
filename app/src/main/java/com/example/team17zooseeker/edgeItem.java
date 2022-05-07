package com.example.team17zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

@Entity(tableName = "edge_items")
public class edgeItem {
    @PrimaryKey()
    @NonNull
    public String id;

    @NonNull
    public String street;

    edgeItem(String id, @NonNull String street) {
        this.id = id;
        this.street = street;
    }

    @Override
    public String toString() {
        return "edgeItem{" +
                "id='" + id + '\'' +
                ", street='" + street + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public static Map<String, edgeItem> loadEdgeInfoJSON(Context context, String path) throws IOException {
        InputStream input = context.getAssets().open(path);
        Reader reader = new InputStreamReader(input);

        Gson gson = new Gson();
        Type type = new TypeToken<List<edgeItem>>(){}.getType();
        List<edgeItem> zooData = gson.fromJson(reader, type);

        Map<String, edgeItem> indexedZooData = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        return indexedZooData;
    }
}
