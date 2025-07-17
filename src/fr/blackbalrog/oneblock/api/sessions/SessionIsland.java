package fr.blackbalrog.oneblock.api.sessions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionIsland
{
	private UUID ownerUuid;
	private String islandName;
	private Location locationCenter;
	private Location homeLocation;
	private Location locationBlockRegen;
	private int level;
	private Vector islandSize;
	private List<UUID> listMembersIsland = new ArrayList<>();
	
	public void setOwner(UUID ownerUuid)
	{
		this.ownerUuid = ownerUuid;
	}
	
	public UUID getOwnerUuid()
	{
		return this.ownerUuid;
	}
	
	public void setIslandName(String islandName)
	{
		this.islandName = islandName;
	}
	
	public String getIslandName()
	{
		return this.islandName;
	}
	
	public void setCenterLocation(World world, double x, double y, double z, float yaw, float pitch)
	{
		this.locationCenter = new Location(world, x, y, z, yaw, pitch);
	}
	
	public Location getCenterLocation()
	{
		return this.locationCenter;
	}
	
	
	
	public void setLocationBlockRegen(Location loc)
	{
		this.locationBlockRegen = loc;
	}
	
	public Location getLocationBlockRegen()
	{
		return this.locationBlockRegen;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public void setIslandSize(int size)
	{
		this.islandSize = new Vector(size/2, 0, size/2);
	}
	
	public Vector getIslandSize()
	{
		return this.islandSize;
	}
	
	public void setMembersIsland(List<UUID> list)
	{
		this.listMembersIsland = list != null ? list : new ArrayList<>();
	}
	
	public List<UUID> getMembersIsland()
	{
		return this.listMembersIsland;
	}
	
	public void setHomeLocation(Location homeLocation)
	{
		this.homeLocation = homeLocation;
	}
	
	public Location getHomeLocation()
	{
		return this.homeLocation;
	}
}
