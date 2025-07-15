package fr.blackbalrog.oneblock.api.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class IslandsRegenBlock
{
	private Map<Long, SessionIsland> regenIndex = new HashMap<>();
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	
	/**
	 * Reconstruit l'index à partir de toutes les sessions îles existantes
	 */
	public void rebuild()
	{
		this.regenIndex.clear();
		for (SessionIsland sessionIsland : this.sessionManager.getSessionIslandMap().values())
		{
			Location locationBlockRegen = sessionIsland.getLocationBlockRegen();
			if (locationBlockRegen != null)
			{
				this.regenIndex.put(this.pack(locationBlockRegen), sessionIsland);
			}
		}
	}
	
	public Map<Long, SessionIsland> getRegenIndex()
	{
		return this.regenIndex;
	}
	
	public void indexIsland(SessionIsland sessionIsland)
	{
		Location locationBlockRegen = sessionIsland.getLocationBlockRegen();
		if (locationBlockRegen != null) this.regenIndex.put(this.pack(locationBlockRegen), sessionIsland);
	}
	
	public void removeIsland(SessionIsland sessionIsland)
	{
		Location locationBlockRegen = sessionIsland.getLocationBlockRegen();
		if (locationBlockRegen != null) this.regenIndex.remove(this.pack(locationBlockRegen));
	}
	
	/**
	 * Calcule la prochaine coordonnée X libre pour une nouvelle île,
	 * espacée d'un intervalle donné
	 */
	public double getNextIslandX(double spacing)
	{
		if (this.regenIndex.isEmpty())
		{
			return 0.0;
		}
		// extrait le X de chaque clé packée
		int maxX = this.regenIndex.keySet().stream()
				.mapToInt(key -> (int) (key >> 32))
				.max()
				.orElse(0);
		return maxX + spacing;
	}
	
	public long pack(Location locationBlockRegen)
	{
		int x = locationBlockRegen.getBlockX(), z = locationBlockRegen.getBlockZ();
		return ((long)x << 32) | (z & 0xFFFFFFFFL);
	}
}
