package fr.blackbalrog.oneblock.api.command.handler.player;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommandPlayer
{
	String getName();
	boolean executePlayer(CommandSender sender, String[] args);
	List<String> tabCompletePlayer(CommandSender sender, String[] args);
}

