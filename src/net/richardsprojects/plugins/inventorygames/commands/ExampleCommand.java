package net.richardsprojects.plugins.inventorygames.commands;

import net.md_5.bungee.api.ChatColor;
import net.richardsprojects.plugins.inventorygames.InventoryGames;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			final Player player = (Player) sender;

			// send Top 10 highscores
			Bukkit.getScheduler().runTaskAsynchronously(InventoryGames.instance, new Runnable() {
				@Override
				public void run() {
					String leaderboard = InventoryGames.instance.getDatastore().getLeaderboard();
					player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leaderboard: ");
					String[] leaderboardArray = leaderboard.split(">");
					for (int i = 0; i < leaderboardArray.length; i++) {
						String tmp = leaderboardArray[i];
						String[] tmpArray = tmp.split(",");
						player.sendMessage(tmpArray[0] + " - " + tmpArray[1]);
					}
				}
			});
		}

		return false;
	}

}
