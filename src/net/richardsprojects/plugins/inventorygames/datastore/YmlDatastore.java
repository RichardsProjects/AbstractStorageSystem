package net.richardsprojects.plugins.inventorygames.datastore;

import net.richardsprojects.plugins.inventorygames.InventoryGames;
import net.richardsprojects.plugins.inventorygames.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * This class is an implementation of the InventoryGames datastore system in
 * YML.
 *
 * @author RichardB122
 * @version 3/24/17
 */
public class YmlDatastore extends Datastore {

	private HashMap<UUID, String> names = new HashMap<UUID, String>();
	private HashMap<UUID, Integer> losses = new HashMap<UUID, Integer>();
	private HashMap<UUID, Integer> wins = new HashMap<UUID, Integer>();
	private HashMap<UUID, Integer> highscores = new HashMap<UUID, Integer>();

	private File highscoresFile;
	private File ticTacToeFile;
	private File uuidsFile;

	private SaveTask saveTask;

	public boolean uuidsNeedsUpdate = false;
	public boolean ticTacToeNeedsUpdate = false;
	public boolean highscoresNeedsUpdate = false;

	/**
	 * Checks for the existence of tictactoe.yml, highscores.yml and uuids.yml
	 * and creates them if they do not exist. Loads highscores, tic tac toe
	 * stats and uuids into memory and starts a background task to save changes
	 * every 30 seconds. Returns false if anything fails.
	 *
	 * @return whether it succeeded or not.
	 */
	@Override
	public boolean initalize() {
		if (!checkFiles()) return false;
		if (!loadHighscores()) return false;
		if (!loadTicTacToe()) return false;

		saveTask = new SaveTask(this, true);
		saveTask.runTaskTimerAsynchronously(InventoryGames.instance, 600, 600);

		return loadUUIDs();
	}

