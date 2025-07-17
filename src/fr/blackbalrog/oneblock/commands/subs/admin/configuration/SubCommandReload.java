package fr.blackbalrog.oneblock.commands.subs.admin.configuration;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.admin.SubCommandAdmin;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SubCommandReload implements SubCommandAdmin
{
	
	private String prefix = OneBlock.getInstance().getPrefix();
	
	@Override
	public String getName()
	{
		return "reload";
	}
	
	@Override
	public boolean executeAdmin(CommandSender sender, String[] args)
	{
		if (!sender.hasPermission("oneblock.command.admin.reload"))
		{
			sender.sendMessage(this.prefix + "§7Vous n'avez pas la permission");
			return false;
		}
		OneBlock.getInstance().getConfigurationManager().reloadConfigurations();
		sender.sendMessage(this.prefix + "§7La configuration a été reload");
		return true;
	}
	
	@Override
	public List<String> tabCompleteAdmin(CommandSender sender, String[] args)
	{
		return List.of();
	}
}
