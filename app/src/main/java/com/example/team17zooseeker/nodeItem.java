package com.example.team17zooseeker;

import java.util.List;

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
}