	/**
	 * This method attempts to load the highscore information from
	 * highscores.yml and returns whether the operation failed or succeeded.
	 *
	 * @return whether or not it was successful
	 */
	private boolean loadHighscores() {
		try {
			YamlConfiguration highscoresYML = new YamlConfiguration();
			highscoresYML.load(highscoresFile);

			for (String key : highscoresYML.getKeys(true)) {
				UUID uuid = Utils.parseUUID(key);
				if (uuid != null) {
					int score = highscoresYML.getInt(key);
					highscores.put(uuid, score);
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * This method attempts to load the UUID to username information from
	 * uuids.yml and returns whether the operation failed or succeeded.
	 *
	 * @return whether or not it was successful
	 */
	private boolean loadUUIDs() {
		try {
			YamlConfiguration uuidsYML = new YamlConfiguration();
			uuidsYML.load(uuidsFile);

			for (String key : uuidsYML.getKeys(true)) {
				UUID uuid = Utils.parseUUID(key);
				if (uuid != null) {
					String name = uuidsYML.getString(key);
					names.put(uuid, name);
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * This method attempts to load the highscore information from
	 * highscores.yml and returns whether the operation failed or succeeded.
	 *
	 * @return whether or not it was successful
	 */
	private boolean loadTicTacToe() {
		try {
			YamlConfiguration ticTacToeYML = new YamlConfiguration();
			ticTacToeYML.load(ticTacToeFile);

			for (String key : ticTacToeYML.getKeys(true)) {
				UUID uuid = Utils.parseUUID(key);
				if (uuid != null) {
					// parse resulting wins and losses
					boolean sucessfullyParsed = true;
					String data = ticTacToeYML.getString(key);
					String[] split = data.split("-");
					int losses = 0;
					int wins = 0;
					if (split.length == 2) {
						try {
							wins = Integer.parseInt(split[0]);
							losses = Integer.parseInt(split[1]);
						} catch (Exception e) {
							sucessfullyParsed = false;
						}
					} else {
						sucessfullyParsed = false;
					}

					if (sucessfullyParsed) {
						this.losses.put(uuid, losses);
						this.wins.put(uuid, wins);
					} else {
						String msg = "[InventoryGames] There was an error reading ";
						msg = msg + key + " from tictactoe.yml.";
						InventoryGames.instance.log.info(msg);
					}
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Helper method that attempts to get the UUID of a player from the HashMap
	 * based on the provided name. Will return null if it could not find one.
	 *
	 * @param name the player name
	 * @return the player's UUID based on the HashMap
	 */
	private UUID getUUID(String name) {
		UUID uuid = null;

		if (names.containsValue(name)) {
			for (UUID cUUID : names.keySet()) {
				if (names.get(cUUID).equals(name)) {
					uuid = cUUID;
				}
			}
		}

		return uuid;
	}

	/**
	 * Helper method that attempts to get the name of a player from the HashMap
	 * based on the provided UUID. Will return null if it could not find one.
	 *
	 * @param uuid the player's uuid
	 * @return the player's UUID based on the HashMap
	 */
	private String getName(UUID uuid) {
		String name = null;

		if (names.containsKey(uuid)) {
			name = names.get(uuid);
		}

		return name;
	}

	/**
	 * A simple method that checks for the existence of the required YML files
	 * and attempts to create them if they don't exist. If the method fails to
	 * create them it returns false.
	 *
	 * @return whether or not the method was successful or not
	 */
	private boolean checkFiles() {
		// create highscores file
		String path = InventoryGames.dataFolder.toString() + File.separator + "highscores.yml";
		highscoresFile = new File(path);
		if (!highscoresFile.exists()) {
			try {
				highscoresFile.createNewFile();
			} catch (Exception e) {
				return false;
			}
		}

		// create tictactoe file
		path = InventoryGames.dataFolder.toString() + File.separator + "tictactoe.yml";
		ticTacToeFile = new File(path);
		if (!ticTacToeFile.exists()) {
			try {
				ticTacToeFile.createNewFile();
			} catch (Exception e) {
				return false;
			}
		}

		// create UUID's file
		path = InventoryGames.dataFolder.toString() + File.separator + "uuids.yml";
		uuidsFile = new File(path);
		if (!uuidsFile.exists()) {
			try {
				uuidsFile.createNewFile();
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @see Datastore#updateHighscore(UUID, String, int)
	 */
	@Override
	public boolean updateHighscore(UUID player, String name, int score) {
		if (highscores.containsKey(player)) {
			highscores.remove(player);
		}
		highscores.put(player, score);

		if (names.containsKey(player)) {
			names.remove(player);
		}
		names.put(player, name);

		highscoresNeedsUpdate = true;
		uuidsNeedsUpdate = true;

		return true;
	}

	/**
	 * @see Datastore#getHighscore(UUID)
	 */
	@Override
	public int getHighscore(UUID player) {
		if (highscores.containsKey(player)) {
			return highscores.get(player);
		} else {
			return 0;
		}
	}

	/**
	 * @see Datastore#getHighscore(String)
	 */
	@Override
	public int getHighscore(String player) {
		UUID uuid = getUUID(player);

		if (uuid != null) {
			return getHighscore(uuid);
		} else {
			return 0;
		}
	}


	/**
	 * This method creates a String with the leaderboard of highscores in 2048.
	 * Each entry is separated by a greater than sign (>) and each the username
	 * and score is separated by a comma (,). There will be a max of 10 entries
	 * returned.
	 *
	 * @return the scoreboard String
	 */
	@Override
	public String getLeaderboard() {
		if (highscores.size() < 1) {
			return "";
		}

		String leaderboard = "";
		List<Integer> list = new ArrayList<Integer>(highscores.values());
		Collections.sort(list, Collections.reverseOrder());

		List<Integer> sortedList;
		if (list.size() < 10) {

			sortedList = list.subList(0, list.size());
		} else {
			sortedList = list.subList(0, 9);
		}

		for (int i = 0; i < sortedList.size(); i++) {
			int score = sortedList.get(i);

			if ((Utils.countOfValue(sortedList, score) > 1 &&
					!leaderboard.contains(score + "")) ||
					Utils.countOfValue(sortedList, score) == 1) {
				// get the list of people with that score
				String pName = "";
				for (UUID uuid : highscores.keySet()) {
					if (highscores.get(uuid) == score) {
						pName = pName + " & " + getName(uuid);
					}
				}
				if (pName.length() > 2) {
					pName = pName.substring(3);
				}

				leaderboard = leaderboard + ">" + pName + "," + score;
			}
		}

		if (leaderboard.length() > 0) leaderboard = leaderboard.substring(1);
		return leaderboard;
	}

	/**
	 * @see Datastore#updateTicTacToeWins(UUID, String, int)
	 */
	@Override
	public boolean updateTicTacToeWins(UUID player, String name, int value) {
		if (wins.containsKey(player)) {
			wins.remove(player);
		}
		wins.put(player, value);

		if (names.containsKey(player)) {
			names.remove(player);
		}
		names.put(player, name);

		ticTacToeNeedsUpdate = true;
		uuidsNeedsUpdate = true;

		return true;
	}

	/**
	 * @see Datastore#getTicTacToeWins(UUID)
	 */
	@Override
	public int getTicTacToeWins(UUID player) {
		if (wins.containsKey(player)) {
			return wins.get(player);
		} else {
			return 0;
		}
	}

	/**
	 * @see Datastore#getTicTacToeWins(String)
	 */
	@Override
	public int getTicTacToeWins(String player) {
		UUID uuid = getUUID(player);

		if (uuid != null) {
			return getTicTacToeWins(uuid);
		} else {
			return 0;
		}
	}

	/**
	 * @see Datastore#updateTicTacToeLosses(UUID, String, int)
	 */
	@Override
	public boolean updateTicTacToeLosses(UUID player, String name, int value) {
		if (losses.containsKey(player)) {
			losses.remove(player);
		}
		losses.put(player, value);

		if (names.containsKey(player)) {
			names.remove(player);
		}
		names.put(player, name);

		ticTacToeNeedsUpdate = true;
		uuidsNeedsUpdate = true;

		return true;
	}

	/**
	 * @see Datastore#getTicTacToeLosses(UUID)
	 */
	@Override
	public int getTicTacToeLosses(UUID player) {
		if (losses.containsKey(player)) {
			return losses.get(player);
		} else {
			return 0;
		}
	}

	/**
	 * @see Datastore#getTicTacToeLosses(String)
	 */
	@Override
	public int getTicTacToeLosses(String player) {
		UUID uuid = getUUID(player);

		if (uuid != null) {
			return getTicTacToeLosses(uuid);
		} else {
			return 0;
		}
	}


	/**
	 * Simple method that handles cleanup for the YMLDatastore by saving
	 * everything to disk.
	 */
	@Override
	public void onDisable() {
		String msg = "[InventoryGames] Saving data to disk...";
		InventoryGames.instance.log.info(msg);
		saveTask.cancel();
		saveTask = new SaveTask(this, false);
		saveTask.run();
	}

	/**
	 * The goal of this method is to save all data to disk from the HashMaps
	 * and return whether or not it was successful.
	 *
	 * @param flag whether it should be saved regardless of anything being
	 *             updated
	 * @return whether or not the save was successful
	 *
	 */
	private boolean save(boolean flag) {
		if (uuidsNeedsUpdate || !flag) {
			if (!saveUUIDs()) {
				return false;
			}
		}

		if (highscoresNeedsUpdate || !flag) {
			if (!saveHighscores()) {
				return false;
			}
		}

		if (ticTacToeNeedsUpdate || !flag) {
			if (!saveTicTacToe()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Saves all the UUIDs and their corresponding names to uuids.yml based on
	 * the contents of the hashmaps. Returns false if an exception occurred,
	 * true otherwise.
	 *
	 * @return whether the operation was successful or not
	 */
	private boolean saveUUIDs() {
		try {
			YamlConfiguration uuidsYML = new YamlConfiguration();
			uuidsYML.load(uuidsFile);

			for (UUID uuid : names.keySet()) {
				String name = names.get(uuid);
				if (name != null) {
					uuidsYML.set(uuid.toString(), name);
				}
			}

			uuidsYML.save(uuidsFile);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Saves all the highscores in 2048 with the player's corresponding UUID in
	 * highscores.yml based on the contents of the hashmaps. Returns false if
	 * an exception occured and true if there were no problems.
	 *
	 * @return whether or not it was successful
	 */
	private boolean saveHighscores() {
		try {
			YamlConfiguration highscoresYML = new YamlConfiguration();
			highscoresYML.load(highscoresFile);

			for (UUID uuid : highscores.keySet()) {
				int highscore = highscores.get(uuid);
				highscoresYML.set(uuid.toString(), highscore);
			}

			highscoresYML.save(highscoresFile);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Saves the the tic tac toe wins and losses from HashMaps to the
	 * tictactoe.yml file.
	 *
	 * @return whether or not it was successful
	 */
	private boolean saveTicTacToe() {
		try {
			YamlConfiguration ticTacToeYML = new YamlConfiguration();
			ticTacToeYML.load(ticTacToeFile);

			for (UUID uuid : wins.keySet()) {
				int wins = this.wins.get(uuid);
				int losses = this.losses.get(uuid);

				String str = wins + "-" + losses;
				ticTacToeYML.set(uuid.toString(), str);
			}

			ticTacToeYML.save(ticTacToeFile);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * This is a small private subclass that adds BukkitRunnable capability to
	 * the save method so that it can be called every thirty seconds in order
	 * to save changes in the background.
	 *
	 * @author RichardB122
	 * @version 3/26/17
	 */
	private class SaveTask extends BukkitRunnable {

		private boolean onlySaveIfUpdated;
		private YmlDatastore storage;

		public SaveTask(YmlDatastore storage, boolean onlySaveIfUpdated) {
			this.storage = storage;
			this.onlySaveIfUpdated = onlySaveIfUpdated;
		}

		public void run() {
			storage.save(onlySaveIfUpdated);
		}
	}
}
