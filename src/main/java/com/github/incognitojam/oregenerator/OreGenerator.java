package com.github.incognitojam.oregenerator;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class OreGenerator extends JavaPlugin {
    private OreConfig oreConfig;
    
    @Override
    public void onEnable() {
        final long seed = getServer().getWorlds().get(0).getSeed();
        
        oreConfig = new OreConfig(seed);
        loadConfig();
        getServer().getPluginManager().registerEvents(new OreListener(oreConfig), this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("oregenerator")) {
            // Sender ran the /oregenerator command
            
            if (args.length == 0) {
                // No arguments provided
                sender.sendMessage(ChatColor.AQUA + "OreGenerator v" + this.getDescription().getVersion());
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                // Sender ran the reload command
                loadConfig();
                sender.sendMessage(ChatColor.AQUA + "Loaded latest OreGenerator config.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown argument \"" + args[0] + "\"");
            }
            
            return false;
        }
        
        return super.onCommand(sender, command, label, args);
    }
    
    private void loadConfig() {
        // Make sure data folder exists
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        
        // Make sure config file exists
        File configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()) {
            // this code makes me sad :(
            try {
                InputStream inputStream = getResource("config.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                
                OutputStream outputStream = new FileOutputStream(configFile);
                outputStream.write(buffer);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Attempt to read config file from disk
        FileReader reader = null;
        try {
            reader = new FileReader(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        // Load config file into memory
        oreConfig.loadConfig(reader);
    }
}
