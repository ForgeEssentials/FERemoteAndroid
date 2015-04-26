package com.forgeessentials.remote.client.data;

public class WorldArea extends AreaBase
{
	
    protected int dim;

    public WorldArea(int dim, Point start, Point end)
    {
        super(start, end);
        this.dim = dim;
    }

    public WorldArea(int dim, AreaBase area)
    {
        super(area.getHighPoint(), area.getLowPoint());
        this.dim = dim;
    }

    public int getDimension()
	{
		return dim;
	}

    public void setDimension(int dimensionId)
    {
        this.dim = dimensionId;
    }

	public boolean contains(WorldPoint point)
    {
        if (point.dim == dim)
        {
            return super.contains(point);
        }
        else
        {
            return false;
        }
    }

    public boolean contains(WorldArea area)
    {
        if (area.dim == dim)
        {
            return super.contains(area);
        }
        else
        {
            return false;
        }
    }

    public boolean intersectsWith(WorldArea area)
    {
        if (area.dim == dim)
        {
            return super.intersectsWith(area);
        }
        else
        {
            return false;
        }
    }

    public AreaBase getIntersection(WorldArea area)
    {
        if (area.dim == dim)
        {
            return super.getIntersection(area);
        }
        else
        {
            return null;
        }
    }

    public boolean makesCuboidWith(WorldArea area)
    {
        if (area.dim == dim)
        {
            return super.makesCuboidWith(area);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return " { " + dim + " , " + getHighPoint().toString() + " , " + getLowPoint().toString() + " }";
    }

}
