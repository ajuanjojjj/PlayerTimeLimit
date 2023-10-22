package eu.ajuanjojjj.api;

import org.bukkit.entity.Player;

import eu.ajuanjojjj.PlayerTimeLimit;
import eu.ajuanjojjj.managers.PlayerManager;
import eu.ajuanjojjj.model.TimeLimitPlayer;
import eu.ajuanjojjj.utils.UtilsTime;

public class PlayerTimeLimitAPI {

	private static PlayerTimeLimit plugin;

	public PlayerTimeLimitAPI(PlayerTimeLimit plugin) {
		PlayerTimeLimitAPI.plugin = plugin;
	}

	public static String getTimeLeft(Player player) {
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId());
		int timeLimit = playerManager.getTimeLimitPlayer(player);
		return playerManager.getTimeLeft(p, timeLimit);
	}

	public static String getTotalTime(Player player) {
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId());
		return UtilsTime.getTime(p.getTotalTime(), plugin.getMensajesManager());
	}
}
