package fr.blackbalrog.oneblock.world;

import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class OneBlockWorld
{
	public static String WORLD_NAME = "OneBlock";
	public static AtomicBoolean OPERATION = new AtomicBoolean(false);
	public static Path getWorldDir()
	{
		return Bukkit.getWorldContainer().toPath().resolve(WORLD_NAME);
	}
}
