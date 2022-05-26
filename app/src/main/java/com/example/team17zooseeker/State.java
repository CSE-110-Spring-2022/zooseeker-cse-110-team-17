package com.example.team17zooseeker;

import android.content.Context;
import android.util.Log;

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

@Entity(tableName = "state_table")
public class State {
    @PrimaryKey()
    @NonNull
    public String state;

    State(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "State{" +
                "state='" + state +
                '}';
    }

    public String getState() {
        return state;
    }

    public static State loadStateInfoJSON(Context context, String path) throws IOException {

        InputStream input = null;

        try {

            input = context.getAssets().open("state.json");
            Reader reader = new InputStreamReader(input);

            Gson gson = new Gson();
            Type type = new TypeToken<State>(){}.getType();
            State data = gson.fromJson(reader, type);

            return data;

        } catch(IOException e) {
            Log.e("IOException: ", e.toString());
            e.printStackTrace();
        }

        return null;
    }

}
