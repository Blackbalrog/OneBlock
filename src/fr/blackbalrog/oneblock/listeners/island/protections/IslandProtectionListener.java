package fr.blackbalrog.oneblock.listeners.island.protections;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.island.IslandManager;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.api.sessions.SessionPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class IslandProtectionListener implements Listener
{
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private String prefix = OneBlock.getInstance().getPrefix();
	private IslandManager islandManager = new IslandManager();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Location location = event.getBlock().getLocation();
		SessionIsland sessionIsland = this.islandManager.getIslandAt(location);
		if (sessionIsland == null) return; // pas dans une île
		
		Player player = event.getPlayer();
		
		SessionPlayer sessionPlayer = this.sessionManager.getSessionPlayer(player.getUniqueId());
		if (sessionPlayer == null) return;
		
		if (sessionPlayer.isAccess())
		{
			Location locationBlockRegen = sessionIsland.getLocationBlockRegen();
			if (event.getBlock().getLocation().equals(locationBlockRegen))
			{
				event.setCancelled(true);
				player.sendMessage(this.prefix + "§7Vous ne pouvez pas casser le block de régéneration de l'île");
			}
			return;
		}
		if (!this.islandManager.isOwner(player, sessionIsland))
		{
			event.setCancelled(true);
			player.sendMessage(this.prefix + "§cVous ne pouvez pas casser sur l'île d'un autre joueur !");
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Location location = event.getBlockPlaced().getLocation();
		SessionIsland sessionIsland = this.islandManager.getIslandAt(location);
		if (sessionIsland == null) return;
		
		Player player = event.getPlayer();
		
		SessionPlayer sessionPlayer = this.sessionManager.getSessionPlayer(player.getUniqueId());
		if (sessionPlayer == null) return;
		
		if (sessionPlayer.isAccess()) return;
		
		if (!this.islandManager.isOwner(player, sessionIsland))
		{
			event.setCancelled(true);
			player.sendMessage(this.prefix + "§cVous ne pouvez pas poser de bloc sur l'île d'un autre joueur !");
		}
	}
	
	@EventHandler
	public void onUse(PlayerInteractEvent event)
	{
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getClickedBlock() == null) return;
		
		Location location = event.getClickedBlock().getLocation();
		SessionIsland sessionIsland = this.islandManager.getIslandAt(location);
		if (sessionIsland == null) return;
		
		Player player = event.getPlayer();
		
		SessionPlayer sessionPlayer = this.sessionManager.getSessionPlayer(player.getUniqueId());
		if (sessionPlayer == null) return;
		
		if (sessionPlayer.isAccess()) return;
		
		if (!this.islandManager.isOwner(player, sessionIsland))
		{
			event.setCancelled(true);
			player.sendMessage(this.prefix + "§cInteraction non autorisée sur cette île !");
		}
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event)
	{
		event.blockList().removeIf(block -> this.islandManager.getIslandAt(block.getLocation()) != null);
	}
}
