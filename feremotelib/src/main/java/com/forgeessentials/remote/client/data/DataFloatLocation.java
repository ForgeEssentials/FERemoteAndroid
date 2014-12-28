package com.forgeessentials.remote.client.data;

import java.util.Locale;


public class DataFloatLocation {

    public int dim;

    public double x;

    public double y;

    public double z;

    public DataFloatLocation(int dim, double x, double y, double z)
    {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString()
    {
        return String.format(Locale.US, "{dim=%d, x=%.1f, y=%.1f, z=%.1f}", dim, x, y, z);
    }

}
