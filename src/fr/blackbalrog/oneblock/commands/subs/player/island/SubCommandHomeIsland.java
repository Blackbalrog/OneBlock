package fr.blackbalrog.oneblock.commands.subs.player.island;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.player.SubCommandPlayer;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.world.island.border.BorderIsland;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubCommandHomeIsland implements SubCommandPlayer
{
	
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private String prefix = OneBlock.getInstance().getPrefix();
	private Console console = OneBlock.getInstance().getConsole();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	
	@Override
	public String getName()
	{
		return "home";
	}
	
	@Override
	public boolean executePlayer(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			this.console.setError("Vous ne pouvez pas executer cette commande");
			return false;
		}
		
		Player player = (Player) sender;
		
		SessionIsland sessionIsland = this.sessionManager.getSessionIsland(player.getUniqueId());
		if (sessionIsland == null /*|| sessionIsland.getLocationSpawnIsland() == null*/)
		{
			player.sendMessage(this.prefix + "§7Vous n'avez pas d'île");
			return false;
		}
		
		player.teleport(sessionIsland.getLocationSpawnIsland());
		
		int size = this.configuration.getInt("Island.level." + sessionIsland.getLevel() + ".size");
		
		BorderIsland borderIsland = new BorderIsland();
		borderIsland.setBorder(player, sessionIsland.getLocationSpawnIsland(), size);
		
		player.sendMessage(this.prefix + "§7Téleporation à vôtre île");
		return true;
	}
	
	@Override
	public List<String> tabCompletePlayer(CommandSender sender, String[] args)
	{
		return List.of();
	}
}
