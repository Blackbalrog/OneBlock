package fr.blackbalrog.oneblock.api.sessions;

import org.bukkit.Location;
import org.bukkit.World;

public class SessionPlayer
{

	private String islandName;
	private Location LocationSpawnIsland;
	private boolean access = false;
	
	public void setIslandName(String islandName)
	{
		this.islandName = islandName;
	}

	public String getIslandName()
	{
		return this.islandName;
	}
	
	public void setLocationSpawnIsland(World world, double x, double y, double z, float yaw, float pitch)
	{
		this.LocationSpawnIsland = new Location(world, x, y, z, yaw, pitch);
	}
	
	public Location getLocationSpawnIsland()
	{
		return this.LocationSpawnIsland;
	}
	
	public void setAccess(boolean access)
	{
		this.access = access;
	}
	
	public boolean isAccess()
	{
		return this.access;
	}
}
