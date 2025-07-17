package fr.blackbalrog.oneblock.world;

import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class OneBlockWorldManager
{
	public static String WORLD_NAME = "OneBlock";
	// Lié à la création du monde
	public static AtomicBoolean OPERATION = new AtomicBoolean(false);
	public static Path getWorldDir()
	{
		return Bukkit.getWorldContainer().toPath().resolve(WORLD_NAME);
	}
}
