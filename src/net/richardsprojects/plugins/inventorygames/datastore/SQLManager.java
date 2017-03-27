package net.richardsprojects.plugins.inventorygames.datastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * This class contains all the queries needed for the database storage and
 * manages the connection pool.
 *
 * @author RichardB122
 * @version 3/23/17
 */
public class SQLManager {

    private final ConnectionPoolManager pool;
 
    public SQLManager() {
        pool = new ConnectionPoolManager();
    }

    /**
     * Checks that the tables needed for the plugin exist and creates them if
     * they do not. Returns true if the operation succeeded false if it failed.
     *
     * @return whether it succeeded or failed
     */
    public boolean checkTables() {
        return makeTicTacToeTable() && makeHighscoresTable();
    }

    /**
     * Gets the highscore of a player from the database based on the provided
     * UUID. Returns 0 if they had no record in the database.
     *
     * @param player player's UUID
     * @return player's highscore or 0 if no record was found.
     */
    public int getHighscore(UUID player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	int highscore = 0;
		
		String sql = "SELECT Highscore FROM Highscores WHERE UUID = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player.toString());
            set = ps.executeQuery();
            
            // load data from set
            if (set != null) {
            	while (set.next()) {
            		highscore = set.getInt("Highscore");
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }
        
        return highscore;
    }

    /**
     * Gets the highscore of a player from the database based on the provided
     * name. Returns 0 if they had no record in the database.
     *
     * @param player player's name
     * @return player's highscore or 0 if no record was found.
     */
    public int getHighscore(String player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	int highscore = 0;
		
		String sql = "SELECT Highscore FROM Highscores WHERE Player = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player);
            set = ps.executeQuery();
            
            // load data from set
            if (set != null) {
            	while (set.next()) {
            		highscore = set.getInt("Highscore");
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }
        
        return highscore;
    }

    /**
     * Gets the number of tic tac toe wins of a player from the database based
     * on the provided UUID. Returns 0 if they had no record in the database.
     *
     * @param player player's UUID
     * @return player's wins or 0 if no record was found.
     */
    public int getTicTacToeWins(UUID player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	int gamesWon = 0;
		
		String sql = "SELECT GamesWon FROM TicTacToe WHERE UUID = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player.toString());
            set = ps.executeQuery();
            
            // load data from set
            if (set != null) {
            	while (set.next()) {
            		gamesWon = set.getInt("GamesWon");
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }
        
        return gamesWon;
    }

    /**
     * Gets the number of tic tac toe wins of a player from the database based
     * on the provided name. Returns 0 if they had no record in the database.
     *
     * @param player player's name
     * @return player's wins or 0 if no record was found.
     */
    public int getTicTacToeWins(String player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	int gamesWon = 0;
		
		String sql = "SELECT GamesWon FROM TicTacToe WHERE Player = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player);
            set = ps.executeQuery();
            
            // load data from set
            if (set != null) {
            	while (set.next()) {
            		gamesWon = set.getInt("GamesWon");
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }
        
        return gamesWon;
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
    	boolean recordExists = false;
    	boolean success = true;
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;        
		
		String sql = "SELECT Highscore FROM Highscores WHERE UUID = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player.toString());
            set = ps.executeQuery();
            
            if (set != null) {
            	while (set.next()) {
            		recordExists = true;
            	}
            }
            
        } catch (SQLException e) {
        	success = false;
        } finally {
            pool.close(conn, ps, set);
        }
        
        
        if (recordExists) {
    		sql = "UPDATE Highscores SET Player = ?, Highscore = ? WHERE UUID = ?;";
    	} else {
    		sql = "INSERT INTO Highscores VALUES (?, ?, ?);";
        }
        
        if (success) {
	        try {
	            conn = pool.getConnection();
	            ps = conn.prepareStatement(sql);
	            
	            if (recordExists) {
	            	ps.setString(1, name);
	            	ps.setInt(2, score);
	            	ps.setString(3, player.toString());
	            } else {
	            	ps.setString(1, player.toString());
	            	ps.setString(2, name);
	            	ps.setInt(3, score);
	            }
	            
	            ps.executeUpdate();
	        } catch (SQLException e) {
	        	success = false;
	        } finally {
	            pool.close(conn, ps, set);
	        }
        }
        
        return success;
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
    	boolean recordExists = false;
    	boolean success = true;
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;        
		
		String sql = "SELECT GamesLost FROM TicTacToe WHERE UUID = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player.toString());
            set = ps.executeQuery();
            
            if (set != null) {
            	while (set.next()) {
            		recordExists = true;
            	}
            }
            
        } catch (SQLException e) {
        	success = false;
        } finally {
            pool.close(conn, ps, set);
        }
        
        
        if (recordExists) {
    		sql = "UPDATE TicTacToe SET Player = ?, GamesLost = ? WHERE UUID = ?;";
    	} else {
    		sql = "INSERT INTO TicTacToe VALUES (?, ?, 0, ?);";
        }
        
        if (success) {
	        try {
	            conn = pool.getConnection();
	            ps = conn.prepareStatement(sql);
	            
	            if (recordExists) {
	            	ps.setString(1, name);
	            	ps.setInt(2, losses);
	            	ps.setString(3, player.toString());
	            } else {
	            	ps.setString(1, player.toString());
	            	ps.setString(2, name);
	            	ps.setInt(3, losses);
	            }
	            
	            ps.executeUpdate();
	        } catch (SQLException e) {
	        	success = false;
	        } finally {
	            pool.close(conn, ps, set);
	        }
        }
        
        return success;
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
    public boolean updateTickTackToeWins(UUID player, String name, int wins) {
    	boolean recordExists = false;
    	boolean success = true;
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;        
		
		String sql = "SELECT GamesWon FROM TicTacToe WHERE UUID = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player.toString());
            set = ps.executeQuery();
            
            if (set != null) {
            	while (set.next()) {
            		recordExists = true;
            	}
            }
            
        } catch (SQLException e) {
        	success = false;
        } finally {
            pool.close(conn, ps, set);
        }
        
        
        if (recordExists) {
    		sql = "UPDATE TicTacToe SET Player = ?, GamesWon = ? WHERE UUID = ?;";
    	} else {
    		sql = "INSERT INTO TicTacToe VALUES (?, ?, ?, 0);";
        }
        
        if (success) {
	        try {
	            conn = pool.getConnection();
	            ps = conn.prepareStatement(sql);
	            
	            if (recordExists) {
	            	ps.setString(1, name);
	            	ps.setInt(2, wins);
	            	ps.setString(3, player.toString());
	            } else {
	            	ps.setString(1, player.toString());
	            	ps.setString(2, name);
	            	ps.setInt(3, wins);
	            }
	            
	            ps.executeUpdate();
	        } catch (SQLException e) {
	        	success = false;
	        } finally {
	            pool.close(conn, ps, set);
	        }
        }
        
        return success;
    }

    /**
     * Returns the number of games of Tic Tac Toe losses based on the provided
     * UUID. Returns 0 if there is no record for the specified player.
     *
     * @param player player's UUID
     * @return loss count or 0 if no information available
     */
    public int getTicTacToeLosses(UUID player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	int gamesLost = 0;
		
		String sql = "SELECT GamesLost FROM TicTacToe WHERE UUID = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player.toString());
            set = ps.executeQuery();
            
            // load data from set
            if (set != null) {
            	while (set.next()) {
            		gamesLost = set.getInt("GamesLost");
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }
        
        return gamesLost;
    }

    /**
     * Returns the number of games of Tic Tac Toe losses based on the provided
     * name. Returns 0 if there is no record for the specified player.
     *
     * @param player player's username
     * @return loss count or 0 if no information available
     */
    public int getTicTacToeLosses(String player) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	int gamesLost = 0;
		
		String sql = "SELECT GamesLost FROM TicTacToe WHERE Player = ?";
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, player);
            set = ps.executeQuery();
            
            // load data from set
            if (set != null) {
            	while (set.next()) {
            		gamesLost = set.getInt("GamesLost");
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }
        
        return gamesLost;
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
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
    	
		String leaderboard = "";
    	String sql = "SELECT UUID, Player, Highscore FROM Highscores ORDER BY Highscore DESC LIMIT 10";
		
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            set = ps.executeQuery();
            
            if (set != null) {
            	while (set.next()) {
            		int score = set.getInt("Highscore");
            		String pName = set.getString("Player");
            		leaderboard = leaderboard + ">" + pName + "," + score;
            	}
            }
            
        } catch (SQLException e) {} finally {
            pool.close(conn, ps, set);
        }

        if (leaderboard.length() > 0) leaderboard = leaderboard.substring(1);
		return leaderboard;
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
    	
    	String sql = "CREATE TABLE IF NOT EXISTS TicTacToe (UUID VARCHAR(50";
        sql = sql + "), Player VARCHAR(50), GamesWon INT, GamesLost INT)";
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            result = false;
        } finally {
            pool.close(conn, ps, null);
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
    	
    	String sql = "CREATE TABLE IF NOT EXISTS Highscores (UUID VARCHAR(50),";
    	sql = sql + " Player VARCHAR(50), Highscore INT)";
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            result = false;
        } finally {
            pool.close(conn, ps, null);
        }
        
        return result;
    }

    /**
     * Tells the ConnectionPool to close. Should be called before plugin is
     * disabled.
     */
    public void onDisable() {
        pool.closePool();
    }
}
