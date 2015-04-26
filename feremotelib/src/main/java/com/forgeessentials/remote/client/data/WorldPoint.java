package com.forgeessentials.remote.client.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Point which stores dimension as well
 */
public class WorldPoint extends Point {

    protected int dim;
    
    // ------------------------------------------------------------

    public WorldPoint(int dimension, int x, int y, int z)
    {
        super(x, y, z);
        dim = dimension;
    }

    public WorldPoint(WorldPoint other)
    {
        this(other.dim, other.x, other.y, other.z);
    }

    public WorldPoint(WarpPoint other)
    {
        this(other.getDimension(), other.getBlockX(), other.getBlockY(), other.getBlockZ());
    }

    // ------------------------------------------------------------

    public int getDimension()
    {
        return dim;
    }

    public void setDimension(int dim)
    {
        this.dim = dim;
    }

    public WarpPoint toWarpPoint(float rotationPitch, float rotationYaw)
    {
        return new WarpPoint(this, rotationPitch, rotationYaw);
    }

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + x + "," + y + "," + z + ",dim=" + dim + "]";
    }

    private static final Pattern fromStringPattern = Pattern
            .compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*dim\\s*=\\s*(-?\\d+)\\s*\\]\\s*");

    public static WorldPoint fromString(String value)
    {
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WorldPoint(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
            }
            catch (NumberFormatException e)
            {
                /* do nothing */
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof WorldPoint)
        {
            WorldPoint p = (WorldPoint) object;
            return dim == p.dim && x == p.x && y == p.y && z == p.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1 + x;
        h = h * 31 + y;
        h = h * 31 + z;
        h = h * 31 + dim;
        return h;
    }


}
