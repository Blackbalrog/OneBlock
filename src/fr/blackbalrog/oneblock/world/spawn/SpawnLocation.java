package fr.blackbalrog.oneblock.world.spawn;

import fr.blackbalrog.oneblock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SpawnLocation
{

	private String prefix = OneBlock.getInstance().getPrefix();
	
	public Location getLocationSpawn(Player player)
	{
		File dataFile = new File(OneBlock.getInstance().getDataFolder(), "Data/Location.yml");
		FileConfiguration locationSpawnConfiguration = YamlConfiguration.loadConfiguration(dataFile);
		
		if (locationSpawnConfiguration.getConfigurationSection("Spawn") == null)
		{
			player.sendMessage(this.prefix + "§7Le spawn n'a pas été défini");
			return null;
		}
		
		World world = Bukkit.getWorld(locationSpawnConfiguration.getString("Spawn.World"));
		if (world == null)
		{
			player.sendMessage(this.prefix + "§7Le monde n'éxiste pas");
			return null;
		}
		
		double x = locationSpawnConfiguration.getDouble("Spawn.X");
		double y = locationSpawnConfiguration.getDouble("Spawn.Y");
		double z = locationSpawnConfiguration.getDouble("Spawn.Z");
		float yaw = (float) locationSpawnConfiguration.getInt("Spawn.Yaw");
		float pitch = (float) locationSpawnConfiguration.getInt("Spawn.Pitch");
		
		player.sendMessage(this.prefix + "§7Téléportation au spawn");
		
		return new Location(world, x, y, z, yaw, pitch);
	}
}
