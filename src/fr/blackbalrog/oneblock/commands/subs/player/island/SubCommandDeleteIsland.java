package fr.blackbalrog.oneblock.commands.subs.player.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.player.SubCommandPlayer;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.island.IslandsRegenBlock;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.spawn.SpawnLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubCommandDeleteIsland implements SubCommandPlayer
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private Console console = OneBlock.getInstance().getConsole();
	private String prefix = OneBlock.getInstance().getPrefix();
	private IslandDatabase playerDatabase = OneBlock.getInstance().getDatabasePlayer();
	private IslandsRegenBlock islandsRegenBlock = OneBlock.getInstance().getIslandMapRegenBlock();
	
	@Override
	public boolean executePlayer(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			this.console.setError("Seul un joueur peut effectuer cette commande");
			return false;
		}
		
		Player player = (Player) sender;
		
		SessionIsland sessionIsland = this.sessionManager.getSessionIsland(player.getUniqueId());
		if (sessionIsland == null)
		{
			player.sendMessage(this.prefix + "§7Vous n'avez pas d'île");
			return false;
		}
		
		World world = Bukkit.getWorld("OneBlock");
		if (world == null)
		{
			player.sendMessage(this.prefix + "§7Le monde du OneBlock n'éxiste pas");
			return false;
		}
		
		int sizeX = sessionIsland.getIslandSize().getBlockX();
		int sizeZ = sessionIsland.getIslandSize().getBlockZ();
		Location locationCenterIsland = sessionIsland.getLocationSpawnIsland();
		if (locationCenterIsland == null)
		{
			player.sendMessage(this.prefix + "§7Vous n'avez pas d'île");
			return false;
		}
		
		this.deleteIsland(world, locationCenterIsland, sizeX, sizeZ);
		this.islandsRegenBlock.removeIsland(sessionIsland);
		
		this.sessionManager.removeSessionIsland(player.getUniqueId());
		
		// Database et Redis
		// ...
		this.playerDatabase.deleteIsland(player.getUniqueId());
		
		player.sendMessage(this.prefix + "§7Île supprimer");
		
		player.teleport(new SpawnLocation().getLocationSpawn(player));
		return true;
	}
	
	@Override
	public List<String> tabCompletePlayer(CommandSender sender, String[] args)
	{
		return List.of();
	}
	
	@Override
	public String getName()
	{
		return "delete";
	}
	
	private void deleteIsland(World world, Location center, int sizeX, int sizeZ)
	{
		int cx = center.getBlockX();
		int cz = center.getBlockZ();
		int minY = world.getMinHeight();
		int maxY = world.getMaxHeight();
		
		for (int x = -sizeX; x <= sizeX; x++)
		{
			for (int z = -sizeZ; z <= sizeZ; z++)
			{
				for (int y = minY; y < maxY; y++)
				{
					world.getBlockAt(cx + x, y, cz + z).setType(Material.AIR);
				}
			}
		}
	}
}
