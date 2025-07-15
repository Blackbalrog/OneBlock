package fr.blackbalrog.oneblock.listeners.admin;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.island.border.BorderIsland;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TeleportBorderListener implements Listener
{
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private BorderIsland border = new BorderIsland();
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		String command = event.getMessage();
		if (! command.startsWith("/tp "))
			return;
		String[] parts = command.split("\\s+");
		if (parts.length < 2)
			return;
		
		Player player = event.getPlayer();
		Player target = Bukkit.getPlayerExact(parts[1]);
		if (target == null)
			return;
		
		new BorderIsland().callBorder(player, this.sessionManager, target);
	}
}
