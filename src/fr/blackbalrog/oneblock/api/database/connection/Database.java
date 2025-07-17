package fr.blackbalrog.oneblock.api.database.connection;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.message.Console;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database
{
	private DatabaseReference reference;
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	private Connection connection;
	private Console console = OneBlock.getInstance().getConsole();
	
	public Database()
	{
		ConfigurationSection section = configuration.getConfigurationSection("Database");
		this.reference = new DatabaseReference(
				section.getString("hote"),
				section.getString("databaseName"),
				section.getString("username"),
				section.getString("password"),
				section.getInt("port")
		);
	}
	
	/**
	 * Établit la connexion à la base de données.
	 */
	public void connect()
	{
		if (this.connection != null) return;
		
		String url = String.format(
				"jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
				this.reference.hote(), this.reference.port(), this.reference.databaseName()
		);
		
		try
		{
			this.connection = DriverManager.getConnection(
					url,
					this.reference.username(),
					this.reference.password()
			);
			this.console.setDebug("Connecté à la base de données MySQL");
		}
		catch (SQLException exeption)
		{
			this.console.setError("Erreur de connexion à la base de données: §4" + exeption.getMessage());
			//exeption.printStackTrace();
		}
	}
	
	/**
	 * Ferme la connexion à la base de données
	 */
	public void disconnect()
	{
		if (this.connection != null)
		{
			try
			{
				this.connection.close();
				this.console.setDebug("Déconnecté de la base de données");
			}
			catch (SQLException exeption)
			{
				this.console.setError("Erreur lors de la déconnexion: " + exeption.getMessage());
				exeption.printStackTrace();
			}
			finally
			{
				this.connection = null;
			}
		}
	}
	
	/**
	 * Vérifie si la connexion est fermée ou n'a jamais été ouverte
	 */
	public boolean isDisconnected()
	{
		try
		{
			return this.connection == null || this.connection.isClosed();
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return true;
		}
	}
	
	/**
	 * Retourne la connexion active
	 */
	public Connection getConnection()
	{
		return this.connection;
	}
}
