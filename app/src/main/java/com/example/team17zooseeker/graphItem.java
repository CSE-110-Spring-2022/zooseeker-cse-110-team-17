package com.example.team17zooseeker;

import android.content.Context;

import androidx.room.Entity;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Entity(tableName = "graph_items")
public class graphItem {
    public Graph<String, IdentifiedWeightedEdge> graph;

    graphItem(Graph<String, IdentifiedWeightedEdge> graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        return "graphItem{" +
                "graph=" + graph +
                '}';
    }

    public static Graph<String, IdentifiedWeightedEdge> loadZooGraphJSON(Context context, String path) throws IOException {
        // Create an empty graph to populate.
        Graph<String, IdentifiedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        // On Android, you would use context.getAssets().open(path) here like in Lab 5.
        InputStream input = context.getAssets().open(path);
        Reader reader = new InputStreamReader(input);

        // And now we just import it!
        importer.importGraph(g, reader);

        return g;
    }

}
