package net.richardsprojects.plugins.lobbygames.commands;

import net.md_5.bungee.api.ChatColor;
import net.richardsprojects.plugins.lobbygames.LobbyGames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is a simple CommandExecutor for the example command. Its purpose is to
 * demonstrate how calls should be made to the datastore asynchronously
 * to not hold up the main thread and how these calls should work regardless of
 * what storage type is being used.
 *
 * @author RichardB122
 * @version 3/26/17
 */
public class ExampleCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
							 String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			new PrintLeaderboardTask(player).runTaskAsynchronously(LobbyGames.instance);
		}

		return false;
	}

	private class PrintLeaderboardTask extends BukkitRunnable {

		private Player player;

		private PrintLeaderboardTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			// load leaderboard data
			String leaderboard = LobbyGames.instance.getDatastore().getLeaderboard();
			String[] leaderboardArray = leaderboard.split(">");

			// print leaderboard
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leaderboard: ");
			for (int i = 0; i < leaderboardArray.length; i++) {
				String tmp = leaderboardArray[i];
				String[] tmpArray = tmp.split(",");
				player.sendMessage(tmpArray[0] + " - " + tmpArray[1]);
			}
		}
	}
}
