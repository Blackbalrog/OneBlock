package fr.blackbalrog.oneblock.api.sessions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionIsland
{
	private UUID ownerUuid;
	private String islandName;
	private Location locationSpawnIsland;
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
	
	public Player getOwner()
	{
		return this.ownerUuid != null ? Bukkit.getPlayer(this.ownerUuid) : null;
	}
	
	public void setIslandName(String islandName)
	{
		this.islandName = islandName;
	}
	
	public String getIslandName()
	{
		return this.islandName;
	}
	
	public void setSpawnLocation(Location loc)
	{
		this.locationSpawnIsland = loc;
	}
	
	public void setSpawnLocation(World world, double x, double y, double z, float yaw, float pitch)
	{
		this.locationSpawnIsland = new Location(world, x, y, z, yaw, pitch);
	}
	
	public Location getLocationSpawnIsland()
	{
		return this.locationSpawnIsland;
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
		return islandSize;
	}
	
	public void setMembersIsland(List<UUID> list)
	{
		this.listMembersIsland = list != null ? list : new ArrayList<>();
	}
	
	public List<UUID> getMembersIsland()
	{
		return listMembersIsland;
	}
}
