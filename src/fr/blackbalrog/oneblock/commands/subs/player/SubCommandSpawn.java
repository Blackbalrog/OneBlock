package fr.blackbalrog.oneblock.commands.subs.player;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.player.SubCommandPlayer;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.world.spawn.SpawnLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubCommandSpawn implements SubCommandPlayer
{
	
	private Console console = OneBlock.getInstance().getConsole();
	
	@Override
	public boolean executePlayer(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			this.console.setError("Seul un joueur peut effectuer cette commande");
			return false;
		}
		
		Player player = (Player) sender;
		
		player.teleport(new SpawnLocation().getLocationSpawn(player));
		return true;
	}
	
	@Override
	public String getName()
	{
		return "spawn";
	}
	
	
	@Override
	public List<String> tabCompletePlayer(CommandSender sender, String[] args)
	{
		return List.of();
	}
}
