package fr.blackbalrog.oneblock.commands.subs.admin.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.admin.SubCommandAdmin;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.api.sessions.SessionPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubCommandIslandAccess implements SubCommandAdmin
{
	
	private Console console = OneBlock.getInstance().getConsole();
	private String prefix = OneBlock.getInstance().getPrefix();
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	
	@Override
	public String getName()
	{
		return "access";
	}
	
	@Override
	public boolean executeAdmin(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			this.console.setError("Vous ne pouvez pas executer cette commande");
			return false;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("oneblock.command.admin.access"))
		{
			player.sendMessage(this.prefix + "§7Vous n'avez pas la permission");
			return false;
		}
		
		SessionPlayer sessionPlayer = this.sessionManager.getSessionPlayer(player.getUniqueId());
		if (sessionPlayer == null) return false;
		
		sessionPlayer.setAccess(!sessionPlayer.isAccess());
		player.sendMessage(this.prefix + (sessionPlayer.isAccess() ? "§7Vous avez à présent accès à l'île" : "§7Vous n'avez à présent plus accès à l'île"));
		
		return true;
	}
	
	@Override
	public List<String> tabCompleteAdmin(CommandSender sender, String[] args)
	{
		return List.of();
	}
}
