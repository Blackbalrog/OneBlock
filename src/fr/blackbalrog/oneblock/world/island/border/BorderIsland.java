package fr.blackbalrog.oneblock.world.island.border;

import fr.blackbalrog.oneblock.OneBlock;
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
		
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				player.setWorldBorder(border);
			}
		}.runTaskLater(OneBlock.getInstance(), 1L);
	}
}
