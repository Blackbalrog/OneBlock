package fr.blackbalrog.oneblock.api.message;

import org.bukkit.Bukkit;

public class Console
{
	private String prefix;
	
	public Console(String prefix) {
		this.prefix = prefix;
	}
	
	public void setError(String message)
	{
		Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§c" + message);
	}
	
	public void setDebug(String message)
	{
		Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§3" + message);
	}
	
	public void setMessage(String message)
	{
		Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§r" + message);
	}
	
	public void setEnable(String message)
	{
		Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§a" + message);
	}
	
	public void setDisable(String message)
	{
		Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§c" + message);
	}
	
	public String getPrefix() {
		return this.prefix;
	}
}
