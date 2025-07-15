package fr.blackbalrog.oneblock.world.island.border;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderIsland
{
	
	private WorldBorder border = Bukkit.getServer().createWorldBorder();
	
	public void setBorder(Player player, Location center, int size)
	{
		double cx = center.getBlockX() + 0.5;
		double cz = center.getBlockZ() + 0.5;
		
		double diameter = (size * 2.0 + 2.0) /2;
		
		this.border.setCenter(cx, cz);
		this.border.setSize(diameter);
		this.border.setWarningDistance(0);
		this.border.setWarningTime(0);
		
		player.setWorldBorder(this.border);
	}
	
	public void callBorder(Player player, SessionManager sessionManager, Player target)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				SessionIsland sessionIsland = sessionManager.getSessionIsland(target.getUniqueId());
				if (sessionIsland == null) return;
				int size = (int) sessionIsland.getIslandSize().getX();
				setBorder(player, sessionIsland.getLocationSpawnIsland(), size);
			}
		}.runTaskLater(OneBlock.getInstance(), 1L);
	}
}
