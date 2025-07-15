package fr.blackbalrog.oneblock.listeners;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.island.IslandManager;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.island.border.BorderIsland;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener
{
	
	private final SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private final IslandDatabase islandDatabase = OneBlock.getInstance().getDatabasePlayer();
	private final DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	private final IslandManager islandManager = new IslandManager();
	private final Console console = OneBlock.getInstance().getConsole();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		// Prépare les sessions
		sessionManager.createSessionPlayer(player.getUniqueId());
		
		// Charge la session île depuis la base
		this.islandDatabase.loadSessionIsland(player.getUniqueId());
		
		SessionIsland ownIsland = this.sessionManager.getSessionIsland(player.getUniqueId());
		if (ownIsland != null)
		{
			// Initialise la taille de l'île pour les protections
			int lvl = ownIsland.getLevel();
			ownIsland.setIslandSize(configuration.getInt("Island.level." + lvl + ".size"));
			console.setDebug("Loaded island: name=" + ownIsland.getIslandName() + ", level=" + lvl + ", size=" + ownIsland.getIslandSize().getBlockX());
		}
		else
		{
			console.setDebug("No island to load for player " + player.getName());
		}
		
		// Décalage d'un tick pour s'assurer que player.getLocation().getWorld() est valide
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				console.setDebug("debug join 0");
				Location loc = player.getLocation();
				World world = loc.getWorld();
				if (world == null)
				{
					console.setDebug("debug join: world is null");
					return;
				}
				
				SessionIsland islandAt = islandManager.getIslandAt(loc);
				if (islandAt == null)
				{
					console.setDebug("debug join: no island at " + loc);
					return;
				}
				
				console.setDebug("debug join: found island " + islandAt.getIslandName());
				int size = configuration.getInt("Island.level." + islandAt.getLevel() + ".size");
				console.setDebug("debug join 2: setting border size=" + size);
				new BorderIsland().setBorder(player, islandAt.getLocationSpawnIsland(), size);
			}
		}.runTaskLater(OneBlock.getInstance(), 1L);
	}
}
