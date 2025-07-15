package fr.blackbalrog.oneblock.api.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class IslandManager
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private IslandDatabase islandDatabase = OneBlock.getInstance().getDatabasePlayer();
	
	/**
	 * Retourne l'île dont la zone contient la location, ou null.
	 * Parcourt d'abord les sessions actives, puis toutes les îles persistées.
	 */
	public SessionIsland getIslandAt(Location location)
	{
		if (location == null) return null;
		World world = location.getWorld();
		if (world == null) return null;
		
		// 1) sessions actives
		for (SessionIsland sessionIsland : this.sessionManager.getSessionIslandMap().values())
		{
			if (this.isInsideIsland(sessionIsland, world, location))
			{
				return sessionIsland;
			}
		}
		
		// 2) îles persistées (offline incluses)
		for (UUID ownerUuid : this.islandDatabase.getAllOwnerUuids())
		{
			this.islandDatabase.loadSessionIsland(ownerUuid);
			// On ne touche PAS à sessionManager ici, on charge une instance temporaire
			SessionIsland sessionIsland = this.sessionManager.getSessionIsland(ownerUuid);
			if (sessionIsland == null) continue;
			
			if (this.isInsideIsland(sessionIsland, world, location))
			{
				return sessionIsland;
			}
		}
		return null;
	}
	
	private boolean isInsideIsland(SessionIsland island, World world, Location loc) {
		if (island == null || loc == null) return false;
		
		// Récupère le centre et sa référence au monde
		Location center = island.getLocationSpawnIsland();
		if (center == null) return false;
		World centerWorld = center.getWorld();
		
		// GARDE-FOU : si l’un des mondes est null, on sort sans comparer
		if (centerWorld == null || world == null) return false;
		if (!centerWorld.equals(world)) return false;
		
		// Taille de l’île
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
