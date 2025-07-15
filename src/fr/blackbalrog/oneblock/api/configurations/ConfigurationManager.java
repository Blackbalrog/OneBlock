package fr.blackbalrog.oneblock.api.configurations;

import java.util.ArrayList;
import java.util.List;


/**
 * @apiNote Bon là je sais pas torp quoi dire, je m'en sers principalement pour reload
 * les configurations par default et après je créé une commande de reload, en faite cette class ne sert qu'à ça
 */
public class ConfigurationManager
{
	
	private List<DefaultConfiguration> configurations;
	
	public ConfigurationManager()
	{
		this.configurations = new ArrayList<>();
	}
	
	public void add(DefaultConfiguration configuration)
	{
		this.configurations.add(configuration);
	}
	
	public void remove(DefaultConfiguration configuration)
	{
		this.configurations.remove(configuration);
	}
	
	public void reloadConfigurations()
	{
		this.configurations.forEach(DefaultConfiguration::reload);
	}
	
	public List<DefaultConfiguration> getConfigurations()
	{
		return this.configurations;
	}
}
