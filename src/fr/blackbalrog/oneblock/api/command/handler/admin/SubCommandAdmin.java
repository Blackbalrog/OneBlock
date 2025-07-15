package fr.blackbalrog.oneblock.api.command.handler.admin;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommandAdmin
{
	String getName();
	boolean executeAdmin(CommandSender sender, String[] args);
	List<String> tabCompleteAdmin(CommandSender sender, String[] args);
}

