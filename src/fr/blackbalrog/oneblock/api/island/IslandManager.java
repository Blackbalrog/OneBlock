package fr.blackbalrog.oneblock.api.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class IslandManager
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private IslandDatabase islandDatabase = OneBlock.getInstance().getDatabasePlayer();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	
	/**
	 * Retourne l'île dont la zone contient la location, ou null
	 * Parcourt d'abord les sessions actives, puis toutes les îles persistées
	 */
	public SessionIsland getIslandAt(Location location)
	{
		if (location == null) return null;
		World world = location.getWorld();
		if (world == null) return null;
		
		// Sessions actives
		for (SessionIsland sessionIsland : this.sessionManager.getSessionIslandMap().values())
		{
			if (this.isInsideIsland(sessionIsland, world, location))
			{
				return sessionIsland;
			}
		}
		
		// îles persistées (offline incluses)
		for (UUID ownerUuid : this.islandDatabase.getAllOwnerUuids())
		{
			this.sessionManager.createSessionIsland(ownerUuid);
			SessionIsland sessionIsland = this.islandDatabase.loadIsland(ownerUuid);
			
			if (sessionIsland == null) continue;
			
			int level = sessionIsland.getLevel();
			int size = this.configuration.getInt("Island.level." + level + ".size");
			sessionIsland.setIslandSize(size);
			
			if (this.isInsideIsland(sessionIsland, world, location))
			{
				return sessionIsland;
			}
		}
		return null;
	}
	
	private boolean isInsideIsland(SessionIsland island, World world, Location loc)
	{
		if (island == null || loc == null) return false;
		
		// Récupère le centre et sa référence au monde
		Location center = island.getCenterLocation();
		if (center == null) return false;
		
		World centerWorld = center.getWorld();
		// GARDE-FOU: si l’un des mondes est null, on sort sans comparer
		if (centerWorld == null || world == null) return false;
		if (!centerWorld.equals(world)) return false;
		
		Vector size = island.getIslandSize();
		if (size == null) return false;
		
		int sx = size.getBlockX();
		int sz = size.getBlockZ();
		int dx = loc.getBlockX() - center.getBlockX();
		int dz = loc.getBlockZ() - center.getBlockZ();
		return dx >= -sx && dx <= sx && dz >= -sz && dz <= sz;
	}
	
	
	/** Compare l'UUID de l'owner dans SessionIsland avec le joueur */
	public boolean isOwner(Player player, SessionIsland sessionIsland)
	{
		return player.getUniqueId().equals(sessionIsland.getOwnerUuid());
	}
}
