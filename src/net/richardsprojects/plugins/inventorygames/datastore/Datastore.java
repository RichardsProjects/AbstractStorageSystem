package net.richardsprojects.plugins.inventorygames.datastore;

import java.util.UUID;

/**
 * A simple abstract class that contains all the methods that an InventoryGames
 * plugin datastore needs to contain.
 *
 * @author RichardB122
 * @version 3/23/16
 */
public abstract class Datastore {

	/**
	 * Initializes the datastore type. Should be run in the onEnable method of
	 * the plugin.
	 *
	 * @return if the initialization was successful or not
	 */
	public abstract boolean initalize();

	/**
	 * Updates both the 2048 highscore of the player and their name based on
	 * the provided UUID.
	 *
	 * @param player UUID of player
	 * @param name player's name as a String
	 * @param score the new highscore
	 * @return if the update was successful
	 */
	public abstract boolean updateHighscore(UUID player, String name, int score);

	/**
	 * Get's the highscore of the player based on the provided UUID. Returns 0
	 * if there is no saved highscore.
	 *
	 * @param player player's UUID
	 * @return the player's highscore or 0
	 */
	public abstract int getHighscore(UUID player);

	/**
	 * Get's the highscore of the player on the provided name. It does this by
	 * resolving the username to UUID and running getHighscore(UUID). This
	 * method returns 0 if the specified username is not in the records.
	 *
	 * @param player the player's name
	 * @return the player's highscore or 0.
	 */
	public abstract int getHighscore(String player);

	/**
	 * This method creates a String with the leaderboard of highscores in 2048.
	 * Each entry is separated by a greater than sign (>) and each the username
	 * and score is separated by a comma (,). There will be a max of 10 entries
	 * returned.
	 *
	 * @return the scoreboard String
	 */
	public abstract String getLeaderboard();

	/**
	 * Updates the player's Tic Tac Wins and their username based on the
	 * provided UUID.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param value the new win count
	 * @return whether the operation was successful or not
	 */
	public abstract boolean updateTicTacToeWins(UUID player, String name, int value);

	/**
	 * Returns the player's win count in Tic Tac Toe or 0 if there are no win
	 * information for the provided UUID.
	 *
	 * @param player player's UUID
	 * @return win count or 0
	 */
	public abstract int getTicTacToeWins(UUID player);

	/**
	 * Get's the tic tac wins of the player based on the provided name. First
	 * attempts to get the UUID of the provided player name. If no UUID can be
	 * resolved to the provided player name the method returns 0, otherwise it
	 * returns getTicTacToeWins(UUID).
	 *
	 * @param player player's name
	 * @return their win count or 0
	 */
	public abstract int getTicTacToeWins(String player);

	/**
	 * Update's the player's loss count and username based on the provided
	 * UUID. Returns whether the operation was successful or not.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param value new loss count
	 * @return whether the operation was successful or not
	 */
	public abstract boolean updateTicTacToeLosses(UUID player, String name, int value);

	/**
	 * Gets the number of losses the player has by the specified UUID. Returns
	 * 0 if there are no records attached to that player UUID.
	 *
	 * @param player player's UUID
	 * @return their loss count or 0
	 */
	public abstract int getTicTacToeLosses(UUID player);

	/**
	 * Get's the tic tac losses of the player based on the provided name. First
	 * attempts to get the UUID of the provided player name. If no UUID can be
	 * resolved to the provided player name the method returns 0, otherwise it
	 * returns getTicTacToeLosses(UUID).
	 *
	 * @param player player's name
	 * @return tic tac loss count or 0
	 */
	public abstract int getTicTacToeLosses(String player);

	/**
	 * Closes connections and saves all pending data. Should be run in the
	 * onDisable method of the plugin.
	 */
	public abstract void onDisable();
	
}
