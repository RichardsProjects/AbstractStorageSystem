package net.richardsprojects.plugins.lobbygames;

import java.util.List;
import java.util.UUID;

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
			String msg = "[LobbyGames] There was an error parsing the UUID " + key;
			LobbyGames.instance.log.info(msg);
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
