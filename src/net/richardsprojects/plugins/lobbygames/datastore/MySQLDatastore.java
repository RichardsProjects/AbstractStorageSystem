package net.richardsprojects.plugins.lobbygames.datastore;

import net.richardsprojects.plugins.lobbygames.LobbyGames;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * This class is an implementation of the LobbyGames plugin datastore for
 * MySQL databases that uses HikariCP for connection management.
 *
 * @author RichardB122
 * @version 3/28/17
 */
public class MySQLDatastore extends Datastore {

	private final ConnectionPoolManager pool;
	private final String PREFIX = LobbyGames.instance.dbPrefix;

	/**
	 * Setups the connection to the MySQL database.
	 */
	public MySQLDatastore() {
		pool = new ConnectionPoolManager();
	}

	/**
	 * Updates a player's loss count in Tic Tac Toe and their username based on
	 * the provided UUID. Returns true if it succeeds false if it fails.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param losses new loss count
	 * @return whether it succeeded or failed
	 */
	public boolean updateTicTacToeLosses(UUID player, String name, int losses) {
		boolean success = true;

		String sql = "INSERT INTO " + PREFIX + "tictactoe VALUES (?, ?, 0, ?, 0) ON DUPLICATE KEY" +
				" UPDATE games_lost = ?, player = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			ps.setString(2, name);
			ps.setInt(3, losses);
			ps.setInt(4, losses);
			ps.setString(5, name);

			ps.executeUpdate();
		} catch (SQLException e) {
			success = false;
		}

