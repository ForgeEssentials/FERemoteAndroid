package com.forgeessentials.remote.client.data;

public final class PushChatHandler {
    
    public static final String ID = "push_chat";

    public static class Request {

        public boolean enable;

        public Request(boolean enable)
        {
            this.enable = enable;
        }
    }

    public static class Response {

        public String username;

        public String message;

        public Response(String username, String message)
        {
            this.username = username;
            this.message = message;
        }
    }

}