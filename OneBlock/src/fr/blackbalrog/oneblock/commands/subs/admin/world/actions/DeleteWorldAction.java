package fr.blackbalrog.oneblock.commands.subs.admin.world.actions;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.command.handler.world.WorldAction;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.world.OneBlockWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeleteWorldAction implements WorldAction
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
		return "delete";
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if (! sender.hasPermission("oneblock.command.admin.world.delete"))
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
		this.asyncDeleteWorld(sender);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args)
	{
		return List.of("delete");
	}
	
	private void asyncDeleteWorld(CommandSender sender)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				try
				{
					Files.list(worldDir).forEach(path -> console.setDebug("    contient: " + path.getFileName()));
				}
				catch (IOException exeption)
				{
					console.setError("Impossible de lister le dossier: " + exeption.getMessage());
				}
				
				Bukkit.getScheduler().runTask(instance, () -> {
					World world = Bukkit.getWorld(worldName);
					if (world != null)
					{
						boolean unloadWorld = Bukkit.unloadWorld(world, false);
						console.setDebug("unloadWorld: " + unloadWorld);
					}
					else
					{
						console.setDebug("Le monde n'était pas chargé");
					}
				});
				
				try
				{
					if (Files.notExists(worldDir))
					{
						throw new NoSuchFileException(worldDir.toString());
					}
					Files.walk(worldDir)
							.sorted(Comparator.reverseOrder())
							.forEach(path -> {
								try
								{
									Files.delete(path);
									console.setMessage("supprimé: " + path);
								}
								catch (IOException exeption)
								{
									console.setError("Échec suppression " + path + ": " + exeption.getMessage());
									throw new UncheckedIOException(exeption);
								}
							});
				}
				catch (NoSuchFileException exeption)
				{
					Bukkit.getScheduler().runTask(instance, () -> sender.sendMessage(prefix + "§7Le monde n'existe pas"));
					operation.set(false);
					return;
				}
				catch (UncheckedIOException exeption) {Bukkit.getScheduler().runTask(instance, () -> sender.sendMessage(prefix + "§cErreur suppression, voir console"));
					operation.set(false);
					return;
				}
				catch (IOException exeption)
				{
					Bukkit.getScheduler().runTask(instance, () -> sender.sendMessage(prefix + "§cErreur I/O: " + exeption.getMessage()));
					operation.set(false);
					return;
				}
				
				Bukkit.getScheduler().runTask(instance, () -> {
					sender.sendMessage(prefix + "§7Le monde a été supprimé");
					operation.set(false);
				});
			}
		}.runTaskAsynchronously(this.instance);
	}
}
