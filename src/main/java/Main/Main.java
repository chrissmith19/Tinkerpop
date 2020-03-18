package Main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class Main {
//rename the new graph you put in the resoureces to be test.graphml
//change the territory to be the same as the territory you are using

    public static void main(String[] args) throws IOException {
        String territory = "IT";
        String graphPath= "/Users/csm40/workspace/Tinkerpop/src/main/resources/repackaging graph nowtv it 2.graphml";
        int maxRoleId = getCurrentMaxRoleId(territory);
        scriptWriter(getUnMappedRolesList(territory,graphPath), maxRoleId,territory,graphPath);
    }


    public static List<String> getUnMappedRolesList(String territory, String graphPath) throws IOException {
        List<String> currentGbRolesList = getCurrentGbRolesList(territory);
        return getGraphRolesList(graphPath).stream()
                .filter(r -> !currentGbRolesList.contains(r))
                .collect(Collectors.toList());
    }

    public static Iterable<Vertex> getGraphVertices(String graphPath) {
        try {
//            String graphml = "/Users/csm40/workspace/Tinkerpop/src/main/resources/repackaging graph nowtv it 2.graphml";
            Graph graph = new TinkerGraph();
            GraphMLReader reader = new GraphMLReader(graph);
            InputStream inputStreamgraph = new BufferedInputStream(new FileInputStream(graphPath));
            reader.inputGraph(inputStreamgraph);
            return graph.getVertices();
        } catch (Exception e) {
            System.out.println("cannot find file" + e);
            return null;
        }
    }

    public static List<String> getGraphSegmentsList(String graphPath) {
        Iterable<Vertex> vertices = getGraphVertices(graphPath);

        return StreamSupport.stream(vertices.spliterator(), false)
                .filter(v -> v.getProperty("type").equals("content_segment") || v.getProperty("type").equals("account_segment") || v.getProperty("type").equals("notification_segment"))
                .map(v -> v.getProperty("name").toString())
                .collect(Collectors.toList());
    }

    public static List<String> getGraphRolesList(String graphPath) {
        Iterable<Vertex> vertices = getGraphVertices(graphPath);

        return StreamSupport.stream(vertices.spliterator(), false)
                .filter(v -> v.getProperty("type").equals("ott_role"))
                .map(v -> v.getProperty("name").toString().toLowerCase())
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getCurrentGbRolesList(String territory) throws IOException {
        RolesResponse response = getUmvRoles();
        return response.getRolesMap().get(territory).stream()
                .map(r -> r.roleName)
                .collect(Collectors.toList());
    }

    public static RolesResponse getRoleResponse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File("/Users/csm40/workspace/Tinkerpop/src/main/resources/json.json"), RolesResponse.class);
    }

    public static int getCurrentMaxRoleId(String territory) throws IOException {
        RolesResponse roles = getUmvRoles();
        return roles.getRolesMap().get(territory).get(getCurrentGbRolesList(territory).size() - 1).roleId;
    }

    public static void scriptWriter(List<String> rolesToBeAdded, int currentMaxRoleNumber,String territory,String graphPath) throws IOException {
        String newRolesScript = "";
        for (int i = 0; i < rolesToBeAdded.size(); i++) {
            String role = rolesToBeAdded.get(i);
            newRolesScript += ("INSERT INTO ott_roles (provider_territory, role_id, role_name) VALUES('"+territory+"'," + (currentMaxRoleNumber + i +1) + ",'" + role + "');\n");
        }
        System.out.println(newRolesScript);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("rolesScript"));
        bufferedWriter.write(newRolesScript);
        bufferedWriter.close();
    }

    public static RolesResponse getUmvRoles() throws IOException {
        URL url = new URL("http://umv-int.dev.cosmic.sky/umv-service/private/roles-list");
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization","Basic VVNFUk5BTUU6cGFzc3dvcmQ=");

        ObjectMapper mapper = new ObjectMapper();
        RolesResponse rolesResponse = mapper.readValue(connection.getInputStream(), RolesResponse.class);
        return rolesResponse;
        }


}
