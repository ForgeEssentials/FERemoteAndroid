package com.forgeessentials.remote.client.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;

public final class QueryPlayerHandler {
    
    public static final String ID = "query_player";

    public static class Request {

        public String name;

        public Set<String> flags = new HashSet<>();

        public Request(String name, String... flags)
        {
            this.name = name;
            for (int i = 0; i < flags.length; i++)
            {
                this.flags.add(flags[i]);
            }
        }

        public Request(String name, Collection<String> flags)
        {
            this.name = name;
            this.flags.addAll(flags);
        }

    }

    public static class Response {

        public List<PlayerInfoResponse> players = new ArrayList<>();

    }

    public static class PlayerInfoResponse {

        public String uuid;

        public String name;

        public Map<String, JsonElement> data = new HashMap<>();

        public PlayerInfoResponse(String uuid, String username)
        {
            this.uuid = uuid;
            this.name = username;
        }

    }

}
