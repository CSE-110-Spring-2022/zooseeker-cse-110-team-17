package com.example.team17zooseeker;

import android.content.Context;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class graphItem {
    public Graph<String, identifiedWeightedEdge> graph;

    graphItem(Graph<String, identifiedWeightedEdge> graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        return "graphItem{" +
                "graph=" + graph +
                '}';
    }

    public static Graph<String, identifiedWeightedEdge> loadZooGraphJSON(Context context, String path) throws IOException {
        // Create an empty graph to populate.
        Graph<String, identifiedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(identifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, identifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(identifiedWeightedEdge::attributeConsumer);

        // On Android, you would use context.getAssets().open(path) here like in Lab 5.
        InputStream input = context.getAssets().open(path);
        Reader reader = new InputStreamReader(input);

        // And now we just import it!
        importer.importGraph(g, reader);

        return g;
    }

}
