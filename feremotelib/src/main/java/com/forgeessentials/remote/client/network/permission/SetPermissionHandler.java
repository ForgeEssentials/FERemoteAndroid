package com.forgeessentials.remote.client.network.permission;

import com.forgeessentials.remote.client.data.UserIdent;

public final class SetPermissionHandler {

    public static final String ID = "set_permission";

    public static class Request {

        public int zoneId;

        public UserIdent user;

        public String group;

        public String permission;

        public String value;

        public Request(int zoneId, UserIdent user, String permission, String value)
        {
            this.zoneId = zoneId;
            this.user = user;
            this.group = null;
            this.permission = permission;
            this.value = value;
        }

        public Request(int zoneId, String group, String permission, String value)
        {
            this.zoneId = zoneId;
            this.user = null;
            this.group = group;
            this.permission = permission;
            this.value = value;
        }
    }

}
