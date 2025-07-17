package fr.blackbalrog.oneblock.api.database.libs;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.configurations.DefaultConfiguration;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class IslandDatabase
{
	
	private final Connection connection = OneBlock.getInstance().getDatabase().getConnection();
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private Console console = OneBlock.getInstance().getConsole();
	private DefaultConfiguration configuration = OneBlock.getInstance().getConfiguration();
	
	/**
	 * Crée la table islands si elle n'existe pas déjà
	 */
	public void createTable()
	{
		String sql = "CREATE TABLE IF NOT EXISTS islands ("
				+ " owner_uuid       VARCHAR(36) PRIMARY KEY, "
				+ " island_name      VARCHAR(255) NOT NULL, "
				+ " level            INT NOT NULL, "
				+ " center_location  TEXT NOT NULL, "
				+ " home_location    TEXT NOT NULL,"
				+ " regen_location   TEXT NOT NULL, "
				+ " members          TEXT NOT NULL"
				+ ");";
		try (Statement statement = this.connection.createStatement())
		{
			statement.executeUpdate(sql);
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
		}
	}
	
	public SessionIsland loadIsland(UUID ownerUuid)
	{
		
		String sql = "SELECT island_name, level, center_location, home_location, regen_location, members "
				+ "FROM islands WHERE owner_uuid = ?;";
		
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				
				SessionIsland sessionIsland = this.sessionManager.getSessionIsland(ownerUuid);
				
				if (!resultSet.next())
				{
					return sessionIsland;
				}
				
				sessionIsland.setOwner(ownerUuid);
				sessionIsland.setIslandName(resultSet.getString("island_name"));
				sessionIsland.setLevel(resultSet.getInt("level"));
				
				int level = sessionIsland.getLevel();
				int size = this.configuration.getInt("Island.level." + level + ".size");
				sessionIsland.setIslandSize(size);
				
				JSONObject center_location = new JSONObject(resultSet.getString("center_location"));
				World worldCenterIsland = Bukkit.getWorld(center_location.getString("world"));
				sessionIsland.setCenterLocation(
						worldCenterIsland,
						center_location.getDouble("x"),
						center_location.getDouble("y"),
						center_location.getDouble("z"),
						(float) center_location.getDouble("yaw"),
						(float) center_location.getDouble("pitch")
				);
				
				JSONObject homeLocation = new JSONObject(resultSet.getString("home_location"));
				World worldHomeIsland = Bukkit.getWorld(homeLocation.getString("world"));
				sessionIsland.setHomeLocation(new Location(
						worldHomeIsland,
						homeLocation.getDouble("x"),
						homeLocation.getDouble("y"),
						homeLocation.getDouble("z"),
						(float) homeLocation.getDouble("yaw"),
						(float) homeLocation.getDouble("pitch")
				));
				
				JSONObject regenBlockLocation = new JSONObject(resultSet.getString("regen_location"));
				World worldBlockRegen = Bukkit.getWorld(regenBlockLocation.getString("world"));
				sessionIsland.setLocationBlockRegen(new Location(
						worldBlockRegen,
						regenBlockLocation.getDouble("x"),
						regenBlockLocation.getDouble("y"),
						regenBlockLocation.getDouble("z")
				));
				
				String members = resultSet.getString("members");
				if (members != null && !members.isEmpty())
				{
					List<UUID> listMembers = Arrays.stream(members.split(","))
							.map(UUID::fromString)
							.collect(Collectors.toList());
					sessionIsland.setMembersIsland(listMembers);
				}
				/*
				console.setDebug("========== BASE DE DONNEE ==========");
				console.setDebug("Owner: " + ownerUuid);
				console.setDebug("Nom de l'île: " + sessionIsland.getIslandName());
				console.setDebug("Level: " + sessionIsland.getLevel());
				console.setDebug("Spawn Location: " + sessionIsland.getLocationSpawnIsland());
				console.setDebug("Block Regen Location: " + sessionIsland.getLocationBlockRegen());
				sessionIsland.getMembersIsland().forEach(uuid -> console.setDebug("Membre: " + uuid));
				*/
				return sessionIsland;
			}
		}
		catch (SQLException exception)
		{
			exception.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Insert ou met à jour une île, en serialisant spawn_location et regen_location en JSON.
	 */
	public void upsertIsland(SessionIsland sessionIsland)
	{
		String sql = "REPLACE INTO islands " +
				"(owner_uuid, island_name, level, center_location, home_location, regen_location, members) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?);";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, sessionIsland.getOwnerUuid().toString());
			preparedStatement.setString(2, sessionIsland.getIslandName());
			preparedStatement.setInt(3, sessionIsland.getLevel());
			
			Location centerLocation = sessionIsland.getCenterLocation();
			JSONObject centerJson = new JSONObject()
					.put("world", centerLocation.getWorld().getName())
					.put("x", centerLocation.getX())
					.put("y", centerLocation.getY())
					.put("z", centerLocation.getZ())
					.put("yaw", centerLocation.getYaw())
					.put("pitch", centerLocation.getPitch());
			preparedStatement.setString(4, centerJson.toString());
			
			Location homeLocation = sessionIsland.getHomeLocation();
			JSONObject homeJson = new JSONObject()
					.put("world", homeLocation.getWorld().getName())
					.put("x", homeLocation.getX())
					.put("y", homeLocation.getY())
					.put("z", homeLocation.getZ())
					.put("yaw", homeLocation.getYaw())
					.put("pitch", homeLocation.getPitch());
			preparedStatement.setString(5, homeJson.toString());
			
			Location regeneBlockLocation = sessionIsland.getLocationBlockRegen();
			JSONObject regenJson = new JSONObject()
					.put("world", regeneBlockLocation.getWorld().getName())
					.put("x", regeneBlockLocation.getX())
					.put("y", regeneBlockLocation.getY())
					.put("z", regeneBlockLocation.getZ());
			preparedStatement.setString(6, regenJson.toString());
			
			List<UUID> membersList = sessionIsland.getMembersIsland();
			if (membersList != null && !membersList.isEmpty())
			{
				String members = membersList.stream()
						.map(UUID::toString)
						.collect(Collectors.joining(","));
				preparedStatement.setString(7, members);
			}
			else
			{
				preparedStatement.setString(7, "");
			}
			
			preparedStatement.executeUpdate();
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
		}
	}
	
	/**
	 * Supprime une île et son index.
	 */
	public void deleteIsland(UUID ownerUuid)
	{
		String sql = "DELETE FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			preparedStatement.executeUpdate();
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
		}
	}
	
	public List<UUID> getAllOwnerUuids()
	{
		List<UUID> uuids = new ArrayList<>();
		try (Statement statement = this.connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT owner_uuid FROM islands"))
		{
			while (resultSet.next())
			{
				uuids.add(UUID.fromString(resultSet.getString("owner_uuid")));
			}
		}
		catch (SQLException exeption)
		{
			throw new RuntimeException(exeption);
		}
		return uuids;
	}
	
	/** Retourne le propriétaire (UUID) de l'île en base, ou null. */
	public UUID getOwner(UUID ownerUuid)
	{
		String sql = "SELECT owner_uuid FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				return resultSet.next() ? UUID.fromString(resultSet.getString("owner_uuid")) : null;
			}
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return null;
		}
	}
	
	/** Retourne le level de l'île en base, ou -1 si introuvable. */
	public int getLevel(UUID ownerUuid)
	{
		String sql = "SELECT level FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				return resultSet.next() ? resultSet.getInt("level") : -1;
			}
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return 0;
		}
	}
	
	/** Retourne la Location du spawn de l'île, ou null si introuvable. */
	public Location getCenterLocation(UUID ownerUuid)
	{
		String sql = "SELECT center_location FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				if (!resultSet.next()) return null;
				JSONObject spawnLocation = new JSONObject(resultSet.getString("spawn_location"));
				World world = Bukkit.getWorld(spawnLocation.getString("world"));
				return new Location(
						world,
						spawnLocation.getDouble("x"),
						spawnLocation.getDouble("y"),
						spawnLocation.getDouble("z"),
						(float) spawnLocation.getDouble("yaw"),
						(float) spawnLocation.getDouble("pitch")
				);
			}
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return null;
		}
	}
	
	public Location getHomeLocation(UUID ownerUuid)
	{
		String sql = "SELECT home_location FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				if (!resultSet.next()) return null;
				JSONObject homeLocation = new JSONObject(resultSet.getString("home_location"));
				World world = Bukkit.getWorld(homeLocation.getString("world"));
				return new Location(
						world,
						homeLocation.getDouble("x"),
						homeLocation.getDouble("y"),
						homeLocation.getDouble("z"),
						(float) homeLocation.getDouble("yaw"),
						(float) homeLocation.getDouble("pitch")
				);
			}
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return null;
		}
	}
	
	/** Retourne la Location du bloc de régénération, ou null si introuvable. */
	public Location getRegenLocation(UUID ownerUuid)
	{
		String sql = "SELECT regen_location FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				if (!resultSet.next()) return null;
				String regen = resultSet.getString("regen_location");
				if (regen == null || regen.isEmpty()) return null;
				JSONObject regenBlockLocation = new JSONObject(regen);
				World world = Bukkit.getWorld(regenBlockLocation.getString("world"));
				return new Location(
						world,
						regenBlockLocation.getDouble("x"),
						regenBlockLocation.getDouble("y"),
						regenBlockLocation.getDouble("z")
				);
			}
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return null;
		}
	}
	
	/** Retourne la liste des membres (UUID), ou vide si introuvable. */
	public List<UUID> getMembers(UUID ownerUuid)
	{
		String sql = "SELECT members FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				if (!resultSet.next()) return Collections.emptyList();
				String mem = resultSet.getString("members");
				if (mem == null || mem.isEmpty()) return Collections.emptyList();
				List<UUID> list = new ArrayList<>();
				for (String s : mem.split(",")) list.add(UUID.fromString(s));
				return list;
			}
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	/**
	 * Retourne le nom de l'île pour le propriétaire donné, ou null si introuvable.
	 */
	public String getIslandName(UUID ownerUuid)
	{
		String sql = "SELECT island_name FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			preparedStatement.setString(1, ownerUuid.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery())
			{
				return resultSet.next() ? resultSet.getString("island_name") : null;
			}
		} catch (SQLException exeption)
		{
			exeption.printStackTrace();
			return null;
		}
	}
}
