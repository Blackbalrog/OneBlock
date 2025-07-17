package fr.blackbalrog.oneblock.world.generator;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.world.OneBlockWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Gère la création et le chargement du monde OneBlock, selon son existence
 */
public class AsyncOneBlockWorld
{
	
	private OneBlock instance = OneBlock.getInstance();
	private Console console = instance.getConsole();
	private AtomicBoolean operation = OneBlockWorldManager.OPERATION;
	private String worldName = OneBlockWorldManager.WORLD_NAME;
	private String prefix = instance.getPrefix();
	private Path worldDir = OneBlockWorldManager.getWorldDir();
	
	/**
	 * <html>
	 * Initialise le monde:
	 * </br>
	 * - s'il existe déjà (dossier présent), on le charge
	 * </br>
	 * - sinon, on le crée
	 * </html>
	 */
	public void initWorld(CommandSender sender)
	{
		if (Files.exists(this.worldDir))
		{
			this.loadWorldOneBlock();
			sender.sendMessage(prefix + "§7Monde existant et chargé");
		}
		else
		{
			this.createWorld(sender);
		}
	}
	
	/**
	 * Crée le monde en asynchrone (nettoyage + génération puis chargement sur le thread principal)
	 */
	public void createWorld(CommandSender sender)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Si dossier existant, on le supprime
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
					WorldCreator creator = new WorldCreator(worldName).generator(new VoidWorldGenerator());
					creator.createWorld();
					sender.sendMessage(prefix + "§7Monde créé.");
					
					loadWorldOneBlock();
					
					operation.set(false);
				});
			}
		}.runTaskAsynchronously(instance);
	}
	
	public void deleteWorld(CommandSender sender)
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
	
	/**
	 * Charge le monde s'il éxiste, ou le crée si absent
	 */
	public void loadWorldOneBlock()
	{
		World world = Bukkit.getWorld(worldName);
		if (world == null)
		{
			world = Bukkit.createWorld(new WorldCreator(worldName));
			if (world != null)
			{
				this.console.setDebug("Monde chargé avec succès: " + world.getName());
			}
			else
			{
				this.console.setError("Échec du chargement du monde " + this.worldName + ". Vérifie que le dossier existe");
			}
		}
		else
		{
			this.console.setDebug("Monde déjà chargé: " + world.getName());
		}
	}
}
