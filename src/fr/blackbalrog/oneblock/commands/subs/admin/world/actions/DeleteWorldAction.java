package fr.blackbalrog.oneblock.commands.subs.admin.world.actions;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.admin.world.WorldAction;
import fr.blackbalrog.oneblock.world.OneBlockWorldManager;
import fr.blackbalrog.oneblock.world.generator.AsyncOneBlockWorld;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeleteWorldAction implements WorldAction
{
	
	private AtomicBoolean operation = OneBlockWorldManager.OPERATION;
	private String prefix = OneBlock.getInstance().getPrefix();
	
	@Override
	public String name()
	{
		return "delete";
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if (! sender.hasPermission("oneblock.command.admin.world.delete"))
		{
			sender.sendMessage(this.prefix + "§7Vous n'avez pas la permission");
			return false;
		}
		
		if (this.operation.get())
		{
			sender.sendMessage(this.prefix + "§7Une opération est déjà en cours, veuillez patienter…");
			return true;
		}
		
		this.operation.set(true);
		new AsyncOneBlockWorld().deleteWorld(sender);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args)
	{
		return List.of("delete");
	}
}
