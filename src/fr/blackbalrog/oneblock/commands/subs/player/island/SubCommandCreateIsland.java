package fr.blackbalrog.oneblock.commands.subs.player.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.player.SubCommandPlayer;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.island.IslandsRegenBlock;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.island.border.BorderIsland;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubCommandCreateIsland implements SubCommandPlayer
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private Console console = OneBlock.getInstance().getConsole();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	private String prefix = OneBlock.getInstance().getPrefix();
	private IslandDatabase islandDatabase = OneBlock.getInstance().getDatabasePlayer();
	private IslandsRegenBlock islandsRegenBlock = OneBlock.getInstance().getIslandMapRegenBlock();
	
	@Override
	public boolean executePlayer(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			this.console.setError("Seul un joueur peut exécuter cette commande");
			return false;
		}
		Player player = (Player) sender;
		
		int size = this.configuration.getInt("Island.level." + 1 + ".size");
		
		World world = Bukkit.getWorld("OneBlock");
		if (world == null)
		{
			player.sendMessage(this.prefix + "§7Le monde OneBlock n'existe pas");
			return false;
		}
		
		double spacing = 2000.0;
		double centerX = this.islandsRegenBlock.getNextIslandX(spacing);
		double centerZ = 0.0;
		Location center = new Location(world, centerX, 100.0, centerZ);
		
		this.sessionManager.createSessionIsland(player.getUniqueId());
		SessionIsland sessionIsland = this.sessionManager.getSessionIsland(player.getUniqueId());
		sessionIsland.setIslandName(player.getName());
		sessionIsland.setIslandSize(size);
		sessionIsland.setLevel(1);
		sessionIsland.setOwner(player.getUniqueId());
		sessionIsland.setMembersIsland(List.of(player.getUniqueId()));
		sessionIsland.setSpawnLocation(
				center.getWorld(),
				center.getX(),
				center.getY(),
				center.getZ(),
				center.getYaw(),
				center.getPitch()
		);
		
		Location blockRegenLocation = new Location(world, center.getBlockX(), center.getBlockY() - 1, center.getBlockZ());
		world.getBlockAt(blockRegenLocation).setType(Material.OAK_WOOD);
		sessionIsland.setLocationBlockRegen(blockRegenLocation);
		
		this.islandDatabase.upsertIsland(sessionIsland);
		
		this.islandsRegenBlock.indexIsland(sessionIsland);
		
		player.teleport(sessionIsland.getLocationSpawnIsland());
		new BorderIsland().setBorder(player, center, size);
		player.teleport(center);
		
		player.sendMessage("§8§m----------§r " + this.prefix + "§m----------");
		player.sendMessage("");
		player.sendMessage("§7§nInformations:");
		player.sendMessage("");
		player.sendMessage("§7Niveau: §b" + sessionIsland.getLevel());
		player.sendMessage("§7Taille:  §b" + size + "x" + size + " blocs");
		player.sendMessage("§7Nom:    §b" + sessionIsland.getIslandName());
		player.sendMessage("");
		player.sendMessage("§7Île créée");
		player.sendMessage("");
		
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
		return "create";
	}
}
