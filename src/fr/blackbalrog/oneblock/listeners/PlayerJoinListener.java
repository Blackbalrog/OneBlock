package fr.blackbalrog.oneblock.listeners;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.island.IslandManager;
import fr.blackbalrog.oneblock.api.island.IslandsRegenBlock;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.OneBlockWorldManager;
import fr.blackbalrog.oneblock.world.island.border.BorderIsland;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private IslandDatabase islandDatabase = OneBlock.getInstance().getDatabasePlayer();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	private IslandManager islandManager = new IslandManager();
	private IslandsRegenBlock islandsRegenBlock = OneBlock.getInstance().getIslandMapRegenBlock();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		UUID uuidPlayer = player.getUniqueId();
		this.sessionManager.createSessionPlayer(uuidPlayer);
		
		this.sessionManager.createSessionIsland(uuidPlayer);
		
		this.islandDatabase.loadIsland(uuidPlayer);
		
		SessionIsland sessionIsland = this.sessionManager.getSessionIsland(uuidPlayer);
		
		if (sessionIsland == null) return;
		
		this.islandsRegenBlock.indexIsland(sessionIsland);
		
		int level = sessionIsland.getLevel();
		sessionIsland.setIslandSize(configuration.getInt("Island.level." + level + ".size"));
		
		Location locationPlayer = player.getLocation();
		if (!locationPlayer.getWorld().getName().equals(OneBlockWorldManager.WORLD_NAME)) return;
		
		SessionIsland islandAt = this.islandManager.getIslandAt(locationPlayer);
		
		int size = configuration.getInt("Island.level." + islandAt.getLevel() + ".size");
		new BorderIsland().setBorder(player, islandAt.getCenterLocation(), size);
	}
}
