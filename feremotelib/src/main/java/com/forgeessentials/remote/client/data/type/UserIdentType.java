package com.forgeessentials.remote.client.data.type;

import java.lang.reflect.Type;
import java.util.UUID;

import com.forgeessentials.remote.client.RemoteClient.DataType;
import com.forgeessentials.remote.client.data.UserIdent;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class UserIdentType implements DataType<UserIdent> {

    @Override
    public JsonElement serialize(UserIdent src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public UserIdent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        if (json.isJsonObject())
        {
            JsonObject obj = json.getAsJsonObject();
            JsonElement uuid = obj.get("uuid");
            JsonElement username = obj.get("username");
            return new UserIdent(uuid == null ? null : UUID.fromString(uuid.getAsString()), username == null ? null : username.getAsString());
        }
        return UserIdent.fromString(json.getAsString());
    }

    @Override
    public Class<UserIdent> getType()
    {
        return UserIdent.class;
    }

}
