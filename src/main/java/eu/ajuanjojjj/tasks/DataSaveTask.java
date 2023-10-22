package eu.ajuanjojjj.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import eu.ajuanjojjj.PlayerTimeLimit;

public class DataSaveTask {

	private PlayerTimeLimit plugin;
	private boolean end;

	public DataSaveTask(PlayerTimeLimit plugin) {
		this.plugin = plugin;
		this.end = false;
	}

	public void end() {
		end = true;
	}

	public void start(int minutes) {
		long ticks = minutes * 60 * 20;

		new BukkitRunnable() {
			@Override
			public void run() {
				if (end) {
					this.cancel();
				} else {
					execute();
				}
			}

		}.runTaskTimerAsynchronously(plugin, 0L, ticks);
	}

	public void execute() {
		plugin.getConfigsManager().getPlayerConfigsManager().guardarJugadores();
		plugin.getServerManager().saveDataTime();
	}
}
