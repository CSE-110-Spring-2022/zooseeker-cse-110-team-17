package com.example.team17zooseeker;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class nodeItemConverter {

    @TypeConverter
    public List<String> storedStringToNodeItems(String value) {
        List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
        return items;
    }

    @TypeConverter
    public String nodeItemsToStoredString(List<String> items) {
        String value = "";

        for (String lang :items)
            value += lang + ",";

        return value;
    }

}
