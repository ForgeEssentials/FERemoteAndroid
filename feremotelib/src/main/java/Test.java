import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;

import com.forgeessentials.remote.client.RemoteClient;
import com.forgeessentials.remote.client.RemoteRequest;
import com.forgeessentials.remote.client.RemoteResponse;
import com.forgeessentials.remote.client.RemoteResponse.JsonRemoteResponse;
import com.forgeessentials.remote.client.RequestAuth;
import com.forgeessentials.remote.client.data.PermissionList;
import com.forgeessentials.remote.client.data.UserIdent;
import com.forgeessentials.remote.client.network.PushChatHandler;
import com.forgeessentials.remote.client.network.permission.QueryPermissionsHandler;
import com.forgeessentials.remote.client.network.permission.QueryPermissionsHandler.AreaZone;
import com.forgeessentials.remote.client.network.permission.QueryPermissionsHandler.WorldZone;
import com.forgeessentials.remote.client.network.permission.QueryPermissionsHandler.Zone;
import com.forgeessentials.remote.client.network.QueryPlayerHandler;
import com.google.gson.JsonElement;

public class Test implements Runnable {

    private RemoteClient client;

    private RequestAuth auth;

    public Test() throws UnknownHostException, IOException, NoSuchAlgorithmException
    {
        // client = RemoteClient.createSslClient("localhost", 27020);
        client = new RemoteClient("localhost", 27020);
        new Thread(this).start();
        auth = new RequestAuth("ForgeDevName", "nChhHK");
        //auth = new RequestAuth("a129ebab-93ca-3ac8-86a9-47cce1be86d0", "pmZGpN");
    }

    @Override
    public void run()
    {
        while (!client.isClosed())
        {
            JsonRemoteResponse response = client.getNextResponse(0);
            if (response != null)
            {
                if (response.id == null)
                    handleUnknownMessage(response);
                else
                {
                    switch (response.id)
                    {
                    case PushChatHandler.ID:
                    {
                        RemoteResponse<PushChatHandler.Response> r = client.transformResponse(response, PushChatHandler.Response.class);
                        System.out.println(String.format("Chat (%s): %s", r.data.username, r.data.message));
                        break;
                    }
                    case "shutdown":
                    {
                        System.out.println("Server shutdown");
                        client.close();
                        break;
                    }
                    default:
                        handleUnknownMessage(response);
                        break;
                    }
                }
            }
        }
    }

    public void handleUnknownMessage(JsonRemoteResponse response)
    {
        if (response.id == null)
            response.id = "";
        if (response.success)
        {
            if (response.message == null)
                response.message = "success";
            if (response.data == null)
                System.out.println(String.format("EAT Response %s:#%d (%s)", response.id, response.rid, response.message));
            else
                System.out.println(String.format("EAT Response %s:#%d (%s): %s", response.id, response.rid, response.message, response.data.toString()));
        }
        else
        {
            if (response.message == null)
                response.message = "failure";
            if (response.data == null)
                System.out.println(String.format("EAT Response %s:#%d (%s)", response.id, response.rid, response.message));
            else
                System.out.println(String.format("EAT Response %s:#%d (%s): %s", response.id, response.rid, response.message, response.data.toString()));
        }
    }

    public void queryPlayer()
    {
        RemoteResponse<QueryPlayerHandler.Response> response = client.sendRequestAndWait(new RemoteRequest<>(QueryPlayerHandler.ID, auth,
                new QueryPlayerHandler.Request("ForgeDevName", "location", "detail")), QueryPlayerHandler.Response.class, 60 * 1000);
        if (response == null || !response.success)
        {
            System.err.println("Error: " + (response == null ? "no response" : response.message));
        }
        else
        {
            System.out.println("Response:");
            System.out.println("Username = " + response.data.players.get(0).name);
            System.out.println("UUID     = " + response.data.players.get(0).uuid);
            for (Entry<String, JsonElement> data : response.data.players.get(0).data.entrySet())
            {
                System.out.println("> " + data.getKey() + ": " + data.getValue().toString());
            }
        }
    }
    
    private void printPermissions(Zone z)
    {
        System.out.println("zone " + z.id);
        for (Entry<UserIdent, PermissionList> perms : z.playerPermissions.entrySet())
        {
            System.out.println("  player " + perms.getKey().username);
            for (Entry<String, String> perm : perms.getValue().entrySet())
                System.out.println("    " + perm.getKey() + " = " + perm.getValue());
        }
        for (Entry<String, PermissionList> perms : z.groupPermissions.entrySet())
        {
            System.out.println("  group " + perms.getKey());
            for (Entry<String, String> perm : perms.getValue().entrySet())
                System.out.println("    " + perm.getKey() + " = " + perm.getValue());
        }
    }

    public void queryPermissions()
    {
        RemoteResponse<QueryPermissionsHandler.Response> response = client.sendRequestAndWait(new RemoteRequest<>(QueryPermissionsHandler.ID, auth, null),
                QueryPermissionsHandler.Response.class, 60 * 1000);
        if (response == null || !response.success)
        {
            System.err.println("Error: " + (response == null ? "no response" : response.message));
        }
        else
        {
            printPermissions(response.data);
            for (WorldZone wz : response.data.worldZones.values())
            {
                printPermissions(wz);
                for (AreaZone az : wz.areaZones)
                    printPermissions(az);
            }
        }
    }

    public void pushChat(boolean enable)
    {
        RemoteResponse<?> response = client.sendRequestAndWait(new RemoteRequest<>(PushChatHandler.ID, auth, new PushChatHandler.Request(enable)),
                PushChatHandler.Response.class, 60 * 1000);
        if (response == null)
        {
            System.err.println("No response");
        }
        else
        {
            if (response.success)
                System.out.println(enable ? "Enabled chat monitoring" : "Disabled chat monitoring");
            else
                System.err.println(response.message);
        }
    }

    public void close()
    {
        client.close();
    }

    public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException
    {
        Test main = new Test();
        if (main.client.isClosed())
            return;
        main.queryPlayer();
        main.queryPermissions();
        // main.pushChat(true);
        main.close();
    }

}
