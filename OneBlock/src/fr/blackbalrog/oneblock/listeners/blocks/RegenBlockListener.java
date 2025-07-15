package fr.blackbalrog.oneblock.listeners.blocks;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.island.IslandsRegenBlock;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.island.IslandsRegenBlock;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.world.OneBlockWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class RegenBlockListener implements Listener
{
	
	private IslandsRegenBlock islandsRegenBlock = OneBlock.getInstance().getIslandMapRegenBlock();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		if (!block.getWorld().getName().equals(OneBlockWorld.WORLD_NAME)) return;
		
		long key = this.islandsRegenBlock.pack(block.getLocation());
		SessionIsland sessionIsland = this.islandsRegenBlock.getRegenIndex().get(key);
		if (sessionIsland == null) return;
		
		if (!block.getLocation().equals(sessionIsland.getLocationBlockRegen())) return;
		
		event.setCancelled(true);
		int level = sessionIsland.getLevel();
		var materials = this.configuration
				.getStringList("Island.level." + level + ".blocks")
				.stream()
				.map(Material::valueOf)
				.toList();
		
		if (materials.isEmpty()) return;
		
		Material next = materials.get((int)(Math.random() * materials.size()));
		block.setType(next);
		block.getWorld()
				.dropItemNaturally(
						block.getLocation().add(0, 1, 0),
						new ItemStack(next)
				);
	}
}
