package net.rageland.ragemod.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class Configuration {
	private final JavaPlugin plugin;
	private final File file;
	private YamlConfiguration config;
	
	/**
	 * Creates a new configuration together with its own file.
	 * @param plugin the plugin the configuration belongs to
	 * @param fileName the name of the file, should end with .yml
	 */
	public Configuration(final JavaPlugin plugin, final String fileName) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), fileName);	
		reload();
	}
	
	/**
	 * Reloads the configuration from disk.
	 */
	public void reload() {
		if (!checkMemberVariables())
			throw new IllegalStateException("Neither plugin nor the file to be used must be null!");
			
		config = YamlConfiguration.loadConfiguration(file);
		 
		final InputStream defaultStream = plugin.getResource(file.getName());
	    if (defaultStream != null) {
	        final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defaultStream);
	 
	        config.setDefaults(defConfig);
	    }
	}
	
	/**
	 * Saves the configuration to the disk.
	 * @return true if successful
	 */
	public boolean save() {
		if (!checkMemberVariables())
			throw new IllegalStateException("Neither plugin nor the file to be used must be null.");
		
		try {
	        config.save(file);
	        return true;
	    } catch (IOException ex) {
	    	Bukkit.getLogger().log(Level.SEVERE, new StringBuilder("[").append(plugin.toString()).append("] Failed saving configuration to ").append(file.toString()).toString(), ex);
	        return false;
	    }
	}
	
	public void load() {
		if (!checkMemberVariables())
			throw new IllegalStateException("Neither plugin nor the file to be used must be null!");
		
		config = YamlConfiguration.loadConfiguration(file);
		
		final InputStream defaultStream = plugin.getResource(file.getName());
		if (defaultStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defaultStream);		
			config.setDefaults(defConfig);
		}
	}
	
	/**
	 * Returns the YamlConfiguration.
	 * @return the YamlConfiguration
	 */
	public YamlConfiguration getYamlConfig() {
		return config;
	}
	
	private boolean checkMemberVariables() {
		return (plugin != null && file != null);
	}
}
