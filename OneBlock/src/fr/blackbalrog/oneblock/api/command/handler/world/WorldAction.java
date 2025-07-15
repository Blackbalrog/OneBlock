package fr.blackbalrog.oneblock.api.command.handler.world;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface WorldAction
{
	String name();
	boolean execute(CommandSender sender, String[] args);
	List<String> tabComplete(CommandSender sender, String[] args);
}

