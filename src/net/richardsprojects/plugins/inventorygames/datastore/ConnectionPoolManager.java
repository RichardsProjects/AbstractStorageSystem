package net.richardsprojects.plugins.inventorygames.datastore;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.richardsprojects.plugins.inventorygames.InventoryGames;

/**
 * A wrapper class for HikariCP. Used to manage the connection pool. Based on a
 * tutorial by Insou on the Spigot forums which can be viewed here:
 * https://www.spigotmc.org/threads/.102864/
 *
 * @author RichardB122, Insou
 * @version 3/22/17
 */
public class ConnectionPoolManager {
	
    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;
	
    private HikariDataSource dataSource;
    
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    /**
     * Simple constructor that runs the init method.
     */
    public ConnectionPoolManager() {
    	init();
    }

    /**
     * Initializes the connection pool based on the data loaded from the
     * plugin's config file.
     */
    private void init() {
        hostname = InventoryGames.instance.mysql_dbHost;
        port = InventoryGames.instance.mysql_port;        
        database = InventoryGames.instance.mysql_dbName;
        username = InventoryGames.instance.mysql_dbUsername;
        password = InventoryGames.instance.mysql_dbPassword;
        
        minimumConnections = 0;
        maximumConnections = 5;
        connectionTimeout = 29999;
        setupPool();
    }

    /**
     * Creates a new HikariDataSource.
     */
    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        hostname +
                        ":" +
                        port +
                        "/" +
                        database
        );
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }

    /**
     * Returns a new available connection from the pool.
     *
     * @return a connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes a connection based on the provided connection and prepared
     * statement. It also can take a ResultSet if one was used or null if there
     * was not one.
     *
     * @param conn Connection
     * @param ps PreparedStatement
     * @param res ResultSet or null if there was not one
     */
    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }

    /**
     * Closes the entire connection pool. Should be run before the plugin
     * shuts down to avoid memory leaks.
     */
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}