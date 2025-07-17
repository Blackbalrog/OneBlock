package fr.blackbalrog.oneblock;

import fr.blackbalrog.oneblock.api.command.handler.CommandOneBlock;
import fr.blackbalrog.oneblock.api.island.IslandsRegenBlock;
import fr.blackbalrog.oneblock.api.configurations.ConfigurationManager;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.database.connection.Database;
import fr.blackbalrog.oneblock.api.database.libs.IslandDatabase;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import fr.blackbalrog.oneblock.commands.subs.admin.configuration.SubCommandReload;
import fr.blackbalrog.oneblock.commands.subs.admin.island.SubCommandIslandAccess;
import fr.blackbalrog.oneblock.commands.subs.admin.island.SubCommandIslandLevel;
import fr.blackbalrog.oneblock.commands.subs.admin.spawn.SubCommandSetSpawn;
import fr.blackbalrog.oneblock.commands.subs.admin.world.SubCommandWorld;
import fr.blackbalrog.oneblock.commands.subs.player.island.SubCommandCreateIsland;
import fr.blackbalrog.oneblock.commands.subs.player.SubCommandSpawn;
import fr.blackbalrog.oneblock.commands.subs.player.island.SubCommandDeleteIsland;
import fr.blackbalrog.oneblock.commands.subs.player.island.SubCommandHomeIsland;
import fr.blackbalrog.oneblock.listeners.PlayerJoinListener;
import fr.blackbalrog.oneblock.listeners.PlayerQuitListener;
import fr.blackbalrog.oneblock.listeners.admin.TeleportBorderListener;
import fr.blackbalrog.oneblock.listeners.blocks.RegenBlockListener;
import fr.blackbalrog.oneblock.listeners.island.protections.IslandProtectionListener;
import fr.blackbalrog.oneblock.listeners.island.protections.PistonProtectionListener;
import fr.blackbalrog.oneblock.world.generator.AsyncOneBlockWorld;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class OneBlock extends JavaPlugin
{
	
	private static OneBlock instance;
	
	private ConfigurationManager configurationManager;
	private DefaultConfiguration configuration;
	
	private String prefix;
	
	private Console console;
	
	private Database database;
	private IslandDatabase islandDatabase;
	
	private SessionManager sessionManager;
	
	private IslandsRegenBlock islandsRegenBlock;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		this.configurationManager = new ConfigurationManager();
		this.configurationManager.add(this.configuration = new DefaultConfiguration(this, "Configuration.yml"));
		
		this.prefix = this.configuration.getString("Prefix").replaceAll("&", "§");
		
		this.console = new Console(this.prefix);
		
		new AsyncOneBlockWorld().initWorld(Bukkit.getServer().getConsoleSender());
		
		this.database = new Database();
		this.database.connect();
		
		this.sessionManager = new SessionManager();
		this.islandDatabase = new IslandDatabase();
		
		if (this.database.isDisconnected())
		{
			this.console.setError("Le OneBlock n'est pas connecter à la base de donnée");
			return;
		}
		
		this.islandDatabase.createTable();
		
		// INIT
		Bukkit.getOnlinePlayers().forEach(player -> {
			
			UUID uuidPlayer = player.getUniqueId();
			
			this.sessionManager.createSessionPlayer(uuidPlayer);
			this.sessionManager.createSessionIsland(uuidPlayer);
			
			// LOAD ISLAND SESSION
			this.islandDatabase.loadIsland(uuidPlayer);
		});
		
		this.islandsRegenBlock = new IslandsRegenBlock();
		this.islandsRegenBlock.rebuild();
		
		this.onCommands();
		this.onListeners();
		
		this.console.setEnable("Allumer");
		
	}
	
	@Override
	public void onDisable()
	{
		this.database.disconnect();
		this.console.setDisable("Eteint");
	}
	
	private void onCommands()
	{
		CommandOneBlock handler = new CommandOneBlock();
		
		// PLAYER
		handler.registerPlayer(new SubCommandCreateIsland());
		handler.registerPlayer(new SubCommandDeleteIsland());
		handler.registerPlayer(new SubCommandHomeIsland());
		handler.registerPlayer(new SubCommandSpawn());
		
		// ADMIN
		handler.registerAdmin(new SubCommandWorld());
		handler.registerAdmin(new SubCommandSetSpawn());
		handler.registerAdmin(new SubCommandReload());
		handler.registerAdmin(new SubCommandIslandAccess());
		handler.registerAdmin(new SubCommandIslandLevel());
		
		this.getCommand("oneblock").setExecutor(handler);
		this.getCommand("oneblock").setTabCompleter(handler);
	}
	
	private void onListeners()
	{
		var pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new RegenBlockListener(), this);
		pluginManager.registerEvents(new IslandProtectionListener(), this);
		pluginManager.registerEvents(new PlayerJoinListener(), this);
		pluginManager.registerEvents(new PlayerQuitListener(), this);
		pluginManager.registerEvents(new TeleportBorderListener(), this);
		pluginManager.registerEvents(new PistonProtectionListener(), this);
	}
	
	public ConfigurationManager getConfigurationManager()
	{
		return this.configurationManager;
	}
	
	public DefaultConfiguration getConfiguration()
	{
		return this.configuration;
	}
	
	public String getPrefix()
	{
		return this.prefix;
	}
	
	public Console getConsole()
	{
		return this.console;
	}
	
	public Database getDatabase()
	{
		return this.database;
	}
	
	public IslandDatabase getDatabasePlayer() { return this.islandDatabase; }
	
	public SessionManager getSessionManager()
	{
		return this.sessionManager;
	}
	
	public IslandsRegenBlock getIslandMapRegenBlock()
	{
		return this.islandsRegenBlock;
	}
	
	public static OneBlock getInstance()
	{
		return instance;
	}
	
}
