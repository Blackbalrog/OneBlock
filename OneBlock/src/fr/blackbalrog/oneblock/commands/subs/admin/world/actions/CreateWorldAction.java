package fr.blackbalrog.oneblock.commands.subs.admin.world.actions;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.world.WorldAction;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.world.OneBlockWorld;
import fr.blackbalrog.oneblock.world.generator.VoidWorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateWorldAction implements WorldAction
{
	private OneBlock instance = OneBlock.getInstance();
	private AtomicBoolean operation = OneBlockWorld.OPERATION;
	private String worldName = OneBlockWorld.WORLD_NAME;
	private Path worldDir = OneBlockWorld.getWorldDir();
	private Console console = OneBlock.getInstance().getConsole();
	private String prefix = OneBlock.getInstance().getPrefix();
	
	@Override
	public String name()
	{
		return "create";
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if (!sender.hasPermission("oneblock.command.admin.world.create"))
		{
			sender.sendMessage(this.prefix + "§7Vous n'avez pas la permission");
			return false;
		}
		
		if (this.operation.get())
		{
			sender.sendMessage(this.prefix + "§7Une opération est déjà en cours, veuillez patienter…");
			return true;
		}
		
		this.operation.set(true);
		this.asyncCreateWorld(sender);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args)
	{
		return List.of();
	}
	
	private void asyncCreateWorld(CommandSender sender)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (Files.exists(worldDir))
				{
					try
					{
						Files.walk(worldDir)
								.sorted(Comparator.reverseOrder())
								.forEach(path -> {
									try
									{
										Files.delete(path);
										console.setDebug("nettoyé: " + path);
									}
									catch (IOException exeption)
									{
										console.setError("Échec nettoyage " + path + ": " + exeption.getMessage());
										throw new UncheckedIOException(exeption);
									}
								});
					}
					catch (IOException exeption)
					{
						Bukkit.getScheduler().runTask(instance, () -> sender.sendMessage(prefix + "§cImpossible de nettoyer l'ancien monde: " + exeption.getMessage()));
						operation.set(false);
						return;
					}
				}
				
				Bukkit.getScheduler().runTask(instance, () -> {
					WorldCreator worldCreator = new WorldCreator(worldName)
							.generator(new VoidWorldGenerator());
					worldCreator.createWorld();
					sender.sendMessage(prefix + "§7Monde créé");
					operation.set(false);
				});
			}
		}.runTaskAsynchronously(this.instance);
	}
}
