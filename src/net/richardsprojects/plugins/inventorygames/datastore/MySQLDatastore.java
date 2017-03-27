package net.richardsprojects.plugins.inventorygames.datastore;

import java.util.UUID;

/**
 * This class is an implementation of the InventoryGames plugin datastore for
 * the MySQL server and mostly a wrapper class for SQLManager.
 *
 * @author RichardB122
 * @version 3/24/17
 */
public class MySQLDatastore extends Datastore {

	private SQLManager sql;

	/**
	 * Setups the connection to the MySQL database.
	 */
	public MySQLDatastore() {
		sql = new SQLManager();
	}

	/**
	 * Opens the initial database connection and checks for the needed tables.
	 * If they can't be found it adds them.
	 *
	 * @return whether it was successful or not.
	 */
	@Override
	public boolean initalize() {
		return sql.checkTables();
	}

	/**
	 * @see SQLManager#getHighscore(UUID)
	 */
	@Override
	public int getHighscore(UUID player) {
		return sql.getHighscore(player);
	}

	/**
	 * @see SQLManager#getLeaderboard()
	 */
	@Override
	public String getLeaderboard() {
		return sql.getLeaderboard();
	}

	/**
	 * @see SQLManager#getTicTacToeWins(UUID)
	 */
	@Override
	public int getTicTacToeWins(UUID player) {
		return sql.getTicTacToeWins(player);
	}

	/**
	 * @see SQLManager#getTicTacToeLosses(UUID)
	 */
	@Override
	public int getTicTacToeLosses(UUID player) {
		return sql.getTicTacToeLosses(player);
	}

	/**
	 * @see SQLManager#updateHighscore(UUID, String, int)
	 */
	@Override
	public boolean updateHighscore(UUID player, String name, int score) {
		return sql.updateHighscore(player, name, score);
	}

	/**
	 * @see SQLManager#getHighscore(String)
	 */
	@Override
	public int getHighscore(String player) {
		return sql.getHighscore(player);
	}

	/**
	 * @see SQLManager#updateTickTackToeWins(UUID, String, int)
	 */
	@Override
	public boolean updateTicTacToeWins(UUID player, String name, int value) {
		return sql.updateTickTackToeWins(player, name, value);
	}

	/**
	 * @see SQLManager#getTicTacToeWins(String)
	 */
	@Override
	public int getTicTacToeWins(String player) {
		return sql.getTicTacToeWins(player);
	}

	/**
	 * @see SQLManager#updateTickTackToeLosses(UUID, String, int)
	 */
	@Override
	public boolean updateTicTacToeLosses(UUID player, String name, int value) {
		return sql.updateTickTackToeLosses(player, name, value);
	}

	/**
	 * @see SQLManager#getTicTacToeLosses(String)
	 */
	@Override
	public int getTicTacToeLosses(String player) {
		return sql.getTicTacToeLosses(player);
	}

	/**
	 * Closes all connections in the connection pool. Should be called in the
	 * onDisable method of the plugin.
	 */
	@Override
	public void onDisable() {
		sql.onDisable();		
	}
}
