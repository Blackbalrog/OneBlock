package fr.blackbalrog.oneblock.listeners.island.protections;

import fr.blackbalrog.oneblock.api.island.IslandManager;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

/**
 * Empêche les pistons de déplacer des blocs en-dehors de l'île
 */
public class PistonProtectionListener implements Listener
{
	
	private IslandManager islandManager = new IslandManager();
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent event)
	{
		Location pistonLocation = event.getBlock().getLocation();
		SessionIsland sessionIsland = this.islandManager.getIslandAt(pistonLocation);
		if (sessionIsland == null) return;
		
		BlockFace blockFace = event.getDirection();
		for (Block moved : event.getBlocks())
		{
			Location destination = moved.getLocation().clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
			SessionIsland destinationIsland = this.islandManager.getIslandAt(destination);
			if (!sessionIsland.equals(destinationIsland))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonRetract(BlockPistonRetractEvent event)
	{
		if (!event.isSticky()) return;
		
		Location pistonLocation = event.getBlock().getLocation();
		SessionIsland sessionIsland = this.islandManager.getIslandAt(pistonLocation);
		if (sessionIsland == null) return;
		
		BlockFace blockFace = event.getDirection();
		Block attached = event.getBlock().getRelative(blockFace);
		Location destination = attached.getLocation().clone().subtract(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
		SessionIsland destinationIsland = this.islandManager.getIslandAt(destination);
		if (!sessionIsland.equals(destinationIsland))
		{
			event.setCancelled(true);
		}
	}
}