		return success;
	}

	/**
	 * Updates a player's tie count in Tic Tac Toe and their username based on
	 * the provided UUID. Returns true if it succeeds false if it fails.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param ties new tie count
	 * @return whether it succeeded or failed
	 */
	@Override
	public boolean updateTicTacToeTies(UUID player, String name, int ties) {
		boolean success = true;

		String sql = "INSERT INTO " + PREFIX + "tictactoe VALUES (?, ?, 0, 0, ?) ON DUPLICATE KEY" +
				" UPDATE games_tied = ?, player = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			ps.setString(2, name);
			ps.setInt(3, ties);
			ps.setInt(4, ties);
			ps.setString(5, name);

			ps.executeUpdate();
		} catch (SQLException e) {
			success = false;
		}

		return success;
	}

	/**
	 * Opens the initial database connection and checks for the needed tables.
	 * If they can't be found it adds them.
	 *
	 * @return whether it was successful or not.
	 */
	@Override
	public boolean initalize() {
		return checkTables();
	}

	/**
	 * Gets the highscore of a player from the database based on the provided
	 * UUID. Returns 0 if they had no record in the database.
	 *
	 * @param player player's UUID
	 * @return player's highscore or 0 if no record was found.
	 */
	public int getHighscore(UUID player) {
		ResultSet set;
		int highscore = 0;

		String sql = "SELECT highscore FROM " + PREFIX + "highscores WHERE uuid = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				highscore = set.getInt("highscore");
			}

			set.close();
		} catch (SQLException e) {}

		return highscore;
	}

	/**
	 * Returns a String with the top 10 highscores in 2048, or less if there
	 * have not been 10 recorded scores yet. Each entry in the String is
	 * separated by a greater than sign (>) and the username and score are
	 * separated by a comma (,)
	 *
	 * @return leaderboard String
	 */
	public String getLeaderboard() {
		ResultSet set;
		StringBuilder builder = new StringBuilder();
		String leaderboard;

		String sql = "SELECT uuid, player, highscore FROM " + PREFIX
				+ "highscores ORDER BY highscore DESC LIMIT 10";

		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			set = ps.executeQuery();

			while (set.next()) {
				int score = set.getInt("highscore");
				String pName = set.getString("player");
				builder.append(">");
				builder.append(pName);
				builder.append(",");
				builder.append(score);
			}

			set.close();
		} catch (SQLException e) {}

		leaderboard = builder.toString();
		if (leaderboard.length() > 0) leaderboard = leaderboard.substring(1);
		return leaderboard;
	}

	/**
	 * Gets the number of tic tac toe wins of a player from the database based
	 * on the provided UUID. Returns 0 if they had no record in the database.
	 *
	 * @param player player's UUID
	 * @return player's wins or 0 if no record was found.
	 */
	public int getTicTacToeWins(UUID player) {
		ResultSet set;
		int gamesWon = 0;

		String sql = "SELECT games_won FROM " + PREFIX + "tictactoe WHERE uuid = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				gamesWon = set.getInt("games_won");
			}

			set.close();
		} catch (SQLException e) {}

		return gamesWon;
	}

	/**
	 * Gets the number of tic tac toe ties of a player from the database based
	 * on the provided UUID. Returns 0 if they had no record in the database.
	 *
	 * @param player player's UUID
	 * @return player's ties or 0 if no record was found.
	 */
	@Override
	public int getTicTacToeTies(UUID player) {
		ResultSet set;
		int gamesTied = 0;

		String sql = "SELECT games_tied FROM " + PREFIX + "tictactoe WHERE uuid = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, player.toString());
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				gamesTied = set.getInt("games_tied");
			}

			set.close();
		} catch (SQLException e) {}

		return gamesTied;
	}

	/**
	 * Gets the number of tic tac toe ties of a player from the database based
	 * on the provided name. Returns 0 if they had no record in the database.
	 *
	 * @param player player's name
	 * @return player's ties or 0 if no record was found.
	 */
	@Override
	public int getTicTacToeTies(String player) {
		ResultSet set;
		int gamesTied = 0;

		String sql = "SELECT games_tied FROM " + PREFIX + "tictactoe WHERE player = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player);
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				gamesTied = set.getInt("games_tied");
			}

			set.close();
		} catch (SQLException e) {}

		return gamesTied;
	}

	/**
	 * Returns the number of games of Tic Tac Toe losses based on the provided
	 * UUID. Returns 0 if there is no record for the specified player.
	 *
	 * @param player player's UUID
	 * @return loss count or 0 if no information available
	 */
	public int getTicTacToeLosses(UUID player) {
		ResultSet set;
		int gamesLost = 0;

		String sql = "SELECT games_lost FROM " + PREFIX + "tictactoe WHERE uuid = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				gamesLost = set.getInt("games_lost");
			}

			set.close();
		} catch (SQLException e) {}

		return gamesLost;
	}

	/**
	 * Updates a player's highscore and their username based on the provided
	 * UUID. Returns true if the operation succeeded, false if it did not.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param score new highscore
	 * @return whether the operation succeeded or failed
	 */
	public boolean updateHighscore(UUID player, String name, int score) {
		boolean success = true;

		String sql = "INSERT INTO " + PREFIX + "highscores VALUES (?, ?, ?) ON DUPLICATE KEY" +
				" UPDATE highscore = ?, player = ?";

		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			ps.setString(2, name);
			ps.setInt(3, score);
			ps.setInt(4, score);
			ps.setString(5, name);

			ps.executeUpdate();
		} catch (SQLException e) {
			success = false;
		}

		return success;
	}

	/**
	 * Gets the highscore of a player from the database based on the provided
	 * name. Returns 0 if they had no record in the database.
	 *
	 * @param player player's name
	 * @return player's highscore or 0 if no record was found.
	 */
	public int getHighscore(String player) {
		ResultSet set;
		int highscore = 0;

		String sql = "SELECT highscore FROM " + PREFIX + "highscores WHERE player = ?";
		try (Connection conn = pool.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player);
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				highscore = set.getInt("highscore");
			}

			set.close();
		} catch (SQLException e) {}

		return highscore;
	}

	/**
	 * Updates a player's win count in Tic Tac Toe and their username based on
	 * the provided UUID. Returns true if it succeeds false if it fails.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param wins new win count
	 * @return whether it succeeded or failed
	 */
	public boolean updateTicTacToeWins(UUID player, String name, int wins) {
		boolean success = true;

		String sql = "INSERT INTO " + PREFIX + "tictactoe VALUES (?, ?, ?, 0, 0) ON DUPLICATE KEY" +
				" UPDATE games_won = ?, player = ?";

		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			ps.setString(2, name);
			ps.setInt(3, wins);
			ps.setInt(4, wins);
			ps.setString(5, name);

			ps.executeUpdate();
		} catch (SQLException e) {
			success = false;
		}

		return success;
	}

	/**
	 * Gets the number of tic tac toe wins of a player from the database based
	 * on the provided name. Returns 0 if they had no record in the database.
	 *
	 * @param player player's name
	 * @return player's wins or 0 if no record was found.
	 */
	public int getTicTacToeWins(String player) {
		int gamesWon = 0;
		ResultSet set;

		String sql = "SELECT games_won FROM " + PREFIX + "tictactoe WHERE player = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player);
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				gamesWon = set.getInt("games_won");
			}

			set.close();
		} catch (SQLException e) {}

		return gamesWon;
	}

	/**
	 * Updates a player's loss count in Tic Tac Toe and their username based on
	 * the provided UUID. Returns true if it succeeds false if it fails.
	 *
	 * @param player player's UUID
	 * @param name player's name
	 * @param losses new loss count
	 * @return whether it succeeded or failed
	 */
	public boolean updateTickTackToeLosses(UUID player, String name, int losses) {
		boolean success = true;

		String sql = "INSERT INTO " + PREFIX + "tictactoe VALUES (?, ?, 0, ?, 0) ON" +
				" DUPLICATE KEY UPDATE games_lost = ?, name = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, player.toString());
			ps.setString(2, name);
			ps.setInt(3, losses);
			ps.setInt(4, losses);
			ps.setString(5, name);

			ps.executeUpdate();
		} catch (SQLException e) {
			success = false;
		}

		return success;
	}

	/**
	 * Returns the number of games of Tic Tac Toe losses based on the provided
	 * name. Returns 0 if there is no record for the specified player.
	 *
	 * @param player player's username
	 * @return loss count or 0 if no information available
	 */
	public int getTicTacToeLosses(String player) {
		ResultSet set;
		int gamesLost = 0;

		String sql = "SELECT games_lost FROM " + PREFIX + "tictactoe WHERE Player = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, player);
			set = ps.executeQuery();

			while (set.next()) {
				gamesLost = set.getInt("games_lost");
			}

			set.close();
		} catch (SQLException e) {}

		return gamesLost;
	}

	/**
	 * Closes all connections in the connection pool. Should be called in the
	 * onDisable method of the plugin.
	 */
	@Override
	public void onDisable() {
		pool.closePool();
	}

	/**
	 * This is a simple method that returns if there is information
	 * regarding this name in the datastore.
	 *
	 * @param name the player's name to check
	 * @return if there is information regarding this player in the datastore
	 */
	@Override
	public boolean registeredName(String name) {
		ResultSet set;
		boolean registeredName = false;

		String sql = "SELECT games_won FROM " + PREFIX + "tictactoe WHERE player = ?";
		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name);
			set = ps.executeQuery();

			// load data from set
			while (set.next()) {
				registeredName = true;
			}

			set.close();
		} catch (SQLException e) {}

		return registeredName;
	}

	/**
	 * Checks that the tables needed for the plugin exist and creates them if
	 * they do not. Returns true if the operation succeeded false if it failed.
	 *
	 * @return whether it succeeded or failed
	 */
	private boolean checkTables() {
		return makeTicTacToeTable() && makeHighscoresTable();
	}

	/**
	 * Checks for the existence of the TicTacToe table in the database and
	 * creates it if it does not exist. Returns whether the operation
	 * succeeded or failed.
	 *
	 * @return success of the operation
	 */
	private boolean makeTicTacToeTable() {
		boolean result = true;

		String sql = "CREATE TABLE IF NOT EXISTS " + PREFIX + "tictactoe (uuid VARCHAR(50"
				+ "), player VARCHAR(50), games_won INT, games_lost INT, games_tied INT" +
				", PRIMARY KEY(uuid))";

		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException e) {
			result = false;
		}

		return result;
	}

	/**
	 * Checks for the existence of the Highscores table in the database and
	 * creates it if it does not exist. Returns whether the operation
	 * succeeded or failed.
	 *
	 * @return success of the operation
	 */
	private boolean makeHighscoresTable() {
		boolean result = true;

		String sql = "CREATE TABLE IF NOT EXISTS " + PREFIX + "highscores" +
				" (uuid VARCHAR(50), player VARCHAR(50), highscore INT, " +
				"PRIMARY KEY (uuid))";

		try (Connection conn = pool.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException e) {
			result = false;
		}

		return result;
	}
}
