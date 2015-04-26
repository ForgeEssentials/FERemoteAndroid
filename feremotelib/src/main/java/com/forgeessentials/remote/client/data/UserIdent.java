package com.forgeessentials.remote.client.data;

import java.util.UUID;

public class UserIdent {

    public UUID uuid;

    public String username;

    public UserIdent(UUID uuid, String username)
    {
        this.uuid = uuid;
        this.username = username;
    }

    public static UserIdent fromString(String string)
    {
        if (string.charAt(0) != '(' || string.charAt(string.length() - 1) != ')' || string.indexOf('|') < 0)
            throw new IllegalArgumentException("UserIdent string needs to be in the format \"(<uuid>|<username>)\"");
        String[] parts = string.substring(1, string.length() - 1).split("\\|", 2);
        return new UserIdent(UUID.fromString(parts[0]), parts[1]);
    }

    @Override
    public String toString()
    {
        return username == null ? uuid.toString() : username;
    }
    
}