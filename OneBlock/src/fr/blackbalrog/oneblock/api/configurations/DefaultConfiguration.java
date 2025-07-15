package fr.blackbalrog.oneblock.api.configurations;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DefaultConfiguration extends YamlConfiguration
{
	private File fileConfiguration;
	private String file_name;
	
	public DefaultConfiguration(JavaPlugin plugin, String file_name)
	{
		this.file_name = file_name;
		this.fileConfiguration = new File(plugin.getDataFolder(), file_name);
		if (!this.fileConfiguration.exists())
		{
			plugin.saveResource(file_name, false);
		}
		
		this.reload();
	}
	
	public void reload()
	{
		try
		{
			super.load(this.fileConfiguration);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		
	}
	
	public String getFileName() {
		return this.file_name;
	}
	
	public File getFile() {
		return this.fileConfiguration;
	}
}
