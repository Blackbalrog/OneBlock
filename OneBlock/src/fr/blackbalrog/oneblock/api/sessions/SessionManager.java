package fr.blackbalrog.oneblock.api.sessions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager
{
	private Map<UUID, SessionPlayer> sessionPlayerMap;
	private Map<UUID, SessionIsland> sessionIslandMap;
	
	public SessionManager()
	{
		this.sessionPlayerMap = new HashMap<>();
		this.sessionIslandMap = new HashMap<>();
	}
	
	public void createSessionPlayer(UUID playerUUID)
	{
		if (!this.sessionPlayerMap.containsKey(playerUUID)) this.sessionPlayerMap.put(playerUUID, new SessionPlayer());
	}
	
	public void removeSessionPlayer(UUID playerUUID)
	{
		this.sessionPlayerMap.remove(playerUUID);
	}
	
	public SessionPlayer getSessionPlayer(UUID playerUUID)
	{
		return this.sessionPlayerMap.get(playerUUID);
	}
	
	public Map<UUID, SessionPlayer> getSessionPlayerMap()
	{
		return this.sessionPlayerMap;
	}
	
	public void createSessionIsland(UUID playerUUID)
	{
		if (!this.sessionIslandMap.containsKey(playerUUID)) this.sessionIslandMap.put(playerUUID, new SessionIsland());
	}
	
	public void removeSessionIsland(UUID playerUUID)
	{
		this.sessionIslandMap.remove(playerUUID);
	}
	
	public SessionIsland getSessionIsland(UUID playerUUID)
	{
		return this.sessionIslandMap.get(playerUUID);
	}
	
	public Map<UUID, SessionIsland> getSessionIslandMap()
	{
		return this.sessionIslandMap;
	}
}
