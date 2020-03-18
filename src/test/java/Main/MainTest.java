package Main;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Main.Main.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    public void getGraphSegmentListTest(String graphPath) {
        List<String> graphSegments = new ArrayList<>();
        graphSegments.add("Movies");
        graphSegments.add("Entertainment");
        graphSegments.add("Sports");
        graphSegments.add("Kids");
        graphSegments.add("Downloads");

        assertEquals(getGraphSegmentsList(graphPath), graphSegments);
    }

    @Test
    public void getGraphRolesListTest() {
        List<String> graphRoles = new ArrayList<>();
        graphRoles.add("StreamCalle13");
        graphRoles.add("ABC_TVBOXSETS");
        graphRoles.add("StreamAxn");
        graphRoles.add("StreamAxnwhite");
        graphRoles.add("babytv_CATCHUP");
        graphRoles.add("ABC_CATCHUP");
        graphRoles.add("babytv_TVBOXSETS");
        graphRoles.add("DOWNLOADS_ALLOWED");
        graphRoles.add("axnwhite_CATCHUP");
        graphRoles.add("axn_CATCHUP");
        graphRoles.add("axnwhite_TVBOXSETS");
        graphRoles.add("axn_TVBOXSETS");
        graphRoles.add("PLAYREADY_DEVELOPER");
        graphRoles.add("GLOBAL_ACCESS_ALLOWED");

        assertEquals(getGraphRolesList("/Users/csm40/workspace/Tinkerpop/src/main/resources/test.graphml"), graphRoles);
    }

    @Test
    public void getUnmappedRolesTest() throws IOException {
        List<String> actualList = getUnMappedRolesList("GB","/Users/csm40/workspace/Tinkerpop/src/main/resources/test.graphml");
        List<String> expectedList = new ArrayList();
        expectedList.add("streamcalle13");
//        expectedList.add("abc_tvboxsets");
        expectedList.add("streamaxn");
        expectedList.add("streamaxnwhite");
//        expectedList.add("babytv_catchup");
//        expectedList.add("abc_catchup");
//        expectedList.add("babytv_tvboxsets");
//        expectedList.add("downloads_allowed");
        expectedList.add("axnwhite_catchup");
        expectedList.add("axn_catchup");
        expectedList.add("axnwhite_tvboxsets");
        expectedList.add("axn_tvboxsets");
//        expectedList.add("playready_developer");
//        expectedList.add("global_access_allowed");

        assertEquals(expectedList, actualList);
    }

    @Test
    public void generateRolesScript() throws IOException {
        scriptWriter(getUnMappedRolesList("GB","/Users/csm40/workspace/Tinkerpop/src/main/resources/test.graphml"),getCurrentMaxRoleId("GB"),"GB","/Users/csm40/workspace/Tinkerpop/src/main/resources/test.graphml");

        String expectedDataResult = "";
        Scanner scanner = new Scanner(new File("/Users/csm40/workspace/Tinkerpop/src/test/java/Main/testRolesData.txt"));
        while (scanner.hasNext()) {
            expectedDataResult += scanner.nextLine() + "\n";
        }
        String actualScriptData = "";
        Scanner scanner1 = new Scanner(new File("/Users/csm40/workspace/Tinkerpop/rolesScript"));
        while (scanner1.hasNext()) {
            actualScriptData += scanner1.nextLine() + "\n";
        }

        assertEquals(expectedDataResult, actualScriptData);
    }

    @Test
    public void getCurrentMaxRoleIdTest() throws IOException {
        assertEquals(515, getCurrentMaxRoleId("GB"));
        assertEquals(67, getCurrentMaxRoleId("DE"));
        assertEquals(232, getCurrentMaxRoleId("IT"));
    }

    @Test
    public void getUmvRolesTest() throws IOException {
        RolesResponse rolesResponse = getUmvRoles();
        System.out.println(rolesResponse.getRolesMap().get("GB"));

        System.out.println(getCurrentMaxRoleId("GB"));
    }

    @Test
    public void test() throws IOException {
        URL url = new URL("http://umv-int.dev.cosmic.sky/umv-service/private/roles-list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Basic VVNFUk5BTUU6cGFzc3dvcmQ=");

        ObjectMapper mapper = new ObjectMapper();
        RolesResponse rolesResponse = mapper.readValue(connection.getInputStream(), RolesResponse.class);
        System.out.println(rolesResponse.getRolesMap().get("GB"));

    }
}