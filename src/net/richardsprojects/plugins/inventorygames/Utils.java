package net.richardsprojects.plugins.inventorygames;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Important utility methods for the plugin.
 *
 * @author RichardB122
 * @version 3/26/17
 */
public class Utils {

	/**
	 * A helper method for parsing UUID's from Strings. It's main difference
	 * over the UUID.fromString() method is that it logs an error and returns
	 * null instead of throwing an exception.
	 *
	 * @param key the String to parse
	 * @return the UUID version or null
	 */
	public static UUID parseUUID(String key) {
		try {
			UUID uuid = UUID.fromString(key);
			return uuid;
		} catch (IllegalArgumentException e) {
			String msg = "[InventoryGames] There was an error parsing the UUID " + key;
			InventoryGames.instance.log.info(msg);
			return null;
		}
	}

	/**
	 * Small helper method designed to count the number of times a specified
	 * int appears in a list.
	 *
	 * @return number of times int appeared
	 */
	public static int countOfValue(List<Integer> list, int target) {
		int count = 0;

		for (int value : list) {
			if (target == value) {
				count++;
			}
		}

		return count;
	}
}
