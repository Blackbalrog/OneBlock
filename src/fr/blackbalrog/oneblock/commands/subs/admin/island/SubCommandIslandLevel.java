package fr.blackbalrog.oneblock.commands.subs.admin.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.admin.SubCommandAdmin;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.island.border.BorderIsland;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubCommandIslandLevel implements SubCommandAdmin
{
	
	private Console console = OneBlock.getInstance().getConsole();
	private String prefix = OneBlock.getInstance().getPrefix();
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	
	@Override
	public String getName()
	{
		return "level";
	}
	
	@Override
	public boolean executeAdmin(CommandSender sender, String[] args)
	{
		if (!(sender instanceof  Player))
		{
			this.console.setError("Seul un joueur peut effectuer cette commande");
			return false;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("oneblock.command.admin.level.*"))
		{
			player.sendMessage(this.prefix + "§7Vous n'avez pas la permission");
			return false;
		}
		
		if (args.length == 2)
		{
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null)
			{
				player.sendMessage(this.prefix + "§7Le joueur n'est connecter");
				return false;
			}
			
			SessionIsland sessionIsland = this.sessionManager.getSessionIsland(target.getUniqueId());
			BorderIsland borderIsland = new BorderIsland();
			
			int oldLevel = sessionIsland.getLevel();
			
			if (args[1].equalsIgnoreCase("up"))
			{
				sessionIsland.setLevel(sessionIsland.getLevel() + 1);
				int newSize = this.configuration.getInt("Island.level." + sessionIsland.getLevel() + ".size");
				borderIsland.setBorder(player, sessionIsland.getCenterLocation(), newSize);
				
				this.getInformations(player, sessionIsland, oldLevel);
				
				player.sendMessage(this.prefix + "§7Niveau d'île augmenter pour §e" + target.getName());
				return true;
			}
			
			else if (args[1].equalsIgnoreCase("down"))
			{
				sessionIsland.setLevel(sessionIsland.getLevel() - 1);
				int newSize = this.configuration.getInt("Island.level." + sessionIsland.getLevel() + ".size");
				borderIsland.setBorder(player, sessionIsland.getCenterLocation(), newSize);
				
				this.getInformations(player, sessionIsland, oldLevel);
				
				player.sendMessage(this.prefix + "§7Niveau d'île rétrograder pour §e" + target.getName());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<String> tabCompleteAdmin(CommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return Bukkit.getOnlinePlayers().stream()
					.map(Player::getName)
					.toList();
		}
		
		else if (args.length == 2)
		{
			return List.of("up", "down");
		}
		
		return List.of();
	}
	
	private void getInformations(Player player, SessionIsland sessionIsland, int oldLevel)
	{
		
		String updateColor;
		List<String> list = new ArrayList<>();
		
		list.add("§8§m----------§r " + this.prefix + "§m----------");
		list.add("");
		list.add("§7§nInformations:");
		list.add("");
		
		int newLevel = sessionIsland.getLevel();
		if (newLevel > oldLevel)
		{
			updateColor = "§a";
		}
		else
		{
			updateColor = "§c";
		}
		
		list.add("§7Niveau d'île: §e" + oldLevel + " §7-> " + updateColor + sessionIsland.getLevel());
		
		int newSize = this.configuration.getInt("Island.level." + sessionIsland.getLevel() + ".size");
		list.add("§7Taille:  §b" + newSize + "x" + newSize + " blocs");
		
		list.add("§7Nom:    §b" + sessionIsland.getIslandName());
		list.add("");
		list.add("§7île mis à niveau");
		list.add("");
		
		list.forEach(player::sendMessage);
	}
}
