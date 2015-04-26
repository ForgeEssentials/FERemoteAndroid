package com.android305.forgeessentialsremote.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Andres on 12/28/2014.
 */
public class Player implements Serializable {

    private String uuid;
    private String username;
    private float health;
    private float armor;
    private float hunger;
    private float saturation;
    private int dim;
    private float x, y, z;

    public Player() {
    }

    public Player(String uuid, String username, float health, float armor, float hunger, float saturation, int dim, float x, float y, float z) {
        this.uuid = uuid;
        this.username = username;
        this.health = health;
        this.armor = armor;
        this.hunger = hunger;
        this.saturation = saturation;
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Player getPlayerFromSerializedBytes(byte[] server) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(server));
        Player o = (Player) ois.readObject();
        ois.close();
        return o;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getArmor() {
        return armor;
    }

    public void setArmor(float armor) {
        this.armor = armor;
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = hunger;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player1 = (Player) o;
        if (!username.equals(player1.username)) return false;
        if (!uuid.equals(player1.uuid)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + username.hashCode();
        return result;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        return baos.toByteArray();
    }
}
