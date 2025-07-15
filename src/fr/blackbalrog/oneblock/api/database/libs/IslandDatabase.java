package fr.blackbalrog.oneblock.api.database.libs;

import fr.blackbalrog.oneblock.OneBlock;
import fr.blackbalrog.oneblock.api.message.Console;
import fr.blackbalrog.oneblock.api.sessions.SessionIsland;
import fr.blackbalrog.oneblock.api.sessions.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class IslandDatabase
{
	
	private final Connection connection = OneBlock.getInstance().getDatabase().getConnection();
	private SessionManager sessionManager = OneBlock.getInstance().getSessionManager();
	private Console console = OneBlock.getInstance().getConsole();
	
	/**
	 * Crée la table islands si elle n'existe pas déjà,
	 * en ajoutant un champ regen_location pour le bloc de régénération.
	 */
	public void createTable()
	{
		String sql = "CREATE TABLE IF NOT EXISTS islands (" +
				" owner_uuid       VARCHAR(36) PRIMARY KEY, " +
				" island_name      VARCHAR(255) NOT NULL, " +
				" level            INT NOT NULL, " +
				" spawn_location   TEXT NOT NULL, " +
				" regen_location   TEXT NOT NULL, " +
				" members          TEXT NOT NULL" +
				");";
		try (Statement statement = this.connection.createStatement())
		{
			statement.executeUpdate(sql);
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
		}
	}
	
	/**
	 * Insert ou met à jour une île, en serialisant spawn_location et regen_location en JSON.
	 */
	public void upsertIsland(SessionIsland island)
	{
		String sql = "REPLACE INTO islands " +
				"(owner_uuid, island_name, level, spawn_location, regen_location, members) " +
				"VALUES (?, ?, ?, ?, ?, ?);";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql))
		{
			// owner, name, level
			preparedStatement.setString(1, island.getOwner().getUniqueId().toString());
			preparedStatement.setString(2, island.getIslandName());
			preparedStatement.setInt(3, island.getLevel());
			
			// spawn_location JSON
			Location spawn = island.getLocationSpawnIsland();
			JSONObject spawnJson = new JSONObject()
					.put("world", spawn.getWorld().getName())
					.put("x", spawn.getX())
					.put("y", spawn.getY())
					.put("z", spawn.getZ())
					.put("yaw", spawn.getYaw())
					.put("pitch", spawn.getPitch());
			preparedStatement.setString(4, spawnJson.toString());
			
			// regen_location JSON (nullable)
			Location regen = island.getLocationBlockRegen();
			if (regen != null)
			{
				JSONObject regenJson = new JSONObject()
						.put("world", regen.getWorld().getName())
						.put("x", regen.getX())
						.put("y", regen.getY())
						.put("z", regen.getZ());
				preparedStatement.setString(5, regenJson.toString());
			}
			else
			{
				preparedStatement.setNull(5, Types.VARCHAR);
			}
			
			// members CSV
			List<UUID> membersList = island.getMembersIsland();
			if (membersList != null && !membersList.isEmpty())
			{
				String members = String.join(",", membersList.stream()
						.map(UUID::toString)
						.toList());
				
				preparedStatement.setString(6, members);
			}
			else
			{
				preparedStatement.setNull(6, Types.VARCHAR);
			}
			preparedStatement.executeUpdate();
		}
		catch (SQLException exeption)
		{
			exeption.printStackTrace();
		}
	}
	
	/**
	 * Charge une île et restaure spawn_location et regen_location.
	 */
	// IslandDatabase.java
	
	public SessionIsland loadSessionIsland(UUID ownerUuid) {
		String sql = "SELECT island_name, level, spawn_location, regen_location, members " +
				"FROM islands WHERE owner_uuid = ?;";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, ownerUuid.toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) return null;
				
				// Récupère (ou crée) la session
				SessionIsland island = sessionManager.getSessionIslandMap().get(ownerUuid);
				if (island == null) {
					island = new SessionIsland();
					island.setOwner(ownerUuid);
					sessionManager.getSessionIslandMap().put(ownerUuid, island);
				}
				
				island.setIslandName(rs.getString("island_name"));
				island.setLevel(rs.getInt("level"));
				
				// --- spawn_location ---
				String spawnStr = rs.getString("spawn_location");
				World spawnWorld = null;
				double sx = 0, sy = 0, sz = 0;
				float syaw = 0, spitch = 0;
				
				if (spawnStr != null && spawnStr.trim().startsWith("{")) {
					// JSON
					JSONObject json = new JSONObject(spawnStr);
					String worldName = json.getString("world");
					spawnWorld = Bukkit.getServer().getWorld(worldName);
					if (spawnWorld == null) {
						// fallback insensitive
						spawnWorld = Bukkit.getServer().getWorlds().stream()
								.filter(w -> w.getName().equalsIgnoreCase(worldName))
								.findFirst().orElse(null);
					}
					sx = json.getDouble("x");
					sy = json.getDouble("y");
					sz = json.getDouble("z");
					syaw   = (float) json.getDouble("yaw");
					spitch = (float) json.getDouble("pitch");
				} else if (spawnStr != null) {
					// Legacy Location.toString()
					// Ex: "World: OneBlock, X:0.0, Y:100.0, Z:0.0, Yaw:0.0, Pitch:0.0"
					String[] parts = spawnStr.split(",\\s*");
					Map<String,String> map = new HashMap<>();
					for (String p : parts) {
						String[] kv = p.split(":\\s*", 2);
						if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
					}
					String worldName = map.get("World");
					spawnWorld = Bukkit.getServer().getWorld(worldName);
					if (spawnWorld == null) {
						spawnWorld = Bukkit.getServer().getWorlds().stream()
								.filter(w -> w.getName().equalsIgnoreCase(worldName))
								.findFirst().orElse(null);
					}
					sx = Double.parseDouble(map.get("X"));
					sy = Double.parseDouble(map.get("Y"));
					sz = Double.parseDouble(map.get("Z"));
					syaw   = Float.parseFloat(map.get("Yaw"));
					spitch = Float.parseFloat(map.get("Pitch"));
				}
				
				if (spawnWorld == null) {
					console.setError("Monde introuvable pour spawn_location: " + spawnStr);
					return null;
				}
				island.setSpawnLocation(spawnWorld, sx, sy, sz, syaw, spitch);
				
				
				// --- regen_location (uniquement JSON) ---
				String regenStr = rs.getString("regen_location");
				if (regenStr != null && regenStr.trim().startsWith("{")) {
					JSONObject json = new JSONObject(regenStr);
					String worldName = json.getString("world");
					World regenWorld = Bukkit.getServer().getWorld(worldName);
					if (regenWorld == null) {
						regenWorld = Bukkit.getServer().getWorlds().stream()
								.filter(w -> w.getName().equalsIgnoreCase(worldName))
								.findFirst().orElse(null);
					}
					if (regenWorld != null) {
						island.setLocationBlockRegen(new Location(
								regenWorld,
								json.getDouble("x"),
								json.getDouble("y"),
								json.getDouble("z")
						));
					} else {
						console.setError("Monde introuvable pour regen_location: " + worldName);
					}
				}
				
				
				// --- membres ---
				String membersCsv = rs.getString("members");
				if (membersCsv != null && !membersCsv.isEmpty()) {
					List<UUID> members = new ArrayList<>();
					for (String s : membersCsv.split(",")) {
						members.add(UUID.fromString(s));
					}
					island.setMembersIsland(members);
				}
				
				return island;
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return null;
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
		try (Statement stmt = this.connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT owner_uuid FROM islands"))
		{
			while (rs.next())
			{
				uuids.add(UUID.fromString(rs.getString("owner_uuid")));
			}
		}
		catch (SQLException exeption)
		{
			throw new RuntimeException(exeption);
		}
		return uuids;
	}
	
}
