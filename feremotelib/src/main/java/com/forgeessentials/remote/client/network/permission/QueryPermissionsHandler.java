package com.forgeessentials.remote.client.network.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.remote.client.data.AreaBase;
import com.forgeessentials.remote.client.data.AreaShape;
import com.forgeessentials.remote.client.data.PermissionList;
import com.forgeessentials.remote.client.data.UserIdent;

public final class QueryPermissionsHandler {

    public static final String ID = "query_permissions";

    public static class Zone {

        public int id;

        public Map<UserIdent, PermissionList> playerPermissions = new HashMap<UserIdent, PermissionList>();

        public Map<String, PermissionList> groupPermissions = new HashMap<String, PermissionList>();

    }

    public static class ServerZone extends Zone {

        public Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

        public Map<UserIdent, Set<String>> playerGroups = new HashMap<UserIdent, Set<String>>();

        @Override
        public String toString()
        {
            return "Server zone";
        }
    }

    public static class WorldZone extends Zone {

        public int dimensionID;

        public List<AreaZone> areaZones = new ArrayList<AreaZone>();

        @Override
        public String toString()
        {
            return "Dimension " + dimensionID;
        }
    }

    public static class AreaZone extends Zone {

        public String name;

        public AreaBase area;

        public AreaShape shape = AreaShape.BOX;

        public int priority;

        @Override
        public String toString()
        {
            return name;
        }
    }

    public static class Response extends ServerZone {
        /* dummy class */
    }

}