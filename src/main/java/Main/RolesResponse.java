package Main;

import java.util.List;
import java.util.Map;

public class RolesResponse {

    public Map<String, List<Role>> getRolesMap() {
        return rolesMap;
    }

    public void setRolesMap(Map<String, List<Role>> rolesMap) {
        this.rolesMap = rolesMap;
    }

    private Map<String, List<Role>> rolesMap;
 }


