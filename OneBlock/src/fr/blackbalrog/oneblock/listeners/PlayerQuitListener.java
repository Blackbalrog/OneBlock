package fr.blackbalrog.oneblock.listeners;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private IslandDatabase islandDatabase = OneBlock.getInstance().getDatabasePlayer();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		SessionIsland sessionIsland = this.sessionManager.getSessionIsland(player.getUniqueId());
		if (sessionIsland.getOwner() != null)
		{
			this.islandDatabase.upsertIsland(sessionIsland);
		}
		
		//this.sessionManager.removeSessionIsland(player.getUniqueId());
		this.sessionManager.removeSessionPlayer(player.getUniqueId());
	}
	
}
