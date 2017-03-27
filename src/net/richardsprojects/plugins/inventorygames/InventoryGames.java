package net.richardsprojects.plugins.inventorygames;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Logger;

import net.richardsprojects.plugins.inventorygames.commands.ExampleCommand;
import net.richardsprojects.plugins.inventorygames.datastore.Datastore;
import net.richardsprojects.plugins.inventorygames.datastore.YmlDatastore;
import net.richardsprojects.plugins.inventorygames.datastore.MySQLDatastore;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A cutdown version of the InventoryGames class to demonstrate the abstract
 * data storage system.
 *
 * @author RichardB122
 * @version 3/23/17
 */
public class InventoryGames extends JavaPlugin {

	public static InventoryGames instance;

	public Logger log;
	public PluginManager pm;

	private Datastore storage;

	public static File dataFolder;

	// config
	public String mysql_dbHost = "";
	public String mysql_dbName = "";
	public String mysql_dbUsername = "";
	public String mysql_dbPassword = "";
	public String mysql_port = "3306";
	public String dbPrefix = "";
	public String dbType = "";

	/**
	 * Called when the plugin is enabled by the Bukkit API. Loads the config,
	 * initializes the data storage system and registers an example command.
	 */
	@Override
	public void onEnable() {
		InventoryGames.instance = this;

		// create the data folder
		if (!(getDataFolder().exists()))
			getDataFolder().mkdirs();

		dataFolder = getDataFolder();
		log = Logger.getLogger("Minecraft");
		pm = getServer().getPluginManager();

		if (!loadConfig()) {
			log.info("[InventoryGames] Disabling InventoryGames...");
			this.setEnabled(false);
		}

		if (!storage.initalize()) {
			log.info("[InvenotryGames] There was an error enabling the storage system.");
			log.info("[InventoryGames] Disabling InventoryGames...");
			this.setEnabled(false);
		}

		// register example command
		getCommand("example").setExecutor(new ExampleCommand());
	}

	/**
	 * Simple helper method to setup and load settings from the config and
	 * return whether it was successful or not.
	 * @return
	 */
	private boolean loadConfig() {
		File fileConfig = new File(dataFolder + File.separator + "config.yml");

		// create config if it does not exist
		if(!fileConfig.exists()) {
			try {
				fileConfig.createNewFile();
				log.info("[InventoryGames] Created config.yml file...");

				PrintWriter out = new PrintWriter(fileConfig);
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						InventoryGames.class.getResourceAsStream("/config.yml")));

				String currentLine;
				while ((currentLine = reader.readLine()) != null) {
					out.append(currentLine + "\n");
				}
				out.close();
				reader.close();
			} catch (Exception e) {
				String msg = "Error occurred while creating config.yml file...";
				msg = msg + " Please check your permissions.";
				log.info(msg);
				return false;
			}
		}

		// load config data
		try {
			YamlConfiguration config = new YamlConfiguration();
			config.load(fileConfig);

			dbType = config.getString("dbType");
			mysql_dbHost = config.getString("MySQL_Host");
			mysql_dbName = config.getString("MySQL_DatabaseName");
			mysql_dbUsername = config.getString("MySQL_Username");
			mysql_dbPassword = config.getString("MySQL_Password");
			mysql_port = config.getString("MySQL_Port");

			// make sure port is number
			try {
				Integer.parseInt(mysql_port);
			} catch (Exception e) {
				log.info("[InventoryGames] Your port must be a number...");
				return false;
			}

			dbPrefix = config.getString("tablePrefix");
		} catch (Exception e) {
			log.info("[InventoryGames] There was an error reading from the config...");
			return false;
		}

		if (dbType.equalsIgnoreCase("mysql")) {
			log.info("[InventoryGames] Using MySQL as database...");
			storage = new MySQLDatastore();
		} else if(dbType.equalsIgnoreCase("yml")) {
			log.info("[InventoryGames] Using yml files to store data...");
			storage = new YmlDatastore();
		} else {
			log.info("[InventoryGames] Please set your storage type.");
			return false;
		}

		return true;
	}

	/**
	 * Called when the plugin is disabled. Calls the onDisable method in the
	 * storage system to clean things up.
	 */
	@Override
	public void onDisable() {
	    storage.onDisable();
	}

	/**
	 * Returns the datastore for the plugin.
	 *
	 * @return plugin's datastore
	 */
	public Datastore getDatastore() {
			return storage;
		}
}
