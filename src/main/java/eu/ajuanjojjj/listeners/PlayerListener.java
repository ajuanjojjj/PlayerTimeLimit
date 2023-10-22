package eu.ajuanjojjj.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import eu.ajuanjojjj.PlayerTimeLimit;
import eu.ajuanjojjj.configs.MainConfigManager;
import eu.ajuanjojjj.managers.MensajesManager;
import eu.ajuanjojjj.managers.PlayerManager;
import eu.ajuanjojjj.model.TimeLimitPlayer;

public class PlayerListener implements Listener {

	private PlayerTimeLimit plugin;

	public PlayerListener(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPreJoin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId());
		if (p != null) {
			// Si entra al server y esta activada la world whitelist no se le
			// expulsa al intentar entrar al server
			if (plugin.getConfigsManager().getMainConfigManager().isWorldWhitelistEnabled()) {
				return;
			}

			if (!playerManager.hasTimeLeft(p)) {
				FileConfiguration messages = plugin.getMessages();
				List<String> msg = messages.getStringList("joinErrorMessage");
				String finalMessage = "";
				for (String line : msg) {
					finalMessage = finalMessage + line + "\n";
				}
				finalMessage = MensajesManager.getMensajeColor(finalMessage);
				event.disallow(Result.KICK_OTHER, finalMessage);
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId());
		if (p == null) {
			p = playerManager.createPlayer(player);
		}
		p.setPlayer(player);
		p.setName(player.getName());

		FileConfiguration config = plugin.getConfig();
		if (config.getString("update_notification").equals("true")) {
			if (player.isOp() && !(plugin.version.equals(plugin.latestversion))) {
				player.sendMessage(
						PlayerTimeLimit.nombrePlugin + ChatColor.RED + " There is a new version available. "
								+ ChatColor.YELLOW +
								"(" + ChatColor.GRAY + plugin.latestversion + ChatColor.YELLOW + ")");
				player.sendMessage(ChatColor.RED + "You can download it at: " + ChatColor.GREEN
						+ "https://www.spigotmc.org/resources/96577/");
			}
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId());
		if (p != null) {
			p.setPlayer(null);
			p.eliminarBossBar();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event) {
		TeleportCause cause = event.getCause();
		if (cause.equals(TeleportCause.PLUGIN) || cause.equals(TeleportCause.COMMAND)) {
			Player player = event.getPlayer();
			PlayerManager playerManager = plugin.getPlayerManager();
			TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId());
			if (p == null) {
				return;
			}

			MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
			if (!mainConfig.isWorldWhitelistEnabled()) {
				return;
			}

			// Revisar si el mundo donde va esta activado
			World worldTo = event.getTo().getWorld();
			List<String> worlds = mainConfig.getWorldWhitelistWorlds();
			if (!worlds.contains(worldTo.getName())) {
				return;
			}

			// Revisar si se le ha acabado el tiempo
			if (!playerManager.hasTimeLeft(p)) {
				FileConfiguration messages = plugin.getMessages();
				List<String> msg = messages.getStringList("joinErrorMessage");
				for (String m : msg) {
					player.sendMessage(MensajesManager.getMensajeColor(m));
				}
				event.setCancelled(true);
				return;
			}
		}
	}
}
