package GraphMaker;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GraphMakerMain {
    public static void main(String[] args) throws IOException {

        Graph graph = new TinkerGraph();
        Vertex vertex = graph.addVertex(null);
        Vertex vertex2 = graph.addVertex(null);
        vertex2.setProperty("name","someRoleName2");
        vertex2.addEdge("1",vertex);

        vertex.setProperty("name","someRoleName");
        vertex.setProperty("type","ott_role");

        GraphMLWriter writer = new GraphMLWriter(graph);

        writer.outputGraph("/Users/csm40/workspace/Tinkerpop/src/main/resources/graphMakerTest.graphml");
    }


}
