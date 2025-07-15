package fr.blackbalrog.oneblock.commands.subs.admin.spawn;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.admin.SubCommandAdmin;
import fr.blackbalrog.oneblock.api.message.Console;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SubCommandSetSpawn implements SubCommandAdmin
{
	
	private Console console = OneBlock.getInstance().getConsole();
	private String prefix = OneBlock.getInstance().getPrefix();
	
	@Override
	public String getName()
	{
		return "setspawn";
	}
	
	@Override
	public boolean executeAdmin(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			this.console.setError("Seul un joueur peut effectuer cette commande");
			return false;
		}
		
		Player player = (Player) sender;
		
		File dataFile = new File(OneBlock.getInstance().getDataFolder(), "Data/Location.yml");
		FileConfiguration locationSpawnConfiguration = YamlConfiguration.loadConfiguration(dataFile);
		
		if (! player.hasPermission("oneblock.command.admin.setspawn"))
		{
			player.sendMessage(this.prefix + "§7Vous n'avez pas la permission");
			return false;
		}
		
		locationSpawnConfiguration.set("Spawn.World", player.getLocation().getWorld().getName());
		locationSpawnConfiguration.set("Spawn.X", player.getLocation().getX());
		locationSpawnConfiguration.set("Spawn.Y", player.getLocation().getY());
		locationSpawnConfiguration.set("Spawn.Z", player.getLocation().getZ());
		locationSpawnConfiguration.set("Spawn.Yaw", player.getLocation().getYaw());
		locationSpawnConfiguration.set("Spawn.Pitch", player.getLocation().getPitch());
		
		try
		{
			locationSpawnConfiguration.save(dataFile);
			player.sendMessage(this.prefix + "§7Spawn défini");
		}
		catch (IOException exeption)
		{
			throw new RuntimeException(exeption);
		}
		
		return true;
	}
	
	@Override
	public List<String> tabCompleteAdmin(CommandSender sender, String[] args)
	{
		return List.of();
	}
}
