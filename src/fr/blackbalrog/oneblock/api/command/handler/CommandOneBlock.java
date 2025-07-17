package fr.blackbalrog.oneblock.api.command.handler;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.admin.SubCommandAdmin;
import fr.blackbalrog.oneblock.api.command.handler.player.SubCommandPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

public class CommandOneBlock implements CommandExecutor, TabCompleter
{
	private final Map<String, SubCommandPlayer> playerCommands = new HashMap<>();
	private final Map<String, SubCommandAdmin> adminCommands  = new HashMap<>();
	
	private String prefix = OneBlock.getInstance().getPrefix();
	
	public void registerPlayer(SubCommandPlayer cmd)
	{
		playerCommands.put(cmd.getName().toLowerCase(), cmd);
	}
	
	public void registerAdmin(SubCommandAdmin cmd)
	{
		adminCommands.put(cmd.getName().toLowerCase(), cmd);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage("§cUtilisation : /oneblock <(admin)?> <commande> [args...]");
			return true;
		}
		
		boolean isAdminMode = args[0].equalsIgnoreCase("admin");
		int idxName = isAdminMode ? 1 : 0;
		if (args.length <= idxName)
		{
			sender.sendMessage("§cIl manque la sous-commande.");
			return true;
		}
		
		String name = args[idxName].toLowerCase();
		String[] subArgs = Arrays.copyOfRange(args, idxName + 1, args.length);
		
		if (isAdminMode)
		{
			if (!sender.hasPermission("oneblock.admin"))
			{
				sender.sendMessage(this.prefix + "§7Tu n'as pas la permission");
				return true;
			}
			SubCommandAdmin cmd = adminCommands.get(name);
			if (cmd == null)
			{
				sender.sendMessage(this.prefix + "§Sous-commande admin inconnue: " + name);
				return true;
			}
			return cmd.executeAdmin(sender, subArgs);
		}
		else
		{
			SubCommandPlayer cmd = playerCommands.get(name);
			if (cmd == null)
			{
				sender.sendMessage(this.prefix + "§7Sous-commande joueur inconnue: " + name);
				return true;
			}
			return cmd.executePlayer(sender, subArgs);
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 1)
		{
			List<String> suggestions = new ArrayList<>();
			suggestions.add("admin");
			playerCommands.keySet().stream()
					.filter(n -> n.startsWith(args[0].toLowerCase()))
					.forEach(suggestions::add);
			return suggestions;
		}
		
		boolean isAdminMode = args[0].equalsIgnoreCase("admin");
		int idxName = isAdminMode ? 1 : 0;
		if (args.length == idxName + 1)
		{
			Collection<String> keys = isAdminMode ? adminCommands.keySet() : playerCommands.keySet();
			return keys.stream()
					.filter(n -> n.startsWith(args[idxName].toLowerCase()))
					.collect(Collectors.toList());
		}
		
		String name = args[idxName].toLowerCase();
		String[] subArgs = Arrays.copyOfRange(args, idxName + 1, args.length);
		if (isAdminMode)
		{
			SubCommandAdmin cmd = adminCommands.get(name);
			if (cmd != null) return cmd.tabCompleteAdmin(sender, subArgs);
		}
		else
		{
			SubCommandPlayer cmd = playerCommands.get(name);
			if (cmd != null) return cmd.tabCompletePlayer(sender, subArgs);
		}
		return Collections.emptyList();
	}
}
