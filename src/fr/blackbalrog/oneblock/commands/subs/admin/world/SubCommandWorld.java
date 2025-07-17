package fr.blackbalrog.oneblock.commands.subs.admin.world;

import fr.blackbalrog.oneblock.api.command.handler.admin.SubCommandAdmin;
import fr.blackbalrog.oneblock.api.command.handler.admin.world.WorldAction;
import fr.blackbalrog.oneblock.commands.subs.admin.world.actions.CreateWorldAction;
import fr.blackbalrog.oneblock.commands.subs.admin.world.actions.DeleteWorldAction;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class SubCommandWorld implements SubCommandAdmin
{
	
	private final Map<String, WorldAction> actions = new HashMap<>();
	
	public SubCommandWorld()
	{
		this.register(new CreateWorldAction());
		this.register(new DeleteWorldAction());
	}
	
	@Override
	public String getName()
	{
		return "world";
	}
	
	@Override
	public boolean executeAdmin(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage("§cUsage : /oneblock admin world <" + String.join("|", this.actions.keySet()) + ">");
			return true;
		}
		
		String key = args[0].toLowerCase();
		WorldAction action = this.actions.get(key);
		if (action == null)
		{
			sender.sendMessage("§cAction inconnue: " + key);
			return true;
		}
		
		// délègue tout le reste
		String[] sub = Arrays.copyOfRange(args, 1, args.length);
		return action.execute(sender, sub);
	}
	
	@Override
	public List<String> tabCompleteAdmin(CommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return this.actions.keySet().stream()
					.filter(a -> a.startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}
		WorldAction action = this.actions.get(args[0].toLowerCase());
		if (action != null)
		{
			String[] sub = Arrays.copyOfRange(args, 1, args.length);
			return action.tabComplete(sender, sub);
		}
		return Collections.emptyList();
	}
	
	private void register(WorldAction action)
	{
		this.actions.put(action.name(), action);
	}
}
